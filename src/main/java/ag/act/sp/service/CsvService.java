package ag.act.sp.service;

import ag.act.configuration.security.CryptoHelper;
import ag.act.entity.Role;
import ag.act.entity.User;
import ag.act.entity.UserRole;
import ag.act.enums.RoleType;
import ag.act.exception.BadRequestException;
import ag.act.model.Gender;
import ag.act.service.RoleService;
import ag.act.service.user.UserRoleService;
import ag.act.service.user.UserService;
import ag.act.util.DateTimeFormatUtil;
import ag.act.util.DateTimeUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Transactional
@Service
public class CsvService {
    private final CryptoHelper cryptoHelper;
    private final RoleService roleService;
    private final UserRoleService userRoleService;
    private final UserService userService;

    public List<User> getUsersFromCsv(MultipartFile file) {
        String line;
        String csvSplitBy = ",";
        List<User> users = new ArrayList<>();

        /*
        이메일,권한,성명,성별,생년월일
        air1@naver.com,USER,홍길동,남,1990-01-01
        air2@naver.com,LEADER_USER,강호동,남,1990-01-01
        air3@naver.com,USER,유재석,남,1990-01-01
         */
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            // 첫 번째 줄을 무시 (헤더)
            br.readLine();

            while ((line = br.readLine()) != null) {
                // CSV의 각 행을 쉼표로 나눕니다.
                String[] userData = line.split(csvSplitBy);

                // 데이터가 잘못된 경우를 대비한 체크
                if (userData.length < 5) {
                    continue; // 유효하지 않은 행은 무시
                }

                final String[] splitEmail = splitEmail(userData[0].trim());

                // User 객체 생성 및 값 설정
                User user = new User();
                user.setEmail(userData[0].trim());
                user.setName(userData[2].trim());
                user.setGender(getGender(userData[3].trim()));
                user.setBirthDate(DateTimeUtil.parseLocalDateTime(userData[4].trim(), DateTimeFormatUtil.yyyy_MM_dd().toString()));
                user.setPassword(encrypt(splitEmail[0].trim()));
                user.setHashedPhoneNumber(encrypt(splitEmail[0].trim()));
                user.setHashedCI(encrypt(splitEmail[0].trim()));
                user.setHashedDI(encrypt(splitEmail[0].trim()));
                user.setFirstNumberOfIdentification(1);

                // User 객체 저장
                final User savedUser = userService.saveUser(user);
                final UserRole roles = getRoles(userData[1].trim(), savedUser.getId());
                savedUser.setRoles(List.of(roles));
                users.add(savedUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    private UserRole getRoles(String s, Long userId) {
        final RoleType roleType = RoleType.fromValue(s);
        final Role role = roleService.findRoleByType(roleType)
            .orElseThrow(() -> new BadRequestException("권한을 찾을 수 없습니다."));
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(role.getId());
        userRole.setRole(role);
        return userRoleService.saveUserRole(userRole);
    }

    private Gender getGender(String s) {
        return Objects.equals(s, "남") ? Gender.M : Gender.F;
    }

    private String encrypt(String email) throws Exception {
        return cryptoHelper.encrypt(email);
    }

    private String[] splitEmail(String email) {
        return email.split("@");
    }
}

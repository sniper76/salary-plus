package ag.act.service;

import ag.act.entity.Role;
import ag.act.enums.RoleType;
import ag.act.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Optional<Role> findRoleByType(RoleType roleType) {
        return roleRepository.findByType(roleType);
    }
}

# salary-plus

## 사용자 등록
1. 본인 인증
    1. 본인인증 요청, 문자 발송 API
   - /api/sms/send-auth-request
    2. 본인인증 재요청, 문자 재발송 API
   - /api/sms/resend-auth-request
2. 본인인증 검증 API
   - /api/sms/verify-auth-code
3. 회원정보 등록 API
   - /api/auth/register-user-info 
4. 핀번호 등록 API 
   - /api/auth/register-pin-number 
5. 핀번호 검증 API
   - /api/auth/verify-pin-number 
6. 로그아웃
   - /api/auth/logout 
7. 핀번호 리셋
   - /api/auth/reset-pin-number

## WEB
1. 웹 인증코드 생성
   - /api/auth/web/generate-verification-code
2. 웹 인증코드로 로그인 확인
   - /api/auth/web/verify-verification-code
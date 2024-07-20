package ag.act.module.auth.web;

public interface WebVerificationBase {
    int VERIFICATION_CODE_DURATION_MINUTES = 3;
    char[] VERIFICATION_CODE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    int VERIFICATION_CODE_MAX_NUMBER_BOUND = 1000;
    int FIVE_MINUTES = 5;
}

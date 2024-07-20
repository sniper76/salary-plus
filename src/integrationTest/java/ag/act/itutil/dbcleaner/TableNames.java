package ag.act.itutil.dbcleaner;

@SuppressWarnings("LombokGetterMayBeUsed")
public enum TableNames {
    POSTS("posts"),
    DIGITAL_DOCUMENTS("digital_documents"),
    POLLS("polls"),
    POLL_ITEMS("poll_items"),
    POLL_ANSWERS("poll_answers"),
    USERS("users"),
    USER_ROLES("user_roles"),
    USER_VERIFICATION_HISTORIES("user_verification_histories"),
    USER_HOLDING_STOCKS("user_holding_stocks"),
    SOLIDARITIES("solidarities"),
    SOLIDARITY_DAILY_SUMMARIES("solidarity_daily_summaries"),
    PUSHES("pushes"),
    POPUPS("popups"),
    STOCKS_RANKINGS("stock_rankings"),
    TEST_STOCKS("test_stocks"),
    PRIVATE_STOCKS("private_stocks"),
    STOCKS("stocks"),
    BOARDS("boards"),
    DASHBOARD_STOCK_STATISTICS("dashboard_stock_statistics"),
    USER_HOLDING_STOCK_ON_REFERENCE_DATES("user_holding_stock_on_reference_dates"),
    NICKNAME_HISTORIES("nickname_histories");

    private final String value;

    TableNames(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

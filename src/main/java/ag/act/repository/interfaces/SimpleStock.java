package ag.act.repository.interfaces;

public interface SimpleStock {

    String getCode();

    String getName();

    default String getStandardCode() {
        return getCode();
    }
}

package ag.act.enums;

public enum SelectionOption {
    MULTIPLE_ITEMS,
    SINGLE_ITEM;

    public boolean isSingleItem() {
        return this == SINGLE_ITEM;
    }
}

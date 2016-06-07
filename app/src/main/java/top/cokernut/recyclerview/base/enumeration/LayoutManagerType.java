package top.cokernut.recyclerview.base.enumeration;

public enum LayoutManagerType {
    /**
     * LINEAR:竖向线性布局
     * GRID:竖向表格布局
     * STAGGERED:竖向瀑布流布局
     * LINEAR_VERTICAL_TRUE:竖向反向线性布局
     * LINEAR_HORIZONTAL:横向线性布局
     */
    LINEAR(1), GRID(2), STAGGERED(3), LINEAR_VERTICAL_TRUE(4), LINEAR_HORIZONTAL(5);
    private int value;

    LayoutManagerType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
package rygel.cn.calendarview.listener;

/**
 * 月份变换监听
 * @author Rygel
 */
public interface OnMonthChangedListener {

    /**
     * 当选中月份发生改变时回调
     * @param year 年份
     * @param month 月份
     */
    void OnMonthChange(int year, int month);

}

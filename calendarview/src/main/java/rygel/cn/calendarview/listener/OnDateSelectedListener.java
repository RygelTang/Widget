package rygel.cn.calendarview.listener;

import rygel.cn.calendar.bean.Solar;

/**
 * 选中日期的回调
 * @author Rygel
 */
public interface OnDateSelectedListener {

    /**
     * 当日历控件选中日期以后会对这个函数进行回调
     * @param solar 选中的日期
     */
    void onSelected(Solar solar);

}

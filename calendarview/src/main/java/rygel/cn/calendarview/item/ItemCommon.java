package rygel.cn.calendarview.item;

import android.graphics.Canvas;
import android.graphics.Rect;
import rygel.cn.calendar.bean.Solar;

/**
 * 普通日期实现的接口
 * @author Rygel
 */
public interface ItemCommon {

    /**
     * 重写这个函数以实现自定义样式的日历控件
     * 在canvas上通过bound确定子项的大小，并根据solar来获取日期的信息来进行绘制
     * @param canvas 画布
     * @param bound 边界
     * @param solar 公历日期
     */
    void draw(Canvas canvas, Rect bound, Solar solar);

}

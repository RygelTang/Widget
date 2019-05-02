package rygel.cn.calendarview.item;

import android.graphics.Canvas;
import android.graphics.Rect;
import rygel.cn.calendar.bean.Solar;

/**
 * 选中日期实现的接口
 * @author Rygel
 */
public interface ItemSelected extends ItemCommon {

    /**
     * 这个函数是用于实现选中的动画
     * 前几个参数和{@link ItemSelected#draw(Canvas, Rect, Solar)}一致
     * percent表示的是当前的进度百分比
     * 动画完成后会调用{@link ItemSelected#draw(Canvas, Rect, Solar)}方法
     * @param canvas 画布
     * @param bound 边界
     * @param solar 日期
     * @param percent 动画百分比
     */
    void anim(Canvas canvas, Rect bound, Solar solar, float percent);

}

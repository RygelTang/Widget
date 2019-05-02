package rygel.cn.calendarview.weekbar;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * 星期栏要实现的接口
 * @author Rygel
 */
public interface WeekBar {

    /**
     * 重写这个函数以实现自定义样式的星期栏
     * 在canvas上通过bound确定星期栏的大小
     * @param canvas 画布
     * @param bound 边界
     * @param start 起始星期，0为周日，1为周一，以此类推
     */
    void draw(Canvas canvas, Rect bound, int start);

}

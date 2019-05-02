package rygel.cn.calendarview.weekbar.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import rygel.cn.calendarview.CalendarView;
import rygel.cn.calendarview.weekbar.WeekBar;

import java.lang.ref.WeakReference;

/**
 * 默认提供的星期栏实现
 * @author Rygel
 */
public class DefaultWeekBarImpl implements WeekBar {

    private WeakReference<CalendarView> mCalendarViewWeakReference;

    private static final int WEEKDAYS = 7;

    private TextPaint mWeekDayPaint = new TextPaint();

    private int mWeekdayTextSize = 48;

    private int mWeekdayTextColor = Color.parseColor("#000000");

    public String[] mWeekdays = {
            "日", "一", "二", "三", "四", "五", "六"
    };

    public DefaultWeekBarImpl(CalendarView view) {
        mCalendarViewWeakReference = new WeakReference<>(view);
        initPaint();
    }

    /**
     * 初始化Paint
     */
    private void initPaint() {
        mWeekDayPaint.setColor(mWeekdayTextColor);
        mWeekDayPaint.setTextSize(mWeekdayTextSize);
        mWeekDayPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void draw(Canvas canvas, Rect bound, int start) {
        final int itemWidth = bound.width() / WEEKDAYS;
        for(int i = 0;i < WEEKDAYS;i++){
            Rect textBound = new Rect();
            mWeekDayPaint.getTextBounds(mWeekdays[(i + start) % 7], 0, mWeekdays[i].length(), textBound);
            final int x = bound.left + i * itemWidth + itemWidth / 2;
            final int y = bound.centerY() + textBound.height() / 2;
            canvas.drawText(mWeekdays[(i + start) % 7], x, y, mWeekDayPaint);
        }
    }

    /**
     * 设置显示的星期
     * @param weekdays 确保长度为7，周日为第一个，例如{"日", "一", "二", "三", "四", "五", "六"}
     * @return
     */
    public DefaultWeekBarImpl setWeekdays(String[] weekdays) {
        mWeekdays = weekdays;
        if(mCalendarViewWeakReference.get() != null) {
            mCalendarViewWeakReference.get().postInvalidate();
        }
        return this;
    }

    /**
     * 设置星期的字体大小
     * @param weekdayTextSize
     * @return
     */
    public DefaultWeekBarImpl setWeekdayTextSize(int weekdayTextSize) {
        mWeekdayTextSize = weekdayTextSize;
        mWeekDayPaint.setTextSize(weekdayTextSize);
        if(mCalendarViewWeakReference.get() != null) {
            mCalendarViewWeakReference.get().postInvalidate();
        }
        return this;
    }

    /**
     * 设置星期的字体颜色
     * @param weekdayTextColor
     * @return
     */
    public DefaultWeekBarImpl setWeekdayTextColor(int weekdayTextColor) {
        mWeekdayTextColor = weekdayTextColor;
        mWeekDayPaint.setColor(weekdayTextColor);
        if(mCalendarViewWeakReference.get() != null) {
            mCalendarViewWeakReference.get().postInvalidate();
        }
        return this;
    }
}

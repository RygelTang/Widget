package rygel.cn.calendarview.item.impl;

import android.graphics.*;
import android.text.TextPaint;
import rygel.cn.calendar.bean.Solar;
import rygel.cn.calendar.utils.LunarUtils;
import rygel.cn.calendarview.CalendarView;
import rygel.cn.calendarview.item.ItemSelected;
import rygel.cn.calendarview.provider.HolidayInfoProvider;
import rygel.cn.calendarview.provider.TermInfoProvider;
import rygel.cn.calendarview.provider.impl.DefaultHolidayInfoProvider;
import rygel.cn.calendarview.provider.impl.DefaultTermInfoProvider;

import java.lang.ref.WeakReference;

/**
 * 默认提供选中子项样式
 * @author Rygel
 */
public class DefaultItemSelectedImpl implements ItemSelected {

    private static final String TAG = "DefaultItemSelectedImpl";

    private WeakReference<CalendarView> mCalendarViewWeakReference;

    private int mDateTextColor = Color.parseColor("#FFFFFF");
    private int mLunarTextColor = Color.parseColor("#FFFFFF");
    private int mTermTextColor = Color.parseColor("#FFFFFF");
    private int mHolidayTextColor = Color.parseColor("#FFFFFF");
    private int mCornerTextColor = Color.parseColor("#FFFFFF");
    private int mMakeUpDayTextColor = Color.parseColor("#FFFFFF");

    private int mBackgroundColor = Color.parseColor("#008577");

    private int mDateTextSize = 56;
    private int mLunarTextSize = 48;
    private int mTermTextSize = 48;
    private int mHolidayTextSize = 48;
    private int mCornerTextSize = 40;
    private int mMakeUpDayTextSize = 40;

    private int mRoundCornerRadius = 10;
    private int mStrokeWidth = 5;

    private int mCornerPadding = 20;

    private TextPaint mDateTextPaint = new TextPaint();
    private TextPaint mLunarTextPaint = new TextPaint();
    private TextPaint mTermTextPaint = new TextPaint();
    private TextPaint mHolidayTextPaint = new TextPaint();
    private TextPaint mCornerTextPaint = new TextPaint();
    private TextPaint mMakeUpDayTextPaint = new TextPaint();

    private Paint mBackgroundPaint = new Paint();

    private HolidayInfoProvider mHolidayInfoProvider = new DefaultHolidayInfoProvider();
    private TermInfoProvider mTermInfoProvider = new DefaultTermInfoProvider();

    private Rect mTextBound = new Rect();

    private RectF mTempRectF = new RectF();
    private Rect mTempRect = new Rect();

    public DefaultItemSelectedImpl(CalendarView view) {
        mCalendarViewWeakReference = new WeakReference<>(view);
        initPaint();
    }

    /**
     * 初始化Paint
     */
    private void initPaint() {
        mDateTextPaint.setColor(mDateTextColor);
        mLunarTextPaint.setColor(mLunarTextColor);
        mTermTextPaint.setColor(mTermTextColor);
        mHolidayTextPaint.setColor(mHolidayTextColor);
        mCornerTextPaint.setColor(mCornerTextColor);
        mMakeUpDayTextPaint.setColor(mMakeUpDayTextColor);

        mBackgroundPaint.setColor(mBackgroundColor);

        mDateTextPaint.setTextSize(mDateTextSize);
        mLunarTextPaint.setTextSize(mLunarTextSize);
        mTermTextPaint.setTextSize(mTermTextSize);
        mHolidayTextPaint.setTextSize(mHolidayTextSize);
        mCornerTextPaint.setTextSize(mCornerTextSize);
        mMakeUpDayTextPaint.setTextSize(mMakeUpDayTextSize);

        mDateTextPaint.setTextAlign(Paint.Align.CENTER);
        mLunarTextPaint.setTextAlign(Paint.Align.CENTER);
        mTermTextPaint.setTextAlign(Paint.Align.CENTER);
        mHolidayTextPaint.setTextAlign(Paint.Align.CENTER);
        mCornerTextPaint.setTextAlign(Paint.Align.CENTER);
        mMakeUpDayTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void anim(Canvas canvas, Rect bound, Solar solar, float percent) {
        final int width = (int) (bound.width() * percent);
        final int height = (int) (bound.height() * percent);
        final int centerX = bound.centerX();
        final int centerY = bound.centerY();
        mTempRect.top = centerY - height / 2;
        mTempRect.left = centerX - width / 2;
        mTempRect.bottom = centerY + height / 2;
        mTempRect.right = centerX + width / 2;
        drawBackground(canvas, mTempRect);
        drawForeground(canvas, bound, solar);
    }

    @Override
    public void draw(Canvas canvas, Rect bound, Solar solar) {
        drawBackground(canvas, bound);
        drawForeground(canvas, bound, solar);
    }

    /**
     * 绘制前景
     * @param canvas
     * @param bound
     * @param solar
     */
    private void drawForeground(Canvas canvas, Rect bound, Solar solar) {
        drawDate(canvas, bound, solar);
        drawCorner(canvas, bound, solar);
        if(mHolidayInfoProvider.isNormalHoliday(solar)) {
            drawHoliday(canvas, bound, solar);
        } else if (mTermInfoProvider.isTerm(solar)) {
            drawTerm(canvas, bound, solar);
        } else {
            drawLunar(canvas, bound, solar);
        }
    }

    /**
     * 绘制背景
     * @param canvas
     * @param bound
     */
    private void drawBackground(Canvas canvas, Rect bound) {
        mTempRectF.left = bound.left + mStrokeWidth / 2;
        mTempRectF.top = bound.top + mStrokeWidth / 2;
        mTempRectF.right = bound.right - mStrokeWidth / 2;
        mTempRectF.bottom = bound.bottom - mStrokeWidth / 2;
        canvas.drawRoundRect(mTempRectF, mRoundCornerRadius, mRoundCornerRadius, mBackgroundPaint);
    }

    /**
     * 绘制日期
     * @param canvas
     * @param bound
     * @param solar
     */
    private void drawDate(Canvas canvas, Rect bound, Solar solar) {
        final String date = String.valueOf(solar.solarDay);
        mDateTextPaint.getTextBounds(date,0,date.length(),mTextBound);
        canvas.drawText(date,
                bound.centerX(),
                bound.top + bound.height() / 3 + mTextBound.height() / 2,
                mDateTextPaint
        );
    }

    /**
     * 绘制右上角角标
     * @param canvas
     * @param bound
     * @param solar
     */
    private void drawCorner(Canvas canvas, Rect bound, Solar solar) {
        TextPaint paint = null;
        String str = null;
        if(mHolidayInfoProvider.isStatutoryHoliday(solar)) {
            paint = mCornerTextPaint;
            str = "休";
        }
        if(mHolidayInfoProvider.isMakeUpDay(solar)) {
            paint = mMakeUpDayTextPaint;
            str = "班";
        }
        if(paint == null) {
            return;
        }
        paint.getTextBounds(str,0,str.length(),mTextBound);
        canvas.drawText(str,
                bound.right - mTextBound.width() / 2 - mCornerPadding,
                bound.top + mTextBound.height() + mCornerPadding,
                paint
        );
    }

    /**
     * 绘制农历日期
     * @param canvas
     * @param bound
     * @param solar
     */
    private void drawLunar(Canvas canvas, Rect bound, Solar solar) {
        final String lunarDay = LunarUtils.LUNAR_DAYS[solar.toLunar().lunarDay - 1];
        mLunarTextPaint.getTextBounds(lunarDay,0,lunarDay.length(),mTextBound);
        canvas.drawText(lunarDay,
                bound.centerX(),
                bound.top + bound.height() * 2 / 3 + mTextBound.height() / 2,
                mLunarTextPaint
        );
    }

    /**
     * 绘制节气
     * @param canvas
     * @param bound
     * @param solar
     */
    private void drawTerm(Canvas canvas, Rect bound, Solar solar) {
        final String term = mTermInfoProvider.getTermString(solar);
        if(term == null) {
            return;
        }
        mTermTextPaint.getTextBounds(term,0,term.length(),mTextBound);
        canvas.drawText(term,
                bound.centerX(),
                bound.top + bound.height() * 2 / 3 + mTextBound.height() / 2,
                mTermTextPaint
        );
    }

    /**
     * 绘制节日信息
     * @param canvas
     * @param bound
     * @param solar
     */
    private void drawHoliday(Canvas canvas, Rect bound, Solar solar) {
        final String holiday = mHolidayInfoProvider.getHolidayString(solar);
        if(holiday == null) {
            return;
        }
        mHolidayTextPaint.getTextBounds(holiday,0,holiday.length(),mTextBound);
        canvas.drawText(holiday,
                bound.centerX(),
                bound.top + bound.height() * 2 / 3 + mTextBound.height() / 2,
                mHolidayTextPaint
        );
    }

    /**
     * 设置日期字体颜色
     * @param dateTextColor
     * @return
     */
    public DefaultItemSelectedImpl setDateTextColor(int dateTextColor) {
        mDateTextColor = dateTextColor;
        mDateTextPaint.setColor(dateTextColor);
        if(mCalendarViewWeakReference.get() != null) {
            mCalendarViewWeakReference.get().postInvalidate();
        }
        return this;
    }

    /**
     * 设置农历日期字体颜色
     * @param lunarTextColor
     * @return
     */
    public DefaultItemSelectedImpl setLunarTextColor(int lunarTextColor) {
        mLunarTextColor = lunarTextColor;
        mLunarTextPaint.setColor(lunarTextColor);
        if(mCalendarViewWeakReference.get() != null) {
            mCalendarViewWeakReference.get().postInvalidate();
        }
        return this;
    }

    /**
     * 设置节气字体颜色
     * @param termTextColor
     * @return
     */
    public DefaultItemSelectedImpl setTermTextColor(int termTextColor) {
        mTermTextColor = termTextColor;
        mTermTextPaint.setColor(termTextColor);
        if(mCalendarViewWeakReference.get() != null) {
            mCalendarViewWeakReference.get().postInvalidate();
        }
        return this;
    }

    /**
     * 设置节日字体颜色
     * @param holidayTextColor
     * @return
     */
    public DefaultItemSelectedImpl setHolidayTextColor(int holidayTextColor) {
        mHolidayTextColor = holidayTextColor;
        mHolidayTextPaint.setColor(holidayTextColor);
        if(mCalendarViewWeakReference.get() != null) {
            mCalendarViewWeakReference.get().postInvalidate();
        }
        return this;
    }

    /**
     * 设置角标字体颜色
     * @param cornerTextColor
     * @return
     */
    public DefaultItemSelectedImpl setCornerTextColor(int cornerTextColor) {
        mCornerTextColor = cornerTextColor;
        mCornerTextPaint.setColor(cornerTextColor);
        if(mCalendarViewWeakReference.get() != null) {
            mCalendarViewWeakReference.get().postInvalidate();
        }
        return this;
    }

    /**
     * 设置补班字体颜色
     * @param makeUpDayTextColor
     * @return
     */
    public DefaultItemSelectedImpl setMakeUpDayTextColor(int makeUpDayTextColor) {
        mMakeUpDayTextColor = makeUpDayTextColor;
        mMakeUpDayTextPaint.setColor(makeUpDayTextColor);
        if(mCalendarViewWeakReference.get() != null) {
            mCalendarViewWeakReference.get().postInvalidate();
        }
        return this;
    }

    /**
     * 设置背景颜色
     * @param backgroundColor
     * @return
     */
    public DefaultItemSelectedImpl setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
        mBackgroundPaint.setColor(backgroundColor);
        if(mCalendarViewWeakReference.get() != null) {
            mCalendarViewWeakReference.get().postInvalidate();
        }
        return this;
    }

    /**
     * 设置圆角半径
     * @param roundCornerRadius
     * @return
     */
    public DefaultItemSelectedImpl setRoundCornerRadius(int roundCornerRadius) {
        mRoundCornerRadius = roundCornerRadius;
        if(mCalendarViewWeakReference.get() != null) {
            mCalendarViewWeakReference.get().postInvalidate();
        }
        return this;
    }

    /**
     * 设置假期信息提供者
     * @param holidayInfoProvider
     * @return
     */
    public DefaultItemSelectedImpl setHolidayInfoProvider(HolidayInfoProvider holidayInfoProvider) {
        mHolidayInfoProvider = holidayInfoProvider;
        if(mCalendarViewWeakReference.get() != null) {
            mCalendarViewWeakReference.get().postInvalidate();
        }
        return this;
    }

    /**
     * 设置节气信息提供者
     * @param termInfoProvider
     * @return
     */
    public DefaultItemSelectedImpl setTermInfoProvider(TermInfoProvider termInfoProvider) {
        mTermInfoProvider = termInfoProvider;
        if(mCalendarViewWeakReference.get() != null) {
            mCalendarViewWeakReference.get().postInvalidate();
        }
        return this;
    }

    /**
     * 设置角标的padding
     * @param cornerPadding
     * @return
     */
    public DefaultItemSelectedImpl setCornerPadding(int cornerPadding) {
        mCornerPadding = cornerPadding;
        if(mCalendarViewWeakReference.get() != null) {
            mCalendarViewWeakReference.get().postInvalidate();
        }
        return this;
    }

}

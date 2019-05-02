package rygel.cn.calendarview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import rygel.cn.calendar.bean.Solar;
import rygel.cn.calendarview.adapter.MonthAdapter;
import rygel.cn.calendarview.item.ItemCommon;
import rygel.cn.calendarview.item.ItemSelected;
import rygel.cn.calendarview.item.ItemToday;
import rygel.cn.calendarview.item.impl.DefaultItemCommonImpl;
import rygel.cn.calendarview.item.impl.DefaultItemSelectedImpl;
import rygel.cn.calendarview.item.impl.DefaultItemTodayImpl;
import rygel.cn.calendarview.listener.OnDateSelectedListener;
import rygel.cn.calendarview.listener.OnMonthChangedListener;
import rygel.cn.calendarview.weekbar.WeekBar;
import rygel.cn.calendarview.weekbar.impl.DefaultWeekBarImpl;

import java.lang.ref.WeakReference;

/**
 * 自定义日历控件
 * @author Rygel
 */
public class CalendarView extends ViewPager {

    private static final String TAG = "CalendarView";

    private MonthAdapter mAdapter = new MonthAdapter(this);
    private Config mConfig = new Config(this);

    public CalendarView(@NonNull Context context) {
        super(context);
    }

    public CalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initConfig();
        setAdapter(mAdapter);
    }

    /**
     * 初始化配置项
     */
    private void initConfig() {
        mAdapter.setConfig(mConfig.setWeekBar(new DefaultWeekBarImpl(this))
                .setItemCommon(new DefaultItemCommonImpl(this))
                .setItemSelected(new DefaultItemSelectedImpl(this))
                .setItemToday(new DefaultItemTodayImpl(this)));
    }

    /**
     * 获取当前配置项
     * @return
     */
    public Config getConfig() {
        return mConfig;
    }

    /**
     * 设置配置项
     * @param config
     */
    private void setConfig(Config config) {
        mAdapter.setConfig(config);
    }

    /**
     * 选中指定日期
     * @param solar
     */
    public void setSelect(Solar solar) {
        mAdapter.select(solar);
    }

    /**
     * 跳转到上一个月
     * @param anim 是否显示动画
     */
    public void getToLastMonth(boolean anim) {
        mAdapter.getToLastMonth(anim);
    }

    /**
     * 跳转到上一年
     * @param anim 是否显示动画
     */
    public void getToLastYear(boolean anim) {
        mAdapter.getToLastYear(anim);
    }

    /**
     * 跳转到下一个月
     * @param anim 是否显示动画
     */
    public void getToNextMonth(boolean anim) {
        mAdapter.getToNextMonth(anim);
    }

    /**
     * 跳转到下一年
     * @param anim 是否显示动画
     */
    public void getToNextYear(boolean anim) {
        mAdapter.getToNextYear(anim);
    }

    /**
     *
     * @param year
     * @param month
     */
    public void getToMonth(int year,int month,boolean anim) {
        mAdapter.getToMonth(year, month, anim);
    }

    /**
     * 设置日期选中监听
     * @param onDateSelectedListener
     */
    public void setOnDateSelectListener(OnDateSelectedListener onDateSelectedListener) {
        mAdapter.setOnDateSelectedListener(onDateSelectedListener);
    }

    /**
     * 设置月份更改监听
     * @param onMonthChangedListener
     */
    public void setOnMonthChangedListener(OnMonthChangedListener onMonthChangedListener) {
        mAdapter.setOnMonthChangeListener(onMonthChangedListener);
    }

    /**
     * 配置项
     */
    public static class Config {

        private WeakReference<CalendarView> mCalendarViewWeakReference;

        private int mWeekBarHeight = 120;

        private int mChildPaddingLeft = 10;
        private int mChildPaddingRight = 10;
        private int mChildPaddingTop = 10;
        private int mChildPaddingBottom = 10;

        private WeekBar mWeekBar;
        private ItemCommon mItemCommon;
        private ItemSelected mItemSelected;
        private ItemToday mItemToday;

        private int mStartOffset = 0;

        public Config(CalendarView view) {
            mCalendarViewWeakReference = new WeakReference<>(view);
        }

        public int getWeekBarHeight() {
            return mWeekBarHeight;
        }

        public int getChildPaddingLeft() {
            return mChildPaddingLeft;
        }

        public int getChildPaddingRight() {
            return mChildPaddingRight;
        }

        public int getChildPaddingTop() {
            return mChildPaddingTop;
        }

        public int getChildPaddingBottom() {
            return mChildPaddingBottom;
        }

        public int getStartOffset() {
            return mStartOffset;
        }

        public WeekBar getWeekBar() {
            return mWeekBar;
        }

        public ItemCommon getItemCommon() {
            return mItemCommon;
        }

        public ItemSelected getItemSelected() {
            return mItemSelected;
        }

        public ItemToday getItemToday() {
            return mItemToday;
        }

        public Config setStartOffset(int startOffset) {
            mStartOffset = startOffset;
            return this;
        }

        public Config setWeekBarHeight(int weekBarHeight) {
            mWeekBarHeight = weekBarHeight;
            return this;
        }

        public Config setChildPaddingLeft(int childPaddingLeft) {
            mChildPaddingLeft = childPaddingLeft;
            return this;
        }

        public Config setChildPaddingRight(int childPaddingRight) {
            mChildPaddingRight = childPaddingRight;
            return this;
        }

        public Config setChildPaddingTop(int childPaddingTop) {
            mChildPaddingTop = childPaddingTop;
            return this;
        }

        public Config setChildPaddingBottom(int childPaddingBottom) {
            mChildPaddingBottom = childPaddingBottom;
            return this;
        }

        public Config setWeekBar(WeekBar weekBar) {
            mWeekBar = weekBar;
            return this;
        }

        public Config setItemCommon(ItemCommon itemCommon) {
            mItemCommon = itemCommon;
            return this;
        }

        public Config setItemSelected(ItemSelected itemSelected) {
            mItemSelected = itemSelected;
            return this;
        }

        public Config setItemToday(ItemToday itemToday) {
            mItemToday = itemToday;
            return this;
        }

        public CalendarView config() {
            if(mCalendarViewWeakReference.get() != null) {
                mCalendarViewWeakReference.get().setConfig(this);
            }
            return mCalendarViewWeakReference.get();
        }

    }

}

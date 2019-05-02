package rygel.cn.calendarview.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import rygel.cn.calendar.bean.Solar;
import rygel.cn.calendar.utils.SolarUtils;
import rygel.cn.calendarview.CalendarView;
import rygel.cn.calendarview.MonthView;
import rygel.cn.calendarview.listener.OnDateSelectedListener;
import rygel.cn.calendarview.listener.OnMonthChangedListener;

/**
 * 这个类用于为{@link CalendarView}提供适配器
 * @author Rygel
 */
public class MonthAdapter extends PagerAdapter {

    private static final String TAG = "MonthAdapter";

    public static final int MONTH_COUNT = 12 * 199;

    private SparseArray<MonthView> mCachedCalendarViews = new SparseArray<>();

    private Solar mToday = SolarUtils.today();

    private OnDateSelectedListener mOnDateSelectedListener = null;

    private OnMonthChangedListener mOnMonthChangeListener = null;

    private ViewPager mCalendarPager = null;

    private Solar mSelected = null;

    private int mLastItem = -1;

    private int mChildCount = 0;

    private CalendarView.Config mConfig = null;

    public MonthAdapter(ViewPager calendarPager) {
        mCalendarPager = calendarPager;
        mCalendarPager.setOffscreenPageLimit(0);
        mCalendarPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if(mOnMonthChangeListener != null){
                    mOnMonthChangeListener.OnMonthChange(getYearByPosition(i),getMonthByPosition(i));
                }
                removeSelectItem();
                mLastItem = i;
                if(mSelected != null && i == getIndexByMonth(mSelected.solarYear,mSelected.solarMonth)) {
                    MonthView month = mCachedCalendarViews.get(i);
                    if(month != null){
                        month.setSelectIndex(mSelected.solarDay - 1);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (mChildCount > 0) {
            mChildCount --;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }

    @Override
    public void notifyDataSetChanged() {
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        if(position < 0 ){
            return super.instantiateItem(container, position);
        }
        final int year = getYearByPosition(position);
        final int month =  getMonthByPosition(position);
        MonthView monthView = mCachedCalendarViews.get(position);
        if(monthView == null){
            monthView = new MonthView(container.getContext());
            monthView.getConfig()
                    .setWeekBarHeight(mConfig.getWeekBarHeight())
                    .setChildPaddingTop(mConfig.getChildPaddingTop())
                    .setChildPaddingBottom(mConfig.getChildPaddingBottom())
                    .setChildPaddingLeft(mConfig.getChildPaddingLeft())
                    .setChildPaddingRight(mConfig.getChildPaddingRight())
                    .setWeekBar(mConfig.getWeekBar())
                    .setItemCommon(mConfig.getItemCommon())
                    .setItemSelected(mConfig.getItemSelected())
                    .setItemToday(mConfig.getItemToday())
                    .setStartOffset(mConfig.getStartOffset())
                    .setYear(year)
                    .setMonth(month)
                    .setTodayIndex(SolarUtils.isDateInMonth(mToday, year, month) ? mToday.solarDay - 1 : -1)
                    .setSelectIndex(mSelected == null || !SolarUtils.isDateInMonth(mSelected, year, month) ? - 1 : mSelected.solarDay -1)
                    .config();
            monthView.setOnDateSelectedListener(new OnDateSelectedListener() {
                @Override
                public void onSelected(Solar solar) {
                    mSelected = solar;
                    if(mOnDateSelectedListener != null) {
                        mOnDateSelectedListener.onSelected(mSelected);
                    }
                }
            });
            mCachedCalendarViews.put(position,monthView);
        }
        if(mSelected != null && position == mCalendarPager.getCurrentItem() && position == getIndexByMonth(mSelected.solarYear,mSelected.solarMonth)) {
            if (!mSelected.equals(monthView.getSelectDate())) {
                monthView.setSelectIndex(mSelected.solarDay - 1);
            }
        }
        container.addView(monthView);
        return monthView;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        removeParent((View) object);
    }

    private void removeParent(View view){
        if(view.getParent() != null && view.getParent() instanceof ViewGroup){
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    @Override
    public int getCount() {
        return MONTH_COUNT;
    }

    /**
     * 根据月份计算出他在日历中的位置
     * @param year 年
     * @param month 月
     * @return
     */
    private int getIndexByMonth(int year, int month){
        if(year < 1901 || year > 2099) {
            Log.e(TAG,"year out of bound");
        }
        if(month < 1 || month > 12) {
            Log.e(TAG,"month out of bound");
        }
        return (year - 1901) * 12 + month - 1;
    }

    /**
     * 选择相应的日期
     * @param solar
     */
    public void select(Solar solar){
        mSelected = solar;
        int targetIndex = getIndexByMonth(solar.solarYear,solar.solarMonth);
        if(targetIndex == mCalendarPager.getCurrentItem()){
            MonthView month = mCachedCalendarViews.get(targetIndex);
            if(month != null){
                month.setSelectIndex(solar.solarDay - 1);
            }
        } else {
            mCalendarPager.setCurrentItem(targetIndex,true);
        }
    }

    /**
     * 跳转到指定月份
     * @param year
     * @param month
     * @param anim
     */
    public void getToMonth(int year,int month,boolean anim) {
        if(year < 1901 || year > 2099) {
            Log.e(TAG,"year out of bound");
            return;
        }
        if(month < 1 || month > 12) {
            Log.e(TAG,"month out of bound");
            return;
        }
        mCalendarPager.setCurrentItem(getIndexByMonth(year, month),anim);
    }

    /**
     * 跳转到上一个月
     * @param anim 是否显示动画
     */
    public void getToLastMonth(boolean anim) {
        if(mCalendarPager.getCurrentItem() - 1 < 0) {
            Log.e(TAG,"this is the first month");
            return;
        }
        mCalendarPager.setCurrentItem(mCalendarPager.getCurrentItem() - 1,anim);
    }

    /**
     * 跳转到上一年
     * @param anim 是否显示动画
     */
    public void getToLastYear(boolean anim) {
        if(mCalendarPager.getCurrentItem() - 12 < 0) {
            Log.e(TAG,"this is the first year");
            return;
        }
        mCalendarPager.setCurrentItem(mCalendarPager.getCurrentItem() - 12,anim);
    }

    /**
     * 跳转到下一个月
     * @param anim 是否显示动画
     */
    public void getToNextMonth(boolean anim) {
        if(mCalendarPager.getCurrentItem() + 1 >= MonthAdapter.MONTH_COUNT) {
            Log.e(TAG,"this is the last month");
            return;
        }
        mCalendarPager.setCurrentItem(mCalendarPager.getCurrentItem() + 1,anim);
    }

    /**
     * 跳转到下一年
     * @param anim 是否显示动画
     */
    public void getToNextYear(boolean anim) {
        if(mCalendarPager.getCurrentItem() + 1 >= MonthAdapter.MONTH_COUNT) {
            Log.e(TAG,"this is the last year");
            return;
        }
        mCalendarPager.setCurrentItem(mCalendarPager.getCurrentItem() + 12,anim);
    }

    /**
     * 获取当前选中的日期
     * @return
     */
    public Solar getSelectDate(){
        if(mSelected == null) {
            return null;
        }
        return new Solar(mSelected.solarYear,mSelected.solarMonth,mSelected.solarDay);
    }

    /**
     * 用于判断当前显示的月份是否是第一个月
     * @return
     */
    public boolean isFirstMonth() {
        return mCalendarPager.getCurrentItem() == 0;
    }

    /**
     * 用于判断当前显示的月份是否是最后一个月
     * @return
     */
    public boolean isLastMonth() {
        return mCalendarPager.getCurrentItem() == MONTH_COUNT - 1;
    }

    /**
     * 根据位置计算出当前的年份
     * @param position
     * @return
     */
    protected int getYearByPosition(int position){
        return position / 12 + 1901;
    }

    /**
     * 根据位置计算出当前的月份
     * @param position
     * @return
     */
    protected int getMonthByPosition(int position){
        return position % 12 + 1;
    }

    /**
     * 设置选中日期变换的监听
     * @param onDateSelectedListener
     */
    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
        mOnDateSelectedListener = onDateSelectedListener;
        mCachedCalendarViews.clear();
    }

    /**
     * 设置月份变换的监听
     * @param onMonthChangeListener
     */
    public void setOnMonthChangeListener(OnMonthChangedListener onMonthChangeListener) {
        mOnMonthChangeListener = onMonthChangeListener;
    }

    /**
     * 获取当前的配置项
     * @return
     */
    public CalendarView.Config getConfig() {
        return mConfig;
    }

    /**
     * 设置配置项
     * @param config
     */
    public void setConfig(CalendarView.Config config) {
        mConfig = config;
        mCalendarPager.postInvalidate();
    }

    /**
     * 移除当前选中的项目
     */
    protected void removeSelectItem(){
        if(mLastItem >= 0){
            MonthView month = mCachedCalendarViews.get(mLastItem);
            if(month != null){
                month.setSelectIndex(-1);
            }
        }
    }

}

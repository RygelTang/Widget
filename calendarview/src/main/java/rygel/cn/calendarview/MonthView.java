package rygel.cn.calendarview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import rygel.cn.calendar.bean.Solar;
import rygel.cn.calendar.utils.SolarUtils;
import rygel.cn.calendarview.item.ItemCommon;
import rygel.cn.calendarview.item.ItemSelected;
import rygel.cn.calendarview.item.ItemToday;
import rygel.cn.calendarview.listener.OnDateSelectedListener;
import rygel.cn.calendarview.weekbar.WeekBar;

import java.lang.ref.WeakReference;

/**
 * 月份View
 * @author Rygel
 */
public class MonthView extends View {

    private static final String TAG = "MonthView";

    public static final int MIN_YEAR = 1901;
    public static final int MAX_YEAR = 2099;

    public static final int MIN_MONTH = 1;
    public static final int MAX_MONTH = 12;

    /******************************************************************************************************/
    private Config mConfig = new Config(this);

    private Rect mBound = new Rect();
    private Rect mWeekBarBound = new Rect();
    private Rect mChildBound = new Rect();

    private boolean mFirstLoad = true;
    private float mFirstLoadAnimationPercent = 0F;
    private float mSelectAnimationPercent = 0F;

    private OnDateSelectedListener mOnDateSelectedListener = null;

    private Solar mTempSolar = new Solar();

    private int mDownX = 0;
    private int mDownY = 0;

    private OnTouchListener mTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    onActionDown(event);
                    break;
                case MotionEvent.ACTION_UP:
                    onActionUp(event);
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    private ObjectAnimator mFirstLoadAnimator = null;

    private ObjectAnimator mSelectAnimator = null;

    public MonthView(Context context) {
        this(context, null);
    }

    public MonthView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MonthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAnimator();
        setClickable(true);
        setOnTouchListener(mTouchListener);
    }

    /**
     * 初始化动画
     */
    private void initAnimator() {
        mFirstLoadAnimator = ObjectAnimator
                .ofFloat(this,"FirstLoadAnimationPercent",0F,1F)
                .setDuration(300);
        mSelectAnimator = ObjectAnimator
                .ofFloat(this,"SelectAnimationPercent",0F,1F)
                .setDuration(300);
    }

    @Override
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        mWeekBarBound.left = paddingLeft;
        mWeekBarBound.top = paddingTop;
        mWeekBarBound.right = getMeasuredWidth() - paddingRight;
        mWeekBarBound.bottom = paddingTop + mConfig.mWeekBarHeight;

        mBound.left = paddingLeft;
        mBound.top = mWeekBarBound.bottom;
        mBound.right = getMeasuredWidth() - paddingRight;
        mBound.bottom = getMeasuredHeight() - paddingBottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 对绘制需要的参数进行检查
        if(mConfig.mWeekBar == null) {
            throw new IllegalStateException("week bar should be generate first!");
        }
        if(mConfig.mItemCommon == null) {
            throw new IllegalStateException("common item should be generate first!");
        }
        if(mConfig.mItemSelected == null) {
            throw new IllegalStateException("select item should be generate first!");
        }
        if(mConfig.mItemToday == null) {
            throw new IllegalStateException("today item should be generate first!");
        }
        if(mFirstLoad) {
            if(mFirstLoadAnimator != null) {
                mFirstLoadAnimator.start();
                mFirstLoad = false;
            }
        }
        mConfig.mWeekBar.draw(canvas, mWeekBarBound, mConfig.mStartOffset);
        int maxWeek = getMaxWeeks();
        mTempSolar.solarYear = mConfig.mYear;
        mTempSolar.solarMonth = mConfig.mMonth;
        for (int i = 0; i < getMonthDays(); i++) {
            mTempSolar.solarDay = i + 1;
            if (i == mConfig.mSelectIndex) {
                if(mSelectAnimator != null && mSelectAnimator.isRunning()) {
                    mConfig.mItemSelected.anim(canvas, getChildBound(i, maxWeek), mTempSolar, mSelectAnimationPercent);
                } else {
                    mConfig.mItemSelected.draw(canvas, getChildBound(i, maxWeek), mTempSolar);
                }
            } else if(i == mConfig.mTodayIndex) {
                if (mFirstLoadAnimator != null && mFirstLoadAnimator.isRunning()) {
                    mConfig.mItemToday.anim(canvas, getChildBound(i, maxWeek), mTempSolar, mFirstLoadAnimationPercent);
                } else {
                    mConfig.mItemToday.draw(canvas, getChildBound(i, maxWeek), mTempSolar);
                }
            } else {
                mConfig.mItemCommon.draw(canvas, getChildBound(i, maxWeek), mTempSolar);
            }
        }
    }

    /**
     * 根据index和本月跨过的周数来计算子项的大小
     * @param index
     * @param maxWeek
     * @return
     */
    private Rect getChildBound(int index, int maxWeek){
        index += getStartIndex();
        final int column = index % 7;
        final int row = index / 7;
        final int itemWidth = mBound.width() / 7;
        final int itemHeight = mBound.height() / maxWeek;
        mChildBound.left = mBound.left + column * itemWidth + mConfig.mChildPaddingLeft;
        mChildBound.right = mBound.left + (column + 1) * itemWidth - mConfig.mChildPaddingRight;
        mChildBound.top = mBound.top + row * itemHeight + mConfig.mChildPaddingTop;
        mChildBound.bottom = mBound.top + (row + 1) * itemHeight - mConfig.mChildPaddingBottom;
        return mChildBound;
    }

    /**
     * 按下的操作
     * @param event
     */
    protected void onActionDown(MotionEvent event){
        mDownX = (int) event.getX();
        mDownY = (int) event.getY();
        Log.i(TAG,"action down : " + mDownX + "," + mDownY);
    }

    /**
     * 手指离开的操作
     * @param event
     */
    protected void onActionUp(MotionEvent event){
        int maxOffset = 20;
        if(Math.abs(event.getX() - mDownX) > maxOffset || Math.abs(event.getY() - mDownY) > maxOffset){
            return;
        }
        setSelectIndex(getItemIndexBy(event.getX(),event.getY()));
        Log.i(TAG,"action up : " + event.getX() + "," + event.getY());
        playSoundEffect(SoundEffectConstants.CLICK);
    }

    /**
     * 设置选中的index
     * @param selectIndex
     */
    public void setSelectIndex(int selectIndex) {
        if(selectIndex == mConfig.mSelectIndex) {
            return;
        }
        mConfig.mSelectIndex = selectIndex;
        Log.i(TAG,"select index : " + selectIndex);
        // 确保未超出月历范围
        if(selectIndex < 0 || selectIndex >= getMonthDays()){
            Log.i(TAG,"select index out of range, would not call back");
            return;
        }
        if(mOnDateSelectedListener != null) {
            Solar select = getSelectDate();
            if(select != null) {
                mOnDateSelectedListener.onSelected(select);
            }
        }
        if(mSelectAnimator == null) {
            return;
        }
        if(mSelectAnimator.isRunning()) {
            mSelectAnimator.cancel();
        }
        mSelectAnimator.start();
    }

    /**
     * 根据手指位置获取被选中的index
     * @param x
     * @param y
     * @return
     */
    private int getItemIndexBy(float x, float y){
        if(x < mBound.left || x > mBound.right){
            return -1;
        }
        if(y < mBound.top || y > mBound.bottom){
            return -1;
        }
        final int column = (int) ((x - mBound.left) / (mChildBound.width() + mConfig.mChildPaddingLeft + mConfig.mChildPaddingRight));
        final int row = (int) ((y - mBound.top) / (mChildBound.height() + mConfig.mChildPaddingTop + mConfig.mChildPaddingBottom));
        int index = column + 7 * row - getStartIndex();
        if(index >= getMonthDays()){
            return -1;
        }
        return index;
    }

    /**
     * 计算该月跨过了几周
     * @return
     */
    private int getMaxWeeks() {
        return (getMonthDays() + getStartIndex()) / 7 + ((getMonthDays() + getStartIndex()) % 7 == 0 ? 0 : 1);
    }

    /**
     * 获取该月第一天的index
     * @return
     */
    private int getStartIndex() {
        int startIndex = SolarUtils.getWeekDay(new Solar(mConfig.mYear, mConfig.mMonth, 1)) - mConfig.mStartOffset;
        return startIndex < 0 ? startIndex + 7 : startIndex;
    }

    /**
     * 计算该月共有多少天
     * @return
     */
    private int getMonthDays() {
        return SolarUtils.getMonthDay(mConfig.mYear, mConfig.mMonth);
    }

    public void setWeekBarHeight(int weekBarHeight) {
        mConfig.mWeekBarHeight = weekBarHeight;
        postInvalidate();
    }

    public void setChildPaddingLeft(int childPaddingLeft) {
        mConfig.mChildPaddingLeft = childPaddingLeft;
        postInvalidate();
    }

    public void setChildPaddingRight(int childPaddingRight) {
        mConfig.mChildPaddingRight = childPaddingRight;
        postInvalidate();
    }

    public void setChildPaddingTop(int childPaddingTop) {
        mConfig.mChildPaddingTop = childPaddingTop;
        postInvalidate();
    }

    public void setChildPaddingBottom(int childPaddingBottom) {
        mConfig.mChildPaddingBottom = childPaddingBottom;
        postInvalidate();
    }

    public void setYear(int year) {
        if(year > MAX_YEAR || year < MIN_YEAR) {
            throw new IllegalArgumentException("out of bound!");
        }
        mConfig.mYear = year;
        postInvalidate();
    }

    public void setMonth(int month) {
        if(month > MAX_MONTH || month < MIN_MONTH) {
            throw new IllegalArgumentException("out of bound!");
        }
        mConfig.mMonth = month;
        postInvalidate();
    }

    public void setTodayIndex(int todayIndex) {
        mConfig.mTodayIndex = todayIndex;
        postInvalidate();
    }

    public void setStartOffset(int startOffset) {
        mConfig.mStartOffset = startOffset;
        postInvalidate();
    }

    public void setWeekBar(WeekBar weekBar) {
        mConfig.mWeekBar = weekBar;
        postInvalidate();
    }

    public void setItemCommon(ItemCommon itemCommon) {
        mConfig.mItemCommon = itemCommon;
        postInvalidate();
    }

    public void setItemSelected(ItemSelected itemSelected) {
        mConfig.mItemSelected = itemSelected;
        postInvalidate();
    }

    public void setItemToday(ItemToday itemToday) {
        mConfig.mItemToday = itemToday;
        postInvalidate();
    }

    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
        mOnDateSelectedListener = onDateSelectedListener;
    }

    private float getFirstLoadAnimationPercent() {
        return mFirstLoadAnimationPercent;
    }

    public void setFirstLoadAnimationPercent(float firstLoadAnimationPercent) {
        mFirstLoadAnimationPercent = firstLoadAnimationPercent;
        postInvalidate();
    }

    private float getSelectAnimationPercent() {
        return mSelectAnimationPercent;
    }

    public void setSelectAnimationPercent(float selectAnimationPercent) {
        mSelectAnimationPercent = selectAnimationPercent;
        postInvalidate();
    }

    public Solar getSelectDate() {
        if (mConfig.mSelectIndex < 0) {
            return null;
        }
        return new Solar(mConfig.mYear, mConfig.mMonth, mConfig.mSelectIndex + 1);
    }

    public Config getConfig() {
        return mConfig;
    }

    public void setConfig(Config config) {
        mConfig = config;
        postInvalidate();
    }

    public static class Config {

        private WeakReference<MonthView> mMonthViewWeakReference;

        private int mWeekBarHeight = 120;

        private int mChildPaddingLeft = 10;
        private int mChildPaddingRight = 10;
        private int mChildPaddingTop = 10;
        private int mChildPaddingBottom = 10;

        private int mYear = 1901;
        private int mMonth = 1;

        private int mSelectIndex = -1;
        private int mTodayIndex = -1;

        private int mStartOffset = 0;

        private WeekBar mWeekBar;
        private ItemCommon mItemCommon;
        private ItemSelected mItemSelected;
        private ItemToday mItemToday;

        public Config(MonthView view) {
            mMonthViewWeakReference = new WeakReference<>(view);
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

        public Config setYear(int year) {
            mYear = year;
            return this;
        }

        public Config setMonth(int month) {
            mMonth = month;
            return this;
        }

        public Config setSelectIndex(int selectIndex) {
            mSelectIndex = selectIndex;
            return this;
        }

        public Config setTodayIndex(int todayIndex) {
            mTodayIndex = todayIndex;
            return this;
        }

        public Config setStartOffset(int startOffset) {
            mStartOffset = startOffset;
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

        public MonthView config() {
            if(mMonthViewWeakReference.get() != null) {
                mMonthViewWeakReference.get().setConfig(this);
            }
            return mMonthViewWeakReference.get();
        }

    }
}

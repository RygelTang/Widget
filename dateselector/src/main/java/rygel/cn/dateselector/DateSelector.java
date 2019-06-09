package rygel.cn.dateselector;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cncoderx.wheelview.OnWheelChangedListener;
import com.cncoderx.wheelview.Wheel3DView;
import com.cncoderx.wheelview.WheelView;
import com.kyleduo.switchbutton.SwitchButton;
import rygel.cn.calendar.bean.Lunar;
import rygel.cn.calendar.bean.Solar;
import rygel.cn.calendar.utils.LunarUtils;
import rygel.cn.calendar.utils.SolarUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 日期选择器
 * @author Rygel
 */
public class DateSelector extends LinearLayout {

    private static final String TAG = "DateSelector";

    private Wheel3DView mYearSelector;
    private Wheel3DView mMonthSelector;
    private Wheel3DView mDaySelector;

    private SwitchButton mLunarSwitchButton;
    private TextView mLunarSwitchText;

    private Solar mSelectDate = SolarUtils.today();
    private Lunar mSelectLunar = mSelectDate.toLunar();
    private OnDateSelectListener mOnDateSelectListener;

    private static final int MIN_YEAR = 1901;

    private static final int YEAR_COUNT = 199;

    private OnWheelChangedListener mYearChangeListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView view, int oldIndex, int newIndex) {
            onYearIndexChange(newIndex);
        }
    };
    private OnWheelChangedListener mMonthChangeListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView view, int oldIndex, int newIndex) {
            onMonthIndexChange(newIndex);
        }
    };
    private OnWheelChangedListener mDayChangeListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView view, int oldIndex, int newIndex) {
            onDayIndexChange(newIndex);
        }
    };

    private static final List<String> YEAR = new ArrayList<>();
    private static final List<String> SOLAR_MONTHS = new ArrayList<>();
    private static final List<String> SOLAR_DAYS = new ArrayList<>();

    private static final String[] LUNAR_MONTHS = {
            "一月", "二月", "三月", "四月", "五月", "六月",
            "七月", "八月", "九月", "十月", "十一月", "腊月"
    };

    private static final String[] LUNAR_DAYS = {
            "初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十",
            "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "廿十",
            "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "卅十"
    };

    static {
        initYears();
        initSolarMonths();
        initSolarDays();
    }

    private static void initYears(){
        YEAR.clear();
        for(int i = 1901;i <= 2099;i++) {
            YEAR.add(i + "年");
        }
    }

    private static void initSolarMonths(){
        SOLAR_MONTHS.clear();
        for(int i = 1;i <= 12;i++) {
            SOLAR_MONTHS.add(i + "月");
        }
    }

    private static void initSolarDays(){
        SOLAR_DAYS.clear();
        for(int i = 1;i <= 31;i++) {
            SOLAR_DAYS.add(i + "日");
        }
    }

    /*************************************** Constructor **************************************/
    public DateSelector(Context context) {
        this(context, null);
    }

    public DateSelector(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DateSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(getContext(), R.layout.widget_date_selector, this);
        findViewById(R.id.mBtnSelect).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnDateSelectListener != null) {
                    mOnDateSelectListener.onSelect(mSelectDate, getSelectMode());
                }
            }
        });
        initSwitcher();
        initSelectors();
        select(mSelectDate);
    }

    /**
     * 初始化切换器
     */
    private void initSwitcher() {
        mLunarSwitchButton = findViewById(R.id.mLunarSwitcher);
        mLunarSwitchText = findViewById(R.id.mLunarSwitcherText);

        mLunarSwitchText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mLunarSwitchButton.setChecked(!mLunarSwitchButton.isChecked());
            }
        });
        mLunarSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchToLunarMode();
                } else {
                    switchToSolarMode();
                }
            }
        });
    }

    /**
     * 初始化选择器
     */
    private void initSelectors() {
        mYearSelector = findViewById(R.id.mYearSelector);
        mMonthSelector = findViewById(R.id.mMonthSelector);
        mDaySelector = findViewById(R.id.mDaySelector);

        mYearSelector.setEntries(YEAR);
        mMonthSelector.setEntries(SOLAR_MONTHS);
        mDaySelector.setEntries(SOLAR_DAYS);

        mYearSelector.setOnWheelChangedListener(mYearChangeListener);
        mMonthSelector.setOnWheelChangedListener(mMonthChangeListener);
        mDaySelector.setOnWheelChangedListener(mDayChangeListener);
    }

    private void onYearIndexChange(int index) {
        int year = index + MIN_YEAR;
        int monthIndex = mMonthSelector.getCurrentIndex();
        if(getSelectMode()) {
            mSelectLunar.lunarYear = year;
            mMonthSelector.setEntries(getLunarMonthEntries(year));
            mMonthSelector.setCurrentIndex(monthIndex);
        } else {
            mSelectDate.solarYear = year;
        }
    }

    private void onMonthIndexChange(int index) {
        int month = index + 1;
        int dayIndex = mDaySelector.getCurrentIndex();
        if(getSelectMode()) {
            int leap = LunarUtils.getLeapMonth(mSelectLunar.lunarYear);
            mSelectLunar.isLeap = leap > 0 && leap == index;
            mSelectLunar.lunarMonth = month - (mSelectLunar.isLeap || (leap > 0 && leap < month) ? 1 : 0);
            mDaySelector.setEntries(getLunarDayEntries(mSelectLunar.lunarYear,mSelectLunar.lunarMonth,mSelectLunar.isLeap));
            mDaySelector.setCurrentIndex(dayIndex);
        } else {
            mSelectDate.solarMonth = month;
            mDaySelector.setEntries(getSolarDayEntries(mSelectDate.solarYear,mSelectDate.solarMonth));
            mDaySelector.setCurrentIndex(dayIndex);
        }

    }

    private void onDayIndexChange(int index) {
        int day = index + 1;
        if(getSelectMode()) {
            mSelectLunar.lunarDay = day;
        } else {
            mSelectDate.solarDay = day;
        }
    }

    private boolean getSelectMode() {
        return mLunarSwitchButton.isChecked();
    }

    private static List<String> getLunarMonthEntries(int year){
        List<String> months = new ArrayList<>();
        Collections.addAll(months,LUNAR_MONTHS);
        int leapMonth = LunarUtils.getLeapMonth(year);
        if(leapMonth > 0){
            months.add(leapMonth,"闰" + LUNAR_MONTHS[leapMonth - 1]);
        }
        return months;
    }

    private static List<String> getLunarDayEntries(int year,int month,boolean isLeap) {
        List<String> days = new ArrayList<>();
        int daysInMonth = LunarUtils.daysInMonth(year,month,isLeap) - 1;
        for(int i = 0;i < daysInMonth;i++){
            days.add(LUNAR_DAYS[i]);
        }
        return days;
    }

    private static List<String> getSolarDayEntries(int year,int month){
        return SOLAR_DAYS.subList(0,SolarUtils.getMonthDay(year, month));
    }

    private void switchToLunarMode(){
        final Lunar lunar = mSelectDate.toLunar();
        final int leap = LunarUtils.getLeapMonth(lunar.lunarYear);
        final int leapOffset = leap > 0 && (leap > lunar.lunarMonth || lunar.isLeap) ? 1 : 0;
        mYearSelector.setCurrentIndex(lunar.lunarYear - MIN_YEAR);
        mMonthSelector.setEntries(getLunarMonthEntries(lunar.lunarYear));
        mMonthSelector.setCurrentIndex(lunar.lunarMonth - 1 + leapOffset);
        mDaySelector.setEntries(getLunarDayEntries(lunar.lunarYear,lunar.lunarMonth,lunar.isLeap));
        mDaySelector.setCurrentIndex(lunar.lunarDay - 1);
        mSelectLunar = lunar;
    }

    private void switchToSolarMode(){
        Solar solar = mSelectLunar.toSolar();
        mYearSelector.setCurrentIndex(solar.solarYear - MIN_YEAR);
        mMonthSelector.setEntries(SOLAR_MONTHS);
        mMonthSelector.setCurrentIndex(solar.solarMonth - 1);
        mDaySelector.setEntries(getSolarDayEntries(solar.solarYear,solar.solarMonth));
        mDaySelector.setCurrentIndex(solar.solarDay - 1);
        mSelectDate = solar;
    }

    private void select(Solar solar) {
        if(getSelectMode()) {
            mSelectLunar = solar.toLunar();
            switchToLunarMode();
        } else {
            mSelectDate = solar;
            switchToSolarMode();
        }
    }

    /**
     * 设置监听
     * @param onDateSelectListener
     */
    public void setOndateSelectListener(OnDateSelectListener onDateSelectListener) {
        this.mOnDateSelectListener = onDateSelectListener;
    }

    public static interface OnDateSelectListener {
        /**
         * 当选中日期发生改变时回调
         * @param solar 选中的日期的公历日期
         * @param isLunarMode 用于标志当前是农历选择模式下选中的还是公历模式下选中的
         *                    true 表示农历模式
         *                    false 表示公历模式
         */
        void onSelect(Solar solar,boolean isLunarMode);
    }

}

package rygel.cn.calendarview.provider.impl;

import rygel.cn.calendar.bean.Lunar;
import rygel.cn.calendar.bean.Solar;
import rygel.cn.calendar.utils.SolarUtils;
import rygel.cn.calendarview.provider.HolidayInfoProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于提供节假日信息
 * @author Rygel
 */
public class DefaultHolidayInfoProvider implements HolidayInfoProvider {

    private final static Map<Date,String> SOLAR_HOLIDAY = new HashMap<>();
    private final static Map<Date,String> LUNAR_HOLIDAY = new HashMap<>();
    private final static List<Solar> STATUTORY_HOLIDAY = new ArrayList<>();
    private final static List<Solar> MAKE_UP_DAY = new ArrayList<>();

    static {
        initStatutoryHoliday();
        initMakeUpDay();
        initSolarHolidays();
        initLunarHoliday();
    }

    @Override
    public boolean isNormalHoliday(Solar solar) {
        Lunar lunar = solar.toLunar();
        return isChineseNYEve(solar) ||
                isFatherDay(solar) ||
                isMonthDay(solar) ||
                isThanksGivingDay(solar) ||
                isTombSweepingDay(solar) ||
                SOLAR_HOLIDAY.containsKey(new Date(solar.solarMonth,solar.solarDay)) ||
                (!lunar.isLeap && LUNAR_HOLIDAY.containsKey(new Date(lunar.lunarMonth,lunar.lunarDay)));
    }

    @Override
    public boolean isStatutoryHoliday(Solar solar) {
        for(Solar s : STATUTORY_HOLIDAY) {
            if(s.equals(solar)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isMakeUpDay(Solar solar) {
        for(Solar s : MAKE_UP_DAY) {
            if(s.equals(solar)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getHolidayString(Solar solar) {
        if(isChineseNYEve(solar)) {
            return "除夕";
        }
        if(isTombSweepingDay(solar)) {
            return "清明节";
        }
        if(isFatherDay(solar)) {
            return "父亲节";
        }
        if(isMonthDay(solar)) {
            return "母亲节";
        }
        if(isThanksGivingDay(solar)) {
            return "感恩节";
        }
        if(SOLAR_HOLIDAY.containsKey(new Date(solar.solarMonth,solar.solarDay))) {
            return SOLAR_HOLIDAY.get(new Date(solar.solarMonth,solar.solarDay));
        }
        Lunar lunar = solar.toLunar();
        if(!lunar.isLeap && LUNAR_HOLIDAY.containsKey(new Date(lunar.lunarMonth,lunar.lunarDay))) {
            return LUNAR_HOLIDAY.get(new Date(lunar.lunarMonth,lunar.lunarDay));
        }
        return null;
    }

    /**
     * 判断是否是母亲节
     * @param solar
     * @return
     */
    private boolean isMonthDay(Solar solar) {
        if (solar.solarMonth != 5) {
            return false;
        }
        if(SolarUtils.getWeekDay(solar) != 0) {
            return false;
        }
        int start = SolarUtils.getWeekDay(new Solar(solar.solarYear,solar.solarMonth,1));
        start = start == 0 ? 7 : start;
        return (solar.solarDay + start) / 7 == 2;
    }

    /**
     * 判断是否是父亲节
     * @param solar
     * @return
     */
    private boolean isFatherDay(Solar solar) {
        if (solar.solarMonth != 6) {
            return false;
        }
        if(SolarUtils.getWeekDay(solar) != 0) {
            return false;
        }
        int start = SolarUtils.getWeekDay(new Solar(solar.solarYear,solar.solarMonth,1));
        start = start == 0 ? 7 : start;
        return (solar.solarDay + start) / 7 == 3;
    }

    /**
     * 判断是否是感恩节
     * @param solar
     * @return
     */
    private boolean isThanksGivingDay(Solar solar) {
        if (solar.solarMonth != 11) {
            return false;
        }
        if(SolarUtils.getWeekDay(solar) != 4) {
            return false;
        }
        int start = SolarUtils.getWeekDay(new Solar(solar.solarYear,solar.solarMonth,1));
        return (solar.solarDay + start) / 7 == 4;
    }

    /**
     * 判断是否是除夕
     * @param solar
     * @return
     */
    private boolean isChineseNYEve(Solar solar) {
        Lunar lunar = solar.next().toLunar();
        return lunar.lunarMonth == 1 && lunar.lunarDay == 1 && !lunar.isLeap;
    }

    /**
     * 判断是否是清明节
     * @param solar
     * @return
     */
    private boolean isTombSweepingDay(Solar solar) {
        if (solar.solarYear <= 1999) {
            int compare = (int) (((solar.solarYear - 1900) * 0.2422 + 5.59) - ((solar.solarYear - 1900) / 4));
            if (compare == solar.solarDay) {
                return true;
            }
        } else {
            int compare = (int) (((solar.solarYear - 2000) * 0.2422 + 4.81) - ((solar.solarYear - 2000) / 4));
            if (compare == solar.solarDay) {
                return true;
            }
        }
        return false;
    }

    /**
     * 这个类是方便快速对节假日查询的一个类
     * 重写了{@link Date#hashCode()}和{@link Date#equals(Object)}方法
     * 目的是方便在HashMap中查找相应的日期是否存有节假日信息
     */
    private static class Date {
        int mMonth;
        int mDay;

        public Date(int month, int day) {
            mMonth = month;
            mDay = day;
        }

        @Override
        public int hashCode() {
            return (mMonth << 5) | (mDay);
        }

        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof Date)) {
                return false;
            }
            return obj.hashCode() == hashCode();
        }
    }

    /**
     * 初始化阳历节假日信息
     */
    private static void initSolarHolidays() {
        SOLAR_HOLIDAY.put(new Date(1,1),"元旦节");
        SOLAR_HOLIDAY.put(new Date(2,14),"情人节");
        SOLAR_HOLIDAY.put(new Date(3,8),"妇女节");
        SOLAR_HOLIDAY.put(new Date(3,12),"植树节");
        SOLAR_HOLIDAY.put(new Date(3,15),"消费者日");
        SOLAR_HOLIDAY.put(new Date(4,1),"愚人节");
        SOLAR_HOLIDAY.put(new Date(4,7),"卫生日");
        SOLAR_HOLIDAY.put(new Date(4,22),"地球日");
        SOLAR_HOLIDAY.put(new Date(5,1),"劳动节");
        SOLAR_HOLIDAY.put(new Date(5,4),"青年节");
        SOLAR_HOLIDAY.put(new Date(5,12),"护士节");
        SOLAR_HOLIDAY.put(new Date(6,1),"儿童节");
        SOLAR_HOLIDAY.put(new Date(8,1),"建军节");
        SOLAR_HOLIDAY.put(new Date(9,10),"教师节");
        SOLAR_HOLIDAY.put(new Date(10,1),"国庆节");
        SOLAR_HOLIDAY.put(new Date(10,31),"万圣节");
        SOLAR_HOLIDAY.put(new Date(12,24),"平安夜");
        SOLAR_HOLIDAY.put(new Date(12,25),"圣诞节");
    }

    /**
     * 初始化农历节假日信息
     */
    private static void initLunarHoliday() {
        LUNAR_HOLIDAY.put(new Date(1,1),"春节");
        LUNAR_HOLIDAY.put(new Date(1,15),"元宵节");
        LUNAR_HOLIDAY.put(new Date(2,2),"龙抬头");
        LUNAR_HOLIDAY.put(new Date(5,5),"端午节");
        LUNAR_HOLIDAY.put(new Date(7,7),"七夕");
        LUNAR_HOLIDAY.put(new Date(7,15),"中元节");
        LUNAR_HOLIDAY.put(new Date(8,15),"中秋节");
        LUNAR_HOLIDAY.put(new Date(9,9),"重阳节");
        LUNAR_HOLIDAY.put(new Date(12,23),"北方小年");
        LUNAR_HOLIDAY.put(new Date(12,24),"南方小年");
    }

    /**
     * 初始化法定节假日信息
     */
    private static void initStatutoryHoliday() {
        STATUTORY_HOLIDAY.add(new Solar(2018,12,30));
        STATUTORY_HOLIDAY.add(new Solar(2018,12,31));
        STATUTORY_HOLIDAY.add(new Solar(2019,1,1));
        STATUTORY_HOLIDAY.add(new Solar(2019,2,4));
        STATUTORY_HOLIDAY.add(new Solar(2019,2,5));
        STATUTORY_HOLIDAY.add(new Solar(2019,2,6));
        STATUTORY_HOLIDAY.add(new Solar(2019,2,7));
        STATUTORY_HOLIDAY.add(new Solar(2019,2,8));
        STATUTORY_HOLIDAY.add(new Solar(2019,2,9));
        STATUTORY_HOLIDAY.add(new Solar(2019,2,10));
        STATUTORY_HOLIDAY.add(new Solar(2019,4,5));
        STATUTORY_HOLIDAY.add(new Solar(2019,4,6));
        STATUTORY_HOLIDAY.add(new Solar(2019,4,7));
        STATUTORY_HOLIDAY.add(new Solar(2019,5,1));
        STATUTORY_HOLIDAY.add(new Solar(2019,6,7));
        STATUTORY_HOLIDAY.add(new Solar(2019,6,8));
        STATUTORY_HOLIDAY.add(new Solar(2019,6,9));
        STATUTORY_HOLIDAY.add(new Solar(2019,9,13));
        STATUTORY_HOLIDAY.add(new Solar(2019,9,14));
        STATUTORY_HOLIDAY.add(new Solar(2019,9,15));
        STATUTORY_HOLIDAY.add(new Solar(2019,10,1));
        STATUTORY_HOLIDAY.add(new Solar(2019,10,2));
        STATUTORY_HOLIDAY.add(new Solar(2019,10,3));
        STATUTORY_HOLIDAY.add(new Solar(2019,10,4));
        STATUTORY_HOLIDAY.add(new Solar(2019,10,5));
        STATUTORY_HOLIDAY.add(new Solar(2019,10,6));
        STATUTORY_HOLIDAY.add(new Solar(2019,10,7));
    }

    /**
     * 初始化法定节假日补班信息
     */
    private static void initMakeUpDay() {
        MAKE_UP_DAY.add(new Solar(2018,12,29));
        MAKE_UP_DAY.add(new Solar(2019,2,2));
        MAKE_UP_DAY.add(new Solar(2019,2,3));
        MAKE_UP_DAY.add(new Solar(2019,9,29));
        MAKE_UP_DAY.add(new Solar(2019,10,12));
    }

}

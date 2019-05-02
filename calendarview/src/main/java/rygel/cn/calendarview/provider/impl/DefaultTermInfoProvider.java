package rygel.cn.calendarview.provider.impl;

import android.util.SparseArray;
import rygel.cn.calendar.bean.Solar;
import rygel.cn.calendar.utils.SolarUtils;
import rygel.cn.calendarview.provider.TermInfoProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * 提供24节气计算方法
 * @author Rygel
 */
public class DefaultTermInfoProvider implements TermInfoProvider {

    private static final List<SpecialTermInfo> SPECIAL_TERM_INFOS = new ArrayList<>();

    public static final String[] TERMS = {

            "小寒", "大寒", "立春", "雨水",
            "惊蛰", "春分", "清明", "谷雨",
            "立夏", "小满", "芒种", "夏至",
            "小暑", "大暑", "立秋", "处暑",
            "白露", "秋分", "寒露", "霜降",
            "立冬", "小雪", "大雪", "冬至"

    };

    static {
        // 初始化节气计算特殊年份需要注意的信息
        initSpecialTermInfo();
    }

    /**
     * 用于缓存之前的计算结果
     */
    private SparseArray<List<Term>> mTermsTemp = new SparseArray<>();

    @Override
    public boolean isTerm(Solar solar) {
        return getTermString(solar) != null;
    }

    @Override
    public String getTermString(Solar solar) {
        List<Term> temp = mTermsTemp.get(solar.solarYear);
        if(temp == null) {
            temp = getTermsOf(solar.solarYear);
            if(temp == null) {
                return null;
            }
            mTermsTemp.put(solar.solarYear,temp);
        }
        for(Term term : temp) {
            if(term.mDay == solar.solarDay && term.mIndex / 2 + 1 == solar.solarMonth) {
                return TERMS[term.mIndex];
            }
        }
        return null;
    }

    /**
     * 节气计算用到的常量
     */
    private static final double[][] TERM_CONSTANTS = new double[][]{
            {
                    6.11, 20.84,4.6295, 19.4599,
                    6.3826, 21.4155, 5.59, 20.888,
                    6.318, 21.86, 6.5, 22.2,
                    7.928, 23.65, 8.35, 23.95,
                    8.44, 23.822, 9.098, 24.218,
                    8.218, 23.08, 7.9, 22.6
            },
            {
                    5.4055, 20.12,3.87, 18.73,
                    5.63, 20.646, 4.81, 20.1,
                    5.52, 21.04, 5.678, 21.37,
                    7.108, 22.83, 7.5, 23.13,
                    7.646, 23.042, 8.318, 23.438,
                    7.438, 22.36, 7.18, 21.94
            }
    };

    public static List<Term> getTermsOf(int year) {
        if (year < 1901 || year >= 2100) {
            return null;
        } else {
            int centuryIndex = year >= 2000 ? 1 : 0;
            int temp = year % 100;
            List<Term> terms = new ArrayList<>();
            for(int i = 0; i < 24; i++) {
                int specialTermIndex = SPECIAL_TERM_INFOS.indexOf(new SpecialTermInfo(year,i,0));
                int offset = 0;
                if(specialTermIndex >= 0) {
                    offset = SPECIAL_TERM_INFOS.get(specialTermIndex).mOffset;
                }
                // 本身是闰年的闰年年份计算情况特殊一些
                if(SolarUtils.isLeapYear(year) && i <= 3) {
                    // y * d + c - y / 4 + offset
                    terms.add(new Term((int) ((temp - 1) * 0.2422F + TERM_CONSTANTS[centuryIndex][i] - (temp - 1) / 4) + offset,i));
                } else {
                    // y * d + c - y / 4 + offset
                    terms.add(new Term((int) (temp * 0.2422F + TERM_CONSTANTS[centuryIndex][i] - temp / 4) + offset,i));
                }
            }
            return terms;
        }
    }

    /**
     * 初始化节气计算特殊年份需要注意的信息
     */
    private static void initSpecialTermInfo () {
        SPECIAL_TERM_INFOS.add(new SpecialTermInfo(1982, 0, 1));
        SPECIAL_TERM_INFOS.add(new SpecialTermInfo(2019, 0, -1));
        SPECIAL_TERM_INFOS.add(new SpecialTermInfo(2082, 1, 1));
        SPECIAL_TERM_INFOS.add(new SpecialTermInfo(2084, 5, 1));
        SPECIAL_TERM_INFOS.add(new SpecialTermInfo(1911, 8, 1));
        SPECIAL_TERM_INFOS.add(new SpecialTermInfo(2008, 9, 1));
        SPECIAL_TERM_INFOS.add(new SpecialTermInfo(1902, 10, 1));
        SPECIAL_TERM_INFOS.add(new SpecialTermInfo(1928, 11, 1));
        SPECIAL_TERM_INFOS.add(new SpecialTermInfo(1925, 12, 1));
        SPECIAL_TERM_INFOS.add(new SpecialTermInfo(2016, 12, 1));
        SPECIAL_TERM_INFOS.add(new SpecialTermInfo(1922, 13, 1));
        SPECIAL_TERM_INFOS.add(new SpecialTermInfo(2002, 14, 1));
        SPECIAL_TERM_INFOS.add(new SpecialTermInfo(1927, 16, 1));
        SPECIAL_TERM_INFOS.add(new SpecialTermInfo(1942, 17, 1));
        SPECIAL_TERM_INFOS.add(new SpecialTermInfo(2089, 19, 1));
        SPECIAL_TERM_INFOS.add(new SpecialTermInfo(2089, 20, 1));
        SPECIAL_TERM_INFOS.add(new SpecialTermInfo(1978, 21, 1));
        SPECIAL_TERM_INFOS.add(new SpecialTermInfo(1954, 22, 1));
        SPECIAL_TERM_INFOS.add(new SpecialTermInfo(1918, 23, -1));
        SPECIAL_TERM_INFOS.add(new SpecialTermInfo(2021, 23, -1));
    }

    /**
     * 保存计算的到的节气信息
     */
    private static class Term {
        int mDay;
        int mIndex;

        public Term(int day, int index) {
            mDay = day;
            mIndex = index;
        }
    }

    /**
     * 用于保存节气计算的特殊情况
     */
    private static class SpecialTermInfo {
        int mYear;
        int mTermIndex;
        int mOffset;

        public SpecialTermInfo(int year, int termIndex, int offset) {
            mYear = year;
            mTermIndex = termIndex;
            mOffset = offset;
        }

        @Override
        public int hashCode() {
            return mYear << 1 | mOffset;
        }

        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof SpecialTermInfo)) {
                return false;
            }
            return obj.hashCode() == hashCode();
        }
    }

}

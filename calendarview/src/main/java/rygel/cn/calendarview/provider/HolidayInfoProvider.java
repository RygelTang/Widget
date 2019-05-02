package rygel.cn.calendarview.provider;

import rygel.cn.calendar.bean.Solar;

/**
 * 节假日信息提供需要实现的接口
 * @author Rygel
 */
public interface HolidayInfoProvider {

    /**
     * 判断是否是节日
     * @param solar
     * @return
     */
    boolean isNormalHoliday (Solar solar);

    /**
     * 判断是不是法定节假日
     * @param solar
     * @return
     */
    boolean isStatutoryHoliday (Solar solar);

    /**
     * 判断是否是法定节假日补班日
     * @param solar
     * @return
     */
    boolean isMakeUpDay (Solar solar);

    /**
     * 获取节日名称
     * @param solar
     * @return
     */
    String getHolidayString (Solar solar);

}

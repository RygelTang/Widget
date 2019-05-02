package rygel.cn.calendarview.provider;

import rygel.cn.calendar.bean.Solar;

/**
 * 节气信息提供者
 * @author Rygel
 */
public interface TermInfoProvider {

    /**
     * 判断该日期是否是24节气之一
     * @param solar
     * @return
     */
    boolean isTerm (Solar solar);

    /**
     * 获取节气的字符串
     * @param solar
     * @return
     */
    String getTermString (Solar solar);

}

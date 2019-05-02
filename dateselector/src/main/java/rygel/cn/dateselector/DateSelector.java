package rygel.cn.dateselector;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class DateSelector extends LinearLayout {

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
    }

}

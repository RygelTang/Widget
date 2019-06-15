package rygel.cn.dateselector;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import com.cncoderx.wheelview.OnWheelChangedListener;
import com.cncoderx.wheelview.Wheel3DView;
import com.cncoderx.wheelview.WheelView;

public class TimeSelector extends LinearLayout {

    private static final String TAG = "TimeSelector";

    Wheel3DView mHourSelector;

    Wheel3DView mMinuteSelector;

    private int mHour = 0;
    private int mMinute = 0;

    private OnTimeSelectListener mTimeSelectListener = null;

    public TimeSelector(Context context) {
        super(context);
    }

    public TimeSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(getContext(),R.layout.widget_time_selector,this);
        initPickers();
        findViewById(R.id.mBtnSelect).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onTimeSelected();
            }
        });
    }

    private void initPickers(){
        mHourSelector = findViewById(R.id.mHourSelector);
        mMinuteSelector = findViewById(R.id.mMinuteSelector);
        mHourSelector.setOnWheelChangedListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView view, int oldIndex, int newIndex) {
                mHour = newIndex;
                Log.i(TAG, "on hour select : " + newIndex);
            }
        });
        mMinuteSelector.setOnWheelChangedListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView view, int oldIndex, int newIndex) {
                mMinute = newIndex;
                Log.i(TAG, "on minute select : " + newIndex);
            }
        });
    }

    protected void onTimeSelected(){
        if(mTimeSelectListener != null) {
            mTimeSelectListener.onTimeSelect(mHour,mMinute);
            return;
        }
        Log.i(TAG, "onTimeSelectListener is null,please check has called setTimeSelectListener");
    }

    public void setSelectTime(int hour, int minute){
        mHourSelector.setCurrentIndex(hour);
        mMinuteSelector.setCurrentIndex(minute);
    }

    public void setTimeSelectListener(OnTimeSelectListener timeSelectListener) {
        mTimeSelectListener = timeSelectListener;
    }

    public static interface OnTimeSelectListener {
        /**
         * 当选中时间时回调
         * @param hour
         * @param minute
         */
        void onTimeSelect(int hour,int minute);
    }

}

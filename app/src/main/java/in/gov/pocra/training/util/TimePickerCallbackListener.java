package in.gov.pocra.training.util;

import android.widget.TextView;

public interface TimePickerCallbackListener {
    void onTimeSelected(TextView textView, int hour, int min, String amOrPm);
}

package nl.tue.demothermostat;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by s146448 on 24/06/2015.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        // Todo: Set current entered as default
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        String amPm = "AM";
        String minuteStr = String.valueOf(minute);

        if(Day.isDay){
            Day.daySwitchTime = String.valueOf(hourOfDay)+":"+String.valueOf(minute);
        } else {
            Day.nightSwitchTime = String.valueOf(hourOfDay)+":"+String.valueOf(minute);
        }

        if(hourOfDay >= 13){
            hourOfDay -= 12;
            amPm = "PM";
        } else if(hourOfDay == 12){
            amPm = "PM";
        } else if(hourOfDay == 0){
            hourOfDay = 12;
        }
        if(minute == 0){
            minuteStr = "00";
        }
        if(Day.isDay){
            Day.dayTimeText.setText(hourOfDay+":"+minuteStr+" "+amPm);
        } else {
            Day.nightTimeText.setText(hourOfDay+":"+minuteStr+" "+amPm);
        }
    }
}

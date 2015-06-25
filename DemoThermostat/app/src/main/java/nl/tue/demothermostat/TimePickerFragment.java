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
        int hour;
        int minute;
        if(Day.isDay){
            hour = Day.input[0];
            minute = Day.input[1];
        } else {
            hour = Day.input[2];
            minute = Day.input[3];
        }

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        if(Day.isDay){
            Day.input[0] = hourOfDay;
            Day.input[1] = minute;
        } else {
            Day.input[2] = hourOfDay;
            Day.input[3] = minute;
        }

        Day.displayInput(hourOfDay, minute);
    }
}

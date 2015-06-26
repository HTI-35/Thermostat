package nl.tue.demothermostat;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;

import org.thermostatapp.util.CorruptWeekProgramException;
import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.Switch;
import org.thermostatapp.util.WeekProgram;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;

/**
 * Created by Anne on 05/06/2015.
 */
public class Day extends Activity {

    // Declare arraylists with switches for each day
    public ArrayList<Switch> mondaySwitches;
    public ArrayList<Switch> tuesdaySwitches;
    public ArrayList<Switch> wednesdaySwitches;
    public ArrayList<Switch> thursdaySwitches;
    public ArrayList<Switch> fridaySwitches;
    public ArrayList<Switch> saturdaySwitches;
    public ArrayList<Switch> sundaySwitches;
    // Declare week program
    WeekProgram wkProgram;
    // Declare layout
    Button bMondayAdd;
    Button bMondayRemoveAll;
    Button bMondayChange;
    TextView mondaySwitch1, mondaySwitch2, mondaySwitch3, mondaySwitch4, mondaySwitch5;
    ImageButton[] bMondayRemoveSwitches = new ImageButton[5]; // remove buttons
    TextView mondayDayTempText, mondayNightTempText, mondayDayTemp, mondayNightTemp;
    EditText mondayDaySwitchHrs, mondayDaySwitchMins, mondayNightSwitchHrs, mondayNightSwitchMins;
    TextView mondayTitle;
    static TextView dayTimeText;
    static TextView nightTimeText;
    // Declare day and night temperature
    double dayTemp;
    double nightTemp;
    // Declare day and daynumber to be initialized in subclasses
    String day;
    int dayNumber;
    // Some local variables that didn't want to be local without being final (inner class)
    int j;
    int remove1;
    int remove2;
    // Switch vars
    static String daySwitchTime;
    static String nightSwitchTime;
    static Boolean isDay;
    static String[] times; // 0: day hour, 1: day minute, 2: night hour, 3: night minute
    static int[] input; // 0: day hour, 1: day minute, 2: night hour, 3: night minute

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switch (day) {
            case "Monday":
                setContentView(R.layout.activity_monday);
                break;
            case "Tuesday":
                setContentView(R.layout.activity_tuesday);
                break;
            case "Wednesday":
                setContentView(R.layout.activity_wednesday);
                break;
            case "Thursday":
                setContentView(R.layout.activity_thursday);
                break;
            case "Friday":
                setContentView(R.layout.activity_friday);
                break;
            case "Saturday":
                setContentView(R.layout.activity_saturday);
                break;
            case "Sunday":
                setContentView(R.layout.activity_sunday);
                break;
        }

        dayTimeText = (TextView)findViewById(R.id.mondayDayTimeText);
        nightTimeText = (TextView)findViewById(R.id.mondayNightTimeText);

        times = new String[4];

        input = new int[]{0, 0, 0, 0};
        isDay = true;
        displayInput(input[0], input[1]);
        isDay = false;
        displayInput(input[2], input[3]);

        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/35";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/35/weekprogram";

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dayTemp = Double.parseDouble(HeatingSystem.get("dayTemperature"));
                    nightTemp = Double.parseDouble(HeatingSystem.get("nightTemperature"));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mondayDayTemp.setText("Day temperature: " + dayTemp + " \u2103");
                            mondayNightTemp.setText("Night temperature: " + nightTemp +  " \u2103");
                        }
                    });
                    wkProgram = HeatingSystem.getWeekProgram();
                    mondaySwitches = wkProgram.getDay("Monday");
                    tuesdaySwitches = wkProgram.getDay("Tuesday");
                    wednesdaySwitches = wkProgram.getDay("Wednesday");
                    thursdaySwitches = wkProgram.getDay("Thursday");
                    fridaySwitches = wkProgram.getDay("Friday");
                    saturdaySwitches = wkProgram.getDay("Saturday");
                    sundaySwitches = wkProgram.getDay("Sunday");
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();

    }

    /* Reload day & night temperature when the day activity is revisited */
    public void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dayTemp = Double.parseDouble(HeatingSystem.get("dayTemperature"));
                    nightTemp = Double.parseDouble(HeatingSystem.get("nightTemperature"));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                mondayDayTemp.setText("Day temperature: " + dayTemp + " \u2103");
                                mondayNightTemp.setText("Night temperature: " + nightTemp + " \u2103");
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");

        if(v.getId() == R.id.bDaySwitch){
            isDay = true;
        } else {
            isDay = false;
        }
    }

    static void displayInput(int hourOfDay, int minute){
        String amPm = "AM";
        String minuteStr = String.valueOf(minute);

        if(minute == 0){
            minuteStr = "00";
        }

        if(isDay){
            times[0] = String.valueOf(hourOfDay);
            times[1] = minuteStr;
        } else {
            times[2] = String.valueOf(hourOfDay);
            times[3] = minuteStr;
        }

        if(hourOfDay >= 13){
            hourOfDay -= 12;
            amPm = "PM";
        } else if(hourOfDay == 12){
            amPm = "PM";
        } else if(hourOfDay == 0){
            hourOfDay = 12;
        }

        if(isDay){
            dayTimeText.setText(hourOfDay+":"+minuteStr+" "+amPm);
        } else {
            nightTimeText.setText(hourOfDay+":"+minuteStr+" "+amPm);
        }
    }
}


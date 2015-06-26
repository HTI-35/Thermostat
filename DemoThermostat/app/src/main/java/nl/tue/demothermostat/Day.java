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

        if(v.getId() == R.id.bDaySwitch || v.getId() == R.id.mondayDayTimeText){
            isDay = true;
        } else {
            isDay = false;
        }
    }

    static void displayInput(int hourOfDay, int minute){
        String[] hourMinuteAmPm = int24HrsTo12HrsStr(hourOfDay, minute, false);

        if(isDay){
            times[0] = hourMinuteAmPm[0];
            times[1] = hourMinuteAmPm[1];
        } else {
            times[2] = hourMinuteAmPm[0];
            times[3] = hourMinuteAmPm[1];
        }

        hourMinuteAmPm = int24HrsTo12HrsStr(hourOfDay, minute, true);

        if(isDay){
            dayTimeText.setText(hourMinuteAmPm[0]+":"+hourMinuteAmPm[1]+" "+hourMinuteAmPm[2]);
        } else {
            nightTimeText.setText(hourMinuteAmPm[0]+":"+hourMinuteAmPm[1]+" "+hourMinuteAmPm[2]);
        }

        /*} else {
            if ((hourOfDay < 10) && ((Integer.parseInt(minuteStr) < 10))) {
                dayTimeText.setText("0"+hourOfDay+":"+"0"+minuteStr+" "+amPm);
            } else if ((hourOfDay < 10) && !((Integer.parseInt(minuteStr) < 10))) {
                dayTimeText.setText("0"+hourOfDay+":"+minuteStr+" "+amPm);
            } else if (!(hourOfDay < 10) && ((Integer.parseInt(minuteStr) < 10))) {
                dayTimeText.setText(hourOfDay+":"+"0"+minuteStr+" "+amPm);
            } else {
                dayTimeText.setText(hourOfDay+":"+minuteStr+" "+amPm);
            }

        }*/
    }

    void displaySwitches(){
        if (mondaySwitches.get(0).getState()) {
            String hourMinuteDay = mondaySwitches.get(0).getTime();
            int[] intHourMinuteDay = Hrs24StrToInt(hourMinuteDay);
            String[] strArrHourMinuteDay = int24HrsTo12HrsStr(intHourMinuteDay[0], intHourMinuteDay[1], true);
            String hourMinuteNight = mondaySwitches.get(1).getTime();
            int[] intHourMinuteNight = Hrs24StrToInt(hourMinuteNight);
            String[] strArrHourMinuteNight = int24HrsTo12HrsStr(intHourMinuteNight[0], intHourMinuteNight[1], true);
            mondaySwitch1.setText("1) " + mondaySwitches.get(0).getType() + ": " + strArrHourMinuteDay[0]+":"+strArrHourMinuteDay[1]+" "+strArrHourMinuteDay[2] + ", " + mondaySwitches.get(1).getType() + ": "
                    + strArrHourMinuteNight[0]+":"+strArrHourMinuteNight[1]+" "+strArrHourMinuteNight[2] + " ");
            bMondayRemoveSwitches[0].setVisibility(View.VISIBLE); // makes button appear
        } else {
            mondaySwitch1.setText("");
            bMondayRemoveSwitches[0].setVisibility(View.INVISIBLE); // makes button appear
        }
        if (mondaySwitches.get(2).getState()) {
            String hourMinuteDay = mondaySwitches.get(2).getTime();
            int[] intHourMinuteDay = Hrs24StrToInt(hourMinuteDay);
            String[] strArrHourMinuteDay = int24HrsTo12HrsStr(intHourMinuteDay[0], intHourMinuteDay[1], true);
            String hourMinuteNight = mondaySwitches.get(3).getTime();
            int[] intHourMinuteNight = Hrs24StrToInt(hourMinuteNight);
            String[] strArrHourMinuteNight = int24HrsTo12HrsStr(intHourMinuteNight[0], intHourMinuteNight[1], true);
            mondaySwitch2.setText("2) " + mondaySwitches.get(2).getType() + ": " + strArrHourMinuteDay[0]+":"+strArrHourMinuteDay[1]+" "+strArrHourMinuteDay[2] + ", " + mondaySwitches.get(3).getType() + ": "
                    + strArrHourMinuteNight[0]+":"+strArrHourMinuteNight[1]+" "+strArrHourMinuteNight[2] + " ");
            bMondayRemoveSwitches[1].setVisibility(View.VISIBLE); // makes button appear
        } else {
            mondaySwitch2.setText("");
            bMondayRemoveSwitches[1].setVisibility(View.INVISIBLE); // makes button appear
        }
        if (mondaySwitches.get(4).getState()) {
            String hourMinuteDay = mondaySwitches.get(4).getTime();
            int[] intHourMinuteDay = Hrs24StrToInt(hourMinuteDay);
            String[] strArrHourMinuteDay = int24HrsTo12HrsStr(intHourMinuteDay[0], intHourMinuteDay[1], true);
            String hourMinuteNight = mondaySwitches.get(5).getTime();
            int[] intHourMinuteNight = Hrs24StrToInt(hourMinuteNight);
            String[] strArrHourMinuteNight = int24HrsTo12HrsStr(intHourMinuteNight[0], intHourMinuteNight[1], true);
            mondaySwitch3.setText("3) " + mondaySwitches.get(4).getType() + ": " + strArrHourMinuteDay[0]+":"+strArrHourMinuteDay[1]+" "+strArrHourMinuteDay[2] + ", " + mondaySwitches.get(5).getType() + ": "
                    + strArrHourMinuteNight[0]+":"+strArrHourMinuteNight[1]+" "+strArrHourMinuteNight[2] + " ");
            bMondayRemoveSwitches[2].setVisibility(View.VISIBLE); // makes button appear
        } else {
            mondaySwitch3.setText("");
            bMondayRemoveSwitches[2].setVisibility(View.INVISIBLE); // makes button appear
        }
        if (mondaySwitches.get(6).getState()) {
            String hourMinuteDay = mondaySwitches.get(6).getTime();
            int[] intHourMinuteDay = Hrs24StrToInt(hourMinuteDay);
            String[] strArrHourMinuteDay = int24HrsTo12HrsStr(intHourMinuteDay[0], intHourMinuteDay[1], true);
            String hourMinuteNight = mondaySwitches.get(7).getTime();
            int[] intHourMinuteNight = Hrs24StrToInt(hourMinuteNight);
            String[] strArrHourMinuteNight = int24HrsTo12HrsStr(intHourMinuteNight[0], intHourMinuteNight[1], true);
            mondaySwitch4.setText("4) " + mondaySwitches.get(6).getType() + ": " + strArrHourMinuteDay[0]+":"+strArrHourMinuteDay[1]+" "+strArrHourMinuteDay[2] + ", " + mondaySwitches.get(7).getType() + ": "
                    + strArrHourMinuteNight[0]+":"+strArrHourMinuteNight[1]+" "+strArrHourMinuteNight[2] + " ");
            bMondayRemoveSwitches[3].setVisibility(View.VISIBLE); // makes button appear
        } else {
            mondaySwitch4.setText("");
            bMondayRemoveSwitches[3].setVisibility(View.INVISIBLE); // makes button appear
        }
        if (mondaySwitches.get(8).getState()) {
            String hourMinuteDay = mondaySwitches.get(8).getTime();
            int[] intHourMinuteDay = Hrs24StrToInt(hourMinuteDay);
            String[] strArrHourMinuteDay = int24HrsTo12HrsStr(intHourMinuteDay[0], intHourMinuteDay[1], true);
            String hourMinuteNight = mondaySwitches.get(9).getTime();
            int[] intHourMinuteNight = Hrs24StrToInt(hourMinuteNight);
            String[] strArrHourMinuteNight = int24HrsTo12HrsStr(intHourMinuteNight[0], intHourMinuteNight[1], true);
            mondaySwitch5.setText("5) " + mondaySwitches.get(8).getType() + ": " + strArrHourMinuteDay[0]+":"+strArrHourMinuteDay[1]+" "+strArrHourMinuteDay[2] + ", " + mondaySwitches.get(9).getType() + ": "
                    + strArrHourMinuteNight[0]+":"+strArrHourMinuteNight[1]+" "+strArrHourMinuteNight[2] + " ");
            bMondayRemoveSwitches[4].setVisibility(View.VISIBLE); // makes button appear
        } else {
            mondaySwitch5.setText("");
            bMondayRemoveSwitches[4].setVisibility(View.INVISIBLE); // makes button appear
        }
    }

    static String[] int24HrsTo12HrsStr(int hour, int minute, boolean min12) {
        String amPm = "AM";
        String minuteStr = String.valueOf(minute);

        if(minute == 0){
            minuteStr = "00";
        } else if(minute < 10){
            minuteStr = "0"+String.valueOf(minute);
        }

        if(min12){
            if(hour >= 13){
                hour -= 12;
                amPm = "PM";
            } else if(hour == 12){
                amPm = "PM";
            } else if(hour == 0){
                hour = 12;
            }
        }

        String hourStr = String.valueOf(hour);

        if(hour < 10){
            hourStr = "0"+String.valueOf(hour);
        }

        String[] hourMinuteAmPm = new String[]{hourStr, minuteStr, amPm};
        return hourMinuteAmPm;
    }

    int[] Hrs24StrToInt(String hourMinute) {
        String hour = hourMinute.substring(0,2);
        String minute = hourMinute.substring(3,5);

        int intHour = Integer.parseInt(hour);
        int intMinute = Integer.parseInt(minute);

        int[] hourMin = new int[]{intHour, intMinute};
        return hourMin;
    }
}


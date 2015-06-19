package nl.tue.demothermostat;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.thermostatapp.util.CorruptWeekProgramException;
import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.Switch;
import org.thermostatapp.util.WeekProgram;

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

        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/35";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/35/weekprogram";

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dayTemp = Double.parseDouble(HeatingSystem.get("dayTemperature"));
                    nightTemp = Double.parseDouble(HeatingSystem.get("nightTemperature"));
                    mondayDayTemp.setText("Day Temperature: " + dayTemp + " \u2103");
                    mondayNightTemp.setText("Night Temperature: " + nightTemp +  " \u2103");
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
                            if(day == "Monday") {
                                mondayDayTemp.setText("Day Temperature: " + dayTemp + " \u2103");
                                mondayNightTemp.setText("Night Temperature: " + nightTemp + " \u2103");
                            }
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }

}


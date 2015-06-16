package nl.tue.demothermostat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.Switch;
import org.thermostatapp.util.WeekProgram;

import java.util.ArrayList;

/**
 * Created by Anne on 05/06/2015.
 */
public class Day extends Activity {

    //declare arraylists with switches for each day
    public ArrayList<Switch> mondaySwitches;
    public ArrayList<Switch> tuesdaySwitches;
    public ArrayList<Switch> wednesdaySwitches;
    public ArrayList<Switch> thursdaySwitches;
    public ArrayList<Switch> fridaySwitches;
    public ArrayList<Switch> saturdaySwitches;
    public ArrayList<Switch> sundaySwitches;
    //declare week program
    WeekProgram wkProgram;
    //declare layout
    Button bAdd;
    Button bRemoveAll;
    Button bChange;
    TextView switch0;
    TextView switch1;
    TextView switch2;
    TextView switch3;
    TextView switch4;
    TextView switch5;
    TextView dayTempText;
    TextView nightTempText;
    TextView DayTemp;
    TextView NightTemp;
    TextView title;
    EditText daySwitchHrs, daySwitchMins, nightSwitchHrs, nightSwitchMins;
    //declare day and night temperature
    double dayTemp;
    double nightTemp;
    //declare day and daynumber to be initialized in subclasses
    String day;
    int dayNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monday);
        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/35";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/35/weekprogram";

        bAdd = (Button)findViewById(R.id.bMondayAdd);
        bRemoveAll = (Button)findViewById(R.id.bMondayRemoveAll);
        bChange = (Button)findViewById(R.id.bMondayChange);
        dayTempText = (TextView)findViewById(R.id.dayTemp);
        nightTempText = (TextView)findViewById(R.id.nightTemp);
        title = (TextView)findViewById(R.id.mondayTitle);

        switch0 = (TextView)findViewById(R.id.mondaySwitch0);
        switch1 = (TextView)findViewById(R.id.mondaySwitch1);
        switch2 = (TextView)findViewById(R.id.mondaySwitch2);
        switch3 = (TextView)findViewById(R.id.mondaySwitch3);
        switch4 = (TextView)findViewById(R.id.mondaySwitch4);
        switch5 = (TextView)findViewById(R.id.mondaySwitch5);

        DayTemp = (TextView)findViewById(R.id.mondayDayTemp);
        NightTemp = (TextView)findViewById(R.id.mondayNightTemp);

        daySwitchHrs = (EditText)findViewById(R.id.mondayDayTimeHrs);
        daySwitchMins = (EditText)findViewById(R.id.mondayDayTimeMins);
        nightSwitchHrs = (EditText)findViewById(R.id.mondayNightTimeMins);
        nightSwitchMins = (EditText)findViewById(R.id.mondayDayTimeMins);

        //set input filters on switches
        int maxLength = 2;
        daySwitchHrs.setFilters(new InputFilter[]{new InputFilterMinMax("00", "24"), new InputFilter.LengthFilter(maxLength)});
        nightSwitchMins.setFilters(new InputFilter[]{new InputFilterMinMax("00", "59"), new InputFilter.LengthFilter(maxLength)});
        nightSwitchHrs.setFilters(new InputFilter[]{new InputFilterMinMax("00", "24"), new InputFilter.LengthFilter(maxLength)});
        nightSwitchMins.setFilters(new InputFilter[]{new InputFilterMinMax("00", "59"), new InputFilter.LengthFilter(maxLength)});
        //put cursor is on right side of input field when user presses 'next' (doesn't work for nightSwitchHrs for some reason)
        daySwitchHrs.setSelection(2);
        daySwitchMins.setSelection(2);
        nightSwitchHrs.setSelection(2);
        nightSwitchMins.setSelection(2);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dayTemp = Double.parseDouble(HeatingSystem.get("dayTemperature"));
                    nightTemp = Double.parseDouble(HeatingSystem.get("nightTemperature"));
                    DayTemp.setText("Day Temperature: " + dayTemp + " \u2103");
                    NightTemp.setText("Night Temperature: " + nightTemp +  " \u2103");
                    //get week program from server & initialize switches arraylists
                    wkProgram = HeatingSystem.getWeekProgram();
                    mondaySwitches = wkProgram.getDay("Monday");
                    tuesdaySwitches = wkProgram.getDay("Tuesday");
                    wednesdaySwitches = wkProgram.getDay("Wednesday");
                    thursdaySwitches = wkProgram.getDay("Thursday");
                    fridaySwitches = wkProgram.getDay("Friday");
                    saturdaySwitches = wkProgram.getDay("Saturday");
                    sundaySwitches = wkProgram.getDay("Sunday");
                    //go through switches array
                    //if the switch is turned on, then print the switch to console
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();

        //todo: somehow translate text from EditText mondayDayTime & mondayNightTime to ints
        // by removing the colons so we can use them in AddSwitch method

        bRemoveAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    for (int i = 0;i< wkProgram.get_nr_switches_active(dayNumber); i++){
                        wkProgram.RemoveFirstSwitch(day);
                    }
                    HeatingSystem.setWeekProgram(wkProgram);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch1.setText("");
                            switch2.setText("");
                            switch3.setText("");
                            switch4.setText("");
                            switch5.setText("");
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }


            }
        });

        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: add the switch from the input fields to switch list
                // using WeekProgram.AddSwitch(int start_time, int end_time, String type, String day)
                // but idk how we should have both a start and end time for a single type (night/day)
                // I think if you set the type to day, the start_time is the time of the day switch
                // and the end_time is the time of the night switch, but i'm not sure
                try {
                    int nrSwitches = wkProgram.get_nr_switches_active(dayNumber);
                    if (nrSwitches < 5) {
                        //wkProgram.addSwitch(TODO,TODO,"day","monday");
                    }
                    HeatingSystem.setWeekProgram(wkProgram);
                    //todo:get switches from server and set textfields
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }

            }
        });

        bChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), DayNight.class));
            }
        });

    }


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
                            DayTemp.setText("Day Temperature: " + dayTemp + " \u2103");
                            NightTemp.setText("Night Temperature: " + nightTemp +  " \u2103");
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }


}


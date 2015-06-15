package nl.tue.demothermostat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.thermostatapp.util.*;
/**
 * Created by Anne on 05/06/2015.
 */
public class Monday extends Day {


    WeekProgram wkProgram;
    Button bMondayAdd;
    Button bMondayRemoveAll;
    Button bMondayChange;
    TextView mondaySwitch1;
    TextView mondaySwitch2;
    TextView mondaySwitch3;
    TextView mondaySwitch4;
    TextView mondaySwitch5;
    TextView dayTempText;
    TextView nightTempText;
    TextView mondayDayTemp;
    TextView mondayNightTemp;
    EditText daySwitchHrs, daySwitchMins, nightSwitchHrs, nightSwitchMins;
    double dayTemp; //day temperature
    double nightTemp; //night temperature

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monday);
        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/35";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/35/weekprogram";

        bMondayAdd = (Button)findViewById(R.id.bMondayAdd);
        bMondayRemoveAll = (Button)findViewById(R.id.bMondayRemoveAll);
        bMondayChange = (Button)findViewById(R.id.bMondayChange);
        dayTempText = (TextView)findViewById(R.id.dayTemp);
        nightTempText = (TextView)findViewById(R.id.nightTemp);

        mondaySwitch1 = (TextView)findViewById(R.id.mondaySwitch1);
        mondaySwitch2 = (TextView)findViewById(R.id.mondaySwitch2);
        mondaySwitch3 = (TextView)findViewById(R.id.mondaySwitch3);
        mondaySwitch4 = (TextView)findViewById(R.id.mondaySwitch4);
        mondaySwitch5 = (TextView)findViewById(R.id.mondaySwitch5);

        mondayDayTemp = (TextView)findViewById(R.id.mondayDayTemp);
        mondayNightTemp = (TextView)findViewById(R.id.mondayNightTemp);

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
        //make sure cursor is on the right side of the edittext when user presses 'next'
        //doesn't work for nightSwitchHrs for some reason.
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

                    mondayDayTemp.setText("Day Temperature: " + dayTemp + " \u2103");
                    mondayNightTemp.setText("Night Temperature: " + nightTemp +  " \u2103");
                    //Showing weekprogram
                    wkProgram = HeatingSystem.getWeekProgram();
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();

        //todo: timer to get day temp & night temp continuously from server

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //todo: get the switch times from the server...
                    //mondaySwitch1.setText("day: " + dayswitch + ", night: " + nightswitch);
                    //etc?
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();

        //todo: somehow translate text from EditText mondayDayTime & mondayNightTime to ints
        // by removing the colons so we can use them in AddSwitch method

        bMondayRemoveAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    for (int i = 0;i< wkProgram.get_nr_switches_active(1); i++){
                        wkProgram.RemoveFirstSwitch("monday");
                    }
                    HeatingSystem.setWeekProgram(wkProgram);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mondaySwitch1.setText("");
                            mondaySwitch2.setText("");
                            mondaySwitch3.setText("");
                            mondaySwitch4.setText("");
                            mondaySwitch5.setText("");
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }


            }
        });

        bMondayAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: add the switch from the input fields to switch list
                // using WeekProgram.AddSwitch(int start_time, int end_time, String type, String day)
                // but idk how we should have both a start and end time for a single type (night/day)
                // I think if you set the type to day, the start_time is the time of the day switch
                // and the end_time is the time of the night switch, but i'm not sure
                try {
                    int nrSwitches = wkProgram.get_nr_switches_active(1);
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

        bMondayChange.setOnClickListener(new View.OnClickListener() {
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
                            mondayDayTemp.setText("Day Temperature: " + dayTemp + " \u2103");
                            mondayNightTemp.setText("Night Temperature: " + nightTemp +  " \u2103");
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }
}

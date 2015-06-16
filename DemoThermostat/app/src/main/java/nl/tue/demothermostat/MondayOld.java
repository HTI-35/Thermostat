package nl.tue.demothermostat;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.WeekProgram;

/**
 * Created by Anne on 05/06/2015.
 */
public class MondayOld extends Day {
/*

    WeekProgram wkProgram;
    Button bMondayAdd;
    Button bMondayRemoveAll;
    Button bMondayChange;
    TextView mondaySwitch0;
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

        mondaySwitch0 = (TextView)findViewById(R.id.mondaySwitch0);
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
                    //get week program from server
                    wkProgram = HeatingSystem.getWeekProgram();
                    //go through switches array
                    //if the switch is turned on, then print the switch to console
                    displaySwitches();
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();

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

    public void displaySwitches() {
        for (int i=0; i<Day.ownSwitches.size(); i++) {
            //print switches to console if they're in ON state
            if (Day.ownSwitches.get(i).getState()) {
                System.out.println("switch: " +
                                Day.ownSwitches.get(i).getType() + " " +
                                Day.ownSwitches.get(i).getTime()
                );
            }
        }
        /*
        day and night switches must be set at the same time (not only day or only night)
        so if index 1 is set to on, then 2 must also be set to on, so they can be displayed simultaneously
        the index 0 is the 00:00 switch to night which does not belong in such a pair
        so we put it separately. there should be 10 switches though and 1 EXTRA for 00:00 to night???
        but in the server XML there are only 10 switches for each day. how to fix this????????
        *//*
        if(Day.ownSwitches.get(0).getState())
        mondaySwitch0.setText("Standard) " + Day.ownSwitches.get(0).getType() + ": " + Day.ownSwitches.get(0).getTime());
        if(Day.ownSwitches.get(1).getState())
        mondaySwitch1.setText("1) " + Day.ownSwitches.get(1).getType() + ": " + Day.ownSwitches.get(1).getTime() + ", " + Day.ownSwitches.get(2).getType() + ": " + Day.ownSwitches.get(2).getTime());
        if(Day.ownSwitches.get(3).getState())
        mondaySwitch2.setText("2) " + Day.ownSwitches.get(3).getType() + ": " + Day.ownSwitches.get(3).getTime() + ", " + Day.ownSwitches.get(4).getType() + ": " + Day.ownSwitches.get(4).getTime());
        if(Day.ownSwitches.get(5).getState())
        mondaySwitch3.setText("3) " + Day.ownSwitches.get(5).getType() + ": " + Day.ownSwitches.get(5).getTime() + ", " + Day.ownSwitches.get(6).getType() + ": " + Day.ownSwitches.get(5).getTime());
        if(Day.ownSwitches.get(7).getState())
        mondaySwitch4.setText("4) " + Day.ownSwitches.get(7).getType() + ": " + Day.ownSwitches.get(7).getTime() + ", " + Day.ownSwitches.get(8).getType() + ": " + Day.ownSwitches.get(7).getTime());
        if(Day.ownSwitches.get(9).getState())
        mondaySwitch5.setText("5) " + Day.ownSwitches.get(9).getType() + ": " + Day.ownSwitches.get(9).getTime());
    }
    */
}

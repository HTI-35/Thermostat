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

    public static ArrayList<Switch> ownSwitches;
    WeekProgram wkProgram;
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
    double dayTemp; //day temperature
    double nightTemp; //night temperature

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

                    DayTemp.setText("Day Temperature: " + dayTemp + " \u2103");
                    NightTemp.setText("Night Temperature: " + nightTemp +  " \u2103");
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
        */
        if(Day.ownSwitches.get(0).getState())
            switch0.setText("Standard) " + Day.ownSwitches.get(0).getType() + ": " + Day.ownSwitches.get(0).getTime());
        if(Day.ownSwitches.get(1).getState())
            switch1.setText("1) " + Day.ownSwitches.get(1).getType() + ": " + Day.ownSwitches.get(1).getTime() + ", " + Day.ownSwitches.get(2).getType() + ": " + Day.ownSwitches.get(2).getTime());
        if(Day.ownSwitches.get(3).getState())
            switch2.setText("2) " + Day.ownSwitches.get(3).getType() + ": " + Day.ownSwitches.get(3).getTime() + ", " + Day.ownSwitches.get(4).getType() + ": " + Day.ownSwitches.get(4).getTime());
        if(Day.ownSwitches.get(5).getState())
            switch3.setText("3) " + Day.ownSwitches.get(5).getType() + ": " + Day.ownSwitches.get(5).getTime() + ", " + Day.ownSwitches.get(6).getType() + ": " + Day.ownSwitches.get(5).getTime());
        if(Day.ownSwitches.get(7).getState())
            switch4.setText("4) " + Day.ownSwitches.get(7).getType() + ": " + Day.ownSwitches.get(7).getTime() + ", " + Day.ownSwitches.get(8).getType() + ": " + Day.ownSwitches.get(7).getTime());
        if(Day.ownSwitches.get(9).getState())
            switch5.setText("5) " + Day.ownSwitches.get(9).getType() + ": " + Day.ownSwitches.get(9).getTime());
    }
}


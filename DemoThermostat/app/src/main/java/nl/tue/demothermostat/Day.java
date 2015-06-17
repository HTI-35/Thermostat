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

    final int nSwitches = 5; //number of switches

    public static ArrayList<Switch> ownSwitches;
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
    Button bRemove;
    Button bChange;
    TextView switch0;
    TextView switch1;
    TextView switch2;
    TextView switch3;
    TextView switch4;
    TextView switch5;
    ImageButton[] bRemoveSwitches = new ImageButton[5]; // remove buttons
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
    String day = "Monday";
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
        bRemoveSwitches[0] = (ImageButton)findViewById(R.id.bMondayRemoveSwitch1);
        bRemoveSwitches[1] = (ImageButton)findViewById(R.id.bMondayRemoveSwitch2);
        bRemoveSwitches[2] = (ImageButton)findViewById(R.id.bMondayRemoveSwitch3);
        bRemoveSwitches[3] = (ImageButton)findViewById(R.id.bMondayRemoveSwitch4);
        bRemoveSwitches[4] = (ImageButton)findViewById(R.id.bMondayRemoveSwitch5);

        DayTemp = (TextView)findViewById(R.id.mondayDayTemp);
        NightTemp = (TextView)findViewById(R.id.mondayNightTemp);

        daySwitchHrs = (EditText)findViewById(R.id.mondayDayTimeHrs);
        daySwitchMins = (EditText)findViewById(R.id.mondayDayTimeMins);
        nightSwitchHrs = (EditText)findViewById(R.id.mondayNightTimeHrs);
        nightSwitchMins = (EditText)findViewById(R.id.mondayNightTimeMins);

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
                    for (int i = 9;i>= 0; i--){
                        wkProgram.RemoveSwitch(i, day);
                        System.out.println("switch " + i + ":" + wkProgram.getDay(day).get(i).getText());
                    }
                    for (int i = 0;i<5; i++){
                        bRemoveSwitches[i].setVisibility(View.INVISIBLE);
                    }

                    //HeatingSystem.setWeekProgram(wkProgram);
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

        for(int i = 0; i < nSwitches; i++){
            final int j = i; // because of inner class nonsense
            bRemoveSwitches[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("i: " + j);
                    System.out.println("Day: " + day);

                    try {
                        //wkProgram.RemoveSwitch((2*j)-1,day); todo: correct indexes
                        //wkProgram.RemoveSwitch((2*j)-2,day); todo: correct indexes
                        bRemoveSwitches[j].setVisibility(View.INVISIBLE);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (j) {
                                    case 0:
                                        switch1.setText("");
                                        break;
                                    case 1:
                                        switch2.setText("");
                                        break;
                                    case 2:
                                        switch3.setText("");
                                        break;
                                    case 3:
                                        switch4.setText("");
                                        break;
                                    case 4:
                                        switch5.setText("");
                                    default:
                                        System.out.println("Error: No such switch");
                                        break;
                                }
                            }
                        });
                        //HeatingSystem.setWeekProgram(wkProgram);
                    } catch (Exception e) {
                        System.err.println("Error from getdatas " + e);
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            wkProgram.RemoveSwitch(j, day);
                            HeatingSystem.setWeekProgram(wkProgram);
                        }
                    }).start();
                }
            });
        }

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
//
//    void updateSwitches() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    wkProgram = HeatingSystem.getWeekProgram();
//                } catch (ConnectException e) {
//                    e.printStackTrace();
//                } catch (CorruptWeekProgramException e) {
//                    e.printStackTrace();
//                }
//                Monday.displaySwitches();
//            }
//        })
//    }
}


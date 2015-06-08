package nl.tue.demothermostat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.WeekProgram;

/**
 * Created by Anne on 05/06/2015.
 */
public class Monday extends Day {

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
    EditText mondayDayTime;
    EditText mondayNightTime;
    double dayTemp; //day temperature
    double nightTemp; //night temperature

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monday);
        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/35";

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

        mondayDayTime = (EditText)findViewById(R.id.mondayDayTime);
        mondayNightTime = (EditText)findViewById(R.id.mondayNightTime);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //todo: display day & night temperature, THIS IS NOT WORKING :((((
                    dayTemp = Double.parseDouble(HeatingSystem.get("dayTemperature"));
                    dayTempText.setText(dayTemp + " \u2103");
                    nightTemp = Double.parseDouble(HeatingSystem.get("nightTemperature"));
                    nightTempText.setText(nightTemp +  " \u2103");
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();

        //todo: timer to get day temp & night temp continuously from server
        //once i can get it to display in the first place that is...

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

        bMondayAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: add the switch from the input fields to switch list
                // using WeekProgram.AddSwitch(int start_time, int end_time, String type, String day)
                // but idk how we should have both a start and end time for a single type (night/day)
                }
        });

        bMondayChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), DayNight.class));
            }
        });

    }
}

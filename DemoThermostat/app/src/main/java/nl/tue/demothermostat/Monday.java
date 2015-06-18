package nl.tue.demothermostat;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.thermostatapp.util.*;

import java.net.ConnectException;
import java.util.ArrayList;

/**
 * Created by Anne on 05/06/2015.
 */
public class Monday extends Day {

    String daySwitchTime;
    String nightSwitchTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        day = "Monday";
        dayNumber = 1;

        bMondayAdd = (Button)findViewById(R.id.bMondayAdd);
        bMondayRemoveAll = (Button)findViewById(R.id.bMondayRemoveAll);
        bMondayChange = (Button)findViewById(R.id.bMondayChange);
        mondayDayTempText = (TextView)findViewById(R.id.dayTemp);
        mondayNightTempText = (TextView)findViewById(R.id.nightTemp);
        mondayTitle = (TextView)findViewById(R.id.mondayTitle);

        mondaySwitch1 = (TextView)findViewById(R.id.mondaySwitch1);
        mondaySwitch2 = (TextView)findViewById(R.id.mondaySwitch2);
        mondaySwitch3 = (TextView)findViewById(R.id.mondaySwitch3);
        mondaySwitch4 = (TextView)findViewById(R.id.mondaySwitch4);
        mondaySwitch5 = (TextView)findViewById(R.id.mondaySwitch5);
        bMondayRemoveSwitches[0] = (ImageButton)findViewById(R.id.bMondayRemoveSwitch1);
        bMondayRemoveSwitches[1] = (ImageButton)findViewById(R.id.bMondayRemoveSwitch2);
        bMondayRemoveSwitches[2] = (ImageButton)findViewById(R.id.bMondayRemoveSwitch3);
        bMondayRemoveSwitches[3] = (ImageButton)findViewById(R.id.bMondayRemoveSwitch4);
        bMondayRemoveSwitches[4] = (ImageButton)findViewById(R.id.bMondayRemoveSwitch5);

        mondayDayTemp = (TextView)findViewById(R.id.mondayDayTemp);
        mondayNightTemp = (TextView)findViewById(R.id.mondayNightTemp);

        mondayDaySwitchHrs = (EditText)findViewById(R.id.mondayDayTimeHrs);
        mondayDaySwitchMins = (EditText)findViewById(R.id.mondayDayTimeMins);
        mondayNightSwitchHrs = (EditText)findViewById(R.id.mondayNightTimeHrs);
        mondayNightSwitchMins = (EditText)findViewById(R.id.mondayNightTimeMins);

        //set input filters on switches
        int maxLength = 2;
        mondayDaySwitchHrs.setFilters(new InputFilter[]{new InputFilterMinMax("00", "24"), new InputFilter.LengthFilter(maxLength)});
        mondayNightSwitchMins.setFilters(new InputFilter[]{new InputFilterMinMax("00", "59"), new InputFilter.LengthFilter(maxLength)});
        mondayNightSwitchHrs.setFilters(new InputFilter[]{new InputFilterMinMax("00", "24"), new InputFilter.LengthFilter(maxLength)});
        mondayNightSwitchMins.setFilters(new InputFilter[]{new InputFilterMinMax("00", "59"), new InputFilter.LengthFilter(maxLength)});
        //put cursor is on right side of input field when user presses 'next' (doesn't work for nightSwitchHrs for some reason)
        mondayDaySwitchHrs.setSelection(2);
        mondayDaySwitchMins.setSelection(2);
        mondayNightSwitchHrs.setSelection(2);
        mondayNightSwitchMins.setSelection(2);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    wkProgram = HeatingSystem.getWeekProgram();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            displaySwitches();
                        }
                    });
                    for (int i = 0;i< 10; i++){
                        System.out.println("InitSwitch " + i + ":" + wkProgram.getDay(day).get(i).getText());
                    }
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();

        new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(500);
                        //wkProgram = HeatingSystem.getWeekProgram();
                        displaySwitches();
                        for (int i = 0;i< 10; i++){
                            System.out.println("MondayS " + i + ":" + wkProgram.getDay(day).get(i).getText());
                        }
                    }
                } catch (InterruptedException e){

                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }.start();

        /* Go to activity to change day & night temperature when Change button is clicked */
        bMondayChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), DayNight.class));
            }
        });

        /* Set on click listener for Add button */
        bMondayAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] times = new String[4];
                times[0] = mondayDaySwitchHrs.getText().toString();
                times[1] = mondayDaySwitchMins.getText().toString();
                times[2] = mondayNightSwitchHrs.getText().toString();
                times[3] = mondayNightSwitchMins.getText().toString();
                // Make sure all input fields have 2 digit format even if user inputs fewer digits
                for (int i = 0; i < times.length; i++) {
                    if (times[i].length() == 0) {
                        times[i] = "00";
                    } else if (times[i].length() == 1) {
                        times[i] = "0" + times[i];
                    }
                }
                // Concatenate strings to create a hh:mm format
                daySwitchTime = times[0] + ":" + times[1];
                nightSwitchTime = times[2] + ":" + times[3];
                //add switches
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            setSwitch(day, daySwitchTime, nightSwitchTime);
                            HeatingSystem.setWeekProgram(wkProgram);
                            wkProgram = HeatingSystem.getWeekProgram();
                            mondaySwitches = wkProgram.getDay("Monday");
                            // Display switches again (doesn't really work yet)
                            // todo: fix this so it updates the switches properly
                            displaySwitches();
                        } catch (Exception e) {
                            System.out.println("Error in getdata: " + e);
                        }
                    }
                }).start();
            }
        });

        /* Set on click listener for Remove All button */
        bMondayRemoveAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //Remove all switches from wkProgram
                    for (int i = 0; i < 5; i++){
                        wkProgram.data.get(day).set(2*i, new Switch("day", false, "23:59"));
                        wkProgram.data.get(day).set(2 * i + 1, new Switch("night", false, "23:59"));
                    }
                    //Remove all remove icons
                    for (int i = 0;i<5; i++){
                        bMondayRemoveSwitches[i].setVisibility(View.INVISIBLE);
                    }
                    mondaySwitch1.setText("");
                    mondaySwitch2.setText("");
                    mondaySwitch3.setText("");
                    mondaySwitch4.setText("");
                    mondaySwitch5.setText("");
                    //Remove text for all switches and upload week program to server
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                HeatingSystem.setWeekProgram(wkProgram);
                                //wkProgram = HeatingSystem.getWeekProgram();
                                //mondaySwitches = wkProgram.getDay("Monday");
                            } catch (Exception e) {
                                System.out.println("Error in getdata: " + e);
                            }
                        }
                    }).start();
                } catch (Exception e) {
                    System.err.println("Error from getdata 2 " + e);
                }
            }
        });

        /* Set on click listeners for all remove buttons */
        for(int i = 0; i < 5; i++){
            bMondayRemoveSwitches[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /* Check which button was pressed */
                    ImageButton clickedButton = (ImageButton) v;
                    switch (clickedButton.getId()) {
                        case R.id.bMondayRemoveSwitch1:
                            j = 0;
                            break;
                        case R.id.bMondayRemoveSwitch2:
                            j = 1;
                            break;
                        case R.id.bMondayRemoveSwitch3:
                            j = 2;
                            break;
                        case R.id.bMondayRemoveSwitch4:
                            j = 3;
                            break;
                        case R.id.bMondayRemoveSwitch5:
                            j = 4;
                            break;
                    }
                    remove1 = 2*j;
                    remove2 = (2*j)+1;
                    // Remove displayed text & icon for said switch
                    try {
                        bMondayRemoveSwitches[j].setVisibility(View.INVISIBLE);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (j) {
                                    case 0:
                                        mondaySwitch1.setText("");
                                        break;
                                    case 1:
                                        mondaySwitch2.setText("");
                                        break;
                                    case 2:
                                        mondaySwitch3.setText("");
                                        break;
                                    case 3:
                                        mondaySwitch4.setText("");
                                        break;
                                    case 4:
                                        mondaySwitch5.setText("");
                                    default:
                                        System.out.println("Error: No such switch");
                                        break;
                                }
                            }
                        });
                    } catch (Exception e) {
                        System.err.println("Error from getdata " + e);
                    }
                    // Remove switch from server and upload week program to server
                    // todo: fix this. It doesn't always remove the correct switches...
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                wkProgram.RemoveSwitch(remove1, day);
                                HeatingSystem.setWeekProgram(wkProgram);
                                wkProgram = HeatingSystem.getWeekProgram();
                                mondaySwitches = wkProgram.getDay("Monday");
                                // Display switches again (doesn't really work yet)
                                //displaySwitches();
                            } catch (Exception e) {
                                System.out.println("Error in getdata: " + e);
                            }
                        }
                    }).start();
                }
            });
        }


    }

    /* Add a day and a night switch to the array for a specified day */
    public void setSwitch(String day, String dayTime, String nightTime) {
        // If 5 switches are already enabled, tell the user no more switches can be added.
        if (wkProgram.data.get(day).get(8).getState()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast maxSwitchesAdded = Toast.makeText(getApplicationContext(), "The switch was not added: you can't add more than 5 switches.", Toast.LENGTH_SHORT);
                    maxSwitchesAdded.show();
                }
            });
        }
        for (int i=0; i<5; i++) {
            // Find an OFF pair
            if (!wkProgram.data.get(day).get(2*i).getState()) {
                // Set the OFF pair to be the new switch.
                wkProgram.data.get(day).set(2*i, new Switch("day", true, dayTime));
                wkProgram.data.get(day).set(2*i + 1, new Switch("night", true, nightTime));
                // When the last switch has been added, tell the user no more switches can be added.
                if(i==4) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast maxSwitchesAdded = Toast.makeText(getApplicationContext(), "You've now added the maximum of 5 switches.", Toast.LENGTH_SHORT);
                            maxSwitchesAdded.show();
                        }
                    });
                }
                i=5;
            }
        }
    }


    /* Display the switches on the screen */
    public void displaySwitches() {
        if (this.mondaySwitches.get(0).getState()) {
            mondaySwitch1.setText("1) " + this.mondaySwitches.get(0).getType() + ": " + this.mondaySwitches.get(0).getTime() + ", " + this.mondaySwitches.get(1).getType() + ": "
                    + this.mondaySwitches.get(1).getTime() + " ");
            bMondayRemoveSwitches[0].setVisibility(View.VISIBLE); // makes button appear
        }
        if (this.mondaySwitches.get(2).getState()) {
            mondaySwitch2.setText("2) " + this.mondaySwitches.get(2).getType() + ": " + this.mondaySwitches.get(2).getTime() + ", " + this.mondaySwitches.get(3).getType() + ": "
                    + this.mondaySwitches.get(3).getTime() + " ");
            bMondayRemoveSwitches[1].setVisibility(View.VISIBLE); // makes button appear
        }
        if (this.mondaySwitches.get(4).getState()) {
            mondaySwitch3.setText("3) " + this.mondaySwitches.get(4).getType() + ": " + this.mondaySwitches.get(4).getTime() + ", " + this.mondaySwitches.get(5).getType() + ": "
                    + this.mondaySwitches.get(5).getTime() + " ");
            bMondayRemoveSwitches[2].setVisibility(View.VISIBLE); // makes button appear
        }
        if (this.mondaySwitches.get(6).getState()) {
            mondaySwitch3.setText("4) " + this.mondaySwitches.get(6).getType() + ": " + this.mondaySwitches.get(6).getTime() + ", " + this.mondaySwitches.get(7).getType() + ": "
                    + this.mondaySwitches.get(7).getTime() + " ");
            bMondayRemoveSwitches[3].setVisibility(View.VISIBLE); // makes button appear
        }
        if (this.mondaySwitches.get(8).getState()) {
            mondaySwitch3.setText("5) " + this.mondaySwitches.get(8).getType() + ": " + this.mondaySwitches.get(8).getTime() + ", " + this.mondaySwitches.get(9).getType() + ": "
                    + this.mondaySwitches.get(9).getTime() + " ");
            bMondayRemoveSwitches[4].setVisibility(View.VISIBLE); // makes button appear
        }
    }
}

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
public class Thursday extends Day {

    TextView title;
    boolean allowed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        day = "Thursday";
        dayNumber = 4;
        super.onCreate(savedInstanceState);

        bMondayAdd = (Button)findViewById(R.id.bMondayAdd);
        bMondayRemoveAll = (Button)findViewById(R.id.bMondayRemoveAll);
        bMondayChange = (Button)findViewById(R.id.bMondayChange);
        mondayDayTempText = (TextView)findViewById(R.id.dayTemp);
        mondayNightTempText = (TextView)findViewById(R.id.nightTemp);
        mondayTitle = (TextView)findViewById(R.id.mondayTitle);

        title = (TextView)findViewById(R.id.mondayTitle);
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

        title.setText(day + " — Switches");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    wkProgram = HeatingSystem.getWeekProgram();
                    mondaySwitches = wkProgram.getDay(day);
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
                        wkProgram = HeatingSystem.getWeekProgram();
                        mondaySwitches = wkProgram.getDay(day);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                displaySwitches();
                            }
                        });
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
                // Make sure all input fields have 2 digit format even if user inputs fewer digits
                for (int i = 0; i < times.length; i++) {
                    if (times[i].length() == 0) {
                        times[i] = "00";
                    } else if (times[i].length() == 1) {
                        times[i] = "0" + times[i];
                    }
                }
                // Make sure new switch has acceptable switch times
                int newDay = Integer.parseInt(times[0] + times[1], 10);
                int newNight = Integer.parseInt(times[2] + times[3], 10);
                for (int i = 0; i < 5; i++) {
                    if (wkProgram.data.get(day).get(2 * i).getState()) {
                        // Convert active switch's time to int
                        String tmpd = wkProgram.data.get(day).get(2 * i).getTime();
                        String tmpd2 = tmpd.substring(0, 2) + tmpd.substring(3, 5);
                        String tmpn = wkProgram.data.get(day).get(2 * i + 1).getTime();
                        String tmpn2 = tmpn.substring(0, 2) + tmpn.substring(3, 5);
                        int oldDay = Integer.parseInt(tmpd2, 10);
                        int oldNight = Integer.parseInt(tmpn2, 10);
                        // Check if the new switch does not overlap with active switch
                        if (newDay < oldDay && newNight < oldDay) {
                            // The new switch is before the active switch.
                        } else if (newDay > oldNight && newNight > oldNight) {
                            // The new switch is after the active switch.
                        } else {
                            // The new switch overlaps with the active switch.
                            allowed = false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast overlap = Toast.makeText(getApplicationContext(), "The switch was not added: a new switch can't overlap with an already active switch.", Toast.LENGTH_LONG);
                                    overlap.show();
                                }
                            });
                        }
                    }
                }
                // Check if the new night switch is after the new day switch
                if (!(newDay < newNight)) {
                    allowed = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast night = Toast.makeText(getApplicationContext(), "The switch was not added: the night switch must be later than the day switch.", Toast.LENGTH_LONG);
                            night.show();
                        }
                    });
                }

                // Concatenate strings to create a hh:mm format
                daySwitchTime = times[0] + ":" + times[1];
                nightSwitchTime = times[2] + ":" + times[3];
                //add switches
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (allowed) {
                                setSwitch(day, daySwitchTime, nightSwitchTime);
                            }
                            allowed = true;
                            HeatingSystem.setWeekProgram(wkProgram);
                            wkProgram = HeatingSystem.getWeekProgram();
                            mondaySwitches = wkProgram.getDay(day);
                            // Display switches again
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    displaySwitches();
                                }
                            });
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
                    displaySwitches();
                    //Remove text for all switches and upload week program to server
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                HeatingSystem.setWeekProgram(wkProgram);
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
                                displaySwitches();
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
                                mondaySwitches = wkProgram.getDay(day);
                                // Display switches again (doesn't really work yet)
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        displaySwitches();
                                    }
                                });
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
                    Toast maxSwitchesAdded = Toast.makeText(getApplicationContext(), "The switch was not added: you can't add more than 5 switches.", Toast.LENGTH_LONG);
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
                            Toast maxSwitchesAdded = Toast.makeText(getApplicationContext(), "You've now added the maximum of 5 switches.", Toast.LENGTH_LONG);
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
        try {
            if (this.mondaySwitches.get(0).getState()) {
                mondaySwitch1.setText("1) " + this.mondaySwitches.get(0).getType() + ": " + this.mondaySwitches.get(0).getTime() + ", " + this.mondaySwitches.get(1).getType() + ": "
                        + this.mondaySwitches.get(1).getTime() + " ");
                bMondayRemoveSwitches[0].setVisibility(View.VISIBLE); // makes button appear
            } else {
                mondaySwitch1.setText("");
                bMondayRemoveSwitches[0].setVisibility(View.INVISIBLE); // makes button appear
            }
            if (this.mondaySwitches.get(2).getState()) {
                mondaySwitch2.setText("2) " + this.mondaySwitches.get(2).getType() + ": " + this.mondaySwitches.get(2).getTime() + ", " + this.mondaySwitches.get(3).getType() + ": "
                        + this.mondaySwitches.get(3).getTime() + " ");
                bMondayRemoveSwitches[1].setVisibility(View.VISIBLE); // makes button appear
            } else {
                mondaySwitch2.setText("");
                bMondayRemoveSwitches[1].setVisibility(View.INVISIBLE); // makes button appear
            }
            if (this.mondaySwitches.get(4).getState()) {
                mondaySwitch3.setText("3) " + this.mondaySwitches.get(4).getType() + ": " + this.mondaySwitches.get(4).getTime() + ", " + this.mondaySwitches.get(5).getType() + ": "
                        + this.mondaySwitches.get(5).getTime() + " ");
                bMondayRemoveSwitches[2].setVisibility(View.VISIBLE); // makes button appear
            } else {
                mondaySwitch3.setText("");
                bMondayRemoveSwitches[2].setVisibility(View.INVISIBLE); // makes button appear
            }
            if (this.mondaySwitches.get(6).getState()) {
                mondaySwitch4.setText("4) " + this.mondaySwitches.get(6).getType() + ": " + this.mondaySwitches.get(6).getTime() + ", " + this.mondaySwitches.get(7).getType() + ": "
                        + this.mondaySwitches.get(7).getTime() + " ");
                bMondayRemoveSwitches[3].setVisibility(View.VISIBLE); // makes button appear
            } else {
                mondaySwitch4.setText("");
                bMondayRemoveSwitches[3].setVisibility(View.INVISIBLE); // makes button appear
            }
            if (this.mondaySwitches.get(8).getState()) {
                mondaySwitch5.setText("5) " + this.mondaySwitches.get(8).getType() + ": " + this.mondaySwitches.get(8).getTime() + ", " + this.mondaySwitches.get(9).getType() + ": "
                        + this.mondaySwitches.get(9).getTime() + " ");
                bMondayRemoveSwitches[4].setVisibility(View.VISIBLE); // makes button appear
            } else {
                mondaySwitch5.setText("");
                bMondayRemoveSwitches[4].setVisibility(View.INVISIBLE); // makes button appear
            }
        } catch (Exception e) {
            System.out.println("Error in getdata: " + e);
        }
    }
}

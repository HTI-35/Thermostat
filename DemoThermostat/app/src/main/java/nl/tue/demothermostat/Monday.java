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
import android.widget.TextView;

import org.thermostatapp.util.*;

import java.net.ConnectException;
import java.util.ArrayList;

/**
 * Created by Anne on 05/06/2015.
 */
public class Monday extends Day {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        day = "Monday";
        dayNumber = 1;
        title.setText(day);

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

        bAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: add the switch from the input fields to switch list
                /* try {
                    int nrSwitches = wkProgram.get_nr_switches_active(dayNumber);
                    if (nrSwitches < 5) {
                        System.out.println("")
                        //wkProgram.addSwitch(TODO,TODO,"day","monday");
                    }
                    HeatingSystem.setWeekProgram(wkProgram);
                    //todo:get switches from server and set textfields
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
                */
                System.out.println("add test: You've clicked the add button.");

                String[] times = new String[4];
                times[0] = daySwitchHrs.getText().toString();
                times[1] = daySwitchMins.getText().toString();
                times[2] = nightSwitchHrs.getText().toString();
                times[3] = nightSwitchMins.getText().toString();
                //make sure all input fields have 2 digit format even if user inputs fewer digits
                for(int i=0; i<times.length; i++) {
                    if (times[i].length() == 0) {
                        times[i] = "00";
                    } else if (times[i].length() == 1) {
                        times[i] = "0" + times[i];
                    }
                }
                //concatenate strings to create a hh:mm format
                String daySwitchTime = times[0] + ":" + times[1];
                String nightSwitchTime = times[2] + ":" + times[3];
                //add switches
                setSwitch(day, daySwitchTime, nightSwitchTime);
            }
        });


    }

    /* Add a day and a night switch to the array for a specified day */
    public void setSwitch(String day, String dayTime, String nightTime) {
        //System.out.println("add test: you're in the setSwitch method");
        for (int i=0; i<5; i++) {
            //System.out.println("add test: you're in the for loop");
            if (!wkProgram.data.get(day).get(2*i).getState()) { //if one switch pair of the day is off
                //System.out.println("add test: an OFF pair has been found");
                wkProgram.data.get(day).remove(2*i); //remove day switch
                wkProgram.data.get(day).remove(2*i+1); //remove night switch
                //System.out.println("add test: two switches have been removed");
                wkProgram.data.get(day).add(new Switch("day", true, dayTime)); //add day switch
                wkProgram.data.get(day).add(new Switch("night", true, nightTime)); //add day switch
                //System.out.println("add test: two switches have been added");
                i=5;
            }
        }
        if (wkProgram.data.get(day).get(8).getState()) {
            //System.out.println("add test: all switches are enabled, no more can be added");
        }
        //HeatingSystem.setWeekProgram(wkProgram);
        //connection error in HeatingSystem.get.("weekProgramState")
    }


    public void displaySwitches() {
        if (this.mondaySwitches.get(0).getState()) {
            switch1.setText("1) " + this.mondaySwitches.get(0).getType() + ": " + this.mondaySwitches.get(0).getTime() + ", " + this.mondaySwitches.get(1).getType() + ": "
                    + this.mondaySwitches.get(1).getTime() + " ");
            bRemoveSwitches[0].setVisibility(View.VISIBLE); // makes button appear
        }
        if (this.mondaySwitches.get(2).getState()) {
            switch2.setText("2) " + this.mondaySwitches.get(2).getType() + ": " + this.mondaySwitches.get(2).getTime() + ", " + this.mondaySwitches.get(3).getType() + ": "
                    + this.mondaySwitches.get(3).getTime() + " ");
            bRemoveSwitches[1].setVisibility(View.VISIBLE); // makes button appear
        }
        if (this.mondaySwitches.get(4).getState()) {
            switch3.setText("3) " + this.mondaySwitches.get(4).getType() + ": " + this.mondaySwitches.get(4).getTime() + ", " + this.mondaySwitches.get(5).getType() + ": "
                    + this.mondaySwitches.get(5).getTime() + " ");
            bRemoveSwitches[2].setVisibility(View.VISIBLE); // makes button appear
        }
        if (this.mondaySwitches.get(6).getState()) {
            switch3.setText("4) " + this.mondaySwitches.get(6).getType() + ": " + this.mondaySwitches.get(6).getTime() + ", " + this.mondaySwitches.get(7).getType() + ": "
                    + this.mondaySwitches.get(7).getTime() + " ");
            bRemoveSwitches[3].setVisibility(View.VISIBLE); // makes button appear
        }
        if (this.mondaySwitches.get(8).getState()) {
            switch3.setText("5) " + this.mondaySwitches.get(8).getType() + ": " + this.mondaySwitches.get(8).getTime() + ", " + this.mondaySwitches.get(9).getType() + ": "
                    + this.mondaySwitches.get(9).getTime() + " ");
            bRemoveSwitches[4].setVisibility(View.VISIBLE); // makes button appear
        }
    }
}

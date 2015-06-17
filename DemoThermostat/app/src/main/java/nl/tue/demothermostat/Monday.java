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
                    displaySwitches();
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


    }

    public void displaySwitches() {
        /*for (int i=0; i<Day.ownSwitches.size(); i++) {
            //print switches to console if they're in ON state
            if (Day.ownSwitches.get(i).getState()) {
                System.out.println("switch: " +
                                Day.ownSwitches.get(i).getType() + " " +
                                Day.ownSwitches.get(i).getTime()
                );
            }
        }*/
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ownSwitches = mondaySwitches;
                if (Day.ownSwitches.get(1).getState()) {
                    switch1.setText("1) " + Day.ownSwitches.get(0).getType() + ": " + Day.ownSwitches.get(0).getTime() + ", " + Day.ownSwitches.get(1).getType() + ": "
                            + Day.ownSwitches.get(1).getTime());
                    bRemoveSwitches[0].setVisibility(View.VISIBLE); // makes button appear
                }
                if (Day.ownSwitches.get(3).getState()) {
                    switch2.setText("2) " + Day.ownSwitches.get(2).getType() + ": " + Day.ownSwitches.get(2).getTime() + ", " + Day.ownSwitches.get(3).getType() + ": "
                            + Day.ownSwitches.get(3).getTime());
                    bRemoveSwitches[1].setVisibility(View.VISIBLE); // makes button appear
                }
                if (Day.ownSwitches.get(5).getState()) {
                    switch3.setText("3) " + Day.ownSwitches.get(4).getType() + ": " + Day.ownSwitches.get(4).getTime() + ", " + Day.ownSwitches.get(5).getType() + ": "
                            + Day.ownSwitches.get(5).getTime());
                    bRemoveSwitches[2].setVisibility(View.VISIBLE); // makes button appear
                }
                if (Day.ownSwitches.get(7).getState()) {
                    switch4.setText("4) " + Day.ownSwitches.get(6).getType() + ": " + Day.ownSwitches.get(6).getTime() + ", " + Day.ownSwitches.get(7).getType() + ": "
                            + Day.ownSwitches.get(7).getTime());
                    bRemoveSwitches[3].setVisibility(View.VISIBLE); // makes button appear
                }
                if (Day.ownSwitches.get(9).getState()) {
                    switch5.setText("5) " + Day.ownSwitches.get(8).getType() + ": " + Day.ownSwitches.get(8).getTime() + ", " + Day.ownSwitches.get(9).getType() + ": "
                            + Day.ownSwitches.get(9).getTime());
                    bRemoveSwitches[4].setVisibility(View.VISIBLE); // makes button appear
                }
            }
        });
    }
}

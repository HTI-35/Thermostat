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

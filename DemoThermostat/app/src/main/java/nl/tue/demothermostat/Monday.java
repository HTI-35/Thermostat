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
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
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
        if(this.ownSwitches.get(0).getState())
            switch1.setText("1) " + this.ownSwitches.get(0).getType() + ": " + this.ownSwitches.get(0).getTime() + ", " + this.ownSwitches.get(1).getType() + ": " + this.ownSwitches.get(1).getTime());
        if(this.ownSwitches.get(2).getState())
            switch2.setText("2) " + this.ownSwitches.get(2).getType() + ": " + this.ownSwitches.get(2).getTime() + ", " + this.ownSwitches.get(3).getType() + ": " + this.ownSwitches.get(3).getTime());
        if(this.ownSwitches.get(4).getState())
            switch3.setText("3) " + this.ownSwitches.get(4).getType() + ": " + this.ownSwitches.get(4).getTime() + ", " + this.ownSwitches.get(5).getType() + ": " + this.ownSwitches.get(5).getTime());
        if(this.ownSwitches.get(6).getState())
            switch4.setText("4) " + this.ownSwitches.get(6).getType() + ": " + this.ownSwitches.get(6).getTime() + ", " + this.ownSwitches.get(7).getType() + ": " + this.ownSwitches.get(7).getTime());
        if(this.ownSwitches.get(8).getState())
            switch5.setText("5) " + this.ownSwitches.get(8).getType() + ": " + this.ownSwitches.get(8).getTime() + ", " + this.ownSwitches.get(9).getType() + ": " + this.ownSwitches.get(9).getTime());
    }
}

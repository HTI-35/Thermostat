package nl.tue.demothermostat;

import android.os.Bundle;

import org.thermostatapp.util.HeatingSystem;

/**
 * Created by Anne on 05/06/2015.
 */
public class Wednesday extends Day {
    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        day = "Wednesday";
        dayNumber = 3;
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
    */
}
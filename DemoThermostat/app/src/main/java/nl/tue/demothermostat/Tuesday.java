package nl.tue.demothermostat;

import android.os.Bundle;

import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.WeekProgram;

/**
 * Created by Anne on 05/06/2015.
 */
public class Tuesday extends Day {

    WeekProgram testProgram;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        day = "Tuesday";
        dayNumber = 2;
        title.setText(day);
    }
}


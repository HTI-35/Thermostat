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
        setContentView(R.layout.activity_tuesday);
        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/35";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/35/weekprogram";
    }
}


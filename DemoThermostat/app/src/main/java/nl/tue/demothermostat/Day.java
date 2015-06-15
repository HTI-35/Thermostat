package nl.tue.demothermostat;

import android.app.Activity;
import android.os.Bundle;

import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.Switch;

import java.util.ArrayList;

/**
 * Created by Anne on 05/06/2015.
 */
public class Day extends Activity {

    public static ArrayList<Switch> ownSwitches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/35";
        HeatingSystem.WEEK_PROGRAM_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/35/weekprogram";
    }
}

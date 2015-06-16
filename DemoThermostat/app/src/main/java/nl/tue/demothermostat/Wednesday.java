package nl.tue.demothermostat;

import android.os.Bundle;

/**
 * Created by Anne on 05/06/2015.
 */
public class Wednesday extends Day {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        day = "Wednesday";
        dayNumber = 3;
        title.setText(day);
    }
}

package nl.tue.demothermostat;

import android.os.Bundle;

/**
 * Created by Anne on 05/06/2015.
 */
public class Saturday extends Day {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        day = "Saturday";
        dayNumber = 6;
        title.setText(day);
    }
}

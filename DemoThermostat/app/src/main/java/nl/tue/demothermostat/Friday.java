package nl.tue.demothermostat;

import android.os.Bundle;

/**
 * Created by Anne on 05/06/2015.
 */
public class Friday extends Day {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        day = "Friday";
        dayNumber = 5;
        title.setText(day);
    }
}

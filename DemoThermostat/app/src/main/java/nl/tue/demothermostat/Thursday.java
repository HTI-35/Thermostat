package nl.tue.demothermostat;

import android.os.Bundle;

/**
 * Created by Anne on 05/06/2015.
 */
public class Thursday extends Day {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        day = "Thursday";
        dayNumber = 4;
        title.setText(day);
    }
}

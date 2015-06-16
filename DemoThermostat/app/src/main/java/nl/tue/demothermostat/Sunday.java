package nl.tue.demothermostat;

import android.os.Bundle;

/**
 * Created by Anne on 05/06/2015.
 */
public class Sunday extends Day {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        day = "Sunday";
        dayNumber = 7;
        title.setText(day);
    }
}

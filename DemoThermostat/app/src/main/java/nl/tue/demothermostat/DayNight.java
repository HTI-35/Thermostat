package nl.tue.demothermostat;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.thermostatapp.util.HeatingSystem;

/**
 * Created by Anne on 08/06/2015.
 */
public class DayNight extends Activity {

    Button bIncrDayTemp;
    Button bDecrDayTemp;
    Button bIncrNightTemp;
    Button bDecrNightTemp;
    TextView dayTempText;
    TextView nightTempText;
    double dayTemp; //day temperature
    double nightTemp; //night temperature

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daynight);

        bIncrDayTemp = (Button)findViewById(R.id.bIncrDayTemp);
        bDecrDayTemp = (Button)findViewById(R.id.bDecrDayTemp);
        bIncrNightTemp = (Button)findViewById(R.id.bIncrNightTemp);
        bDecrNightTemp = (Button)findViewById(R.id.bDecrNightTemp);
        dayTempText = (TextView)findViewById(R.id.dayTemp);
        nightTempText = (TextView)findViewById(R.id.nightTemp);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //day & night temperature
                    dayTemp = Double.parseDouble(HeatingSystem.get("dayTemperature"));
                    dayTempText.setText("" + dayTemp);
                    nightTemp = Double.parseDouble(HeatingSystem.get("nightTemperature"));
                    nightTempText.setText("" + nightTemp);
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();

        bIncrDayTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //increase day temp
                if (dayTemp <= 30) {
                    dayTemp+=1;
                    dayTempText.setText(dayTemp + " \u2103");
                    setInputLimits();
                }
                putDayTemperature();
            }
        });

        bDecrDayTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //decrease day temp
                if (dayTemp >= 5) {
                    dayTemp-=1;
                    dayTempText.setText(dayTemp + " \u2103");
                    setInputLimits();
                }
                putDayTemperature();
            }
        });

        bIncrNightTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //increase night temp
                if (nightTemp <= 30) {
                    nightTemp += 1;
                    nightTempText.setText(nightTemp + " \u2103");
                    setInputLimits();
                }
                putNightTemperature();
            }
        });

        bDecrNightTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //decrease night temp
                if (nightTemp >= 5) {
                    nightTemp -= 1;
                    nightTempText.setText(nightTemp + " \u2103");
                    setInputLimits();
                }
                putNightTemperature();
            }
        });

    }

    public void putDayTemperature(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HeatingSystem.put("dayTemperature", "" + dayTemp);
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }

    public void putNightTemperature(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HeatingSystem.put("nightTemperature", "" + nightTemp);
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }

    public void setInputLimits(){
        if (dayTemp == 30) { //graying out buttons and reenabling them
            bIncrDayTemp.setClickable(false);
            bIncrDayTemp.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
            bDecrDayTemp.setClickable(true);
            bDecrDayTemp.getBackground().setColorFilter(null);
        } else if (dayTemp == 5) {
            bDecrDayTemp.setClickable(false);
            bDecrDayTemp.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
            bIncrDayTemp.setClickable(true);
        } else {
            bDecrDayTemp.setClickable(true);
            bDecrDayTemp.getBackground().setColorFilter(null);
            bIncrDayTemp.setClickable(true);
            bIncrDayTemp.getBackground().setColorFilter(null);
        }

        if (nightTemp == 30) { //graying out buttons and reenabling them
            bIncrNightTemp.setClickable(false);
            bIncrNightTemp.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
            bDecrNightTemp.setClickable(true);
            bDecrNightTemp.getBackground().setColorFilter(null);
        } else if (nightTemp == 5) {
            bDecrNightTemp.setClickable(false);
            bDecrNightTemp.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
            bIncrNightTemp.setClickable(true);
        } else {
            bDecrNightTemp.setClickable(true);
            bDecrNightTemp.getBackground().setColorFilter(null);
            bIncrNightTemp.setClickable(true);
            bIncrNightTemp.getBackground().setColorFilter(null);
        }
    }
}

package nl.tue.demothermostat;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
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
    VerticalSeekBar seekBarDay;
    VerticalSeekBar seekBarNight;

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
        seekBarDay = (VerticalSeekBar)findViewById(R.id.tempSeekbarDay);
        seekBarNight = (VerticalSeekBar)findViewById(R.id.tempSeekbarNight);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //day & night temperature
                    dayTemp = Double.parseDouble(HeatingSystem.get("dayTemperature"));
                    dayTempText.setText(dayTemp + " \u2103");
                    updateSeekBarDay();
                    nightTemp = Double.parseDouble(HeatingSystem.get("nightTemperature"));
                    nightTempText.setText(nightTemp + " \u2103");
                    updateSeekBarNight();
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();

        bIncrDayTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //increase day temp
                if (dayTemp <= 30) {
                    dayTemp += 0.1;
                    setDayTemp();
                    updateSeekBarDay();
                    setInputLimits();
                }
                putDayTemperature();
            }
        });

        bDecrDayTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //decrease day temp
                if (dayTemp >= 5) {
                    dayTemp -= 0.1;
                    setDayTemp();
                    updateSeekBarDay();
                    setInputLimits();
                }
                putDayTemperature();
            }
        });

        seekBarDay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dayTemp = (progress + 50) / 10.0;
                setDayTemp();
                setInputLimits();
                putDayTemperature();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        bIncrNightTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //increase night temp
                if (nightTemp <= 30) {
                    nightTemp += 0.1;
                    setNightTemp();
                    updateSeekBarNight();
                    setInputLimits();
                }
                putNightTemperature();
            }
        });

        bDecrNightTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //decrease night temp
                if (nightTemp >= 5) {
                    nightTemp -= 0.1;
                    setNightTemp();
                    updateSeekBarNight();
                    setInputLimits();
                }
                putNightTemperature();
            }
        });

        seekBarNight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                nightTemp = (progress + 50) / 10.0;
                setNightTemp();
                setInputLimits();
                putNightTemperature();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void setDayTemp() {
        dayTemp = Math.round(dayTemp*10);
        dayTemp = dayTemp/10;
        dayTempText.setText(dayTemp + " \u2103");
    }

    public void setNightTemp() {
        nightTemp = Math.round(nightTemp*10);
        nightTemp = nightTemp/10;
        nightTempText.setText(nightTemp + " \u2103");
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

    void updateSeekBarDay(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                seekBarDay.setProgress((int) (dayTemp * 10 - 50));
            }
        });
    }

    void updateSeekBarNight(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                seekBarNight.setProgress((int) (nightTemp * 10 - 50));
            }
        });
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

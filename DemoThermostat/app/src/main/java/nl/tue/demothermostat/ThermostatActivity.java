package nl.tue.demothermostat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

//webserver
import org.thermostatapp.util.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.ConnectException;
import java.util.Timer;
import java.util.TimerTask;


public class ThermostatActivity extends Activity {

    double vTemp, cTemp; //current temperature
    TextView targetTemp, currentTemp;
    SeekBar seekBar;
    Button bIncrTemp, bDecrTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermostat);

        bIncrTemp = (Button)findViewById(R.id.bIncrTemp);
        bDecrTemp = (Button)findViewById(R.id.bDecrTemp);
        Button bWeekOverview = (Button)findViewById(R.id.bWeekOverview);

        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/35";
        targetTemp = (TextView)findViewById(R.id.targetTemp);
        currentTemp = (TextView)findViewById(R.id.currTemp);
        seekBar = (SeekBar)findViewById(R.id.seekBar);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //target temperature
                    vTemp = Double.parseDouble(HeatingSystem.get("currentTemperature"));
                    System.out.println("tt: " + HeatingSystem.get("targetTemperature"));
                    System.out.println("tt: ");
                    targetTemp.setText("" + vTemp + " \u2103");
                    currentTemp.setText("" + vTemp + " \u2103");
                    seekBar.setProgress((int) Math.floor(vTemp - 5.0));
                    System.out.println("vTemp1:" + HeatingSystem.get("currentTemperature"));
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        cTemp = Double.parseDouble(HeatingSystem.get("currentTemperature"));
                        Double targetBuffer =  Double.parseDouble(HeatingSystem.get("targettemperature"));
                        System.out.println("qqq vtemp: " + vTemp);
                        System.out.println("qqq targetBuffer: " + targetBuffer);
                        if (targetBuffer != vTemp){
                            vTemp = targetBuffer;
                            seekBar.setProgress((int) Math.floor(vTemp - 5.0));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                targetTemp.setText("" + vTemp + " \u2103");
                                currentTemp.setText("" + cTemp + " \u2103");
                            }
                        });
                    }
                } catch (InterruptedException e){

                } catch (ConnectException e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        };

        t.start();


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                targetTemp.setText( (progress + 5) + " \u2103");
                vTemp = progress + 5;
                setInputLimits();
                putCurrentTemperature();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        bIncrTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//increase temperature via button
                if (vTemp <= 30) {
                    vTemp+=1;
                    targetTemp.setText(vTemp + " \u2103");
                    seekBar.setProgress((int)(Math.floor(vTemp - 5.0)));
                   setInputLimits();
                }
                putCurrentTemperature();
            }
        });
        bDecrTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//decrease temperature via button
                if (vTemp >= 5){
                    vTemp -= 1;
                    targetTemp.setText(vTemp + " \u2103");
                    seekBar.setProgress((int)(Math.floor(vTemp - 5)));
                    setInputLimits();
                }
                putCurrentTemperature();
            }
        });
        bWeekOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toWeekOverview = new Intent(v.getContext(), WeekOverview.class);
                startActivity(toWeekOverview);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_thermostat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void putCurrentTemperature(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HeatingSystem.put("currentTemperature", "" + vTemp);
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }

    public void setInputLimits(){
        if (vTemp == 30) { //graying out buttons and reenabling them
            bIncrTemp.setClickable(false);
            bIncrTemp.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
            bDecrTemp.setClickable(true);
            bDecrTemp.getBackground().setColorFilter(null);
        } else if (vTemp == 5) {
            bDecrTemp.setClickable(false);
            bDecrTemp.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
            bIncrTemp.setClickable(true);
        } else {
            bDecrTemp.setClickable(true);
            bDecrTemp.getBackground().setColorFilter(null);
            bIncrTemp.setClickable(true);
            bIncrTemp.getBackground().setColorFilter(null);
        }
    }
}

package nl.tue.demothermostat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

//webserver
import org.thermostatapp.util.*;
import org.thermostatapp.util.Switch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.ConnectException;
import java.util.Timer;
import java.util.TimerTask;


public class ThermostatActivity extends Activity {

    double vTemp, cTemp; //target temperature * 10, current temperature
    boolean wkProgramEnabled;
    TextView targetTemp, currentTemp;
    SeekBar seekBar;
    Button bIncrTemp, bDecrTemp;
    CheckBox vacationMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermostat);

        bIncrTemp = (Button)findViewById(R.id.bIncrTemp);
        bDecrTemp = (Button)findViewById(R.id.bDecrTemp);
        Button bWeekOverview = (Button)findViewById(R.id.bWeekOverview);
        vacationMode = (CheckBox)findViewById(R.id.vacationMode);

        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/35";
        targetTemp = (TextView)findViewById(R.id.targetTemp);
        currentTemp = (TextView)findViewById(R.id.currTemp);
        seekBar = (SeekBar)findViewById(R.id.seekBar);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //target temperature
                    vTemp = Double.parseDouble(HeatingSystem.get("currentTemperature")); // It's supposed to be getting currentTemperature
                    System.out.println("tt: " + HeatingSystem.get("targetTemperature"));
                    System.out.println("tt: ");
                    updateTargetTemp();
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
                        wkProgramEnabled = HeatingSystem.getVacationMode();

                        if (targetBuffer != vTemp){
                            vTemp = targetBuffer;
                            seekBar.setProgress((int) Math.floor(vTemp - 5.0));
                        }
                        updateTargetTemp();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
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
                vTemp = progress + 5;
                updateTargetTemp();
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
                    vTemp += 0.1;
                    updateTargetTemp();
                    seekBar.setProgress((int) (Math.floor(vTemp - 5.0)));
                    setInputLimits();
                }
                putCurrentTemperature();
            }
        });
        bDecrTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//decrease temperature via button
                if (vTemp >= 5){
                    vTemp -= 0.1;
                    updateTargetTemp();
                    seekBar.setProgress((int)(Math.floor(vTemp - 5.0)));
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

        vacationMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                   System.out.println("This is working yay");
                   if (!HeatingSystem.getVacationMode()) {
                       HeatingSystem.put("weekProgramState", "on");
                   }
               }
           }
        );

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

    void putCurrentTemperature(){
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

    void updateTargetTemp(){
        vTemp = Math.round(vTemp*10);
        vTemp = vTemp/10;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("temp in method is: "+vTemp);
                targetTemp.setText(vTemp + " \u2103");
            }
        });
    }

    public void setInputLimits(){
        if (vTemp == 30) { //graying out buttons and re-enabling them
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

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

    double vTemp, cTemp; //target temperature, current temperature
    boolean wkProgramEnabled, checkvacation = true;
    TextView targetTemp, currentTemp, enabled;
    VerticalSeekBar seekBar;
    Button bIncrTemp, bDecrTemp;
    CheckBox vacationMode;
    ImageView flame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermostat);

        bIncrTemp = (Button)findViewById(R.id.bIncrTemp);
        bDecrTemp = (Button)findViewById(R.id.bDecrTemp);
        final Button bWeekOverview = (Button)findViewById(R.id.bWeekOverview);
        vacationMode = (CheckBox)findViewById(R.id.vacationMode);
        flame = (ImageView)findViewById(R.id.flame);

        HeatingSystem.BASE_ADDRESS = "http://wwwis.win.tue.nl/2id40-ws/35";
        enabled = (TextView)findViewById(R.id.wkProgramEnabled);
        targetTemp = (TextView)findViewById(R.id.targetTemp);
        currentTemp = (TextView)findViewById(R.id.currTemp);
        seekBar = (VerticalSeekBar)findViewById(R.id.tempSeekbar);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Update target temperature
                    vTemp = Double.parseDouble(HeatingSystem.get("currentTemperature")); // It's supposed to be getting currentTemperature
                    updateTargetTemp();
                    // Set the flame visibility
                    if (Double.parseDouble(HeatingSystem.get("currentTemperature")) < Double.parseDouble(HeatingSystem.get("targetTemperature"))) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                flame.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                flame.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                    // Set the checkbox for Vacation Mode
                    if (HeatingSystem.get("weekProgramState").equals("off")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                vacationMode.setChecked(true);
                                enabled.setText("Week program is disabled.");
                            }
                        });

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                vacationMode.setChecked(false);
                                enabled.setText("Week program is enabled.");
                            }
                        });

                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            currentTemp.setText(vTemp + " \u2103");
                            seekBar.setProgress((int) (vTemp * 10 - 50));
                        }
                    });
                    System.out.println("vTemp1:" + HeatingSystem.get("currentTemperature"));
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();

        new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        cTemp = Double.parseDouble(HeatingSystem.get("currentTemperature"));
                        Double targetBuffer =  Double.parseDouble(HeatingSystem.get("targettemperature"));
                        wkProgramEnabled = !(HeatingSystem.getVacationMode());

                        if (targetBuffer != vTemp){
                            vTemp = targetBuffer;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    seekBar.setProgress((int) (vTemp * 10 - 50));
                                }
                            });
                        }
                        updateTargetTemp();
                        // Set the flame visibility
                        if (Double.parseDouble(HeatingSystem.get("currentTemperature")) < Double.parseDouble(HeatingSystem.get("targetTemperature"))) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    flame.setVisibility(View.VISIBLE);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    flame.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                        // Set the checkbox for Vacation Mode
                        if (checkvacation) {
                            if (HeatingSystem.get("weekProgramState").equals("off")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        vacationMode.setChecked(true);
                                        enabled.setText("Week program is disabled.");
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        vacationMode.setChecked(false);
                                        enabled.setText("Week program is enabled.");
                                    }
                                });
                            }
                        } else {
                            checkvacation = true;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                currentTemp.setText("" + cTemp + " \u2103");
                            }
                        });
                    }
                } catch (InterruptedException e){
                    System.err.println("Error from getdata " + e);
                    e.printStackTrace();
                } catch (ConnectException e) {
                    System.err.println("Error from getdata " + e);
                    e.printStackTrace();
                }
            }
        }.start();



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                vTemp = (progress + 50) / 10.0;
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
                    seekBar.setProgress((int) (vTemp * 10 - 50));
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
                    seekBar.setProgress((int)(vTemp * 10 - 50));
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
                checkvacation = false;
                if (isChecked){
                    disableWeekProgram();
                } else {
                    enableWeekProgram();
                }
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

    void disableWeekProgram(){
        System.out.println("Vacation: trying to set program to OFF");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HeatingSystem.put("weekProgramState", "off");
                    System.out.println("Vacation: set program to OFF");
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void enableWeekProgram(){
        System.out.println("Vacation: trying to set program to ON");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HeatingSystem.put("weekProgramState", "on");
                    System.out.println("Vacation: set program to ON");
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void putCurrentTemperature(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HeatingSystem.put("currentTemperature", "" + vTemp);
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                    e.printStackTrace();
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

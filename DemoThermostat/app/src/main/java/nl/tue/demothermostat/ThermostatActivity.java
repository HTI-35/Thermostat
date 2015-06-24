package nl.tue.demothermostat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

//webserver
import org.thermostatapp.util.*;
import java.net.ConnectException;


public class ThermostatActivity extends Activity {

    double vTemp, cTemp; // Target temperature, current temperature
    boolean wkProgramEnabled, checkvacation = true;
    TextView targetTemp, currentTemp, enabled, infoText, dayTempHome, nightTempHome;
    VerticalSeekBar seekBar;
    Button bIncrTemp, bDecrTemp, bChangeHome;
    ImageButton info, close;
    CheckBox vacationMode;
    ImageView flame;
    // Declare day and night temperature
    double dayTemp;
    double nightTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermostat);

        bIncrTemp = (Button)findViewById(R.id.bIncrTemp);
        bDecrTemp = (Button)findViewById(R.id.bDecrTemp);
        final Button bWeekOverview = (Button)findViewById(R.id.bWeekOverview);
        dayTempHome = (TextView)findViewById(R.id.dayTempHome);
        nightTempHome = (TextView)findViewById(R.id.nightTempHome);
        bChangeHome = (Button)findViewById(R.id.bChangeHome);
        vacationMode = (CheckBox)findViewById(R.id.vacationMode);
        info = (ImageButton)findViewById(R.id.info);
        flame = (ImageView)findViewById(R.id.flame);
        close = (ImageButton)findViewById(R.id.close);

        HeatingSystem.BASE_ADDRESS = "http://pcwin889.win.tue.nl/2id40-ws/35";
        enabled = (TextView)findViewById(R.id.wkProgramEnabled);
        infoText = (TextView)findViewById(R.id.infoText);
        targetTemp = (TextView)findViewById(R.id.targetTemp);
        currentTemp = (TextView)findViewById(R.id.currTemp);
        seekBar = (VerticalSeekBar)findViewById(R.id.tempSeekbar);

        seekBar.setProgress(160); // set progress to the default of 21.0 degrees centigrade

        /* Check if user is online... this doesn't work, I get a fatal exception in AsyncTask and don't know why */
        if(!isOnline()) {
            Toast disconnected = Toast.makeText(getApplicationContext(), "You need to be connected to a WiFi or cellular data network in order to use this application.", Toast.LENGTH_LONG);
            disconnected.show();
            System.exit(0);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dayTemp = Double.parseDouble(HeatingSystem.get("dayTemperature"));
                    nightTemp = Double.parseDouble(HeatingSystem.get("nightTemperature"));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dayTempHome.setText("Day temperature: " + dayTemp + " \u2103");
                            nightTempHome.setText("Night temperature: " + nightTemp +  " \u2103");
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Update target temperature
                    vTemp = Double.parseDouble(HeatingSystem.get("currentTemperature")); // It's supposed to be getting currentTemperature
                    updateTargetTemp();
                    // Set day and night temperature
                    dayTemp = Double.parseDouble(HeatingSystem.get("dayTemperature"));
                    nightTemp = Double.parseDouble(HeatingSystem.get("nightTemperature"));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dayTempHome.setText("Day temperature: " + dayTemp + " \u2103");
                            nightTempHome.setText("Night temperature: " + nightTemp + " \u2103");
                        }
                    });
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
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        /* Set listeners for all buttons */
        // Seekbar listener
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
        // Info button listener
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dayTempHome.setVisibility(View.INVISIBLE);
                        nightTempHome.setVisibility(View.INVISIBLE);
                        bChangeHome.setVisibility(View.INVISIBLE);
                        infoText.setVisibility(View.VISIBLE);
                        close.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        // Close button listener
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        infoText.setVisibility(View.INVISIBLE);
                        close.setVisibility(View.INVISIBLE);
                        dayTempHome.setVisibility(View.VISIBLE);
                        nightTempHome.setVisibility(View.VISIBLE);
                        bChangeHome.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        // Increase temperature button listener
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
        // Decrease temperature button listener
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
        // Week overview button listener
        bWeekOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toWeekOverview = new Intent(v.getContext(), WeekOverview.class);
                startActivity(toWeekOverview);
            }
        });
        // Vacation mode checkbox listener
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
        // Change button listener
        bChangeHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), DayNight.class));
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

    /* Disabling the week program */
    void disableWeekProgram(){
        System.out.println("Vacation: trying to set program to OFF");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HeatingSystem.put("weekProgramState", "off");
                    System.out.println("Vacation: set program to OFF");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            enabled.setText("Week program is disabled.");
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /* Enabling the week program */
    void enableWeekProgram(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HeatingSystem.put("weekProgramState", "on");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            enabled.setText("Week program is enabled.");
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /* Putting the target temperature */
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

    /* Updating the target temperature */
    void updateTargetTemp(){
        vTemp = Math.round(vTemp*10);
        vTemp = vTemp/10;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                targetTemp.setText(vTemp + " \u2103");
            }
        });
    }

    /* Graying out buttons and re-enabling them */
    public void setInputLimits(){
        if (vTemp == 30) {
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

    /* Reload day & night temperature when the day activity is revisited */
    public void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dayTemp = Double.parseDouble(HeatingSystem.get("dayTemperature"));
                    nightTemp = Double.parseDouble(HeatingSystem.get("nightTemperature"));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dayTempHome.setText("Day temperature: " + dayTemp + " \u2103");
                            nightTempHome.setText("Night temperature: " + nightTemp + " \u2103");
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata " + e);
                }
            }
        }).start();
    }

    /* Check if user is connected to a network */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}

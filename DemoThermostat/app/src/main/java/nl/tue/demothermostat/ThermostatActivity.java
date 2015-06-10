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


public class ThermostatActivity extends Activity {

    int vTemp = 21;//target temperature
    TextView targetTemp;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermostat);
        final Button bIncrTemp = (Button)findViewById(R.id.bIncrTemp);
        final Button bDecrTemp = (Button)findViewById(R.id.bDecrTemp);
        Button bWeekOverview = (Button)findViewById(R.id.bWeekOverview);
        targetTemp = (TextView)findViewById(R.id.targetTemp);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setProgress(vTemp - 5);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                targetTemp.setText( (progress + 5) + " \u0026");
                vTemp = progress + 5;
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
                    vTemp++;
                    targetTemp.setText(vTemp + " \u2103");
                    seekBar.setProgress(vTemp - 5);
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
        });
        bDecrTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//decrease temperature via button
                if (vTemp >= 5){
                    vTemp--;
                    targetTemp.setText(vTemp + " \u2103");
                    seekBar.setProgress(vTemp - 5);
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
}

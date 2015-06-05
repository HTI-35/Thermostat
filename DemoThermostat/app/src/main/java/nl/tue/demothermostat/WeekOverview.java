package nl.tue.demothermostat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Anne on 04/06/2015.
 */
public class WeekOverview extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekoverview);
        Button bMonday = (Button)findViewById(R.id.bMonday);
        Button bTuesday = (Button)findViewById(R.id.bTuesday);
        Button bWednesday = (Button)findViewById(R.id.bWednesday);
        Button bThursday = (Button)findViewById(R.id.bThursday);
        Button bFriday = (Button)findViewById(R.id.bFriday);
        Button bSaturday = (Button)findViewById(R.id.bSaturday);
        Button bSunday = (Button)findViewById(R.id.bSunday);

        bMonday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), Monday.class));
            }
        });
        bTuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), Tuesday.class));
            }
        });
        bWednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), Wednesday.class));
            }
        });
        bThursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), Thursday.class));
            }
        });
        bFriday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), Friday.class));
            }
        });
        bSaturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), Saturday.class));
            }
        });
        bSunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), Sunday.class));
            }
        });
    }
}

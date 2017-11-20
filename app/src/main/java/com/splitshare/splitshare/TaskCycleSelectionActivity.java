package com.splitshare.splitshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by armando on 11/20/17.
 */

public class TaskCycleSelectionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cycle_type_selection_layout);

        Button oneTimeButton = (Button) findViewById(R.id.cycleTypeOneTimeButton);
        oneTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { returnCycleType(0); }
        });

        Button dailyButton = (Button) findViewById(R.id.cycleTypeDailyButton);
        dailyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { returnCycleType(1); }
        });

        Button weeklyButton = (Button) findViewById(R.id.cycleTypeWeeklyButton);
        weeklyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { returnCycleType(2); }
        });

        Button monthlyButton = (Button) findViewById(R.id.cycleTypeMonthlyButton);
        monthlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { returnCycleType(3); }
        });

        Button yearlyButton = (Button) findViewById(R.id.cycleTypeYearlyButton);
        yearlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { returnCycleType(4); }
        });
    }

    @Override
    public void onBackPressed() {
        closeDateSelector();
    }

    private void closeDateSelector(){
        finish();
    }

    private void returnCycleType(int t) {
        Intent data = new Intent();
        data.putExtra("CYCLE_TYPE", t);
        setResult(RESULT_OK, data);
        finish();
    }
}

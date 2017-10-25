package com.splitshare.splitshare;

/**
 * Created by armando on 10/25/17.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class DateSelectionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_selection_activity);

        Button doneSelectingDate = (Button) findViewById(R.id.date_button_done);
        doneSelectingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { closeDateSelector(); }
        });
    }

    @Override
    public void onBackPressed() {
        closeDateSelector();
    }

    private void closeDateSelector(){
        finish();
    }
}

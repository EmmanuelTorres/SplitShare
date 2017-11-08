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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.Date;

public class TaskCreationActivity extends AppCompatActivity {
    public static Calendar startDate;
    public static Calendar endDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_task_activity);

        Button finishButton = (Button) findViewById(R.id.button_finish);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText titleDesc = findViewById(R.id.TitleEntry);
                String title = titleDesc.getText().toString();

                Spinner groupSpinner = findViewById(R.id.spinner);
                Group group = new Group(groupSpinner.getSelectedItem().toString());

                Task newTask = new Task(Calendar.getInstance(),title,"category?",SplitShareApp.acct.getDisplayName(),group);
                MainActivity.addToTaskList(newTask);
                closeTaskCreator();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeTaskCreator();
            }
        });


        Button beginDateButton = (Button) findViewById(R.id.startDateSetButton);
        beginDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { openDateSelector(); }
        });


        Button endDateButton = (Button) findViewById(R.id.endDateSetButton);
        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { openDateSelector(); }
        });
    }

    @Override
    public void onBackPressed() {
        closeTaskCreator();
    }

    private void closeTaskCreator(){
        finish();
    }
    private void openDateSelector() { startActivity(new Intent(this, DateSelectionActivity.class)); }
}

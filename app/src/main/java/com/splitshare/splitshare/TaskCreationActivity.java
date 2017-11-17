package com.splitshare.splitshare;

/**
 * Created by armando on 10/25/17.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static com.splitshare.splitshare.SplitShareApp.usersGroups;

public class TaskCreationActivity extends AppCompatActivity {
    public static SimpleDate startDate;
    public static SimpleDate endDate;
    static final int SET_START_DATE_REQ = 1;
    static final int SET_END_DATE_REQ = 2;
    private boolean startDateIsSet = false;
    private boolean endDateIsSet = false;
    private boolean isTaskReady = false;
    private Button finishButton;
    public List<User> activeUsers = null;
    public Cycle cycle = new Cycle();
    public static Activity taskCreationActivityRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_task_activity);
        taskCreationActivityRef = this;

        // populate spinner with existing groups
        Spinner groupSpinner = findViewById(R.id.spinner);
        ArrayAdapter<Group> groupListAdapter = new ArrayAdapter<Group>(this, android.R.layout.simple_spinner_dropdown_item, usersGroups);
        groupSpinner.setAdapter(groupListAdapter);

        finishButton = (Button) findViewById(R.id.button_finish);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isTaskReady) {
                    outputStatus();
                    return;
                }
                EditText titleDesc = findViewById(R.id.TitleEntry);
                String title = titleDesc.getText().toString();

                EditText descriptionDesc = findViewById(R.id.DescEntry);
                String description = descriptionDesc.getText().toString();

                int pos = ((Spinner)findViewById(R.id.spinner)).getSelectedItemPosition();
                Group group = usersGroups.get(pos);

                Task newTask = new Task(startDate.toCalendar(),title,"category?",SplitShareApp.acct.getDisplayName(),group);
                MainActivity.addToTaskList(newTask);

                boolean[] oneWeek = {false, true, false, true, false, true, false};
                cycle = new Cycle(startDate.toCalendar().get(Calendar.DAY_OF_WEEK), startDate.toCalendar().get(Calendar.WEEK_OF_MONTH), 2);

                //MasterTask(String title, String description, int type, Calendar startDate,
                //Calendar endDate, long groupId, List<User> activeUsers, double paymentAmount,
                //Cycle cycle)
                if (usersGroups.size() > 0) {
                    StoredMasterTask newMasterTask = new StoredMasterTask(title, description, "Chores", startDate, endDate, usersGroups.get(0), activeUsers, false, 123, cycle);
                    String mTaskID = title + (int) (Math.random() * 100);
                    System.out.println(mTaskID);
                    newMasterTask.addToDatabase();
                }

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
            public void onClick(View view) { openDateSelector(SET_START_DATE_REQ); }
        });


        Button endDateButton = (Button) findViewById(R.id.endDateSetButton);
        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { openDateSelector(SET_END_DATE_REQ); }
        });
    }

    @Override
    public void onBackPressed() {
        closeTaskCreator();
    }

    private void closeTaskCreator(){
        TaskPopulation.populate();
        finish();
    }
    private void openDateSelector(int request) { startActivityForResult(new Intent(this, DateSelectionActivity.class), request); }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (resultCode == RESULT_OK) {

            if (requestCode == SET_START_DATE_REQ) {
                startDate = new SimpleDate(data.getIntExtra("MONTH", 4),
                        data.getIntExtra("DAY", 21),
                        data.getIntExtra("YEAR", 1999));
                startDateIsSet = true;
            } else if (requestCode == SET_END_DATE_REQ) {
                endDate = new SimpleDate(data.getIntExtra("MONTH", 4),
                        data.getIntExtra("DAY", 21),
                        data.getIntExtra("YEAR", 1999));
                endDateIsSet = true;
            }
            if (startDateIsSet && endDateIsSet) {
                isTaskReady = true;
            }
        }
    }

    void outputStatus() {
        if (!startDateIsSet)
            Toast.makeText(this, "No start date set", Toast.LENGTH_LONG).show();
        else if (!endDateIsSet)
            Toast.makeText(this, "No end date set", Toast.LENGTH_LONG).show();
    }
}

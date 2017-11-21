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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static com.splitshare.splitshare.SplitShareApp.usersGroups;

public class TaskCreationActivity extends AppCompatActivity {
    public static SimpleDate startDate;
    public static SimpleDate endDate;
    public static int cycleType = 0;
    public static int numUsers = 0;
    static final int SET_START_DATE_REQ = 1;
    static final int SET_END_DATE_REQ = 2;
    static final int SET_CYCLE_TYPE_REQ = 3;
    static final int SET_MEMBERS_REQ = 4;
    private boolean startDateIsSet = false;
    private boolean endDateIsSet = false;
    private boolean isTaskReady = false;
    private boolean isTaskForever = false;
    public static List<User> activeUsers = new ArrayList<User>();
    public Cycle cycle = new Cycle();
    public static Activity taskCreationActivityRef;


    // view elements relevant to Cycle specification
    private LinearLayout cycleWeekDetails;
    private LinearLayout doEveryPeriodLayout;
    private TextView textDoEveryDiscriptor;
    private Button endDateButton;
    private CheckBox repeatForeverBox;

    EditText taskTitle;
    EditText doEveryTextbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_task_activity);
        taskCreationActivityRef = this;

        // populate spinner with existing groups
        Spinner groupSpinner = findViewById(R.id.spinner);
        ArrayAdapter<Group> groupListAdapter = new ArrayAdapter<Group>(this, android.R.layout.simple_spinner_dropdown_item, usersGroups);
        groupSpinner.setAdapter(groupListAdapter);

        taskTitle = findViewById(R.id.TitleEntry);
        doEveryTextbox = findViewById(R.id.RepeatEveryEntry);

        // various things relevant to cycle details
        cycleWeekDetails = findViewById(R.id.daysOfWeekBoxes);
        doEveryPeriodLayout = findViewById(R.id.doEveryPeriod);
        textDoEveryDiscriptor = findViewById(R.id.textDoEveryDiscriptor);
        endDateButton = (Button) findViewById(R.id.endDateSetButton);
        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { openDateSelector(SET_END_DATE_REQ); }
        });

        repeatForeverBox = (CheckBox) findViewById(R.id.repeatForeverCheckbox);
        repeatForeverBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    endDateButton.setVisibility(View.GONE);
                    isTaskForever = true;
                } else {
                    endDateButton.setVisibility(View.VISIBLE);
                    isTaskForever = false;
                }
            }
        });
        clearDetailOptions();

        Button cycleTypeSetter = (Button) findViewById(R.id.setCycleButton);
        cycleTypeSetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(taskCreationActivityRef, TaskCycleSelectionActivity.class), SET_CYCLE_TYPE_REQ);
            }
        });

        Button selectUsersButton = (Button) findViewById(R.id.selectApplicableMembers);
        selectUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = ((Spinner)findViewById(R.id.spinner)).getSelectedItemPosition();
                Group group = usersGroups.get(pos);
                openUserSelectionWithGroup(group);
            }
        });

        Button finishButton = (Button) findViewById(R.id.button_finish);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = taskTitle.getText().toString();

                EditText descriptionDesc = findViewById(R.id.DescEntry);
                String description = descriptionDesc.getText().toString();

                if (!checkStatus()) {
                    return;
                }

                int pos = ((Spinner)findViewById(R.id.spinner)).getSelectedItemPosition();
                Group group = usersGroups.get(pos);
                if (cycleType  == 0) {
                    cycle = new Cycle();
                } else if (cycleType == 1) {
                    int spacing = Integer.parseInt(doEveryTextbox.getText().toString());
                    cycle = new Cycle(spacing);
                } else if (cycleType == 2) {
                    int spacing = Integer.parseInt(doEveryTextbox.getText().toString());
                    boolean[] oneWeek = new boolean[7];
                    CheckBox cbw = findViewById(R.id.checkBoxSun);
                    oneWeek[0] = cbw.isChecked();
                    cbw = findViewById(R.id.checkBoxMon);
                    oneWeek[1] = cbw.isChecked();
                    cbw = findViewById(R.id.checkBoxTues);
                    oneWeek[2] = cbw.isChecked();
                    cbw = findViewById(R.id.checkBoxWed);
                    oneWeek[3] = cbw.isChecked();
                    cbw = findViewById(R.id.checkBoxThurs);
                    oneWeek[4] = cbw.isChecked();
                    cbw = findViewById(R.id.checkBoxFri);
                    oneWeek[5] = cbw.isChecked();
                    cbw = findViewById(R.id.checkBoxSat);
                    oneWeek[6] = cbw.isChecked();

                    cycle = new Cycle(oneWeek, spacing);
                } else if (cycleType == 3) {
                    int spacing = Integer.parseInt(doEveryTextbox.getText().toString());
                    cycle = new Cycle(startDate.day, spacing);
                } else if (cycleType == 4) {
                    cycle = new Cycle(startDate.month, startDate.year, true);
                }
                if (cycleType == 0 || isTaskForever) {
                    //TODO: for infinite tasks, what should endDate be?
                    endDate = startDate;
                }

                if (usersGroups.size() > 0) {
                    StoredMasterTask newMasterTask = new StoredMasterTask(title, description, "Chores", startDate, endDate, usersGroups.get(0), null, false, 123, cycle);
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
    }

    @Override
    public void onBackPressed() {
        closeTaskCreator();
    }

    private void closeTaskCreator(){
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
            if (requestCode == SET_CYCLE_TYPE_REQ) {
                cycleType = data.getIntExtra("CYCLE_TYPE", 0);
                updateUIforType(cycleType);
            }
            if (requestCode == SET_MEMBERS_REQ) {
                numUsers = data.getIntExtra("USERS_SELECTED", 0);
                activeUsers = new ArrayList<User>(AddUserToListActivity.usersSelected);
            }
            if (startDateIsSet && endDateIsSet) {
                isTaskReady = true;
            }
        }
    }

    boolean checkStatus() {
        if (!startDateIsSet) {
            Toast.makeText(this, "No start date set", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (!endDateIsSet && !isTaskForever && cycleType != 0) {
            Toast.makeText(this, "No end date set", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (taskTitle.getText().toString().length() == 0) {
            Toast.makeText(this, "Make sure you've given the task a name!", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (numUsers < 1) {
            Toast.makeText(this, "Make sure you've selected applicable members!", Toast.LENGTH_LONG).show();
            return false;
        }
        else if (Integer.parseInt(doEveryTextbox.getText().toString()) == 0) {
            Toast.makeText(this, "Can't do every 0 times!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    void openUserSelectionWithGroup(Group g) {
        // Creates a new object that references the Firebase database
        DatabaseReference groupsReference = SplitShareApp.firebaseDatabase.getReference("groups/" + g.getGroupTimestamp());

        // The ArrayList that users will be added to
        AddUserToListActivity.usersAvail.clear();

        // A query to get all GroupMembers belonging to our Group
        Query query = groupsReference.child("GroupMembers");

        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot groupMembers)
            {
                // If the GroupMembers portion of our table exists
                if (groupMembers.exists())
                {
                    Log.d("TaskCreate-MembrSelect", groupMembers.toString());

                    // Loop through all the Users
                    for (DataSnapshot groupMember: groupMembers.getChildren())
                    {
                        Log.d("Group-GetUsers", groupMember.getKey());

                        AddUserToListActivity.usersAvail.add(new User(groupMember.getKey(), (String) groupMember.getValue()));
                    }
                    startActivityForResult(new Intent(taskCreationActivityRef, AddUserToListActivity.class), SET_MEMBERS_REQ);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    void clearDetailOptions() {
        cycleWeekDetails.setVisibility(View.GONE);
        repeatForeverBox.setVisibility(View.GONE);
        doEveryPeriodLayout.setVisibility(View.GONE);
        if (repeatForeverBox.isChecked()) {
            endDateButton.setVisibility(View.GONE);
        } else {
            endDateButton.setVisibility(View.VISIBLE);
        }

    }
    void updateUIforType(int t) {
        clearDetailOptions();
        if (t == 0) {
            endDateButton.setVisibility(View.GONE);
        } else if (t == 1) {
            doEveryPeriodLayout.setVisibility(View.VISIBLE);
            repeatForeverBox.setVisibility(View.VISIBLE);
            textDoEveryDiscriptor.setText("day(s)");
        } else if (t == 2) {
            doEveryPeriodLayout.setVisibility(View.VISIBLE);
            repeatForeverBox.setVisibility(View.VISIBLE);
            textDoEveryDiscriptor.setText("week(s)");
            cycleWeekDetails.setVisibility(View.VISIBLE);
        } else if (t == 3) {
            doEveryPeriodLayout.setVisibility(View.VISIBLE);
            repeatForeverBox.setVisibility(View.VISIBLE);
            textDoEveryDiscriptor.setText("month(s)");
        } else if (t == 4) {
            doEveryPeriodLayout.setVisibility(View.VISIBLE);
            repeatForeverBox.setVisibility(View.VISIBLE);
            textDoEveryDiscriptor.setText("year(s)");
        }


    }
}

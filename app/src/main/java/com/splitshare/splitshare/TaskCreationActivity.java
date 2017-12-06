package com.splitshare.splitshare;

/**
 * Created by armando on 10/25/17.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.splitshare.splitshare.SplitShareApp.usersGroups;

public class TaskCreationActivity extends AppCompatActivity {
    // Specified start/end dates
    public static SimpleDate startDate;
    public static SimpleDate endDate;
    // cycle type information
    public static int cycleType = 0;
    public Cycle cycle = new Cycle();
    // number of users involved in task
    public static int numUsers = 0;
    public static List<String> activeUsers = new ArrayList<String>();
    // constants for activity result requests
    static final int SET_START_DATE_REQ = 1;
    static final int SET_END_DATE_REQ = 2;
    static final int SET_CYCLE_TYPE_REQ = 3;
    static final int SET_MEMBERS_REQ = 4;
    // booleans that help determine whether a task is ready
    private boolean startDateIsSet = false;
    private boolean endDateIsSet = false;
    private boolean isTaskForever = false;
    private boolean taskHasCost = false;
    // external references to this activity and the resulting StoredMasterTask
    public static Activity taskCreationActivityRef;
    public static StoredMasterTask newMasterTask;

    // button for selecting members
    Button selectUsersButton;

    // UI elements relevant to Cycle specification
    private LinearLayout cycleWeekDetails;
    private LinearLayout doEveryPeriodLayout;
    private TextView textDoEveryDiscriptor;
    private Button endDateButton;
    private CheckBox repeatForeverBox;
    EditText doEveryTextbox;

    // cost related UI
    private LinearLayout paymentElements;
    private CheckBox hasCostBox;
    private RadioButton perPerson;
    private RadioButton splitEvenly;
    private EditText costValueEntry;

    // task title textbox
    EditText taskTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_task_activity);
        taskCreationActivityRef = this;

        // populate spinner with existing groups
        Spinner groupSpinner = findViewById(R.id.spinner);
        ArrayAdapter<Group> groupListAdapter = new ArrayAdapter<Group>(this, android.R.layout.simple_spinner_dropdown_item, usersGroups);
        groupSpinner.setAdapter(groupListAdapter);

        // initialize reference to task title text entry box
        taskTitle = findViewById(R.id.TitleEntry);
        // initialize reference to [] in "do every x [days, weeks, etc]"
        doEveryTextbox = findViewById(R.id.RepeatEveryEntry);

        // setup cost related UI
        paymentElements = findViewById(R.id.paymentRelatedElements);
        paymentElements.setVisibility(View.GONE);
        perPerson = findViewById(R.id.radioPerPerson);
        splitEvenly = findViewById(R.id.radioSplitEqually);
        costValueEntry = findViewById(R.id.costEntry);

        // various UI relevant to cycle details
        cycleWeekDetails = findViewById(R.id.daysOfWeekBoxes);
        doEveryPeriodLayout = findViewById(R.id.doEveryPeriod);
        textDoEveryDiscriptor = findViewById(R.id.textDoEveryDiscriptor);
        endDateButton = (Button) findViewById(R.id.endDateSetButton);
        // date selection buttons
        Button beginDateButton = (Button) findViewById(R.id.startDateSetButton);
        beginDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { openDateSelector(SET_START_DATE_REQ); }
        });
        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { openDateSelector(SET_END_DATE_REQ); }
        });

        // setup checkbox that toggles repeating task indefinately
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

        // setup checkbox that toggles whether or not there's an associated cost
        hasCostBox = (CheckBox) findViewById(R.id.hasCostCheckbox);
        hasCostBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    paymentElements.setVisibility(View.VISIBLE);
                    taskHasCost = true;
                } else {
                    paymentElements.setVisibility(View.GONE);
                    taskHasCost = false;
                }
            }
        });

        // ensure cycle UI is set up so appropriate UI elements are hidden/visible.
        clearDetailOptions();

        // declare button that lets user select cycle type
        Button cycleTypeSetter = (Button) findViewById(R.id.setCycleButton);
        cycleTypeSetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(taskCreationActivityRef, TaskCycleSelectionActivity.class), SET_CYCLE_TYPE_REQ);
            }
        });

        // declare button that lets user select applicable members
        selectUsersButton = (Button) findViewById(R.id.selectApplicableMembers);
        selectUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = ((Spinner)findViewById(R.id.spinner)).getSelectedItemPosition();
                Group group = usersGroups.get(pos);
                openUserSelectionWithGroup(group);
            }
        });

        // declare button that lets us finish task creation
        Button finishButton = (Button) findViewById(R.id.button_finish);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // fetch task title
                String title = taskTitle.getText().toString();
                // fetch task cateogry
                String category = ((EditText) findViewById(R.id.CategoryEntry)).getText().toString();
                // fetch task description
                EditText descriptionDesc = findViewById(R.id.DescEntry);
                String description = descriptionDesc.getText().toString();

                // ensure task is ready and fully populated
                if (!checkStatus()) {
                    return;
                }

                // fetch applicable group
                int pos = ((Spinner)findViewById(R.id.spinner)).getSelectedItemPosition();
                Group group = usersGroups.get(pos);

                // fetch cycle information from UI and turn it into a Cycle object
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
                // if task is forever, set end date to be start date.
                if (cycleType == 0 || isTaskForever) {
                    // End date is ignored with forever tasks, so set to same day.
                    endDate = startDate;
                }
                // create task that doesn't have an associated cost
                if (!taskHasCost) {
                    if (usersGroups.size() > 0) {
                        Log.d("TaskCreate-newMastrTask", activeUsers.size() + activeUsers.get(0));
                        newMasterTask = new StoredMasterTask(title, description, category, startDate, endDate, group, activeUsers, false, false, 123, cycle, isTaskForever);
                        newMasterTask.addToDatabase();
                    }
                }
                // create task that has an associated cost
                else {
                    double value = Double.parseDouble(costValueEntry.getText().toString());
                    boolean evenSplit = splitEvenly.isChecked();
                    if (usersGroups.size() > 0) {
                        Log.d("TaskCreate-newMastrTask", activeUsers.size() + activeUsers.get(0));
                        newMasterTask = new StoredMasterTask(title, description, category, startDate, endDate, group, activeUsers, true, evenSplit, value, cycle, isTaskForever);
                        newMasterTask.addToDatabase();
                    }
                }

                // exit task creation
                closeTaskCreator();
            }
        });

        // declare button that cancels task creation
        Button cancelButton = (Button) findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeTaskCreator();
            }
        });
    }

    @Override
    public void onBackPressed() {
        closeTaskCreator();
    }

    private void closeTaskCreator(){
        MainActivity.refreshAll();
        finish();
    }
    // opens a new activity that allows date selection
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
                activeUsers.clear();
                for (User u : AddUserToListActivity.usersSelected)
                    activeUsers.add(u.getUserId());
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
        else if (taskHasCost) {
            String t = costValueEntry.getText().toString();
            if (t.length() == 0 || Double.parseDouble(t) == 0.0) {
                Toast.makeText(this, "Please enter a non-zero payment", Toast.LENGTH_LONG).show();
                return false;
            }
            if (!perPerson.isChecked() && !splitEvenly.isChecked()) {
                Toast.makeText(this, "Please select a cost splitting method", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    void openUserSelectionWithGroup(Group g) {
        selectUsersButton.setFocusable(false);
        selectUsersButton.setClickable(false);
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
                        Log.d("TaskCreate-MembrSelect", groupMember.getKey());

                        AddUserToListActivity.usersAvail.add(new User(groupMember.getKey(), (String) groupMember.getValue()));
                    }
                    startActivityForResult(new Intent(taskCreationActivityRef, AddUserToListActivity.class), SET_MEMBERS_REQ);
                    selectUsersButton.setFocusable(true);
                    selectUsersButton.setClickable(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    // this function ensures that the UI for cycle configuration is properly initialized
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
    // updates UI for a particular cycle type
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

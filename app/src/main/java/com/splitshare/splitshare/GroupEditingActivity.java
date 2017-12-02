package com.splitshare.splitshare;

/**
 * Created by Duncan on 11/20/17.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class GroupEditingActivity extends AppCompatActivity {
    private Button finishButton;
    private Button addUserButton;
    private Button removeUserButton;
    private Button setAgendaButton;
    public static Activity groupEditingActivityRef;
    public static Group forEditing;
    public List<User> activeUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_editing_activity);
        groupEditingActivityRef = this;

        activeUsers = forEditing.getUsers();

        TextView editingGroupX = findViewById(R.id.editing_groupx);
        editingGroupX.append(forEditing.getGroupName());

        finishButton = (Button) findViewById(R.id.button_finish);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeGroupCreator();
            }
        });

        addUserButton = (Button) findViewById(R.id.addUserButton);
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText newUserText = findViewById(R.id.addUserField);
                String newUserString = newUserText.getText().toString();
                forEditing.completeAddMember(newUserString);

                closeGroupCreator();
            }
        });

        removeUserButton = (Button) findViewById(R.id.removeUserButton);
        removeUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText newUserText = findViewById(R.id.removeUserField);
                String newUserString = newUserText.getText().toString();
                forEditing.removeMember(newUserString);

                closeGroupCreator();
            }
        });

        setAgendaButton = (Button) findViewById(R.id.setAgendaButton);
        setAgendaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskPopulation.filterGroup = forEditing;
                MainActivity.scheduleUIupdate();
                closeGroupCreator();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeGroupCreator();
            }
        });
    }

    @Override
    public void onBackPressed() {
        closeGroupCreator();
    }

    private void closeGroupCreator(){
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to

    }
}

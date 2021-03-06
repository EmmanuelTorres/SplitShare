package com.splitshare.splitshare;

/**
 * Created by Duncan on 11/17/17.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static com.splitshare.splitshare.SplitShareApp.usersGroups;

public class GroupCreationActivity extends AppCompatActivity {
    private Button finishButton;
    public List<User> activeUsers = null;
    public static Activity groupCreationActivityRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_group_activity);
        groupCreationActivityRef = this;

        finishButton = (Button) findViewById(R.id.button_finish);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText titleDesc = findViewById(R.id.TitleEntry);
                String title = titleDesc.getText().toString();

//                EditText descriptionDesc = findViewById(R.id.DescEntry);
//                String description = descriptionDesc.getText().toString();

                Group newGroup = new Group(title);
                newGroup.createGroup();

//                if (usersGroups.size() > 0) {
//
//                }

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

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
//                EditText titleDesc = findViewById(R.id.TitleEntry);
//                String title = titleDesc.getText().toString();

//                EditText descriptionDesc = findViewById(R.id.DescEntry);
//                String description = descriptionDesc.getText().toString();


                EditText newUserText = findViewById(R.id.addUserField);
                String newUserString = newUserText.getText().toString();

                forEditing.addMember(newUserString,newUserString); //TODO: Figure out what the second argument is supposed to be

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

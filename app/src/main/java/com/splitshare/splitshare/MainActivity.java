package com.splitshare.splitshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.button;
import static com.splitshare.splitshare.R.id.button_finish;
import static com.splitshare.splitshare.R.id.fab;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public static List<Task> mainTaskList = new ArrayList<Task>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainactivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Establishes a global connection to the
        SplitShareApp.firebaseDatabase = FirebaseDatabase.getInstance();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openTaskCreator();

//                // Establishes a reference to the user account
//                DatabaseReference accountReference =
//                        SplitShareApp.firebaseDatabase.getReference("users/" + SplitShareApp.acct.getEmail());
//
//                // Creates a SplitShareUser object to interact with the database to make
//                // accounts, etc
//                SplitShareUser splitShareUser = new SplitShareUser(accountReference);
//
//                splitShareUser.createAccount();
//
//                Group group = new Group("3", "TestGroup", null);
//                // Creates a group with ourselves as the sole member
//                group.createGroup();
//                // Adds ourselves
//                group.addMember(splitShareUser.getAccountId());
//                group.addMember("6969");
//
//                splitShareUser.getGroups();
//
//                Snackbar.make(view, "Attempting to add default task to database", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                // Finds a member in the above Group
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



//        mainTaskList.add(new Task());
//        mainTaskList.add(new Task());
//        mainTaskList.add(new Task());
        ListAdapter taskViewList = new TaskAdapter(this, mainTaskList);
        ListView mainTaskView = (ListView) findViewById(R.id.mainListView);
        mainTaskView.setAdapter(taskViewList);
    }

    private void openTaskCreator() {
        startActivity(new Intent(this, TaskCreationActivity.class));
    }

    public static void addToTaskList(Task inputTask)
    {
        mainTaskList.add(inputTask);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.groups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        SplitShareApp.mAuth.addAuthStateListener(SplitShareApp.mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (SplitShareApp.mAuthListener != null) {
            SplitShareApp.mAuth.removeAuthStateListener(SplitShareApp.mAuthListener);
        }
    }
}

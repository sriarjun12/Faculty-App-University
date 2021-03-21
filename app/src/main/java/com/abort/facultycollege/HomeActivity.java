package com.abort.facultycollege;

import android.os.Bundle;

import com.abort.facultycollege.EventBus.AttendenceClick;
import com.abort.facultycollege.EventBus.NotesClick;
import com.abort.facultycollege.EventBus.ProfileClick;
import com.abort.facultycollege.EventBus.StudentClick;
import com.abort.facultycollege.EventBus.TimeTableClick;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class HomeActivity extends AppCompatActivity {
    NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }
    @Subscribe(sticky=true,threadMode= ThreadMode.MAIN)
    public void onNotesCLick(NotesClick event){
        if(event.isSuccess())
        {


            navController.navigate(R.id.nav_notes);



        }
    }
    @Subscribe(sticky=true,threadMode= ThreadMode.MAIN)
    public void onAttendenceCLick(AttendenceClick event){
        if(event.isSuccess())
        {

            //navController.navigate(R.id.nav_details);


        }
    }
    @Subscribe(sticky=true,threadMode= ThreadMode.MAIN)
    public void onProfileCLick(ProfileClick event){
        if(event.isSuccess())
        {

            navController.navigate(R.id.nav_profile);


        }
    }
    @Subscribe(sticky=true,threadMode= ThreadMode.MAIN)
    public void onTimeTableCLick(TimeTableClick event){
        if(event.isSuccess())
        {

            navController.navigate(R.id.nav_timetable);


        }
    }
    @Subscribe(sticky=true,threadMode= ThreadMode.MAIN)
    public void onStudentCLick(StudentClick event){
        if(event.isSuccess())
        {

            navController.navigate(R.id.nav_studentdetails);


        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    protected void onStop() {
        EventBus.getDefault().removeAllStickyEvents(); // Fix event bus always called after onActivityResult
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
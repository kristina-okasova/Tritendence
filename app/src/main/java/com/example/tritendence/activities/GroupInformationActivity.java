package com.example.tritendence.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.tritendence.R;
import com.example.tritendence.fragments.GroupInformationFragment;
import com.example.tritendence.model.LoadData;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.groups.Group;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GroupInformationActivity extends AppCompatActivity {
    //Intent's extras
    private TriathlonClub club;
    private LoadData loadData;
    private String signedUser, sportSelection, theme;

    private GroupInformationFragment groupInformationFragment;
    private BottomNavigationView navigation;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Setting user's theme.
        this.theme = getIntent().getExtras().getString(getString(R.string.THEME_EXTRA));
        switch(this.theme) {
            case "DarkRed":
                setTheme(R.style.Theme_DarkRed);
                break;
            case "DarkBlue":
                setTheme(R.style.Theme_DarkBlue);
                break;
            case "LightRed":
                setTheme(R.style.Theme_LightRed);
                break;
            case "LightBlue":
                setTheme(R.style.Theme_LightBlue);
                break;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_information);

        //Getting extras of the intent.
        this.club = (TriathlonClub) getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        this.loadData = (LoadData) getIntent().getExtras().getSerializable(getString(R.string.LOAD_DATA_EXTRA));
        this.signedUser = getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
        this.sportSelection = getIntent().getExtras().getString(getString(R.string.SPORT_SELECTION_EXTRA));

        //Setting currently selected navigation item and navigation listener.
        this.navigation = findViewById(R.id.bottomNavigationView);
        this.findTypeOfSignedUser();

        this.navigation.setSelectedItemId(R.id.groupsFragment);
        this.navigation.setOnNavigationItemSelectedListener(navigationListener);

        //Attaching fragment to the activity.
        this.groupInformationFragment = new GroupInformationFragment(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.groupFragmentContainerView, this.groupInformationFragment).commit();
    }

    private void findTypeOfSignedUser() {
        if (this.club.getAdminOfClub().getFullName().equals(signedUser)) {
            this.navigation.getMenu().clear();
            this.navigation.inflateMenu(R.menu.home_bottom_menu_admin);
        }
        else {
            this.navigation.getMenu().clear();
            this.navigation.inflateMenu(R.menu.home_bottom_menu);
        }
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            item -> {
                //Creating new intent of Home Activity as part of navigation listener.
                Intent homePage = new Intent(this, HomeActivity.class);
                homePage.putExtra(getString(R.string.SIGNED_USER_EXTRA), signedUser);
                homePage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), club);
                homePage.putExtra(getString(R.string.LOAD_DATA_EXTRA), this.loadData);
                homePage.putExtra(getString(R.string.SPORT_SELECTION_EXTRA), this.sportSelection);
                homePage.putExtra(getString(R.string.THEME_EXTRA), this.theme);
                homePage.putExtra(getString(R.string.SELECTED_FRAGMENT_EXTRA), item.getItemId());
                startActivity(homePage);
                finish();
                return true;
            };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void showMembersOfGroup(View view) {
        this.groupInformationFragment.showMembersOfGroupInFragment();
    }

    public void deleteGroup(View view) {
        //Showing alert box to confirm deletion of the group.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(getString(R.string.DELETE_GROUP));
        builder.setMessage(getString(R.string.DELETE_GROUP_QUESTION));

        //Deleting athlete when confirmed. Closing alert box when denied.
        builder.setPositiveButton(getString(R.string.YES),
                (dialog, which) -> this.groupInformationFragment.deleteGroup());
        builder.setNegativeButton(getString(R.string.NO), (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void editGroup(View view) {
        Group editGroup = this.groupInformationFragment.findSelectedGroup();

        //Creating intent of Add Group Activity to edit selected group.
        Intent addGroupPage = new Intent(this, AddGroupActivity.class);
        addGroupPage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        addGroupPage.putExtra(getString(R.string.SIGNED_USER_EXTRA), signedUser);
        addGroupPage.putExtra(getString(R.string.LOAD_DATA_EXTRA), loadData);
        addGroupPage.putExtra(getString(R.string.SPORT_SELECTION_EXTRA), this.sportSelection);
        addGroupPage.putExtra(getString(R.string.THEME_EXTRA), this.theme);
        addGroupPage.putExtra(getString(R.string.EDIT_GROUP_EXTRA), editGroup);
        startActivity(addGroupPage);
        finish();
    }
}
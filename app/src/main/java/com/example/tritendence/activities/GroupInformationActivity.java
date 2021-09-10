package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.tritendence.R;
import com.example.tritendence.fragments.GroupInformationFragment;
import com.example.tritendence.model.LoadData;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.groups.Group;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GroupInformationActivity extends AppCompatActivity {
    private GroupInformationFragment groupInformationFragment;
    private TriathlonClub club;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_information);

        this.club = (TriathlonClub) getIntent().getExtras().getSerializable(getString(R.string.TRIATHLON_CLUB_EXTRA));
        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setSelectedItemId(R.id.groupsFragment);
        navigation.setOnNavigationItemSelectedListener(navigationListener);
        this.groupInformationFragment = new GroupInformationFragment(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.groupFragmentContainerView, this.groupInformationFragment).commit();
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            item -> {
                String signedUser = getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
                Intent homePage = new Intent(this, HomeActivity.class);
                homePage.putExtra(getString(R.string.SIGNED_USER_EXTRA), signedUser);
                homePage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), club);
                homePage.putExtra(getString(R.string.SELECTED_FRAGMENT_EXTRA), item.getItemId());
                startActivity(homePage);
                finish();
                return true;
            };

    public void showMembersOfGroup(View view) {
        this.groupInformationFragment.showMembersOfGroupInFragment();
    }

    public void deleteGroup(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(getString(R.string.DELETE_GROUP));
        builder.setMessage(getString(R.string.DELETE_GROUP_QUESTION));

        builder.setPositiveButton(getString(R.string.YES),
                (dialog, which) -> this.groupInformationFragment.deleteGroup());
        builder.setNegativeButton(getString(R.string.NO), (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void editGroup(View view) {
        Group editGroup = this.groupInformationFragment.findSelectedGroup();
        String signedUser = getIntent().getExtras().getString(getString(R.string.SIGNED_USER_EXTRA));
        LoadData loadData = (LoadData) getIntent().getExtras().getSerializable(getString(R.string.LOAD_DATA_EXTRA));
        Intent addGroupPage = new Intent(this, AddGroupActivity.class);
        addGroupPage.putExtra(getString(R.string.TRIATHLON_CLUB_EXTRA), this.club);
        addGroupPage.putExtra(getString(R.string.SIGNED_USER_EXTRA), signedUser);
        addGroupPage.putExtra(getString(R.string.LOAD_DATA_EXTRA), loadData);
        addGroupPage.putExtra(getString(R.string.EDIT_GROUP_EXTRA), editGroup);
        startActivity(addGroupPage);
        finish();
    }

    public void notifyAboutChange(TriathlonClub club) {
        this.club = club;
    }
}
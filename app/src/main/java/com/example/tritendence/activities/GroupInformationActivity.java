package com.example.tritendence.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import com.example.tritendence.R;
import com.example.tritendence.fragments.AthletesFragment;
import com.example.tritendence.fragments.AttendanceFragment;
import com.example.tritendence.fragments.GroupInformationFragment;
import com.example.tritendence.fragments.GroupsFragment;
import com.example.tritendence.fragments.ProfileFragment;
import com.example.tritendence.model.TriathlonClub;
import com.example.tritendence.model.groups.Group;
import com.example.tritendence.model.users.Athlete;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GroupInformationActivity extends AppCompatActivity {
    private GroupInformationFragment groupInformationFragment;
    private TriathlonClub club;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_information);

        this.club = (TriathlonClub) getIntent().getExtras().getSerializable("TRIATHLON_CLUB");
        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setSelectedItemId(R.id.groupsFragment);
        navigation.setOnNavigationItemSelectedListener(navigationListener);
        this.groupInformationFragment = new GroupInformationFragment(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.groupFragmentContainerView, this.groupInformationFragment).commit();
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            item -> {
                String signedUser = getIntent().getExtras().getString("SIGNED_USER");
                Intent homePage = new Intent(this, HomeActivity.class);
                homePage.putExtra("SIGNED_USER", signedUser);
                homePage.putExtra("TRIATHLON_CLUB", club);
                homePage.putExtra("SELECTED_FRAGMENT", item.getItemId());
                startActivity(homePage);
                finish();
                return true;
            };

    public void showMembersOfGroup(View view) {
        this.groupInformationFragment.showMembersOfGroupInFragment(view);
    }

    public void deleteGroup(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Odstránenie skupiny");
        builder.setMessage("Ste si istí, že chcete odstrániť danú skupinu?");

        builder.setPositiveButton("Áno",
                (dialog, which) -> this.groupInformationFragment.deleteGroup());
        builder.setNegativeButton("Nie", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void editGroup(View view) {
        Group editGroup = this.groupInformationFragment.findSelectedGroup();
        String signedUser = getIntent().getExtras().getString("SIGNED_USER");
        Intent addGroupPage = new Intent(this, AddGroupActivity.class);
        addGroupPage.putExtra("TRIATHLON_CLUB", this.club);
        addGroupPage.putExtra("SIGNED_USER", signedUser);
        addGroupPage.putExtra("EDIT_GROUP", editGroup);
        startActivity(addGroupPage);
        finish();
    }
}
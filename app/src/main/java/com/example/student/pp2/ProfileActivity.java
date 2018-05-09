package com.example.student.pp2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textViewUsername, textViewUserEmail,
            textViewNames, textViewID;
    private Button buttonGoToCreateRepair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, StartActivity.class));
        }

        textViewUsername = (TextView) findViewById(R.id.textViewUserName);
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        textViewNames = (TextView) findViewById(R.id.textViewNames);
        textViewID = (TextView) findViewById(R.id.textViewID);

        buttonGoToCreateRepair = (Button) findViewById(R.id.buttonGoToCreateRepair);
        buttonGoToCreateRepair.setOnClickListener(this);

        String isAdmin = "NIE";
        if (SharedPrefManager.getInstance(this).isAdmin() == 1) {
            isAdmin = "TAK";
        }
        textViewID.setText("ID: " + SharedPrefManager.getInstance(this).getUserID()
                + "\nAdministrator: " + isAdmin
                + "\nPunkty bonusowe: " + SharedPrefManager.getInstance(this).getBonus());

        textViewUsername.setText(SharedPrefManager.getInstance(this).getUsername());
        textViewUserEmail.setText(SharedPrefManager.getInstance(this).getUserEmail());
        textViewNames.setText(SharedPrefManager.getInstance(this).getUserName()
                + " " + SharedPrefManager.getInstance(this).getUserSurname());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (SharedPrefManager.getInstance(this).isAdmin() == 1) {
            getMenuInflater().inflate(R.menu.adminmenu, menu);
        } else {
            getMenuInflater().inflate(R.menu.usermenu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuAllRepairs:
                startActivity(new Intent(this, AllRepairsActivity.class));
                break;
            case R.id.menuUserRepairs:
                startActivity(new Intent(this, UserRepairsActivity.class));
                break;
            case R.id.menuSettings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.menuLogout:
                SharedPrefManager.getInstance(this).logout();
                finish();
                startActivity(new Intent(this, StartActivity.class));
                break;
            case R.id.menuExit:
                this.finishAffinity();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view == buttonGoToCreateRepair) {
            startActivity(new Intent(getApplicationContext(), CreateRepairActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

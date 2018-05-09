package com.example.student.pp2;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditRepairActivity extends AppCompatActivity {

    private EditText editTextRepairName, editTextRepairDetails;

    private Button buttonSaveChanges, buttonDeleteRepair, buttonBackToRepairList;

    String id, msg = "null";

    // Progress Dialog
    private ProgressDialog progressDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // single repair url
    private static final String url_repair_details = Constants.URL_GET_REPAIR_DETAILS;

    // url to update repair
    private static final String url_update_repair = Constants.URL_UPDATE_REPAIR;

    // url to delete repair
    private static final String url_delete_repair = Constants.URL_DELETE_REPAIR;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_REPAIR = "repair";
    private static final String TAG_RID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_DETAILS = "details";
    private static final String TAG_MESSAGE = "message";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_repair);

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, StartActivity.class));
        }

        // buttons
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges);
        buttonDeleteRepair = findViewById(R.id.buttonDeleteRepair);
        buttonBackToRepairList = findViewById(R.id.buttonBackToRepairList);

        // getting repair details from intent
        Intent i = getIntent();

        // getting repair id (id) from intent
        id = i.getStringExtra(TAG_RID);

        // Getting complete repair details in background thread
        new GetRepairDetails().execute();

        // save button click event
        buttonSaveChanges.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // starting background task to update repair
                new SaveRepairDetails().execute();
            }
        });

        // Delete button click event
        buttonDeleteRepair.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // deleting repair in background thread
                new DeleteRepair().execute();
            }
        });

        buttonBackToRepairList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                if (SharedPrefManager.getInstance(getApplicationContext()).isAdmin() == 1) {
                    startActivity(new Intent(getApplicationContext(), AllRepairsActivity.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), UserRepairsActivity.class));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        if (SharedPrefManager.getInstance(getApplicationContext()).isAdmin() == 1) {
            startActivity(new Intent(getApplicationContext(), AllRepairsActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), UserRepairsActivity.class));
        }
    }

    /**
     * Background Async Task to Get complete repair details
     */
    class GetRepairDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(EditRepairActivity.this);
            progressDialog.setMessage("Wczytywanie szczegółów naprawy. Proszę czekać...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        /**
         * Getting repair details in background thread
         */
        protected String doInBackground(String... params) {

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    // Check for success tag
                    int success;
                    try {
                        // Building Parameters
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("id", id));

                        // getting repair details by making HTTP request
                        // Note that repair details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(
                                url_repair_details, "POST", params);

                        // check your log for json response
                        Log.e("Szczegóły naprawy", json.toString());

                        // json success tag
                        success = json.getInt(TAG_SUCCESS);

                        if (success == 1) {
                            // successfully received repair details
                            JSONArray repairObj = json
                                    .getJSONArray(TAG_REPAIR); // JSON Array

                            // get first repair object from JSON Array
                            JSONObject repair = repairObj.getJSONObject(0);

                            // repair with this id found
                            // Edit Text
                            editTextRepairName = findViewById(R.id.editTextRepairName);
                            editTextRepairDetails = findViewById(R.id.editTextRepairDetails);

                            // display repair data in EditText
                            editTextRepairName.setText(repair.getString(TAG_NAME));
                            editTextRepairDetails.setText(repair.getString(TAG_DETAILS));

                            if (SharedPrefManager.getInstance(getApplicationContext()).isAdmin() == 0) {
                                //buttonSaveChanges.setEnabled(false);
                                //buttonDeleteRepair.setEnabled(false);
                                buttonSaveChanges.setVisibility(View.GONE);
                                buttonDeleteRepair.setVisibility(View.GONE);
                                editTextRepairName.setEnabled(false);
                                editTextRepairDetails.setEnabled(false);
                                editTextRepairName.setTextColor(Color.parseColor("#000000"));
                                editTextRepairDetails.setTextColor(Color.parseColor("#000000"));
                            }

                        } else {
                            // repair with id not found
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            progressDialog.dismiss();
        }
    }

    /**
     * Background Async Task to  Save repair Details
     */
    class SaveRepairDetails extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(EditRepairActivity.this);
            progressDialog.setMessage("Zapisywanie naprawy...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        /**
         * Saving repair
         */
        protected String doInBackground(String... args) {

            // getting updated data from EditTexts
            String name = editTextRepairName.getText().toString();
            String details = editTextRepairDetails.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_RID, id));
            params.add(new BasicNameValuePair(TAG_NAME, name));
            params.add(new BasicNameValuePair(TAG_DETAILS, details));

            // sending modified data through http request
            // Notice that update repair url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_repair,
                    "POST", params);

            // check json success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
                msg = json.getString(TAG_MESSAGE);

                if (success == 1) {
                    // successfully updated
                    Intent i = getIntent();
                    // send result code 100 to notify about repair update
                    setResult(100, i);
                    finish();
                } else {
                    // failed to update repair
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once repair uupdated
            progressDialog.dismiss();
        }
    }

    /*****************************************************************
     * Background Async Task to Delete repair
     * */
    class DeleteRepair extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(EditRepairActivity.this);
            progressDialog.setMessage("Usuwanie naprawy...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        /**
         * Deleting repair
         */
        protected String doInBackground(String... args) {

            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id", id));

                // getting repair details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        url_delete_repair, "POST", params);

                // check your log for json response
                Log.d("Usunięta naprawa", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                msg = json.getString(TAG_MESSAGE);

                if (success == 1) {
                    // repair successfully deleted
                    // notify previous activity by sending code 100
                    Intent i = getIntent();
                    // send result code 100 to notify about repair deletion
                    setResult(100, i);
                    finish();
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once repair deleted
            progressDialog.dismiss();

        }

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
}

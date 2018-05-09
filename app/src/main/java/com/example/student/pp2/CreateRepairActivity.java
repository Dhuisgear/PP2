package com.example.student.pp2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateRepairActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextUserID, editTextRepairName, editTextRepairDetails;

    private Button buttonCreateRepair;

    private ProgressDialog progressDialog;

    JSONParser jsonParser = new JSONParser();

    private static String url_create_repair = Constants.URL_CREATE_REPAIR;
    private static String url_all_users = Constants.URL_GET_ALL_USERS;

    private static final String TAG_RESPONSE = "error";
    private static final String TAG_MESSAGE = "message";

    String msg = "null";

    Integer userID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_repair);

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, StartActivity.class));
        }

        editTextUserID = (EditText) findViewById(R.id.editTextUserID);
        editTextRepairName = (EditText) findViewById(R.id.editTextRepairName);
        editTextRepairDetails = (EditText) findViewById(R.id.editTextRepairDetails);

        if (SharedPrefManager.getInstance(getApplicationContext()).isAdmin() == 0) {
            userID = SharedPrefManager.getInstance(getApplicationContext()).getUserID();
            editTextUserID.setText(userID.toString());
            editTextUserID.setEnabled(false);
            Log.e("TAG", userID.toString());
        } else {
            editTextUserID.setText("0");
        }

        buttonCreateRepair = (Button) findViewById(R.id.buttonCreateRepair);
        buttonCreateRepair.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View view) {
        //createRepair();
        new CreateNewRepair().execute();
    }

    private void createRepair() {
        final Integer userID = Integer.parseInt(editTextUserID.getText().toString().trim());
        final String repairName = editTextRepairName.getText().toString().trim();
        final String repairDetails = editTextRepairDetails.getText().toString().trim();

        progressDialog.setMessage("Trwa dodawanie naprawy...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_CREATE_REPAIR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(),
                                    jsonObject.getString("message"),
                                    Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(getApplicationContext(), error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userID", userID.toString());
                params.put("name", repairName);
                params.put("details", repairDetails);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    class CreateNewRepair extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CreateRepairActivity.this);
            progressDialog.setMessage("Dodawanie naprawy..");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        /**
         * Creating repair
         */
        protected String doInBackground(String... args) {
            //editTextUserID.setText("");

            userID = Integer.parseInt(editTextUserID.getText().toString().trim());

            final String repairName = editTextRepairName.getText().toString().trim();
            final String repairDetails = editTextRepairDetails.getText().toString().trim();

            Log.e("TEST", "uid: " + userID + ", name: " + repairName + ", details: " + repairDetails);

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userID", userID.toString()));
            params.add(new BasicNameValuePair("name", repairName));
            params.add(new BasicNameValuePair("details", repairDetails));

            // getting JSON Object
            // Note that create repair url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_repair,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
//                int success = json.getInt(TAG_RESPONSE);

                msg = json.getString(TAG_MESSAGE);

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        Log.e("TAG", msg);
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
            // dismiss the dialog once done
            progressDialog.dismiss();

            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
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


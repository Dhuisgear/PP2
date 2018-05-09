package com.example.student.pp2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class UserRepairsActivity extends ListActivity {

    // Progress Dialog
    private ProgressDialog progressDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> repairsList;

    // url to get all repairs list
    private static String url_user_repairs = Constants.URL_GET_USER_REPAIRS;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_REPAIRS = "repairs";
    private static final String TAG_RID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_DETAILS = "details";
    private static final String TAG_MESSAGE = "message";

    // repairs JSONArray
    JSONArray repairs = null;

    Integer userID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_repairs);

        userID = SharedPrefManager.getInstance(this).getUserID();

        // Hashmap for ListView
        repairsList = new ArrayList<HashMap<String, String>>();

        // Loading repairs in Background Thread
        new LoadUserRepairs().execute();

        // Get listview
        ListView lv = getListView();

        // on seleting single repair
        // launching Edit repair Screen
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String rid = ((TextView) view.findViewById(R.id.pid)).getText()
                        .toString();

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        EditRepairActivity.class);
                // sending pid to next activity
                in.putExtra(TAG_RID, rid);

                // starting new activity and expecting some response back
                finish();
                startActivityForResult(in, 100);
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
    }

    /**
     * Background Async Task to Load all repair by making HTTP Request
     */
    class LoadUserRepairs extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UserRepairsActivity.this);
            progressDialog.setMessage("Wczytywanie napraw. Proszę czekać...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
//            progressDialog.show();
        }

        /**
         * getting All repairs from url
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("userID", userID.toString()));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_user_repairs, "POST", params);

            // Check your log cat for JSON reponse
//            Log.d("Wszystkie naprawy: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // repairs found
                    // Getting Array of repairs
                    repairs = json.getJSONArray(TAG_REPAIRS);

                    // looping through All repairs
                    for (int i = 0; i < repairs.length(); i++) {
                        JSONObject c = repairs.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_RID);
                        String name = c.getString(TAG_NAME);
                        //String details = c.getString(TAG_DETAILS);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_RID, id);
                        map.put(TAG_NAME, name);
                        //map.put(TAG_DETAILS, details);

                        // adding HashList to ArrayList
                        repairsList.add(map);
                    }
                } else {
                    // no repairs found
                    // Launch Add New repair Activity
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Brak napraw do wyświetlenia", Toast.LENGTH_LONG).show();
                        }
                    });

                    Intent i = new Intent(getApplicationContext(),
                            ProfileActivity.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all repairs
            progressDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            UserRepairsActivity.this, repairsList,
                            R.layout.list_item, new String[]{TAG_RID,
                            TAG_NAME},
                            new int[]{R.id.pid, R.id.name});
                    // updating listview
                    setListAdapter(adapter);
                }
            });

        }

    }
}

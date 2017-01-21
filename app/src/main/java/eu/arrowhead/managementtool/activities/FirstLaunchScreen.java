package eu.arrowhead.managementtool.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import eu.arrowhead.managementtool.R;
import eu.arrowhead.managementtool.utility.Utility;
import eu.arrowhead.managementtool.volley.Networking;

public class FirstLaunchScreen extends AppCompatActivity {

    private EditText apiAddress, srAddress, keystorePath;
    private Button browseButton, confirmButton;
    private SharedPreferences prefs;

    private static final int OPEN_FILE_REQUEST = 0;
    private static String API_URL;
    private static String SR_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        apiAddress = (EditText) findViewById(R.id.api_uri_input);
        srAddress = (EditText) findViewById(R.id.sr_uri_input);
        //keystorePath = (EditText) findViewById(R.id.keystore_input);
        //browseButton = (Button) findViewById(R.id.browse_button);
        confirmButton = (Button) findViewById(R.id.confirm_button);

        prefs = this.getSharedPreferences("eu.arrowhead.managementtool", Context.MODE_PRIVATE);

        /*browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
                intent.setDataAndType(uri, "file/*");
                startActivityForResult(Intent.createChooser(intent, "Choose keystore file"), OPEN_FILE_REQUEST);
            }
        });*/

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                API_URL = apiAddress.getText().toString();
                if(API_URL.isEmpty()){
                    Toast.makeText(FirstLaunchScreen.this, R.string.api_address_warning, Toast.LENGTH_LONG).show();
                }
                else{
                    validateAPI_URL();
                }

                SR_URL = srAddress.getText().toString();
                if(SR_URL.isEmpty()){
                    Toast.makeText(FirstLaunchScreen.this, R.string.sr_address_warning, Toast.LENGTH_LONG).show();
                }
                else{
                    validateSR_URL();
                }
            }
        });
    }

    public void validateAPI_URL(){
        String testURL = Uri.parse(API_URL).buildUpon().appendPath("common").build().toString();
        if (Utility.isConnected(this)) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, testURL,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            if(response.equals("Got it!")){
                                prefs.edit().putString("api_address", API_URL).apply();
                                if(!prefs.getString("sr_address", "").isEmpty()){
                                    prefs.edit().putBoolean("not_first_launch", true).apply();
                                    finish();
                                }
                            }
                            else{
                                Toast.makeText(FirstLaunchScreen.this, R.string.api_address_warning, Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(FirstLaunchScreen.this, R.string.api_address_warning, Toast.LENGTH_LONG).show();
                        }
                    });

            Networking.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Utility.showNoConnectionToast(FirstLaunchScreen.this);
        }
    }

    public void validateSR_URL() {
        if (Utility.isConnected(this)) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, SR_URL,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            if(response.equals("This is the Service Registry.")){
                                prefs.edit().putString("sr_address", SR_URL).apply();
                                if(!prefs.getString("api_address", "").isEmpty()){
                                    prefs.edit().putBoolean("not_first_launch", true).apply();
                                    finish();
                                }
                            }
                            else{
                                Toast.makeText(FirstLaunchScreen.this, R.string.sr_address_warning, Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(FirstLaunchScreen.this, R.string.sr_address_warning, Toast.LENGTH_LONG).show();
                        }
                    });

            Networking.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Utility.showNoConnectionToast(FirstLaunchScreen.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                keystorePath.setText(uri.getPath());
                keystorePath.setSelection(keystorePath.getText().toString().length());
            }
            if (resultCode == RESULT_CANCELED) {
                //handle cancel
            }
        }
    }

}

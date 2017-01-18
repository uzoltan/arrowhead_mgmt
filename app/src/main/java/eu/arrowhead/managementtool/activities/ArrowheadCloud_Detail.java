package eu.arrowhead.managementtool.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

import eu.arrowhead.managementtool.R;
import eu.arrowhead.managementtool.fragments.ConfirmDeleteDialog;
import eu.arrowhead.managementtool.model.ArrowheadCloud;
import eu.arrowhead.managementtool.utility.Utility;
import eu.arrowhead.managementtool.volley.JsonArrayRequest;
import eu.arrowhead.managementtool.volley.JsonObjectRequest;
import eu.arrowhead.managementtool.volley.Networking;

public class ArrowheadCloud_Detail extends AppCompatActivity implements
        ConfirmDeleteDialog.ConfirmDeleteListener{

    private CoordinatorLayout rootView;
    private Button saveButton;
    public TextView operatorTv, cloudNameTv, addressTv, portTv, serviceUriTv, authInfoTv;
    private EditText operatorEt, cloudNameEt, addressEt, portEt, serviceUriEt, authInfoEt;
    private ViewSwitcher operatorSwitcher, cloudNameSwitcher, addressSwitcher, portSwitcher, serviceUriSwitcher, authInfoSwitcher;

    private static String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrowhead_cloud__detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.cloud_toolbar);
        setSupportActionBar(toolbar);
        //It will not be null ever, since we use a Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences prefs = getSharedPreferences("eu.arrowhead.managementtool", Context.MODE_PRIVATE);
        URL = Uri.parse(prefs.getString("api_address", null)).buildUpon()
                .appendPath("common").appendPath("clouds").build().toString();

        rootView = (CoordinatorLayout) findViewById(R.id.cloud_detail_root_view);
        saveButton = (Button) findViewById(R.id.save_changes_button);
        operatorSwitcher = (ViewSwitcher) findViewById(R.id.operator_switcher);
        operatorTv = (TextView) findViewById(R.id.operator_textview);
        operatorEt = (EditText) findViewById(R.id.operator_edittext);
        cloudNameSwitcher = (ViewSwitcher) findViewById(R.id.cloud_name_switcher);
        cloudNameTv = (TextView) findViewById(R.id.cloud_name_textview);
        cloudNameEt = (EditText) findViewById(R.id.cloud_name_edittext);
        addressSwitcher = (ViewSwitcher) findViewById(R.id.address_switcher);
        addressTv = (TextView) findViewById(R.id.address_textview);
        addressEt = (EditText) findViewById(R.id.address_edittext);
        portSwitcher = (ViewSwitcher) findViewById(R.id.port_switcher);
        portTv = (TextView) findViewById(R.id.port_textview);
        portEt = (EditText) findViewById(R.id.port_edittext);
        serviceUriSwitcher = (ViewSwitcher) findViewById(R.id.gatekeeper_service_uri_switcher);
        serviceUriTv = (TextView) findViewById(R.id.gatekeeper_service_uri_textview);
        serviceUriEt = (EditText) findViewById(R.id.gatekeeper_service_uri_edittext);
        authInfoSwitcher = (ViewSwitcher) findViewById(R.id.auth_info_switcher);
        authInfoTv = (TextView) findViewById(R.id.auth_info_textview);
        authInfoEt = (EditText) findViewById(R.id.auth_info_edittext);

        Intent intent = getIntent();
        ArrowheadCloud cloud = (ArrowheadCloud) intent.getSerializableExtra("arrowhead_cloud");
        operatorTv.setText(cloud.getOperator());
        cloudNameTv.setText(cloud.getCloudName());
        addressTv.setText(cloud.getAddress());
        portTv.setText(cloud.getPort());
        serviceUriTv.setText(cloud.getGatekeeperServiceURI());
        authInfoTv.setText(cloud.getAuthenticationInfo());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrowheadCloud cloud = new ArrowheadCloud(operatorEt.getText().toString(), cloudNameEt.getText().toString(),
                        addressEt.getText().toString(), portEt.getText().toString(), serviceUriEt.getText().toString(), authInfoEt.getText().toString());

                if (!operatorEt.getText().toString().equals(operatorTv.getText().toString()) ||
                        !cloudNameEt.getText().toString().equals(cloudNameTv.getText().toString())) {
                    //If the user changes the operator or cloud name, we have to do a delete+post combo
                    sendDeleteRequest(cloud, true);
                } else {
                    sendUpdateRequest(cloud);
                }

                //hides the soft input keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    public void sendUpdateRequest(final ArrowheadCloud cloud) {
        if (Utility.isConnected(ArrowheadCloud_Detail.this)) {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT, URL, Utility.toJsonObject(cloud),
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            addressTv.setText(cloud.getAddress());
                            portTv.setText(cloud.getPort());
                            serviceUriTv.setText(cloud.getGatekeeperServiceURI());
                            authInfoTv.setText(cloud.getAuthenticationInfo());

                            operatorSwitcher.showPrevious();
                            cloudNameSwitcher.showPrevious();
                            addressSwitcher.showPrevious();
                            portSwitcher.showPrevious();
                            serviceUriSwitcher.showPrevious();
                            authInfoSwitcher.showPrevious();

                            Snackbar.make(rootView, R.string.cloud_update_success, Snackbar.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utility.showServerErrorFragment(error, ArrowheadCloud_Detail.this);

                            operatorSwitcher.showPrevious();
                            cloudNameSwitcher.showPrevious();
                            addressSwitcher.showPrevious();
                            portSwitcher.showPrevious();
                            serviceUriSwitcher.showPrevious();
                            authInfoSwitcher.showPrevious();
                        }
                    }
            );

            Networking.getInstance(ArrowheadCloud_Detail.this).addToRequestQueue(jsObjRequest);
            Toast.makeText(ArrowheadCloud_Detail.this, R.string.cloud_update_request, Toast.LENGTH_SHORT).show();
        } else {
            Utility.showNoConnectionSnackbar(rootView);
        }
    }

    public void sendDeleteRequest(final ArrowheadCloud cloud, final boolean forcedUpdate) {
        String deleteURL = URL + "/operator/" + operatorTv.getText() + "/cloudname/" + cloudNameTv.getText();
        if (Utility.isConnected(this)) {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.DELETE, deleteURL, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            if (forcedUpdate) {
                                sendPostRequest(cloud);
                            } else {
                                Toast.makeText(ArrowheadCloud_Detail.this, R.string.deleted, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utility.showServerErrorFragment(error, ArrowheadCloud_Detail.this);

                            if (forcedUpdate) {
                                operatorSwitcher.showPrevious();
                                cloudNameSwitcher.showPrevious();
                                addressSwitcher.showPrevious();
                                portSwitcher.showPrevious();
                                serviceUriSwitcher.showPrevious();
                                authInfoSwitcher.showPrevious();
                            }
                        }
                    }
            );

            Networking.getInstance(this).addToRequestQueue(jsObjRequest);
            if (forcedUpdate) {
                Toast.makeText(ArrowheadCloud_Detail.this, R.string.cloud_update_request, Toast.LENGTH_SHORT).show();
            }
        } else {
            Utility.showNoConnectionSnackbar(rootView);
        }
    }

    public void sendPostRequest(final ArrowheadCloud cloud) {
        List<ArrowheadCloud> cloudList = Collections.singletonList(cloud);
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.POST, URL, Utility.toJsonArray(cloudList),
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        operatorTv.setText(cloud.getOperator());
                        cloudNameTv.setText(cloud.getCloudName());
                        addressTv.setText(cloud.getAddress());
                        portTv.setText(cloud.getPort());
                        serviceUriTv.setText(cloud.getGatekeeperServiceURI());
                        authInfoTv.setText(cloud.getAuthenticationInfo());

                        operatorSwitcher.showPrevious();
                        cloudNameSwitcher.showPrevious();
                        addressSwitcher.showPrevious();
                        portSwitcher.showPrevious();
                        serviceUriSwitcher.showPrevious();
                        authInfoSwitcher.showPrevious();

                        Snackbar.make(rootView, R.string.cloud_update_success, Snackbar.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utility.showServerErrorFragment(error, ArrowheadCloud_Detail.this);

                        operatorSwitcher.showPrevious();
                        cloudNameSwitcher.showPrevious();
                        addressSwitcher.showPrevious();
                        portSwitcher.showPrevious();
                        serviceUriSwitcher.showPrevious();
                        authInfoSwitcher.showPrevious();
                    }
                }
        );

        Networking.getInstance(this).addToRequestQueue(jsArrayRequest);
    }

    @Override
    public void onBackPressed() {
        //Check if the service is being edited right now. (Could use any of the 3 view switchers to check.)
        if (operatorSwitcher.getDisplayedChild() == 1) {
            operatorSwitcher.showPrevious();
            cloudNameSwitcher.showPrevious();
            addressSwitcher.showPrevious();
            portSwitcher.showPrevious();
            serviceUriSwitcher.showPrevious();
            authInfoSwitcher.showPrevious();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.arrowhead_cloud_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit_cloud) {
            operatorEt.setText(operatorTv.getText());
            cloudNameEt.setText(cloudNameTv.getText());
            addressEt.setText(addressTv.getText());
            portEt.setText(portTv.getText());
            serviceUriEt.setText(serviceUriTv.getText());
            authInfoEt.setText(authInfoTv.getText());

            operatorSwitcher.showNext();
            cloudNameSwitcher.showNext();
            addressSwitcher.showNext();
            portSwitcher.showNext();
            serviceUriSwitcher.showNext();
            authInfoSwitcher.showNext();

            //shows the soft input keyboard
            View dummy = ArrowheadCloud_Detail.this.getCurrentFocus();
            if (dummy != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(dummy, 0);
            }
            return true;
        }
        if (id == R.id.action_delete_cloud) {
            DialogFragment newFragment = new ConfirmDeleteDialog();
            newFragment.show(getSupportFragmentManager(), ConfirmDeleteDialog.TAG);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentPositiveClick(DialogFragment dialog) {
        ArrowheadCloud cloud = new ArrowheadCloud(operatorTv.getText().toString(), cloudNameTv.getText().toString(), null, null, null, null);
        sendDeleteRequest(cloud, false);
    }
}

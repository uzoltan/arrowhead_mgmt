package eu.arrowhead.managementtool.activities;

import android.content.Context;
import android.content.Intent;
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
import eu.arrowhead.managementtool.model.CoreSystem;
import eu.arrowhead.managementtool.utility.Utility;
import eu.arrowhead.managementtool.volley.JsonArrayRequest;
import eu.arrowhead.managementtool.volley.JsonObjectRequest;
import eu.arrowhead.managementtool.volley.Networking;

public class CoreSystem_Detail extends AppCompatActivity implements
        ConfirmDeleteDialog.ConfirmDeleteListener {

    private CoordinatorLayout rootView;
    private Button saveButton;
    public TextView systemNameTv, addressTv, portTv, serviceUriTv, authInfoTv;
    private EditText systemNameEt, addressEt, portEt, serviceUriEt, authInfoEt;
    private ViewSwitcher snSwitcher, addressSwitcher, portSwitcher, serviceUriSwitcher, authInfoSwitcher;

    private static final String URL = "http://arrowhead.tmit.bme.hu:8081/api/configuration/coresystems";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core_system__detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.core_system_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rootView = (CoordinatorLayout) findViewById(R.id.core_system_detail_root_view);
        saveButton = (Button) findViewById(R.id.save_changes_button);
        snSwitcher = (ViewSwitcher) findViewById(R.id.system_name_switcher);
        systemNameTv = (TextView) findViewById(R.id.system_name_textview);
        systemNameEt = (EditText) findViewById(R.id.system_name_edittext);
        addressSwitcher = (ViewSwitcher) findViewById(R.id.address_switcher);
        addressTv = (TextView) findViewById(R.id.address_textview);
        addressEt = (EditText) findViewById(R.id.address_edittext);
        portSwitcher = (ViewSwitcher) findViewById(R.id.port_switcher);
        portTv = (TextView) findViewById(R.id.port_textview);
        portEt = (EditText) findViewById(R.id.port_edittext);
        serviceUriSwitcher = (ViewSwitcher) findViewById(R.id.service_uri_switcher);
        serviceUriTv = (TextView) findViewById(R.id.service_uri_textview);
        serviceUriEt = (EditText) findViewById(R.id.service_uri_edittext);
        authInfoSwitcher = (ViewSwitcher) findViewById(R.id.auth_info_switcher);
        authInfoTv = (TextView) findViewById(R.id.auth_info_textview);
        authInfoEt = (EditText) findViewById(R.id.auth_info_edittext);

        Intent intent = getIntent();
        CoreSystem system = (CoreSystem) intent.getSerializableExtra("core_system");
        systemNameTv.setText(system.getSystemName());
        addressTv.setText(system.getAddress());
        portTv.setText(system.getPort());
        serviceUriTv.setText(system.getServiceURI());
        authInfoTv.setText(system.getAuthenticationInfo());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CoreSystem system = new CoreSystem(systemNameEt.getText().toString(), addressEt.getText().toString(),
                        portEt.getText().toString(), serviceUriEt.getText().toString(), authInfoEt.getText().toString(), false);

                if (!systemNameEt.getText().toString().equals(systemNameTv.getText().toString())) {
                    //If the user changes the system name, we have to do a delete+post combo
                    sendDeleteRequest(system, true);
                } else {
                    sendUpdateRequest(system);
                }

                //hides the soft input keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    public void sendUpdateRequest(final CoreSystem system) {
        if (Utility.isConnected(CoreSystem_Detail.this)) {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT, URL, Utility.toJsonObject(system),
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            addressTv.setText(system.getAddress());
                            portTv.setText(system.getPort());
                            serviceUriTv.setText(system.getServiceURI());
                            authInfoTv.setText(system.getAuthenticationInfo());

                            snSwitcher.showPrevious();
                            addressSwitcher.showPrevious();
                            portSwitcher.showPrevious();
                            serviceUriSwitcher.showPrevious();
                            authInfoSwitcher.showPrevious();

                            Snackbar.make(rootView, R.string.core_system_update_success, Snackbar.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utility.showServerErrorFragment(error, CoreSystem_Detail.this);

                            snSwitcher.showPrevious();
                            addressSwitcher.showPrevious();
                            portSwitcher.showPrevious();
                            serviceUriSwitcher.showPrevious();
                            authInfoSwitcher.showPrevious();
                        }
                    }
            );

            Networking.getInstance(CoreSystem_Detail.this).addToRequestQueue(jsObjRequest);
            Toast.makeText(CoreSystem_Detail.this, R.string.core_system_update_request, Toast.LENGTH_SHORT).show();
        } else {
            Utility.showNoConnectionSnackbar(rootView);
        }
    }

    public void sendDeleteRequest(final CoreSystem system, final boolean forcedUpdate) {
        String deleteURL = URL + "/" + systemNameTv.getText();
        if (Utility.isConnected(this)) {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.DELETE, deleteURL, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            if (forcedUpdate) {
                                sendPostRequest(system);
                            } else {
                                Toast.makeText(CoreSystem_Detail.this, R.string.deleted, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utility.showServerErrorFragment(error, CoreSystem_Detail.this);

                            if (forcedUpdate) {
                                snSwitcher.showPrevious();
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
                Toast.makeText(CoreSystem_Detail.this, R.string.core_system_update_request, Toast.LENGTH_SHORT).show();
            }
        } else {
            Utility.showNoConnectionSnackbar(rootView);
        }
    }

    public void sendPostRequest(final CoreSystem system) {
        List<CoreSystem> systemList = Collections.singletonList(system);
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.POST, URL, Utility.toJsonArray(systemList),
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        systemNameTv.setText(system.getSystemName());
                        addressTv.setText(system.getAddress());
                        portTv.setText(system.getPort());
                        serviceUriTv.setText(system.getServiceURI());
                        authInfoTv.setText(system.getAuthenticationInfo());

                        snSwitcher.showPrevious();
                        addressSwitcher.showPrevious();
                        portSwitcher.showPrevious();
                        serviceUriSwitcher.showPrevious();
                        authInfoSwitcher.showPrevious();

                        Snackbar.make(rootView, R.string.core_system_update_success, Snackbar.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utility.showServerErrorFragment(error, CoreSystem_Detail.this);

                        snSwitcher.showPrevious();
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
        //Check if the system is being edited right now. (Could use any of the view switchers to check.)
        if (snSwitcher.getDisplayedChild() == 1) {
            snSwitcher.showPrevious();
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
        getMenuInflater().inflate(R.menu.core_system_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit_core_system) {
            systemNameEt.setText(systemNameTv.getText());
            addressEt.setText(addressTv.getText());
            portEt.setText(portTv.getText());
            serviceUriEt.setText(serviceUriTv.getText());
            authInfoEt.setText(authInfoTv.getText());

            snSwitcher.showNext();
            addressSwitcher.showNext();
            portSwitcher.showNext();
            serviceUriSwitcher.showNext();
            authInfoSwitcher.showNext();

            //shows the soft input keyboard
            View dummy = CoreSystem_Detail.this.getCurrentFocus();
            if (dummy != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(dummy, 0);
            }
            return true;
        }
        if (id == R.id.action_delete_core_system) {
            DialogFragment newFragment = new ConfirmDeleteDialog();
            newFragment.show(getSupportFragmentManager(), ConfirmDeleteDialog.TAG);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentPositiveClick(DialogFragment dialog) {
        CoreSystem system = new CoreSystem(systemNameTv.getText().toString(), null, null, null, null, false);
        sendDeleteRequest(system, false);
    }
}

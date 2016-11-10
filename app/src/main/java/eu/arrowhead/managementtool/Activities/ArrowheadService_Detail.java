package eu.arrowhead.managementtool.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.arrowhead.managementtool.R;
import eu.arrowhead.managementtool.adapters.ArrowheadService_Interfaces_Adapter;
import eu.arrowhead.managementtool.fragments.ConfirmDelete;
import eu.arrowhead.managementtool.model.ArrowheadService;
import eu.arrowhead.managementtool.utility.Networking;
import eu.arrowhead.managementtool.utility.Utility;

public class ArrowheadService_Detail extends AppCompatActivity implements
        ConfirmDelete.ConfirmDeleteListener {

    //TODO edites edittextek fontja legyen ugyanolyan méretű és vastagságú, mint a textview amit helyettesít

    private CoordinatorLayout rootView;
    private Button saveButton;
    private TextView serviceGroupTv, serviceDefinitionTv;
    private EditText serviceGroupEt, serviceDefinitionEt, interfaceEt;
    private ViewSwitcher sgSwitcher, sdSwitcher, interfaceSwitcher;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //TODO replace hardwired url with proper solution
    private static final String URL = "http://arrowhead.tmit.bme.hu:8081/api/common/services";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrowhead_service__detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.services_toolbar);
        setSupportActionBar(toolbar);
        //It will not be null ever, since we use a Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rootView = (CoordinatorLayout) findViewById(R.id.service_detail_root_view);
        saveButton = (Button) findViewById(R.id.save_changes_button);
        serviceGroupTv = (TextView) findViewById(R.id.service_group_textview);
        serviceDefinitionTv = (TextView) findViewById(R.id.service_definition_textview);
        serviceGroupEt = (EditText) findViewById(R.id.service_group_edittext);
        serviceDefinitionEt = (EditText) findViewById(R.id.service_definition_edittext);
        interfaceEt = (EditText) findViewById(R.id.interfaces_edittext);
        sgSwitcher = (ViewSwitcher) findViewById(R.id.service_group_switcher);
        sdSwitcher = (ViewSwitcher) findViewById(R.id.service_definition_switcher);
        interfaceSwitcher = (ViewSwitcher) findViewById(R.id.interface_list_switcher);
        mRecyclerView = (RecyclerView) findViewById(R.id.interface_list);

        Intent intent = getIntent();
        ArrowheadService service = (ArrowheadService) intent.getSerializableExtra("arrowhead_service");
        serviceGroupTv.setText(service.getServiceGroup());
        serviceDefinitionTv.setText(service.getServiceDefinition());

        //recyclerview setup
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mAdapter = new ArrowheadService_Interfaces_Adapter(service.getInterfaces());
        //NOTE: there is no empty view for the interface recview, but this is a minor problem, low priority
        mRecyclerView.setAdapter(mAdapter);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrowheadService service = new ArrowheadService(serviceGroupEt.getText().toString(), serviceDefinitionEt.getText().toString(), null, null);
                List<String> interfaces = new ArrayList<>();
                if (!interfaceEt.getText().toString().equals("")) {
                    interfaces = Arrays.asList(interfaceEt.getText().toString().split(","));
                }
                service.setInterfaces(interfaces);

                if (!serviceGroupEt.getText().equals(serviceGroupTv.getText()) ||
                        !serviceDefinitionEt.getText().equals(serviceDefinitionTv.getText())) {
                    sendUpdateRequest(service);
                } else {
                    //If the user changes the service group or service definition, we have to do a delete+post combo
                    sendDeleteRequest(service, true);
                }

                //hides the soft input keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
    }

    public void sendUpdateRequest(final ArrowheadService service) {
        if (Utility.isConnected(this)) {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT, URL, Utility.toJsonObject(service),
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            mAdapter = new ArrowheadService_Interfaces_Adapter(service.getInterfaces());
                            mRecyclerView.setAdapter(mAdapter);

                            sgSwitcher.showPrevious();
                            sdSwitcher.showPrevious();
                            interfaceSwitcher.showPrevious();

                            Snackbar.make(rootView, R.string.service_update_success, Snackbar.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ArrowheadService_Detail.this, R.string.service_update_error, Toast.LENGTH_LONG).show();
                            sgSwitcher.showPrevious();
                            sdSwitcher.showPrevious();
                            interfaceSwitcher.showPrevious();
                        }
                    }
            );

            Networking.getInstance(this).addToRequestQueue(jsObjRequest);
            Toast.makeText(ArrowheadService_Detail.this, R.string.service_update_request, Toast.LENGTH_SHORT).show();
        } else {
            Utility.showNoConnectionSnackbar(rootView);
        }
    }

    public void sendDeleteRequest(final ArrowheadService service, final boolean forcedUpdate) {
        String deleteURL = URL + "/servicegroup/" + serviceGroupTv.getText() + "/servicedef/" + serviceDefinitionTv.getText();
        if (Utility.isConnected(this)) {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.DELETE, deleteURL, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            if (forcedUpdate) {
                                sendPostRequest(service);
                            } else {
                                Snackbar.make(rootView, R.string.deleted, Snackbar.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //TODO add this kind of error handling to all request, so the user can see the server response
                            //TODO after adding header the delete works, but the app still signals error for some reason, check the reason and cleanup this part after
                            NetworkResponse response = error.networkResponse;
                            if (error instanceof ServerError && response != null) {
                                try {
                                    String res = new String(response.data,
                                            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                    // Now you can use any deserializer to make sense of data
                                    Log.i("testing", res);
                                } catch (UnsupportedEncodingException e1) {
                                    // Couldn't properly decode data to string
                                    e1.printStackTrace();
                                }
                            }

                            if (forcedUpdate) {
                                Toast.makeText(ArrowheadService_Detail.this, R.string.service_update_error, Toast.LENGTH_LONG).show();
                                sgSwitcher.showPrevious();
                                sdSwitcher.showPrevious();
                                interfaceSwitcher.showPrevious();
                            } else {
                                Toast.makeText(ArrowheadService_Detail.this, R.string.service_delete_error, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
            )
                //TODO not sure how this syntax works. maybe i should create custom request for clealer code. and to avoid putting this method everywhere
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            Networking.getInstance(this).addToRequestQueue(jsObjRequest);
            if (forcedUpdate) {
                Toast.makeText(ArrowheadService_Detail.this, R.string.service_update_request, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ArrowheadService_Detail.this, R.string.service_delete_request, Toast.LENGTH_SHORT).show();
            }
        } else {
            Utility.showNoConnectionSnackbar(rootView);
        }

    }

    public void sendPostRequest(final ArrowheadService service) {
        List<ArrowheadService> serviceList = Collections.singletonList(service);
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.POST, URL, Utility.toJsonArray(serviceList),
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        mAdapter = new ArrowheadService_Interfaces_Adapter(service.getInterfaces());
                        mRecyclerView.setAdapter(mAdapter);
                        serviceGroupTv.setText(service.getServiceGroup());
                        serviceDefinitionTv.setText(service.getServiceDefinition());

                        sgSwitcher.showPrevious();
                        sdSwitcher.showPrevious();
                        interfaceSwitcher.showPrevious();

                        Snackbar.make(rootView, R.string.service_update_success, Snackbar.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ArrowheadService_Detail.this, R.string.service_update_error, Toast.LENGTH_LONG).show();
                        sgSwitcher.showPrevious();
                        sdSwitcher.showPrevious();
                        interfaceSwitcher.showPrevious();
                    }
                }
        );

        Networking.getInstance(this).addToRequestQueue(jsArrayRequest);
    }

    @Override
    public void onBackPressed() {
        //Check if the service is being edited right now. (Could use any of the 3 view switchers to check.)
        if (sgSwitcher.getDisplayedChild() == 1) {
            sgSwitcher.showPrevious();
            sdSwitcher.showPrevious();
            interfaceSwitcher.showPrevious();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.arrowhead_service_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit_service) {
            serviceGroupEt.setText(serviceGroupTv.getText());
            serviceDefinitionEt.setText(serviceDefinitionTv.getText());
            sgSwitcher.showNext();
            sdSwitcher.showNext();
            interfaceSwitcher.showNext();

            //shows the soft input keyboard
            View dummy = ArrowheadService_Detail.this.getCurrentFocus();
            if (dummy != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(dummy, 0);
            }
            return true;
        }
        if (id == R.id.action_delete_service) {
            DialogFragment newFragment = new ConfirmDelete();
            newFragment.show(getSupportFragmentManager(), ConfirmDelete.TAG);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentPositiveClick(DialogFragment dialog) {
        ArrowheadService service = new ArrowheadService(serviceGroupTv.getText().toString(), serviceDefinitionTv.getText().toString(), null, null);
        sendDeleteRequest(service, false);
    }
}

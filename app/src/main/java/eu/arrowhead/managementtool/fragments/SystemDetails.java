package eu.arrowhead.managementtool.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import eu.arrowhead.managementtool.R;
import eu.arrowhead.managementtool.model.ArrowheadSystem;
import eu.arrowhead.managementtool.utility.Utility;
import eu.arrowhead.managementtool.volley.JsonArrayRequest;
import eu.arrowhead.managementtool.volley.JsonObjectRequest;
import eu.arrowhead.managementtool.volley.Networking;

public class SystemDetails extends Fragment {

    private ArrowheadSystem system;

    private ScrollView rootView;
    private Button saveButton;
    public TextView systemGroupTv, systemNameTv, addressTv, portTv, authInfoTv;
    private EditText systemGroupEt, systemNameEt, addressEt, portEt, authInfoEt;
    private ViewSwitcher sgSwitcher, snSwitcher, addressSwitcher, portSwitcher, authInfoSwitcher;

    private static final String URL = "http://arrowhead.tmit.bme.hu:8081/api/common/systems";

    public SystemDetails(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        system = (ArrowheadSystem) args.getSerializable("arrowhead_system");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_system_details, container, false);
        setHasOptionsMenu(true);

        rootView = (ScrollView) layout.findViewById(R.id.system_detail_root_view);
        saveButton = (Button) layout.findViewById(R.id.save_changes_button);
        sgSwitcher = (ViewSwitcher) layout.findViewById(R.id.system_group_switcher);
        systemGroupTv = (TextView) layout.findViewById(R.id.system_group_textview);
        systemGroupEt = (EditText) layout.findViewById(R.id.system_group_edittext);
        snSwitcher = (ViewSwitcher) layout.findViewById(R.id.system_name_switcher);
        systemNameTv = (TextView) layout.findViewById(R.id.system_name_textview);
        systemNameEt = (EditText) layout.findViewById(R.id.system_name_edittext);
        addressSwitcher = (ViewSwitcher) layout.findViewById(R.id.address_switcher);
        addressTv = (TextView) layout.findViewById(R.id.address_textview);
        addressEt = (EditText) layout.findViewById(R.id.address_edittext);
        portSwitcher = (ViewSwitcher) layout.findViewById(R.id.port_switcher);
        portTv = (TextView) layout.findViewById(R.id.port_textview);
        portEt = (EditText) layout.findViewById(R.id.port_edittext);
        authInfoSwitcher = (ViewSwitcher) layout.findViewById(R.id.auth_info_switcher);
        authInfoTv = (TextView) layout.findViewById(R.id.auth_info_textview);
        authInfoEt = (EditText) layout.findViewById(R.id.auth_info_edittext);

        systemGroupTv.setText(system.getSystemGroup());
        systemNameTv.setText(system.getSystemName());
        addressTv.setText(system.getAddress());
        portTv.setText(system.getPort());
        authInfoTv.setText(system.getAuthenticationInfo());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrowheadSystem system = new ArrowheadSystem(systemGroupEt.getText().toString(), systemNameEt.getText().toString(),
                        addressEt.getText().toString(), portEt.getText().toString(), authInfoEt.getText().toString());

                if (!systemGroupEt.getText().toString().equals(systemGroupTv.getText().toString()) ||
                        !systemNameEt.getText().toString().equals(systemNameTv.getText().toString())) {
                    //If the user changes the service group or service definition, we have to do a delete+post combo
                    sendDeleteRequest(system, true);
                } else {
                    sendUpdateRequest(system);
                }

                //hides the soft input keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        return layout;
    }

    public void sendUpdateRequest(final ArrowheadSystem system) {
        if (Utility.isConnected(getActivity())) {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT, URL, Utility.toJsonObject(system),
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            addressTv.setText(system.getAddress());
                            portTv.setText(system.getPort());
                            authInfoTv.setText(system.getAuthenticationInfo());

                            sgSwitcher.showPrevious();
                            snSwitcher.showPrevious();
                            addressSwitcher.showPrevious();
                            portSwitcher.showPrevious();
                            authInfoSwitcher.showPrevious();

                            Snackbar.make(rootView, R.string.system_update_succcess, Snackbar.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utility.showServerErrorFragment(error, (AppCompatActivity) getActivity());

                            sgSwitcher.showPrevious();
                            snSwitcher.showPrevious();
                            addressSwitcher.showPrevious();
                            portSwitcher.showPrevious();
                            authInfoSwitcher.showPrevious();
                        }
                    }
            );

            Networking.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
            Toast.makeText(getActivity(), R.string.system_update_request, Toast.LENGTH_SHORT).show();
        } else {
            Utility.showNoConnectionSnackbar(rootView);
        }
    }

    public void sendDeleteRequest(final ArrowheadSystem system, final boolean forcedUpdate) {
        String deleteURL = URL + "/systemgroup/" + systemGroupTv.getText() + "/systemname/" + systemNameTv.getText();
        if (Utility.isConnected(getActivity())) {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.DELETE, deleteURL, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            if (forcedUpdate) {
                                sendPostRequest(system);
                            } else {
                                Toast.makeText(getActivity(), R.string.deleted, Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            }
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utility.showServerErrorFragment(error, (AppCompatActivity) getActivity());

                            if (forcedUpdate) {
                                sgSwitcher.showPrevious();
                                snSwitcher.showPrevious();
                                addressSwitcher.showPrevious();
                                portSwitcher.showPrevious();
                                authInfoSwitcher.showPrevious();
                            }
                        }
                    }
            );

            Networking.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
            if (forcedUpdate) {
                Toast.makeText(getActivity(), R.string.system_update_request, Toast.LENGTH_SHORT).show();
            }
        } else {
            Utility.showNoConnectionSnackbar(rootView);
        }
    }

    public void sendPostRequest(final ArrowheadSystem system) {
        List<ArrowheadSystem> systemList = Collections.singletonList(system);
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.POST, URL, Utility.toJsonArray(systemList),
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        systemGroupTv.setText(system.getSystemGroup());
                        systemNameTv.setText(system.getSystemName());
                        addressTv.setText(system.getAddress());
                        portTv.setText(system.getPort());
                        authInfoTv.setText(system.getAuthenticationInfo());

                        sgSwitcher.showPrevious();
                        snSwitcher.showPrevious();
                        addressSwitcher.showPrevious();
                        portSwitcher.showPrevious();
                        authInfoSwitcher.showPrevious();

                        Snackbar.make(rootView, R.string.service_update_success, Snackbar.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utility.showServerErrorFragment(error, (AppCompatActivity) getActivity());

                        sgSwitcher.showPrevious();
                        snSwitcher.showPrevious();
                        addressSwitcher.showPrevious();
                        portSwitcher.showPrevious();
                        authInfoSwitcher.showPrevious();
                    }
                }
        );

        Networking.getInstance(getActivity()).addToRequestQueue(jsArrayRequest);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.arrowhead_system_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit_service) {
            systemGroupEt.setText(systemGroupTv.getText());
            systemNameEt.setText(systemNameTv.getText());
            addressEt.setText(addressTv.getText());
            portEt.setText(portTv.getText());
            authInfoEt.setText(authInfoTv.getText());

            sgSwitcher.showNext();
            snSwitcher.showNext();
            addressSwitcher.showNext();
            portSwitcher.showNext();
            authInfoSwitcher.showNext();

            //shows the soft input keyboard
            View dummy = getActivity().getCurrentFocus();
            if (dummy != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(dummy, 0);
            }
            return true;
        }
        if (id == R.id.action_delete_service) {
            DialogFragment newFragment = new ConfirmDeleteDialog();
            newFragment.show(getActivity().getSupportFragmentManager(), ConfirmDeleteDialog.TAG);
        }

        return super.onOptionsItemSelected(item);
    }

    public List<ViewSwitcher> getViewSwitchers(){
        ViewSwitcher array[] = new ViewSwitcher[]{sgSwitcher, snSwitcher, addressSwitcher, portSwitcher, authInfoSwitcher};
        return new ArrayList<>(Arrays.asList(array));
    }
}

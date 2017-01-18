package eu.arrowhead.managementtool.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.arrowhead.managementtool.R;
import eu.arrowhead.managementtool.adapters.ArrowheadSystems_Adapter;
import eu.arrowhead.managementtool.fragments.AddNewSystemDialog;
import eu.arrowhead.managementtool.fragments.QRCodeScannerDialog;
import eu.arrowhead.managementtool.model.ArrowheadSystem;
import eu.arrowhead.managementtool.utility.Utility;
import eu.arrowhead.managementtool.volley.JsonArrayRequest;
import eu.arrowhead.managementtool.volley.JsonObjectRequest;
import eu.arrowhead.managementtool.volley.Networking;

public class ArrowheadSystems extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener,
        AddNewSystemDialog.AddNewSystemListener,
        QRCodeScannerDialog.QRCodeScannerListener,
        SearchView.OnQueryTextListener {

    private DrawerLayout drawer;
    private RecyclerView mRecyclerView;
    private ArrowheadSystems_Adapter mAdapter;
    private SwipeRefreshLayout srl;
    private SharedPreferences prefs;

    private List<ArrowheadSystem> systemList = new ArrayList<>();
    private static String URL;
    private static final int SCAN_QR_CODE_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = this.getSharedPreferences("eu.arrowhead.managementtool", Context.MODE_PRIVATE);
        if(!prefs.getBoolean("not_first_launch", false)){
            Intent intent = new Intent(ArrowheadSystems.this, FirstLaunchScreen.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_arrowhead_systems);
        Toolbar toolbar = (Toolbar) findViewById(R.id.systems_toolbar);
        setSupportActionBar(toolbar);
        setTitle(getResources().getString(R.string.title_activity_arrowhead_systems));

        //navigation drawer setup
        drawer = (DrawerLayout) findViewById(R.id.systems_root_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //recyclerview setup
        mRecyclerView = (RecyclerView) findViewById(R.id.system_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        srl = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_system_list);
        srl.setOnRefreshListener(this);
    }

    @Override
    protected void onResume() {
        URL = Uri.parse(prefs.getString("api_address", "")).buildUpon()
                .appendPath("common").appendPath("systems").build().toString();

        //We send the request here, because the user can delete a list element in the detail activity, and we want the list to update automatically.
        sendGetAllRequest();
        super.onResume();
    }

    public void sendGetAllRequest() {
        final ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.system_list_switcher);
        if (Utility.isConnected(this)) {
            JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {
                            systemList = Utility.fromJsonArray(response.toString(), ArrowheadSystem.class);
                            if (mAdapter == null) {
                                mAdapter = new ArrowheadSystems_Adapter(systemList);
                                mRecyclerView.setAdapter(mAdapter);
                            } else {
                                mAdapter.setSystemList(systemList);
                            }

                            if (systemList.size() > 0 && switcher.getDisplayedChild() == 1) {
                                //if the empty view is displayed at the moment, switch to the recyclerview
                                switcher.showPrevious();
                            }
                            if (switcher.getDisplayedChild() == 0 && systemList.size() == 0) {
                                //if the recyclerview is displayed at the moment, switch to the empty view
                                switcher.showNext();
                            }
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (switcher.getDisplayedChild() == 0 && systemList.size() == 0) {
                                //if the recyclerview is displayed at the moment, switch to the empty view
                                switcher.showNext();
                            }
                            Utility.showServerErrorFragment(error, ArrowheadSystems.this);
                        }
                    }
            );

            Networking.getInstance(this).addToRequestQueue(jsArrayRequest);
        } else {
            if (switcher.getDisplayedChild() == 0 && systemList.size() == 0) {
                //if the recyclerview is displayed at the moment, switch to the empty view
                switcher.showNext();
            }
            Utility.showNoConnectionSnackbar(drawer);
        }

        srl.setRefreshing(false);
    }

    @Override
    public void onSaveSystemButtonClicked(DialogFragment dialog) {
        EditText systemGroupEt = (EditText) dialog.getDialog().findViewById(R.id.system_group_edittext);
        EditText systemNameEt = (EditText) dialog.getDialog().findViewById(R.id.system_name_edittext);
        EditText addressEt = (EditText) dialog.getDialog().findViewById(R.id.address_edittext);
        EditText portEt = (EditText) dialog.getDialog().findViewById(R.id.port_edittext);
        EditText authInfoEt = (EditText) dialog.getDialog().findViewById(R.id.auth_info_edittext);

        if(systemGroupEt.getText().toString().isEmpty() || systemNameEt.getText().toString().isEmpty()){
            Toast.makeText(ArrowheadSystems.this, R.string.mandatory_fields_warning, Toast.LENGTH_LONG).show();
        }
        else{
            ArrowheadSystem system = new ArrowheadSystem(systemGroupEt.getText().toString(), systemNameEt.getText().toString(),
                    addressEt.getText().toString(), portEt.getText().toString(), authInfoEt.getText().toString());
            List<ArrowheadSystem> systemList = Collections.singletonList(system);

            JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.POST, URL, Utility.toJsonArray(systemList),
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {
                            sendGetAllRequest();
                            Snackbar.make(drawer, R.string.add_system_successful, Snackbar.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utility.showServerErrorFragment(error, ArrowheadSystems.this);
                        }
                    }
            );

            Networking.getInstance(this).addToRequestQueue(jsArrayRequest);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.arrowhead_systems, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        mAdapter.setSystemList(systemList);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true; // Return true to expand action view
                    }
                });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_system) {
            DialogFragment newFragment = new AddNewSystemDialog();
            newFragment.show(getSupportFragmentManager(), AddNewSystemDialog.TAG);
        }

        if (id == R.id.action_scan_qr_code) {
            try {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
                startActivityForResult(intent, SCAN_QR_CODE_REQUEST);
            } catch (Exception e) {
                DialogFragment newFragment = new QRCodeScannerDialog();
                newFragment.show(getSupportFragmentManager(), QRCodeScannerDialog.TAG);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentPositiveClick(DialogFragment dialog) {
        Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
        startActivity(marketIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                String[] system = contents.split(";");
                String systemGroup = system[0];
                String systemName = system[1];
                sendGetRequest(systemGroup, systemName);
            }
            if (resultCode == RESULT_CANCELED) {
                //handle cancel
            }
        }
    }

    public void sendGetRequest(String systemGroup, String systemName){
        String systemURL = URL + "/systemgroup/" + systemGroup + "/systemname/" + systemName;
        if (Utility.isConnected(this)) {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, systemURL, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            ArrowheadSystem system = Utility.fromJsonObject(response.toString(), ArrowheadSystem.class);
                            Intent intent = new Intent(ArrowheadSystems.this, ArrowheadSystem_Detail.class);
                            intent.putExtra("arrowhead_system", system);
                            startActivity(intent);
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utility.showServerErrorFragment(error, ArrowheadSystems.this);
                        }
                    }
            );

            Networking.getInstance(this).addToRequestQueue(jsObjRequest);
        } else {
            Utility.showNoConnectionSnackbar(drawer);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<ArrowheadSystem> filteredModelList = filterList(systemList, newText);
        if (mAdapter == null) {
            mAdapter = new ArrowheadSystems_Adapter(filteredModelList);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setSystemList(filteredModelList);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<ArrowheadSystem> filterList(List<ArrowheadSystem> systems, String query) {
        query = query.toLowerCase();

        final List<ArrowheadSystem> filteredSystemList = new ArrayList<>();
        for (ArrowheadSystem system : systems) {
            final String systemGroup = system.getSystemGroup().toLowerCase();
            final String systemName = system.getSystemName().toLowerCase();
            if (systemGroup.contains(query) || systemName.contains(query)) {
                filteredSystemList.add(system);
            }
        }
        return filteredSystemList;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if(Utility.handleNavigationItemClick(item, ArrowheadSystems.this)){
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRefresh() {
        srl.setRefreshing(true);
        sendGetAllRequest();
    }
}

package eu.arrowhead.managementtool.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ViewSwitcher;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.arrowhead.managementtool.R;
import eu.arrowhead.managementtool.adapters.ArrowheadSystems_Adapter;
import eu.arrowhead.managementtool.fragments.AddNewSystem;
import eu.arrowhead.managementtool.model.ArrowheadSystem;
import eu.arrowhead.managementtool.utility.Utility;
import eu.arrowhead.managementtool.volley.JsonArrayRequest;
import eu.arrowhead.managementtool.volley.Networking;

public class ArrowheadSystems extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener,
        AddNewSystem.AddNewSystemListener{

    private DrawerLayout drawer;
    private RecyclerView mRecyclerView;
    private ArrowheadSystems_Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout srl;

    private List<ArrowheadSystem> systemList = new ArrayList<>();

    //TODO replace hardwired url with proper solution
    //TODO valamiért system lista nem tölt be + nav menüben ki van jelölve a service menüpont???
    private static final String URL = "http://arrowhead.tmit.bme.hu:8081/api/common/systems";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrowhead_systems);
        Toolbar toolbar = (Toolbar) findViewById(R.id.systems_toolbar);
        setSupportActionBar(toolbar);

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
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        srl = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_system_list);
        srl.setOnRefreshListener(this);
    }

    @Override
    protected void onResume() {
        //We send the request here, because the user can delete a list element in the detail activity, and we want the list to update automatically.
        sendGetAllRequest();
        super.onResume();
    }

    public void sendGetAllRequest(){
        final ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.system_list_switcher);
        if (Utility.isConnected(this)) {
            JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response){
                            systemList = Utility.fromJsonArray(response.toString(), ArrowheadSystem.class);
                            if(mAdapter == null){
                                mAdapter = new ArrowheadSystems_Adapter(systemList);
                                mRecyclerView.setAdapter(mAdapter);
                            }
                            else{
                                mAdapter.setSystemList(systemList);
                                mAdapter.notifyDataSetChanged();
                            }

                            if(systemList.size() > 0 && switcher.getDisplayedChild() == 1){
                                //if the empty view is displayed at the moment, switch to the recyclerview
                                switcher.showPrevious();
                            }
                            if(switcher.getDisplayedChild() == 0 && systemList.size() == 0){
                                //if the recyclerview is displayed at the moment, switch to the empty view
                                switcher.showNext();
                            }
                        }},
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(switcher.getDisplayedChild() == 0 && systemList.size() == 0){
                                //if the recyclerview is displayed at the moment, switch to the empty view
                                switcher.showNext();
                            }
                            Utility.showServerErrorFragment(error, ArrowheadSystems.this);
                        }}
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
        EditText AddressEt = (EditText) dialog.getDialog().findViewById(R.id.address_edittext);
        EditText PortEt = (EditText) dialog.getDialog().findViewById(R.id.port_edittext);
        EditText AuthInfoEt = (EditText) dialog.getDialog().findViewById(R.id.auth_info_edittext);

        ArrowheadSystem system = new ArrowheadSystem(systemGroupEt.getText().toString(), systemNameEt.getText().toString(),
                AddressEt.getText().toString(), PortEt.getText().toString(), AuthInfoEt.getText().toString());
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

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.arrowhead_systems, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_system) {
            DialogFragment newFragment = new AddNewSystem();
            newFragment.show(getSupportFragmentManager(), AddNewSystem.TAG);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        return Utility.handleNavigationItemClick(item, ArrowheadSystems.this);
    }

    @Override
    public void onRefresh() {
        srl.setRefreshing(true);
        sendGetAllRequest();
    }
}

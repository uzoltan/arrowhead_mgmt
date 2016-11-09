package eu.arrowhead.managementtool.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.widget.ViewSwitcher;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import eu.arrowhead.managementtool.R;
import eu.arrowhead.managementtool.adapters.ArrowheadServices_Adapter;
import eu.arrowhead.managementtool.model.ArrowheadService;
import eu.arrowhead.managementtool.utility.Networking;
import eu.arrowhead.managementtool.utility.Utility;

public class ArrowheadServices extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener{

    private DrawerLayout drawer;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout srl;

    private List<ArrowheadService> serviceList = new ArrayList<>();

    //TODO replace hardwired url with proper solution
    private static final String URL = "http://arrowhead.tmit.bme.hu:8081/api/common/services";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrowhead_services);
        Toolbar toolbar = (Toolbar) findViewById(R.id.services_toolbar);
        setSupportActionBar(toolbar);

        //navigation drawer setup
        drawer = (DrawerLayout) findViewById(R.id.services_root_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //recyclerview setup
        mRecyclerView = (RecyclerView) findViewById(R.id.service_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        srl = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_service_list);
        srl.setOnRefreshListener(this);

        sendRequest();
    }

    public void sendRequest(){
        final ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.service_list_switcher);
        if (Utility.isConnected(this)) {
            JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response){
                            serviceList = Utility.fromJsonArray(response.toString(), ArrowheadService.class);
                            mAdapter = new ArrowheadServices_Adapter(serviceList);
                            mRecyclerView.setAdapter(mAdapter);
                            if(serviceList.size() > 0 && switcher.getDisplayedChild() == 1){
                                //if the empty view is displayed at the moment, switch to the recyclerview
                                switcher.showPrevious();
                            }
                        }},
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(switcher.getDisplayedChild() == 0 && serviceList.size() == 0){
                                //if the recyclerview is displayed at the moment, switch to the empty view
                                switcher.showNext();
                            }
                            Snackbar.make(drawer, error.getMessage(), Snackbar.LENGTH_LONG).show();
                        }}
            );

            Networking.getInstance(this).addToRequestQueue(jsArrayRequest);
        } else {
            if (switcher.getDisplayedChild() == 0 && serviceList.size() == 0) {
                //if the recyclerview is displayed at the moment, switch to the empty view
                switcher.showNext();
            }
            Utility.showNoConnectionSnackbar(drawer);
        }

        srl.setRefreshing(false);
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
        getMenuInflater().inflate(R.menu.arrowhead_services, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRefresh() {
        srl.setRefreshing(true);
        sendRequest();
    }
}

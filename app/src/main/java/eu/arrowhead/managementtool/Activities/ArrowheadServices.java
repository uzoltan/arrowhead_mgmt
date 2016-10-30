package eu.arrowhead.managementtool.Activities;

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
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import eu.arrowhead.managementtool.Adapters.ArrowheadServices_Adapter;
import eu.arrowhead.managementtool.CustomViews.RecyclerViewEmptySupport;
import eu.arrowhead.managementtool.R;
import eu.arrowhead.managementtool.Utility.Networking;
import eu.arrowhead.managementtool.Utility.Utility;
import eu.arrowhead.managementtool.model.ArrowheadService;

public class ArrowheadServices extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener{

    private DrawerLayout drawer;
    private RecyclerViewEmptySupport mRecyclerView;
    private View emptyView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout srl;

    private List<ArrowheadService> serviceList = new ArrayList<>();

    //TODO custom recyclerview with empty view not working (as intended) for some reason
    //TODO replace hardwired url
    private static final String URL = "http://arrowhead.tmit.bme.hu:8081/api/common/services";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrowhead_services);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //navigation drawer setup
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //recyclerview setup
        mRecyclerView = (RecyclerViewEmptySupport) findViewById(R.id.service_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        emptyView = findViewById(R.id.empty_recview);
        srl = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_service_list);
        srl.setOnRefreshListener(this);

        sendRequest();
    }

    public void sendRequest(){
        if (Utility.isConnected(this)) {
            JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                    (Request.Method.GET, URL, null,
                            new Response.Listener<JSONArray>() {

                                @Override
                                public void onResponse(JSONArray response){
                                    serviceList = Utility.fromJsonArray(response.toString(), ArrowheadService.class);
                                    mAdapter = new ArrowheadServices_Adapter(serviceList);
                                    mRecyclerView.setAdapter(mAdapter);
                                }},
                            new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Snackbar.make(drawer, error.getMessage(), Snackbar.LENGTH_LONG).show();
                                }}
                    );

            Networking.getInstance(this).addToRequestQueue(jsArrayRequest);
        } else {
            Snackbar sb = Snackbar.make(drawer, R.string.no_connection, Snackbar.LENGTH_LONG);
            TextView sbText = (TextView) sb.getView().findViewById(android.support.design.R.id.snackbar_text);
            sbText.setTextSize(20f);
            sb.show();
        }
        if(mRecyclerView.getAdapter() == null){
            List<ArrowheadService> emptyList = new ArrayList<>();
            mAdapter = new ArrowheadServices_Adapter(emptyList);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setEmptyView(emptyView);
        }

        srl.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.arrowhead_services, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRefresh() {
        srl.setRefreshing(true);
        sendRequest();
    }
}

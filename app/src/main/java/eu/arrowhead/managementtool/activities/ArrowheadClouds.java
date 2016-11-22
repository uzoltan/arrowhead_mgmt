package eu.arrowhead.managementtool.activities;

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
import android.widget.ViewSwitcher;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.arrowhead.managementtool.R;
import eu.arrowhead.managementtool.adapters.ArrowheadClouds_Adapter;
import eu.arrowhead.managementtool.fragments.AddNewCloudDialog;
import eu.arrowhead.managementtool.model.ArrowheadCloud;
import eu.arrowhead.managementtool.utility.Utility;
import eu.arrowhead.managementtool.volley.JsonArrayRequest;
import eu.arrowhead.managementtool.volley.Networking;

public class ArrowheadClouds extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener,
        AddNewCloudDialog.AddNewCloudListener,
        SearchView.OnQueryTextListener{

    private DrawerLayout drawer;
    private RecyclerView mRecyclerView;
    private ArrowheadClouds_Adapter mAdapter;
    private SwipeRefreshLayout srl;

    private List<ArrowheadCloud> cloudList = new ArrayList<>();
    private static final String URL = "http://arrowhead.tmit.bme.hu:8081/api/common/clouds";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrowhead_clouds);
        Toolbar toolbar = (Toolbar) findViewById(R.id.clouds_toolbar);
        setSupportActionBar(toolbar);

        //navigation drawer setup
        drawer = (DrawerLayout) findViewById(R.id.clouds_root_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //recyclerview setup
        mRecyclerView = (RecyclerView) findViewById(R.id.cloud_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        srl = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_cloud_list);
        srl.setOnRefreshListener(this);
    }

    @Override
    protected void onResume() {
        //We send the request here, because the user can delete a list element in the detail activity, and we want the list to update automatically.
        sendGetAllRequest();
        super.onResume();
    }

    public void sendGetAllRequest() {
        final ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.cloud_list_switcher);
        if (Utility.isConnected(this)) {
            JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {
                            cloudList = Utility.fromJsonArray(response.toString(), ArrowheadCloud.class);
                            if (mAdapter == null) {
                                mAdapter = new ArrowheadClouds_Adapter(cloudList);
                                mRecyclerView.setAdapter(mAdapter);
                            } else {
                                mAdapter.setCloudList(cloudList);
                            }

                            if (cloudList.size() > 0 && switcher.getDisplayedChild() == 1) {
                                //if the empty view is displayed at the moment, switch to the recyclerview
                                switcher.showPrevious();
                            }
                            if (switcher.getDisplayedChild() == 0 && cloudList.size() == 0) {
                                //if the recyclerview is displayed at the moment, switch to the empty view
                                switcher.showNext();
                            }
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (switcher.getDisplayedChild() == 0 && cloudList.size() == 0) {
                                //if the recyclerview is displayed at the moment, switch to the empty view
                                switcher.showNext();
                            }
                            Utility.showServerErrorFragment(error, ArrowheadClouds.this);
                        }
                    }
            );

            Networking.getInstance(this).addToRequestQueue(jsArrayRequest);
        } else {
            if (switcher.getDisplayedChild() == 0 && cloudList.size() == 0) {
                //if the recyclerview is displayed at the moment, switch to the empty view
                switcher.showNext();
            }
            Utility.showNoConnectionSnackbar(drawer);
        }

        srl.setRefreshing(false);
    }

    @Override
    public void onSaveCloudButtonClicked(DialogFragment dialog) {
        EditText operatorEt = (EditText) dialog.getDialog().findViewById(R.id.operator_edittext);
        EditText cloudNameEt = (EditText) dialog.getDialog().findViewById(R.id.cloud_name_edittext);
        EditText addressEt = (EditText) dialog.getDialog().findViewById(R.id.address_edittext);
        EditText portEt = (EditText) dialog.getDialog().findViewById(R.id.port_edittext);
        EditText serviceUriEt = (EditText) dialog.getDialog().findViewById(R.id.gatekeeper_service_uri_edittext);
        EditText authInfoEt = (EditText) dialog.getDialog().findViewById(R.id.auth_info_edittext);

        ArrowheadCloud cloud = new ArrowheadCloud(operatorEt.getText().toString(), cloudNameEt.getText().toString(),
                addressEt.getText().toString(), portEt.getText().toString(), serviceUriEt.getText().toString(), authInfoEt.getText().toString());
        List<ArrowheadCloud> cloudList = Collections.singletonList(cloud);

        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.POST, URL, Utility.toJsonArray(cloudList),
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        sendGetAllRequest();
                        Snackbar.make(drawer, R.string.add_cloud_successful, Snackbar.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utility.showServerErrorFragment(error, ArrowheadClouds.this);
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
        getMenuInflater().inflate(R.menu.arrowhead_clouds, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        mAdapter.setCloudList(cloudList);
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

        if (id == R.id.action_add_cloud) {
            DialogFragment newFragment = new AddNewCloudDialog();
            newFragment.show(getSupportFragmentManager(), AddNewCloudDialog.TAG);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<ArrowheadCloud> filteredModelList = filterList(cloudList, newText);
        mAdapter.setCloudList(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<ArrowheadCloud> filterList(List<ArrowheadCloud> clouds, String query) {
        query = query.toLowerCase();

        final List<ArrowheadCloud> filteredCloudList = new ArrayList<>();
        for (ArrowheadCloud cloud : clouds) {
            final String operator = cloud.getOperator().toLowerCase();
            final String cloudName = cloud.getCloudName().toLowerCase();
            if (operator.contains(query) || cloudName.contains(query)) {
                filteredCloudList.add(cloud);
            }
        }
        return filteredCloudList;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if(Utility.handleNavigationItemClick(item, ArrowheadClouds.this)){
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onRefresh() {
        srl.setRefreshing(true);
        sendGetAllRequest();
    }
}

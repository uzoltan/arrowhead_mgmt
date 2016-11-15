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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import eu.arrowhead.managementtool.R;
import eu.arrowhead.managementtool.adapters.ArrowheadServices_Adapter;
import eu.arrowhead.managementtool.fragments.AddNewServiceDialog;
import eu.arrowhead.managementtool.model.ArrowheadService;
import eu.arrowhead.managementtool.utility.Utility;
import eu.arrowhead.managementtool.volley.JsonArrayRequest;
import eu.arrowhead.managementtool.volley.Networking;

public class ArrowheadServices extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener,
        AddNewServiceDialog.AddNewServiceListener,
        SearchView.OnQueryTextListener{

    private DrawerLayout drawer;
    private RecyclerView mRecyclerView;
    private ArrowheadServices_Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout srl;

    private List<ArrowheadService> serviceList = new ArrayList<>();

    //TODO replace hardwired url with proper solution
    //TODO recview searchbar!
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
    }

    @Override
    protected void onResume() {
        //We send the request here, because the user can delete a list element in the detail activity, and we want the list to update automatically.
        sendGetAllRequest();
        super.onResume();
    }

    public void sendGetAllRequest() {
        final ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.service_list_switcher);
        if (Utility.isConnected(this)) {
            JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {
                            serviceList = Utility.fromJsonArray(response.toString(), ArrowheadService.class);
                            if (mAdapter == null) {
                                mAdapter = new ArrowheadServices_Adapter(serviceList);
                                mRecyclerView.setAdapter(mAdapter);
                            } else {
                                mAdapter.setServiceList(serviceList);
                            }

                            if (serviceList.size() > 0 && switcher.getDisplayedChild() == 1) {
                                //if the empty view is displayed at the moment, switch to the recyclerview
                                switcher.showPrevious();
                            }
                            if (switcher.getDisplayedChild() == 0 && serviceList.size() == 0) {
                                //if the recyclerview is displayed at the moment, switch to the empty view
                                switcher.showNext();
                            }
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (switcher.getDisplayedChild() == 0 && serviceList.size() == 0) {
                                //if the recyclerview is displayed at the moment, switch to the empty view
                                switcher.showNext();
                            }
                            Utility.showServerErrorFragment(error, ArrowheadServices.this);
                        }
                    }
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
    public void onSaveServiceButtonClicked(DialogFragment dialog) {
        EditText serviceGroupEt = (EditText) dialog.getDialog().findViewById(R.id.service_group_edittext);
        EditText serviceDefEt = (EditText) dialog.getDialog().findViewById(R.id.service_definition_edittext);
        EditText interfacesEt = (EditText) dialog.getDialog().findViewById(R.id.interfaces_edittext);

        ArrowheadService service = new ArrowheadService(serviceGroupEt.getText().toString(), serviceDefEt.getText().toString(), null, null);
        List<String> interfaces = new ArrayList<>();
        if (!interfacesEt.getText().toString().equals("")) {
            interfaces = Arrays.asList(interfacesEt.getText().toString().split(","));
        }
        service.setInterfaces(interfaces);
        List<ArrowheadService> serviceList = Collections.singletonList(service);

        JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.POST, URL, Utility.toJsonArray(serviceList),
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        sendGetAllRequest();
                        Snackbar.make(drawer, R.string.add_service_successful, Snackbar.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utility.showServerErrorFragment(error, ArrowheadServices.this);
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
        getMenuInflater().inflate(R.menu.arrowhead_services, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        mAdapter.setServiceList(serviceList);
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

        if (id == R.id.action_add_service) {
            DialogFragment newFragment = new AddNewServiceDialog();
            newFragment.show(getSupportFragmentManager(), AddNewServiceDialog.TAG);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<ArrowheadService> filteredModelList = filterList(serviceList, newText);
        mAdapter.setServiceList(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<ArrowheadService> filterList(List<ArrowheadService> services, String query) {
        query = query.toLowerCase();

        final List<ArrowheadService> filteredServiceList = new ArrayList<>();
        for (ArrowheadService service : services) {
            final String serviceGroup = service.getServiceGroup().toLowerCase();
            final String serviceDef = service.getServiceDefinition().toLowerCase();
            if (serviceGroup.contains(query) || serviceDef.contains(query)) {
                filteredServiceList.add(service);
            }
        }
        return filteredServiceList;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        return Utility.handleNavigationItemClick(item, ArrowheadServices.this);
    }

    @Override
    public void onRefresh() {
        srl.setRefreshing(true);
        sendGetAllRequest();
    }
}

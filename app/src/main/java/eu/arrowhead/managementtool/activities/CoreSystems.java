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
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.arrowhead.managementtool.R;
import eu.arrowhead.managementtool.adapters.CoreSystems_Adapter;
import eu.arrowhead.managementtool.fragments.AddNewCoreSystemDialog;
import eu.arrowhead.managementtool.model.CoreSystem;
import eu.arrowhead.managementtool.utility.Utility;
import eu.arrowhead.managementtool.volley.JsonArrayRequest;
import eu.arrowhead.managementtool.volley.Networking;

public class CoreSystems extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener,
        AddNewCoreSystemDialog.AddNewCoreSystemListener,
        SearchView.OnQueryTextListener {

    private DrawerLayout drawer;
    private RecyclerView mRecyclerView;
    private CoreSystems_Adapter mAdapter;
    private SwipeRefreshLayout srl;

    //TODO isSecure boolean nincs implementálva még, mivel tmit szerveren nem volt updatelve még a db
    private List<CoreSystem> systemList = new ArrayList<>();
    private static final String URL = "http://arrowhead.tmit.bme.hu:8081/api/configuration/coresystems";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core_systems);
        Toolbar toolbar = (Toolbar) findViewById(R.id.core_systems_toolbar);
        setSupportActionBar(toolbar);

        //navigation drawer setup
        drawer = (DrawerLayout) findViewById(R.id.core_systems_root_view);
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
                            systemList = Utility.fromJsonArray(response.toString(), CoreSystem.class);
                            if (mAdapter == null) {
                                mAdapter = new CoreSystems_Adapter(systemList);
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
                            Utility.showServerErrorFragment(error, CoreSystems.this);
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
    public void onSaveCoreSystemButtonClicked(DialogFragment dialog) {
        EditText systemNameEt = (EditText) dialog.getDialog().findViewById(R.id.system_name_edittext);
        EditText addressEt = (EditText) dialog.getDialog().findViewById(R.id.address_edittext);
        EditText portEt = (EditText) dialog.getDialog().findViewById(R.id.port_edittext);
        EditText serviceUriEt = (EditText) dialog.getDialog().findViewById(R.id.service_uri_edittext);
        EditText authInfoEt = (EditText) dialog.getDialog().findViewById(R.id.auth_info_edittext);

        if (systemNameEt.getText().toString().isEmpty()) {
            Toast.makeText(CoreSystems.this, R.string.mandatory_fields_warning, Toast.LENGTH_LONG).show();
        } else {
            CoreSystem system = new CoreSystem(systemNameEt.getText().toString(), addressEt.getText().toString(),
                    portEt.getText().toString(), serviceUriEt.getText().toString(), authInfoEt.getText().toString(), false);
            List<CoreSystem> systemList = Collections.singletonList(system);

            JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.POST, URL, Utility.toJsonArray(systemList),
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {
                            sendGetAllRequest();
                            Snackbar.make(drawer, R.string.add_core_system_successful, Snackbar.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utility.showServerErrorFragment(error, CoreSystems.this);
                        }
                    }
            );

            Networking.getInstance(this).addToRequestQueue(jsArrayRequest);
        }
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
        getMenuInflater().inflate(R.menu.core_systems, menu);

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

        if (id == R.id.action_add_core_system) {
            DialogFragment newFragment = new AddNewCoreSystemDialog();
            newFragment.show(getSupportFragmentManager(), AddNewCoreSystemDialog.TAG);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<CoreSystem> filteredModelList = filterList(systemList, newText);
        mAdapter.setSystemList(filteredModelList);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<CoreSystem> filterList(List<CoreSystem> systems, String query) {
        query = query.toLowerCase();

        final List<CoreSystem> filteredSystemList = new ArrayList<>();
        for (CoreSystem system : systems) {
            final String systemName = system.getSystemName().toLowerCase();
            final String address = system.getAddress().toLowerCase();
            if (systemName.contains(query) || address.contains(query)) {
                filteredSystemList.add(system);
            }
        }
        return filteredSystemList;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if(Utility.handleNavigationItemClick(item, CoreSystems.this)){
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

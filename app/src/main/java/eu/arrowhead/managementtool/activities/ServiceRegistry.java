package eu.arrowhead.managementtool.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import android.widget.ViewSwitcher;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eu.arrowhead.managementtool.R;
import eu.arrowhead.managementtool.adapters.ServiceRegistry_Adapter;
import eu.arrowhead.managementtool.model.ProvidedService;
import eu.arrowhead.managementtool.model.ServiceRegChild_ListEntry;
import eu.arrowhead.managementtool.model.ServiceRegParent_ListEntry;
import eu.arrowhead.managementtool.model.messages.ServiceQueryResult;
import eu.arrowhead.managementtool.utility.Utility;
import eu.arrowhead.managementtool.volley.JsonObjectRequest;
import eu.arrowhead.managementtool.volley.Networking;

public class ServiceRegistry extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener,
        SearchView.OnQueryTextListener {

    private DrawerLayout drawer;
    private RecyclerView mRecyclerView;
    private ServiceRegistry_Adapter mAdapter;
    private SwipeRefreshLayout srl;

    private List<ServiceRegParent_ListEntry> srList = new ArrayList<>();
    private static String URL;

    //TODO try to see if notifyDataSetChanged can work with the expandable list
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_registry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.service_registry_toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences prefs = getSharedPreferences("eu.arrowhead.managementtool", Context.MODE_PRIVATE);
        URL = Uri.parse(prefs.getString("sr_address", "")).buildUpon().appendPath("all").build().toString();

        //navigation drawer setup
        drawer = (DrawerLayout) findViewById(R.id.service_registry_root_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //recyclerview setup
        mRecyclerView = (RecyclerView) findViewById(R.id.sr_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        srl = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_sr_list);
        srl.setOnRefreshListener(this);

        sendGetAllRequest();
    }

    public void sendGetAllRequest() {
        final ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.sr_list_switcher);
        if (Utility.isConnected(this)) {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            ServiceQueryResult serviceQueryResult = Utility.fromJsonObject(response.toString(), ServiceQueryResult.class);
                            List<ProvidedService> psList = serviceQueryResult.getServiceQueryData();

                            srList.clear();
                            ServiceRegParent_ListEntry firstEntry = new ServiceRegParent_ListEntry();
                            firstEntry.setSystemGroup(psList.get(0).getProvider().getSystemGroup());
                            firstEntry.setSystemName(psList.get(0).getProvider().getSystemName());
                            List<ServiceRegChild_ListEntry> firstServices = new ArrayList<>();
                            firstServices.add(new ServiceRegChild_ListEntry(psList.get(0).getOffered().getServiceGroup(),
                                    psList.get(0).getOffered().getServiceDefinition(), psList.get(0).getServiceURI()));
                            firstEntry.setServices(firstServices);
                            srList.add(firstEntry);

                            boolean foundAMatch = false;
                            boolean serviceFound = true;
                            int entryIndex = 0;
                            for(ProvidedService ps : psList){
                                for(ServiceRegParent_ListEntry entry : srList){
                                    if(ps.getProvider().getSystemGroup().equals(entry.getSystemGroup()) &&
                                            ps.getProvider().getSystemName().equals(entry.getSystemName())){
                                        foundAMatch = true;

                                        ServiceRegChild_ListEntry childEntry = new ServiceRegChild_ListEntry(
                                                ps.getOffered().getServiceGroup(), ps.getOffered().getServiceDefinition(), ps.getServiceURI());
                                        if(!entry.getServices().contains(childEntry)){
                                            serviceFound = false;
                                            entryIndex = srList.indexOf(entry);
                                        }
                                    }
                                }
                                if(!foundAMatch){
                                    ServiceRegParent_ListEntry newEntry = new ServiceRegParent_ListEntry();
                                    newEntry.setSystemGroup(ps.getProvider().getSystemGroup());
                                    newEntry.setSystemName(ps.getProvider().getSystemName());

                                    List<ServiceRegChild_ListEntry> services = new ArrayList<>();
                                    services.add(new ServiceRegChild_ListEntry(ps.getOffered().getServiceGroup(),
                                            ps.getOffered().getServiceDefinition(), ps.getServiceURI()));
                                    newEntry.setServices(services);
                                    srList.add(newEntry);
                                }
                                if(!serviceFound){
                                    ServiceRegParent_ListEntry entry = srList.get(entryIndex);
                                    ServiceRegChild_ListEntry childEntry = new ServiceRegChild_ListEntry(
                                            ps.getOffered().getServiceGroup(), ps.getOffered().getServiceDefinition(), ps.getServiceURI());
                                    entry.getServices().add(childEntry);
                                }
                            }

                            mAdapter = new ServiceRegistry_Adapter(ServiceRegistry.this, srList);
                            mRecyclerView.setAdapter(mAdapter);

                            if (srList.size() > 0 && switcher.getDisplayedChild() == 1) {
                                //if the empty view is displayed at the moment, switch to the recyclerview
                                switcher.showPrevious();
                            }
                            if (switcher.getDisplayedChild() == 0 && srList.size() == 0) {
                                //if the recyclerview is displayed at the moment, switch to the empty view
                                switcher.showNext();
                            }
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (switcher.getDisplayedChild() == 0 && srList.size() == 0) {
                                //if the recyclerview is displayed at the moment, switch to the empty view
                                switcher.showNext();
                            }
                            Utility.showServerErrorFragment(error, ServiceRegistry.this);
                        }
                    }
            );

            Networking.getInstance(this).addToRequestQueue(jsObjRequest);
        } else {
            if (switcher.getDisplayedChild() == 0 && srList.size() == 0) {
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
        getMenuInflater().inflate(R.menu.service_registry, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        mAdapter = new ServiceRegistry_Adapter(ServiceRegistry.this, srList);
                        mRecyclerView.setAdapter(mAdapter);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<ServiceRegParent_ListEntry> filteredList = filterList(srList, newText);
        mAdapter = new ServiceRegistry_Adapter(ServiceRegistry.this, filteredList);
        mRecyclerView.setAdapter(mAdapter);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    //TODO valahogy jó lenne ha a lenyitott részek között is lehet keresni, check how is it possible
    private List<ServiceRegParent_ListEntry> filterList(List<ServiceRegParent_ListEntry> entries, String query) {
        query = query.toLowerCase();

        final List<ServiceRegParent_ListEntry> filteredList = new ArrayList<>();
        for (ServiceRegParent_ListEntry entry : entries) {
            final String systemName = entry.getSystemName().toLowerCase();
            final String systemGroup = entry.getSystemGroup().toLowerCase();
            if (systemName.contains(query) || systemGroup.contains(query)) {
                filteredList.add(entry);
            }
        }
        return filteredList;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if(Utility.handleNavigationItemClick(item, ServiceRegistry.this)){
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

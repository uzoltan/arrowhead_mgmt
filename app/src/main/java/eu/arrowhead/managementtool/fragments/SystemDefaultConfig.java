package eu.arrowhead.managementtool.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import eu.arrowhead.managementtool.R;
import eu.arrowhead.managementtool.adapters.ArrowheadSystem_DefaultAdapter;
import eu.arrowhead.managementtool.model.ArrowheadService;
import eu.arrowhead.managementtool.model.ArrowheadSystem;
import eu.arrowhead.managementtool.model.OrchestrationStore;
import eu.arrowhead.managementtool.model.SystemStoreBased_ListEntry;
import eu.arrowhead.managementtool.utility.Utility;
import eu.arrowhead.managementtool.volley.JsonArrayRequest;
import eu.arrowhead.managementtool.volley.Networking;

public class SystemDefaultConfig extends Fragment{

    private ArrowheadSystem system;

    private ViewSwitcher consumerSwitcher, providerSwitcher;
    private RecyclerView consumerRecView, providerRecView;
    private ArrowheadSystem_DefaultAdapter consumerAdapter, providerAdapter;

    private static String URL;

    public SystemDefaultConfig() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        system = (ArrowheadSystem) args.getSerializable("arrowhead_system");

        SharedPreferences prefs = getActivity().getSharedPreferences("eu.arrowhead.managementtool", Context.MODE_PRIVATE);
        URL = Uri.parse(prefs.getString("api_address", null)).buildUpon()
                .appendPath("orchestrator").appendPath("store").appendPath("all_active").build().toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_system_default_config, container, false);
        consumerSwitcher = (ViewSwitcher) layout.findViewById(R.id.consumer_default_config_switcher);
        providerSwitcher = (ViewSwitcher) layout.findViewById(R.id.provider_default_config_switcher);
        consumerRecView = (RecyclerView) layout.findViewById(R.id.consumer_default_config_list);
        providerRecView = (RecyclerView) layout.findViewById(R.id.provider_default_config_list);

        if (Utility.isConnected(getActivity())) {
            JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {
                            List<OrchestrationStore> storeList = Utility.fromJsonArray(response.toString(), OrchestrationStore.class);
                            List<OrchestrationStore> filteredConsumerStoreList = new ArrayList<>();
                            List<OrchestrationStore> filteredProviderStoreList = new ArrayList<>();
                            for(OrchestrationStore storeEntry : storeList){
                                if(storeEntry.getConsumer().equals(system)){
                                    filteredConsumerStoreList.add(storeEntry);
                                }
                                if(storeEntry.getProviderSystem().equals(system)){
                                    filteredProviderStoreList.add(storeEntry);
                                }
                            }
                            //A Set will not contain duplicates
                            HashSet<ArrowheadService> consumerServiceList = new HashSet<>();
                            HashSet<ArrowheadService> providerServiceList = new HashSet<>();
                            for(OrchestrationStore storeEntry : filteredConsumerStoreList){
                                consumerServiceList.add(storeEntry.getService());
                            }
                            for(OrchestrationStore storeEntry : filteredProviderStoreList){
                                providerServiceList.add(storeEntry.getService());
                            }

                            List<SystemStoreBased_ListEntry> groupedConsumerStoreList = new ArrayList<>();
                            for(ArrowheadService service : consumerServiceList){
                                List<OrchestrationStore> storeEntries = new ArrayList<>();
                                for(OrchestrationStore storeEntry : filteredConsumerStoreList){
                                    if(service.equals(storeEntry.getService())){
                                        storeEntry.setConsumerSide(true);
                                        storeEntries.add(storeEntry);
                                    }
                                }
                                groupedConsumerStoreList.add(new SystemStoreBased_ListEntry(service.getServiceGroup(), service.getServiceDefinition(), storeEntries));
                            }

                            List<SystemStoreBased_ListEntry> groupedProviderStoreList = new ArrayList<>();
                            for(ArrowheadService service : providerServiceList){
                                List<OrchestrationStore> storeEntries = new ArrayList<>();
                                for(OrchestrationStore storeEntry : filteredProviderStoreList){
                                    if(service.equals(storeEntry.getService())){
                                        storeEntries.add(storeEntry);
                                    }
                                }
                                groupedProviderStoreList.add(new SystemStoreBased_ListEntry(service.getServiceGroup(), service.getServiceDefinition(), storeEntries));
                            }

                            if(!groupedConsumerStoreList.isEmpty()){
                                if(consumerSwitcher.getDisplayedChild() == 1){
                                    consumerSwitcher.showPrevious();
                                }
                                consumerRecView.setHasFixedSize(true);
                                consumerRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(consumerRecView.getContext(),
                                        DividerItemDecoration.VERTICAL);
                                consumerRecView.addItemDecoration(dividerItemDecoration);
                                consumerAdapter = new ArrowheadSystem_DefaultAdapter(getActivity(), groupedConsumerStoreList);
                                consumerAdapter.onRestoreInstanceState(savedInstanceState);
                                consumerRecView.setAdapter(consumerAdapter);
                            } else{
                                if(consumerSwitcher.getDisplayedChild() == 0){
                                    consumerSwitcher.showNext();
                                }
                            }

                            if(!groupedProviderStoreList.isEmpty()){
                                if(providerSwitcher.getDisplayedChild() == 1){
                                    providerSwitcher.showPrevious();
                                }
                                providerRecView.setHasFixedSize(true);
                                providerRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(providerRecView.getContext(),
                                        DividerItemDecoration.VERTICAL);
                                providerRecView.addItemDecoration(dividerItemDecoration);
                                providerAdapter = new ArrowheadSystem_DefaultAdapter(getActivity(), groupedProviderStoreList);
                                providerAdapter.onRestoreInstanceState(savedInstanceState);
                                providerRecView.setAdapter(providerAdapter);
                            } else{
                                if(providerSwitcher.getDisplayedChild() == 0){
                                    providerSwitcher.showNext();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utility.showServerErrorFragment(error, (AppCompatActivity) getActivity());
                            if(consumerSwitcher.getDisplayedChild() == 0){
                                consumerSwitcher.showNext();
                            }
                            if(providerSwitcher.getDisplayedChild() == 0){
                                providerSwitcher.showNext();
                            }
                        }
                    }
            );

            Networking.getInstance(getActivity()).addToRequestQueue(jsArrayRequest);
        } else {
            Utility.showNoConnectionToast(getActivity());
            if(consumerSwitcher.getDisplayedChild() == 0){
                consumerSwitcher.showNext();
            }
            if(providerSwitcher.getDisplayedChild() == 0){
                providerSwitcher.showNext();
            }
        }

        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if(consumerAdapter != null){
            consumerAdapter.onSaveInstanceState(savedInstanceState);
        }
        if(providerAdapter != null){
            providerAdapter.onSaveInstanceState(savedInstanceState);
        }
    }
}

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
import eu.arrowhead.managementtool.adapters.ArrowheadSystem_AuthAdapter;
import eu.arrowhead.managementtool.model.ArrowheadService;
import eu.arrowhead.managementtool.model.ArrowheadSystem;
import eu.arrowhead.managementtool.model.IntraCloudAuthorization;
import eu.arrowhead.managementtool.model.SystemAuth_ListEntry;
import eu.arrowhead.managementtool.utility.Utility;
import eu.arrowhead.managementtool.volley.JsonArrayRequest;
import eu.arrowhead.managementtool.volley.Networking;

public class SystemAuthRights extends Fragment {

    private ArrowheadSystem system;

    private ViewSwitcher consumerSwitcher, providerSwitcher;
    private RecyclerView consumerRecView, providerRecView;
    private ArrowheadSystem_AuthAdapter consumerAdapter, providerAdapter;

    private static String URL;

    public SystemAuthRights() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        system = (ArrowheadSystem) args.getSerializable("arrowhead_system");

        SharedPreferences prefs = getActivity().getSharedPreferences("eu.arrowhead.managementtool", Context.MODE_PRIVATE);
        URL = Uri.parse(prefs.getString("api_address", null)).buildUpon()
                .appendPath("auth").appendPath("intracloud").build().toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_system_auth_rights, container, false);
        consumerSwitcher = (ViewSwitcher) layout.findViewById(R.id.consumer_intra_auth_list_switcher);
        providerSwitcher = (ViewSwitcher) layout.findViewById(R.id.provider_intra_auth_list_switcher);
        consumerRecView = (RecyclerView) layout.findViewById(R.id.consumer_intra_auth_list);
        providerRecView = (RecyclerView) layout.findViewById(R.id.provider_intra_auth_list);

        if (Utility.isConnected(getActivity())) {
            JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {
                            List<IntraCloudAuthorization> authList = Utility.fromJsonArray(response.toString(), IntraCloudAuthorization.class);
                            List<IntraCloudAuthorization> filteredConsumerAuthList = new ArrayList<>();
                            List<IntraCloudAuthorization> filteredProviderAuthList = new ArrayList<>();
                            for(IntraCloudAuthorization authEntry : authList){
                                if(authEntry.getConsumer().equals(system)){
                                    filteredConsumerAuthList.add(authEntry);
                                }
                                if(authEntry.getProvider().equals(system)){
                                    filteredProviderAuthList.add(authEntry);
                                }
                            }
                            //A Set will not contain duplicates
                            HashSet<ArrowheadService> consumerServiceList = new HashSet<>();
                            HashSet<ArrowheadService> providerServiceList = new HashSet<>();
                            for(IntraCloudAuthorization authEntry : filteredConsumerAuthList){
                                consumerServiceList.add(authEntry.getService());
                            }
                            for(IntraCloudAuthorization authEntry : filteredProviderAuthList){
                                providerServiceList.add(authEntry.getService());
                            }

                            List<SystemAuth_ListEntry> groupedConsumerAuthList = new ArrayList<>();
                            for(ArrowheadService service : consumerServiceList){
                                List<ArrowheadSystem> providers = new ArrayList<>();
                                for(IntraCloudAuthorization authEntry : filteredConsumerAuthList){
                                    if(service.equals(authEntry.getService())){
                                        providers.add(authEntry.getProvider());
                                    }
                                }
                                groupedConsumerAuthList.add(new SystemAuth_ListEntry(service.getServiceGroup(), service.getServiceDefinition(), providers));
                            }

                            List<SystemAuth_ListEntry> groupedProviderAuthList = new ArrayList<>();
                            for(ArrowheadService service : providerServiceList){
                                List<ArrowheadSystem> consumers = new ArrayList<>();
                                for(IntraCloudAuthorization authEntry : filteredProviderAuthList){
                                    if(service.equals(authEntry.getService())){
                                        consumers.add(authEntry.getConsumer());
                                    }
                                }
                                groupedProviderAuthList.add(new SystemAuth_ListEntry(service.getServiceGroup(), service.getServiceDefinition(), consumers));
                            }

                            if(!groupedConsumerAuthList.isEmpty()){
                                if(consumerSwitcher.getDisplayedChild() == 1){
                                    consumerSwitcher.showPrevious();
                                }
                                consumerRecView.setHasFixedSize(true);
                                consumerRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(consumerRecView.getContext(),
                                        DividerItemDecoration.VERTICAL);
                                consumerRecView.addItemDecoration(dividerItemDecoration);
                                consumerAdapter = new ArrowheadSystem_AuthAdapter(getActivity(), groupedConsumerAuthList);
                                consumerAdapter.onRestoreInstanceState(savedInstanceState);
                                consumerRecView.setAdapter(consumerAdapter);
                            } else{
                                if(consumerSwitcher.getDisplayedChild() == 0){
                                    consumerSwitcher.showNext();
                                }
                            }

                            if(!groupedProviderAuthList.isEmpty()){
                                if(providerSwitcher.getDisplayedChild() == 1){
                                    providerSwitcher.showPrevious();
                                }
                                providerRecView.setHasFixedSize(true);
                                providerRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(providerRecView.getContext(),
                                        DividerItemDecoration.VERTICAL);
                                providerRecView.addItemDecoration(dividerItemDecoration);
                                providerAdapter = new ArrowheadSystem_AuthAdapter(getActivity(), groupedProviderAuthList);
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

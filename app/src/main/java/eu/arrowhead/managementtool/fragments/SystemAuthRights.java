package eu.arrowhead.managementtool.fragments;


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

    private ViewSwitcher switcher;
    private RecyclerView mRecyclerView;
    private ArrowheadSystem_AuthAdapter mAdapter;

    //TODO hardwired URL
    private static final String URL = "http://arrowhead.tmit.bme.hu:8081/api/auth/intracloud";

    public SystemAuthRights() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        system = (ArrowheadSystem) args.getSerializable("arrowhead_system");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_system_auth_rights, container, false);
        switcher = (ViewSwitcher) layout.findViewById(R.id.intra_auth_list_switcher);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.intra_auth_list);

        if (Utility.isConnected(getActivity())) {
            JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {
                            List<IntraCloudAuthorization> authList = Utility.fromJsonArray(response.toString(), IntraCloudAuthorization.class);
                            List<IntraCloudAuthorization> filteredAuthList = new ArrayList<>();
                            for(IntraCloudAuthorization authEntry : authList){
                                if(authEntry.getConsumer().equals(system)){
                                    filteredAuthList.add(authEntry);
                                }
                            }
                            //A Set will not contain duplicates
                            HashSet<ArrowheadService> serviceList = new HashSet<>();
                            for(IntraCloudAuthorization authEntry : filteredAuthList){
                                serviceList.add(authEntry.getService());
                            }

                            List<SystemAuth_ListEntry> groupedAuthList = new ArrayList<>();
                            for(ArrowheadService service : serviceList){
                                List<ArrowheadSystem> providers = new ArrayList<>();
                                for(IntraCloudAuthorization authEntry : filteredAuthList){
                                    if(service.equals(authEntry.getService())){
                                        providers.add(authEntry.getProvider());
                                    }
                                }
                                groupedAuthList.add(new SystemAuth_ListEntry(service.getServiceGroup(), service.getServiceDefinition(), providers));
                            }

                            if(!groupedAuthList.isEmpty()){
                                if(switcher.getDisplayedChild() == 1){
                                    switcher.showPrevious();
                                }
                                mRecyclerView.setHasFixedSize(true);
                                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                                        DividerItemDecoration.VERTICAL);
                                mRecyclerView.addItemDecoration(dividerItemDecoration);
                                mAdapter = new ArrowheadSystem_AuthAdapter(getActivity(), groupedAuthList);
                                mAdapter.onRestoreInstanceState(savedInstanceState);
                                mRecyclerView.setAdapter(mAdapter);
                            } else{
                                if(switcher.getDisplayedChild() == 0){
                                    switcher.showNext();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Utility.showServerErrorFragment(error, (AppCompatActivity) getActivity());
                        }
                    }
            );

            Networking.getInstance(getActivity()).addToRequestQueue(jsArrayRequest);
        } else {
            Utility.showNoConnectionToast(getActivity());
        }

        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        mAdapter.onSaveInstanceState(savedInstanceState);
    }
}

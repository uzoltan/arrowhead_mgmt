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
import java.util.List;

import eu.arrowhead.managementtool.R;
import eu.arrowhead.managementtool.adapters.ArrowheadSystem_AuthAdapter;
import eu.arrowhead.managementtool.model.ArrowheadSystem;
import eu.arrowhead.managementtool.model.IntraCloudAuthorization;
import eu.arrowhead.managementtool.model.SystemAuth_ListEntry;
import eu.arrowhead.managementtool.utility.Utility;
import eu.arrowhead.managementtool.volley.JsonArrayRequest;
import eu.arrowhead.managementtool.volley.Networking;

public class SystemAuthRights extends Fragment {

    private ArrowheadSystem system;
    private List<IntraCloudAuthorization> authList = new ArrayList<>();
    private List<IntraCloudAuthorization> filteredAuthList = new ArrayList<>();
    private List<SystemAuth_ListEntry> groupedAuthList = new ArrayList<>();

    //TODO groupedAuthList összeállítása, ami mehet adapternek
    //TODO hardwired URL
    private static final String URL = "http://arrowhead.tmit.bme.hu:8081/api/auth/intracloud";

    public SystemAuthRights() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        system = (ArrowheadSystem) args.getSerializable("arrowhead_system");

        if (Utility.isConnected(getContext())) {
            JsonArrayRequest jsArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {
                            authList = Utility.fromJsonArray(response.toString(), IntraCloudAuthorization.class);
                            for(IntraCloudAuthorization authEntry : authList){
                                if(authEntry.getConsumer().equals(system)){
                                    filteredAuthList.add(authEntry);
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

            Networking.getInstance(getContext()).addToRequestQueue(jsArrayRequest);
        } else {
            Utility.showNoConnectionToast(getContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_system_auth_rights, container, false);
        ViewSwitcher switcher = (ViewSwitcher) layout.findViewById(R.id.intra_auth_list_switcher);
        RecyclerView mRecyclerView = (RecyclerView) layout.findViewById(R.id.intra_auth_list);

        if(!groupedAuthList.isEmpty()){
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                    DividerItemDecoration.VERTICAL);
            mRecyclerView.addItemDecoration(dividerItemDecoration);
            ArrowheadSystem_AuthAdapter mAdapter = new ArrowheadSystem_AuthAdapter(getContext(), groupedAuthList);
            mRecyclerView.setAdapter(mAdapter);
        } else{
            switcher.showNext();
        }

        return layout;
    }

}

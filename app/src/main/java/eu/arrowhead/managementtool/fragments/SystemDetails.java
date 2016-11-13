package eu.arrowhead.managementtool.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import eu.arrowhead.managementtool.R;
import eu.arrowhead.managementtool.model.ArrowheadSystem;

public class SystemDetails extends Fragment {

    private ArrowheadSystem system;

    public SystemDetails(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        system = (ArrowheadSystem) args.getSerializable("arrowhead_system");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_system_details, container, false);

        TextView systemGroupTv = (TextView) layout.findViewById(R.id.system_group_textview);
        TextView systemNameTv = (TextView) layout.findViewById(R.id.system_name_textview);
        TextView addressTv = (TextView) layout.findViewById(R.id.address_textview);
        TextView portTv = (TextView) layout.findViewById(R.id.port_textview);
        TextView authInfoTv = (TextView) layout.findViewById(R.id.auth_info_textview);

        systemGroupTv.setText(system.getSystemGroup());
        systemNameTv.setText(system.getSystemName());
        addressTv.setText(system.getAddress());
        portTv.setText(system.getPort());
        authInfoTv.setText(system.getAuthenticationInfo());

        return layout;
    }
}

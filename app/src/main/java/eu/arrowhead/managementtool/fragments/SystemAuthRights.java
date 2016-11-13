package eu.arrowhead.managementtool.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.arrowhead.managementtool.R;

public class SystemAuthRights extends Fragment {

    public SystemAuthRights() {
    }

    //TODO decide if this method is needed or not
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_system_auth_rights, container, false);
    }

}

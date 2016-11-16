package eu.arrowhead.managementtool.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import eu.arrowhead.managementtool.R;
import eu.arrowhead.managementtool.adapters.ArrowheadSystem_ViewPageAdapter;
import eu.arrowhead.managementtool.fragments.SystemAuthRights;
import eu.arrowhead.managementtool.fragments.SystemDefaultConfig;
import eu.arrowhead.managementtool.fragments.SystemDetails;
import eu.arrowhead.managementtool.fragments.SystemStoreEntries;
import eu.arrowhead.managementtool.model.ArrowheadSystem;

public class ArrowheadSystem_Detail extends AppCompatActivity {

    private ArrowheadSystem system;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrowhead_systems__detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.system_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        system = (ArrowheadSystem) intent.getSerializableExtra("arrowhead_system");

        ViewPager viewPager = (ViewPager) findViewById(R.id.system_viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.system_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ArrowheadSystem_ViewPageAdapter adapter = new ArrowheadSystem_ViewPageAdapter(getSupportFragmentManager());

        Bundle args = new Bundle();
        args.putSerializable("arrowhead_system", system);

        SystemDetails systemDetails = new SystemDetails();
        systemDetails.setArguments(args);
        SystemAuthRights systemAuthRights = new SystemAuthRights();
        systemAuthRights.setArguments(args);
        SystemDefaultConfig systemDefaultConfig = new SystemDefaultConfig();
        systemDefaultConfig.setArguments(args);
        SystemStoreEntries systemStoreEntries = new SystemStoreEntries();
        systemStoreEntries.setArguments(args);

        adapter.addFragment(systemDetails, "General");
        adapter.addFragment(systemAuthRights, "Auth Rights");
        adapter.addFragment(systemDefaultConfig, "Default Config");
        adapter.addFragment(systemStoreEntries, "Store Based");
        viewPager.setAdapter(adapter);
    }

}

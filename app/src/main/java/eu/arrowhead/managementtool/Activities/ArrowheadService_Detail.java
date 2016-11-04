package eu.arrowhead.managementtool.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import eu.arrowhead.managementtool.R;
import eu.arrowhead.managementtool.adapters.ArrowheadService_Interfaces_Adapter;
import eu.arrowhead.managementtool.model.ArrowheadService;

public class ArrowheadService_Detail extends AppCompatActivity {

    //TODO sendrequest függvények megírása, pozitív/negatív response ágak megírása.
    //sikeres updatenél maradunk az activityben, viewswitcherek vissza, updatelt értékek
    //sikeres törlésnél activityn finish()

    private Button saveButton;
    private TextView serviceGroupTv, serviceDefinitionTv;
    private EditText serviceGroupEt, serviceDefinitionEt, interfaceEt;
    private ViewSwitcher sgSwitcher, sdSwitcher, interfaceSwitcher;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //TODO replace hardwired url with proper solution
    //same for POST and PUT, DELETE has path params too
    private static final String URL = "http://arrowhead.tmit.bme.hu:8081/api/common/services";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrowhead_service__detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.services_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        saveButton = (Button) findViewById(R.id.save_changes_button);
        serviceGroupTv = (TextView) findViewById(R.id.service_group_textview);
        serviceDefinitionTv = (TextView) findViewById(R.id.service_definition_textview);
        serviceGroupEt = (EditText) findViewById(R.id.service_group_edittext);
        serviceDefinitionEt = (EditText) findViewById(R.id.service_definition_edittext);
        interfaceEt = (EditText) findViewById(R.id.interfaces_edittext);
        sgSwitcher = (ViewSwitcher) findViewById(R.id.service_group_switcher);
        sdSwitcher = (ViewSwitcher) findViewById(R.id.service_definition_switcher);
        interfaceSwitcher = (ViewSwitcher) findViewById(R.id.interface_list_switcher);
        mRecyclerView = (RecyclerView) findViewById(R.id.interface_list);

        Intent intent = getIntent();
        ArrowheadService service = (ArrowheadService) intent.getSerializableExtra("arrowhead_service");
        serviceGroupTv.setText(service.getServiceGroup());
        serviceDefinitionTv.setText(service.getServiceDefinition());

        //recyclerview setup
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mAdapter = new ArrowheadService_Interfaces_Adapter(service.getInterfaces());
        mRecyclerView.setAdapter(mAdapter);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!serviceGroupEt.getText().equals(serviceGroupTv.getText()) ||
                        !serviceDefinitionEt.getText().equals(serviceDefinitionTv.getText())){
                    sendUpdateRequest();
                }
                else{
                    //If the user changes the service group or service definition, we have to do a delete+post combo
                    sendDeleteRequest(true);
                }
            }
        });
        //TODO ezt használni majd interface lista készítéséhez a user inputból
        //List<String> elephantList = Arrays.asList(str.split(","));
    }

    public void sendUpdateRequest(){

    }

    public void sendDeleteRequest(boolean forcedUpdate){

    }

    public void sendPostRequest(){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.arrowhead_service_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit_service) {
            serviceGroupEt.setText(serviceGroupTv.getText());
            serviceDefinitionEt.setText(serviceDefinitionTv.getText());
            sgSwitcher.showNext();
            sdSwitcher.showNext();
            interfaceSwitcher.showNext();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

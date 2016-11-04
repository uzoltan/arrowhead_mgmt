package eu.arrowhead.managementtool.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import eu.arrowhead.managementtool.R;

//TESTING GSON and VOLLEY library shit
public class MainActivity extends AppCompatActivity {

    public TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.textview);
        Button servicesLauncher = (Button) findViewById(R.id.services_launcher);
        servicesLauncher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ArrowheadServices.class);
                startActivity(intent);
            }
        });

        /*ArrowheadCloud cloud = new ArrowheadCloud("1", "2", "3", "4", "5", "6");
        ArrayList<String> interfaces = new ArrayList<String>();
        interfaces.add("json");
        interfaces.add("json");
        interfaces.add("json");
        ServiceMetadata data = new ServiceMetadata("key", "value");
        ArrayList<ServiceMetadata> metadata = new ArrayList<ServiceMetadata>();
        metadata.add(data);

        ArrowheadService service = new ArrowheadService("group", "def", interfaces, metadata);
        ArrowheadService service2 = new ArrowheadService("group2", "def2", interfaces, metadata);
        ArrowheadService service3 = new ArrowheadService("group3", "def3", interfaces, metadata);
        ArrayList<ArrowheadService> services = new ArrayList<>();
        services.add(service);
        services.add(service2);
        services.add(service3);

        InterCloudAuthEntry entry = new InterCloudAuthEntry(cloud, services);

        String json = Utility.toJson(entry);
        Log.i("json test", json);

        InterCloudAuthEntry entry2 = Utility.fromJsonObject(json, InterCloudAuthEntry.class);
        String json2 = Utility.toJson(entry2);
        Log.i("json test", json2);*/
    }

    public void TextViewClicked(View view) {
        ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.my_switcher);
        switcher.showNext(); //or switcher.showPrevious();
    }


    @Override
    protected void onResume() {
        super.onResume();

        /*String url ="http://arrowhead.tmit.bme.hu:8084/orchestrator/orchestration";

        if (Utility.isConnected(this)) {
            JsonArrayRequest jsObjRequest = new JsonArrayRequest
                    (Request.Method.POST, url, null,
                            new Response.Listener<JSONArray>() {

                                @Override
                                public void onResponse(JSONArray response){
                                    mTextView.setText("Response: " + response.toString());
                                    List<ArrowheadService> serviceList = new ArrayList<ArrowheadService>();
                                    serviceList = Utility.fromJsonArray(response.toString(), ArrowheadService.class);
                                    Log.i("servicelist size", String.valueOf(serviceList.size()));
                                    for(ArrowheadService service : serviceList){
                                        Log.i("servicelist", service.toString());
                                    }
                                    Log.i("network_test", "it worked!");
                                }},
                            new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    mTextView.setText(error.getMessage());
                                    Log.i("network_test", error.toString());
                                }}
                    );

            Networking.getInstance(this).addToRequestQueue(jsObjRequest);
        } else {
            mTextView.setText("not connected");
            Log.i("network_test", "not connected");
        }*/

    }

}

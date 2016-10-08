package eu.arrowhead.managementtool;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import eu.arrowhead.managementtool.Utility.Networking;
import eu.arrowhead.managementtool.Utility.Utility;
import eu.arrowhead.managementtool.model.ArrowheadCloud;
import eu.arrowhead.managementtool.model.ArrowheadService;
import eu.arrowhead.managementtool.model.ServiceMetadata;
import eu.arrowhead.managementtool.model.messages.InterCloudAuthEntry;

//TESTING GSON and VOLLEY library shit
public class MainActivity extends AppCompatActivity {

    public TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.textview);

        ArrowheadCloud cloud = new ArrowheadCloud("1", "2", "3", "4", "5", "6");
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
        Log.i("json test", json2);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String url ="http://MODIFY_IP:8450/api/common/services";

        if (Utility.isConnected(this)) {
            JsonArrayRequest jsObjRequest = new JsonArrayRequest
                    (Request.Method.GET, url, null,
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
                                    Log.i("network_test", "it did not work :(");
                                }}
                    );

            Networking.getInstance(this).addToRequestQueue(jsObjRequest);
        } else {
            mTextView.setText("not connected");
            Log.i("network_test", "not connected");
        }

    }
}

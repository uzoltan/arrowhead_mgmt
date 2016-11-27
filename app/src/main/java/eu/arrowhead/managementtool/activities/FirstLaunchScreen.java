package eu.arrowhead.managementtool.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import eu.arrowhead.managementtool.R;

public class FirstLaunchScreen extends AppCompatActivity {

    private EditText apiAddress;
    private Button confirmButton;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        apiAddress = (EditText) findViewById(R.id.api_uri_input);
        confirmButton = (Button) findViewById(R.id.confirm_button);

        prefs = this.getSharedPreferences("eu.arrowhead.managementtool", Context.MODE_PRIVATE);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(apiAddress.getText().toString().isEmpty()){
                    Toast.makeText(FirstLaunchScreen.this, R.string.mandatory_fields_warning, Toast.LENGTH_LONG).show();
                }
                else{
                    prefs.edit().putBoolean("not_first_launch", true).apply();
                    finish();
                }
            }
        });
    }

}

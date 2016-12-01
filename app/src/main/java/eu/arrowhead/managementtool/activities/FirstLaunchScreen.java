package eu.arrowhead.managementtool.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import eu.arrowhead.managementtool.R;

public class FirstLaunchScreen extends AppCompatActivity {

    private EditText apiAddress, keystorePath;
    private Button browseButton, confirmButton;
    private SharedPreferences prefs;

    private static final int OPEN_FILE_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_launch_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        apiAddress = (EditText) findViewById(R.id.api_uri_input);
        keystorePath = (EditText) findViewById(R.id.keystore_input);
        browseButton = (Button) findViewById(R.id.browse_button);
        confirmButton = (Button) findViewById(R.id.confirm_button);

        prefs = this.getSharedPreferences("eu.arrowhead.managementtool", Context.MODE_PRIVATE);

        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
                intent.setDataAndType(uri, "file/*");
                startActivityForResult(Intent.createChooser(intent, "Choose keystore file"), OPEN_FILE_REQUEST);
            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                keystorePath.setText(uri.getPath());
                keystorePath.setSelection(keystorePath.getText().toString().length());
            }
            if (resultCode == RESULT_CANCELED) {
                //handle cancel
            }
        }
    }

}

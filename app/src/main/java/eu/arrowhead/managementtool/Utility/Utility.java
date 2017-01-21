package eu.arrowhead.managementtool.utility;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

import eu.arrowhead.managementtool.R;
import eu.arrowhead.managementtool.activities.ArrowheadClouds;
import eu.arrowhead.managementtool.activities.ArrowheadServices;
import eu.arrowhead.managementtool.activities.ArrowheadSystems;
import eu.arrowhead.managementtool.activities.CoreSystems;
import eu.arrowhead.managementtool.activities.ServiceRegistry;
import eu.arrowhead.managementtool.fragments.ServerErrorDialog;
import eu.arrowhead.managementtool.model.ErrorMessage;


public final class Utility {

    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

    private Utility(){
    }

    public static <T> JSONObject toJsonObject(T object){
        try{
            return new JSONObject(gson.toJson(object));
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static <T> JSONArray toJsonArray(T object) {
        try{
            return new JSONArray(gson.toJson(object));
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T fromJsonObject(String json, Class<T> parsedClass){
        return gson.fromJson(json, parsedClass);
    }

    public static <T> List<T> fromJsonArray(String json, final Class<T> parsedClass){
        return gson.fromJson(json, new ListOfJson<T>(parsedClass));
    }

    public static boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void showNoConnectionSnackbar(View rootView){
        Snackbar sb = Snackbar.make(rootView, R.string.no_connection, Snackbar.LENGTH_LONG);
        TextView sbText = (TextView) sb.getView().findViewById(android.support.design.R.id.snackbar_text);
        sbText.setTextSize(20f);
        sb.show();
    }

    public static void showNoConnectionToast(Context context){
        Toast.makeText(context, R.string.no_connection, Toast.LENGTH_LONG).show();
    }

    public static void showServerErrorFragment(VolleyError error, AppCompatActivity context){
        NetworkResponse response = error.networkResponse;
        if (response != null) { //error instanceof ServerErrorDialog condition too?
            try {
                String serverResponse = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                ErrorMessage errorMessage = fromJsonObject(serverResponse, ErrorMessage.class);
                DialogFragment newFragment = new ServerErrorDialog();
                Bundle args = new Bundle();
                args.putInt("status_code", errorMessage.getErrorCode());
                args.putString("error_message", errorMessage.getErrorMessage());
                newFragment.setArguments(args);
                newFragment.show(context.getSupportFragmentManager(), ServerErrorDialog.TAG);
            } catch (UnsupportedEncodingException e) {
                // Couldn't properly decode data to string
                Toast.makeText(context, R.string.server_side_error, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public static boolean handleNavigationItemClick(MenuItem item, AppCompatActivity activity){
        int id = item.getItemId();

        if (id == R.id.nav_arrowhead_systems) {
            if(activity.getClass() != ArrowheadSystems.class){
                Intent intent = new Intent(activity, ArrowheadSystems.class);
                activity.startActivity(intent);
            }
            return true;
        }
        else if(id == R.id.nav_arrowhead_services){
            if(activity.getClass() != ArrowheadServices.class){
                Intent intent = new Intent(activity, ArrowheadServices.class);
                activity.startActivity(intent);
            }
            return true;
        }
        else if(id == R.id.nav_arrowhead_clouds){
            if(activity.getClass() != ArrowheadClouds.class){
                Intent intent = new Intent(activity, ArrowheadClouds.class);
                activity.startActivity(intent);
            }
            return true;
        }
        else if(id == R.id.nav_core_systems){
            if(activity.getClass() != CoreSystems.class){
                Intent intent = new Intent(activity, CoreSystems.class);
                activity.startActivity(intent);
            }
            return true;
        }
        /*else if(id == R.id.nav_neighborhood_clouds){
            Toast.makeText(activity, R.string.not_implemented_yet, Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(id == R.id.nav_own_cloud){
            Toast.makeText(activity, R.string.not_implemented_yet, Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(id == R.id.nav_intracloud_auth){
            Toast.makeText(activity, R.string.not_implemented_yet, Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(id == R.id.nav_intercloud_auth){
            Toast.makeText(activity, R.string.not_implemented_yet, Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(id == R.id.nav_orch_store){
            Toast.makeText(activity, R.string.not_implemented_yet, Toast.LENGTH_SHORT).show();
            return false;
        }*/
        else if(id == R.id.nav_service_registry){
            if(activity.getClass() != ServiceRegistry.class){
                Intent intent = new Intent(activity, ServiceRegistry.class);
                activity.startActivity(intent);
            }
            return true;
        }

        return true;
    }

}

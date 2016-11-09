package eu.arrowhead.managementtool.utility;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import eu.arrowhead.managementtool.R;


public final class Utility {

    private static final Gson gson = new Gson();

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


}

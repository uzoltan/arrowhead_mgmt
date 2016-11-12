package eu.arrowhead.managementtool.volley;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

//Custom volley request with JSONObject payload. The 1 header is preset.
public class JsonObjectRequest extends JsonRequest<JSONObject> {

    private int method;

    public JsonObjectRequest(int method, String url, JSONObject jsonRequest,
                             Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener, errorListener);
        this.method = method;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            if(jsonString.isEmpty()){
                /*This is to allow the API coresystem to return responses without a JSON payload. The default volley JSON request
                * tries to create a JSONObject from the empty string payload, which causes an exception.*/
                return Response.success(new JSONObject("{\"test\":\"value\"}"), HttpHeaderParser.parseCacheHeaders(response));
            }
            else{
                return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
            }
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    //For some reason DELETE requests fail without this header, but PUT and POST requests fail if this header is set.
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if(method == 3){
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/json");
            return headers;
        }
        else{
         return super.getHeaders();
        }
    }
}

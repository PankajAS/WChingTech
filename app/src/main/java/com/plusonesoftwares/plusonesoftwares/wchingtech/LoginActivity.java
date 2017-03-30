package com.plusonesoftwares.plusonesoftwares.wchingtech;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    TextView txtCompanyName, txtUserName, txtPasswords;
    Button btnLogin;
    ProgressDialog progressDialog;
    JSONObject jsonObject;
    String loginUrl = "http://x.hkgws.com/x/servlet/JSONLoginServlet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtCompanyName = (TextView) findViewById(R.id.txtCompanyName);
        txtUserName = (TextView) findViewById(R.id.txtUserName);
        txtPasswords = (TextView) findViewById(R.id.txtPasswords);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        progressDialog = new ProgressDialog(this);
        jsonObject = new JSONObject();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                login();
            }
        });
    }

    private void login() {

        final String ComapnyName = txtCompanyName.getText().toString();
        final String UserName = txtUserName.getText().toString();
        final String Passowrd = txtPasswords.getText().toString();
        String device_unique_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        progressDialog.setMessage("Loading....");
        progressDialog.show();

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("company_id",ComapnyName);
            jsonBody.put("login_name",UserName);
            jsonBody.put("login_password",Passowrd);
            jsonBody.put("token",device_unique_id);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    progressDialog.dismiss();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("response", response);
                    intent.putExtra("ComapnyName", ComapnyName);
                    intent.putExtra("UserName", UserName);
                    intent.putExtra("Passowrd", Passowrd);
                    startActivity(intent);
                    finish();

                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            requestQueue.add(stringRequest);
            requestQueue.start();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("company_id",ComapnyName);
        params.put("login_name",UserName);
        params.put("login_password",Passowrd);
        params.put("token",device_unique_id);

        JsonObjectRequest req = new JsonObjectRequest(loginUrl, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));


                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("response", response.getString("login_status"));
                            intent.putExtra("ComapnyName", ComapnyName);
                            intent.putExtra("UserName", UserName);
                            intent.putExtra("Passowrd", Passowrd);
                            intent.putExtra("userdesc", response.getString("userdesc"));
                            intent.putExtra("companydesc", response.getString("companydesc"));
                            startActivity(intent);
                            finish();
                            progressDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),error.getMessage().toString(),Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(req);
        requestQueue.start();

    }

}

package com.plusonesoftwares.plusonesoftwares.wchingtech;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    TextView txtCompanyName, txtUserName, txtPasswords;
    Button btnLogin;
    ProgressDialog progressDialog;
    JSONObject jsonObject;

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

        String ComapnyName = txtCompanyName.getText().toString();
        String UserName = txtUserName.getText().toString();
        String Passowrd = txtPasswords.getText().toString();
        String device_unique_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        try {
            jsonObject.put("company_id",ComapnyName);
            jsonObject.put("login_name",UserName);
            jsonObject.put("login_password",Passowrd);
            jsonObject.put("token2",device_unique_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "http://x.hkgws.com/x/servlet/JSONLoginServlet";
        progressDialog.setMessage("Loading....");
        progressDialog.show();


        new AsyncRequest(getApplicationContext(), jsonObject).execute(url);
    }

    private class AsyncRequest extends AsyncTask<String, String, String> {

        JSONObject jsonObject = new JSONObject();
        Context context;

        public AsyncRequest(Context context, JSONObject jsonObject) {
            this.jsonObject = jsonObject;
            this.context = context;

        }


        @Override
        protected String doInBackground(String... url) {


            final String URL = url[0].toString();
// Post params to be sent to the server
            HashMap<String, String> params = new HashMap<String, String>();
            // params.put("token", "AbCdEfGh123456");
            params.put("login_name", "test");
            params.put("company_id", "web");
            params.put("login_password", "password");
            params.put("token2", "123456458");
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                VolleyLog.v("Response:%n %s", response.toString(4));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.e("Error: ", error.getMessage());
                }
            });
            queue.add(req);
            queue.start();


            return null;
        }

        @Override
        protected void onPostExecute(String loginresponse) {
            progressDialog.dismiss();
            Intent intent = new Intent(context, MainActivity.class);
            //intent.putExtra("response", loginresponse);
            //intent.putExtra("ComapnyName", ComapnyName);
            //intent.putExtra("UserName", UserName);
            //intent.putExtra("Passowrd", Passowrd);
            startActivity(intent);
            //finish();
            super.onPostExecute(loginresponse);
        }
    }
}

package com.plusonesoftwares.plusonesoftwares.wchingtech;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.plusonesoftwares.plusonesoftwares.wchingtech.FontManager.FontManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText txtCompanyName, txtUserName, txtPasswords;
    Button btnLogin;
    CommonClass utils;
    ProgressDialog progressDialog;
    JSONObject jsonObject;
    String loginUrl = "http://x.hkgws.com/x/servlet/JSONLoginServlet";
    String pushUrl = "http://x.hkgws.com/x/servlet/PushNotifications";
    LinearLayout linearLayout;
     String ComapnyName ;
     String UserName ;
     String Passowrd ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        utils = new CommonClass();
        //check session before loading of login screen if session exist
        if (utils.getUserPrefs(utils.UserName, getApplicationContext()) != null && !utils.getUserPrefs(utils.UserName, getApplicationContext()).isEmpty()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        else {

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

            txtCompanyName = (EditText) findViewById(R.id.txtCompanyName);
            txtUserName = (EditText) findViewById(R.id.txtUserName);
            txtPasswords = (EditText) findViewById(R.id.txtPasswords);
            utils.setFontForContainer(getApplicationContext(), linearLayout);
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
    }

    private void login() {

         ComapnyName = txtCompanyName.getText().toString();
         UserName = txtUserName.getText().toString();
         Passowrd = txtPasswords.getText().toString();
        final String device_unique_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        if (ComapnyName != null && UserName != null && Passowrd != null && !ComapnyName.isEmpty() && !UserName.isEmpty() && !Passowrd.isEmpty()) {

            progressDialog.setMessage("Loading....");
            progressDialog.show();

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            HashMap<String, String> params = new HashMap<String, String>();

            params.put("company_id", ComapnyName);
            params.put("login_name", UserName);
            params.put("login_password", Passowrd);
            params.put("token", device_unique_id);
            params.put("phone_type", "android");

            JsonObjectRequest req = new JsonObjectRequest(loginUrl, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                VolleyLog.v("Response:%n %s", response.toString(4));
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                manageSession(response);
                                startActivity(intent);

                                finish();
                                progressDialog.dismiss();
                                txtCompanyName.setText("");
                                txtPasswords.setText("");
                                txtUserName.setText("");
                              //  pushNotification(device_unique_id);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.e("Error: ", error.getMessage());
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });
            requestQueue.add(req);
            requestQueue.start();
        } else {
            Toast.makeText(getApplicationContext(), "Please fill the required information", Toast.LENGTH_SHORT).show();
        }

    }

    private void manageSession(JSONObject response) throws JSONException {
        utils.setUserPrefs(utils.response, response.getString("login_status") ,getApplicationContext());
        utils.setUserPrefs(utils.ComapnyName, ComapnyName ,getApplicationContext());
        utils.setUserPrefs(utils.UserName, UserName ,getApplicationContext());
        utils.setUserPrefs(utils.Passowrd, Passowrd ,getApplicationContext());
        utils.setUserPrefs(utils.userdesc, response.getString("userdesc") ,getApplicationContext());
        utils.setUserPrefs(utils.companydesc, response.getString("companydesc") ,getApplicationContext());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void pushNotification(final String token) {

        RequestQueue queue = Volley.newRequestQueue(this);
        HashMap<String, String> params = new HashMap<String, String>();

        StringRequest req = new StringRequest(Request.Method.POST, pushUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                NotificationManager notif = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notify = new Notification.Builder
                        (getApplicationContext())
                        .setContentTitle(response)
                        .setContentText(response)
                        .setSmallIcon(R.drawable.gear)
                        .build();

                PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                        new Intent(getApplicationContext(), LoginActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

                notify.contentIntent = contentIntent;

                notify.flags |= Notification.FLAG_AUTO_CANCEL;
                notif.notify(0, notify);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("type", "dev");
                params.put("message", "Welcome");
                return params;
            }

        };
        queue.add(req);
        queue.start();

    }
}

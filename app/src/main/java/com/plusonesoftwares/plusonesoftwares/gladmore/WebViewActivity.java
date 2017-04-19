package com.plusonesoftwares.plusonesoftwares.gladmore;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hkgws.gladmore.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WebViewActivity extends AppCompatActivity {

    WebView webView;
    String CompanyId, UserName, Passowrd, response, userdesc, companydesc,menushow;

    CommonClass utils;
    String pushUrl = "http://x.hkgws.com/x/servlet/PushNotifications";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        utils = new CommonClass();
        response = utils.getUserPrefs(utils.response, getApplicationContext());
        CompanyId = utils.getUserPrefs(utils.ComapnyId, getApplicationContext());
        UserName = utils.getUserPrefs(utils.UserName, getApplicationContext());
        Passowrd = utils.getUserPrefs(utils.Passowrd, getApplicationContext());
        userdesc = utils.getUserPrefs(utils.userdesc, getApplicationContext());
        companydesc = utils.getUserPrefs(utils.companydesc, getApplicationContext());
        menushow = utils.getUserPrefs(utils.menushow, getApplicationContext());

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new MyBrowser());

        progressDialog = new ProgressDialog(WebViewActivity.this);
        progressDialog.setMessage("Loading..");
        progressDialog.show();
        webView.loadUrl("http://x.hkgws.com/x/servlet/Login_process?login_name=" + UserName + "&login_password=" + Passowrd + "&company_id=" + CompanyId + "&storecompany=" + response + "&isMobile=Y");
        reload();
    }

    public void reload() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(!utils.getUserPrefs(utils.ComapnyId, getApplicationContext()).isEmpty()
                        && !utils.getUserPrefs(utils.UserName, getApplicationContext()).isEmpty()
                        && !utils.getUserPrefs(utils.Passowrd, getApplicationContext()).isEmpty()
                        && !utils.getUserPrefs(utils.response, getApplicationContext()).isEmpty()) {
                    reload();
                    webView.loadUrl("http://x.hkgws.com/x/servlet/Login_process?login_name=" + UserName + "&login_password=" + Passowrd + "&company_id=" + CompanyId + "&storecompany=" + response + "&isMobile=Y");
                    ValidateUser();//if user and password changed from backend it should redirect user to login page
                }
            }
        }, 720000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
              progressDialog.dismiss();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void pushNotification() {

        final String device_unique_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

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
                params.put("token", device_unique_id);
                params.put("type", "dev");
                params.put("message", "Welcome");
                return params;
            }

        };
        queue.add(req);
        queue.start();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                moveTaskToBack(true);
                return true;
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(menushow.equals("Y") || menushow.equals("")) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        System.out.println(id);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.END); /*Opens the Right Drawer*/
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void ValidateUser() {

        String loginUrl = "http://x.hkgws.com/x/servlet/JSONLoginServlet";
        try
        {
            final String device_unique_id = Settings.Secure.getString(this.getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            HashMap<String, String> params = new HashMap<String, String>();

            params.put("company_id", CompanyId);
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
                                if(response.getString("login_status").equals("N")) {

                                    AlertDialog.Builder messageDialog = new AlertDialog.Builder(WebViewActivity.this);
                                    messageDialog.setMessage("Session exipred please login!");
                                    messageDialog.setTitle("LOGIN");
                                    messageDialog.setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            ClearSession();
                                            Intent intent = new Intent(WebViewActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                    AlertDialog alert = messageDialog.create();
                                    alert.show();
                                }
                            }
                            catch (JSONException e) {
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
        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(), "login Method : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void ClearSession() {
        utils.setUserPrefs(utils.response, "" ,getApplicationContext());
        utils.setUserPrefs(utils.ComapnyId, "" ,getApplicationContext());
        utils.setUserPrefs(utils.UserName, "" ,getApplicationContext());
        utils.setUserPrefs(utils.Passowrd, "" ,getApplicationContext());
        utils.setUserPrefs(utils.userdesc, "" ,getApplicationContext());
        utils.setUserPrefs(utils.companydesc, "",getApplicationContext());
        utils.setUserPrefs(utils.menushow, "" ,getApplicationContext());
    }
}

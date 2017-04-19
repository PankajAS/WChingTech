package com.plusonesoftwares.plusonesoftwares.gladmore;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hkgws.gladmore.R;
import com.plusonesoftwares.plusonesoftwares.gladmore.app.Config;
import com.plusonesoftwares.plusonesoftwares.gladmore.util.NotificationUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    WebView webView;
    String CompanyId, UserName, Passowrd, response, userdesc, companydesc,menushow, CompanyName;
    final String leftMenu_Url = "http://x.hkgws.com/x/servlet/GetIOSMenu";
    final String rightMenu_Url = "http://x.hkgws.com/x/servlet/GetSysLanguage/";
    ListView left_drawer_list, right_drawer_list;
    JSONArray RightMenuArray;
    JSONArray LeftMenuArray;
    CommonClass utils;
    List<String> left_Menu_Items, right_Menu_Items, left_menu_icons;
    ArrayAdapter right_Menu_adapter;
    LeftMenuListAdapter left_Menu_adapter;
    DrawerLayout drawer;
    String pushUrl = "http://x.hkgws.com/x/servlet/PushNotifications";
    Toolbar toolbar;
    ProgressDialog progressDialog;
    //Firebase push block
    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            utils = new CommonClass();
            CompanyId = utils.getUserPrefs(utils.ComapnyId, getApplicationContext());
            CompanyName = utils.getUserPrefs(utils.ComapnyName, getApplicationContext());
            response = utils.getUserPrefs(utils.response, getApplicationContext());
            UserName = utils.getUserPrefs(utils.UserName, getApplicationContext());
            Passowrd = utils.getUserPrefs(utils.Passowrd, getApplicationContext());
            userdesc = utils.getUserPrefs(utils.userdesc, getApplicationContext());
            companydesc = utils.getUserPrefs(utils.companydesc, getApplicationContext());
            menushow = utils.getUserPrefs(utils.menushow, getApplicationContext());

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(CompanyName);
            setSupportActionBar(toolbar);
            Intent intent = getIntent();
            left_drawer_list = (ListView) findViewById(R.id.left_drawer_list);
            right_drawer_list = (ListView) findViewById(R.id.right_drawer_list);
            left_Menu_Items = new ArrayList<>();
            right_Menu_Items = new ArrayList<>();
            left_menu_icons = new ArrayList<>();

            utils.setUserPrefs(utils.SubMenuPageUrl, "", getApplicationContext());
            utils.setUserPrefs(utils.SelectedItem, "", getApplicationContext());

            webView = (WebView) findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.setWebViewClient(new MyBrowser());
            webView.loadUrl("http://x.hkgws.com/x/servlet/Login_process?login_name=" + UserName + "&login_password=" + Passowrd + "&company_id=" + CompanyId + "&storecompany=" + response + "&isMobile=Y");

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            //set header
            LayoutInflater inflater = getLayoutInflater();
            ViewGroup header = (ViewGroup) inflater.inflate(R.layout.nav_header_main, left_drawer_list, false);
            TextView txtuserdesc = (TextView) header.findViewById(R.id.txtusername);
            ImageView profileImage = (ImageView) header.findViewById(R.id.profileImage);
            Picasso.with(getApplicationContext()).load(utils.getUserPrefs(utils.companydesc, getApplicationContext())).into(profileImage);
            txtuserdesc.setText(userdesc);
            left_drawer_list.addHeaderView(header, null, false);
            left_Menu_adapter = new LeftMenuListAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, left_Menu_Items, left_menu_icons);
            left_drawer_list.setAdapter(left_Menu_adapter);

            //set footer
            ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.logout_event_layout, right_drawer_list, false);
            right_drawer_list.addFooterView(footer);
            LinearLayout linearLayout = (LinearLayout) footer.findViewById(R.id.linearlayout);
            right_Menu_adapter = new RightMenuListAdapter(getApplicationContext(), R.layout.single_item_layout_right_menu, right_Menu_Items);
            right_drawer_list.setAdapter(right_Menu_adapter);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //clear session on logout click
                    utils.setUserPrefs(utils.response, "", getApplicationContext());
                    utils.setUserPrefs(utils.ComapnyId, "", getApplicationContext());
                    utils.setUserPrefs(utils.UserName, "", getApplicationContext());
                    utils.setUserPrefs(utils.Passowrd, "", getApplicationContext());
                    utils.setUserPrefs(utils.userdesc, "", getApplicationContext());
                    utils.setUserPrefs(utils.companydesc, "", getApplicationContext());

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            getLeftMenu("en");
        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(), "onCreate Main: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        reload();

        //Firebase push block

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    //txtMessage.setText(message);
                }
            }
        };

       // displayFirebaseRegId();
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);
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
                    toolbar.setTitle(CompanyName);
                    ValidateUser();//validate user after some time interval
                }
            }
        }, 720000);
    }



    @Override
    protected void onResume() {
        super.onResume();
        try{
            if (utils.getUserPrefs(utils.SubMenuPageUrl, getApplicationContext()) != null && !utils.getUserPrefs(utils.SubMenuPageUrl, getApplicationContext()).isEmpty()) {
                webView.loadUrl(utils.getUserPrefs(utils.SubMenuPageUrl, getApplicationContext()));
                toolbar.setTitle(utils.getUserPrefs(utils.SelectedItem, getApplicationContext()));
            }
        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(), "onResume : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        //Firebase push block

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    //Firebase push block
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void getRightMenu() {
        // Post params to be sent to the server
        HashMap<String, String> params1 = new HashMap<String, String>();
        params1.put("company_id", CompanyId);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(rightMenu_Url, new JSONObject(params1),
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            VolleyLog.v("Response:%n %s", response.toString(4));

                            RightMenuArray = response.getJSONArray("language");

                            for (int i = 0; i <= RightMenuArray.length() - 1; i++) {
                                JSONObject obj = RightMenuArray.getJSONObject(i);
                                System.out.println("menu " + obj.get("description"));
                                right_Menu_Items.add(obj.get("description").toString());

                            }
                            right_Menu_adapter.notifyDataSetChanged();
                            //pushNotification();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public RetryPolicy getRetryPolicy() {

                return new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            }
        };

        requestQueue.add(req);
        requestQueue.start();

        right_drawer_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    JSONObject jsonObject = RightMenuArray.getJSONObject(i);
                    //jsonObject.getString("key_value");
                    getLeftMenu(jsonObject.getString("key_value"));
                    webView.loadUrl("http://x.hkgws.com/x/app_main.do?language_sel_input="+ jsonObject.getString("key_value"));//refreshing web view after selection of language
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                drawer.closeDrawer(Gravity.END);
            }
        });
    }

    @NonNull
    private void getLeftMenu(String language) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("login_name", UserName);
        params.put("company_id", CompanyId);
        params.put("language", language);//using defauld language on page load
        params.put("menushow", menushow.equals("")?"Y": menushow);//using defauld language on page load

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        //call for left Menu
        JsonObjectRequest req = new JsonObjectRequest(leftMenu_Url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                            left_Menu_Items.clear();
                            LeftMenuArray = response.getJSONArray("menu_base");
                            for (int i = 0; i <= LeftMenuArray.length() - 1; i++) {
                                JSONObject obj = LeftMenuArray.getJSONObject(i);

                                left_Menu_Items.add(utils.getEncodedString(obj.get("menu_description").toString()));
                                left_menu_icons.add(new FindFontAwesomeIcons().getString(obj.get("menu_icon").toString()));
                            }
                            left_Menu_adapter.notifyDataSetChanged();
                            if(right_Menu_Items.size()<=0) {
                                getRightMenu();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public RetryPolicy getRetryPolicy() {

                return new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            }
        };
        queue.add(req);
        queue.start();

        left_drawer_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    JSONObject obj = LeftMenuArray.getJSONObject(i - 1);
                    System.out.println(obj.get("menu_description"));
                    if (left_Menu_Items.get(i - 1).toString().equals(utils.getEncodedString(obj.get("menu_description").toString()))) {
                        JSONArray array = obj.getJSONArray("submenu");
                        utils.setUserPrefs(utils.SubMenuPageUrl, "", getApplicationContext());
                        Intent intent = new Intent(getApplicationContext(), FacilitiesActivity.class);
                        intent.putExtra("submenu", array.toString());
                        startActivity(intent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                drawer.closeDrawer(Gravity.START);
            }
        });
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (utils.getUserPrefs(utils.SubMenuPageUrl, getApplicationContext()) != null && !utils.getUserPrefs(utils.SubMenuPageUrl, getApplicationContext()).isEmpty())
            {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Loading..");
                progressDialog.show();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (utils.getUserPrefs(utils.SubMenuPageUrl, getApplicationContext()) != null && !utils.getUserPrefs(utils.SubMenuPageUrl, getApplicationContext()).isEmpty()) {
                progressDialog.dismiss();
            }
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
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        try {
            for (int i = 0; i <= LeftMenuArray.length() - 1; i++) {
                JSONObject obj = LeftMenuArray.getJSONObject(i);
                System.out.println(obj.get("menu_description"));
                if (item.toString().equals(obj.get("menu_description"))) {
                    JSONArray array = obj.getJSONArray("submenu");
                    utils.setUserPrefs(utils.SubMenuPageUrl, "", getApplicationContext());
                    Intent intent = new Intent(this, FacilitiesActivity.class);
                    intent.putExtra("submenu", array.toString());
                    startActivity(intent);
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

                                    AlertDialog.Builder messageDialog = new AlertDialog.Builder(MainActivity.this);
                                    messageDialog.setMessage("Session exipred please login!");
                                    messageDialog.setTitle("LOGIN");
                                    messageDialog.setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            ClearSession();
                                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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
        utils.setUserPrefs(utils.ComapnyName, "" ,getApplicationContext());
        utils.setUserPrefs(utils.UserName, "" ,getApplicationContext());
        utils.setUserPrefs(utils.Passowrd, "" ,getApplicationContext());
        utils.setUserPrefs(utils.userdesc, "" ,getApplicationContext());
        utils.setUserPrefs(utils.companydesc, "",getApplicationContext());
        utils.setUserPrefs(utils.menushow, "" ,getApplicationContext());
    }
}

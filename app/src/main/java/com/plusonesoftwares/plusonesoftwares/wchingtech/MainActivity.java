package com.plusonesoftwares.plusonesoftwares.wchingtech;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    String CompanyName, UserName, Passowrd, response, userdesc, companydesc;
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
    List<String> listicons;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Home");
            setSupportActionBar(toolbar);
            Intent intent = getIntent();
            utils = new CommonClass();
            left_drawer_list = (ListView) findViewById(R.id.left_drawer_list);
            right_drawer_list = (ListView) findViewById(R.id.right_drawer_list);
            left_Menu_Items = new ArrayList<>();
            right_Menu_Items = new ArrayList<>();
            left_menu_icons = new ArrayList<>();

            utils.setUserPrefs(utils.SubMenuPageUrl, "", getApplicationContext());
            utils.setUserPrefs(utils.SelectedItem, "", getApplicationContext());

            response = utils.getUserPrefs(utils.response, getApplicationContext());
            CompanyName = utils.getUserPrefs(utils.ComapnyName, getApplicationContext());
            UserName = utils.getUserPrefs(utils.UserName, getApplicationContext());
            Passowrd = utils.getUserPrefs(utils.Passowrd, getApplicationContext());
            userdesc = utils.getUserPrefs(utils.userdesc, getApplicationContext());
            companydesc = utils.getUserPrefs(utils.companydesc, getApplicationContext());

            webView = (WebView) findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.setWebViewClient(new MyBrowser());
            webView.loadUrl("http://x.hkgws.com/x/servlet/Login_process?login_name=" + UserName + "&login_password=" + Passowrd + "&company_id=" + CompanyName + "&storecompany=" + response + "&isMobile=Y");

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            //set adapter
            left_Menu_adapter = new LeftMenuListAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, left_Menu_Items, left_menu_icons);
            right_Menu_adapter = new RightMenuListAdapter(getApplicationContext(), R.layout.single_item_layout_right_menu, right_Menu_Items);
            left_drawer_list.setAdapter(left_Menu_adapter);
            right_drawer_list.setAdapter(right_Menu_adapter);

            //set header
            LayoutInflater inflater = getLayoutInflater();
            ViewGroup header = (ViewGroup) inflater.inflate(R.layout.nav_header_main, left_drawer_list, false);
            TextView txtuserdesc = (TextView) header.findViewById(R.id.txtusername);
            ImageView profileImage = (ImageView) header.findViewById(R.id.profileImage);
            Picasso.with(getApplicationContext()).load(utils.getUserPrefs(utils.companydesc, getApplicationContext())).into(profileImage);
            txtuserdesc.setText(userdesc);
            left_drawer_list.addHeaderView(header, null, false);

            //set footer
            ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.logout_event_layout, right_drawer_list, false);
            right_drawer_list.addFooterView(footer);
            LinearLayout linearLayout = (LinearLayout) footer.findViewById(R.id.linearlayout);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //clear session on logout click
                    utils.setUserPrefs(utils.response, "", getApplicationContext());
                    utils.setUserPrefs(utils.ComapnyName, "", getApplicationContext());
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
    }

    @Override
    protected void onResume() {
        try{

            if (utils.getUserPrefs(utils.SubMenuPageUrl, getApplicationContext()) != null && !utils.getUserPrefs(utils.SubMenuPageUrl, getApplicationContext()).isEmpty()) {
                webView.loadUrl(utils.getUserPrefs(utils.SubMenuPageUrl, getApplicationContext()));
                toolbar.setTitle(utils.getUserPrefs(utils.SelectedItem, getApplicationContext()));
            }
            super.onResume();
        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(), "onResume : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void getRightMenu() {
        // Post params to be sent to the server
        HashMap<String, String> params1 = new HashMap<String, String>();
        params1.put("company_id", CompanyName);
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
                            pushNotification();
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
        params.put("company_id", CompanyName);
        params.put("language", language);//using defauld language on page load

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


}

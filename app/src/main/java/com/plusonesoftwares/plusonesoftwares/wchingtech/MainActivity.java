package com.plusonesoftwares.plusonesoftwares.wchingtech;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    WebView webView;
    String CompanyName, UserName, Passowrd,response;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        CompanyName = intent.getStringExtra("ComapnyName");
        UserName = intent.getStringExtra("UserName");
        Passowrd = intent.getStringExtra("Passowrd");
        response = intent.getStringExtra("response");
        String url = "http://x.hkgws.com/x/servlet/Login_process?login_name=test&login_password=passowrd&company_id=web&storecompany=N&isMobile=Y";

        webView = (WebView)findViewById(R.id.webView);
        webView.setWebViewClient(new MyBrowser());
        webView.loadUrl(url);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemTextColor(ColorStateList.valueOf(Color.WHITE));

        NavigationView nav_right_view = (NavigationView) findViewById(R.id.nav_right_view);
        nav_right_view.setItemTextColor(ColorStateList.valueOf(Color.WHITE));
        nav_right_view.setNavigationItemSelectedListener(this);

      //  new AsyncClass(UserName,CompanyName,"en").execute("http://x.hkgws.com/x/servlet/GetIOSMenu");

        final String URL = "http://x.hkgws.com/x/servlet/GetIOSMenu";
// Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        // params.put("token", "AbCdEfGh123456");
        params.put("login_name", "test");
        params.put("company_id", "web");
        params.put("language", "en");

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));

                            JSONArray jsonarray = response.getJSONArray("menu_base");
                            Menu menu = navigationView.getMenu();

                            for(int i=0;i<=jsonarray.length()-1;i++){
                                JSONObject obj = jsonarray.getJSONObject(i);
                                System.out.println(obj.get("menu_description"));
                                menu.add(obj.get("menu_description").toString());
                                System.out.println(menu.getItem(i));

                            }


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

    }

    private class AsyncClass extends AsyncTask<String,String,JSONObject>{
        String UserName, CompanyName,language;

        public AsyncClass(String UserName, String CompanyName,String language){
            this.UserName = UserName;
            this.CompanyName = CompanyName;
            this.language = language;
        }


         @Override
        protected JSONObject doInBackground(String... strings) {
             JSONObject jsonObject = new JSONObject();

             try {
                 URL url = new URL(strings[0]);



        } catch (MalformedURLException e) {
                 e.printStackTrace();

             }
             return jsonObject;
             }

             @Override
        protected void onPostExecute(JSONObject jsonObject) {

            super.onPostExecute(jsonObject);
        }
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
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
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action

            webView.loadUrl("https://firebase.google.com/docs/notifications/android/console-device");
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(this,FacilitiesActivity.class);
            startActivity(intent);
            webView.loadUrl("https://firebase.google.com/docs/notifications/android/console-device");
        } else if (id == R.id.nav_slideshow) {
            webView.loadUrl("https://firebase.google.com/docs/notifications/android/console-device");
        } else if (id == R.id.nav_manage) {
            webView.loadUrl("https://firebase.google.com/docs/notifications/android/console-device");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

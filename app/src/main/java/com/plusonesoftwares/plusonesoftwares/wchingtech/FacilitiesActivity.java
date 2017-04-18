package com.hkgws.gladmore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FacilitiesActivity extends AppCompatActivity {

    ListView list;
    List<String> subMenuDsclist, subMenuPageUrl, subMenuIcon;
    CommonClass util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facilities);
        util = new CommonClass();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        subMenuDsclist = new ArrayList<>();
        subMenuPageUrl = new ArrayList<>();
        subMenuIcon = new ArrayList<>();
        list = (ListView)findViewById(R.id.list);
        Intent intent = getIntent();

        SubMenuAdapter adapter = new SubMenuAdapter(getApplicationContext(), R.layout.left_menu_list_items, subMenuDsclist, subMenuIcon);
        list.setAdapter(adapter);
        util.setFontForContainer(getApplicationContext(),list);

        try {
            JSONArray array = new JSONArray(intent.getStringExtra("submenu"));

            for(int i =0;i<=array.length()-1;i++){
                JSONObject obj = array.getJSONObject(i);
                subMenuDsclist.add(util.getEncodedString(obj.getString("sub_menu_description").toString()));
                subMenuPageUrl.add(obj.getString("sub_menu_page"));
                subMenuIcon.add(new FindFontAwesomeIcons().getString(obj.get("sub_menu_icon").toString()));
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "FacilitiesActivity: onCreate : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    util.setUserPrefs(util.SubMenuPageUrl, subMenuPageUrl.get(i), getApplicationContext());
                    util.setUserPrefs(util.SelectedItem, subMenuDsclist.get(i), getApplicationContext());
                    finish();
                }
                catch(Exception e)
                {
                    Toast.makeText(getApplicationContext(), "FacilitiesActivity: setOnItemClickListener : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}

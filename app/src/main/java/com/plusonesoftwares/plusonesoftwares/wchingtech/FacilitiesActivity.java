package com.plusonesoftwares.plusonesoftwares.wchingtech;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FacilitiesActivity extends AppCompatActivity {

    ListView list;
    List<String> subMenuDsclist, subMenuPageUrl;
    CommonClass util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facilities);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        subMenuDsclist = new ArrayList<>();
        subMenuPageUrl = new ArrayList<>();
        list = (ListView)findViewById(R.id.list);
        Intent intent = getIntent();
        util = new CommonClass();
        try {
            JSONArray array = new JSONArray(intent.getStringExtra("submenu"));

            for(int i =0;i<=array.length()-1;i++){
                JSONObject obj = array.getJSONObject(i);
                subMenuDsclist.add(obj.getString("sub_menu_description"));
                subMenuPageUrl.add(obj.getString("sub_menu_page"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.single_item_layout,R.id.txtItem, subMenuDsclist);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               /* Intent intentToReturn = new Intent(FacilitiesActivity.this, MainActivity.class);
                intentToReturn.putExtra("sub_menu_page",subMenuPageUrl.get(i));
                startActivity(intentToReturn);*/
                util.setUserPrefs(util.SubMenuPageUrl,subMenuPageUrl.get(i),getApplicationContext());
                finish();
            }
        });

    }
}

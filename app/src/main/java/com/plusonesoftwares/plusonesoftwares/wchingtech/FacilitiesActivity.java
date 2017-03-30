package com.plusonesoftwares.plusonesoftwares.wchingtech;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class FacilitiesActivity extends AppCompatActivity {

    ListView list;
    List<String> arraylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facilities);
        list = (ListView)findViewById(R.id.list);
        arraylist = new ArrayList<>();
        arraylist.add("Fisrt");
        arraylist.add("second");
        arraylist.add("third");
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,arraylist);
        list.setAdapter(adapter);

    }
}

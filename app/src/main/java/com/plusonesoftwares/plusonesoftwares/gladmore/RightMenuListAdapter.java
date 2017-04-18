package com.plusonesoftwares.plusonesoftwares.gladmore;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.plusonesoftwares.plusonesoftwares.gladmore.FontManager.FontManager;
import com.hkgws.gladmore.R;

import java.util.List;

/**
 * Created by Plus 3 on 03-04-2017.
 */

public class RightMenuListAdapter extends ArrayAdapter {
    List<String> menuItems;
    Context context;
    CommonClass utils;
    FontManager fontManager;

    public RightMenuListAdapter(Context context, int resource, List<String> menuItems) {
        super(context, resource, menuItems);
        this.menuItems = menuItems;
        this.context = context;
        utils = new CommonClass();
        fontManager = new FontManager();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RightMenuListAdapter.ViewHolder holder= null;
        if(convertView == null) {
            holder = new RightMenuListAdapter.ViewHolder();
            LayoutInflater inflater=LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.single_item_layout_right_menu, null ,true);
            utils.setFontForContainer(context,parent);
            holder.category = (TextView) convertView.findViewById(R.id.language);

            convertView.setTag(holder);

        }else{
            holder = (RightMenuListAdapter.ViewHolder) convertView.getTag();
        }
        holder.category.setText(menuItems.get(position));

        return convertView;
    }

    private class ViewHolder{
        TextView category;
    }
}

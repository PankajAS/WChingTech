package com.plusonesoftwares.plusonesoftwares.wchingtech;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.plusonesoftwares.plusonesoftwares.wchingtech.FontManager.FontManager;

import java.util.List;
import java.util.Map;

/**
 * Created by Plus 3 on 03-04-2017.
 */

public class SubMenuAdapter extends ArrayAdapter {
    List<String> itemName, itemIcon;
    Context context;
    CommonClass utils;

    public SubMenuAdapter(Context context, int resource, List<String> itmeNames, List<String> itemIcon) {
        super(context, resource, itmeNames);
        this.context = context;
        this.itemName = itmeNames;
        this.itemIcon = itemIcon;
        utils = new CommonClass();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SubMenuAdapter.ViewHolder holder = null;
        if (convertView == null) {
            holder = new SubMenuAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.left_menu_list_items, null, true);
            utils.setFontForContainer(context, parent);
            holder.category = (TextView) convertView.findViewById(R.id.Title);
            holder.category_icon = (TextView) convertView.findViewById(R.id.category_icon);
            holder.ivArrow = (ImageView) convertView.findViewById(R.id.ivArrow);
            convertView.setTag(holder);

        }else{
            holder = (SubMenuAdapter.ViewHolder) convertView.getTag();
        }

        holder.ivArrow.setVisibility(View.GONE);
        Typeface iconCalendar = FontManager.getTypeface(
                holder.category.getContext(),
                FontManager.FONTAWESOME);
        holder.category.setTypeface(iconCalendar);
        holder.category.setText(itemName.get(position));
        holder.category.setTextColor(Color.WHITE);

        Typeface icon = FontManager.getTypeface(
                holder.category_icon.getContext(),
                FontManager.FONTAWESOME);
        holder.category_icon.setTypeface(icon);
        holder.category_icon.setTextColor(Color.WHITE);

        for(Map.Entry<String, String> entry: new FindFontAwesomeIcons().getmIcons().entrySet()){
            if(itemIcon.get(position).equals(entry.getKey())){
                System.out.println(entry.getValue());
                holder.category_icon.setText(Html.fromHtml(entry.getValue()));
                break;
            }
        }

        return convertView;
    }

    private class ViewHolder{
        TextView category, category_icon;
        ImageView ivArrow;
    }
}

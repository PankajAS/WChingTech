package com.plusonesoftwares.plusonesoftwares.wchingtech;

import android.content.Context;
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

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Plus 3 on 28-02-2017.
 */

public class LeftMenuListAdapter extends ArrayAdapter {
    List<String> menuItems,menuIcon;
    Context context;
    CommonClass utils;
    FontManager fontManager;

    public LeftMenuListAdapter(Context context, int resource, List<String> menuItems, List<String> menuIcon) {
        super(context, resource, menuItems);
        this.menuIcon = menuIcon;
        this.menuItems = menuItems;
        this.context = context;
        utils = new CommonClass();
        fontManager = new FontManager();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LeftMenuListAdapter.ViewHolder holder= null;
        if(convertView == null) {
            holder = new LeftMenuListAdapter.ViewHolder();
            LayoutInflater inflater=LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.left_menu_list_items, null ,true);
            utils.setFontForContainer(context,parent);

                holder.category = (TextView) convertView.findViewById(R.id.Title);
                holder.category_icon = (TextView) convertView.findViewById(R.id.category_icon);

            convertView.setTag(holder);

        }else{
            holder = (LeftMenuListAdapter.ViewHolder) convertView.getTag();
        }

        Typeface iconCalendar = FontManager.getTypeface(
                holder.category.getContext(),
                FontManager.FONTAWESOME);
        holder.category.setTypeface(iconCalendar);
        holder.category.setText(menuItems.get(position));

        Typeface icon = FontManager.getTypeface(
                holder.category_icon.getContext(),
                FontManager.FONTAWESOME);
        holder.category_icon.setTypeface(icon);

            for(Map.Entry<String, String> entry: new FindFontAwesomeIcons().getmIcons().entrySet()){
                if(menuIcon.get(position).equals(entry.getKey())){
                    System.out.println(entry.getValue());
                    holder.category_icon.setText(Html.fromHtml(entry.getValue()));
                    break;
                }
            }

        //  Picasso.with(context).load(menuIcon.get(position)).into(holder.category_image);
       /* String base64Image = (String) categoryListobj.get(position).get("image");

        if(base64Image != null && !base64Image.isEmpty()) {
            byte[] imageAsBytes = Base64.decode(base64Image.getBytes(), Base64.DEFAULT);
            Bitmap image = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            holder.category_image.setImageBitmap(image);
        }*/

        return convertView;
    }

 private class ViewHolder{
         TextView category, category_icon;
    }
}

package com.plusonesoftwares.plusonesoftwares.wchingtech;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Plus 3 on 28-02-2017.
 */

public class LeftMenuListAdapter extends ArrayAdapter {
    List<String> menuItems,menuIcon;
    Context context;

    public LeftMenuListAdapter(Context context, int resource, List<String> menuItems, List<String> menuIcon) {
        super(context, resource, menuItems);
        this.menuIcon = menuIcon;
        this.menuItems = menuItems;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LeftMenuListAdapter.ViewHolder holder= null;
        if(convertView == null) {
            holder = new LeftMenuListAdapter.ViewHolder();
            LayoutInflater inflater=LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.left_menu_list_items, null ,true);
            holder.category = (TextView) convertView.findViewById(R.id.Title);
            holder.category_image = (ImageView) convertView.findViewById(R.id.category_image);
            convertView.setTag(holder);

        }else{
            holder = (LeftMenuListAdapter.ViewHolder) convertView.getTag();
        }

        holder.category.setText(menuItems.get(position));
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
         TextView category, badge;
         ImageView category_image;
    }
}

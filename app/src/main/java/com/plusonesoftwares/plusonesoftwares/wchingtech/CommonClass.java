package com.plusonesoftwares.plusonesoftwares.wchingtech;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.plusonesoftwares.plusonesoftwares.wchingtech.FontManager.FontManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ashoksharma on 02/03/17.
 */

public class CommonClass {
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String SubMenuPageUrl = "SubMenuPageUrl";
    public static final String SelectedLanguage = "SelectedLanguage";
    public static final String SelectedItem = "SelectedItem";


    SharedPreferences sharedpreferences;
    
    public void setUserPrefs(String key, String value, Context context) {
        if (sharedpreferences == null) {
            sharedpreferences = context.getSharedPreferences(MyPREFERENCES, context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getUserPrefs(String key, Context context) {
        if (sharedpreferences == null) {
            sharedpreferences = context.getSharedPreferences(MyPREFERENCES, context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String value = sharedpreferences.getString(key, null);
        editor.commit();
        return value;
    }
    /*public boolean keyExist(Context context) {
        if (sharedpreferences == null) {
            sharedpreferences = context.getSharedPreferences(MyPREFERENCES, context.MODE_PRIVATE);
        }
        return sharedpreferences.contains(NewsCategories);
    }*/

    public void clearUserPrefs(Context context) {
        if (sharedpreferences == null) {
            sharedpreferences = context.getSharedPreferences(MyPREFERENCES, context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
    }

    public void setFontForContainer(Context context, ViewGroup contentLayout) {
        for (int i=0; i < contentLayout.getChildCount(); i++) {
            View view = contentLayout.getChildAt(i);
            if (view instanceof TextView)
                ((TextView)view).setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
            else if (view instanceof ViewGroup)
               setFontForContainer(context, (ViewGroup) view);
        }
    }

 }


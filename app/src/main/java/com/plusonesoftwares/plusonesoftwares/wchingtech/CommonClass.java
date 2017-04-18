package com.hkgws.gladmore;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hkgws.gladmore.FontManager.FontManager;
import java.io.UnsupportedEncodingException;

/**
 * Created by ashoksharma on 02/03/17.
 */

public class CommonClass {
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String SubMenuPageUrl = "SubMenuPageUrl";
    public static final String SelectedLanguage = "SelectedLanguage";
    public static final String SelectedItem = "SelectedItem";

    public static final String response = "response";
    public static final String ComapnyName = "ComapnyName";
    public static final String UserName = "UserName";
    public static final String Passowrd = "Passowrd";
    public static final String userdesc = "userdesc";
    public static final String companydesc = "companydesc";
    public static final String menushow = "menushow";


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

    public String getEncodedString(String param){
        try {
            return new String(param.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return param;
    }

 }


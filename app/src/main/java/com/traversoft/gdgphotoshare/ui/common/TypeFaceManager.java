package com.traversoft.gdgphotoshare.ui.common;


import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;


public class TypeFaceManager {

    private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

    public static Typeface get(Context context, String name) {
        synchronized(cache) {
            if (!cache.containsKey(name)) {
                Typeface t = Typeface.createFromAsset(context.getAssets(), String.format("fonts/%s.ttf", name));
                cache.put(name, t);
            }
            return cache.get(name);
        }
    }

    public static Typeface get(Context context, int fontNameResource) {

        synchronized(cache) {
            if (fontNameResource == 0) {
                return Typeface.DEFAULT;
            }

            String name = context.getString(fontNameResource);
            if (name.equals("")) {
                return Typeface.DEFAULT;
            }

            if (!cache.containsKey(name)) {
                Typeface t = Typeface.createFromAsset(context.getAssets(), String.format("fonts/%s.ttf", name));
                cache.put(name, t);
            }
            return cache.get(name);
        }
    }
}

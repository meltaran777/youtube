package com.bohdan.youtube.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by Bodia on 05.01.2017.
 */

public class MySingleton {

    private static MySingleton instance;
    private RequestQueue requestQueue;
    private final ImageLoader imageLoader;
    private static Context context;

    private MySingleton(Context context){
        this.context = context;
        requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {

         private final LruCache<String, Bitmap> cache = new LruCache<>(20);

         @Override
         public Bitmap getBitmap(String url) {
             return null;
         }

         @Override
         public void putBitmap(String url, Bitmap bitmap) {

         }
     });
    }

    public static synchronized MySingleton getInstance(Context context){
        if (instance == null){
            instance = new MySingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue(){
        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req){
       getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

}

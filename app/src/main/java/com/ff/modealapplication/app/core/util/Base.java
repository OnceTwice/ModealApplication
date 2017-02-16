package com.ff.modealapplication.app.core.util;

import com.ff.modealapplication.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class Base {
    public static final String url = "http://192.168.1.26:8088/";       // 자신의 ip주소

    public static DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
            // .showImageOnLoading( R.drawable.ic_default_profile ) // resource or drawable
            .showImageForEmptyUri( R.drawable.apple )  // resource or drawable
            .showImageOnFail( R.drawable.apple )       // resource or drawable
            //.resetViewBeforeLoading( false )  // default
            .delayBeforeLoading( 0 )
            //.cacheInMemory( false )   // default
            .cacheOnDisc( true )        // false is default
            //.preProcessor(...)
            //.postProcessor(...)
            //.extraForDownloader(...)
            //.considerExifParams( false )  // default
            //.imageScaleType( ImageScaleType.IN_SAMPLE_POWER_OF_2 )// default
            //.bitmapConfig( Bitmap.Config.ARGB_8888 )  // default
            //.decodingOptions(...)
            //.displayer( new SimpleBitmapDisplayer() ) // default
            //.handler( new Handler() )     // default
            .build();
}

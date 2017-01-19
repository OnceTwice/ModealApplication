package com.ff.modealapplication.app.ui.Main;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ff.modealapplication.R;
import com.ff.modealapplication.app.core.vo.ItemVo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by BIT on 2017-01-19.
 */

public class MainListArrayAdapter extends ArrayAdapter<ItemVo> {

    private LayoutInflater layoutInflater;

    //통신 중 오류시 error 이미지 보여주기
    DisplayImageOptions displayImageOptions =  new DisplayImageOptions.Builder()
            // .showImageOnLoading( R.drawable.ic_default_profile )// resource or drawable
            .showImageForEmptyUri( R.drawable.ic_image_error )// resource or drawable
            .showImageOnFail( R.drawable.ic_image_error )// resource or drawable
            //.resetViewBeforeLoading( false )// default
            .delayBeforeLoading( 0 )
            //.cacheInMemory( false )// default
            .cacheOnDisc( true )// false is default
            //.preProcessor(...)
            //.postProcessor(...)
            //.extraForDownloader(...)
            //.considerExifParams( false )// default
            //.imageScaleType( ImageScaleType.IN_SAMPLE_POWER_OF_2 )// default
            //.bitmapConfig( Bitmap.Config.ARGB_8888 )// default
            //.decodingOptions(...)
            //.displayer( new SimpleBitmapDisplayer() )// default
            //.handler( new Handler() )// default
            .build();

    public MainListArrayAdapter(Context context) {
        super(context, R.layout.activity_main);
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = layoutInflater.inflate(R.layout.row_main_list,parent, false);
        }

        ItemVo itemVo = getItem(position);
        //이미지 저장
        ImageLoader.getInstance().displayImage(itemVo.getPicture(), (ImageView)view.findViewById(R.id.mian_image_item), displayImageOptions);

        //내용저장
        TextView textTimeView=(TextView)view.findViewById(R.id.main_time_textView);
        TextView textItemView=(TextView)view.findViewById(R.id.main_item_name);
        TextView textPriceView=(TextView)view.findViewById(R.id.main_item_price);
        TextView textOriPriceView=(TextView)view.findViewById(R.id.main_item_ori_price);
        TextView textdiscountView=(TextView)view.findViewById(R.id.main_item_discount);
        TextView textShopNameView=(TextView)view.findViewById(R.id.main_shop_shopName);
        TextView textShopSpaceView=(TextView)view.findViewById(R.id.main_shop_space);

        textTimeView.setText(itemVo.getExpDate());
        textdiscountView.setText(String.valueOf((int) itemVo.getDiscount()));
        textItemView.setText(itemVo.getName());
        textOriPriceView.setPaintFlags(textOriPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        textOriPriceView.setText(String.valueOf((int)itemVo.getOriPrice()));
        textShopNameView.setText(String.valueOf((int)itemVo.getShopNo()));
        textPriceView.setText(String.valueOf((int)itemVo.getPrice()));
        textShopSpaceView.setText(itemVo.getName());

        return view;
//        return super.getView(position, convertView, parent);
    }
    public void add(List<ItemVo> itemVos){
        if(itemVos ==null || itemVos.size()==0){
          return;
        }
        for(ItemVo itemVo : itemVos){
            add(itemVo);
        }
    }

}

package com.ff.modealapplication.app.ui.main;

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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by BIT on 2017-01-19.
 */

public class MainListArrayAdapter extends ArrayAdapter<Map<String, Object>> {

    private LayoutInflater layoutInflater;

    //통신 중 오류시 error 이미지 보여주기
    DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
            // .showImageOnLoading( R.drawable.ic_default_profile )// resource or drawable
            .showImageForEmptyUri(R.drawable.ic_image_error)// resource or drawable
            .showImageOnFail(R.drawable.ic_image_error)// resource or drawable
            //.resetViewBeforeLoading( false )// default
            .delayBeforeLoading(0)
            //.cacheInMemory( false )// default
            .cacheOnDisc(true)// false is default
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.row_main_list, parent, false);
        }

        final Map<String, Object> itemVo = (Map<String, Object>) getItem(position);

        //이미지 저장 [ 연결된 ip로 upload ] 위치에 내용이 있어야 한다.
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getContext()));
//        ImageLoader.getInstance().displayImage("http://192.168.1.90:8088/modeal/shop/images/" + itemVo.get("picture"), (ImageView) view.findViewById(R.id.main_image_item), displayImageOptions);
        ImageLoader.getInstance().displayImage("http://192.168.1.93:8088/modeal/shop/images/" + itemVo.get("picture"), (ImageView) view.findViewById(R.id.main_image_item), displayImageOptions);

        //내용저장
        TextView textTimeView = (TextView) view.findViewById(R.id.main_time_textView);
        TextView textItemView = (TextView) view.findViewById(R.id.main_item_name);
        TextView textPriceView = (TextView) view.findViewById(R.id.main_item_price);
        TextView textOriPriceView = (TextView) view.findViewById(R.id.main_item_ori_price);
        TextView textdiscountView = (TextView) view.findViewById(R.id.main_item_discount);
        TextView textShopNameView = (TextView) view.findViewById(R.id.main_shop_shopName);
        TextView textShopSpaceView = (TextView) view.findViewById(R.id.main_shop_space);

        // 액티비티로 데이터 보내기 위해서...
        ((TextView) view.findViewById(R.id.send_no)).setText(String.valueOf(itemVo.get("no"))); // 상품 no
        ((TextView) view.findViewById(R.id.send_shopNo)).setText(String.valueOf(itemVo.get("shopNo"))); // 매장 no

        StringTokenizer tokens = new StringTokenizer(itemVo.get("expDate").toString(), "/:- ");
        String year = tokens.nextToken();
        String month = tokens.nextToken();
        String day = tokens.nextToken();
        String hour = tokens.nextToken();
        String minute = tokens.nextToken();
//        String second = tokens.nextToken();

//        String expDate = year + "년 " + month + "월 " +

        textTimeView.setText(itemVo.get("expDate").toString());
        // ((Double)itemVo.get("discount")).longValue() [Double형 → Long형]
        textdiscountView.setText(String.valueOf(((Double) itemVo.get("discount")).longValue()));
        textItemView.setText(itemVo.get("name").toString());
        textOriPriceView.setPaintFlags(textOriPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); //가운데 줄긋기
        textOriPriceView.setText(String.valueOf(((Double) itemVo.get("oriPrice")).longValue()));
        textShopNameView.setText(String.valueOf(itemVo.get("shopName")));
        textPriceView.setText(String.valueOf(((Double) itemVo.get("price")).longValue()));
        if (itemVo.get("distance") != null) {
            textShopSpaceView.setText(String.valueOf(((Double) itemVo.get("distance")).longValue()) + "m");
        }

        return view;
//        return super.getView(position, convertView, parent);
    }

    public void add(List<Map<String, Object>> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        for (Map<String, Object> map : list) {
            add(map);
        }
    }
}

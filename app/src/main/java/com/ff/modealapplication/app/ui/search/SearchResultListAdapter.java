package com.ff.modealapplication.app.ui.search;

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
 * Created by BIT on 2017-01-31.
 */

public class SearchResultListAdapter extends ArrayAdapter<Map<String, Object>> {

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

    public SearchResultListAdapter(Context context) {
        super(context, R.layout.activity_search_result);
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.row_main_list, parent, false);
        }

        Map<String, Object> itemVo = (Map<String, Object>) getItem(position);

        //이미지 저장 [ 연결된 ip로 upload ] 위치에 내용이 있어야 한다.
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getContext()));
        ImageLoader.getInstance().displayImage(itemVo.get("picture").toString(), (ImageView) view.findViewById(R.id.main_image_item), displayImageOptions);

        //내용저장
        TextView textTimeView = (TextView) view.findViewById(R.id.main_time_textView);
        TextView textItemView = (TextView) view.findViewById(R.id.main_item_name);
        TextView textPriceView = (TextView) view.findViewById(R.id.main_item_price);
        TextView textOriPriceView = (TextView) view.findViewById(R.id.main_item_ori_price);
        TextView textdiscountView = (TextView) view.findViewById(R.id.main_item_discount);
        TextView textShopNameView = (TextView) view.findViewById(R.id.main_shop_shopName);
        TextView textShopSpaceView = (TextView) view.findViewById(R.id.main_shop_space);
        TextView textShopNo = (TextView) view.findViewById(R.id.send_shopNo);
        TextView textNo = (TextView) view.findViewById(R.id.send_no);

        StringTokenizer tokens = new StringTokenizer(itemVo.get("expDate").toString(), "-/: ");
        String year = tokens.nextToken();
        String month = tokens.nextToken();
        String day = tokens.nextToken();
        String hour = tokens.nextToken();
        String minute = tokens.nextToken();

        textTimeView.setText(year + "년 " + month + "월 " + day + "일 " + hour + "시 " + minute + "분 ");
        textdiscountView.setText(((Double) itemVo.get("discount")).longValue() + "%");
        textItemView.setText(itemVo.get("name").toString());
        textOriPriceView.setPaintFlags(textOriPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); //가운데 줄긋기
        textOriPriceView.setText(((Double) itemVo.get("oriPrice")).longValue() + "원");
        textShopNameView.setText(String.valueOf(itemVo.get("shopName")));
        textPriceView.setText(((Double) itemVo.get("price")).longValue() + "원");
        textShopSpaceView.setText(((Double)itemVo.get("distance")).longValue() + "m");
        textShopNo.setText(itemVo.get("shopNo").toString());
        textNo.setText(itemVo.get("no").toString());

        return view;
//        return super.getView(position, convertView, parent);
    }

    public void add(List<Map<String, Object>> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        for (Map<String, Object> maps : list) {
            add(maps);
        }
    }


}

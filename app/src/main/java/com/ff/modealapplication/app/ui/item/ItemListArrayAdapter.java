package com.ff.modealapplication.app.ui.item;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
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

/**
 * Created by bit-desktop on 2017-02-01.
 */

public class ItemListArrayAdapter extends ArrayAdapter<Map<String, Object>> {

    private LayoutInflater layoutInflater;
    DisplayImageOptions displayImageOption = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.cake)
            .showImageOnFail(R.drawable.cake)
            .delayBeforeLoading(0)
            .cacheOnDisc(true)
            .build();

    public ItemListArrayAdapter(Context context) {
        super(context, R.layout.item_list);
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_list_row, parent, false);
        }

        Map<String, Object> map = getItem(position);
//        ((TextView) view.findViewById(R.id.shop_name)).setText(map.get("shopName").toString());             // 해당 매장명
        ((TextView) view.findViewById(R.id.item_list_clock)).setText(map.get("expDate").toString());        // 유통기한
        ((TextView) view.findViewById(R.id.item_list_name)).setText(map.get("name").toString());            // 상품명
        ((TextView) view.findViewById(R.id.item_list_ori_price)).setText(map.get("oriPrice").toString());   // 원가
        ((TextView) view.findViewById(R.id.item_list_price)).setText(map.get("price").toString());          // 판매가
        ((TextView) view.findViewById(R.id.item_list_shop_name)).setText(map.get("shopName").toString());   // 매장명
//        ((TextView) view.findViewById(R.id.item_list_distance)).setText(map.get("distance").toString());    // 거리(반경)

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getContext()));
        ImageLoader.getInstance().displayImage("http://192.168.1.90:8088/modeal/shop/images/" + map.get("picture"),
                (ImageView) view.findViewById(R.id.item_list_image), displayImageOption);                // 상품이미지

        return view;
    }

    // 목록에 상품이 추가됨
    public void add(List<Map<String, Object>> list) {
        if (list == null) {
            return;
        }
        for (Map<String, Object> map : list) {
            Log.d("test", "" + map);
            add(map);
        }
    }
}

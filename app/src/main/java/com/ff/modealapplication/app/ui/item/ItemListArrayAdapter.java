package com.ff.modealapplication.app.ui.item;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class ItemListArrayAdapter extends ArrayAdapter<Map<String, Object>> implements View.OnClickListener {

    private LayoutInflater layoutInflater;
    DisplayImageOptions displayImageOption = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.apple)
            .showImageOnFail(R.drawable.apple)
            .delayBeforeLoading(0)
            .cacheOnDisc(true)
            .build();

    public ItemListArrayAdapter(Context context) {
        super(context, R.layout.item_list);
        // layoutInflater = LayoutInflater.from(context);
        // 위 방법으로 했더니 에러가 뜨길래 아래 방법으로 교체하니 에러 안뜸... (170207/상욱변경)
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) { // 내부 많이 변경 (170207/상욱변경)
        ItemViewHolder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_list_row, parent, false);
            holder = new ItemViewHolder();
            holder.hide = (Button) convertView.findViewById(R.id.button_hiding_item);
            holder.modify = (Button) convertView.findViewById(R.id.button_modify_item);
            holder.delete = (Button) convertView.findViewById(R.id.button_delete_item);
            convertView.setTag(holder);

        } else {
            holder = (ItemViewHolder) convertView.getTag();
        }

        holder.hide.setOnClickListener(this);
        holder.modify.setOnClickListener(this);
        holder.delete.setOnClickListener(this);


        Map<String, Object> map = getItem(position);
//        ((TextView) convertView.findViewById(R.id.shop_name)).setText(map.get("shopName").toString());             // 해당 매장명
        ((TextView) convertView.findViewById(R.id.item_list_clock)).setText(map.get("expDate").toString());        // 유통기한
        ((TextView) convertView.findViewById(R.id.item_list_name)).setText(map.get("name").toString());            // 상품명
        ((TextView) convertView.findViewById(R.id.item_list_ori_price)).setText(map.get("oriPrice").toString());   // 원가
        ((TextView) convertView.findViewById(R.id.item_list_price)).setText(map.get("price").toString());          // 판매가
        ((TextView) convertView.findViewById(R.id.item_list_shop_name)).setText(map.get("shopName").toString());   // 매장명
//        ((TextView) convertView.findViewById(R.id.item_list_distance)).setText(map.get("distance").toString());    // 거리(반경)

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getContext()));
        ImageLoader.getInstance().displayImage("http://192.168.1.90:8088/modeal/shop/images/" + map.get("picture"),
                (ImageView) convertView.findViewById(R.id.item_list_image), displayImageOption);                // 상품이미지

        return convertView;
    }

    // row item의 버튼들 클릭시 (170207/상욱추가)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.button_hiding_item: {
                LinearLayout show = (LinearLayout) view.findViewById(R.id.button_hiding_item);
                if ( // 상품출력유무
                    ((Button) view.findViewById(R.id.button_hiding_item)).getText() == "보이기") {
                    ((Button) view.findViewById(R.id.button_hiding_item)).setText("숨기기");
                    show.setVisibility(view.INVISIBLE);

                } else {
                    ((Button) view.findViewById(R.id.button_hiding_item)).setText("보이기");
                    show.setVisibility(view.VISIBLE);
                }
                break;
            }
            case R.id.button_modify_item: {
                Intent intent = new Intent(getContext(), ItemModifyActivity.class);
                getContext().startActivity(intent);
                break;
            }
            case R.id.button_delete_item: {
                Intent intent = new Intent(getContext(), ItemActivity.class);
                getContext().startActivity(intent);
                break;
            }
        }
    }

    // listview 내부의 row item 버튼 클릭하기 위해서... (170207/상욱추가)
    // http://bellgori.tistory.com/entry/Android-pattern-01-ViewHolder-pattern ← ViewHolder 쓰는 이유
    public static class ItemViewHolder {
        public Button hide;
        public Button modify;
        public Button delete;
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

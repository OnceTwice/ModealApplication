package com.ff.modealapplication.app.ui.main;

import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ff.modealapplication.R;
import com.ff.modealapplication.app.core.util.Base;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by BIT on 2017-01-19.
 */

public class MainListArrayAdapter extends ArrayAdapter<Map<String, Object>> {

    private LayoutInflater layoutInflater;
    private long diff;
    private TextView textTimeView;
    private TextView textCountDownView;

    //통신 중 오류시 error 이미지 보여주기
    DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.ic_image_error)// resource or drawable
            .showImageOnFail(R.drawable.ic_image_error)// resource or drawable
            .delayBeforeLoading(0)
            .cacheOnDisc(true)// false is default
            .build();

    public MainListArrayAdapter(Context context) {
        super(context, R.layout.activity_main);
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final Map<String, Object> itemVo = getItem(position);
        if (view == null) {
            view = layoutInflater.inflate(R.layout.row_main_list, parent, false);
        }
        textTimeView = (TextView) view.findViewById(R.id.main_time_textView);
        TextView textItemView = (TextView) view.findViewById(R.id.main_item_name);
        TextView textPriceView = (TextView) view.findViewById(R.id.main_item_price);
        TextView textOriPriceView = (TextView) view.findViewById(R.id.main_item_ori_price);
        TextView textDiscountView = (TextView) view.findViewById(R.id.main_item_discount);
        TextView textShopNameView = (TextView) view.findViewById(R.id.main_shop_shopName);
        TextView textShopSpaceView = (TextView) view.findViewById(R.id.main_shop_space);
        textCountDownView = (TextView) view.findViewById(R.id.count_down);
        ImageView imageView = (ImageView) view.findViewById(R.id.main_image_item);
        TextView sendNo = (TextView) view.findViewById(R.id.send_no);
        TextView sendShopNo = (TextView) view.findViewById(R.id.send_shopNo);

        try {
            if (new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(itemVo.get("expDate").toString()).getTime() - new Date().getTime() < (2 * 24 * 60 * 60 * 1000)) {
                count(getItem(position));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //이미지 저장 [ 연결된 ip로 upload ] 위치에 내용이 있어야 한다.
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getContext()));
        ImageLoader.getInstance().displayImage(Base.url + "modeal/shop/images/" + itemVo.get("picture"), imageView, displayImageOptions)
        ;

        textTimeView.setText(itemVo.get("expDate").toString());
        // ((Double)itemVo.get("discount")).longValue() [Double형 → Long형]
        textDiscountView.setText(String.valueOf(((Double) itemVo.get("discount")).longValue()));
        textItemView.setText(itemVo.get("name").toString());
        textOriPriceView.setPaintFlags(textOriPriceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); //가운데 줄긋기
        textOriPriceView.setText(String.valueOf(((Double) itemVo.get("oriPrice")).longValue()));
        textShopNameView.setText(String.valueOf(itemVo.get("shopName")));
        textPriceView.setText(String.valueOf(((Double) itemVo.get("price")).longValue()));
        sendNo.setText(String.valueOf(((Double) itemVo.get("no")).longValue()));
        sendShopNo.setText(String.valueOf(((Double) itemVo.get("shopNo")).longValue()));
        if (itemVo.get("distance") != null) {
            textShopSpaceView.setText(String.valueOf(((Double) itemVo.get("distance")).longValue()) + "m");
        }
        return view;
    }

    public void count(final Map<String, Object> map) {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    handler.postDelayed(this, 1000);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                    Date date = format.parse(map.get("expDate").toString());
                    Date currentDate = new Date();
                    if (!currentDate.after(date)) {
                        diff = date.getTime() - currentDate.getTime();
                        if (diff < (2 * 24 * 60 * 60 * 1000)) {
                            long days = diff / (24 * 60 * 60 * 1000);
                            diff -= days * (24 * 60 * 60 * 1000);
                            long hours = diff / (60 * 60 * 1000);
                            diff -= hours * (60 * 60 * 1000);
                            long minutes = diff / (60 * 1000);
                            diff -= minutes * (60 * 1000);
                            long seconds = diff / 1000;
                            textCountDownView.setText(String.format("%d", days) + "일 "
                                    + String.format("%d", hours) + "시 "
                                    + String.format("%d", minutes) + "분 "
                                    + String.format("%d", seconds) + "초");
                        }
                    } else {
                        textTimeView.setText("판매시간 종료!");
                        textCountDownView.setVisibility(View.INVISIBLE);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void add(List<Map<String, Object>> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        for (final Map<String, Object> map : list) {
            add(map);
        }
    }
}

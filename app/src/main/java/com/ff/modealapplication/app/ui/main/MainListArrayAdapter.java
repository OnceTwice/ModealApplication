package com.ff.modealapplication.app.ui.main;

import android.graphics.Paint;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ff.modealapplication.R;
import com.ff.modealapplication.app.core.util.Base;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.ff.modealapplication.app.core.util.Base.displayImageOptions;

/**
 * Created by BIT on 2017-01-19.
 */

public class MainListArrayAdapter extends RecyclerView.Adapter<MainListArrayAdapter.ViewHolder> {

    private long diff;
    private List<Map<String, Object>> list;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_main_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Map<String, Object> item = list.get(position);

        //이미지 저장 [ 연결된 ip로 upload ] 위치에 내용이 있어야 한다.
//        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(new MainActivity()));
        ImageLoader.getInstance().displayImage(Base.url + "modeal/shop/images/" + item.get("picture"), holder.itemImage, displayImageOptions);

        holder.mainTime.setText(item.get("expDate").toString());
        holder.itemName.setText(item.get("name").toString());
        // ((Double)itemVo.get("discount")).longValue() [Double형 → Long형]
        holder.itemPrice.setText(String.valueOf(((Double) item.get("price")).longValue()));
        holder.itemOriPrice.setPaintFlags(holder.itemOriPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); //가운데 줄긋기
        holder.itemOriPrice.setText(String.valueOf(((Double) item.get("oriPrice")).longValue()));
        holder.discount.setText(((Double) item.get("discount")).longValue() + "%");
        holder.shopName.setText(String.valueOf(item.get("shopName")));
        if (item.get("distance") != null) {
            holder.shopSpace.setText(String.valueOf(((Double) item.get("distance")).longValue()) + "m");
        }
        holder.sendNo.setText(String.valueOf(((Double) item.get("no")).longValue()));
        holder.sendShopNo.setText(String.valueOf(((Double) item.get("shopNo")).longValue()));

        if (holder.timer != null) {
            holder.timer.cancel();
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            Date date = format.parse(item.get("expDate").toString());
            Date currentDate = new Date();
            diff = date.getTime() - currentDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.timer = new CountDownTimer(diff, 1000) {

            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                int hours = (int) (millisUntilFinished / (1000 * 60 * 60));
                String newtime = hours + ":" + minutes + ":" + seconds;

                if (newtime.equals("0:0:0")) {
                    holder.countdown.setText("판매종료");
                    holder.mainTime.setText("판매종료");
                } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(minutes).length() == 1) && (String.valueOf(seconds).length() == 1)) {
                    holder.countdown.setText("0" + hours + ":0" + minutes + ":0" + seconds);
                } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(minutes).length() == 1)) {
                    holder.countdown.setText("0" + hours + ":0" + minutes + ":" + seconds);
                } else if ((String.valueOf(hours).length() == 1) && (String.valueOf(seconds).length() == 1)) {
                    holder.countdown.setText("0" + hours + ":" + minutes + ":0" + seconds);
                } else if ((String.valueOf(minutes).length() == 1) && (String.valueOf(seconds).length() == 1)) {
                    holder.countdown.setText(hours + ":0" + minutes + ":0" + seconds);
                } else if (String.valueOf(hours).length() == 1) {
                    holder.countdown.setText("0" + hours + ":" + minutes + ":" + seconds);
                } else if (String.valueOf(minutes).length() == 1) {
                    holder.countdown.setText(hours + ":0" + minutes + ":" + seconds);
                } else if (String.valueOf(seconds).length() == 1) {
                    holder.countdown.setText(hours + ":" + minutes + ":0" + seconds);
                } else {
                    holder.countdown.setText(hours + ":" + minutes + ":" + seconds);
                }
            }

            public void onFinish() {
                holder.countdown.setText("판매종료");
                holder.mainTime.setText("판매종료");
            }
        }.start();
    }

    @Override
    public int getItemCount() {
        if (list == null)
            return 0;
        else
            return list.size();
    }
    // 안되는데...
//    public void swap(List<Map<String, Object>> datas) {
//        if (!list.isEmpty()) {
//            list.clear();
//        }
//        list.addAll(datas);
//        notifyDataSetChanged();
//    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void add(List<Map<String, Object>> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mainTime;
        public TextView itemName;
        public TextView itemPrice;
        public TextView itemOriPrice;
        public TextView discount;
        public TextView shopName;
        public TextView shopSpace;
        public TextView sendNo;
        public TextView sendShopNo;
        public ImageView itemImage;
        public TextView countdown;
        CountDownTimer timer;


        public ViewHolder(View itemView) {
            super(itemView);

            if (!ImageLoader.getInstance().isInited()) {
                ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(itemView.getContext()));
            }

            mainTime = (TextView) itemView.findViewById(R.id.main_time_textView);
            itemName = (TextView) itemView.findViewById(R.id.main_item_name);
            itemPrice = (TextView) itemView.findViewById(R.id.main_item_price);
            itemOriPrice = (TextView) itemView.findViewById(R.id.main_item_ori_price);
            discount = (TextView) itemView.findViewById(R.id.main_item_discount);
            shopName = (TextView) itemView.findViewById(R.id.main_shop_shopName);
            shopSpace = (TextView) itemView.findViewById(R.id.main_shop_space);
            sendNo = (TextView) itemView.findViewById(R.id.send_no);
            sendShopNo = (TextView) itemView.findViewById(R.id.send_shopNo);
            itemImage = (ImageView) itemView.findViewById(R.id.main_image_item);
            countdown = (TextView) itemView.findViewById(R.id.count_down);
        }
    }
}
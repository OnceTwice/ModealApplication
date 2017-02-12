package com.ff.modealapplication.app.ui.main;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.MainService;
import com.ff.modealapplication.app.core.util.LoginPreference;
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
        view.setTag();
        if (view == null) {
            view = layoutInflater.inflate(R.layout.row_main_list, parent, false);
        }

        final Map<String, Object> itemVo = (Map<String, Object>) getItem(position);

        //이미지 저장 [ 연결된 ip로 upload ] 위치에 내용이 있어야 한다.
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getContext()));
//        ImageLoader.getInstance().displayImage("http://192.168.1.90:8088/modeal/shop/images/" + itemVo.get("picture"), (ImageView) view.findViewById(R.id.main_image_item), displayImageOptions);
        ImageLoader.getInstance().displayImage("http://192.168.0.17:8088/modeal/shop/images/" + itemVo.get("picture"), (ImageView) view.findViewById(R.id.main_image_item), displayImageOptions);

        //내용저장
        TextView textTimeView = (TextView) view.findViewById(R.id.main_time_textView);
        TextView textItemView = (TextView) view.findViewById(R.id.main_item_name);
        TextView textPriceView = (TextView) view.findViewById(R.id.main_item_price);
        TextView textOriPriceView = (TextView) view.findViewById(R.id.main_item_ori_price);
        TextView textdiscountView = (TextView) view.findViewById(R.id.main_item_discount);
        TextView textShopNameView = (TextView) view.findViewById(R.id.main_shop_shopName);
        TextView textShopSpaceView = (TextView) view.findViewById(R.id.main_shop_space);

        // 액티비티로 데이터 보내기 위해서...
        ((TextView) view.findViewById(R.id.send_no)).setText(String.valueOf(itemVo.get("no")));

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
        textShopNameView.setText(String.valueOf(itemVo.get("shopNo")));
        textPriceView.setText(String.valueOf(((Double) itemVo.get("price")).longValue()));
        textShopSpaceView.setText(itemVo.get("shopName").toString());

        final ToggleButton toggleButton; // 즐겨찾기 버튼

        // 로그인 정보 없을때 즐겨찾기 빈 하트로 하기
        if (LoginPreference.getValue(getContext(), "id") != null) {
            new BookmarkSelect(position).execute();
        } else {
            ((ToggleButton) view.findViewById(R.id.bookmark_button)).setChecked(false);
        }

        // 즐겨찾기 (로그인했을때만 가능)
        toggleButton = ((ToggleButton) view.findViewById(R.id.bookmark_button));
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                if (LoginPreference.getValue(getContext(), "id") != null) { // 로그인시 즐겨찾기 가능
                    if (isCheck) {
                        new BookmarkAdd(position).execute();
                        toggleButton.setChecked(true);
                    } else {
                        new BookmarkDelete(position).execute();
                        toggleButton.setChecked(false);
                    }
                } else { // 비회원시 즐겨찾기 불가능
                    toggleButton.setChecked(false);
                    Toast.makeText(getContext(), "로그인을 하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

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

    // 즐겨찾기 추가
    private class BookmarkAdd extends SafeAsyncTask<Void> {
        private int position;

        public BookmarkAdd(int position) {
            this.position = position;
        }

        @Override
        public Void call() throws Exception {
            MainService mainService = new MainService();
            mainService.bookmarkAdd(((Double) getItem(position).get("no")).longValue(), (Long) LoginPreference.getValue(getContext(), "no"));
            return null;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.d("*Main Exception error :", "" + e);
            throw new RuntimeException(e);
        }
    }

    // 즐겨찾기 삭제
    private class BookmarkDelete extends SafeAsyncTask<Void> {
        private int position;

        public BookmarkDelete(int position) {
            this.position = position;
        }

        @Override
        public Void call() throws Exception {
            MainService mainService = new MainService();
            mainService.bookmarkDelete(((Double) getItem(position).get("no")).longValue(), (Long) LoginPreference.getValue(getContext(), "no"));
            return null;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.d("*Main Exception error :", "" + e);
            throw new RuntimeException(e);
        }
    }

    // 즐겨찾기 검색
    private class BookmarkSelect extends SafeAsyncTask<Long> {
        private int position;

        public BookmarkSelect(int position) {
            this.position = position;
        }

        @Override
        public Long call() throws Exception {
            MainService mainService = new MainService();
            return mainService.bookmarkSelect(((Double) getItem(position).get("no")).longValue(), (Long) LoginPreference.getValue(getContext(), "no"));
        }

        @Override
        protected void onSuccess(Long no) throws Exception {
            getItem(position).
            super.onSuccess(no);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.d("*Main Exception error :", "" + e);
            throw new RuntimeException(e);
        }
    }
}

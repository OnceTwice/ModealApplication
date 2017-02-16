package com.ff.modealapplication.app.ui.notice;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ff.modealapplication.R;
import com.ff.modealapplication.app.core.vo.NoticeVo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

/**
 * Created by BIT on 2017-02-15.
 */

public class NoticeListArrayAdapter extends ArrayAdapter<NoticeVo> {
    private LayoutInflater layoutInflater;
        //통신 중 오류시 error 이미지 보여주기
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                // .showImageOnLoading( R.drawable.ic_default_profile )// resource or drawable
                .showImageForEmptyUri(R.drawable.background_tab)// resource or drawable
                .showImageOnFail(R.drawable.background_tab)// resource or drawable
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

    public NoticeListArrayAdapter(Context context) {
        super(context, R.layout.activity_notice);
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if(view == null){
            view = layoutInflater.inflate(R.layout.notice_list, parent, false);
        }
        final NoticeVo noticeVo = getItem(position);

            //이미지 저장 [ 연결된 ip로 upload ] 위치에 내용이 있어야 한다.
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getContext()));
//        ImageLoader.getInstance().displayImage("http://192.168.1.90:8088/modeal/shop/images/" + itemVo.get("picture"), (ImageView) view.findViewById(R.id.main_image_item), displayImageOptions);
            ImageLoader.getInstance().displayImage("http://192.168.1.87:8088/modeal/shop/images/" + noticeVo.getSaveFileName(), (ImageView) view.findViewById(R.id.notice_image), displayImageOptions);

        ((TextView)view.findViewById(R.id.notice_title)).setText(noticeVo.getTitle());
        ((TextView)view.findViewById(R.id.notice_time)).setText(noticeVo.getRegDate());
        ((TextView)view.findViewById(R.id.notice_content)).setText(noticeVo.getContent());

        return view;
    }


    public void add(List<NoticeVo> noticeList){
        if(noticeList == null || noticeList.size()==0){
            return;
        }
        for(NoticeVo noticeVo : noticeList){
            add(noticeVo);
        }
    }
}

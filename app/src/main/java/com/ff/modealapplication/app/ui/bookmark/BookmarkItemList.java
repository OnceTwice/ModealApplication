package com.ff.modealapplication.app.ui.bookmark;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.BookmarkService;
import com.ff.modealapplication.app.core.util.LoginPreference;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by BIT on 2017-02-14.
 */

public class BookmarkItemList extends ArrayAdapter<Map<String, Object>> {

    private LayoutInflater layoutInflater;
    DisplayImageOptions displayImageOption = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.apple)
            .showImageOnFail(R.drawable.apple)
            .delayBeforeLoading(0)
            .cacheOnDisc(true)
            .build();

    // 체크된 아이템들을 저장할 List
    private ArrayList listItem; // 리스트뷰 중복 문제 해결을 위해서...
    boolean isChecked;

    public BookmarkItemList(Context context) {
        super(context, R.layout.activity_bookmark);
        layoutInflater = LayoutInflater.from(context);

        listItem = new ArrayList(); // 리스트뷰 중복 문제 해결을 위해서...
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (listItem != null && listItem.get(position) != null) {
            convertView = (View) listItem.get(position);
            if (convertView != null) {
                return convertView;
            }
        }
        convertView = layoutInflater.inflate(R.layout.bookmark_item_row, null);
        Button delete = (Button) convertView.findViewById(R.id.bookmark_item_delete);
        TextView text = (TextView) convertView.findViewById(R.id.bookmark_item_text);

        final Map<String, Object> bookmark = getItem(position);
        text.setText(bookmark.get("iname").toString());
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getContext()));
        ImageLoader.getInstance().displayImage("http://192.168.1.93:8088/modeal/shop/images/" + bookmark.get("picture"), (ImageView) convertView.findViewById(R.id.bookmark_image), displayImageOption);

        listItem.add(position);

        return convertView;
    }

    public static class BookmarkHolder {
        public Button delete;
        public TextView text;
    }

    public void add(List<Map<String, Object>> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        for (Map<String, Object> map : list) {
            Log.d("List items : ", "" + map);
            add(map);
        }
    }

    private class BookmarkDelete extends SafeAsyncTask<Void> {
        int buttonPosition;

        public BookmarkDelete(int buttonPosition) {
            this.buttonPosition = buttonPosition;
        }

        @Override
        public Void call() throws Exception {
            BookmarkService bookmarkService = new BookmarkService();
            bookmarkService.bookmarkDelete((long) buttonPosition, (Long) LoginPreference.getValue(getApplicationContext(), "no"));
            return null;
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.d("*Main Exception error :", "" + e);
            throw new RuntimeException(e);
        }
    }
}

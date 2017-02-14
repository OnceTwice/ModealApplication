package com.ff.modealapplication.app.ui.bookmark;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.ff.modealapplication.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;
import java.util.Map;

/**
 * Created by BIT on 2017-02-14.
 */

public class BookmarkShopList extends ArrayAdapter<Map<String, Object>> {

    private LayoutInflater layoutInflater;
    DisplayImageOptions displayImageOption = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.apple)
            .showImageOnFail(R.drawable.apple)
            .delayBeforeLoading(0)
            .cacheOnDisc(true)
            .build();

    public BookmarkShopList(Context context) {
        super(context, R.layout.activity_bookmark);
        layoutInflater = LayoutInflater.from(context);
//        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); // 위와 같음
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        BookmarkHolder holder = new BookmarkHolder();

        if (getItem(position).get("itemNo") == null) {
            if (view == null) {
                view = layoutInflater.inflate(R.layout.bookmark_shop_row, null);
                holder.delete = (Button) view.findViewById(R.id.bookmark_shop_delete);
                holder.text = (TextView) view.findViewById(R.id.bookmark_shop_text);
                view.setTag(holder);
            } else {
                holder = (BookmarkHolder) view.getTag();
            }
            holder.text.setText(getItem(position).get("sname").toString());
        }
        return view;
    }

    public static class BookmarkHolder {
        public Button delete;
        public TextView text;
    }

    public void add(List<Map<String, Object>> list) {
        if (list == null) {
            return;
        }
        for (Map<String, Object> map : list) {
            Log.d("List items : ", "" + map);
            add(map);
        }
    }
}

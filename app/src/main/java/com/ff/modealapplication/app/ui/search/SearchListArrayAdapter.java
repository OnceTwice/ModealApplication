package com.ff.modealapplication.app.ui.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ff.modealapplication.R;
import com.ff.modealapplication.app.core.vo.ItemVo;

import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by BIT on 2017-01-31.
 */

public class SearchListArrayAdapter extends ArrayAdapter<ItemVo> {

    private LayoutInflater layoutInflater;

    public SearchListArrayAdapter(Context context) {
        super(context, R.layout.activity_search);
        layoutInflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SearchDBManager searchDBManager = new SearchDBManager(getApplicationContext(), "SEARCH.db",null,1);

        View view = convertView;
        if(view == null){
            view=layoutInflater.inflate(R.layout.row_search_list,parent,false);
        }
        final ItemVo itemVo = getItem(position);

        TextView textView = (TextView)view.findViewById(R.id.text_search_list);

        textView.setText(itemVo.getName());

        view.findViewById(R.id.search_delete_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDBManager.delete("delete from SEARCH_LIST where name = '"+itemVo.getName()+"';");
            }
        });

        return  view;
//        return super.getView(position, convertView, parent);
    }

    public void add(List<ItemVo> searchList){
        if(searchList ==null || searchList.size()==0){
            return;
        }
        for (ItemVo itemVo : searchList){
            add(itemVo);
        }
    }
}

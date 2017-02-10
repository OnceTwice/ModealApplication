package com.ff.modealapplication.app.ui.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ff.modealapplication.R;

import java.util.List;

/**
 * Created by BIT on 2017-02-09.
 */

public class SearchDBArrayAdapter extends ArrayAdapter<String> {

    private LayoutInflater layoutInflater;

    public SearchDBArrayAdapter(Context context) {
        super(context, R.layout.activity_search);
        layoutInflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
//        return super.getView(position, convertView, parent);

        View view = convertView;
        if(view == null){
            view=layoutInflater.inflate(R.layout.row_search_list_image,parent,false);
        }


        ((TextView)view.findViewById(R.id.text_search_list)).setText(getItem(position));

        return view;
    }




    public void add(List<String> searchLeast){
        if(searchLeast ==null || searchLeast.size() == 0){
            return;
        }
        for(String Least : searchLeast){
            add(Least);
        }
    }
}

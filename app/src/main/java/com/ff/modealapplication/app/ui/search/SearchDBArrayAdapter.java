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

import static com.facebook.FacebookSdk.getApplicationContext;

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
    public View getView(final int position, final View convertView, final ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        final SearchDBManager searchDBManager = new SearchDBManager(getApplicationContext(), "SEARCH.db",null,1);

        View view = convertView;
        if(view == null){
            view=layoutInflater.inflate(R.layout.row_search_list_image,parent,false);
        }


        ((TextView)view.findViewById(R.id.text_search_list)).setText(getItem(position));
        view.findViewById(R.id.search_delete_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDBManager.delete("delete from SEARCH_LIST where name = '"+getItem(position)+"';");
            }
        });
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

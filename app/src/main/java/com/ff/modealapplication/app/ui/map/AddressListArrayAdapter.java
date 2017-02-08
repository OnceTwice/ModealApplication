package com.ff.modealapplication.app.ui.map;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.ff.modealapplication.R;
import com.ff.modealapplication.app.core.vo.daumvo.DaumItemVo;

import java.util.List;


public class AddressListArrayAdapter extends ArrayAdapter<DaumItemVo> {

    private LayoutInflater layoutInflater;
    private Context context;

    public AddressListArrayAdapter(Context context) {

        super(context, R.layout.activity_address_list);
        layoutInflater = layoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.row_address_list, parent, false);
        }
        //내부 ArrayList에서 해당 포지션의 User 객체를 받아옴

        final DaumItemVo daumItemVo = getItem(position);


        //이름 세팅
        TextView textViewId = (TextView) view.findViewById(R.id.textView_address);
        textViewId.setText(daumItemVo.getTitle());
        //버튼 세팅이다


        view.findViewById(R.id.button_select).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("=DaumItemVo.getLng()=", "" + daumItemVo.getLng());
                Log.d("=DaumItemVo.getLat()=", "" + daumItemVo.getLat());

                Intent intent = ((AddressListActivity) context).getIntent();
                intent.putExtra("title", daumItemVo.getTitle());
                intent.putExtra("lng", "" + daumItemVo.getLng());
                intent.putExtra("lat", "" + daumItemVo.getLat());

                ((AddressListActivity) context).setResult(((AddressListActivity) context).RESULT_OK, intent);
                ((AddressListActivity) context).finish();
            }
        });

        view.findViewById(R.id.button_map).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(context, SearchToMapActivity.class);
//                intent.putExtra("lng", "" + daumItemVo.getLng());
//                intent.putExtra("lat", "" + daumItemVo.getLat());
//                context.startActivity(intent);
            }
        });

        return view;
    }

    public void add(List<DaumItemVo> daumItemVos) {
        if (daumItemVos == null || daumItemVos.size() == 0) {
            return;
        }
        for (DaumItemVo daumItemVo : daumItemVos) {
            add(daumItemVo);
        }
        //add에 이미 있음
        //notifyDataSetChanged();
    }

}

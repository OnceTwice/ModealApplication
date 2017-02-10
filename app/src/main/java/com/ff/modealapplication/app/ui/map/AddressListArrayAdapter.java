package com.ff.modealapplication.app.ui.map;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ff.modealapplication.R;
import com.ff.modealapplication.app.core.vo.daumvo.DaumItemVo;

import java.util.List;

import static com.ff.modealapplication.R.id.editText_address;
import static com.ff.modealapplication.app.ui.map.SearchMapRangeActivity.FinishSearchMapRangeActivity;


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


        //주소 세팅
        TextView textViewId = (TextView) view.findViewById(R.id.textView_address);
        textViewId.setText(daumItemVo.getTitle());

        // 버튼 세팅
        // 바로 선택
        view.findViewById(R.id.button_select).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = ((AddressListActivity) context).getIntent();
                intent.putExtra("title", daumItemVo.getTitle()); // 주소
                intent.putExtra("longitude", "" + daumItemVo.getLng()); // 경도
                intent.putExtra("latitude", "" + daumItemVo.getLat()); // 위도

                ((AddressListActivity) context).setResult(((AddressListActivity) context).RESULT_OK, intent); // 이전 액티비티로 값 보내기
                ((AddressListActivity) context).finish();

                ((EditText)FinishSearchMapRangeActivity.findViewById(editText_address)).setText(""); // 검색어 지우기 위해서...
            }
        });

        // 지도에서 선택 (주소이름을 못보내고 경도, 위도만 보냄)
        view.findViewById(R.id.button_map).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SearchToMapActivity.class);
                intent.putExtra("longitude", "" + daumItemVo.getLng()); // 경도
                intent.putExtra("latitude", "" + daumItemVo.getLat()); // 위도
                context.startActivity(intent); // 검색으로 받은 경도, 위도를 지도에서 선택 액티비티로 넘김
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

package com.ff.modealapplication.app.ui.item;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ff.modealapplication.R;
import com.ff.modealapplication.andorid.network.SafeAsyncTask;
import com.ff.modealapplication.app.core.service.ItemService;
import com.ff.modealapplication.app.core.util.Base;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;
import java.util.Map;

/**
 * Created by bit-desktop on 2017-02-01.
 */

public class ItemListArrayAdapter extends ArrayAdapter<Map<String, Object>> {

    ItemService itemService = new ItemService();

    // 상품 목록에 기본 이미지 출력
    private LayoutInflater layoutInflater;
    DisplayImageOptions displayImageOption = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.apple)
            .showImageOnFail(R.drawable.apple)
            .delayBeforeLoading(0)
            .cacheOnDisc(true)
            .build();

    public ItemListArrayAdapter(Context context) {
        super(context, R.layout.item_list);
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) { // 내부 많이 변경 (170207/상욱변경)

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_list_row, parent, false);
        }

        final Map<String, Object> map = getItem(position);
        // 유통기한/ 상품명/ 원가/ 판매가/ 매장명/ 거리(반경)
        ((TextView) convertView.findViewById(R.id.item_list_clock)).setText(map.get("expDate").toString());
        ((TextView) convertView.findViewById(R.id.item_list_name)).setText(map.get("name").toString());
        ((TextView) convertView.findViewById(R.id.item_list_ori_price)).setText((map.get("oriPrice")).toString());
        ((TextView) convertView.findViewById(R.id.item_list_price)).setText(map.get("price").toString());

        // 액티비티로 데이터 보내기 위해서...
        ((TextView) convertView.findViewById(R.id.send_no)).
                setText(String.valueOf(((Double) map.get("no")).longValue()));

        // 상품 이미지
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getContext()));
        ImageLoader.getInstance().displayImage(Base.url + "modeal/shop/images/" + map.get("picture"),
                (ImageView) convertView.findViewById(R.id.item_list_image), displayImageOption);

        // 수정 버튼 클릭시 ------------------------------------------------------------------------
//        convertView.findViewById(R.id.button_modify_item).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), ItemModifyActivity.class);
//                intent.putExtra("no", ((TextView) v.findViewById(R.id.send_no)).getText().toString());
//                getContext().startActivity(intent);
//            }
//        });

        // 삭제 버튼 클릭시 ------------------------------------------------------------------------
        convertView.findViewById(R.id.button_delete_item).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {

                new AlertDialog.Builder(getContext()).
                        setTitle("삭제").
                        setIcon(R.drawable.delete).
                        setMessage("해당 상품을\n삭제하시겠습니까?\n").

                        setPositiveButton("예", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new ItemDelete(((Double) map.get("no")).longValue()).execute();
                                Log.d("setPositiveButton", "" + which);
                            }
                        }).

                        setNegativeButton("아니요", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).
                        show();
            }
        });
        return convertView;
    }

    // 상품 삭제되는 동안 다른 작업이 실행되기 위해서 사용 (=쓰레드?)
    private class ItemDelete extends SafeAsyncTask<Void> {
        Long no;

        public ItemDelete(Long no) {
            this.no = no;
        }

        @Override
        public Void call() throws Exception {
            itemService.itemDelete(no);
            return null;
        }

        @Override // 에러나면 Exception 발생
        protected void onException(Exception e) throws RuntimeException {
            Log.d("!!!!!!!!!!!!!", "" + e);
            super.onException(e);
        }

        @Override // 성공하면 해당 상품이 삭제된 상품목록 출력
        protected void onSuccess(Void Void) throws Exception {
            Toast.makeText(getContext(), "해당 상품이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 목록에 상품이 추가됨 ------------------------------------------------------------------------
    public void add(List<Map<String, Object>> list) {
        if (list == null) {
            return;
        }
        for (Map<String, Object> map : list) {
            Log.d("test", "" + map);
            add(map);
        }
    }
}

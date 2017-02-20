package com.ff.modealapplication.app.ui.comment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ff.modealapplication.R;
import com.ff.modealapplication.app.core.domain.CommentVo;

public class CommentListAdapter extends ArrayAdapter<CommentVo> {
    private LayoutInflater layoutInflater;

    public CommentListAdapter(Context context) {
        super(context, R.layout.row_comment_list);
        layoutInflater = layoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if(view == null) {
            view = layoutInflater.inflate(R.layout.row_comment_list, parent, false);
        }

        Log.d("포지션은 : ", position+";;");
        // 내부에서 해당 포지션의 CommentVo 객체를 받아옴
        CommentVo commentVo = getItem(position);
        Log.d("아오아오", commentVo+";;");

        // 별점 세팅
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rbList);
//        ratingBar.setRating(Float.parseFloat(String.valueOf(commentVo.getGrade())));

        // 댓글 세팅
        TextView textView = (TextView) view.findViewById(R.id.tvCommentList);
        textView.setText("tq");

        return view;
    }
}

package com.ff.modealapplication.app.ui.comment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ff.modealapplication.R;
import com.ff.modealapplication.app.core.domain.CommentVo;

import java.util.List;

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

        // 내부에서 해당 포지션의 CommentVo 객체를 받아옴
        CommentVo commentVo = getItem(position);

        // 별점 세팅
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rbList);
        ratingBar.setRating(Float.parseFloat(String.valueOf(commentVo.getGrade())));

        // 댓글 세팅
        TextView textView = (TextView) view.findViewById(R.id.tvCommentList);
        textView.setText(commentVo.getContent());

        return view;
    }

    public void add(List<CommentVo> commentVos) {
        if(commentVos == null || commentVos.size()==0) {
            return;
        }

        for(CommentVo commentVo : commentVos) {
            add(commentVo);
        }

        // notifyDataSetChanged();
    }
}

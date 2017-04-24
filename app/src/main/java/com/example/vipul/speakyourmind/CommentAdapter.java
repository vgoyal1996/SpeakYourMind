package com.example.vipul.speakyourmind;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {
    private Context context;
    private List<CommentModel> commentModelList;

    public CommentAdapter(Context context, List<CommentModel> commentModelList) {
        this.context = context;
        this.commentModelList = commentModelList;
    }

    @Override
    public CommentAdapter.CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.comment_layout,parent,false);
        return new CommentAdapter.CommentHolder(v);
    }

    @Override
    public void onBindViewHolder(final CommentAdapter.CommentHolder holder, int position) {
        CommentModel model = commentModelList.get(position);
        holder.commentUserName.setText(model.getUserName());
        holder.commentDate.setText(model.getDateOfComment());
        holder.commentText.setText(model.getComment());
    }

    public List<CommentModel> getCommentModelList() {
        return commentModelList;
    }

    public void setCommentModelList(List<CommentModel> commentModelList) {
        this.commentModelList = commentModelList;
    }

    @Override
    public int getItemCount() {
        return commentModelList.size();
    }

    static class CommentHolder extends RecyclerView.ViewHolder{
        TextView commentUserName;
        TextView commentDate;
        TextView commentText;

        public CommentHolder(View itemView) {
            super(itemView);
            commentUserName = (TextView)itemView.findViewById(R.id.comment_user_name);
            commentDate = (TextView)itemView.findViewById(R.id.comment_date);
            commentText = (TextView)itemView.findViewById(R.id.comment_text);
        }
    }

}

package org.feedhenry.apps.arthenry.util.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.feedhenry.apps.arthenry.R;
import org.feedhenry.apps.arthenry.vo.Comment;
import org.feedhenry.apps.arthenry.vo.Commit;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by summers on 11/4/15.
 */
public class CommitsDetailAdapter extends RecyclerView.Adapter<CommitsDetailAdapter.CommitsViewHolder> {

    private final ArrayList<Commit> commits;
    private final Context applicationContext;
    public CommitsDetailAdapter(ArrayList<Commit> commits, Context context) {
        this.commits = commits;
        this.applicationContext = context.getApplicationContext();
        super.setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return commits.get(position).getFHHashCode();
    }

    @Override
    public CommitsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.project_commit_layout, parent, false);

        return new CommitsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommitsViewHolder holder, int position) {
        holder.commit = commits.get(position);
        holder.position = position;
        Picasso.with(applicationContext).load(holder.commit.getPhotoUrl().toString()).into(holder.image);
        holder.commentsList.removeAllViews();
        for (Comment comment : holder.commit.getComments()) {
            View commentView =  LayoutInflater.from(applicationContext)
                    .inflate(R.layout.comment_layout, holder.commentsList, false);
            ((TextView) commentView.findViewById(R.id.author)).setText(comment.getOwnerId());
            ((TextView) commentView.findViewById(R.id.comment_text)).setText(comment.getComment());
            holder.commentsList.addView(commentView);
        }
        holder.commentsList.requestLayout();
    }


    @Override
    public int getItemCount() {
        return commits.size();
    }

    public class CommitsViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.commit_image)
        ImageView image;
        @Bind(R.id.comments_list)
        LinearLayout commentsList;
        @Bind(R.id.add_comment)
        Button addCommentButton;

        Commit commit;
        public int position;

        public CommitsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        @OnClick(R.id.add_comment)
        public void addComment(View view) {
            Comment firstComment = new Comment();
            firstComment.setOwnerId("8675309");
            firstComment.setComment("This is a new comment for testing");
            commit.getComments().add(firstComment);
            notifyDataSetChanged();
            notifyItemChanged(position);
        }

    }
}

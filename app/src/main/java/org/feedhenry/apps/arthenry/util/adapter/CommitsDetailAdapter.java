package org.feedhenry.apps.arthenry.util.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.feedhenry.apps.arthenry.R;
import org.feedhenry.apps.arthenry.events.ProjectsAvailable;
import org.feedhenry.apps.arthenry.fh.FHClient;
import org.feedhenry.apps.arthenry.vo.Account;
import org.feedhenry.apps.arthenry.vo.Comment;
import org.feedhenry.apps.arthenry.vo.Commit;
import org.feedhenry.apps.arthenry.vo.Project;
import org.json.fh.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by summers on 11/4/15.
 */
public class CommitsDetailAdapter extends RecyclerView.Adapter<CommitsDetailAdapter.CommitsViewHolder> {

    private final ArrayList<Commit> commits;
    private final Context applicationContext;
    private Picasso picasso;
    private final Account loggedInUser;
    private final Project project;
    private final FHClient fhClient;

    public CommitsDetailAdapter(ArrayList<Commit> commits, Context context, Account loggedInUser, Project project, FHClient fhClient) {
        this.commits = commits;
        this.loggedInUser = loggedInUser;
        this.project = project;
        this.fhClient = fhClient;
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
        holder.project = project;
        holder.position = position;
        picasso.load(holder.commit.getPhotoUrl().toString()).into(holder.image);
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

    @Subscribe
    public void memesAvailable(ProjectsAvailable event) {
        Set<Project> allData = new HashSet<>(event.getProjects());
        for (Project project : allData) {
            if (this.project.getId().equals(project.getId())) {
                this.commits.clear();
                this.commits.addAll(project.getCommits());
                notifyDataSetChanged();
                break;
            }
        }

    }


    @Override
    public int getItemCount() {
        return commits.size();
    }

    public void setPicasso(Picasso picasso) {
        this.picasso = picasso;
    }

    public class CommitsViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.commit_image)
        ImageView image;
        @Bind(R.id.comments_list)
        LinearLayout commentsList;
        @Bind(R.id.comment_field)
        EditText commentField;
        @Bind(R.id.add_comment)
        Button addCommentButton;

        Commit commit;
        Project project;
        public int position;

        public CommitsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        @OnClick(R.id.add_comment)
        public void addComment(View view) {
            Comment firstComment = new Comment();
            firstComment.setOwnerId(loggedInUser.getName());
            firstComment.setComment(commentField.getText().toString());
            commit.getComments().add(firstComment);

            JSONObject object = new JSONObject(new Gson().toJson(project));
            try {
                fhClient.getSyncClient().update("photos", project.getId(), object);
            } catch (Exception e) {
                Log.d("COMMIT-DETAIL", e.getMessage(), e);
            }

        }

    }
}

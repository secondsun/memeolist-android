package org.feedhenry.apps.arthenry.util.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.feedhenry.apps.arthenry.R;
import org.feedhenry.apps.arthenry.vo.Commit;
import org.feedhenry.apps.arthenry.vo.Project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by summers on 11/3/15.
 */
public class ProjectViewAdapter extends RecyclerView.Adapter<ProjectViewAdapter.ProjectViewHolder>  {

    private List<Project> items = new ArrayList<>();
    private final Context appContext;
    private Picasso picasso;

    public ProjectViewAdapter(Context appContext) {
        this.appContext = appContext.getApplicationContext();
        super.setHasStableIds(true);
    }

    @Override
    public ProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View shoppingItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.art_card, parent, false);

        return new ProjectViewHolder(shoppingItemView);
    }

    @Override
    public void onBindViewHolder(ProjectViewHolder holder, int position) {
        Project project = getProject(position);
        Commit mostRecentCommit = project.getCommits().get(project.getCommits().size() - 1);
        holder.item = project;
        picasso.load(mostRecentCommit.getPhotoUrl().toString()).into(holder.thumb);
        holder.title.setText(mostRecentCommit.getOwnerId());
        holder.firstDetail.setText(mostRecentCommit.getComments().get(0).getComment());
    }

    public synchronized Project getProject(int position) {
        return items.get(position);
    }


    @Override
    public synchronized long getItemId(int position) {
        return items.get(position).getFHhash();
    }

    @Override
    public synchronized int getItemCount() {
        return items.size();
    }

    /**
     * This method will add items from the adapters internal storage that are in itemsToSync but not
     * the internal store
     *
     * @param itemsToSync the currentViewOfTheSyncData
     */
    public synchronized void  addNewItemsFrom(Set<Project> itemsToSync) {
        for (Project item : itemsToSync) {
            if (!items.contains(item)) {
                items.add(item);
            }
        }
        Collections.sort(items);
    }

    /**
     * This method will remove items from the adapters internal storage that are not in itemsToSync
     *
     * @param itemsToSync the currentViewOfTheSyncData
     */
    public synchronized void removeMissingItemsFrom(Set<Project> itemsToSync) {
        items.retainAll(itemsToSync);
        Collections.sort(items);
    }

    public void setPicasso(Picasso picasso) {
        this.picasso = picasso;
    }

    public class ProjectViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.art_thumb)
        ImageView thumb;
        @Bind(R.id.art_card_title)
        TextView title;
        @Bind(R.id.art_card_first_detail)
        TextView firstDetail;
        private Project item;


        public ProjectViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public Project getItem() {
            return item;
        }
    }
}

package org.feedhenry.apps.arthenry.view;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;

import org.feedhenry.apps.arthenry.ArtHenryApplication;
import org.feedhenry.apps.arthenry.R;
import org.feedhenry.apps.arthenry.fh.FHClient;
import org.feedhenry.apps.arthenry.util.adapter.CommitsDetailAdapter;
import org.feedhenry.apps.arthenry.vo.Project;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by summers on 11/4/15.
 */
public class ProjectDetailDialog extends DialogFragment {

    private static final String PROJECT_KEY = "ProjectDetailDialog.PROJECT";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.commits_list)
    RecyclerView commits;

    @Inject
    Picasso picasso;

    @Inject
    FHClient fhClient;

    @Inject
    Bus bus;

    private Project project;
    private CommitsDetailAdapter adapter;

    public static ProjectDetailDialog newInstance(Project project) {
        
        Bundle props = new Bundle();
        props.putParcelable(PROJECT_KEY, project);
        
        ProjectDetailDialog dialog = new ProjectDetailDialog();
        dialog.setArguments(props);
        return dialog;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((ArtHenryApplication)activity.getApplicationContext()).getObjectGraph().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.project_details_layout, null);
        ButterKnife.bind(this, view);
        setupToolbarMenu();
        toolbar.setTitle("Project Details");
        project = getArguments().getParcelable(PROJECT_KEY);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setupCommitsList();
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(adapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(adapter);
    }

    private void setupCommitsList() {
        commits.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.adapter = new CommitsDetailAdapter(project.getCommits(), getActivity(), fhClient.getAccount(), project, fhClient);
        adapter.setPicasso(picasso);
        commits.setAdapter(adapter);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;

    }

    private void setupToolbarMenu() {
        toolbar.inflateMenu(R.menu.project_details_menu);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, project.getCommits().get(0).getPhotoUrl());
                shareIntent.setType("image/jpeg");
                startActivity(Intent.createChooser(shareIntent, "Share Meme To "));
                return true;
            }
        });
    }
}

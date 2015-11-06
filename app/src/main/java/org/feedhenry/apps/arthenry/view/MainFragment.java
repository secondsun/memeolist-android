package org.feedhenry.apps.arthenry.view;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.feedhenry.apps.arthenry.ArtHenryApplication;
import org.feedhenry.apps.arthenry.MainActivity;
import org.feedhenry.apps.arthenry.R;
import org.feedhenry.apps.arthenry.events.ProjectsAvailable;
import org.feedhenry.apps.arthenry.util.ImagePickerUtil;
import org.feedhenry.apps.arthenry.util.RecyclerItemClickListener;
import org.feedhenry.apps.arthenry.util.SwipeTouchHelper;
import org.feedhenry.apps.arthenry.util.adapter.ProjectViewAdapter;
import org.feedhenry.apps.arthenry.vo.Project;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by summers on 11/6/15.
 */
public class MainFragment extends Fragment {

    private static final String IS_TABLET = "MainFragment.IS_TABLET";
    @Bind(R.id.empty)
    View emptyView;

    @Bind(R.id.loading)
    View loadingView;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.art_cards_list)
    RecyclerView artCardsList;
    private ProjectViewAdapter adapter;


    @Inject
    Bus bus;

    @Inject
    Picasso picasso;

    private boolean isTablet = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, null);
        ButterKnife.bind(this, view);
        ((ArtHenryApplication) getActivity().getApplicationContext()).getObjectGraph().inject(this);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        adapter = new ProjectViewAdapter(getActivity().getApplicationContext());
        adapter.setPicasso(picasso);
        this.isTablet = getArguments().getBoolean(IS_TABLET, false);
        setupView();

        return view;
    }


    public static Fragment newInstance(boolean isTablet) {
        Bundle args = new Bundle();
        args.putBoolean(IS_TABLET, isTablet);
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void setupView() {
        artCardsList.setLayoutManager(new GridLayoutManager(getActivity(), isTablet ? 3 : 1));
        artCardsList.setAdapter(adapter);
        artCardsList.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity().getApplicationContext(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ((MainActivity) getActivity()).showPopup(adapter.getProject(position));
                    }
                }
        ));

        SwipeTouchHelper callback = new SwipeTouchHelper(new SwipeTouchHelper.OnItemSwipeListener() {
            @Override
            public void onItemSwipe(Project item) {

            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(artCardsList);
    }


    @Override
    public void onResume() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onStop();
        bus.unregister(this);
    }

    @Subscribe
    public void memesAvailable(ProjectsAvailable event) {
        Set<Project> allData = new HashSet<>(event.getProjects());
        if (allData.size() > 0) {
            loadingView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
            artCardsList.setVisibility(View.VISIBLE);
        } else {
            loadingView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            artCardsList.setVisibility(View.GONE);
            return;
        }

        adapter.removeMissingItemsFrom(allData);
        adapter.addNewItemsFrom(allData);

        adapter.notifyDataSetChanged();
    }



}

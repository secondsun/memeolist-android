package org.feedhenry.apps.arthenry;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.feedhenry.sdk.FHResponse;
import com.feedhenry.sdk.sync.FHSyncListener;
import com.feedhenry.sdk.sync.NotificationMessage;
import com.google.gson.Gson;

import org.feedhenry.apps.arthenry.fh.ConnectionFailure;
import org.feedhenry.apps.arthenry.fh.FHClient;
import org.feedhenry.apps.arthenry.fh.auth.FHAuthClientConfig;
import org.feedhenry.apps.arthenry.fh.sync.AbstractSyncListener;
import org.feedhenry.apps.arthenry.fh.sync.FHSyncClientConfig;
import org.feedhenry.apps.arthenry.fh.sync.InjectableSyncListener;
import org.feedhenry.apps.arthenry.util.adapter.ProjectViewAdapter;
import org.feedhenry.apps.arthenry.util.RecyclerItemClickListener;
import org.feedhenry.apps.arthenry.util.SwipeTouchHelper;
import org.feedhenry.apps.arthenry.view.ProjectDetailDialog;
import org.feedhenry.apps.arthenry.vo.Comment;
import org.feedhenry.apps.arthenry.vo.Commit;
import org.feedhenry.apps.arthenry.vo.Project;
import org.json.fh.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.TreeSet;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String DATA_ID = "photos";


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.empty)
    View emptyView;

    @Bind(R.id.art_cards_list)
    RecyclerView artCardsList;
    private ProjectViewAdapter adapter;

    @Inject
    InjectableSyncListener listener;
    @Inject
    FHClient fhClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        ((ArtHenryApplication)getApplicationContext()).getObjectGraph().inject(this);

        setSupportActionBar(toolbar);
        adapter = new ProjectViewAdapter(getApplicationContext());

        setupView();

    }

    private void setupView() {
        artCardsList.setLayoutManager(new GridLayoutManager(this, 3));
        artCardsList.setAdapter(adapter);
        artCardsList.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        showPopup(adapter.getProject(position));
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

    private void showPopup(Project project) {
        ProjectDetailDialog.newInstance(project).show(getFragmentManager(), "DETAIL");
    }

    @OnClick(R.id.fab)
    public void fabClick(View view) {

        String accountId = fhClient.getAccount().getId();

        Comment firstComment = new Comment();
        firstComment.setOwnerId(accountId);
        firstComment.setComment("This is a default comment for testing");

        Commit firstCommit = new Commit();
        firstCommit.setOwnerId(accountId);
        firstCommit.getComments().add(firstComment);
        try {
            firstCommit.setPhotoUrl(new URL("https://drive.google.com/uc?id=1voMxLt_1ZgvvveImHofWTPWq3ZfoCWSc5A"));
        } catch (MalformedURLException ignore) {
        }

        Project project = new Project();

        project.setOwnerId(accountId);
        project.getCommits().add(firstCommit);

        JSONObject projectJson = new JSONObject(new Gson().toJson(project));
        try {
            fhClient.getSyncClient().create(DATA_ID, projectJson);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        listener.attachListener(new SyncHandler());
    }

    @Override
    protected void onStop() {
        super.onStop();
        listener.detachListener();
    }

    private class SyncHandler extends AbstractSyncListener {
        @Override
        //On sync complete, list all the data and update the adapter
        public void onSyncCompleted(NotificationMessage pMessage) {
            Log.d(TAG, "syncClient - onSyncCompleted");
            Log.d(TAG, "Sync message: " + pMessage.getMessage());

            JSONObject allData = fhClient.getSyncClient().list(DATA_ID);
            if (allData.length() > 0) {
                emptyView.setVisibility(View.GONE);
                artCardsList.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.VISIBLE);
                artCardsList.setVisibility(View.GONE);
                return;
            }
            Iterator<String> it = allData.keys();
            TreeSet<Project> itemsToSync = new TreeSet<Project>();

            while (it.hasNext()) {
                String key = it.next();
                JSONObject data = allData.getJSONObject(key);
                JSONObject dataObj = data.getJSONObject("data");
                Project item = new Gson().fromJson(dataObj.toString(), Project.class);
                itemsToSync.add(item);
            }

            adapter.removeMissingItemsFrom(itemsToSync);
            adapter.addNewItemsFrom(itemsToSync);

            adapter.notifyDataSetChanged();
        }

        @Override
        public void onLocalUpdateApplied(NotificationMessage pMessage) {
            Log.d(TAG, "syncClient - onLocalUpdateApplied");

            JSONObject allData = fhClient.getSyncClient().list(DATA_ID);
            Iterator<String> it = allData.keys();
            if (allData.length() > 0) {
                emptyView.setVisibility(View.GONE);
                artCardsList.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.VISIBLE);
                artCardsList.setVisibility(View.GONE);
                return;
            }
            TreeSet<Project> itemsToSync = new TreeSet<Project>();

            while (it.hasNext()) {
                String key = it.next();
                JSONObject data = allData.getJSONObject(key);
                JSONObject dataObj = data.getJSONObject("data");
                Project item = new Gson().fromJson(dataObj.toString(), Project.class);
                itemsToSync.add(item);
            }

            adapter.removeMissingItemsFrom(itemsToSync);
            adapter.addNewItemsFrom(itemsToSync);

            adapter.notifyDataSetChanged();
        }

    }

}

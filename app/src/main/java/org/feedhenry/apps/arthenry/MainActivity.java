package org.feedhenry.apps.arthenry;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
import org.feedhenry.apps.arthenry.fh.sync.FHSyncClientConfig;
import org.feedhenry.apps.arthenry.vo.Comment;
import org.feedhenry.apps.arthenry.vo.Commit;
import org.feedhenry.apps.arthenry.vo.Project;
import org.json.fh.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.TreeSet;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements InitCallbackListener, FHSyncListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String DATA_ID = "photos";
    private FHClient fhClient;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.empty)
    View emptyView;

    @Bind(R.id.art_cards_list)
    RecyclerView artCardsList;
    private ProjectViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        adapter = new ProjectViewAdapter(getApplicationContext());
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

        this.fhClient = new FHClient.Builder(this)
                .addInitCallback(this)
                .addFeature(new FHSyncClientConfig(this)
                        .addDataSet(DATA_ID)
                        .setNotifySyncStarted(true)
                        .setAutoSyncLocalUpdates(true)
                        .setNotifySyncComplete(true)
                        .setSyncFrequencySeconds(10))
                .addFeature(new FHAuthClientConfig("Google")
                        .setCallingActivity(this))
                .build();

    }

    private void showPopup(Project project) {
        Toast.makeText(this, "Project clicked", Toast.LENGTH_LONG).show();
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
        fhClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        fhClient.disconnect();
    }

    @Override
    public void onInit(FHResponse fhResponse) {

    }

    @Override
    public void onInitError(ConnectionFailure fhResponse) {
        if (fhResponse.hasResolution()) {
            fhResponse.resolve(this, fhResponse.getResponseCode());
        } else {
            Toast.makeText(this, "Failure", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSyncStarted(NotificationMessage notificationMessage) {

    }


    @Override
    public void onUpdateOffline(NotificationMessage notificationMessage) {

    }

    @Override
    public void onCollisionDetected(NotificationMessage notificationMessage) {

    }

    @Override
    public void onRemoteUpdateFailed(NotificationMessage notificationMessage) {

    }

    @Override
    public void onRemoteUpdateApplied(NotificationMessage notificationMessage) {

    }

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

    @Override
    public void onDeltaReceived(NotificationMessage notificationMessage) {

    }

    @Override
    public void onSyncFailed(NotificationMessage notificationMessage) {

    }

    @Override
    public void onClientStorageFailed(NotificationMessage notificationMessage) {

    }
}

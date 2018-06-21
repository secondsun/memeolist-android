package org.aerogear.apps.memeolist.view;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.feedhenry.sdk.FH;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;

import org.aerogear.apps.memeolist.MemeolistApplication;
import org.aerogear.apps.memeolist.R;
import org.aerogear.apps.memeolist.fh.FHClient;
import org.aerogear.apps.memeolist.util.adapter.CommitsDetailAdapter;
import org.aerogear.apps.memeolist.vo.Project;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

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
        ((MemeolistApplication)activity.getApplicationContext()).getObjectGraph().inject(this);
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
        this.adapter = new CommitsDetailAdapter(project.getCommits(), getActivity(), fhClient.getAccount(), project, fhClient, commits);
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
                final Intent shareIntent = new Intent();

                new AsyncTask<Object, Object, Object>() {
                    Uri uri;
                    @Override
                    protected Object doInBackground(Object[] params) {
                        URL fileUrl = project.getCommits().get(0).getPhotoUrl();
                        String fileName = Uri.parse(String.valueOf(fileUrl)).getLastPathSegment();
                        try {
                            File file = File.createTempFile(fileName, null, getActivity().getExternalCacheDir());
                            FileOutputStream os = new FileOutputStream(file);
                            OkHttpClient client = new OkHttpClient();
                            Request.Builder requestBuilder = new Request.Builder().url(fileUrl);
                            for (Header header : FH.getDefaultParamsAsHeaders(null)){
                                requestBuilder.addHeader(header.getName(), header.getValue());
                            }

                            Request request = requestBuilder.build();
                            Response response = client.newCall(request).execute();
                            os.write(response.body().bytes());
                            os.close();
                            String[] path = {file.getCanonicalPath()};
                            String[] mimeType = {"image/jpeg"};
                            MediaScannerConnection.scanFile(getActivity(), path, mimeType, new MediaScannerConnection.MediaScannerConnectionClient() {
                                @Override
                                public void onMediaScannerConnected() {

                                }

                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    shareIntent.setAction(Intent.ACTION_SEND);
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                    shareIntent.setType("image/jpeg");
                                    startActivity(Intent.createChooser(shareIntent, "Share Meme To "));
                                }
                            });

                        } catch (Exception e) {
                            Log.d("Download to Share", e.getMessage(),e);
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);

                    }
                }.execute();

                return true;
            }
        });
    }
}

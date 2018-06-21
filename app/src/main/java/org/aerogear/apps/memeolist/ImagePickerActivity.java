package org.aerogear.apps.memeolist;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.aerogear.apps.memeolist.util.adapter.ImagesAdapter;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ImagePickerActivity extends AppCompatActivity {

    public static final String ACTION_MEME_PICK = "com.fh.ImagePickerActivity";
    private ImagesAdapter adapter;

    @Bind(R.id.images)
    RecyclerView recycler;

    @Inject
    Bus bus;
    @Inject
    Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_picker);
        ButterKnife.bind(this);
        ((MemeolistApplication) getApplicationContext()).getObjectGraph().inject(this);

        recycler.setLayoutManager(new GridLayoutManager(this, 2));
        recycler.setAdapter(adapter = new ImagesAdapter(this, bus, picasso));

    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

   @Subscribe
    public void cardClick(ImagesAdapter.ImageSelectedEvent event) {
       Intent resultIntent = new Intent("RESULT_OK", Uri.parse(event.getImageUrl()));
       setResult(Activity.RESULT_OK, resultIntent);
       finish();
   }

}

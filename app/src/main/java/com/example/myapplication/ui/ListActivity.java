package com.example.myapplication.ui;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ApiInterface;
import com.example.myapplication.model.Summon;
import com.example.myapplication.utils.AudioLab;
import com.example.myapplication.model.GetResponse;
import com.example.myapplication.R;
import com.example.myapplication.utils.MusicUtils;
import com.example.myapplication.utils.SummonLab;
import com.example.myapplication.utils.Tools;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView       mMainList;
    private SummonAdapter      mAdapter;
    private AudioLab           mAudioLab;
    private SummonLab          mSummonLab;
    private ImageButton        bt_play;
    private MediaPlayer        mp;
    private Handler            mHandler = new Handler();
    private View               parent_view, view_mini_player;
    private ProgressBar song_progressbar;
    private MusicUtils  utils;
    private TextView    mini_player_title, mini_player_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initToolbar();

        parent_view      = findViewById(R.id.parent_view);
        view_mini_player = findViewById(R.id.view_mini_player);
        mini_player_title = findViewById(R.id.title);
        mini_player_text = findViewById(R.id.text);

        mMainList           = findViewById(R.id.audio_list);
        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        bt_play             = findViewById(R.id.bt_play);
        song_progressbar    = findViewById(R.id.song_progressbar);
        song_progressbar.setProgress(0);
        song_progressbar.setMax(MusicUtils.MAX_PROGRESS);

        mp = new MediaPlayer();
        mp.setOnCompletionListener(mp -> {
            bt_play.setImageResource(R.drawable.ic_play_arrow);
        });
        utils = new MusicUtils();
        buttonPlayerAction();

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mMainList.setLayoutManager(new LinearLayoutManager(this));
        mAudioLab  = AudioLab.get(this);
        mSummonLab = SummonLab.get(this);
        loadAudios();
    }

    private void loadAudio(Summon summon) {
        try {
            mp.reset();
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            AssetFileDescriptor afd = getAssets().openFd("short_music.mp3");
            mp.setDataSource(summon.getAudio());
//            afd.close();
            mp.prepare();
            bt_play.callOnClick();
        } catch (Exception e) {
            Snackbar.make(parent_view, "Cannot load audio file", Snackbar.LENGTH_SHORT).show();
        }
    }


    /**
     * Play button click event plays a song and changes button to pause image
     * pauses a song and changes button to play image
     */
    private void buttonPlayerAction() {
        bt_play.setOnClickListener(arg0 -> {
            // check for already playing
            if (mp.isPlaying()) {
                mp.pause();
                // Changing button image to play button
                bt_play.setImageResource(R.drawable.ic_play_arrow);
            } else {
                // Resume song
                mp.start();
                // Changing button image to pause button
                bt_play.setImageResource(R.drawable.ic_pause);
                mHandler.post(mUpdateTimeTask);
            }

        });
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            updateTimerAndSeekbar();
            // Running this thread after 10 milliseconds
            if (mp.isPlaying()) {
                mHandler.postDelayed(this, 100);
            }
        }
    };

    private void updateTimerAndSeekbar() {
        long totalDuration   = mp.getDuration();
        long currentDuration = mp.getCurrentPosition();

        // Updating progress bar
        int progress = utils.getProgressSeekBar(currentDuration, totalDuration);
        song_progressbar.setProgress(progress);
    }

    public void controlClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_expand: {
                Snackbar.make(parent_view, "Expand", Snackbar.LENGTH_SHORT).show();
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimeTask);
        mp.release();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("lessons");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_audio_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_add:
//                startActivity(new Intent(ListActivity.this, MainActivity.class));
                startActivity(new Intent(ListActivity.this, AudioUploadFormActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void updateUI() {
        SummonLab    summonLab = SummonLab.get(ListActivity.this);
        List<Summon> summons   = summonLab.getRealSummons();
        if (mAdapter == null) {
            mAdapter = new SummonAdapter(this, summons);
            mMainList.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener((view, obj, position) -> {
//                        Snackbar.make(parent_view, "Item " + obj.getName() + " clicked", Snackbar.LENGTH_SHORT).show();
                loadAudio(obj);
                view_mini_player.setVisibility(View.VISIBLE);
                mini_player_text.setText(obj.getName());
                mini_player_title.setText(obj.getTitle());
            });


            mAdapter.setOnMoreButtonClickListener((view, obj, item) -> Snackbar.make(parent_view, obj.getName() + " (" + item.getTitle() + ") clicked", Snackbar.LENGTH_SHORT).show());

        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void loadAudios() {
        ApiInterface      apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<GetResponse> call         = apiInterface.readAudioList();
        call.enqueue(new Callback<GetResponse>() {
            @Override
            public void onResponse(Call<GetResponse> call, Response<GetResponse> response) {
                if (response.isSuccessful()) {

                    assert response.body() != null;
                    ArrayList<Summon> summons = response.body().getSummons();
                    for (Summon summon : summons) {
                        mSummonLab.addSummon(summon);
                    }
                    updateUI();
                    mSwipeRefreshLayout.setRefreshing(false);
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(ListActivity.this, "No summons", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetResponse> call, Throwable t) {
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(ListActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onRefresh() {
        loadAudios();
    }


}

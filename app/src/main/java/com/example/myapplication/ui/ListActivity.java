package com.example.myapplication.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.example.myapplication.model.RealAudio;
import com.example.myapplication.utils.MusicUtils;
import com.example.myapplication.utils.SummonLab;
import com.example.myapplication.utils.Tools;

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
    private ImageButton bt_play;
    private     MediaPlayer mp;
    private Handler     mHandler = new Handler();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initToolbar();


        mMainList           = findViewById(R.id.audio_list);
        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        bt_play = findViewById(R.id.bt_play);
        song_progressbar = findViewById(R.id.song_progressbar);
        song_progressbar.setProgress(0);
        song_progressbar.setMax(MusicUtils.MAX_PROGRESS);


        mSwipeRefreshLayout.setOnRefreshListener(this);
        mMainList.setLayoutManager(new LinearLayoutManager(this));
        mAudioLab  = AudioLab.get(this);
        mSummonLab = SummonLab.get(this);
        loadAudios();
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
            mAdapter = new SummonAdapter(summons);
            mMainList.setAdapter(mAdapter);
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

    private class SummonHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Summon mSummon;

        private TextView mNameTextView, mDateTextView, mTopicTextView, mTitleTextView;

        public SummonHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_list, parent, false));
            itemView.setOnClickListener(this);

            mNameTextView  = itemView.findViewById(R.id.text);
            mTitleTextView = itemView.findViewById(R.id.title);
//          mDateTextView  = itemView.findViewById(R.id.tv_date);
//          mTopicTextView = itemView.findViewById(R.id.tv_topic);
        }

        public void bind(Summon summon) {
            mSummon = summon;
            mNameTextView.setText(mSummon.getName());
            mTitleTextView.setText(mSummon.getTitle());
//          mDateTextView.setText(mSummon.getCreated_at());
//          mTopicTextView.setText(mSummon.getTopic());
        }

        @Override
        public void onClick(View v) {

        }
    }

    private class SummonAdapter extends RecyclerView.Adapter<SummonHolder> {
        private List<Summon> mSummons;

        public SummonAdapter(List<Summon> summons) {
            mSummons = summons;
        }

        @NonNull
        @Override
        public SummonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(ListActivity.this);
            return new SummonHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull SummonHolder holder, int position) {
            Summon summon = mSummons.get(position);
            holder.bind(summon);
        }

        @Override
        public int getItemCount() {
            return mSummons.size();
        }
    }


}

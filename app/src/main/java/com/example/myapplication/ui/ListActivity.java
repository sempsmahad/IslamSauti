package com.example.myapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.R;
import com.example.myapplication.data.model.RealAudio;
import com.example.myapplication.data.ApiClient;
import com.example.myapplication.data.ApiInterface;
import com.example.myapplication.data.model.AudioLab;
import com.example.myapplication.data.model.GetResponse;
import com.example.myapplication.utils.Tools;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView       mMainList;
    private AudioAdapter mAdapter;
    private AudioLab     mAudioLab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initToolbar();

        mMainList           = findViewById(R.id.audio_list);
        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mMainList.setLayoutManager(new LinearLayoutManager(this));
        mAudioLab = AudioLab.get(this);
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
        AudioLab        audioLab = AudioLab.get(ListActivity.this);
        List<RealAudio> audios   = audioLab.getRealAudios();
        //Toast.makeText(this, audios.get(1).getTopic(), Toast.LENGTH_SHORT).show();
        if (mAdapter == null) {
            mAdapter = new AudioAdapter(audios);
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
                for (RealAudio audio : response.body().getAudios()) {
                    RealAudio ad = new RealAudio();
                    ad.setName(audio.getName());
                    ad.setDate(audio.getDate());
                    ad.setTopic(audio.getTopic());
                    ad.setId((int) audio.getId());
                    ad.setUrl(audio.getUrl());
                    mAudioLab.addAudio(ad);
                }
                updateUI();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<GetResponse> call, Throwable t) {

            }
        });

    }

    @Override
    public void onRefresh() {
        loadAudios();
    }


    private class AudioHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private RealAudio mRealAudio;

        private TextView mNameTextView;
        private TextView mDateTextView;
        private TextView mTopicTextView;

        public AudioHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.layout_summon_view, parent, false));
            itemView.setOnClickListener(this);

            mNameTextView  = itemView.findViewById(R.id.tv_name);
            mDateTextView  = itemView.findViewById(R.id.tv_date);
            mTopicTextView = itemView.findViewById(R.id.tv_topic);
        }

        public void bind(RealAudio realAudio) {
            mRealAudio = realAudio;
            mNameTextView.setText(mRealAudio.getName());
            mDateTextView.setText(mRealAudio.getDate());
            mTopicTextView.setText(mRealAudio.getTopic());
        }


        @Override
        public void onClick(View v) {

        }
    }

    private class AudioAdapter extends RecyclerView.Adapter<AudioHolder> {
        private List<RealAudio> mRealAudios;

        public AudioAdapter(List<RealAudio> realAudios) {
            mRealAudios = realAudios;
        }

        @NonNull
        @Override
        public AudioHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(ListActivity.this);
            return new AudioHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull AudioHolder holder, int position) {
            RealAudio realAudio = mRealAudios.get(position);
            holder.bind(realAudio);
        }

        @Override
        public int getItemCount() {
            return mRealAudios.size();
        }
    }


}

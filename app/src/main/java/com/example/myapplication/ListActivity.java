package com.example.myapplication;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.activities.AudioUploadFormActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView       mMainList;
    private AudioAdapter       mAdapter;
    private AudioLab           mAudioLab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mMainList           = findViewById(R.id.audio_list);
        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mMainList.setLayoutManager(new LinearLayoutManager(this));

        mAudioLab = AudioLab.get(this);

        loadAudios();

//        SnapAdapter<RealAudio,AudiosViewHolder> adapterRecycler = new SnapAdapter<>(
//                this,
//                RealAudio.class, //Model class, matching generic type
//                R.layout.single_audio_view, // Item Layout
//                AudiosViewHolder.class);
//
//        mainList.setAdapter(adapterRecycler);
//        adapterRecycler.addAll(audioList);
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
    //    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.fragment_crime_list, menu);
//
//        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
//        if (mSubtitleVisible) {
//            subtitleItem.setTitle(R.string.hide_subtitle);
//        } else {
//            subtitleItem.setTitle(R.string.show_subtitle);
//        }
//    }

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
                for (RealAudio audio : response.body().audios) {
                    RealAudio ad = new RealAudio();
                    ad.setName(audio.name);
                    ad.setDate(audio.date);
                    ad.setTopic(audio.topic);
                    ad.setId((int) audio.id);
                    ad.setUrl(audio.url);
                    mAudioLab.addAudio(ad);
                }
                updateUI();
                mSwipeRefreshLayout.setRefreshing(false);
//                SnapAdapter<RealAudio,AudiosViewHolder> adapterRecycler = new SnapAdapter<>(
//                        ListActivity.this,
//                        RealAudio.class, //Model class, matching generic type
//                        R.layout.single_audio_view, // Item Layout
//                        AudiosViewHolder.class);
//
//                mainList.setAdapter(adapterRecycler);
//                adapterRecycler.addAll(audioList);
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
            super(inflater.inflate(R.layout.single_audio_view, parent, false));
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

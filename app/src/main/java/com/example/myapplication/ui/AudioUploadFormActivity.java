package com.example.myapplication.ui;

import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.myapplication.api.ApiClient;
import com.example.myapplication.api.ApiInterface;
import com.example.myapplication.model.Audio;
import com.example.myapplication.model.Summon;
import com.example.myapplication.utils.FileUtil;
import com.example.myapplication.utils.ProgressRequestBody;
import com.example.myapplication.R;
import com.example.myapplication.utils.MusicUtils;
import com.example.myapplication.utils.Tools;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AudioUploadFormActivity extends AppCompatActivity implements ProgressRequestBody.UploadCallbacks {

    @Override
    public void onProgressUpdate(int percentage) {
        progress_bar.setProgress(percentage);
        buttonState = buttonState.LOADING;
        progress_bar.setProgress(percentage);
        tv_status.setText(percentage + " %");
        tv_status.setVisibility(View.VISIBLE);
        icon_download.setVisibility(View.GONE);
        tv_download.setText("UPLOADING...");
        button_action.setClickable(false);
    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {
        buttonState = buttonState.DONE;
        progress_bar.setProgress(0);
        tv_status.setVisibility(View.GONE);
        tv_download.setText("UPLOADED");
        button_action.setClickable(true);
        icon_download.setVisibility(View.VISIBLE);
        icon_download.setImageResource(R.drawable.ic_download_done);
        card_view.setCardBackgroundColor(getResources().getColor(R.color.green_500));
    }

    enum ButtonState {
        NORMAL, LOADING, DONE
    }

    private View                 parent_view;
    private AppCompatSeekBar     seek_song_progressbar;
    private FloatingActionButton bt_play;
    private FloatingActionButton btn_attach;
    private TextView             tv_song_current_duration, tv_song_total_duration;
    private EditText et_title, et_description, et_topic, et_sheikh_name, et_date;

    private ProgressBar progress_bar;
    private Handler     mUploadBtnHandler;
    private View        button_action;
    private TextView    tv_status, tv_download;
    private ImageView    icon_download;
    private CardView     card_view;
    private Runnable     runnable;
    private Uri          path = null;
    private Call<Summon> call;


    private ButtonState buttonState = ButtonState.NORMAL;

    private MediaPlayer mp;
    // Handler to update UI timer, progress bar etc,.
    private Handler     mHandler = new Handler();

    private MusicUtils utils;


    @Override
    protected void onStop() {
        super.onStop();
        if (mUploadBtnHandler == null || runnable == null) return;
        mUploadBtnHandler.removeCallbacks(runnable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_checkout);
        initToolbar();
        initComponent();

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type   = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("audio/")) {
                handleSendAudio(intent); // Handle text being sent
            }
        }

    }

    private void handleSendAudio(Intent intent) {
        Uri audioUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (audioUri != null) {
            onGetPath(audioUri);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Checkout");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_audio_list, menu);
        menu.findItem(R.id.btn_add).setVisible(false);
        menu.findItem(R.id.btn_clear).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_clear:
                mp.reset();
                bt_play.setEnabled(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initComponent() {
        parent_view           = findViewById(R.id.parent_view);
        seek_song_progressbar = findViewById(R.id.seek_song_progressbar);
        bt_play               = findViewById(R.id.bt_play);
        btn_attach            = findViewById(R.id.btn_attach);
        et_title              = findViewById(R.id.et_title);
        et_topic              = findViewById(R.id.et_topic);
        et_sheikh_name        = findViewById(R.id.et_sheikh_name);
        et_description        = findViewById(R.id.et_description);
        et_date               = findViewById(R.id.bt_exp_date);

        Calendar calendar = new GregorianCalendar(Locale.getDefault());
        int      year     = calendar.get(Calendar.YEAR);
        int      month    = calendar.get(Calendar.MONTH) + 1;
        int      day      = calendar.get(Calendar.DAY_OF_MONTH);

        et_date.setText(String.format("%02d", day) + "/" + String.format("%02d", month) + "/" + year);

        et_date.setOnClickListener(v -> dialogDatePickerLight(v));

        mUploadBtnHandler = new Handler(this.getMainLooper());
        progress_bar      = findViewById(R.id.progress_bar);
        progress_bar.setProgress(0);

        button_action = findViewById(R.id.button_action);
        tv_status     = findViewById(R.id.tv_status);
        tv_download   = findViewById(R.id.tv_download);
        icon_download = findViewById(R.id.icon_download);
        card_view     = findViewById(R.id.card_view);

        button_action.setOnClickListener(v -> {
            if (buttonState == ButtonState.DONE) {
                onResetClicked(v);
            } else {
//                runProgressDeterminateCircular();
                try {
                    upload(v);
                } catch (NullPointerException e) {
                    Snackbar.make(parent_view, "Please first select an audio file", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        btn_attach.setOnClickListener(v -> {
            Intent intent_upload = new Intent();
            intent_upload.setType("audio/*");
            intent_upload.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent_upload, 1);
        });


//        new Handler(this.getMainLooper()).postDelayed(this::runProgressDeterminateCircular, 500);

        // set Progress bar values
        seek_song_progressbar.setProgress(0);
        seek_song_progressbar.setMax(MusicUtils.MAX_PROGRESS);

        tv_song_current_duration = findViewById(R.id.tv_song_current_duration);
        tv_song_total_duration   = findViewById(R.id.tv_song_total_duration);

        // Media Player
        mp = new MediaPlayer();
        mp.setOnCompletionListener(mp -> {
            // Changing button image to play button
            bt_play.setImageResource(R.drawable.ic_play_arrow);
        });

        utils = new MusicUtils();
        // Listeners
        seek_song_progressbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // remove message Handler from updating progress bar
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                int totalDuration   = mp.getDuration();
                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

                // forward or backward to certain seconds
                mp.seekTo(currentPosition);

                // update timer progress again
                mHandler.post(mUpdateTimeTask);
            }
        });
        buttonPlayerAction();
        updateTimerAndSeekbar();

    }

    private void runProgressDeterminateCircular() {
        runnable = new Runnable() {
            public void run() {
                buttonState = buttonState.LOADING;
                int progress = progress_bar.getProgress() + 1;
                progress_bar.setProgress(progress);
                tv_status.setText(progress + " %");
                tv_status.setVisibility(View.VISIBLE);
                icon_download.setVisibility(View.GONE);
                tv_download.setText("UPLOADING...");
                button_action.setClickable(false);
                if (progress > 100) {
                    buttonState = buttonState.DONE;
                    progress_bar.setProgress(0);
                    tv_status.setVisibility(View.GONE);
                    tv_download.setText("UPLOADED");
                    button_action.setClickable(true);
                    icon_download.setVisibility(View.VISIBLE);
                    icon_download.setImageResource(R.drawable.ic_download_done);
                    card_view.setCardBackgroundColor(getResources().getColor(R.color.green_500));
                } else {
                    mUploadBtnHandler.postDelayed(this, 20);
                }
            }
        };
        mUploadBtnHandler.post(runnable);
    }

    public void onResetClicked(View view) {
        button_action.setClickable(true);
        buttonState = buttonState.NORMAL;
        icon_download.setImageResource(R.drawable.ic_file_download);
        tv_status.setText("");
        icon_download.setVisibility(View.VISIBLE);
        tv_status.setVisibility(View.GONE);
        tv_download.setText("DOWNLOAD");
        card_view.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    private void dialogDatePickerLight(final View v) {
        Calendar cur_calender = Calendar.getInstance();

        DatePickerDialog datePicker = DatePickerDialog.newInstance(
                (view, year, monthOfYear, dayOfMonth) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    long date = calendar.getTimeInMillis();
                    ((EditText) v).setText(Tools.getFormattedDateStroked(date));
                },
                cur_calender.get(Calendar.YEAR),
                cur_calender.get(Calendar.MONTH),
                cur_calender.get(Calendar.DAY_OF_MONTH)
        );
        //set dark light
        datePicker.setThemeDark(false);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.show(getFragmentManager(), "Expiration Date");
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

        // Displaying Total Duration time
        tv_song_total_duration.setText(utils.milliSecondsToTimer(totalDuration));
        // Displaying time completed playing
        tv_song_current_duration.setText(utils.milliSecondsToTimer(currentDuration));

        // Updating progress bar
        int progress = (int) (utils.getProgressSeekBar(currentDuration, totalDuration));
        seek_song_progressbar.setProgress(progress);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            path = data.getData();
            onGetPath(path);
        }
    }

    private void onGetPath(Uri mPath) {
        loadAudioIntoPlayer(mPath);
        String fileName = getFileName(mPath);
        et_title.setText(fileName);

    }

    private String getFileName(Uri urim) throws IllegalArgumentException {
        Cursor cursor = getContentResolver().query(urim, null, null, null, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            throw new IllegalArgumentException("Can't obtain file name, cursor is empty");
        }
        cursor.moveToFirst();
        String fileName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
        cursor.close();
        return fileName;
    }

    /**
     * Play button click event plays a song and changes button to pause image
     * pauses a song and changes button to play image
     */
    private void buttonPlayerAction() {
        bt_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
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
                    // Updating progress bar
                    mHandler.post(mUpdateTimeTask);
                }
            }
        });
    }

    private void loadAudioIntoPlayer(Uri path) {
        try {
            mp.reset();
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //TODO change this to the selected song
            mp.setDataSource(AudioUploadFormActivity.this, path);
            mp.prepare();
            bt_play.setEnabled(true);
        } catch (Exception e) {
            Snackbar.make(parent_view, "Cannot load audio file", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void upload(View view) {
        String title       = et_title.getText().toString().trim();
        String topic       = et_topic.getText().toString().trim();
        String shekName    = et_sheikh_name.getText().toString().trim();
        String description = et_description.getText().toString().trim();
        String date        = et_date.getText().toString().trim();

        File        file      = new File(FileUtil.getPath(path, this));
        RequestBody descTitle = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody descTopic = RequestBody.create(MediaType.parse("text/plain"), topic);
        RequestBody descName  = RequestBody.create(MediaType.parse("text/plain"), shekName);
        RequestBody descDesc  = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody descDate  = RequestBody.create(MediaType.parse("text/plain"), date);

        ProgressRequestBody fileBody = new ProgressRequestBody(file, "audio", AudioUploadFormActivity.this);
        MultipartBody.Part  filePart = MultipartBody.Part.createFormData("audio", file.getName(), fileBody);

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        call = apiInterface.uploadAudio(descTitle, descTopic, descName, descDesc, descDate, filePart);

        call.enqueue(new Callback<Summon>() {
            @Override
            public void onResponse(Call<Summon> call, Response<Summon> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AudioUploadFormActivity.this, "File Uploaded Successfully...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "uploading Failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Summon> call, Throwable t) {
                if (call.isCanceled()) {
                    Toast.makeText(AudioUploadFormActivity.this, "request was cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

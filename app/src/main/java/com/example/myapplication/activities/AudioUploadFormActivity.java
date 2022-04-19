package com.example.myapplication.activities;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.utils.MusicUtils;
import com.example.myapplication.utils.Tools;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class AudioUploadFormActivity extends AppCompatActivity {

    enum ButtonState {
        NORMAL, LOADING, DONE
    }

    private View                 parent_view;
    private AppCompatSeekBar     seek_song_progressbar;
    private FloatingActionButton bt_play;
    private FloatingActionButton btn_attach;
    private TextView             tv_song_current_duration, tv_song_total_duration;
    private EditText et_title, et_description, et_topic, et_date;

    private ProgressBar progress_bar;
    private Handler     mUploadBtnHandler;
    private View        button_action;
    private TextView    tv_status, tv_download;
    private ImageView icon_download;
    private CardView  card_view;
    private Runnable  runnable;
    private Uri       path = null;

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

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Checkout");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    private void initComponent() {


        parent_view           = findViewById(R.id.parent_view);
        seek_song_progressbar = findViewById(R.id.seek_song_progressbar);
        bt_play               = findViewById(R.id.bt_play);
        btn_attach            = findViewById(R.id.btn_attach);
        et_title              = findViewById(R.id.et_title);
        et_description        = findViewById(R.id.et_description);
        et_topic              = findViewById(R.id.et_topic);
        et_date               = findViewById(R.id.et_date);

        Calendar calendar = new GregorianCalendar(Locale.getDefault());
        int      year     = calendar.get(Calendar.YEAR);
        int      month    = calendar.get(Calendar.MONTH);
        int      day      = calendar.get(Calendar.DAY_OF_MONTH);

        et_date.setText(day + "/" + month + "/" + year);

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
                runProgressDeterminateCircular();
            }
        });
        btn_attach.setOnClickListener(v -> {
            Intent intent_upload = new Intent();
            intent_upload.setType("audio/*");
            intent_upload.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent_upload, 1);
        });


        new Handler(this.getMainLooper()).postDelayed(this::runProgressDeterminateCircular, 500);

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

        try {
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //TODO change this to the selected song
            AssetFileDescriptor afd = getAssets().openFd("short_music.mp3");
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mp.prepare();
        } catch (Exception e) {
            Snackbar.make(parent_view, "Cannot load audio file", Snackbar.LENGTH_SHORT).show();
        }

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
                tv_download.setText("DOWNLOADING...");
                button_action.setClickable(false);
                if (progress > 100) {
                    buttonState = buttonState.DONE;
                    progress_bar.setProgress(0);
                    tv_status.setVisibility(View.GONE);
                    tv_download.setText("DOWNLOADED");
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
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        long date = calendar.getTimeInMillis();
                        ((EditText) v).setText(Tools.getFormattedDateShort(date));
                    }
                },
                cur_calender.get(Calendar.YEAR),
                cur_calender.get(Calendar.MONTH),
                cur_calender.get(Calendar.DAY_OF_MONTH)
        );
        //set dark light
        datePicker.setThemeDark(false);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.setMinDate(cur_calender);
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
        mp = MediaPlayer.create(AudioUploadFormActivity.this, mPath);
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
}

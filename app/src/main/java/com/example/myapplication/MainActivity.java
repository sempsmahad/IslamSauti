package com.example.myapplication;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.dinuscxj.progressbar.CircleProgressBar;

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

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,ProgressRequestBody.UploadCallbacks {
    TextView a_name;
    EditText etName;
    EditText etDate;
    EditText etTopic;
    Uri path = null;
    DatePickerDialog datePickerDialog;
    CircleProgressBar uploadProgressBar;
    Button btnCancel;
    Button btnUpload;
    Button btnPlay;
    Button btnStop;
    Call<Audio> call;
    MediaPlayer mp;
    Boolean mPlaying = false;
    int mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ButterKnife.bind(this);
        uploadProgressBar = findViewById(R.id.progressBar);
        uploadProgressBar.setProgressFormatter(new MyProgressFromatter());
        if (savedInstanceState != null) {
           path = Uri.parse(savedInstanceState.getString("PATH"));
           onGetPath(path);
           mPlaying = savedInstanceState.getBoolean("PLAY_STATE",false);
           mProgress = savedInstanceState.getInt("UPLOAD_PROGRESS",0);
           uploadProgressBar.setProgress(mProgress);
        }
        a_name = findViewById(R.id.tv_audio_name);
        etName = findViewById(R.id.et_name);
        etDate = findViewById(R.id.et_date);
        etTopic = findViewById(R.id.et_topic);
        btnCancel = findViewById(R.id.btn_cancel);
        btnUpload = findViewById(R.id.btn_upload);
        btnStop = findViewById(R.id.btn_stop);
        btnPlay = findViewById(R.id.btn_play);

        Calendar calendar = new GregorianCalendar(Locale.getDefault());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        etDate.setText(day+"/"+month+"/"+year);
        datePickerDialog = new DatePickerDialog(this, MainActivity.this, year, month, day);
        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMaxDate(System.currentTimeMillis());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            finish();
            startActivity(intent);
            return;
        }
        if (mPlaying){
            mp.start();
            btnStop.setEnabled(true);
            btnPlay.setEnabled(true);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null ) {
                    path = data.getData();
                    onGetPath(path);
        }
    }
    private void onGetPath(Uri mPath) {
        mp = MediaPlayer.create(MainActivity.this,mPath);
        String fileName = getFileName(mPath);
        a_name.setText(fileName);
        btnStop.setEnabled(true);
        btnPlay.setEnabled(true);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (path != null && mp != null){
        savedInstanceState.putString("PATH",path.toString());
        savedInstanceState.putBoolean("PLAY_STATE",mp.isPlaying());
        savedInstanceState.putInt("UPLOAD_PROGRESS",mp.getCurrentPosition());
        }
    }

    public void chuz(View view) {
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload, 1);
    }
    public void upload(View view) {
        btnCancel.setVisibility(View.VISIBLE);
        btnUpload.setVisibility(View.INVISIBLE);

        String shekName = etName.getText().toString().trim();
        String topic = etTopic.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        File file = new File(FileUtil.getPath(path,this));
        RequestBody descTopic = RequestBody.create(MediaType.parse("text/plain"), topic);
        RequestBody descDate = RequestBody.create(MediaType.parse("text/plain"), date);
        RequestBody descName = RequestBody.create(MediaType.parse("text/plain"), shekName);

        ProgressRequestBody fileBody = new ProgressRequestBody(file, "audio",MainActivity.this);
        MultipartBody.Part filePart =MultipartBody.Part.createFormData("audio", file.getName(), fileBody);

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        call = apiInterface.uploadAudio(descName,descDate,descTopic,filePart);

        call.enqueue(new Callback<Audio>() {
            @Override
            public void onResponse(Call<Audio> call, Response<Audio> response) {
                if (!response.body().error) {
                    Toast.makeText(MainActivity.this, "File Uploaded Successfully...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "uploading Failed", Toast.LENGTH_LONG).show();
                }
               }
            @Override
            public void onFailure(Call<Audio> call, Throwable t) {
                if (call.isCanceled()) {
                    Toast.makeText(MainActivity.this, "request was cancelled", Toast.LENGTH_SHORT).show();
                    btnCancel.setVisibility(View.INVISIBLE);
                    btnUpload.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    btnCancel.setVisibility(View.INVISIBLE);
                    btnUpload.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        etDate.setText(dayOfMonth +"/"+month+"/"+year);
    }
    public void pickDate(View view) {
        datePickerDialog.show();
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

    @Override
    public void onProgressUpdate(int percentage) {
        uploadProgressBar.setProgress(percentage);
    }
    @Override
    public void onError() {
    }
    @Override
    public void onFinish() {
        uploadProgressBar.setProgress(100);
    }
    public void cancelUpload(View view) {
        call.cancel();
    }
    public void play(View view) {
        if(mp.isPlaying()){
            btnPlay.setText("play");
            mp.pause();
            btnStop.setEnabled(true);
        }else{
            btnPlay.setText("pause");
            mp.start();
            btnStop.setEnabled(true);
        }
        mp.setOnCompletionListener(mp1 -> mp1.release());
    }

    public void stop(View view) {
        mp.stop();
        btnStop.setEnabled(false);
    }
}

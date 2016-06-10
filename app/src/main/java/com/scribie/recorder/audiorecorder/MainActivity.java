package com.scribie.recorder.audiorecorder;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.Toast;
import android.os.Environment;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static String mFileName = null;
    private Menu option_menu;
    private MenuItem menuItem;
    private MediaRecorder mMediaRecorder = null;
    private MediaPlayer mMediaPlayer = null;
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    boolean b_play_enable = true;
    boolean b_record_enable = true;
    boolean b_resume = false;
    private int current_pos = 0;



    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMediaRecorder = new MediaRecorder();
        mMediaPlayer = new MediaPlayer();


        final ImageButton img_record_btn = (ImageButton) findViewById(R.id.imageButton);
        final ImageButton img_play_btn = (ImageButton) findViewById(R.id.play_pause_imageButton);
//     final Button img_record_btn = (Button) findViewById(R.id.recordstop);
//        final Button img_play_btn = (Button) findViewById(R.id.playpause);


        img_record_btn.setEnabled(true);
        img_play_btn.setEnabled(false);

        img_record_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("brecord ename:" + b_record_enable);
                img_play_btn.setEnabled(false);
               // img_play_btn.setImageDrawable(R.drawable.ic

                // Toast.makeText(getApplicationContext(), " Recording Started " + getFilename(), Toast.LENGTH_SHORT).show();
                if (b_record_enable) {

                    if(mMediaPlayer.isPlaying()){
                        mMediaPlayer.stop();
                        mMediaPlayer.release();

                    }
                    System.out.println("Hi 123");
                    mMediaRecorder = new MediaRecorder();
                    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mFileName = getFilename();
                    mMediaRecorder.setOutputFile(mFileName);
                    mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    try {
                        System.out.println("Hi 123 prepare");
                        mMediaRecorder.prepare();
                        mMediaRecorder.start();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    b_record_enable = false;
                   // img_record_btn.setText("Stop");
                    System.out.println("Hi 123 false");
                    Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_LONG).show();
                } else {
                    System.out.println("Hi 123 else");
                    mMediaRecorder.stop();
                    // mMediaRecorder.release();
                    //mMediaRecorder = null;
                    b_record_enable=true;
                    Toast.makeText(getApplicationContext(), "Recording Ended", Toast.LENGTH_LONG).show();
                    img_play_btn.setEnabled(true);
                    img_record_btn.setEnabled(true);
                    //img_record_btn.setText("Record");
                    b_play_enable = true;
                    current_pos=0;

                }
            }

        });


        img_play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws IllegalArgumentException, SecurityException, IllegalStateException {

                if (b_play_enable) {
                    img_record_btn.setEnabled(false);
                    try {
                        mMediaPlayer = new MediaPlayer();
                        mMediaPlayer.setDataSource(mFileName);
                        mMediaPlayer.prepare();
                        mMediaPlayer.seekTo(current_pos);
                        mMediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    b_play_enable=false;
                    b_record_enable= true;
                    img_record_btn.setEnabled(true);
                    //img_play_btn.setText("Pause");
                    //checkplaying(mMediaPlayer);




                    Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
                } else {
                    current_pos = mMediaPlayer.getCurrentPosition();
                    mMediaPlayer.pause();
                    //mMediaPlayer.release();
                    //mMediaPlayer = null;
                    b_play_enable=true;
                    img_record_btn.setEnabled(true);
                    b_record_enable = true;
                   // img_play_btn.setText("Play");

                }
                if(!mMediaPlayer.isPlaying())
                    img_record_btn.setEnabled(true);

            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.all_recorded_files:
                return true;
            case R.id.uploaded_files:
                getUploadedFiles();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getUploadedFiles(){
        AsyncHttpClient client = new AsyncHttpClient();

        client.addHeader(
                "Authorization",
                "Basic " + Base64.encodeToString(
                        ("4df3c042f94717a3906e47dfe2e784e5f3ace901" + ":" + "").getBytes(), Base64.NO_WRAP)
        );

        client.get("https://api.scribie.com/v1/files", new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                // JSON Object
                JSONArray arr = null;
                ArrayList<String> arrlst = new ArrayList<String>();
                try {
                    arr = new JSONArray(response);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        System.out.println("Object" + obj.getString("id"));
                        arrlst.add((String) (obj.getString("id")));

                    }
                    Utility utils = Utility.getInstance();
                    utils.setArrListHistory(arrlst);
                    navigatetoHistoryActivity();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // Hide Progress Dialog
                //prgDialog.hide();
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found" + content, Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    System.out.println(statusCode);
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".3gp");
    }

    public void navigatetoHistoryActivity() {
        Intent historyIntent = new Intent(getApplicationContext(), FileHistoryActivity.class);
        historyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(historyIntent);
    }
/*

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.scribie.recorder.audiorecorder/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.scribie.recorder.audiorecorder/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }*/

    @Override
    public void onDestroy(){
        super.onDestroy();
        mMediaPlayer.release();
        mMediaPlayer.release();

    }
}

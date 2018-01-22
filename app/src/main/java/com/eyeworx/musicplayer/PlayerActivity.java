package com.eyeworx.musicplayer;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class PlayerActivity extends AppCompatActivity{

    //Intialize variables
    Button playBtn;
    Button forwardBtn;
    Button backwardBtn;
    SeekBar playBar;
    SeekBar volumeBar;
    TextView elapsedTime;
    TextView remainingTime;
    TextView artistText;
    ImageView albumCover;
    ImageView lowVolume;
    ImageView highVolume;
    MediaPlayer media;
    // for a 5 second default seek
    private int seekForwardTime = 5 * 1000;
    private int seekBackwardTime = 5 * 1000;
    private int totalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        playBtn = findViewById(R.id.playButton);
        forwardBtn = findViewById(R.id.forwardBtn);
        backwardBtn = findViewById(R.id.rewindBtn);
        albumCover = findViewById(R.id.albumCover);

        //Media source
        media = MediaPlayer.create(this, R.raw.jingle_bells);
        media.seekTo(0);
        totalTime = media.getDuration();

        //Time stamps
        elapsedTime = findViewById(R.id.startTime);
        remainingTime = findViewById(R.id.endTime);

        //Music Bar
        playBar = findViewById(R.id.musicBar);
        playBar.setMax(totalTime);
        playBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean userSeekPoint) {
                if(userSeekPoint) {
                    media.seekTo(progress);
                    playBar.setProgress(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Volume icons
        lowVolume = findViewById(R.id.lowVolume);
        highVolume = findViewById(R.id.highVolume);

        //Color for icons
        int color = Color.parseColor("#797777");
        lowVolume.setColorFilter(color);
        highVolume.setColorFilter(color);

        //Artist name scroll
        artistText = findViewById(R.id.artist);
        artistText.setSelected(true);
        artistText.setSingleLine(true);


        //Volume Bar
        volumeBar = findViewById(R.id.volumeBar);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            float volumeNum = progress/100f;
            media.setVolume(volumeNum,volumeNum);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (media != null) {
                    try {
                        Message message = new Message();
                        message.what = media.getCurrentPosition();
                        handler.sendMessage(message);

                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


    }

    @Override
    protected void onResume() {
        super.onResume();

        //Handle play button click
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!media.isPlaying()) {
                    media.start();
                    playBtn.setBackgroundResource(R.drawable.stop);

                } else {
                    media.pause();
                    playBtn.setBackgroundResource(R.drawable.play);
                }
            }
        });

        //Handle forward button click
        forwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (media != null) {
                    int currentPosition = media.getCurrentPosition();
                    if (currentPosition + seekForwardTime <= media.getDuration()) {
                        media.seekTo(currentPosition + seekForwardTime);
                    } else {
                        media.seekTo(media.getDuration());
                    }
                }
            }
        });

        //Handle rewind button click
        backwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (media != null) {
                    int currentPosition = media.getCurrentPosition();
                    if (currentPosition - seekBackwardTime >= 0) {
                        media.seekTo(currentPosition - seekBackwardTime);
                    } else {
                        media.seekTo(0);
                    }
                }
            }
        });

    }
    private  Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int currentPos = msg.what;
            //update current position of seek bar
            playBar.setProgress(currentPos);

            //update time labels
            String startTime = createTimeLabel(currentPos);
            elapsedTime.setText(startTime);
            String endingTime = createTimeLabel(totalTime - currentPos);
            remainingTime.setText(endingTime);
        }
    };

    public String createTimeLabel(int time){
        String timeLabel = "";
        int min = time/1000/60;
        int sec = time/1000%60;
        timeLabel = min +":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;
        return timeLabel;
    }
}


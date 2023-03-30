package com.example.ekmusic_palyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity {

    Button btnplay, btnnext, btnprev,btnfp, btnff, btnrep, btnstop;
    TextView txtsname, txtsstart, txtsstop, txtnext;
    SeekBar seekmusic;

    int replay = 0;


    String sname;
    public static final String Extra_name = "song_name";
    static MediaPlayer mediaPlayer;
    ImageView imageView;
    int position;

    final LinkedList<File> mySongs = findsong(Environment.getExternalStorageDirectory());
    Thread updateseekbar;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().setTitle("now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#444168"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        btnprev = findViewById(R.id.prevbtn);
        btnnext = findViewById(R.id.nextbtn);
        btnplay = findViewById(R.id.playbtn);
        btnff = findViewById(R.id.btnff);
        btnfp = findViewById(R.id.btnfr);
        btnrep = findViewById(R.id.btnrepeat);
        btnstop = findViewById(R.id.btnstop);
        txtsname = findViewById(R.id.txtsn);
        txtnext = findViewById(R.id.txtns);
        txtsstart = findViewById(R.id.txtsstart);
        txtsstop = findViewById(R.id.txtsstop);
        seekmusic = findViewById(R.id.seekbar);
        imageView = findViewById(R.id.imageview);


        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        // mysongs = (ArrayList) bundle.getParcelable("songs");
        // String songname = i.getStringExtra("songname");
        position = bundle.getInt("pos", 0);
        txtsname.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        sname = mySongs.get(position).getName();

        txtsname.setText(sname);
        txtnext.setText(mySongs.get(position+1).getName());

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();

        updateseekbar = new Thread()
        {
            @Override
            public void run() {
                int  totalDuration = mediaPlayer.getDuration();
                int currentposition  = 0;

                while(currentposition<totalDuration)
                {
                    try{
                        sleep(500);
                        currentposition =mediaPlayer.getCurrentPosition();
                        seekmusic.setProgress(currentposition);
                    }
                    catch (InterruptedException | IllegalStateException e)
                    {
                        e.printStackTrace();
                    }
                }

            }
        };


        seekmusic.setMax(mediaPlayer.getDuration());
        updateseekbar.start();
        seekmusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.purple_200), PorterDuff.Mode.MULTIPLY);
        seekmusic.getThumb().setColorFilter(getResources().getColor(R.color.purple_200), PorterDuff.Mode.SRC_IN);

        seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        String endtime = createTime(mediaPlayer.getDuration());
        txtsstop.setText(endtime);

        final Handler handler =new Handler();
        final  int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                txtsstart.setText(currentTime);
                handler.postDelayed(this,delay);
            }
        },delay);

        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying())
                {
                    btnplay.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }
                else {
                    btnplay.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        });

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position =((position+1)%mySongs.size());
                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                sname = mySongs.get(position).getName();
                txtsname.setText(sname);
                txtnext.setText(mySongs.get(position+1).getName());
                mediaPlayer.start();
                btnplay.setBackgroundResource(R.drawable.ic_pause);
                IAnimation(imageView);

            }
        });

        btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position =((position-1)<0)?(mySongs.size()-1):(position-1);
                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                sname = mySongs.get(position).getName();
                txtsname.setText(sname);
                txtnext.setText(mySongs.get(position+1).getName());
                mediaPlayer.start();
                btnplay.setBackgroundResource(R.drawable.ic_pause);
                IAnimation(imageView);
            }
        });



        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                btnnext.performClick();
            }
        });

        btnff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });

        btnfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });

        btnrep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying())
                {
                    if(replay==0)
                    {
                        btnrep.setBackgroundResource(R.drawable.repeat_one);
                        replay = 1;

                            mediaPlayer.setLooping(true);

                    } else if (replay == 1) {
                        btnrep.setBackgroundResource(R.drawable.repeat_all);
                        replay =0;
                        mediaPlayer.setLooping(false);
                    }

                }
                else {
                    btnplay.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        });

      btnstop.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              mediaPlayer.stop();
              mediaPlayer.release();
              position =0;
              Uri u = Uri.parse(mySongs.get(position).toString());
              mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
              sname = mySongs.get(position).getName();
              txtsname.setText(sname);
              txtnext.setText(mySongs.get(position+1).getName());

          }
      });

    }

public void IAnimation(View view)
{
    ObjectAnimator animator = ObjectAnimator.ofFloat(imageView,"rotation",0f,360f);
    animator.setDuration(5000);
    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playTogether(animator);
    animatorSet.start();

}
public String createTime(int duration)
{
    String time = "";
    int min  = duration/1000/60;
    int sec  = duration/1000%60;

    time+=min+":";
    if(sec<10)
    {
        time+="0";
    }
    time+=sec;

    return time;
}

    public LinkedList<File> findsong(File file)
    {
        LinkedList<File> linkedList = new LinkedList<>();
        File[] files = file.listFiles();
        for(File singleFile: files)
        {
            if(singleFile.isDirectory() && !singleFile.isHidden())
            {
                linkedList.addAll(findsong(singleFile));
            }
            else {
                if(singleFile.getName().endsWith(".mp3")|| singleFile.getName().endsWith(".wav"))
                {
                    linkedList.add(singleFile);
                }
            }
        }
        return linkedList;
    }

}
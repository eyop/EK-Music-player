package com.example.ekmusic_palyer;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.jetbrains.annotations.Contract;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;


public class MainActivity extends AppCompatActivity {

    ListView listView;
    String[] items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#444168"));
        actionBar.setBackgroundDrawable(colorDrawable);

        listView = findViewById(R.id.listViewSong);
        runtimePermission();

    }


    public void runtimePermission()
    {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        displaySongs();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
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


    void displaySongs()
    {
//        final ArrayList<File> mySongs =findsong(Environment.getExternalStorageDirectory());
        final LinkedList<File> mySongs = findsong(Environment.getExternalStorageDirectory());
        items =new String[mySongs.size()];
        for(int i = 0; i< mySongs.size();i++)
        {
            items[i] =mySongs.get(i).getName().toString().replace(".mp3","").replace(".wav","");

        }

        customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);


    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            String songname = (String) listView.getItemAtPosition(position);

             startActivity(new Intent(getApplicationContext(), PlayerActivity.class)
                    .putExtra("songs", mySongs)
//                    .putExtra("songname", songname)
                    .putExtra("pos", position)
            );




        }
    });

    }



    class customAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myview = getLayoutInflater().inflate(R.layout.list_item,null);
            TextView textsong = myview.findViewById(R.id.txtsongname);
            textsong.setSelected(true);
            textsong.setText(items[i]);

            return myview;
        }
    }
}
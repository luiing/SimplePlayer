package com.video.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.uis.lib.simpleplayer.Vlog;
import com.uis.lib.simpleplayer.player.PlayerUtils;
import com.uis.lib.simpleplayer.PlayerLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    String[] mIcon = {"http://pic.qiantucdn.com/58pic/19/59/29/86g58PICRd3_1024.jpg",
            "http://pic.90sjimg.com/back_pic/qk/back_origin_pic/00/03/14/c0391a6c1efab3fe00911b04e8cedca4.jpg"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PlayerLayout playerLayout = id(R.id.player_id);
        RecyclerView recyclerView = id(R.id.recycler_view);
        Button btOpen = id(R.id.bt_open);
        btOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(),MainActivity.class));
            }
        });
        boolean isList = true;
        if(isList) {
            playerLayout.setVisibility(View.GONE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            VideoAdapter adapter = new VideoAdapter();
            int size = DemoApp.mUrl.length;
            String[] data = new String[1*size+1];
            data[0] = "";
            for(int i = 1,t = 1*size+1;i<t;i++){
                data[i] = DemoApp.mUrl[(i-1)%size];
            }
            adapter.setData(data);
            recyclerView.setAdapter(adapter);
        }else{
            playerLayout.start(DemoApp.mUrl[2],"");
        }
        try {
            File path = new File(getCacheDir(),"intro.mp4");
            DemoApp.mUrl[3] = path.getPath();
            Vlog.e("path",path+","+path.exists()+","+path.length()/1024/1024);
            if(!path.exists()) {
                InputStream is = getResources().openRawResource(R.raw.intro);
                OutputStream os = new FileOutputStream(path);
                byte[] buff = new byte[1024];
                while (is.read(buff) > 0) {
                    os.write(buff);
                }
                os.flush();
                os.close();
                is.close();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        PlayerUtils.release();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ScreenConvertor.isPlaying()) {
            PlayerUtils.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScreenConvertor.setPlaying();
        PlayerUtils.pause();
    }

    @Override
    public void onBackPressed() {
        if(!ScreenConvertor.onBackPressed(this)){
            super.onBackPressed();
        }
    }

    public <T extends View> T id(int id){
        return (T)findViewById(id);
    }
}

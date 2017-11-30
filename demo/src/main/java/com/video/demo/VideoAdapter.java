package com.video.demo;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.uis.lib.simpleplayer.Vlog;
import com.uis.lib.simpleplayer.player.PlayerUtils;
import com.uis.lib.simpleplayer.PlayerLayout;

/**
 * @author uis on 2017/11/17.
 */

public class VideoAdapter extends RecyclerView.Adapter {

    private String[] data;

    public VideoAdapter() {
        super();
    }

    public void setData(String[] data){
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        RecyclerView.ViewHolder vh;
        if(viewType==0) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_video, parent, false);
            vh = new VideoVH(v);
        }else{
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_viewpager, parent, false);
            vh = new VpVH(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof VideoVH){
            ((VideoVH)holder).onViewBind(data[position]);
        }else if(holder instanceof VpVH){
            ((VpVH)holder).onViewBind();
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if(holder instanceof VideoVH){
            ((VideoVH)holder).onViewRecycled();
        }
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemViewType(int position) {
        String pos = data[position];
        return TextUtils.isEmpty(pos)?1:0;
    }

    @Override
    public int getItemCount() {
        return data==null?0:data.length;
    }

    static class VpVH extends RecyclerView.ViewHolder{
        ViewPager viewPager;

        public VpVH(View itemView) {
            super(itemView);
            final Context mc = itemView.getContext();
            this.viewPager = id(itemView,R.id.viewpager);
        }

        public void onViewBind(){
            final VideoPagerAdapter adapter = new VideoPagerAdapter();
            viewPager.setOffscreenPageLimit(7);
            viewPager.addOnPageChangeListener(adapter);
            viewPager.setAdapter(adapter);
        }
    }

    static class VideoVH extends RecyclerView.ViewHolder{
        PlayerLayout player;

        public VideoVH(View itemView) {
            super(itemView);
            player = id(itemView,R.id.player);
        }

        public void onViewRecycled(){
            Log.e("xx","onViewRecycled...");
        }

        public void onViewBind(String url){
            player.start(url,DemoApp.URL);
            Log.e("xx","onViewBind...");
        }
    }

    public static <T extends View> T id(View view,int id){
        return (T)view.findViewById(id);
    }
}

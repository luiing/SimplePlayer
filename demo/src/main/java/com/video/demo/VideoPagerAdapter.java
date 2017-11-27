package com.video.demo;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uis.lib.simpleplayer.PlayerLayout;

import java.util.LinkedList;

/**
 * @author uis on 2017/11/22.
 */

public class VideoPagerAdapter extends PagerAdapter {
    static String[] mUrl = {
            "http://jzvd.nathen.cn/6ea7357bc3fa4658b29b7933ba575008/fbbba953374248eb913cb1408dc61d85-5287d2089db37e62345123a1be272f8b.mp4",
            "http://img.iblimg.com/goods-135/feng.mp4",
    };

    private LinkedList<View> mList = new LinkedList<>();

    public VideoPagerAdapter() {
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View v;
        if(mList.size()>0){
            v = mList.pop();
        }else{
            v = LayoutInflater.from(container.getContext()).inflate(R.layout.vh_video,container,false);
        }
        final PlayerLayout player = VideoAdapter.id(v,R.id.player);
        player.start(mUrl[position]);
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View v = (View)object;
        container.removeView(v);
        mList.push(v);
    }

    @Override
    public int getCount() {
        return mUrl.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}

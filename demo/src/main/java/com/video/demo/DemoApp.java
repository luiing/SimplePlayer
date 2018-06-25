package com.video.demo;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.uis.lib.simpleplayer.Vlog;

//import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by lhb on 2017/7/25.
 */

public class DemoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Vlog.enableDebug();
        Fresco.initialize(this);
    }

    public final static String URL = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1511769786707&di=a903cd0bc48d1ba8c5da5e530708ab61&imgtype=0&src=http%3A%2F%2Fa.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F58ee3d6d55fbb2fbbb8058d8464a20a44723dc55.jpg";

    public static String[] mUrl = {
            "https://lmbsy.qq.com/flv/140/144/n0025l026va.mp4?sdtfrom=v1103&guid=8bc05ce6e4511a28a4d1b44ed110ff6f&vkey=C9527C07C187684A3D8CE17094A6B9DBCBCDD3AF71E66197C0345A43A1E5999E233543F2717B0A6768E6B95FE7CB8C8C043E518C4FC405D7BBA3D3FBF2BC25BC7AAA94A4622BA6A716DD4FBBB72CEA6B28678DE718AAEA5EF8FF0C949BBC53B0272AC396B53981013AE721DF259D08F8989D5B7B4F612B7A&platform=2",
            "http://data.vod.itc.cn/?rb=1&prot=1&key=jbZhEJhlqlUN-Wj_HEI8BjaVqKNFvDrn&prod=flash&pt=1&new=/51/116/UdKGIuSjQIO8dynrybyS1E.mp4",
            "https://vdse.bdstatic.com//6a90cf9678713ea594a428a8c98d2b5f.mp4?authorization=bce-auth-v1%2Ffb297a5cc0fb434c971b8fa103e8dd7b%2F2017-05-11T09%3A02%3A31Z%2F-1%2F%2F385d02679b636d6e5dd150193a497b787d517afef0f6c0903350a85056408e59",
            "http://data.vod.itc.cn/?rb=1&prot=1&key=jbZhEJhlqlUN-Wj_HEI8BjaVqKNFvDrn&prod=flash&pt=1&new=/20/111/bOT648IiIIVJPS33wZpYWH.mp4",
            "http://jzvd.nathen.cn/6ea7357bc3fa4658b29b7933ba575008/fbbba953374248eb913cb1408dc61d85-5287d2089db37e62345123a1be272f8b.mp4",
    };
}

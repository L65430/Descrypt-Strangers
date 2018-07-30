package com.example.administrator.finalprocject.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ListView;

import com.baidu.android.common.net.ProxyHttpClient;
import com.example.administrator.finalprocject.Adapter.GameInfoAdapter;
import com.example.administrator.finalprocject.Codecfactory.Lcoderfactory;
import com.example.administrator.finalprocject.Handler.ClientMessageHandler;
import com.example.administrator.finalprocject.Info.ClientManger;
import com.example.administrator.finalprocject.Info.Gameinfo;
import com.example.administrator.finalprocject.R;
import com.example.administrator.finalprocject.StaticValues.StaticValues;
import com.example.administrator.finalprocject.View.BaseListView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.bouncycastle.jcajce.provider.symmetric.Camellia;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.Inflater;

public class GameCenter extends Activity {
    View view;
    List<Gameinfo> list = new ArrayList<Gameinfo>();
    ListView gamelistview;
    GameInfoAdapter gameInfoAdapter;
    String url;
    String bestcode;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_center);
        //先访问线程，让他先去访问这个网站，然后得到回来的值
        seturl("http://115.28.34.148/2048-master/savebest.php");
        new GetThread().start();
        //在开始线程之后并不能马上得线到程
        System.out.println("bestcode为："+bestcode);
        initlist();
        gamelistview = (ListView) findViewById(R.id.gameinfo);
        gameInfoAdapter = new GameInfoAdapter(this, list);

        //然后在这里面执行需要时间，所以setadapter没有及时进行
        gamelistview.setAdapter(gameInfoAdapter);

        gamelistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Gameinfo gameinfo = (Gameinfo) gameInfoAdapter.getItem(position);
                if (gameinfo.getGamename().equals("2048")) {
//                    Uri uri = Uri.parse("http://115.28.34.148/2048-master/2048-master/Bestcode.php");


//                    Uri uri1=Uri.parse("http://115.28.34.148/Testphpandandroid.php") ;
//
//
//                    Intent intent = new Intent(Intent. ACTION_VIEW,uri1);

                    Uri uri = Uri.parse("http://115.28.34.148/2048-master/2048-master/Bestcode.php");
                    Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(intent);//这里得到了结果，存入数据库中
                    bestcode="2680";
                    gameinfo.setbestcode("您的最高分为："+bestcode);
                    gameInfoAdapter.notifyDataSetChanged();
                }
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void initlist() {
        Gameinfo gameinfo1 = new Gameinfo("您的最高分为：", "2048", R.drawable.twozerofoureight);
        Gameinfo gameinfo2 = new Gameinfo("您的最高分为：", "flappy bird", R.drawable.flappy_bird);
        list.add(gameinfo1);
        list.add(gameinfo2);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "GameCenter Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.example.administrator.finalprocject.Activity/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
//    }

//    @Override
//    public void onStop() {
//        super.onStop();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "GameCenter Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://com.example.administrator.finalprocject.Activity/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
//    }

    private class GetThread extends Thread {
        public void run() {
                bestcode = null;
                HttpPost httpRequest = new HttpPost(url);//只是发送一次请求
                List params = new ArrayList();
                params.add(new BasicNameValuePair("userEmail", ClientManger.clientEmail));//发送userEmail给php
                try {
                    httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        bestcode = EntityUtils.toString(httpResponse.getEntity());
                        System.out.print("bestcode:" + bestcode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    public void seturl(String url) {
        this.url = url;
    }
}

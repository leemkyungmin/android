package com.example.nemojsoup;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.net.HttpURLConnection;
import java.net.URL;


public class Detail extends AppCompatActivity {

    // 기숙사 221.147.172.121
    // 303  192.168.28.5

    TextView group_name, address, phnumber, email, sns, homepage, staff_time, usingtime, typeOfprice;
    ImageView img_1, img_2, img_3;
    Handler handler = new Handler();
    public String phnum;
    String localhost = "http://192.168.28.5:8080/db/";
    Button phcall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        DetailThread thread = new DetailThread();
        thread.start();

    }

    class DetailThread extends Thread {
        Intent intent = getIntent();

        public void run() {
            String http = localhost + intent.getStringExtra("DetailHttp");
            Document doc = null;
            try {
                group_name = (TextView) findViewById(R.id.group_name);
                address = (TextView) findViewById(R.id.address);

                email = (TextView) findViewById(R.id.email);
                sns = (TextView) findViewById(R.id.sns);
                homepage = (TextView) findViewById(R.id.homepage);
                usingtime = (TextView) findViewById(R.id.using_time);
                typeOfprice = (TextView) findViewById(R.id.typeOfprice);

                URL url = new URL(http);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    doc = Jsoup.connect(http).get();
                    String group = doc.select("a").get(0).html();
                    String addr = doc.select("a").get(1).html();
                    phnum = doc.select("a").get(2).html();
                    String em = doc.select("a").get(3).html();
                    String sn = doc.select("a").get(4).html();
                    String home = doc.select("a").get(5).html();
                    String staff = doc.select("a").get(6).html();
                    String using = doc.select("a").get(7).html();



                   String re_price ="홈페이지,유선문의";


                    Elements img = doc.select("img");

                    String[] imgsrc = new String[img.size()];


                    for (int i = 0; i < img.size(); i++) {
                        imgsrc[i] = doc.select("img").get(i).getElementsByTag("img").attr("src");
                        print(i, imgsrc[i]);
                    }

                    group_name.setText(group);
                    address.setText(addr);
                    address.setTextColor(Color.BLUE);
                    email.setText(em);
                    sns.setText(sn);
                    homepage.setText(home);
                    homepage.setTextColor(Color.BLUE);
                    usingtime.setText(using);
                    typeOfprice.setText(re_price);

                    phcall = (Button) findViewById(R.id.phcall);

                    phcall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           CheckPermission();

                        }
                    });
                }


            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }


    private void print(int index, String out) {
        img_1 = (ImageView) findViewById(R.id.img_1);
        img_2 = (ImageView) findViewById(R.id.img_2);
        img_3 = (ImageView) findViewById(R.id.img_3);
        OpenHttpConnection openHttpConnection = new OpenHttpConnection();
        if (index == 0) {
            openHttpConnection.execute(img_1, localhost + out);
        } else if (index == 1) {
            openHttpConnection.execute(img_2, localhost + out);
        } else if (index == 2) {
            openHttpConnection.execute(img_3, localhost + out);
        }
    }


    public class OpenHttpConnection extends AsyncTask<Object, Void, Bitmap> {

        private ImageView bmImage;

        @Override
        protected Bitmap doInBackground(Object... params) {
            Bitmap mBitmap = null;
            bmImage = (ImageView) params[0];
            String url = (String) params[1];
            InputStream in = null;
            try {
                in = new java.net.URL(url).openStream();
                mBitmap = BitmapFactory.decodeStream(in);
                in.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return mBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bm) {
            super.onPostExecute(bm);
            bmImage.setImageBitmap(bm);
        }

    }

    public void postion(View v) {
        switch (v.getId()) {
            case R.id.address:
                Intent addressintent = new Intent(Detail.this, askWebView.class);
                addressintent.putExtra("address", address.getText());
                startActivity(addressintent);
                break;
            case R.id.homepage:
                Intent homepageintent = new Intent(Detail.this, askWebView.class);
                homepageintent.putExtra("homepage", homepage.getText());
                startActivity(homepageintent);
                break;
        }
    }


    public void CheckPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionResult = checkSelfPermission(Manifest.permission.CALL_PHONE);
            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Detail.this);
                    dialog.setTitle("권한이 필요합니다.")
                            .setMessage("이 기능을 사용하기 위해서는 단말기의 \"전화걸기\" 권한이 필요합니다. 계속 하시겠습니까?")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);
                                    }
                                }
                            }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(Detail.this, "기능을 취소했습니다", Toast.LENGTH_SHORT).show();
                        }
                    }).create().show();
                } else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);
                }
            } else {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phnum));
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phnum));
            startActivity(intent);
        }
    }

@Override
public void onRequestPermissionsResult(int requestCode,@NonNull String[]permissions,@NonNull int[]grantResults){
        if(requestCode==1000){
        if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
        Intent intent=new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phnum));
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED){
        startActivity(intent);}}
        else{
        Toast.makeText(Detail.this,"권한요청을 거부했습니다.",Toast.LENGTH_SHORT).show();}}}


}



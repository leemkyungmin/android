package com.example.nemojsoup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.apache.http.impl.client.BasicResponseHandler;
public class findpassword extends Activity {

    // 기숙사 221.147.172.121
    // 303  192.168.28.5

    EditText findid,findname;
    Button findpassword;
    Handler handler=new Handler();
    public String find_url;
    public String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findpassword);
        findid=(EditText)findViewById(R.id.findid);
        findname=(EditText)findViewById(R.id.findname);
        findpassword=(Button)findViewById(R.id.findpassword);

        findpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(findid.length() !=0 && findname.length() !=0){
                    String ad = null;
                    try {
                         ad = URLEncoder.encode(findname.getText().toString(), "utf-8");
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }



                    find_url="http://192.168.28.5:8080/db/ask/ask_login_password_check.jsp?id="+findid.getText().toString()+"&name="+ad;


                    CheckIdThread checkIdThread=new CheckIdThread();
                    checkIdThread.start();
                }else{
                    Toast.makeText(getApplicationContext(),"아이디 이름을 입력하세요 !!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    class CheckIdThread extends Thread {
        public void run() {
            Document doc = null;
            try {
                URL url = new URL(find_url);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    doc = Jsoup.parse(new URL(find_url).openStream(), "ISO-8859-9", find_url);
                    doc.outputSettings().charset().forName("utf-8");
                    Log.e("주소가 제대로 넘어갔니 ?????", find_url);
                    final Elements tagsize = doc.select("h1");
                    if (tagsize.size() != 0) {


                        for (int i = 0; i < 1; i++) {
                            password = doc.select("h1").get(i).getElementsByTag("h1").html();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder dlg=new AlertDialog.Builder(findpassword.this);
                                    dlg.setTitle(findid.getText().toString()+"님의 비밀번호 ");
                                    dlg.setMessage(password+"입니다");
                                    dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent=new Intent(findpassword.this,loginpage.class);
                                            startActivity(intent);
                                        }
                                    });
                                    dlg.show();

                                }
                            });
                        }
                    }


                } else {
                    Log.e("에러ㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓ", Integer.toString(conn.getResponseCode()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}

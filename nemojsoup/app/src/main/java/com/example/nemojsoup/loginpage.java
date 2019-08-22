package com.example.nemojsoup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.se.omapi.Session;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class loginpage extends Activity {

    // 기숙사 221.147.172.121
    // 303  192.168.28.5

    Button createuser, login, lostpwd;
    public EditText id, pw;
    CheckBox checkBox;
    Handler handler = new Handler();
    public String localhost = "http://192.168.28.5:8080/db/";
    public String login_url;
    private SharedPreferences appData;
    public SharedPreferences loginsession;
    private boolean saveLoginData;
    private boolean savesession;
    private String saveid, savepw,sessionid;
    public boolean id_check_response_code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlogin);

        createuser = (Button) findViewById(R.id.createuser);
        login = (Button) findViewById(R.id.login);
        lostpwd = (Button) findViewById(R.id.lostpassword);
        checkBox=(CheckBox)findViewById(R.id.checkBox) ;
        id = (EditText) findViewById(R.id.loginId);
        pw = (EditText) findViewById(R.id.loginpass);
        appData = getSharedPreferences("appData", MODE_PRIVATE);
         loginsession=getSharedPreferences("loginsession", MODE_PRIVATE);
        load();
        if (saveLoginData) {
            id.setText(saveid);
            pw.setText(savepw);
            checkBox.setChecked(saveLoginData);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            login_url = localhost + "ask/ask_login_check.jsp?id=" + (id.getText().toString()) + "&pwd=" + (pw.getText().toString());

                loginThread thread = new loginThread();
                thread.start();






            }
        });
        createuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(loginpage.this,CreateUser.class);
                startActivity(intent);
            }
        });
        lostpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(loginpage.this,findpassword.class);
                startActivity(intent);
            }
        });
    }

    class loginThread extends Thread {

        public void run() {
            Document doc = null;
            try {
                URL url = new URL(login_url);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    doc = Jsoup.connect(login_url).get();
                    Log.e("주소가 제대로 넘어갔니 ?????", login_url);
                    final Elements tagsize = doc.select("h1");
                    if (tagsize.size() != 0) {


                        for (int i = 0; i < 1; i++) {
                            String requestid = doc.select("h1").get(i).getElementsByTag("h1").html();

                            if (requestid.equals(id.getText().toString())) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast message = Toast.makeText(getApplicationContext(), " 로그인 성공 . ", Toast.LENGTH_SHORT);
                                        message.setGravity(Gravity.CENTER, 0, 0);
                                        message.show();
                                        save();
                                        savesession();
                                        Intent intent=new Intent(loginpage.this,mainpage.class);
                                        startActivity(intent);
                            }
                        });


                            } else {
                                Log.e("reqeustid",requestid);
                                Log.e("id 값 ",id.getText().toString());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast message = Toast.makeText(getApplicationContext(), " 로그인 실패 아이디 비밀번호를 확인해주세요 . ", Toast.LENGTH_SHORT);
                                        message.setGravity(Gravity.CENTER, 0, 0);
                                        message.show();

                                    }
                                });

                            }
                        }
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast message = Toast.makeText(getApplicationContext(), " 등록된 아이디가없습니다 . ", Toast.LENGTH_SHORT);
                                message.setGravity(Gravity.CENTER, 0, 0);
                                message.show();
                            }
                        });
                    }
                } else {
                    Log.e("에러ㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓ", Integer.toString(conn.getResponseCode()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private void save() {//저장
        SharedPreferences.Editor editor = appData.edit();

        editor.putBoolean("SAVE_LOGIN_DATA", checkBox.isChecked());
        editor.putString("ID", id.getText().toString().trim());
        editor.putString("PWD", pw.getText().toString().trim());

        editor.apply();
    }

    private void load() {//불러오기

        saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA", false);
        saveid = appData.getString("ID", "");
        savepw = appData.getString("PWD", "");
    }
    private void savesession() {//저장
        SharedPreferences.Editor session = loginsession.edit();

        session.putBoolean("SAVE_LOGIN_DATA", true);
        session.putString("ID", id.getText().toString().trim());
        session.putString("PWD", pw.getText().toString().trim());

        session.apply();
    }

    public void loadsession() {//불러오기

        savesession = loginsession.getBoolean("session", false);
        sessionid = loginsession.getString("ID", "");
    }
}




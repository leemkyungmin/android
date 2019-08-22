package com.example.nemojsoup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class CreateUser extends Activity {
    // 기숙사 221.147.172.121
    // 303  192.168.28.5
    EditText createid, createpass, checkpass, createname, firstnumber, secondnumber, thridnumber;
    Button checkid, insertData, clear;
    TextView check_your_password;
    public String checkid_url = "http://192.168.28.5:8080/db/ask/ask_login_checkid.jsp";
    public String insertDB_url;
    Handler handler = new Handler();
    public String requestid;
    public boolean id_check;
    public boolean password_check;
    public String insert_response_code;
    public int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createuser);
        checkid = (Button) findViewById(R.id.checkid);
        insertData = (Button) findViewById(R.id.insertData);
        clear = (Button) findViewById(R.id.clear);

        createid = (EditText) findViewById(R.id.createid);
        createpass = (EditText) findViewById(R.id.createpass);
        checkpass = (EditText) findViewById(R.id.checkpass);
        createname = (EditText) findViewById(R.id.db_insert_name);
        firstnumber = (EditText) findViewById(R.id.firstnumber);
        secondnumber = (EditText) findViewById(R.id.secondnumber);
        thridnumber = (EditText) findViewById(R.id.thridnumber);

        check_your_password = (TextView) findViewById(R.id.check_your_password);
        checkid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkid_url = "http://221.147.172.121:8080/db/ask/ask_login_checkid.jsp?id=" + createid.getText().toString();
                CheckIdThread checkIdThread = new CheckIdThread();
                checkIdThread.start();


            }
        });
        checkpass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ((createpass.getText().toString()).equals(checkpass.getText().toString())) {
                    check_your_password.setText(" 사용가능 ");
                    check_your_password.setTextColor(Color.GREEN);
                    password_check = true;
                } else if (checkpass.length() == 0) {
                    check_your_password.setText("");
                    password_check = false;
                } else {
                    check_your_password.setText(" 비밀번호 확인 ");
                    check_your_password.setTextColor(Color.RED);
                    password_check = false;
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createid.setText("");
                createpass.setText("");
                checkpass.setText("");
                createname.setText("");
                firstnumber.setText("");
                secondnumber.setText("");
                thridnumber.setText("");

            }
        });

        insertData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = createid.getText().toString();
                String password = createpass.getText().toString();

                String name = createname.getText().toString();
                String phnumber = firstnumber.getText().toString() + "-" + secondnumber.getText().toString() + "-" + thridnumber.getText().toString();
                if (id.length() > 4 && password.length() > 7 && name.length() > 2 && phnumber.length() > 12) {
                    count++;
                    if (!id_check || !password_check) {
                        if (!id_check) {
                            Toast.makeText(getApplicationContext(), "아이디 중복 확인하세요 ", Toast.LENGTH_LONG).show();
                        } else if (!password_check) {
                            Toast.makeText(getApplicationContext(), "비밀번호 확인 ", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "아이디 중복,비밀번호 확인", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        String ad = null;
                        try {
                            ad = URLEncoder.encode(name, "utf-8");
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                        insertDB_url = "http://192.168.28.5:8080/db/ask/ask_login_insertDB.jsp?id=" + id + "&password=" + password + "&name=" + ad + "&phnumber=" + phnumber;
                        insertDB insertDB = new insertDB();
                        insertDB.start();

                    }
                    Log.e("count수 ", Integer.toString(count));
                } else {
                    Log.e("error", "error");
                }

            }
        });

    }

    class CheckIdThread extends Thread {
        public void run() {
            Document doc = null;
            try {
                URL url = new URL(checkid_url);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    doc = Jsoup.connect(checkid_url).get();
                    Log.e("주소가 제대로 넘어갔니 ?????", checkid_url);
                    final Elements tagsize = doc.select("h1");
                    if (tagsize.size() != 0) {


                        for (int i = 0; i < tagsize.size(); i++) {
                            requestid = doc.select("h1").get(i).getElementsByTag("h1").html();

                            if (requestid.equals("good")) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast message = Toast.makeText(getApplicationContext(), " 사용 가능한 아이디 입니다 . ", Toast.LENGTH_SHORT);
                                        message.setGravity(Gravity.CENTER, 0, 0);
                                        message.show();
                                        id_check = true;
                                        Log.e("boolean 값 :", Boolean.toString(id_check));
                                        test(id_check);
                                    }
                                });


                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast message = Toast.makeText(getApplicationContext(), " 이미 아이디를 사용중입니다  ", Toast.LENGTH_SHORT);
                                        message.setGravity(Gravity.CENTER, 0, 0);
                                        message.show();
                                        createid.setText("");
                                        id_check = false;
                                    }
                                });

                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "아이디를 입력해주세요", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Log.e("에러ㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓ", Integer.toString(conn.getResponseCode()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    class insertDB extends Thread {
        public void run() {
            Document doc = null;
            try {

                doc = Jsoup.connect(insertDB_url).get();
                Log.e("?????????? ?????", insertDB_url);
                final Elements tagsize = doc.select("h1");
                Log.e("111111111111111111111", tagsize.toString());
                if (tagsize.size() != 0) {


                    for (int i = 0; i < 1; i++) {
                        insert_response_code = doc.select("h1").get(i).getElementsByTag("h1").html();

                        if (insert_response_code.equals("good")) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast message = Toast.makeText(getApplicationContext(), " 회원가입 완료 . ", Toast.LENGTH_SHORT);
                                    message.setGravity(Gravity.CENTER, 0, 0);
                                    message.show();
                                    Intent intent = new Intent(CreateUser.this, loginpage.class);
                                    startActivity(intent);
                                }
                            });


                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast message = Toast.makeText(getApplicationContext(), " 에러발생  ", Toast.LENGTH_SHORT);

                                    message.show();

                                }
                            });
                        }

                    }


                } else {

                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    public void test(boolean id_check) {
        if (id_check) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(CreateUser.this);
            dlg.setTitle("아이디 확인");
            dlg.setMessage(createid.getText().toString() + "아이디를 사용 하시겠습니까 ?");
            dlg.setPositiveButton("사용", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    createid.setFocusable(false);

                }
            });
            dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    createid.setFocusable(true);
                    requestid = "fail";
                }
            });
            final AlertDialog alert = dlg.create();
            alert.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                }
            });

            alert.show();
        } else {
            Toast.makeText(getApplicationContext(), " 에러냐?", Toast.LENGTH_SHORT).show();
        }
    }

}

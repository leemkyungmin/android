package com.example.nemojsoup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    Handler handler = new Handler();
    Spinner location1, location2;
    Button search;
    ArrayList<String> arrayList, arrayList2;
    ArrayAdapter<String> arrayAdapter, arrayAdapter2;
    TextView test;
    public String location;
    public String location2_idr;
    public String clickhtml = "http://192.168.28.5:8080/db/nemotest.jsp";
    public String localhost = "http://192.168.28.5:8080/db";
    public String type;
    public String[] address;
    public String[] name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationThread locationThread = new locationThread();
        locationThread.start();

        myThread thread = new myThread();
        thread.start();


        search = (Button) findViewById(R.id.serach);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type != null) {
                    clickhtml = "http://192.168.28.5:8080/db/nemotest.jsp?location2_id=" + location2_idr + "&type=" + type;
                    Toast.makeText(getApplicationContext(), clickhtml, Toast.LENGTH_LONG).show();
                    myThread thread = new myThread();
                    thread.start();

                } else {
                    clickhtml = "http://192.168.28.5:8080/db/nemotest.jsp?location2_id=" + location2_idr;
                    Toast.makeText(getApplicationContext(), clickhtml, Toast.LENGTH_LONG).show();
                    myThread thread = new myThread();
                    thread.start();

                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, "셀프 스토리지");
        menu.add(0, 2, 0, "물류 창고");
        menu.add(0, 3, 0, "컨테이너 보관소");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                type = "ss";
                return true;
            case 2:
                type = "wh";
                return true;
            case 3:
                type = "ct";
                return true;
        }
        return true;
    }

    class myThread extends Thread {
        public void run() {
            test = (TextView) findViewById(R.id.test);
            Document doc = null;

            try {
                URL url = new URL(clickhtml);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    doc = Jsoup.connect(clickhtml).get();

                    Elements img = doc.select("img");
                    Elements atag = doc.select("h1");
                    final String[] asrc = new String[img.size()];
                    String[] imgsrc = new String[img.size()];
                    address = new String[atag.size() / 2];
                    name = new String[atag.size() / 2];
                    final ListView lv = (ListView) findViewById(R.id.listview);
                    final ArrayList<Story> al = new ArrayList<Story>();
                    al.clear();


                    int count = 0;
                    for (int j = 0; j < atag.size() / 2; j++) {
                        address[j] = doc.select("h1").get(count).getElementsByTag("h1").html();
                        count++;
                        name[j] = doc.select("h1").get(count).getElementsByTag("h1").html();
                        count++;

                    }

                    for (int i = 0; i < img.size(); i++) {
                        imgsrc[i] = doc.select("a").get(i).getElementsByTag("img").attr("src");
                        asrc[i] = doc.select("a").get(i).attr("href");
                        al.add(new Story(name[i], address[i], imgsrc[i]));

                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyAdapter adapter = new MyAdapter(
                                    MainActivity.this,
                                    R.layout.list_item,
                                    al);

                            lv.setAdapter(adapter);

                        }
                    });
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Toast.makeText(getApplicationContext(), asrc[position], Toast.LENGTH_SHORT).show();

                            String DetailHttp = asrc[position];
                            Intent intent = new Intent(MainActivity.this, Detail.class);
                            intent.putExtra("DetailHttp", DetailHttp);
                            startActivity(intent);

                        }
                    });

                }


            } catch (IOException e) {
               e.printStackTrace();

            }
        }
    }
        class MyAdapter extends BaseAdapter {
            Context context;
            int layout;
            ArrayList<Story> al;
            LayoutInflater inf;

            public MyAdapter(Context context, int layout, ArrayList<Story> al) {
                this.context = context;
                this.layout = layout;
                this.al = al;
                this.inf = (LayoutInflater) context.getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);
            }

            @Override
            public int getCount() {
                return al.size();
            }

            @Override
            public Object getItem(int position) {
                return al.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View v, ViewGroup parent) {
                if (v == null)
                    v = inf.inflate(layout, null);

                TextView tv1 = (TextView) v.findViewById(R.id.text1);
                TextView tv2 = (TextView) v.findViewById(R.id.text2);
                ImageView iv = (ImageView) v.findViewById(R.id.image);

                Story s = al.get(position);
                tv1.setText(s.date);
                tv2.setText(s.message);
                Glide.with(context)
                        .load(localhost + "/" + s.img)
                        .into(iv);
                return v;
            }
        }

        public class Story {
            String date = "";
            String message = "";
            String img = "";

            public Story(String date, String message, String img) {
                this.date = date;
                this.message = message;
                this.img = img;
            }

            public Story() {
            }
        }

        public class locationThread extends Thread {
            public void run() {
                Document document;
                String html = localhost + "/location1.jsp";

                try {
                    document = Jsoup.connect(html).get();
                    Elements A = document.select("a");
                    final String[] location1_id = new String[A.size() / 2];
                    String[] location1_name = new String[A.size() / 2];
                    arrayList = new ArrayList<>();
                    for (int i = 0; i < A.size() / 2; i++) {
                        location1_id[i] = document.select("a").get(i * 2).html();

                    }
                    for (int j = 0; j < A.size() / 2; j++) {
                        location1_name[j] = document.select("a").get((j * 2) + 1).html();
                        arrayList.add(location1_name[j]);

                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            location1 = (Spinner) findViewById(R.id.location1);
                            arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    arrayList);
                            location1.setAdapter(arrayAdapter);
                            location1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                    location = location1_id[i];

                                    location2Thread location2Thread = new location2Thread();
                                    location2Thread.start();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                }
                            });
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public class location2Thread extends Thread {
            public void run() {
                String html = localhost + "/location2.jsp?location1_id=" + location;
                Document document;
                try {
                    document = Jsoup.connect(html).get();
                    Elements ac = document.select("a");
                    final String[] location2_id = new String[ac.size() / 2];
                    final String[] location2_name = new String[ac.size() / 2];

                    arrayList2 = new ArrayList<>();
                    for (int i = 0; i < ac.size() / 2; i++) {
                        location2_id[i] = document.select("a").get(i * 2).html();

                    }
                    for (int j = 0; j < ac.size() / 2; j++) {
                        location2_name[j] = document.select("a").get((j * 2) + 1).html();

                        arrayList2.add(location2_name[j]);

                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            location2 = (Spinner) findViewById(R.id.location2);
                            arrayAdapter2 = new ArrayAdapter<>(getApplicationContext(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    arrayList2);
                            location2.setAdapter(arrayAdapter2);
                            location2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    location2_idr = location2_id[i];

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }


    }


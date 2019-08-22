package com.example.nemojsoup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.nemojsoup.loginpage;
import com.bumptech.glide.Glide;
import android.content.SharedPreferences;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class mainpage extends AppCompatActivity {

    // 기숙사 221.147.172.121
    // 303  192.168.28.5

    Handler handler = new Handler();
    public String localhost = "http://192.168.28.5:8080/db/";
    public String searchadr = "http://192.168.28.5:8080/db";
    Spinner location1, location2, service;
    Button search;
    Button login;

    ArrayList arrayList, arrayList2, arrayList3;
    ArrayAdapter arrayAdapter, arrayAdapter2, arrayAdapter3;
    public String location;
    public String location2_idr;
    public String type;
    public ArrayList<String> address, name, imgsrc,http;
    TextView sessionname;
    Button sessionButton,insertList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);

        locationThread locationThread = new locationThread();
        locationThread.start();

        ServiceThread serviceThread = new ServiceThread();
        serviceThread.start();

        selfThread selfThread = new selfThread();
        selfThread.start();

        containnerThread containnerThread = new containnerThread();
        containnerThread.start();

        WhitehouseThread whitehouseThread = new WhitehouseThread();
        whitehouseThread.start();
        final SharedPreferences session = getSharedPreferences("loginsession",MODE_PRIVATE);
        Boolean loginSession =session.getBoolean("session",false);
        String id=session.getString("ID","1");

        login=(Button)findViewById(R.id.loginform);
        sessionButton=(Button)findViewById(R.id.sessionlogout);
        sessionname=(TextView)findViewById(R.id.sessionname);
        insertList=(Button)findViewById(R.id.insertList);

        if(session.getString("ID","") !=""){

            login.setVisibility(View.INVISIBLE);
            insertList.setVisibility(View.VISIBLE);
            sessionButton.setVisibility(View.VISIBLE);
            sessionname.setText(session.getString("ID",""));

            insertList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mainpage.this,insertList.class);
                    startActivity(intent);
                }
            });

        }
        else{
            login.setVisibility(View.VISIBLE);
            insertList.setVisibility(View.INVISIBLE);
            sessionButton.setVisibility(View.INVISIBLE);
            sessionname.setText("");
        }
        sessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = session.edit();
                editor.putBoolean("session",false);
                editor.putString("ID","");
                editor.commit();
                Intent intent=new Intent(mainpage.this,mainpage.class);
                startActivity(intent);
            }
        });
            Log.e("세션값 :",Boolean.toString(loginSession));
        Log.e("세션 아이디 값  :",id);

        search = (Button) findViewById(R.id.serach);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type != null) {
                    searchadr = "http://192.168.28.5:8080/db/nemotest.jsp?location2_id=" + location2_idr + "&type=" + type;
                    Log.e("123421421321123123",location2_idr+type) ;
                    myThread myThread = new myThread();
                    myThread.start();


/*
                    Intent intent = new Intent(mainpage.this, mapac.class);
                    intent.putStringArrayListExtra("address", address);
                    intent.putStringArrayListExtra("name", name);
                    intent.putStringArrayListExtra("img", imgsrc);
                    startActivity(intent);
*/
                }

            }
        });
        login=(Button)findViewById(R.id.loginform);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent=new Intent(mainpage.this,loginpage.class);
                startActivity(loginIntent);
            }
        });

    }

    class myThread extends Thread {
        public void run() {

            Document doc = null;

            try {
                URL url = new URL(searchadr);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    doc = Jsoup.connect(searchadr).get();

                    Elements img = doc.select("img");
                    final Elements h1tag = doc.select("h1");

                    int count = 0;
                    address=new ArrayList<String>();
                    name=new ArrayList<String>();
                    imgsrc=new ArrayList<String>();
                    http=new ArrayList<String>();

                    final String[] asrc = new String[img.size()];
                    for (int j = 0; j < h1tag.size() / 2; j++) {

                        address.add((doc.select("h1").get(count).getElementsByTag("h1").html()));
                        count++;
                        name.add(doc.select("h1").get(count).getElementsByTag("h1").html());
                        count++;

                    }

                    for (int i = 0; i < img.size(); i++) {
                        imgsrc.add(doc.select("a").get(i).getElementsByTag("img").attr("src"));
                        http.add(doc.select("a").get(i).attr("href"));

                    }

                }
                if(address.size()<=0 || name.size()<=0 || imgsrc .size()<=0){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast message=Toast.makeText(getApplicationContext()," 등록된 업체가 없습니다 ",Toast.LENGTH_SHORT);
                            message.setGravity(Gravity.CENTER, 0, 0);
                            message.show();
                        }
                    });


                }
                else{
                    Intent intent = new Intent(mainpage.this, mapac.class);
                    intent.putStringArrayListExtra("address", address);
                    intent.putStringArrayListExtra("name", name);
                    intent.putStringArrayListExtra("img", imgsrc);
                    intent.putStringArrayListExtra("http",http);
                    startActivity(intent);
                }



            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    class selfThread extends Thread {
        public void run() {
            Document doc = null;
            try {
                URL url = new URL(localhost + "/nemotest.jsp?type=ss");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    doc = Jsoup.connect(localhost + "/nemotest.jsp?type=ss").get();

                    Elements img = doc.select("img");
                    Elements atag = doc.select("h1");
                    final String[] asrc = new String[img.size()];
                    String[] imgsrc = new String[img.size()];
                    String address[] = new String[atag.size() / 2];
                    String name[] = new String[atag.size() / 2];
                    final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.self);

                    final ArrayList<list_name> al = new ArrayList<list_name>();
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
                        al.add(new list_name(name[i], address[i], imgsrc[i],asrc[i]));

                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            RecyclerView.LayoutManager mLayoutManager;
                            mLayoutManager = new LinearLayoutManager(mainpage.this);
                            ((LinearLayoutManager) mLayoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);
                            recyclerView.setLayoutManager(mLayoutManager);
                            MyAdapter myAdapter = new MyAdapter(al);
                            recyclerView.setAdapter(myAdapter);

                        }
                    });


                }


            } catch (IOException e) {
                e.printStackTrace();

            }
        }

    }

    class containnerThread extends Thread {
        public void run() {
            Document doc = null;
            try {
                URL url = new URL(localhost + "/nemotest.jsp?type=ct");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    doc = Jsoup.connect(localhost + "/nemotest.jsp?type=ct").get();

                    Elements img = doc.select("img");
                    Elements atag = doc.select("h1");
                    final String[] asrc = new String[img.size()];
                    String[] imgsrc = new String[img.size()];
                    String address[] = new String[atag.size() / 2];
                    String name[] = new String[atag.size() / 2];
                    final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.containner);
                    final ArrayList<list_name> al = new ArrayList<list_name>();
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
                        al.add(new list_name(name[i], address[i], imgsrc[i],asrc[i]));

                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            RecyclerView.LayoutManager mLayoutManager;
                            mLayoutManager = new LinearLayoutManager(mainpage.this);
                            ((LinearLayoutManager) mLayoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);
                            recyclerView.setLayoutManager(mLayoutManager);
                            MyAdapter myAdapter = new MyAdapter(al);
                            recyclerView.setAdapter(myAdapter);

                        }
                    });

                }


            } catch (IOException e) {
                e.printStackTrace();

            }
        }

    }

    class WhitehouseThread extends Thread {
        public void run() {
            Document doc = null;
            try {
                URL url = new URL(localhost + "/nemotest.jsp?type=wh");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    doc = Jsoup.connect(localhost + "/nemotest.jsp?type=wh").get();

                    Elements img = doc.select("img");
                    Elements atag = doc.select("h1");
                    final String[] asrc = new String[img.size()];
                    String[] imgsrc = new String[img.size()];
                    String address[] = new String[atag.size() / 2];
                    String name[] = new String[atag.size() / 2];
                    final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.whitehouse);
                    final ArrayList<list_name> al = new ArrayList<list_name>();
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
                        al.add(new list_name(name[i], address[i], imgsrc[i],asrc[i]));

                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            RecyclerView.LayoutManager mLayoutManager;
                            mLayoutManager = new LinearLayoutManager(mainpage.this);
                            ((LinearLayoutManager) mLayoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);
                            recyclerView.setLayoutManager(mLayoutManager);
                            MyAdapter myAdapter = new MyAdapter(al);
                            recyclerView.setAdapter(myAdapter);

                        }
                    });


                }


            } catch (IOException e) {
                e.printStackTrace();

            }
        }

    }

    public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView text1, text2;
            Context context;


            MyViewHolder(View view) {
                super(view);
                image = view.findViewById(R.id.image);
                text1 = view.findViewById(R.id.text1);
                text2 = view.findViewById(R.id.text2);
            }
        }

        private ArrayList<list_name> al;

        MyAdapter(ArrayList<list_name> al) {
            this.al = al;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view2, parent, false);

            return new MyViewHolder(v);
        }


        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

            MyViewHolder myViewHolder = (MyViewHolder) holder;


            Glide.with(holder.itemView.getContext())
                    .load(localhost + al.get(position).img)
                    .into(myViewHolder.image);
            myViewHolder.text1.setText(al.get(position).name);
            myViewHolder.text2.setText(al.get(position).address);

            ((MyViewHolder) holder).image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(holder.itemView.getContext(), Detail.class);
                    intent.putExtra("DetailHttp", al.get(position).http);
                    startActivity(intent);
                }
            });

        }


        @Override
        public int getItemCount() {
            return al.size();
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
                arrayList.clear();
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
                arrayList2.clear();
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

    public class ServiceThread extends Thread {
        public void run() {
            Document document;
            String html = localhost + "/Service.jsp";

            try {
                document = Jsoup.connect(html).get();
                Elements A = document.select("a");
                final String[] type_name = new String[A.size() / 2];
                String[] service_name = new String[A.size() / 2];
                arrayList3 = new ArrayList<>();
                arrayList3.clear();
                for (int i = 0; i < A.size() / 2; i++) {
                    type_name[i] = document.select("a").get(i * 2).html();

                }
                for (int j = 0; j < A.size() / 2; j++) {
                    service_name[j] = document.select("a").get((j * 2) + 1).html();
                    arrayList3.add(service_name[j]);

                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        service = (Spinner) findViewById(R.id.service);
                        arrayAdapter3 = new ArrayAdapter<>(getApplicationContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                arrayList3);
                        service.setAdapter(arrayAdapter3);
                        service.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                type = type_name[i];
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

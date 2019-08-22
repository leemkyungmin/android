package com.example.nemojsoup;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class insertList extends Activity {
    Button db_insert_data, db_insert_clear, img1, img2, img3;
    Button[] img;
    Handler handler=new Handler();
    EditText homepage,sns,db_insert_name, db_insert_address, db_ph_first, db_ph_second, db_ph_thrid, email, db_insert_worktime, db_insert_customusingtime, db_insert_type_of_price;
    Spinner location1, location2, service;
    public String name, address, phnumber, re_email, worktime, cusomsuingtime, type_of_price,re_sns,re_homepage;
    private static int PICK_IMAGE1 = 1;
    public String localhost = "http://192.168.28.5:8080/db/";
    public String insertDB_url="http://192.168.28.5:8080/db/";
    ArrayList arrayList, arrayList2, arrayList3;
    ArrayAdapter arrayAdapter, arrayAdapter2, arrayAdapter3;
    Uri photoURI, albumURI;
    String mCurrentPhotoPath;
    private static final int REQUEST_TAKE_ALBUM = 3333;
    String[] files = new String[3];
    public String[] filenames = new String[3];
    public int index = 0;

    public String location1_idx,location2_idr;
    public String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insertlist);

        locationThread locationThread=new locationThread();
        locationThread.start();
        location2Thread location2Thread=new location2Thread();
        location2Thread.start();

        ServiceThread serviceThread=new ServiceThread();
        serviceThread.start();

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog().build());

        db_insert_data = (Button) findViewById(R.id.db_insert_data);
        db_insert_clear = (Button) findViewById(R.id.db_insert_clear);
        img1 = (Button) findViewById(R.id.img1);
        img2 = (Button) findViewById(R.id.img2);
        img3 = (Button) findViewById(R.id.img3);
         img=new Button[3];
        Integer[] img_id={R.id.img1,R.id.img2,R.id.img3};
        for(int i=0; i<3; i++){
            img[i]=(Button)findViewById(img_id[i]);
        }
        db_insert_name = (EditText) findViewById(R.id.db_insert_name);
        db_insert_address = (EditText) findViewById(R.id.db_insert_address);
        db_ph_first = (EditText) findViewById(R.id.db_ph_first);
        db_ph_second = (EditText) findViewById(R.id.db_ph_second);
        db_ph_thrid = (EditText) findViewById(R.id.db_ph_thrid);
        email = (EditText) findViewById(R.id.email);
        db_insert_worktime = (EditText) findViewById(R.id.db_insert_worktime);
        db_insert_customusingtime = (EditText) findViewById(R.id.db_insert_customusingtime);
        db_insert_type_of_price = (EditText) findViewById(R.id.db_insert_type_of_price);
        sns=(EditText)findViewById(R.id.sns) ;
        homepage=(EditText)findViewById(R.id.homepage) ;


        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 0;
                getAlbum();

            }

        });
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 1;
                getAlbum();
            }

        });
        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = 2;
                getAlbum();
            }

        });

        checkPermission(); //권한 확인

        if (db_insert_name!=null && db_insert_address!=null && db_ph_first!=null && db_ph_second!=null && db_ph_thrid!=null && email!=null &&
           db_insert_worktime!=null && db_insert_customusingtime!=null && db_insert_type_of_price!=null) {
        db_insert_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    name = URLEncoder.encode(db_insert_name.getText().toString(), "utf-8");
                    address = URLEncoder.encode(db_insert_address.getText().toString(), "utf-8");
                    phnumber = URLEncoder.encode(db_ph_first.getText().toString(), "utf-8");
                    re_email = URLEncoder.encode(email.getText().toString(), "utf-8");
                    worktime = URLEncoder.encode(db_insert_worktime.getText().toString(), "utf-8");
                    cusomsuingtime = URLEncoder.encode(db_insert_customusingtime.getText().toString(), "utf-8");
                    type_of_price = URLEncoder.encode(db_insert_type_of_price.getText().toString(), "utf-8");
                    re_homepage=URLEncoder.encode(homepage.getText().toString(), "utf-8");
                    re_sns=URLEncoder.encode(sns.getText().toString(), "utf-8");
                    insertDB_url="http://192.168.28.5:8080/db/ask/ask_insert_save.jsp?name="+name+"&address="+address+"&phnumber="+phnumber+"&email="+re_email+"&sns="+re_sns+"&homepage="+re_homepage+"&worktime="+worktime+"&usingtime="+cusomsuingtime+
                            "&typeOfprice="+type_of_price+"location2_id="+location2_idr+"&type="+type+"&img_1="+filenames[0]+"&img_2="+filenames[1]+"&img_3="+filenames[2];
                    Log.e("왜 안되니 ???? ",insertDB_url);
                    insertDB insertDB=new insertDB();
                    insertDB.start();
                    for(int i=0; i<3; i++){
                        Log.e(i+"번째 이미지 파일 있는가 ",files[i].toString());
                        Log.e(i+"번째 이미지 파일이름 ",filenames[i]);
                        DoFileUpload("http://192.168.28.5:8080/db/ask/ask_upload.jsp",files[i]);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }








            }
        });
       } else {
           Toast errorToast = Toast.makeText(getApplicationContext(), "빈칸 없이 작성 해주세요 ", Toast.LENGTH_LONG);
           errorToast.setGravity(Gravity.CENTER, 0, 0);
            errorToast.show();
            }

    }




    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "img_"+(index+1)+".jpg"; // 이미지 이름
        File imageFile = null;
        //File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "ASK");
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "ASK");
        if (!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1", storageDir.toString());
            storageDir.mkdirs();
        }

        imageFile = new File(storageDir, imageFileName); // 이미지 파일
        mCurrentPhotoPath = imageFile.getAbsolutePath();
        filenames[index] = imageFileName;
        files[index] = mCurrentPhotoPath;
        img[index].setText(filenames[index]);
        return imageFile;
    }

    private void getAlbum() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM); //PICK_IMAGE에는 본인이 원하는 상수넣으면된다.
    }

    public void cropImage() {
        Log.i("cropImage", "Call");
        Log.i("cropImage", "photoURI : " + photoURI + " / albumURI : " + albumURI);

        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        // 50x50픽셀미만은 편집할 수 없다는 문구 처리 + 갤러리, 포토 둘다 호환하는 방법
        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoURI, "image/*");
        //cropIntent.putExtra("outputX", 200); // crop한 이미지의 x축 크기, 결과물의 크기
        //cropIntent.putExtra("outputY", 200); // crop한 이미지의 y축 크기
        cropIntent.putExtra("aspectX", 1); // crop 박스의 x축 비율, 1&1이면 정사각형
        cropIntent.putExtra("aspectY", 1); // crop 박스의 y축 비율
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", albumURI); // 크랍된 이미지를 해당 경로에 저장
        startActivityForResult(cropIntent, 4444);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {


            case REQUEST_TAKE_ALBUM:
                if (resultCode == Activity.RESULT_OK) {

                    if (data.getData() != null) {
                        try {
                            File albumFile = null;
                            albumFile = createImageFile();
                            photoURI = data.getData();
                            albumURI = Uri.fromFile(albumFile);
                            cropImage();

                        } catch (Exception e) {
                            Log.e("TAKE_ALBUM_SINGLE ERROR", e.toString());
                        }
                    }
                }
                break;

        }
    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{..}의 요청으로 넘어감
            if ((ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) ||
                    (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))) {
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1111);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1111:
                for (int i = 0; i < grantResults.length; i++) {
                    // grantResults[] : 허용된 권한은 0, 거부한 권한은 -1
                    if (grantResults[i] < 0) {
                        Toast.makeText(insertList.this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // 허용했다면 이 부분에서..

                break;
        }
    }


    public void DoFileUpload(String apiUrl, String absolutePath) {

        HttpFileUpload(apiUrl, "", absolutePath);

    }



    public void HttpFileUpload(String urlString, String params, String fileName) {


        Log.e("URL",urlString);
        String lineEnd = "\r\n";

        String twoHyphens = "--";

        String boundary = "*****";
        Log.e("bounday",boundary);
        try {

            File sourceFile = new File(fileName);
            Log.e("sourceFile",sourceFile.toString());
            Log.e("filename",fileName);
            DataOutputStream dos;

            if (!sourceFile.isFile()) {

                Log.e("uploadFile", "Source File not exist :" + fileName);

            } else {

                FileInputStream mFileInputStream = new FileInputStream(sourceFile);

                URL connectUrl = new URL(urlString);

                // open connection

                HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();

                conn.setDoInput(true);

                conn.setDoOutput(true);

                conn.setUseCaches(false);

                conn.setRequestMethod("POST");

                conn.setRequestProperty("Connection", "Keep-Alive");

                conn.setRequestProperty("ENCTYPE", "multipart/form-data");

                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                conn.setRequestProperty("uploaded_file", fileName);

                // write data

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);
                Log.e("dos",dos.toString());


                int bytesAvailable = mFileInputStream.available();

                int maxBufferSize = 1024 * 1024;

                int bufferSize = Math.min(bytesAvailable, maxBufferSize);



                byte[] buffer = new byte[bufferSize];

                int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);



                // read image

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);

                    bytesAvailable = mFileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);

                    bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

                }



                dos.writeBytes(lineEnd);

                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                mFileInputStream.close();

                dos.flush(); // finish upload...

                if (conn.getResponseCode() == 200) {

                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");

                    BufferedReader reader = new BufferedReader(tmp);

                    StringBuffer stringBuffer = new StringBuffer();

                    String line;

                    while ((line = reader.readLine()) != null) {

                        stringBuffer.append(line);

                    }

                }

                mFileInputStream.close();

                dos.close();

            }

        } catch (Exception e) {

            e.printStackTrace();

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

                                location1_idx = location1_id[i];

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
            String html = localhost + "/location2.jsp?location1_id=" + location1_idx;
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
                        service = (Spinner) findViewById(R.id.type);
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
                       String insert_response_code = doc.select("h1").get(i).getElementsByTag("h1").html();

                        if (insert_response_code.equals("good")) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast message = Toast.makeText(getApplicationContext(), " 등록완료 . ", Toast.LENGTH_SHORT);
                                    message.setGravity(Gravity.CENTER, 0, 0);
                                    message.show();
                                    Intent intent = new Intent(insertList.this, mainpage.class);
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

}
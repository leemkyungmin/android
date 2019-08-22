package com.example.nemojsoup;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationProvider;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.location.LocationManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class mapac extends FragmentActivity implements OnMapReadyCallback {

    // 기숙사 221.147.172.121
    // 303  192.168.28.5

    private GoogleMap mMap;
    public ArrayList<String> address, name, img, http;
    public String localhost = "http://192.168.28.5:8080/db/";
    Button locationgps;
    double mLatitude;  //위도
    double mLongitude; //경도


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onStart();
        setContentView(R.layout.activity_mapac);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        Intent intent = getIntent();

        address = new ArrayList<String>();
        name = new ArrayList<String>();
        img = new ArrayList<String>();
        http = new ArrayList<String>();

        address = intent.getStringArrayListExtra("address");
        name = intent.getStringArrayListExtra("name");
        img = intent.getStringArrayListExtra("img");
        http = intent.getStringArrayListExtra("http");


        MapAdd(address, name);
    }


    public void MapAdd(ArrayList<String> address, ArrayList<String> name) {
        List<Address> addressList = null;
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        for (int i = 0; i < address.size(); i++) {
            try {

                addressList = geocoder.getFromLocationName(
                        address.get(i), // 주소
                        5); // 최대 검색 결과 개수

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (addressList == null) {
                try {

                    Log.e("error", "null값 이 넘어왓음  ");

                    Toast.makeText(getApplicationContext(), "검색error", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("good", addressList.toString());
                Address lating = addressList.get(0);
                Double lat = lating.getLatitude(); // 위도가져오기
                Double lon = lating.getLongitude(); // 경도가져오기
                Log.e("lat", Double.toString(lat));
                Log.e("long", Double.toString(lon));

                MarkerOptions makerOptions = new MarkerOptions();
                makerOptions
                        .position(new LatLng(lat, lon))
                        .title(name.get(i))
                        .snippet(address.get(i));
                mMap.addMarker(makerOptions);

                if (i == 0) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lon)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 16));

                }
                final ListView lv = (ListView) findViewById(R.id.itemlist);

                final ArrayList<list_name> al = new ArrayList<list_name>();
                al.clear();

                for (int j = 0; j < address.size(); j++) {
                    al.add(new list_name(address.get(j), name.get(j), img.get(j), http.get(j)));
                }
                MyAdapter adapter = new MyAdapter(
                        mapac.this,
                        R.layout.list_view2,
                        al);

                lv.setAdapter(adapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String DetailHttp = http.get(position);
                        Intent intent = new Intent(mapac.this, Detail.class);
                        intent.putExtra("DetailHttp", DetailHttp);
                        startActivity(intent);

                    }
                });
            }
            locationgps = (Button) findViewById(R.id.gpsButton);
            locationgps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    LocationListener locationListener = new LocationListener() {

                        @Override
                        public void onLocationChanged(Location location) {
                            updateMap(location);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                           // alertStatus(provider);
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                            //alertProvider(provider);
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                           // checkProvider(provider);
                        }

                    };
                    String locationProvider = LocationManager.NETWORK_PROVIDER;
                    locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
                }
            });
        }

    }

    public void updateMap(Location location) {
        double lati = location.getLatitude();
        double longti = location.getLongitude();
        MarkerOptions makerOptions = new MarkerOptions();
        makerOptions
                .position(new LatLng(lati, longti))
                .title("현재위치");

        mMap.addMarker(makerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lati, longti), 16));
        Log.e("현재위치 ", "위도:" + lati + "경도:" + longti);
    }

    public void checkProvider(String provider) {

        Toast.makeText(getApplicationContext(), provider + "위치 서비스 시작 ", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
        startActivity(intent);

    }

    public void alertProvider(String provider) {
        Toast.makeText(getApplicationContext(), provider + "위치 서비스 시작 ", Toast.LENGTH_SHORT).show();
    }

    public void alertStatus(String provider) {
        Toast.makeText(getApplicationContext(), provider + "위치 변경 ", Toast.LENGTH_SHORT).show();
    }

    class MyAdapter extends BaseAdapter {
        Context context;
        int layout;
        ArrayList<list_name> al;
        LayoutInflater inf;

        public MyAdapter(Context context, int layout, ArrayList<list_name> al) {
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

            list_name s = al.get(position);
            tv1.setText(s.address);
            tv2.setText(s.name);
            Glide.with(context)
                    .load(localhost + "/" + s.img)
                    .into(iv);
            return v;
        }
    }


}


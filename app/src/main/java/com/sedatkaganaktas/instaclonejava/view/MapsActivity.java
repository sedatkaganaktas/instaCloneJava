package com.sedatkaganaktas.instaclonejava.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.sedatkaganaktas.instaclonejava.R;
import com.sedatkaganaktas.instaclonejava.databinding.ActivityMapsBinding;
import com.sedatkaganaktas.instaclonejava.model.Place;
import com.sedatkaganaktas.instaclonejava.roomdb.PlaceDao;
import com.sedatkaganaktas.instaclonejava.roomdb.PlaceDatabase;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    ActivityResultLauncher<String> permissionLauncher;
    LocationManager locationManager;
    LocationListener locationListener;
    SharedPreferences sharedPreferences;
    boolean information;
     PlaceDatabase db;
     PlaceDao placeDao;
    Double selectedLatitude;
    Double selectedLongitude;
    private CompositeDisposable compositeDisposable= new CompositeDisposable();
    Place selectedPlace;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        registerLauncher();
        sharedPreferences= MapsActivity.this.getSharedPreferences("com.sedatkaganaktas.instaclonejava",MODE_PRIVATE);
        information = false;

      db= Room.databaseBuilder(getApplicationContext(),PlaceDatabase.class,"Places").build(); //db= Room.databaseBuilder(MapsActivity.this,PlaceDatabase.class,"Places").build(); ==> bu da bir yol

      placeDao=db.placeDao(); // bu ikisinden sonra save delete gibi fonk. ulaşabilirim
        selectedLatitude=0.0;
        selectedLongitude=0.0;

        binding.saveButton.setEnabled(false); // ==> kullanıcı basılı tutup bir yer seçene ve lngitude latitude atanana kadar kullanılmaz.
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);


         binding.saveButton.setEnabled(false);
        locationManager= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener= new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                information=sharedPreferences.getBoolean("info",false);

                if(!information){
                    LatLng userLocation=new LatLng(location.getLatitude(),location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
                    sharedPreferences.edit().putBoolean("info",true).apply();
                }

            }
        };
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            //izin iste
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Snackbar.make(binding.getRoot(),"haritalar için isteniyor",Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //izin istemek yine

                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                    }
                }).show();
            }else {
                //izin zaten verilmişse
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

            Location lastLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastLocation!=null){

                LatLng lastUserLocation=new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));
            }

            mMap.setMyLocationEnabled(true);
        }



    }

    private void registerLauncher(){
        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    //permission garnted
                    if(ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener); // kullanıcı izin verirse kullanırım.

                        Location lastLocation= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (lastLocation != null){

                            LatLng lastUserLocation= new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));

                        }
                    }

                }else {
                    //permission denied
                    Toast.makeText(MapsActivity.this, "izin gerekli!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));

        selectedLatitude=latLng.latitude;
        selectedLongitude=latLng.longitude;

        binding.saveButton.setEnabled(true);
    }

    public void save(View view){

        Place place= new Place(binding.placeNameText.getText().toString(),selectedLatitude,selectedLongitude);

        // threading => Main (UI), default (CPU Intensive), IO (network, database)
        //placeDao.insert(place).subscribeOn(Schedulers.io()).subscribe();
        // disposable



        compositeDisposable.add(placeDao.insert(place)  // insert yap
                .subscribeOn(Schedulers.io()) // io thread ile yap
                .observeOn(AndroidSchedulers.mainThread()) // main thread ile gözlemle/ zorunlu değil
                .subscribe(MapsActivity.this::handleResponse) // maps activity. this e subscribe ol ==> çalıştırcağım metodu yazdım
        );



    }
    public void delete(View view){



       if (selectedPlace!= null){
            compositeDisposable.add(placeDao.delete(selectedPlace)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(MapsActivity.this::handleResponse)
            );
        }


    }

    private void handleResponse(){
        Intent intent=new Intent(MapsActivity.this, MyLocations.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
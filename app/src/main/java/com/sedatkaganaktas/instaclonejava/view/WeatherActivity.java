package com.sedatkaganaktas.instaclonejava.view;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sedatkaganaktas.instaclonejava.R;
import com.squareup.picasso.Picasso;

public class WeatherActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                showWeatherData(latitude, longitude);
            }
        }
    };
    private TextView textViewLocation;
    private ImageView imageViewWeatherIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        textViewLocation = findViewById(R.id.textview_location);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        imageViewWeatherIcon = findViewById(R.id.imageview_weather_icon);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkLocationPermission()) {
            startLocationUpdates();
        } else {
            requestLocationPermission();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(LocationRequest.create(), locationCallback, null);
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private boolean checkLocationPermission() {
        int permissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            Toast.makeText(this, "Konum izni gereklidir.", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Konum izni reddedildi.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showWeatherData(double latitude, double longitude) {
        try {

            String locationText = "Latitude: " + latitude + ", Longitude: " + longitude + "Verileri Çekiliyor WeatherAPI.com";
            textViewLocation.setText(locationText);
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "Hava durumu verilerine erişilemedi.", Toast.LENGTH_SHORT).show();
        }
        try {
            OkHttpClient client = new OkHttpClient();

            String url = "URL" + latitude + "%2C" + longitude;

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("API-Key", "API KEY")
                    .addHeader("API-Host", "HOST")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(WeatherActivity.this, "Hava durumu verilerine erişilemedi.", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();


                        Gson gson = new Gson();
                        JsonParser jsonParser = new JsonParser();
                        JsonObject jsonObject = jsonParser.parse(responseData).getAsJsonObject();


                        JsonObject currentObject = jsonObject.getAsJsonObject("current");


                        int tempC = currentObject.get("temp_c").getAsInt();
                        String iconUrl = "https:" + currentObject.getAsJsonObject("condition").get("icon").getAsString();

                        runOnUiThread(() -> {

                            String temperatureText = "Sıcaklık: " + tempC + "°C";
                            textViewLocation.setText(temperatureText);
                            System.out.println(iconUrl);
                            Picasso.get().load(iconUrl).into(imageViewWeatherIcon);
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(WeatherActivity.this, "Hava durumu verilerine erişilemedi.", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Hava durumu verilerine erişilemedi.", Toast.LENGTH_SHORT).show();
 }
}
}
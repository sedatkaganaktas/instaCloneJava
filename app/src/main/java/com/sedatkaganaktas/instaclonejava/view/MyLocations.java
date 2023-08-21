package com.sedatkaganaktas.instaclonejava.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.sedatkaganaktas.instaclonejava.R;
import com.sedatkaganaktas.instaclonejava.adapter.PlaceAdapter;
import com.sedatkaganaktas.instaclonejava.databinding.ActivityMyLocationsBinding;
import com.sedatkaganaktas.instaclonejava.model.Place;
import com.sedatkaganaktas.instaclonejava.roomdb.PlaceDao;
import com.sedatkaganaktas.instaclonejava.roomdb.PlaceDatabase;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MyLocations extends AppCompatActivity {
   private ActivityMyLocationsBinding binding;
    private CompositeDisposable compositeDisposable=new CompositeDisposable();
    PlaceDatabase db;
    PlaceDao placeDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMyLocationsBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_my_locations);
        View view = binding.getRoot();
        setContentView(view);
        db= Room.databaseBuilder(getApplicationContext(),PlaceDatabase.class,"Places").build();
        placeDao=db.placeDao();

        compositeDisposable.add(placeDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MyLocations.this::handleResponse));
    }

    private  void handleResponse(List<Place> placeList){  //flowble list alıyorsam burada da o verilmesli
        binding.mylocRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        PlaceAdapter placeAdapter=new PlaceAdapter(placeList);
        binding.mylocRecyclerView.setAdapter(placeAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
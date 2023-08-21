package com.sedatkaganaktas.instaclonejava.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.function.DoubleToLongFunction;

@Entity
public class Place implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "latitude")
    public Double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;

    public Place(String name, Double latitude, Double longitude){

        this.name=name;
        this.latitude= latitude;
        this.longitude=longitude;

    }
}

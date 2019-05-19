package com.baiz.myfavoriteplaces;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

public class DataBase extends SQLiteOpenHelper {
    // version
    public static final int DATABASE_VERSION = 3;
    // le nom de la base de donnee
    public static final String DATABASE_NAME = "myfavoriteplaces.db";
    public static final String TABLE_NAME = "places";

    // query sql pour creer la table places
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE  places( id integer primary key autoincrement ,name TEXT ,adresse TEXT , category TEXT  " +
                    ", description TEXT,createAt TEXT, image TEXT )";

    // query sql pour suppremer la table places
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS places " ;

    // constructeur
    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void addPlace( Place place )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "insert into places(name,adresse,category,description,createAt,image) values(?,?,?,?,?,?)";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1,place.getName());
        statement.bindString(2,place.getAdresse());
        statement.bindString(3,place.getCategory());
        statement.bindString(4,place.getDescription());
        statement.bindString(5,place.getCreateAt());
        statement.bindString(6,place.getImage());
        statement.executeInsert();
    }

    public ArrayList<Place> getAllPlaces()
    {
        ArrayList<Place> places = new ArrayList<Place>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from places ORDER BY name COLLATE NOCASE ASC",null);
        res.moveToFirst();

        while (res.isAfterLast()==false)
        {
            Place place = new Place();
            place.setId(res.getInt(0));
            place.setName(res.getString(1));
            place.setAdresse(res.getString(2));
            place.setCategory(res.getString(3));
            place.setDescription(res.getString(4));
            place.setCreateAt(res.getString(5));
            place.setImage(res.getString(6));
            places.add(place);
            res.moveToNext();
        }
        return places;
    }

    public ArrayList<Place> getPlacesByCategory(String category)
    {
        ArrayList<Place> places = new ArrayList<Place>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from places  where category='"+category+"' ORDER BY name COLLATE NOCASE ASC",null);
        res.moveToFirst();

        while (res.isAfterLast()==false)
        {
            Place place = new Place();
            place.setId(res.getInt(0));
            place.setName(res.getString(1));
            place.setAdresse(res.getString(2));
            place.setCategory(res.getString(3));
            place.setDescription(res.getString(4));
            place.setCreateAt(res.getString(5));
            place.setImage(res.getString(6));
            places.add(place);
            res.moveToNext();
        }
        return places;
    }
    public ArrayList<Place> getPlacesByName(String name)
    {
        ArrayList<Place> places = new ArrayList<Place>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from places  where name like  '%"+name+"%' ORDER BY name COLLATE NOCASE ASC",null);
        res.moveToFirst();

        while (res.isAfterLast()==false)
        {
            Place place = new Place();
            place.setId(res.getInt(0));
            place.setName(res.getString(1));
            place.setAdresse(res.getString(2));
            place.setCategory(res.getString(3));
            place.setDescription(res.getString(4));
            place.setCreateAt(res.getString(5));
            place.setImage(res.getString(6));
            places.add(place);
            res.moveToNext();
        }
        return places;
    }

    public void updatePlaace(Place place)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",place.getName());
        contentValues.put("adresse",place.getAdresse());
        contentValues.put("category",place.getCategory());
        contentValues.put("description", place.getDescription());
        contentValues.put("image", place.getImage());
        db.update("places", contentValues, "id = ?",new String[] {String.valueOf(place.getId())});

    }


    public void deleteAllPlaces()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete  from places");

    }

    public void deletePlace(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete  from places where id="+id);

    }
}


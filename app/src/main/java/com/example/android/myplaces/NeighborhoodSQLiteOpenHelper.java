package com.example.android.myplaces;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by David on 2/7/2016.
 */
public class NeighborhoodSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "NEIGHBORHOOD_DB";
    public static final String PLACES_TABLE_NAME = "PLACES";
    public static final String REVIEWS_TABLE_NAME = "REVIEWS";

    public static final String COL_PLACE_ID = "_id";
    public static final String COL_PLACE_NAME = "NAME";
    public static final String COL_PLACE_TYPE = "TYPE";
    public static final String COL_PLACE_LOCATION = "LOCATION";
    public static final String COL_PLACE_BOROUGH = "BOROUGH";
    public static final String COL_PLACE_DESCRIPTION = "DESCRIPTION";
    public static final String COL_PLACE_FAVORITE = "FAVORITE";
    public static final String COL_PLACE_IMAGE = "IMAGE";
    public static final String COL_PLACE_SMALL_IMAGE = "SMALL_IMAGE";


    public static final String COL_REVIEW_ID = "_id";
    public static final String COL_REVIEW_REVIEWER = "REVIEWER";
    public static final String COL_REVIEW_RATING = "RATING";
    public static final String COL_REVIEW_COMMENT = "COMMENT";
    public static final String COL_REVIEW_PLACE = "PLACE";

    public static final String[] PLACES_COLUMNS = {COL_PLACE_ID, COL_PLACE_NAME, COL_PLACE_TYPE, COL_PLACE_LOCATION, COL_PLACE_BOROUGH, COL_PLACE_DESCRIPTION, COL_PLACE_FAVORITE, COL_PLACE_IMAGE, COL_PLACE_SMALL_IMAGE};

    public static final String[] REVIEWS_COLUMNS = {COL_REVIEW_ID, COL_REVIEW_REVIEWER, COL_REVIEW_RATING, COL_REVIEW_COMMENT, COL_REVIEW_PLACE};

    private static final String CREATE_PLACES_TABLE_NAME =
            "CREATE TABLE " + PLACES_TABLE_NAME +
                    "(" +
                    COL_PLACE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_PLACE_NAME + " TEXT, " +
                    COL_PLACE_TYPE + " TEXT, " +
                    COL_PLACE_LOCATION + " TEXT, " +
                    COL_PLACE_BOROUGH + " TEXT, " +
                    COL_PLACE_DESCRIPTION + " TEXT,  " +
                    COL_PLACE_FAVORITE + " TEXT, " +
                    COL_PLACE_IMAGE + " BLOB, " +
                    COL_PLACE_SMALL_IMAGE + " BLOB)";

    private static final String CREATE_REVIEWS_TABLE_NAME =
            "CREATE TABLE " + REVIEWS_TABLE_NAME +
                    "(" +
                    COL_REVIEW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_REVIEW_REVIEWER + " TEXT, " +
                    COL_REVIEW_RATING + " REAL, " +
                    COL_REVIEW_COMMENT + " TEXT, " +
                    COL_REVIEW_ID + " INTEGER)";

    private static NeighborhoodSQLiteOpenHelper mInstance;

    public static NeighborhoodSQLiteOpenHelper getmInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NeighborhoodSQLiteOpenHelper(context);
        }

        return mInstance;
    }

    private NeighborhoodSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PLACES_TABLE_NAME);
        db.execSQL(CREATE_REVIEWS_TABLE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PLACES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + REVIEWS_TABLE_NAME);
        this.onCreate(db);
    }

    public Cursor getAllPlaces() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(PLACES_TABLE_NAME, // a. table
                new String[]{COL_PLACE_ID, COL_PLACE_NAME, COL_PLACE_TYPE, COL_PLACE_LOCATION, COL_PLACE_BOROUGH, COL_PLACE_DESCRIPTION, COL_PLACE_FAVORITE}, // b. column names
                null, // c. selections
                null, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        return cursor;
    }

    public Cursor searchPlace(String query) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(PLACES_TABLE_NAME, // a. table
                new String[]{COL_PLACE_ID, COL_PLACE_NAME, COL_PLACE_TYPE, COL_PLACE_BOROUGH}, // b. column names
                COL_PLACE_NAME + " LIKE ? OR " + COL_PLACE_TYPE + " LIKE ? OR " + COL_PLACE_BOROUGH + " LIKE ?", // c. selections
                new String[]{"%" + query + "%", "%" + query + "%", "%" + query + "%"}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        cursor.moveToFirst();
        return cursor;
    }

    public String getPlaceDetails(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(PLACES_TABLE_NAME, // a. table//
                new String[]{COL_PLACE_NAME, COL_PLACE_TYPE, COL_PLACE_LOCATION, COL_PLACE_BOROUGH, COL_PLACE_DESCRIPTION, COL_PLACE_FAVORITE}, // b. column names
                COL_PLACE_ID + " = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(COL_PLACE_NAME))
                    + "\n" + cursor.getString(cursor.getColumnIndex(COL_PLACE_TYPE))
                    + "\n" + cursor.getString(cursor.getColumnIndex(COL_PLACE_LOCATION))
                    + "\n" + cursor.getString(cursor.getColumnIndex(COL_PLACE_BOROUGH));
        }

        return "Description not found.";
    }

    public String getPlaceName(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(PLACES_TABLE_NAME, // a. table
                new String[]{COL_PLACE_NAME}, // b. column names
                COL_PLACE_ID + " = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(COL_PLACE_NAME));
        }

        return "Description not found.";
    }

    public String getPlaceLocation(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(PLACES_TABLE_NAME, // a. table
                new String[]{COL_PLACE_LOCATION}, // b. column names
                COL_PLACE_ID + " = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(COL_PLACE_LOCATION));
        }

        return "Description not found.";
    }

    public String getPlaceDescription(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(PLACES_TABLE_NAME, // a. table
                new String[]{COL_PLACE_DESCRIPTION}, // b. column names
                COL_PLACE_ID + " = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(COL_PLACE_DESCRIPTION));
        }

        return "Description not found.";
    }

    public byte[] getPlaceImage(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(PLACES_TABLE_NAME, // a. table
                new String[]{COL_PLACE_IMAGE}, // b. column names
                COL_PLACE_ID + " = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor.moveToFirst()) {
            return cursor.getBlob(cursor.getColumnIndex(COL_PLACE_IMAGE));
        }

        return new byte[0];
    }

    public byte[] getPlaceSmallImage(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(PLACES_TABLE_NAME, // a. table
                new String[]{COL_PLACE_SMALL_IMAGE}, // b. column names
                COL_PLACE_ID + " = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor.moveToFirst()) {
            return cursor.getBlob(cursor.getColumnIndex(COL_PLACE_SMALL_IMAGE));
        }

        return null;
    }

    public int isPlaceFavorite(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(PLACES_TABLE_NAME, // a. table
                new String[]{COL_PLACE_FAVORITE}, // b. column names
                COL_PLACE_ID + " = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor.moveToFirst()) {
            return cursor.getInt(cursor.getColumnIndex(COL_PLACE_FAVORITE));
        }

        return -1;
    }

    public Cursor getFavorites() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(PLACES_TABLE_NAME, // a. table
                PLACES_COLUMNS, // b. column names
                COL_PLACE_FAVORITE + " = 1", // c. selections
                null, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        return cursor;
    }

    public Cursor getPlaceReviews(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(REVIEWS_TABLE_NAME, // a. table
                REVIEWS_COLUMNS, // b. column names
                COL_REVIEW_PLACE + " = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        return cursor;
    }

    public float getAverageReview(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT AVG(" + COL_REVIEW_RATING + ") FROM " +
                REVIEWS_TABLE_NAME + " WHERE " + COL_REVIEW_PLACE + " = " + id, null);

        if (cursor.moveToFirst()) {
            return cursor.getFloat(0);
        }

        return 0;
    }
}
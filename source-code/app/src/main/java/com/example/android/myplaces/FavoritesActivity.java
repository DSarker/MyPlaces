package com.example.android.myplaces;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class FavoritesActivity extends AppCompatActivity {

    private ListView mFavoritesListView;
    private CursorAdapter mCursorAdapter;
    private NeighborhoodSQLiteOpenHelper mHelper;
    private int mCursorPositionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        DBAssetHelper dbSetup = new DBAssetHelper(FavoritesActivity.this);
        dbSetup.getReadableDatabase();

        mFavoritesListView = (ListView) findViewById(R.id.favorites_listview);
        mHelper = NeighborhoodSQLiteOpenHelper.getmInstance((FavoritesActivity.this));

        Cursor cursor = mHelper.getFavorites();

        mCursorAdapter = new CursorAdapter(FavoritesActivity.this, cursor, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.main_listview_item_layout, parent, false);

            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView placeNameTextView = (TextView) view.findViewById(R.id.listview_item_name);
                ImageView placeImageView = (ImageView) view.findViewById(R.id.listview_item_imageview);
                RatingBar ratingBar = (RatingBar) view.findViewById(R.id.listview_item_ratingbar);
                TextView numOfReviewsTextView = (TextView) view.findViewById(R.id.listview_item_reviews_textview);

                Bitmap bmp;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable = true;
                bmp = BitmapFactory.decodeByteArray(mHelper.getPlaceSmallImage(cursor.getPosition()+1), 0, mHelper.getPlaceSmallImage(cursor.getPosition()+1).length, options);

                placeImageView.setImageBitmap(bmp);
                placeNameTextView.setText(cursor.getString(cursor.getColumnIndex(NeighborhoodSQLiteOpenHelper.COL_PLACE_NAME)));
                ratingBar.setRating(mHelper.getAverageReview(cursor.getPosition() + 1));
                numOfReviewsTextView.setText("(" + mHelper.getPlaceReviews(cursor.getPosition() + 1).getCount() + ")");
            }
        };

        mFavoritesListView.setAdapter(mCursorAdapter);

        mFavoritesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailsIntent = new Intent(FavoritesActivity.this, DetailsActivity.class);
                Cursor cursor = mCursorAdapter.getCursor();

                cursor.moveToPosition(position);
                int cursorPositionID = cursor.getInt(cursor.getColumnIndex((NeighborhoodSQLiteOpenHelper.COL_PLACE_ID)));

                detailsIntent.putExtra("ID", cursorPositionID);
                startActivity(detailsIntent);
            }
        });

        mFavoritesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = mCursorAdapter.getCursor();
                cursor.moveToPosition(position);
                mCursorPositionID = cursor.getInt(cursor.getColumnIndex((NeighborhoodSQLiteOpenHelper.COL_PLACE_ID)));
                FavoritesActivity.this.showPopUpView(view);
                return true;
            }
        });


    }

    // Popup Menu
    public void showPopUpView(View view) {
        PopupMenu popUp = new PopupMenu(this, view);
        popUp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.popup_action) {
                    Toast.makeText(FavoritesActivity.this, "Removed " + mHelper.getPlaceName(mCursorPositionID) + " from favorites", Toast.LENGTH_SHORT).show();
                    SQLiteDatabase db = mHelper.getWritableDatabase();
                    db.execSQL("UPDATE " + mHelper.PLACES_TABLE_NAME + " SET " +
                            mHelper.COL_PLACE_FAVORITE + " = 0 WHERE " + mHelper.COL_PLACE_ID + " = " + mCursorPositionID);

                    Cursor cursor = mHelper.getFavorites();
                    mCursorAdapter.changeCursor(cursor);

                }
                return false;
            }
        });
        MenuInflater inflater = popUp.getMenuInflater();
        inflater.inflate(R.menu.popup, popUp.getMenu());
        popUp.show();
    }
}

package com.example.android.myplaces;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ListView mPlacesListView;
    private CursorAdapter mCursorAdapter;
    private NeighborhoodSQLiteOpenHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBAssetHelper dbSetup = new DBAssetHelper(MainActivity.this);
        dbSetup.getReadableDatabase();

        mPlacesListView = (ListView) findViewById(R.id.main_listview);
        mHelper = NeighborhoodSQLiteOpenHelper.getmInstance((MainActivity.this));

        Cursor cursor = mHelper.getAllPlaces();

        mCursorAdapter = new CursorAdapter(MainActivity.this, cursor, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.main_listview_item_layout, parent, false);

            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView placeNameTextView = (TextView) view.findViewById(R.id.listview_item_name);
                ImageView placeImageView = (ImageView) view.findViewById(R.id.listview_item_imageview);
                RatingBar placeRatingBar = (RatingBar) view.findViewById(R.id.listview_item_ratingbar);
                TextView placeNumOfReviews = (TextView) view.findViewById(R.id.listview_item_reviews_textview);

                Bitmap bmp;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable = true;
                bmp = BitmapFactory.decodeByteArray(mHelper.getPlaceSmallImage(cursor.getPosition() + 1), 0, mHelper.getPlaceSmallImage(cursor.getPosition() + 1).length, options);

                placeImageView.setImageBitmap(bmp);
                placeNameTextView.setText(cursor.getString(cursor.getColumnIndex(NeighborhoodSQLiteOpenHelper.COL_PLACE_NAME)));
                placeRatingBar.setRating(mHelper.getAverageReview(cursor.getPosition() + 1));
                placeNumOfReviews.setText("(" + mHelper.getPlaceReviews(cursor.getPosition() + 1).getCount() + ")");
            }
        };

        mPlacesListView.setAdapter(mCursorAdapter);

        mPlacesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
                Cursor cursor = mCursorAdapter.getCursor();

                cursor.moveToPosition(position);
                int cursorPositionID = cursor.getInt(cursor.getColumnIndex((NeighborhoodSQLiteOpenHelper.COL_PLACE_ID)));

                detailsIntent.putExtra("ID", cursorPositionID);
                startActivity(detailsIntent);
            }
        });

        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Cursor cursor = NeighborhoodSQLiteOpenHelper.getmInstance(MainActivity.this).searchPlace(query);
                mCursorAdapter.changeCursor(cursor);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.favorites) {
            Intent favoriteIntent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(favoriteIntent);
        }
        return true;
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Cursor cursor = mHelper.getAllPlaces();
//        mCursorAdapter.swapCursor(cursor);
//        mCursorAdapter.notifyDataSetChanged();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Cursor cursor = mHelper.getAllPlaces();
//        mCursorAdapter.swapCursor(cursor);
//        mCursorAdapter.notifyDataSetChanged();
//    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Cursor cursor = mHelper.getAllPlaces();
            mCursorAdapter.changeCursor(cursor);
            mCursorAdapter.notifyDataSetChanged();
        }
    }
}

package com.example.android.myplaces;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsActivity extends AppCompatActivity {

    private ListView mReviewsListView;
    private CursorAdapter mCursorAdapter;
    private NeighborhoodSQLiteOpenHelper mHelper;
    private FloatingActionButton addCommentFloatingButton;
    private ImageButton mAddFavoritesButton;
    private Boolean mIsFavoriteFlag;
    private RatingBar mRatingBar;
    private TextView mNumOfReviewsTextView;
    private int mIdOfPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mHelper = NeighborhoodSQLiteOpenHelper.getmInstance(DetailsActivity.this);

        ImageView coverImageView = (ImageView) findViewById(R.id.details_cover_imageview);
        TextView nameTextView = (TextView) findViewById(R.id.details_name_textview);
        TextView locationTextView = (TextView) findViewById(R.id.details_location_textview);
        TextView descriptionTextView = (TextView) findViewById(R.id.datails_description_textview);
        mRatingBar = (RatingBar) findViewById(R.id.details_ratingbar);
        mNumOfReviewsTextView = (TextView) findViewById(R.id.details_num_reviews_textview);
        mAddFavoritesButton = (ImageButton) findViewById(R.id.add_favorites_button);


        mIdOfPlace = getIntent().getIntExtra("ID", -1);

        nameTextView.setText(mHelper.getPlaceName(mIdOfPlace));
        locationTextView.setText(mHelper.getPlaceLocation(mIdOfPlace));
        descriptionTextView.setText(mHelper.getPlaceDescription(mIdOfPlace));
        mRatingBar.setRating(mHelper.getAverageReview(mIdOfPlace));
        mNumOfReviewsTextView.setText("(" + mHelper.getPlaceReviews(mIdOfPlace).getCount() + ")");

        Bitmap bmp;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        bmp = BitmapFactory.decodeByteArray(mHelper.getPlaceImage(mIdOfPlace), 0, mHelper.getPlaceImage(mIdOfPlace).length, options);
        coverImageView.setImageBitmap(bmp);


        if (mHelper.isPlaceFavorite(mIdOfPlace) == 0) {
            mIsFavoriteFlag = false;
            mAddFavoritesButton.setImageResource(R.drawable.ic_star_empty_50);
        } else {
            mIsFavoriteFlag = true;
            mAddFavoritesButton.setImageResource(R.drawable.ic_star_filled_50);
        }

        mAddFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = mHelper.getWritableDatabase();
                mIsFavoriteFlag = !mIsFavoriteFlag;

                if (mIsFavoriteFlag) {
                    db.execSQL("UPDATE " + mHelper.PLACES_TABLE_NAME + " SET " +
                            mHelper.COL_PLACE_FAVORITE + " = 1 WHERE " + mHelper.COL_PLACE_ID + " = " + mIdOfPlace);
                    mAddFavoritesButton.setImageResource(R.drawable.ic_star_filled_50);
                    Toast.makeText(DetailsActivity.this, "Added to favorites", Toast.LENGTH_SHORT);
                } else {
                    db.execSQL("UPDATE " + mHelper.PLACES_TABLE_NAME + " SET " +
                            mHelper.COL_PLACE_FAVORITE + " = 0 WHERE " + mHelper.COL_PLACE_ID + " = " + mIdOfPlace);
                    mAddFavoritesButton.setImageResource(R.drawable.ic_star_empty_50);
                    Toast.makeText(DetailsActivity.this, "Removed from favorites", Toast.LENGTH_SHORT);
                }
            }
        });

        addCommentFloatingButton = (FloatingActionButton) findViewById(R.id.add_comment_floatingbutton);
        addCommentFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this, ReviewActivity.class);
                intent.putExtra("ID", mIdOfPlace);
                startActivity(intent);
            }
        });

        mReviewsListView = (ListView) findViewById(R.id.details_comments_recyclerview);
        Cursor cursorReview = mHelper.getPlaceReviews(mIdOfPlace);
        mCursorAdapter = new CursorAdapter(DetailsActivity.this, cursorReview, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.review_listview_item_layout, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView reviewer = (TextView) view.findViewById(R.id.review_listitem_name_textview);
                RatingBar ratingBar = (RatingBar) view.findViewById(R.id.review_listitem_ratingbar);
                TextView comment = (TextView) view.findViewById(R.id.review_listitem_comment_textview);

                reviewer.setText(cursor.getString(cursor.getColumnIndex(mHelper.COL_REVIEW_REVIEWER)));
                ratingBar.setRating(cursor.getFloat(cursor.getColumnIndex(mHelper.COL_REVIEW_RATING)));
                comment.setText(cursor.getString(cursor.getColumnIndex(mHelper.COL_REVIEW_COMMENT)));
            }
        };

        mReviewsListView.setAdapter(mCursorAdapter);
    }

    @Override
    protected void onResume() {
        mRatingBar.setRating(mHelper.getAverageReview(mIdOfPlace));
        mNumOfReviewsTextView.setText("(" + mHelper.getPlaceReviews(mIdOfPlace).getCount() + ")");
        Cursor cursor = mHelper.getPlaceReviews(mIdOfPlace);
        mCursorAdapter.changeCursor(cursor);
        super.onResume();
    }
}

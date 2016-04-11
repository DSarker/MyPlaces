package com.example.android.myplaces;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

public class ReviewActivity extends AppCompatActivity {
    private NeighborhoodSQLiteOpenHelper mHelper;
    EditText mNameTextView;
    RatingBar mRatingBar;
    EditText mCommentTextView;
    Button mAddReviewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        mNameTextView = (EditText) findViewById(R.id.review_activity_name);
        mRatingBar = (RatingBar) findViewById(R.id.review_activity_ratingbar);
        mCommentTextView = (EditText) findViewById(R.id.review_activity_comment);
        mAddReviewButton = (Button) findViewById(R.id.review_activity_button);

        mHelper = NeighborhoodSQLiteOpenHelper.getmInstance(ReviewActivity.this);

        mAddReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = mHelper.getWritableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put(mHelper.COL_REVIEW_REVIEWER, mNameTextView.getText().toString());
                contentValues.put(mHelper.COL_REVIEW_RATING, mRatingBar.getRating());
                contentValues.put(mHelper.COL_REVIEW_COMMENT, mCommentTextView.getText().toString());
                contentValues.put(mHelper.COL_REVIEW_PLACE, getIntent().getIntExtra("ID", 0));

                db.insert(mHelper.REVIEWS_TABLE_NAME, null, contentValues);
                finish();
            }
        });


    }
}

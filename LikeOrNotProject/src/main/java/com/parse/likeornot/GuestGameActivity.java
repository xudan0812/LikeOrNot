package com.parse.likeornot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import tgio.parselivequery.BaseQuery;
import tgio.parselivequery.LiveQueryEvent;
import tgio.parselivequery.Subscription;
import tgio.parselivequery.interfaces.OnListener;

public class GuestGameActivity extends AppCompatActivity {

    String likeOrNot;
    ImageView imageView2;
    ParseObject curImage;
    ParseObject like;
    TextView userAnswerTextView;
    TextView guestAnswerTextView;
    String inviterName;
    CountDownTimer countDownTimer;
    int rightCount = 0;
    int sumCount = 0;
    TextView guestScoreTextView;
    TextView guestTimeTextView;
    boolean can_getImage;
    Bitmap bitmap;
    String userLike;
    String likeObjectId;
    String imageId;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logOut) {
            ParseUser.getCurrentUser().logOut();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class );
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveAnswer2 (final String likeOrNot) {
        userAnswerTextView.setText("User: " + userLike);
        guestAnswerTextView.setText("Guest: " + likeOrNot);
        if (likeOrNot.equals(userLike)) {
            Toast.makeText(this, "Right", Toast.LENGTH_SHORT).show();
            rightCount++;
        } else {
            Toast.makeText(this, "Wrong", Toast.LENGTH_SHORT).show();
        }
        sumCount++;
        guestScoreTextView.setText(rightCount + "/" + sumCount);
        final ParseQuery<ParseObject> findLikeQuery = new ParseQuery<>("Like");
        findLikeQuery.whereEqualTo("objectId", likeObjectId);
        findLikeQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> likes, ParseException e) {
                if (e == null && likes.size() > 0) {
                    like = likes.get(0);
                    like.put("guestId", ParseUser.getCurrentUser().getUsername());
                    like.put("guestLike", likeOrNot);
                    like.saveInBackground();
                }
            }
        });
        can_getImage = true;
        //imageView2.setImageResource(android.R.color.transparent);
        getImage2();
    }

    public void likeButtonClicked(View view) {
        likeOrNot = "like";
        saveAnswer2(likeOrNot);
    }

    public void dislikeButtonClicked(View view) {
        likeOrNot = "dislike";
        saveAnswer2(likeOrNot);
    }

    public class Checker4 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            final Subscription sub = new BaseQuery.Builder("Like")
                    //.where("imageId", curImage.getObjectId())
                    .where("userId", inviterName)
                    .addField("imageId")
                    .addField("userLike")
                    .build()
                    .subscribe();

            sub.on(LiveQueryEvent.CREATE, new OnListener() {
                @Override
                public void on(final JSONObject object) {
                    sub.unsubscribe();
                    can_getImage = false;
                    Log.i("object", object.toString());

                    try {
                        String s = object.getString("object");
                        JSONObject jsonObject = new JSONObject(s);
                        imageId = jsonObject.getString("imageId");
                        userLike = jsonObject.getString("userLike");
                        likeObjectId = jsonObject.getString("objectId");
                        ParseQuery<ParseObject> findImageQuery = new ParseQuery<ParseObject>("Image");
                        findImageQuery.whereEqualTo("objectId", imageId);
                        findImageQuery.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> images, ParseException e) {
                                if (e == null && images.size() > 0) {
                                    final ParseObject image = images.get(0);
                                    ParseFile file = image.getParseFile("image");
                                    file.getDataInBackground(new GetDataCallback() {
                                        @Override
                                        public void done(byte[] data, ParseException e) {
                                            if (e == null) {
                                                if (data != null) {
                                                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            likeOrNot = null;
                                                            userAnswerTextView.setText("User: ");
                                                            guestAnswerTextView.setText("Guest: ");
                                                            imageView2.setImageBitmap(bitmap);
                                                        }
                                                    });

                                                } else {
                                                    Log.i("get data", "data is null ");
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            return null;
        }
    }



    public void getImage2() {
        if (can_getImage) {
            Toast.makeText(GuestGameActivity.this, "Waiting for your friend to give an answer", Toast.LENGTH_SHORT).show();
            Checker4 checker4 = new Checker4();
            checker4.execute();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_game);
        getWindow().getDecorView().setBackgroundColor(Color.rgb(251,210,26));
        setTitle("Friend Test - Guest");
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        Button likeButton = (Button) findViewById(R.id.likeButton2);
        Button dislikeButton = (Button) findViewById(R.id.dislikeButton2);
        userAnswerTextView = (TextView) findViewById(R.id.userAnswerTextView);
        guestAnswerTextView = (TextView) findViewById(R.id.guestAnswerTextView);
        guestScoreTextView = (TextView) findViewById(R.id.guestScoreTextView);
        guestTimeTextView = (TextView) findViewById(R.id.guestTimeTextView);
        Intent intent = getIntent();
        inviterName = intent.getStringExtra("inviterName");
        can_getImage = true;
        getImage2();

        countDownTimer = new CountDownTimer(60000 + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                guestTimeTextView.setText(String.valueOf(millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                guestTimeTextView.setText("0s");
                can_getImage = false;
                Intent intent = new Intent(getApplicationContext(), playAgainActivity.class);
                intent.putExtra("rightCount", rightCount );
                intent.putExtra("sumCount", sumCount);
                intent.putExtra("userOrGuest", "guest");
                intent.putExtra("inviterName", inviterName);
                intent.putExtra("guestName", ParseUser.getCurrentUser().getUsername());
                startActivity(intent);
            }
        }.start();


    }


}

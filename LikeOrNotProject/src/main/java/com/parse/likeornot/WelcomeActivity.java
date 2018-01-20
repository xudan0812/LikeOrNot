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

public class WelcomeActivity extends AppCompatActivity{
    String likeOrNot = null;
    ImageView imageView;
    ParseObject curImage;
    ParseUser curUser = ParseUser.getCurrentUser();
    TextView userAnswerTextView;
    TextView guestAnswerTextView;
    TextView timeTextView;
    TextView scoreTextView;
    String guestName;
    boolean can_getImage;
    CountDownTimer countDownTimer;
    int rightCount = 0;
    int sumCount = 0;
    String guestAnswer;
    Bitmap bitmap;
    boolean game_is_over;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logOut) {
            curUser.logOut();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class );
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public class Checker extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (!game_is_over) {
                final Subscription sub = new BaseQuery.Builder("Like")
                        //.where("imageId", curImage.getObjectId())
                        .where("userId", curUser.getUsername())
                        .addField("guestLike")
                        .addField("userLike")
                        .build()
                        .subscribe();

                sub.on(LiveQueryEvent.UPDATE, new OnListener() {
                    @Override
                    public void on(final JSONObject object) {
                        sub.unsubscribe();
                        Log.i("object", object.toString());
                        //Log.i("information" , curImage.getObjectId()+ " " + curUser.getUsername());
                        // Action to be executed here when an object that matches
                        // The filter you set up
                        try {
                            String s = object.getString("object");
                            //Log.i("s", s);
                            JSONObject jsonObject = new JSONObject(s);
                            guestAnswer = jsonObject.getString("guestLike");
                            publishProgress();
                            can_getImage = true;
                            getImage();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Toast.makeText(WelcomeActivity.this, "JSON error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            if (guestAnswer != null) {
                guestAnswerTextView.setText("Guest: " + guestAnswer);
                if (likeOrNot.equals(guestAnswer)) {
                    Toast.makeText(WelcomeActivity.this, "Right", Toast.LENGTH_SHORT).show();
                    rightCount++;
                } else {
                    Toast.makeText(WelcomeActivity.this, "Wrong", Toast.LENGTH_SHORT).show();
                }
                sumCount++;
                scoreTextView.setText(rightCount + " / " + sumCount);
            }
        }

    }

    public void saveAnswer (final String likeOrNot) {
        curImage.addUnique("userArray", curUser.getUsername());
        curImage.saveInBackground();
        Log.i("save", "successful");
        ParseQuery<ParseObject> createLikeQuery  = new ParseQuery<ParseObject>("Like");
        createLikeQuery.whereEqualTo("userId", curUser.getUsername());
        createLikeQuery.whereEqualTo("imageId", curImage.getObjectId());
        createLikeQuery.whereExists("userLike");
        createLikeQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> likes, ParseException e) {
                if (e == null && likes.size() == 0) {
                    ParseObject like = new ParseObject("Like");
                    like.put("imageId", curImage.getObjectId());
                    like.put("userLike", likeOrNot);
                    like.put("userId", curUser.getUsername());
                    like.saveInBackground();
                }
            }
        });
        Checker checker = new Checker();
        checker.execute();
    }

    public void likeButtonClicked(View view) {
        if (curImage != null) {
            likeOrNot = "like";
            userAnswerTextView.setText("User: like");
            saveAnswer(likeOrNot);
        }
    }

    public void dislikeButtonClicked(View view) {
        if (curImage != null) {
            likeOrNot = "dislike";
            userAnswerTextView.setText("User: dislike");
            saveAnswer(likeOrNot);
        }
    }

    public void getImage() {
        if (can_getImage && !game_is_over) {

            ParseQuery<ParseObject> query = new ParseQuery<>("Image");
            query.whereNotEqualTo("userArray", curUser.getUsername());
            query.addAscendingOrder("createdAt");
            query.setLimit(1);

            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> images, ParseException e) {
                    if (e == null && images.size() > 0) {
                        likeOrNot = null;
                        userAnswerTextView.setText("User: ");
                        guestAnswerTextView.setText("Guest: ");
                        curImage = images.get(0);
                        Log.i("user get image id", curImage.getObjectId());
                        ParseFile file = curImage.getParseFile("image");
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null && data != null) {
                                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    imageView.setImageBitmap(bitmap);
                                    can_getImage = false;
                                }
                            }
                        });
                    } else {
                        Log.i("get image", "size = 0");
                        Toast.makeText(WelcomeActivity.this, "No image", Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("sequence", "on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getWindow().getDecorView().setBackgroundColor(Color.rgb(251,210,26));
        setTitle("Friend Test - User");
        Intent intent = getIntent();
        guestName = intent.getStringExtra("guestName");
        imageView = (ImageView) findViewById(R.id.imageView);
        Button likeButton = (Button) findViewById(R.id.likeButton2);
        Button dislikeButton = (Button) findViewById(R.id.dislikeButton2);
        //Button nextImageButton = (Button) findViewById(R.id.nextImageButton);
        //nextImageButton.setBackgroundColor(Color.rgb(0, 172,238));
        userAnswerTextView = (TextView) findViewById(R.id.userAnswerTextView);
        guestAnswerTextView = (TextView) findViewById(R.id.guestAnswerTextView);
        userAnswerTextView.setText("User: ");
        guestAnswerTextView.setText("Guest: ");
        scoreTextView = (TextView) findViewById(R.id.scoreTextView);
        scoreTextView.setText(rightCount + " / " + sumCount);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        can_getImage = true;
        game_is_over = false;
        getImage();

        countDownTimer = new CountDownTimer(60000 + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                //Log.i("time untill done", String.valueOf(millisUntilFinished));
                timeTextView.setText(String.valueOf(millisUntilFinished / 1000) + "s");

            }

            @Override
            public void onFinish() {
                timeTextView.setText("0s");
                //checkResultTextView.setText("Final score : " + scoreTextView.getText());
                can_getImage = false;
                game_is_over = true;
                Intent intent = new Intent(getApplicationContext(), playAgainActivity.class);
                //intent.putExtra("finalScore", rightCount + " / " + sumCount);
                intent.putExtra("rightCount", rightCount );
                intent.putExtra("sumCount", sumCount);
                intent.putExtra("userOrGuest", "user");
                intent.putExtra("guestName",guestName );
                intent.putExtra("inviterName", ParseUser.getCurrentUser().getUsername());
                startActivity(intent);
            }
        }.start();

    }

    @Override
    protected void onResume() {
        Log.i("sequence", "on resume");
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        Log.i("sequence", "on newintent");
        super.onNewIntent(intent);
        userAnswerTextView.setText("User: ");
        guestAnswerTextView.setText("Guest: ");
        rightCount = 0;
        sumCount = 0;
        guestName = intent.getStringExtra("guestName");
        scoreTextView.setText(rightCount + " / " + sumCount);
        game_is_over = false;
        can_getImage = true;
        getImage();
        countDownTimer = new CountDownTimer(10000 + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                //Log.i("time untill done", String.valueOf(millisUntilFinished));
                timeTextView.setText(String.valueOf(millisUntilFinished / 1000) + "s");

            }

            @Override
            public void onFinish() {
                timeTextView.setText("0s");
                //checkResultTextView.setText("Final score : " + scoreTextView.getText());
                can_getImage = false;
                game_is_over = true;
                Intent intent = new Intent(getApplicationContext(), playAgainActivity.class);
                intent.putExtra("rightCount", rightCount );
                intent.putExtra("sumCount", sumCount);
                intent.putExtra("userOrGuest", "user");
                intent.putExtra("guestName",guestName );
                intent.putExtra("inviterName", ParseUser.getCurrentUser().getUsername());
                startActivity(intent);
            }
        }.start();
    }

}

package com.parse.likeornot;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.util.List;

import tgio.parselivequery.BaseQuery;
import tgio.parselivequery.LiveQueryEvent;
import tgio.parselivequery.Subscription;
import tgio.parselivequery.interfaces.OnListener;

public class playAgainActivity extends AppCompatActivity {
    String userOrGuest;
    String guestName;
    String inviterName;
    Button playAgainButton;
    Button exchangeButton;
    Button acceptButton;
    Button denyButton;

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

    public void acceptButtonClicked (View view) {
        Intent intent = new Intent(getApplicationContext(), GuestActivity.class);
        intent.putExtra("from", "playAgain");
        intent.putExtra("inviterName", inviterName);
        startActivity(intent);
    }

    public void denyButtonClicked (View view) {
        ParseQuery<ParseObject> findInviterQuery = new ParseQuery<ParseObject>("Invitation");
        findInviterQuery.whereEqualTo("userName", inviterName);
        findInviterQuery.whereEqualTo("guestName", ParseUser.getCurrentUser().getUsername());
        findInviterQuery.whereDoesNotExist("accept");
        //findInviterQuery.setLimit(1);
        findInviterQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    objects.get(0).put("accept", "no");
                    objects.get(0).saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Toast.makeText(playAgainActivity.this, "Invitation rejected", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "No invitation found, please check your inviter's name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class Checker3 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            final Subscription sub = new BaseQuery.Builder("Invitation")
                    .where("userName", inviterName)
                    .where("guestName", guestName)
                    //.addField("accept")
                    .build()
                    .subscribe();

            sub.on(LiveQueryEvent.CREATE, new OnListener() {
                @Override
                public void on(final JSONObject object) {
                    sub.unsubscribe();

                    Log.i("object", object.toString());
                    //Log.i("information" , curImage.getObjectId()+ " " + curUser.getUsername());
                    // Action to be executed here when an object that matches
                    // The filter you set up
                    runOnUiThread(new Runnable() {
                        //Toast.makeText(InviteFriendActivity.this, "Game will start in 3 seconds", Toast.LENGTH_SHORT).show();
                        @Override
                        public void run() {
                            acceptButton.setVisibility(View.VISIBLE);
                            denyButton.setVisibility(View.VISIBLE);
                            Toast.makeText(playAgainActivity.this, "Your friend invite you to play again, do you accept?", Toast.LENGTH_SHORT).show();
                            //Toast.makeText(InviteFriendActivity.this, "Game will start in 3 seconds", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
            return null;
        }
    }

    public void exchangeClicked (View view) {
        if (userOrGuest.equals("user")) {

            Intent intent = new Intent(getApplicationContext(), InviteFriendActivity.class);
            intent.putExtra("from", "exchange");
            intent.putExtra("guestName", guestName);
            startActivity(intent);

        }
        /*
        else {
            ParseObject invitation = new ParseObject("Invitation");
            invitation.put("userName", ParseUser.getCurrentUser().getUsername());
            invitation.put("guestName", inviterName);
            invitation.saveInBackground();
            Log.i("save", "successful");

            ParseQuery<ParseObject> findInviterQuery = new ParseQuery<ParseObject>("Invitation");
            findInviterQuery.whereEqualTo("userName", guestName);
            findInviterQuery.whereEqualTo("guestName", ParseUser.getCurrentUser().getUsername());
            findInviterQuery.whereDoesNotExist("accept");
            findInviterQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null && objects.size()> 0) {
                        objects.get(0).put("accept","yes");
                        Intent intent = new Intent(getApplicationContext(), GuestGameActivity.class);
                        startActivity(intent);
                    }
                }
            });

        }
        */
    }

    public void playAgainClicked(View view) {
        if (userOrGuest.equals("user")) {
            Intent intent = new Intent(getApplicationContext(), InviteFriendActivity.class);
            intent.putExtra("from", "playAgain");
            intent.putExtra("guestName", guestName);
            startActivity(intent);
        }
        /*
        else {
            ParseQuery<ParseObject> findInviterQuery = new ParseQuery<ParseObject>("Invitation");
            findInviterQuery.whereEqualTo("userName", inviterName);
            findInviterQuery.whereEqualTo("guestName", ParseUser.getCurrentUser().getUsername());
            findInviterQuery.whereDoesNotExist("accept");
            findInviterQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null && objects.size()> 0) {
                        objects.get(0).put("accept","yes");
                        Intent intent = new Intent(getApplicationContext(), GuestGameActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
        */
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_again);
        getWindow().getDecorView().setBackgroundColor(Color.rgb(251,210,26));
        setTitle("Play Again ?");
        TextView scoreTextView = (TextView) findViewById(R.id.scoreTextView);
        TextView percentTextView = (TextView) findViewById(R.id.percentTextView);
        Intent intent = getIntent();
        String finalScore = intent.getStringExtra("finalScore");
        guestName = intent.getStringExtra("guestName");
        inviterName = intent.getStringExtra("inviterName");
        userOrGuest = intent.getStringExtra("userOrGuest");
        playAgainButton = (Button) findViewById(R.id.playAgainButton);
        acceptButton = (Button) findViewById(R.id.acceptButton);
        denyButton = (Button) findViewById(R.id.denyButton);
        //exchangeButton = (Button) findViewById(R.id.exchangeButton);

        if (userOrGuest.equals("guest")) {
            playAgainButton.setVisibility(View.INVISIBLE);
            //exchangeButton.setVisibility(View.INVISIBLE);
            Checker3 checker3 = new Checker3();
            checker3.execute();

        }
        int rightCount = intent.getIntExtra("rightCount", 0);
        int sumCount = intent.getIntExtra("sumCount", 0);
        if (sumCount == 0) {
            Toast.makeText(this, "No answer", Toast.LENGTH_SHORT).show();
        } else {
            scoreTextView.setText(rightCount + " / " + sumCount);
        }
        int percent = (int) Math.round(rightCount * 100.0/ sumCount);
        percentTextView.setText(inviterName + " and " + guestName+ " has " + percent + "% chance to be real friend." );
    }
}

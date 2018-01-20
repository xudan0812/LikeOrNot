package com.parse.likeornot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import tgio.parselivequery.BaseQuery;
import tgio.parselivequery.LiveQueryEvent;
import tgio.parselivequery.Subscription;
import tgio.parselivequery.interfaces.OnListener;


public class InviteFriendActivity extends AppCompatActivity implements View.OnClickListener{
    EditText guestNameEditText;
    String userName;
    String guestName;
    TextView countdownTextView;
    TextView nameTextView;
    Button inviteButton;
    boolean from_play_again;
    ImageView waitingImageView;
    TextView waitingTextView;
    String from;
    TextView helloTextView;

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

    public class Checker2 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            final Subscription sub = new BaseQuery.Builder("Invitation")
                    .where("userName", userName)
                    .where("guestName", guestName)
                    .addField("accept")
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
                    String s = null;
                    try {
                        s = object.getString("object");
                        JSONObject jsonObject = new JSONObject(s);
                        String accept = jsonObject.getString("accept");
                        if (accept!= null) {
                            if (accept.equals("yes")) {
                                runOnUiThread(new Runnable() {
                                    //Toast.makeText(InviteFriendActivity.this, "Game will start in 3 seconds", Toast.LENGTH_SHORT).show();
                                    @Override
                                    public void run() {
                                        startGame();
                                        //Toast.makeText(InviteFriendActivity.this, "Game will start in 3 seconds", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else if (accept.equals("no")){
                                runOnUiThread(new Runnable() {
                                    //Toast.makeText(InviteFriendActivity.this, "Game will start in 3 seconds", Toast.LENGTH_SHORT).show();
                                    @Override
                                    public void run() {
                                        Toast.makeText(InviteFriendActivity.this, "Your friend does not accept the invitation.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }



                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Log.i("s", s);
                }
            });
            return null;
        }
    }

    public void startGame() {
        Toast.makeText(InviteFriendActivity.this, "Game will start in 3 seconds", Toast.LENGTH_SHORT).show();
        waitingImageView.setVisibility(View.INVISIBLE);
        waitingTextView.setVisibility(View.INVISIBLE);
        countdownTextView.setVisibility(View.VISIBLE);

        final CountDownTimer countDownTimer = new CountDownTimer(3000 + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //Log.i("time untill done", String.valueOf(millisUntilFinished));
                countdownTextView.setText(String.valueOf(millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                countdownTextView.setText("0s");
                if (from.equals("exchange")) {
                    Intent intent = new Intent(getApplicationContext(), GuestGameActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    from_play_again = false;
                    intent.putExtra("guestName", guestName);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    from_play_again = false;
                    intent.putExtra("guestName", guestName);
                    startActivity(intent);
                }

            }
        }.start();
    }

    public void inviteClicked(View view) {
        //Log.i("invite", "clicked");
        if (!from_play_again) {
            guestName = guestNameEditText.getText().toString();
            if (guestName.equals(userName)) {
                Toast.makeText(this, "You cannot invite yourself", Toast.LENGTH_SHORT).show();
                guestNameEditText.setText("");
                guestName = guestNameEditText.getText().toString();
            } else if (guestName.equals("")) {
                Toast.makeText(this, "Guest name cannot be empty", Toast.LENGTH_SHORT).show();
            }

        }
        ParseQuery<ParseUser> findGuestQuery = new ParseUser().getQuery();
        findGuestQuery.whereEqualTo("username", guestName);
        findGuestQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() == 0) {
                        Toast.makeText(InviteFriendActivity.this, "Please check your guest's name or invite your guest to sign up", Toast.LENGTH_SHORT).show();
                    } else if (objects.size() > 0){
                        guestNameEditText.setVisibility(View.INVISIBLE);
                        nameTextView.setVisibility(View.INVISIBLE);
                        inviteButton.setVisibility(View.INVISIBLE);
                        helloTextView.setVisibility(View.INVISIBLE);
                        waitingImageView.setVisibility(View.VISIBLE);
                        waitingTextView.setVisibility(View.VISIBLE);
                        ParseObject invitation = new ParseObject("Invitation");
                        invitation.put("userName", userName);
                        invitation.put("guestName", guestName);
                        invitation.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                mgr.hideSoftInputFromWindow(guestNameEditText.getWindowToken(), 0);

                            }
                        });
                        //Toast.makeText(this, "Waiting response from your guest...", Toast.LENGTH_SHORT).show();
                        //Log.i("save", "successful");

                        Checker2 checker2 = new Checker2();
                        checker2.execute();
                    }
                }
            }
        });
        if (!guestName.equals("")) {
            /*
            ParsePush.subscribeInBackground(userName + guestName, new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.i("Push", "subscribed");
                    } else {
                        Log.i("push", "no");
                        e.printStackTrace();
                    }
                }
            });

            ParseQuery pushQuery = ParseInstallation.getQuery();
            // Here you can personalize your pushQuery
            // Add channels as you like, or remove this part to reach everyone

            //LinkedList<String> channels = new LinkedList<String>();
            //channels.add("Everyone");
            // Change this "message" String to change the message sent
            String message = "Back4App says Hi!";
            // In case you want to send data, use this

            JSONObject data = null;
            try {
                data = new JSONObject("{\"rating\": \"5 stars\"}");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ParsePush push = new ParsePush();
            // Set our Installation query
            push.setQuery(pushQuery);
            // Sets the channels
            //push.setChannels(channels);
            // Sets the message
            push.setMessage(message);
            // Sets the data
            //push.setData(data);
            push.sendInBackground(new SendCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.i("Push", "sent");
                    } else {
                        Log.i("push", "no");
                        e.printStackTrace();
                    }
                }
            });
*/
            /*
            // Defines the channels this should listen to
            LinkedList<String> channels = new LinkedList<String>();

            // Pushes channelName
            channels.push(userName + guestName);

            // Defines ParseInstallation
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();

            // Adds the Firebase Sender Key
            installation.put("GCMSenderId", "943965271670");

            // Adds the channel list to the installation
            installation.put("channels", channels);
            installation.saveInBackground();


            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put("user", userName);
            installation.saveInBackground();

            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("guestName", guestName);
            String message = "hello datu";
            params.put("message", message);
            ParseCloud.callFunctionInBackground("sendPushToUser", params, new FunctionCallback<String>() {
                public void done(String success, ParseException e) {
                    if (e == null) {
                        // Push sent successfully
                        Log.i("push", "sent successfully");
                    } else {
                        e.printStackTrace();
                        Log.i("push", "not successfully");
                    }
                }
            });
*/

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);
        getWindow().getDecorView().setBackgroundColor(Color.rgb(251,210,26));
        setTitle("Invite Friend");
        Intent intent = getIntent();

        helloTextView = (TextView) findViewById(R.id.helloTextView);
        helloTextView.setText("Hello " + ParseUser.getCurrentUser().getUsername() + ",\n\nPlease type in your guest's name");
        guestNameEditText = (EditText) findViewById(R.id.guestNameEditText);
        countdownTextView = (TextView) findViewById(R.id.countdownTextView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        inviteButton = (Button) findViewById(R.id.inviteButton);
        waitingImageView = (ImageView) findViewById(R.id.waitingImageView2);
        waitingTextView = (TextView)findViewById(R.id.waitingTextView);
        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);
        constraintLayout.setOnClickListener(this);

        from = intent.getStringExtra("from");
        if (from.equals("playAgain")) {
            //inviteClicked(nameTextView);
            guestNameEditText.setVisibility(View.INVISIBLE);
            nameTextView.setVisibility(View.INVISIBLE);
            inviteButton.setVisibility(View.INVISIBLE);
            helloTextView.setVisibility(View.INVISIBLE);
            waitingImageView.setVisibility(View.VISIBLE);
            waitingTextView.setVisibility(View.VISIBLE);
            from_play_again = true;
            guestName = intent.getStringExtra("guestName");
            userName = ParseUser.getCurrentUser().getUsername();
            inviteClicked(nameTextView);
        } else if (from.equals("exchange")) {
            from_play_again = true;
            userName = intent.getStringExtra("guestName");
            guestName = ParseUser.getCurrentUser().getUsername();
            inviteClicked(nameTextView);
        } else {
            from_play_again = false;
            userName = ParseUser.getCurrentUser().getUsername();
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.constraintLayout && waitingImageView.getVisibility() == View.INVISIBLE) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
    }

}

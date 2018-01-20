package com.parse.likeornot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.List;

public class GuestActivity extends AppCompatActivity implements View.OnClickListener {
    EditText editText;
    String inviterName;
    TextView countdownTextView2;
    ImageView waitingImageView2;
    TextView nameTextView2;
    Button beginButton;
    Button denyButton;
    ConstraintLayout constraintLayout2;
    TextView helloTextView2;

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


    public void denyButtonClicked(View view) {
        inviterName = editText.getText().toString();

        if (inviterName == null) {
            Toast.makeText(this, "Please specify your inviter to deny!", Toast.LENGTH_SHORT).show();
        } else if (inviterName.equals(ParseUser.getCurrentUser().getUsername())) {
            Toast.makeText(this, "You cannot be invited by yourself!", Toast.LENGTH_SHORT).show();
            editText.setText("");
            //guestName = guestNameEditText.getText().toString();
        } else {
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);

            ParseQuery<ParseObject> findInviterQuery = new ParseQuery<ParseObject>("Invitation");
            findInviterQuery.whereEqualTo("userName", inviterName);
            findInviterQuery.whereEqualTo("guestName", ParseUser.getCurrentUser().getUsername());
            findInviterQuery.whereDoesNotExist("accept");
            findInviterQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null && objects.size() > 0) {
                        objects.get(0).put("accept", "no");
                        objects.get(0).saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Toast.makeText(GuestActivity.this, "Invitation rejected", Toast.LENGTH_SHORT).show();
                            }
                        });
                        startGame();
                    } else {
                        Toast.makeText(GuestActivity.this, "No invitation found, please check your inviter's name", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    public void startGame() {
        Toast.makeText(getApplicationContext(), "Game will start in 3 seconds", Toast.LENGTH_SHORT).show();
        //waitingImageView2.setVisibility(View.INVISIBLE);
        denyButton.setVisibility(View.INVISIBLE);
        editText.setVisibility(View.INVISIBLE);
        nameTextView2.setVisibility(View.INVISIBLE);
        beginButton.setVisibility(View.INVISIBLE);
        helloTextView2.setVisibility(View.INVISIBLE);

        //waitingImageView.setVisibility(View.VISIBLE);
        //waitingTextView.setVisibility(View.VISIBLE);
        countdownTextView2.setVisibility(View.VISIBLE);

        final CountDownTimer countDownTimer = new CountDownTimer(3000 + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //Log.i("time untill done", String.valueOf(millisUntilFinished));
                countdownTextView2.setText(String.valueOf(millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                countdownTextView2.setText("0s");
                    Intent intent = new Intent(getApplicationContext(), GuestGameActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("inviterName", inviterName);
                    startActivity(intent);

            }
        }.start();
    }

    public void beginGame(View view) {

        inviterName = editText.getText().toString();

        if (inviterName == null) {
            Toast.makeText(this, "Please specify your inviter", Toast.LENGTH_SHORT).show();
        }else if (inviterName.equals(ParseUser.getCurrentUser().getUsername())) {
            Toast.makeText(this, "You cannot invite yourself!", Toast.LENGTH_SHORT).show();
            editText.setText("");
            //guestName = guestNameEditText.getText().toString();
        } else {
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);

            ParseQuery<ParseObject> findInviterQuery = new ParseQuery<ParseObject>("Invitation");
            findInviterQuery.whereEqualTo("userName", inviterName);
            findInviterQuery.whereEqualTo("guestName", ParseUser.getCurrentUser().getUsername());
            findInviterQuery.whereDoesNotExist("accept");
            findInviterQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null && objects.size() > 0) {
                        objects.get(0).put("accept", "yes");
                        objects.get(0).saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                startGame();
                            }
                        });

                    } else {
                        Toast.makeText(GuestActivity.this, "No invitation found, please check your inviter's name", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);
        getWindow().getDecorView().setBackgroundColor(Color.rgb(251,210,26));
        setTitle("Name Your Inviter");

        helloTextView2 = (TextView) findViewById(R.id.helloTextView2);
        helloTextView2.setText("Hello " + ParseUser.getCurrentUser().getUsername() + ",\n\nPlease type in your inviter's name");
        editText = (EditText) findViewById(R.id.editText);
        waitingImageView2 = (ImageView) findViewById(R.id.waitingImageView2);
        countdownTextView2 = (TextView) findViewById(R.id.countdownTextView2);
        nameTextView2 = (TextView) findViewById(R.id.nameTextView2);
        beginButton = (Button) findViewById(R.id.beginButton);
        denyButton = (Button) findViewById(R.id.denyButton);
        constraintLayout2 = (ConstraintLayout) findViewById(R.id.constraintLayout2);
        constraintLayout2.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent.getStringExtra("from").equals("playAgain")) {
            editText.setVisibility(View.INVISIBLE);
            nameTextView2.setVisibility(View.INVISIBLE);
            beginButton.setVisibility(View.INVISIBLE);
            denyButton.setVisibility(View.INVISIBLE);
            helloTextView2.setVisibility(View.INVISIBLE);
            ParseQuery<ParseObject> findInviterQuery = new ParseQuery<ParseObject>("Invitation");
            inviterName = intent.getStringExtra("inviterName");
            findInviterQuery.whereEqualTo("userName", inviterName);
            findInviterQuery.whereEqualTo("guestName", ParseUser.getCurrentUser().getUsername());
            findInviterQuery.whereDoesNotExist("accept");
            findInviterQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null && objects.size() > 0) {
                        objects.get(0).put("accept", "yes");
                        objects.get(0).saveInBackground();
                        startGame();
                    } else {
                        Toast.makeText(GuestActivity.this, "No invitation found, please check your inviter's name", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.constraintLayout2) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
    }
}

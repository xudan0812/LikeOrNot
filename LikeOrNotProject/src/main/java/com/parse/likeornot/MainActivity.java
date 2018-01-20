/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.likeornot;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
  String logInOrSignUp = "signUp";
  TextView textView;
  Button button;
  EditText username;
  EditText password;
  String userOrGuest = "user";
  Handler handler;

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
    }
    return super.onOptionsItemSelected(item);
  }

  public void buttonClicked(View view) {
    InputMethodManager mgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    mgr.hideSoftInputFromWindow(button.getWindowToken(),0);

    if (username.getText().toString().equals("") || password.getText().toString().equals("")) {
      Toast.makeText(this, "A username and password are required.", Toast.LENGTH_SHORT).show();
    }
    else {
      ParseUser user = new ParseUser();
      if (logInOrSignUp == "signUp") {

        user.setUsername(username.getText().toString());
        user.setPassword(password.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
          @Override
          public void done(ParseException e) {
            if (e == null) {
              Log.i("signup", "successful");
              Toast.makeText(MainActivity.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
              final Intent intent = new Intent(getApplicationContext(), UserOrGuestActivity.class);
              handler=new Handler();
              handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                  startActivity(intent);
                }
              }, 1000);
            } else {
              Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
        });
      } else {
        user.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
          @Override
          public void done(ParseUser user, ParseException e) {
            if (user != null) {
              Log.i("login", "successful");
              Toast.makeText(MainActivity.this, "Log in successful!", Toast.LENGTH_SHORT).show();
              final Intent intent = new Intent(getApplicationContext(), UserOrGuestActivity.class);
              /*
              handler=new Handler();
              handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                  startActivity(intent);
                }
              }, 1000);
              */
              startActivity(intent);
          } else {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
          }

        }
      });

    }
  }


}

  public void logInSignUpChange(View view) {
    if (logInOrSignUp == "signUp") {
      textView.setText("Or Sign Up");
      button.setText("log in");
      logInOrSignUp = "logIn";
    } else {
      textView.setText("Or Log In");
      button.setText("sign up");
      logInOrSignUp = "signUp";
    }
  }



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    getWindow().getDecorView().setBackgroundColor(Color.rgb(251,210,26));
    setTitle("Sign Up & Log In");

    //MyFirebaseInstanceIdService myFirebaseInstanceIdService = new MyFirebaseInstanceIdService();

    username = (EditText) findViewById(R.id.usernameEditText);
    password = (EditText) findViewById(R.id.passwordEditText);
    button = (Button)findViewById(R.id.button);
    textView = (TextView) findViewById(R.id.textView);
    RelativeLayout backgroundRelativeLayout = (RelativeLayout) findViewById(R.id.backgroundRelativeLayout);
    ImageView logoImageView = (ImageView) findViewById(R.id.logoImageView);


    //password.setOnKeyListener(this);
    backgroundRelativeLayout.setOnClickListener(this);
    logoImageView.setOnClickListener(this);


    if (ParseUser.getCurrentUser() != null) {
      Intent intent = new Intent(getApplicationContext(), UserOrGuestActivity.class);
      startActivity(intent);
    }
    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

  @Override
  public void onClick(View view) {
    if (view.getId() == R.id.logoImageView || view.getId() == R.id.backgroundRelativeLayout) {
      InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }
  }
/*
  @Override
  public boolean onKey(View view, int i, KeyEvent keyEvent) {
    if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
      buttonClicked(view);
    }
    return false;
  }
  */

/*
  public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
      // Get updated InstanceID token.
      String refreshedToken = FirebaseInstanceId.getInstance().getToken();
      Log.d("token", "Refreshed token: " + refreshedToken);

      // If you want to send messages to this application instance or
      // manage this apps subscriptions on the server side, send the
      // Instance ID token to your app server.
      //sendRegistrationToServer(refreshedToken);


    }
  }
  */
}
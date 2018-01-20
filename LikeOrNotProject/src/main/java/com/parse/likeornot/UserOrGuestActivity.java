package com.parse.likeornot;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.parse.ParseUser;

public class UserOrGuestActivity extends AppCompatActivity {

    public void userButtonClicked ( View view) {
        Intent intent = new Intent(getApplicationContext(), InviteFriendActivity.class);
        intent.putExtra("from", "userOrGuest");
        startActivity(intent);
    }

    public void guestButtonClicked (View view) {
        Intent intent = new Intent(getApplicationContext(), GuestActivity.class);
        intent.putExtra("from", "userOrGuest");
        startActivity(intent);
    }

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
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        /*
        else if (item.getItemId() == R.id.playAgain) {
            Intent intent = new Intent(getApplicationContext(), playAgainActivity.class);
            intent.putExtra("finalScore", "1 / 3" );
            intent.putExtra("userOrGuest", "user");
            intent.putExtra("guestName","datu" );
            intent.putExtra("userName","shizhitu" );
            startActivity(intent);
        }
        */
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_or_guest);
        getWindow().getDecorView().setBackgroundColor(Color.rgb(251,210,26));
        setTitle("User or Guest");
        Button userButton = (Button)findViewById(R.id.userButton);
        Button guestButton = (Button)findViewById(R.id.guestButton);
    }
}

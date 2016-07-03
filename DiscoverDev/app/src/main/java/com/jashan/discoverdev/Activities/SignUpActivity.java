package com.jashan.discoverdev.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.jashan.discoverdev.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class SignUpActivity extends Activity {

    private EditText mUsernameText;
    private EditText mPasswordText;
    private EditText mConfirmPasswordText;
    private EditText mEmailText;
    private ProgressBar mProgressBar;
    private Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mUsernameText = (EditText) findViewById(R.id.signUserName);
        mPasswordText = (EditText) findViewById(R.id.signPassword);
        mConfirmPasswordText = (EditText) findViewById(R.id.signConfirmPassword);
        mProgressBar= (ProgressBar) findViewById(R.id.signProgressBar);
        mSignUpButton = (Button) findViewById(R.id.signUpButton);
        mEmailText = (EditText) findViewById(R.id.signEmail);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp() {
        String password = mPasswordText.getText().toString().trim();
        String confrimPassword = mConfirmPasswordText.getText().toString().trim();

        String username = mUsernameText.getText().toString().trim();
        String email = mEmailText.getText().toString().trim();


        if (username.isEmpty() || password.isEmpty() || email.isEmpty()){
            AlertDialog.Builder builder= new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.empty_fields_error))
                    .setMessage(getString(R.string.no_fields_blank_message))
                    .setPositiveButton(android.R.string.ok, null);

            builder.create().show();

            return;
        }

        else if (password.contentEquals(confrimPassword) == false){
            AlertDialog.Builder builder= new AlertDialog.Builder(this);
            builder.setTitle(R.string.oops)
                    .setMessage(getString(R.string.password_mismatch_error))
                    .setPositiveButton(android.R.string.ok, null);

            builder.create().show();

            return;
        }
        else{
            ParseUser newUser = new ParseUser();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setEmail(email);
            toggleProgressBar();
            newUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    toggleProgressBar();
                    if (e==null){
                        //success! :D no error
                        navigateToMuseumActivity();
                    }
                    else{
                        AlertDialog.Builder builder= new AlertDialog.Builder(SignUpActivity.this);
                        builder.setTitle(getString(R.string.general_error))
                                .setMessage(e.getMessage())
                                .setPositiveButton(android.R.string.ok, null);

                        builder.create().show();

                    }
                }

            });
        }

    }

    private void navigateToMuseumActivity() {
        Intent intent = new Intent (this, MuseumViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void toggleProgressBar() {
        if (mProgressBar.getVisibility() == View.INVISIBLE){
            mProgressBar.setVisibility(View.VISIBLE);
        }
        else{
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

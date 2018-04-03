package com.androidchatapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private TextView register;
    private EditText username, password;
    private Button loginButton;
    private String user, pass;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextInputLayout inputLayoutUsername,inputLayoutPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("chatmsg", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        boolean flag = sharedPreferences.getBoolean("isLogin", false);
        if (flag) {
            ///second time activity
            sharedPreferences = getSharedPreferences("chatmsg", Context.MODE_PRIVATE);
            String username=sharedPreferences.getString("chatappuser","");
            System.out.println("---------------------------------------------"+username);
            UserDetails.username=username;
            Intent i = new Intent(getApplicationContext(), UsersActivity.class);
            startActivity(i);
            finish();
        }
        setContentView(R.layout.activity_login);
        inputLayoutUsername=(TextInputLayout)findViewById(R.id.input_layout_uname);
        inputLayoutPassword=(TextInputLayout)findViewById(R.id.input_layout_password);
        register = (TextView)findViewById(R.id.register);
        username = (EditText)findViewById(R.id.input_uname);
        password = (EditText)findViewById(R.id.input_password);
        loginButton = (Button)findViewById(R.id.loginButton);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = username.getText().toString();
                pass = password.getText().toString();

                if(user.equals("")){
                    username.setError("can't be blank");
                }
                else if(pass.equals("")){
                    password.setError("can't be blank");
                }
                else{
                    String url = "https://fir-chatapp-c419d.firebaseio.com/users.json";
                    final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                    pd.setMessage("Loading...");
                    pd.show();

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                        @Override
                        public void onResponse(String s) {
                            if(s.equals("null")){
                                Toast.makeText(LoginActivity.this, "user not found", Toast.LENGTH_LONG).show();
                            }
                            else{
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if(!obj.has(user)){
                                        Toast.makeText(LoginActivity.this, "user not found", Toast.LENGTH_LONG).show();
                                    }
                                    else if(obj.getJSONObject(user).getString("password").equals(pass)){
                                        UserDetails.username = user;
                                        editor.putString("chatappuser", user).apply();
                                        editor.commit();
                                        System.out.println("user is:"+user);
                                        UserDetails.password = pass;
                                        sharedPreferences.edit().putBoolean("isLogin", true).apply();
                                        System.out.println("======================");
                                        startActivity(new Intent(LoginActivity.this, UsersActivity.class));
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(LoginActivity.this, "incorrect password", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            pd.dismiss();
                        }
                    },new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            System.out.println("" + volleyError);
                            pd.dismiss();
                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(LoginActivity.this);
                    rQueue.add(request);
                }

            }
        });
    }
}

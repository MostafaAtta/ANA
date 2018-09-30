package com.atta.ana;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.atta.ana.classes.Constants;
import com.atta.ana.classes.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{


    // Session Manager Class
    SessionManager session;

    ProgressDialog progressDialog;

    // login button
    Button login;

    // National ID, password edit text
    EditText emailText,passwordText;

    TextView newAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Session class instance
        session = new SessionManager(getApplicationContext());

        // National ID, Password input text
        emailText = (EditText)findViewById(R.id.email);
        passwordText = (EditText)findViewById(R.id.password);

        newAccount = (TextView) findViewById(R.id.btnRegisterScreen);
        newAccount.setOnClickListener(this);

        // Login button
        login = (Button)findViewById(R.id.btn_login);
        login.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if(view == login) {
            if (!validate()) {
                onLoginFailed("Invalid Login details");
                return;
            }
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();
            if(progressDialog != null){
                progressDialog.dismiss();
            }
            progressDialog = new ProgressDialog(LoginActivity.this,R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
            login(email,password);
        }else if (view == newAccount){

            Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void onLoginFailed(String message) {

        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
        login.setEnabled(true);
    }

    public boolean validate (){
        boolean valid = true;

        final String email = emailText.getText().toString().trim();
        String pass = passwordText.getText().toString();
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (!email.matches(emailPattern) || email.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Invalid email address",Toast.LENGTH_SHORT).show();
            emailText.setError("Invalid email address");
            valid = false;

        }else {
            emailText.setError(null);
        }

        if (pass.isEmpty() || pass.length() < 4 || pass.length() > 10) {
            passwordText.setError("password must be between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }


    public void login(final String email, final String password) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Create an empty ArrayList that we can start adding mobiles to

                try {
                    // Create a JSONObject from the JSON response string
                    JSONObject baseJsonResponse = new JSONObject(response);

                    if(!baseJsonResponse.getBoolean("error")){

                        int id = baseJsonResponse.getInt("id");
                        String email = baseJsonResponse.getString("email");
                        login.setEnabled(false);
                        session.createLoginSession(id, email, password);
                        if(progressDialog != null || progressDialog.isShowing() ){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getApplicationContext(),"Login successfully",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        if(progressDialog != null || progressDialog.isShowing() ){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getApplicationContext(), baseJsonResponse.getString("error_msg"), Toast.LENGTH_LONG).show();
                        login.setEnabled(true);
                    }


                } catch (JSONException e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(progressDialog != null || progressDialog.isShowing() ){
                    progressDialog.dismiss();
                }

                Toast.makeText(LoginActivity.this,error.toString(),Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("email", String.valueOf(email));
                params.put("password", String.valueOf(password));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(stringRequest);

    }
}

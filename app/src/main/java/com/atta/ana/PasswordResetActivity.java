package com.atta.ana;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class PasswordResetActivity extends AppCompatActivity implements View.OnClickListener{

    // Session Manager Class
    SessionManager session;

    ProgressDialog progressDialog;

    // login button
    Button reset;

    // National ID, password edit text
    EditText oldPassword, newPassword, confirmPassword;

    TextView userText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        // Session class instance
        session = new SessionManager(getApplicationContext());

        userText = (TextView) findViewById(R.id.name_text);
        userText.setText(session.getUserName());

        // National ID, Password input text
        oldPassword = (EditText)findViewById(R.id.current_password);
        newPassword = (EditText)findViewById(R.id.new_password);
        confirmPassword = (EditText)findViewById(R.id.confirm_password);

        // Reset button
        reset = (Button)findViewById(R.id.btnReset);
        reset.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == reset){
            if (!validate()) {
                Toast.makeText(getApplicationContext(), "Wrong ", Toast.LENGTH_LONG).show();
                reset.setEnabled(true);
                return;
            }

            String oldPasswordText = oldPassword.getText().toString();
            String newPasswordText = newPassword.getText().toString();

            if(progressDialog != null){
                progressDialog.dismiss();
            }
            progressDialog = new ProgressDialog(PasswordResetActivity.this,R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Resetting your password...");
            progressDialog.show();
            reset(newPasswordText);
        }

    }

    public boolean validate (){

        boolean valid = true;

        String oldPasswordText = oldPassword.getText().toString();
        String newPasswordText = newPassword.getText().toString();
        String confirmPasswordText = confirmPassword.getText().toString();
        if (!oldPasswordText.matches(session.getPassword())) {
            oldPassword.setError("old password not correct");
            valid = false;
        }else if (oldPasswordText.isEmpty()) {
            oldPassword.setError("old password couldn't be empty");
            valid = false;
        }else if (newPasswordText.isEmpty() || newPasswordText.length() < 4 || newPasswordText.length() > 10) {
            newPassword.setError("password must be between 4 and 10 alphanumeric characters");
            valid = false;
        }else if (!confirmPasswordText.matches(newPasswordText)){
            confirmPassword.setError("not identical to the new password");
            valid = false;
        }else {
            newPassword.setError(null);
        }


        return valid;
    }

    public void reset(final String newPassword) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.PASSWORD_RESET_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Create an empty ArrayList that we can start adding mobiles to

                try {
                    // Create a JSONObject from the JSON response string
                    JSONObject baseJsonResponse = new JSONObject(response);

                    if(!baseJsonResponse.getBoolean("error")){

                        reset.setEnabled(false);
                        session.updatePassword(newPassword);
                        if(progressDialog != null || progressDialog.isShowing() ){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getApplicationContext(),"Login successfully",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(PasswordResetActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        if(progressDialog != null || progressDialog.isShowing() ){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getApplicationContext(), baseJsonResponse.getString("error_msg"), Toast.LENGTH_LONG).show();
                        reset.setEnabled(true);
                    }


                } catch (JSONException e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PasswordResetActivity.this,error.toString(),Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("email", String.valueOf(session.getEmail()));
                params.put("password", String.valueOf(newPassword));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(PasswordResetActivity.this);
        requestQueue.add(stringRequest);

    }
}

package com.atta.ana;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

public class ContactActivity extends AppCompatActivity implements View.OnClickListener {

    EditText homeAddressText, homeTelephoneText, workTelephoneText, mobileTelephone1Text, mobileTelephone2Text,
            emailAddressesText, faceBookAccountText, websiteAddressText;

    Button updateButton;

    ProgressDialog progressDialog;

    // Session Manager Class
    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);


        // Session class instance
        session = new SessionManager(getApplicationContext());

        homeAddressText = (EditText) findViewById(R.id.home_address_txt);
        homeTelephoneText = (EditText) findViewById(R.id.home_telephone_txt);
        workTelephoneText = (EditText) findViewById(R.id.work_telephone_txt);
        mobileTelephone1Text = (EditText) findViewById(R.id.mobile_txt1);
        mobileTelephone2Text = (EditText) findViewById(R.id.mobile_txt2);
        emailAddressesText = (EditText) findViewById(R.id.email_txt);
        faceBookAccountText = (EditText) findViewById(R.id.faceBook_txt);
        websiteAddressText = (EditText) findViewById(R.id.website_txt);

        updateButton = (Button) findViewById(R.id.btn_update_contacts);
        updateButton.setOnClickListener(this);

        getContactInfo();
    }

    @Override
    public void onClick(View v) {
        if (v == updateButton){

            if(progressDialog != null){
                progressDialog.dismiss();
            }
            progressDialog = new ProgressDialog(ContactActivity.this,R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Updating...");
            progressDialog.show();

            updateContactInfo();
        }
    }

    private void updateContactInfo() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.CONTACT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // Create a JSONObject from the JSON response string
                    JSONObject baseJsonResponse = new JSONObject(response);

                    if(!baseJsonResponse.getBoolean("error")){

                        if(progressDialog != null || progressDialog.isShowing() ){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getApplicationContext(),"Uploaded successfully",Toast.LENGTH_LONG).show();

                    }else{
                        if(progressDialog != null || progressDialog.isShowing() ){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getApplicationContext(), baseJsonResponse.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ContactActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String ,String> params = new HashMap<String,String>();

                params.put("user_id",session.getUserId());
                params.put("home_address",homeAddressText.getText().toString().trim());
                params.put("home_telephone",homeTelephoneText.getText().toString().trim());
                params.put("work_telephone",workTelephoneText.getText().toString().trim());
                params.put("mobile1",mobileTelephone1Text.getText().toString().trim());
                params.put("mobile2",mobileTelephone2Text.getText().toString().trim());
                params.put("email",emailAddressesText.getText().toString().trim());
                params.put("faceBook","https://www.facebook.com/" + faceBookAccountText.getText().toString().trim());
                params.put("website",websiteAddressText.getText().toString().trim());

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getContactInfo() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.CHECK_CONTACT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // Create a JSONObject from the JSON response string
                    JSONObject baseJsonResponse = new JSONObject(response);

                    if(!baseJsonResponse.getBoolean("error")){

                        String homeAddress = baseJsonResponse.getString("home_address");
                        String homeTelephone = baseJsonResponse.getString("home_telephone");
                        String workTelephone = baseJsonResponse.getString("work_telephone");
                        String mobileTelephone1 = baseJsonResponse.getString("mobile1");
                        String mobileTelephone2 = baseJsonResponse.getString("mobile2");
                        String emailAddresses = baseJsonResponse.getString("email");
                        String faceBookAccount = baseJsonResponse.getString("faceBook");
                        String websiteAddress = baseJsonResponse.getString("website");


                        homeAddressText.setText(homeAddress);
                        homeTelephoneText.setText( homeTelephone);
                        workTelephoneText.setText(workTelephone);
                        mobileTelephone1Text.setText(mobileTelephone1);
                        mobileTelephone2Text.setText(mobileTelephone2);
                        emailAddressesText.setText(emailAddresses);
                        faceBookAccountText.setText(faceBookAccount);
                        websiteAddressText.setText(websiteAddress);

                    }else{

                        Toast.makeText(getApplicationContext(), baseJsonResponse.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ContactActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String ,String> params = new HashMap<String,String>();

                params.put("user_id",session.getUserId());

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}

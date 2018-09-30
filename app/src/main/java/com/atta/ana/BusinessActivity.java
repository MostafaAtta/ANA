package com.atta.ana;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class BusinessActivity extends AppCompatActivity implements View.OnClickListener {

    EditText jobTitleText, organizationText, educationLevelText, linkedinAccountText;

    Button updateButton;

    ProgressDialog progressDialog;

    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);


        // Session class instance
        session = new SessionManager(getApplicationContext());

        jobTitleText = (EditText) findViewById(R.id.job_title_txt);
        organizationText = (EditText) findViewById(R.id.organization_txt);
        educationLevelText = (EditText) findViewById(R.id.education_level_txt);
        linkedinAccountText = (EditText) findViewById(R.id.linkedin_account_txt1);

        updateButton = (Button) findViewById(R.id.btn_update_contacts);
        updateButton.setOnClickListener(this);

        getBusinessInfo();
    }

    @Override
    public void onClick(View v) {
        if (v == updateButton){

            if(progressDialog != null){
                progressDialog.dismiss();
            }
            progressDialog = new ProgressDialog(BusinessActivity.this,R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Updating...");
            progressDialog.show();

            updateBusinessInfo();
        }
    }


    private void updateBusinessInfo() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.BUSINESS_URL, new Response.Listener<String>() {
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
                Toast.makeText(BusinessActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String ,String> params = new HashMap<String,String>();

                params.put("user_id",session.getUserId());
                params.put("job_title",jobTitleText.getText().toString().trim());
                params.put("organization",organizationText.getText().toString().trim());
                params.put("education_level",educationLevelText.getText().toString().trim());
                params.put("linkedin_account",linkedinAccountText.getText().toString().trim());

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getBusinessInfo() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.CHECK_BUSINESS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // Create a JSONObject from the JSON response string
                    JSONObject baseJsonResponse = new JSONObject(response);

                    if(!baseJsonResponse.getBoolean("error")){

                        String jobTitle = baseJsonResponse.getString("job_title");
                        String organization = baseJsonResponse.getString("organization");
                        String educationLevel = baseJsonResponse.getString("education_level");
                        String linkedinAccount = baseJsonResponse.getString("linkedin_account");

                        jobTitleText.setText(jobTitle);
                        organizationText.setText(organization);
                        educationLevelText.setText(educationLevel);
                        linkedinAccountText.setText(linkedinAccount);
                    }else{

                        Toast.makeText(getApplicationContext(), baseJsonResponse.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(BusinessActivity.this,error.toString(),Toast.LENGTH_LONG).show();
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

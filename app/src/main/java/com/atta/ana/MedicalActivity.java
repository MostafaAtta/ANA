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

public class MedicalActivity extends AppCompatActivity implements View.OnClickListener {

    EditText bloodGroupText, heightText, weightText, chronicDiseasesText, surgeriesText;

    Button updateButton;

    ProgressDialog progressDialog;

    // Session Manager Class
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical);


        // Session class instance
        session = new SessionManager(getApplicationContext());

        bloodGroupText = (EditText) findViewById(R.id.blood_group_txt);
        heightText = (EditText) findViewById(R.id.height_txt);
        weightText = (EditText) findViewById(R.id.weight_txt);
        chronicDiseasesText = (EditText) findViewById(R.id.chronic_diseases_txt);
        surgeriesText = (EditText) findViewById(R.id.surgeries_txt);

        updateButton = (Button) findViewById(R.id.btn_update_contacts);
        updateButton.setOnClickListener(this);

        getBusinessInfo();

    }



    private void updateBusinessInfo() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.MEDICAL_URL, new Response.Listener<String>() {
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
                Toast.makeText(MedicalActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String ,String> params = new HashMap<String,String>();

                params.put("user_id",session.getUserId());
                params.put("blood_group", bloodGroupText.getText().toString().trim());
                params.put("height", heightText.getText().toString().trim());
                params.put("weight", weightText.getText().toString().trim());
                params.put("chronic_diseases", chronicDiseasesText.getText().toString().trim());
                params.put("surgeries", surgeriesText.getText().toString().trim());

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getBusinessInfo() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.CHECK_MEDICAL_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // Create a JSONObject from the JSON response string
                    JSONObject baseJsonResponse = new JSONObject(response);

                    if(!baseJsonResponse.getBoolean("error")){

                        String jobTitle = baseJsonResponse.getString("blood_group");
                        String organization = baseJsonResponse.getString("height");
                        String educationLevel = baseJsonResponse.getString("weight");
                        String linkedinAccount = baseJsonResponse.getString("chronic_diseases");
                        String surgeries = baseJsonResponse.getString("surgeries");

                        bloodGroupText.setText(jobTitle);
                        heightText.setText(organization);
                        weightText.setText(educationLevel);
                        chronicDiseasesText.setText(linkedinAccount);
                        surgeriesText.setText(surgeries);
                    }else{

                        Toast.makeText(getApplicationContext(), baseJsonResponse.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MedicalActivity.this,error.toString(),Toast.LENGTH_LONG).show();
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

    @Override
    public void onClick(View v) {
        if (v == updateButton){

            if(progressDialog != null){
                progressDialog.dismiss();
            }
            progressDialog = new ProgressDialog(MedicalActivity.this,R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Updating...");
            progressDialog.show();

            updateBusinessInfo();
        }
    }
}

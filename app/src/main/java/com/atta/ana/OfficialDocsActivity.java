package com.atta.ana;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atta.ana.classes.AppController;
import com.atta.ana.classes.Constants;
import com.atta.ana.classes.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OfficialDocsActivity extends AppCompatActivity implements View.OnClickListener {

    static final int REQUEST_IMAGE_CAPTURE = 2;

    // Session Manager Class
    SessionManager session;

    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int EXTERNAL_STORAGE_REQUEST_CODE = 102;

    ProgressDialog progressDialog;

    Bitmap bitmap;

    NetworkImageView docImage;

    ImageView nationalId, birthCertificate, passport, drivingLicense;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    String selectedDoc;

    GridLayout gridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official_docs);

        // Session class instance
        session = new SessionManager(getApplicationContext());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.CAMERA,
                    CAMERA_REQUEST_CODE);
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    EXTERNAL_STORAGE_REQUEST_CODE);
            return;
        }

        if (imageLoader == null){
            imageLoader = AppController.getInstance().getImageLoader();
        }

        nationalId = (ImageView) findViewById(R.id.national_id);
        birthCertificate = (ImageView) findViewById(R.id.birth_certificate);
        passport = (ImageView) findViewById(R.id.passport);
        drivingLicense = (ImageView) findViewById(R.id.driving_license);

        gridLayout = (GridLayout) findViewById(R.id.grid);

        nationalId.setOnClickListener(this);
        birthCertificate.setOnClickListener(this);
        passport.setOnClickListener(this);
        drivingLicense.setOnClickListener(this);


        docImage = (NetworkImageView) findViewById(R.id.doc_image);
        docImage.setOnClickListener(this);


    }


    private void updateImage() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.UPLOAD_DOCS_URL, new Response.Listener<String>() {
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
                Toast.makeText(OfficialDocsActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String image = getStringImage(bitmap);

                Map<String ,String> params = new HashMap<String,String>();

                params.put("user_id",session.getUserId());
                params.put("image",image);
                params.put("image_name",session.getEmail() + "_" + selectedDoc);
                params.put("doc",selectedDoc);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                docImage.setImageBitmap(bitmap);

                if(progressDialog != null){
                    progressDialog.dismiss();
                }
                progressDialog = new ProgressDialog(OfficialDocsActivity.this,R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Updating...");
                progressDialog.show();
                updateImage();

            } catch (IOException e) {

                e.printStackTrace();
            }
        }else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            docImage.setImageBitmap(bitmap);
            if(progressDialog != null){
                progressDialog.dismiss();
            }
            progressDialog = new ProgressDialog(OfficialDocsActivity.this,R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Updating...");
            progressDialog.show();
            updateImage();
        }
    }

    public String getStringImage(Bitmap bm){
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,ba);
        byte[] imagebyte = ba.toByteArray();
        String encode = Base64.encodeToString(imagebyte,Base64.DEFAULT);
        return encode;
    }

    protected void requestPermission(String permissionType, int requestCode) {
        int permission = ContextCompat.checkSelfPermission(this,
                permissionType);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permissionType}, requestCode
            );
        }
    }

    private void viewDoc() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.GET_DOCS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // Create a JSONObject from the JSON response string
                    JSONObject baseJsonResponse = new JSONObject(response);

                    if(!baseJsonResponse.getBoolean("error")){




                        String idImage = Constants.ROOT_URL + "/" + baseJsonResponse.getString("national_id");
                        String birthCertImage = Constants.ROOT_URL + "/" + baseJsonResponse.getString("birth_certificate");
                        String passportImage = Constants.ROOT_URL + "/" + baseJsonResponse.getString("passport");
                        String drivingLicenseImage = Constants.ROOT_URL + "/" + baseJsonResponse.getString("driving_license");

                        docImage.setVisibility(View.VISIBLE);
                        gridLayout.setVisibility(View.GONE);

                        switch (selectedDoc){
                            case "national_id":
                            docImage.setImageUrl(idImage, imageLoader);
                            break;
                            case "birth_certificate":
                                docImage.setImageUrl(birthCertImage, imageLoader);
                                break;
                            case "passport":
                                docImage.setImageUrl(passportImage, imageLoader);
                                break;
                            case "driving_license":
                                docImage.setImageUrl(drivingLicenseImage, imageLoader);
                                break;
                        }


                    }else{

                        Toast.makeText(getApplicationContext(), baseJsonResponse.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OfficialDocsActivity.this,error.toString(),Toast.LENGTH_LONG).show();
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

        if (v == docImage){
            docImage.setVisibility(View.GONE);
            gridLayout.setVisibility(View.VISIBLE);
        }else {
            selectedDoc = getResources().getResourceEntryName(v.getId());
            Toast.makeText(getApplicationContext(),selectedDoc,Toast.LENGTH_LONG).show();


            AlertDialog alertDialog = new AlertDialog.Builder(OfficialDocsActivity.this).create();
            alertDialog.setTitle("Update Image");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Upload",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog alertDialog = new AlertDialog.Builder(OfficialDocsActivity.this).create();
                            alertDialog.setTitle("Update Image");
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Browse",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();

                                            Intent intent = new Intent();

                                            intent.setType("image/*");

                                            intent.setAction(Intent.ACTION_GET_CONTENT);

                                            startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 1);
                                        }
                                    });
                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Take a Photo",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                            }
                                        }
                                    });


                            alertDialog.show();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "View",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            viewDoc();
                        }
                    });


            alertDialog.show();
        }



    }
}

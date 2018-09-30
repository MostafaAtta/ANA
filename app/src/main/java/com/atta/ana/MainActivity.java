package com.atta.ana;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {


    // Session Manager Class
    SessionManager session;

    TextView textView, firstNameText, lastNameText, birthDayText, changeImageTxt;

    NetworkImageView profileImage;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    LinearLayout uploadLinearLayout, searchLinearLayout;


    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int EXTERNAL_STORAGE_REQUEST_CODE = 102;

    ProgressDialog progressDialog;

    Bitmap bitmap;

    static final int REQUEST_IMAGE_CAPTURE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        uploadLinearLayout= (LinearLayout) findViewById(R.id.upload_documents);
        uploadLinearLayout.setOnClickListener(this);
        searchLinearLayout= (LinearLayout) findViewById(R.id.search);
        searchLinearLayout.setOnClickListener(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        textView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textView);
        textView.setText(session.getEmail());

        firstNameText = (TextView) findViewById(R.id.first_name_txt);
        lastNameText = (TextView) findViewById(R.id.last_name_txt);
        birthDayText = (TextView) findViewById(R.id.birth_day_txt);
        changeImageTxt = (TextView) findViewById(R.id.change_txt);

        changeImageTxt.setOnClickListener(this);

        profileImage = (NetworkImageView) findViewById(R.id.profile_image);

        updateCard();
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

    private void updateCard() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.CHECK_PD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // Create a JSONObject from the JSON response string
                    JSONObject baseJsonResponse = new JSONObject(response);

                    if(!baseJsonResponse.getBoolean("error")){

                        String firstName = baseJsonResponse.getString("first_name");
                        String lastName = baseJsonResponse.getString("last_name");
                        String birth = baseJsonResponse.getString("date_of_birth");
                        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
                        lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);

                        firstNameText.setText(firstName);
                        lastNameText.setText(lastName);
                        birthDayText.setText(birth);


                        String image = Constants.ROOT_URL + "/" + baseJsonResponse.getString("image");


                        profileImage.setImageUrl(image, imageLoader);

                    }else{

                        Toast.makeText(getApplicationContext(), baseJsonResponse.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_LONG).show();
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.personal) {

            startActivity(new Intent(this, PersonalActivity.class));

        } else if (id == R.id.contact) {

            startActivity(new Intent(this, ContactActivity.class));
        } else if (id == R.id.business) {

            startActivity(new Intent(this, BusinessActivity.class));
        } else if (id == R.id.medical) {

            startActivity(new Intent(this, MedicalActivity.class));
        }  else if (id == R.id.logout) {

            session.logoutUser();
            startActivity(new Intent(this, LoginActivity.class));

        } else if (id == R.id.reset) {

            startActivity(new Intent(this, PasswordResetActivity.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == changeImageTxt){

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
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

        }else if (v == uploadLinearLayout){
            Intent intent = new Intent(MainActivity.this, OfficialDocsActivity.class);
            startActivity(intent);

        }else if (v == searchLinearLayout) {


            final Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            final Bundle bundle = new Bundle();

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Select from below");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Parties",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            bundle.putString("selected", "official parties");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Service",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            bundle.putString("selected", "services");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });

            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Organization",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            bundle.putString("selected", "organizations");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });


            alertDialog.show();

        }


    }

    private void updateImage() {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.IMAGE_URL, new Response.Listener<String>() {
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
                Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String image = getStringImage(bitmap);

                Map<String ,String> params = new HashMap<String,String>();

                params.put("user_id",session.getUserId());
                params.put("image",image);
                params.put("image_name",session.getEmail());

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

                profileImage.setImageBitmap(bitmap);

                if(progressDialog != null){
                    progressDialog.dismiss();
                }
                progressDialog = new ProgressDialog(MainActivity.this,R.style.AppTheme_Dark_Dialog);
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
            profileImage.setImageBitmap(bitmap);
            if(progressDialog != null){
                progressDialog.dismiss();
            }
            progressDialog = new ProgressDialog(MainActivity.this,R.style.AppTheme_Dark_Dialog);
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
}

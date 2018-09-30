package com.atta.ana;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PersonalActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    NetworkImageView profileImage;

    TextView selectImage, cameraText;

    EditText firstName, lastName, age, nationalIdNumber, passportNumber, birthDate, locationOfBirth,
            citizenship, nationality, wifeName, childrenNo, religion, driverLicenseNumber;

    Spinner maritalStatusSpinner, militaryStatusSpinner;

    String maritalStatus, militaryStatus;

    RadioButton maleButton, femaleButton;

    Button upload, upload1, uploadImage, uploadAll;

    Bitmap bitmap;

    ProgressDialog progressDialog;

    // Session Manager Class
    SessionManager session;

    Calendar myCalendar;

    DatePickerDialog.OnDateSetListener date;

    String image, ageText, genderText, birthDateText;

    boolean imageSelected, imageCamera, imageDownloaded;

    static final int REQUEST_IMAGE_CAPTURE = 2;


    ArrayAdapter<String> maritalStatusAdapter;
    ArrayAdapter<String> militaryStatusAdapter;

    private static List<String> maritalStatusArray = new ArrayList<String>(){{
        add("Marital status");
        add("Married");
        add("Widowed");
        add("Divorced");
        add("Single");
    }};

    private static List<String> militaryStatusArray = new ArrayList<String>(){{
        add("Military status");
        add("Completed");
        add("Postponed");
        add("Exemption");
        add("Currently serving");
        add("Does not apply");
    }};

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int EXTERNAL_STORAGE_REQUEST_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);


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


        imageSelected = false;

        imageCamera = false;

        imageDownloaded = false;

        profileImage = (NetworkImageView) findViewById(R.id.profile_image);

        selectImage = (TextView) findViewById(R.id.upload_txt);
        cameraText = (TextView) findViewById(R.id.camera_txt);

        upload = (Button) findViewById(R.id.btn_update);
        upload1 = (Button) findViewById(R.id.btn_update2);
        uploadImage = (Button) findViewById(R.id.btn_update_image);
        uploadAll = (Button) findViewById(R.id.btn_update_all);

        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        age = (EditText) findViewById(R.id.age);
        nationalIdNumber = (EditText) findViewById(R.id.national_id_text);
        passportNumber = (EditText) findViewById(R.id.passport_id);
        birthDate = (EditText) findViewById(R.id.birth_day);
        locationOfBirth = (EditText) findViewById(R.id.birth_location);
        citizenship = (EditText) findViewById(R.id.citizenship);
        nationality = (EditText) findViewById(R.id.nationality);

        wifeName = (EditText) findViewById(R.id.wife_name);
        childrenNo = (EditText) findViewById(R.id.children_no);
        religion = (EditText) findViewById(R.id.religion);
        driverLicenseNumber = (EditText) findViewById(R.id.driver_license_number);

        maleButton = (RadioButton) findViewById(R.id.male_btn);
        femaleButton = (RadioButton) findViewById(R.id.female_btn);


        maritalStatusSpinner = (Spinner) findViewById(R.id.marital_status);
        militaryStatusSpinner = (Spinner) findViewById(R.id.military_status);
        maritalStatusSpinner.setOnItemSelectedListener(this);
        militaryStatusSpinner.setOnItemSelectedListener(this);

        // Create an ArrayAdapter using the string array and a default spinner layout
        maritalStatusAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, maritalStatusArray);
        maritalStatusSpinner.setAdapter(maritalStatusAdapter);

        militaryStatusAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, militaryStatusArray);
        militaryStatusSpinner.setAdapter(militaryStatusAdapter);

        myCalendar = Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        birthDate.setOnClickListener(this);

        selectImage.setOnClickListener(this);
        cameraText.setOnClickListener(this);
        upload.setOnClickListener(this);
        upload1.setOnClickListener(this);
        uploadImage.setOnClickListener(this);
        uploadAll.setOnClickListener(this);
        maleButton.setOnClickListener(this);
        femaleButton.setOnClickListener(this);


        birthDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String birthDateText = birthDate.getText().toString().trim();

            }
        });

        getData();
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

    @Override
    public void onClick(View view) {
        if (view == birthDate){
            new DatePickerDialog(this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            String myFormat = "MM/dd/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            birthDateText = sdf.format(myCalendar.getTime());

        }else if (view == maleButton){

            genderText = "Male";

        }else if (view == femaleButton){

            genderText = "Female";

        }else if (view == cameraText){

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }


        }else if (view == selectImage){
            Intent intent = new Intent();

            intent.setType("image/*");

            intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 1);


        }else if (view == uploadImage){

            if (imageSelected || imageCamera){


                if(progressDialog != null){
                    progressDialog.dismiss();
                }
                progressDialog = new ProgressDialog(PersonalActivity.this,R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Updating...");
                progressDialog.show();

                updateImage();
            }else {
                Toast.makeText(getApplicationContext(),"Select a profile photo",Toast.LENGTH_SHORT).show();
            }


        }else if (view == upload){

            if (validate(firstName) || validate(lastName) || validate(age) || validateNationalId() ||
                    validate(passportNumber) || validate(birthDate) || validate(locationOfBirth) ||
                    validate(citizenship) || validate(nationality)){

                if (imageSelected || imageCamera || imageDownloaded){


                    if(progressDialog != null){
                        progressDialog.dismiss();
                    }
                    progressDialog = new ProgressDialog(PersonalActivity.this,R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Updating...");
                    progressDialog.show();

                    uploadPData();
                }else {
                    Toast.makeText(getApplicationContext(),"Select a profile photo",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(),"Invalid Input",Toast.LENGTH_SHORT).show();
            }
        }else if (view == upload1){

            if(progressDialog != null){
                progressDialog.dismiss();
            }
            progressDialog = new ProgressDialog(PersonalActivity.this,R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Updating...");
            progressDialog.show();

            uploadSData();


        }else if (view == uploadAll){

            if (validate(firstName) || validate(lastName) || validate(age) || validateNationalId() ||
                    validate(passportNumber) || validate(birthDate) || validate(locationOfBirth) ||
                    validate(citizenship) || validate(nationality)){

                if (imageSelected || imageCamera || imageDownloaded){


                    if(progressDialog != null){
                        progressDialog.dismiss();
                    }
                    progressDialog = new ProgressDialog(PersonalActivity.this,R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Updating...");
                    progressDialog.show();

                    uploadPData();

                    uploadSData();
                }else {
                    Toast.makeText(getApplicationContext(),"Select a profile photo",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(),"Invalid Input",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.CHECK_PD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // Create a JSONObject from the JSON response string
                    JSONObject baseJsonResponse = new JSONObject(response);

                    if(!baseJsonResponse.getBoolean("error")){

                        String firstNameTxt = baseJsonResponse.getString("first_name");
                        String lastNameTxt = baseJsonResponse.getString("last_name");
                        firstNameTxt = firstNameTxt.substring(0, 1).toUpperCase() + firstNameTxt.substring(1);
                        lastNameTxt = lastNameTxt.substring(0, 1).toUpperCase() + lastNameTxt.substring(1);
                        birthDateText = baseJsonResponse.getString("date_of_birth");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                        Date convertedDate = new Date();
                        try {
                            convertedDate = dateFormat.parse(birthDateText);
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(convertedDate);

                        Calendar today = Calendar.getInstance();

                        int ageInt = today.get(Calendar.YEAR) - cal.get(Calendar.YEAR);

                        if (today.get(Calendar.DAY_OF_YEAR) < myCalendar.get(Calendar.DAY_OF_YEAR)){
                            ageInt--;
                        }

                        ageText = String.valueOf(ageInt);

                        String nIdTxt = baseJsonResponse.getString("national_ID_number");
                        String pIdTxt = baseJsonResponse.getString("passport_number");
                        String genderTxt = baseJsonResponse.getString("gender");
                        String locationOfBirthTxt = baseJsonResponse.getString("location_of_birth");
                        String nationalityTxt = baseJsonResponse.getString("nationality");
                        String citizenshipTxt = baseJsonResponse.getString("country_of_citizenship");


                        firstName.setText((CharSequence) firstNameTxt);
                        lastName.setText((CharSequence) lastNameTxt);
                        birthDate.setText(birthDateText);
                        age.setText(ageText);
                        age.setInputType(InputType.TYPE_NULL);
                        nationalIdNumber.setText((CharSequence) nIdTxt);
                        passportNumber.setText((CharSequence) pIdTxt);
                        locationOfBirth.setText((CharSequence) locationOfBirthTxt);
                        citizenship.setText((CharSequence) citizenshipTxt);
                        nationality.setText((CharSequence) nationalityTxt);

                        if (genderTxt.equals("Male") || genderTxt.equals("male") ){
                            maleButton.setChecked(true);
                            genderText = "Male";
                        }else if (genderTxt.equals("Female") || genderTxt.equals("female")){
                            femaleButton.setChecked(true);
                            genderText = "Female";
                        }

                        String image = Constants.ROOT_URL + "/" + baseJsonResponse.getString("image");

                        imageDownloaded = true;
                        profileImage.setBackground(null);
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
                Toast.makeText(PersonalActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String ,String> params = new HashMap<String,String>();

                params.put("user_id",session.getUserId());

                return params;
            }
        };

        StringRequest stringRequest2 = new StringRequest(Request.Method.POST, Constants.CHECK_SC_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // Create a JSONObject from the JSON response string
                    JSONObject baseJsonResponse = new JSONObject(response);

                    if(!baseJsonResponse.getBoolean("error")){

                        maritalStatus = baseJsonResponse.getString("marital_status");
                        String wifeNameTxt = baseJsonResponse.getString("wif_name");
                        String childrenNoTxt = baseJsonResponse.getString("children_number");
                        militaryStatus = baseJsonResponse.getString("military_status");
                        String religionTxt = baseJsonResponse.getString("religion");
                        String driverLicenseNumberTxt = baseJsonResponse.getString("driver_license_number");

                        switch (maritalStatus){
                            case "Married":
                            case "married":
                                maritalStatusSpinner.setSelection(1);
                                wifeName.setText(wifeNameTxt);
                                childrenNo.setText(childrenNoTxt);
                                wifeName.setEnabled(true);
                                childrenNo.setEnabled(true);
                                break;
                            case "Widowed":
                            case "widowed":
                                maritalStatusSpinner.setSelection(2);
                                wifeName.setText(wifeNameTxt);
                                childrenNo.setText(childrenNoTxt);
                                wifeName.setEnabled(true);
                                childrenNo.setEnabled(true);
                                break;
                            case "Divorced":
                            case "divorced":
                                maritalStatusSpinner.setSelection(3);
                                wifeName.setText(wifeNameTxt);
                                childrenNo.setText(childrenNoTxt);
                                wifeName.setEnabled(true);
                                childrenNo.setEnabled(true);
                                break;
                            case "Single":
                            case "single":
                                maritalStatusSpinner.setSelection(4);
                                wifeName.setText("");
                                wifeName.setEnabled(false);
                                childrenNo.setText("");
                                childrenNo.setEnabled(false);
                                break;
                        }

                        switch (militaryStatus){
                            case "Completed":
                            case "completed":
                                militaryStatusSpinner.setSelection(1);
                                break;
                            case "Postponed":
                            case "postponed":
                                militaryStatusSpinner.setSelection(2);
                                break;
                            case "Exemption":
                            case "exemption":
                                militaryStatusSpinner.setSelection(3);
                                break;
                            case "Currently serving":
                            case "currently serving":
                                militaryStatusSpinner.setSelection(4);
                                break;
                            case "Does not apply":
                            case "does not apply":
                                militaryStatusSpinner.setSelection(5);
                                break;
                        }

                        religion.setText(religionTxt);
                        driverLicenseNumber.setText(driverLicenseNumberTxt);

                    }else{

                        Toast.makeText(getApplicationContext(), baseJsonResponse.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PersonalActivity.this,error.toString(),Toast.LENGTH_LONG).show();
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
        requestQueue.add(stringRequest2);
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
                        session.setUserImage(image);
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
                Toast.makeText(PersonalActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String image = getStringImage(bitmap);

                session.setUserImage(image);
                Map<String ,String> params = new HashMap<String,String>();

                params.put("user_id",session.getUserId());
                params.put("image",session.getUserImage());
                params.put("image_name",session.getEmail());

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void updateLabel() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        birthDate.setText(sdf.format(myCalendar.getTime()));
        birthDateText = sdf.format(myCalendar.getTime());
        Calendar today = Calendar.getInstance();

        int ageInt = today.get(Calendar.YEAR) - myCalendar.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < myCalendar.get(Calendar.DAY_OF_YEAR)){
            ageInt--;
        }

        ageText = String.valueOf(ageInt);

        age.setText(ageText);
        age.setInputType(InputType.TYPE_NULL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                profileImage.setBackground(null);
                profileImage.setImageBitmap(bitmap);

                imageSelected = true;

            } catch (IOException e) {

                e.printStackTrace();
            }
        }else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            profileImage.setBackground(null);
            profileImage.setImageBitmap(bitmap);

            imageCamera = true;
        }
    }

    public String getStringImage(Bitmap bm){
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,ba);
        byte[] imagebyte = ba.toByteArray();
        String encode = Base64.encodeToString(imagebyte,Base64.DEFAULT);
        return encode;
    }

    private void uploadPData(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.PD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // Create a JSONObject from the JSON response string
                    JSONObject baseJsonResponse = new JSONObject(response);

                    if(!baseJsonResponse.getBoolean("error")){

                        if(progressDialog != null || progressDialog.isShowing() ){
                            progressDialog.dismiss();
                        }
                        session.setUserImage(image);
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
                Toast.makeText(PersonalActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (bitmap != null){
                    image = getStringImage(bitmap);
                    session.setUserImage(image);
                }

                Map<String ,String> params = new HashMap<String,String>();

                params.put("user_id",session.getUserId());
                params.put("image",session.getUserImage());
                params.put("image_name",session.getEmail());
                params.put("first_name",firstName.getText().toString().trim());
                params.put("last_name",lastName.getText().toString().trim());
                params.put("date_of_birth", birthDateText);
                params.put("age",ageText);
                params.put("national_ID_number",nationalIdNumber.getText().toString().trim());
                params.put("passport_number",passportNumber.getText().toString().trim());
                params.put("gender",genderText);
                params.put("location_of_birth",locationOfBirth.getText().toString().trim());
                params.put("country_of_citizenship",citizenship.getText().toString().trim());
                params.put("nationality",nationality.getText().toString().trim());

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void uploadSData(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SC_URL, new Response.Listener<String>() {
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
                Toast.makeText(PersonalActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String ,String> params = new HashMap<String,String>();

                params.put("user_id",session.getUserId());
                params.put("marital_status", maritalStatus);
                params.put("wif_name",wifeName.getText().toString().trim());
                params.put("children_number",childrenNo.getText().toString().trim());
                params.put("military_status", militaryStatus);
                params.put("religion",religion.getText().toString().trim());
                params.put("driver_license_number",driverLicenseNumber.getText().toString().trim());

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public boolean validate (EditText editText){

        boolean valid = true;

        String text = editText.getText().toString().trim();

        if (text.isEmpty() || text.equals(""))
        {
            editText.setError("Invalid");
            valid = false;

        }else {
            editText.setError(null);
        }

        return valid;
    }

    public boolean validateNationalId (){

        boolean valid = true;

        String nID = nationalIdNumber.getText().toString().trim();

        if (nID.isEmpty()|| nID.length() != 14)
        {
            nationalIdNumber.setError("National ID number must be 14 digit");
            valid = false;

        }else {
            nationalIdNumber.setError(null);
        }

        return valid;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        if (adapterView.getId() == maritalStatusSpinner.getId()){
            switch (position){
                case 0:
                    maritalStatus = "";
                    wifeName.setEnabled(false);
                    wifeName.setText("");
                    childrenNo.setEnabled(false);
                    childrenNo.setText("");
                    break;
                case 1:
                case 2:
                case 3:
                    maritalStatus = maritalStatusArray.get(position);
                    wifeName.setEnabled(true);
                    childrenNo.setEnabled(true);
                    break;
                case  4:
                    maritalStatus = maritalStatusArray.get(position);
                    wifeName.setEnabled(false);
                    wifeName.setText("");
                    childrenNo.setEnabled(false);
                    childrenNo.setText("");
                    break;
            }

        }else if (adapterView.getId() == militaryStatusSpinner.getId()){
            switch (position){
                case 0:
                    militaryStatus = "";
                    break;
                default:
                    militaryStatus = militaryStatusArray.get(position);
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

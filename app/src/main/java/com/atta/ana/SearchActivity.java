package com.atta.ana;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atta.ana.classes.Branch;
import com.atta.ana.classes.Constants;
import com.atta.ana.classes.OfficialParties;
import com.atta.ana.classes.OrgCustomListViewAdapter;
import com.atta.ana.classes.Organization;
import com.atta.ana.classes.PartyCustomListViewAdapter;
import com.atta.ana.classes.Service;
import com.atta.ana.classes.ServiceCustomListViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    String selected;

    List<OfficialParties> listParties = new ArrayList<OfficialParties>();

    List<Service> listServices = new ArrayList<Service>();

    List<Organization> listOrganizations = new ArrayList<Organization>();

    PartyCustomListViewAdapter partyCustomListViewAdapter;

    ServiceCustomListViewAdapter serviceCustomListViewAdapter;

    OrgCustomListViewAdapter orgCustomListViewAdapter;

    ListView listView;

    String Url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (getIntent().hasExtra("selected")) {
            selected = getIntent().getStringExtra("selected");


        }else {
            Toast.makeText(getApplicationContext(),"wrong selection",Toast.LENGTH_LONG).show();
        }

        listView = (ListView) findViewById(R.id.list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                AlertDialog alertDialog = new AlertDialog.Builder(SearchActivity.this).create();




                switch (selected){
                    case "official parties":

                        OfficialParties currentParties = partyCustomListViewAdapter.getItem(position);
                        Intent intent = new Intent(SearchActivity.this,PartyDetailsActivity.class);

                        intent.putExtra("official parties", currentParties);
                        startActivity(intent);
                        break;
                    case "services":
                        final Service currentService = serviceCustomListViewAdapter.getItem(position);
                        alertDialog.setTitle("you can get this service from:");
                        alertDialog.setMessage(currentService.getPartName());
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Go to Website",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(currentService.getWebsite()));
                                        startActivity(i);
                                    }
                                });
                        break;
                    case "organizations":

                        Organization currentOrg = orgCustomListViewAdapter.getItem(position);
                        alertDialog.setTitle("you need the below Docs:");
                        alertDialog.setMessage(currentOrg.getDocuoment());
                        break;
                }


                alertDialog.show();

                //Toast.makeText(getApplicationContext(), String.valueOf(position), Toast.LENGTH_LONG).show();
            }
        });

        switch (selected){
            case "official parties":
                Url = Constants.GET_PARTIES_URL;
                break;
            case "services":
                Url = Constants.GET_SERVICES_URL;
                break;
            case "organizations":
                Url = Constants.GET_ORG_URL;
                break;
        }

        requestResults();

    }


    private void requestResults() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                switch (selected){
                    case "official parties":
                        displayParties(response);
                        break;
                    case "services":
                        displayServices(response);
                        break;
                    case "organizations":
                        displayOrganizations(response);
                        break;
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SearchActivity.this,error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void displayOrganizations(String response) {

        try {

            JSONObject baseJsonResponse = new JSONObject(response);
            if(!baseJsonResponse.getBoolean("error")){


                orgCustomListViewAdapter = new OrgCustomListViewAdapter(SearchActivity.this, listOrganizations);

                orgCustomListViewAdapter.clear();
                listOrganizations.clear();

                // which represents a list of features (or mobiles).
                JSONArray servicesArray = baseJsonResponse.getJSONArray("organizations");

                for (int i=0; i<servicesArray.length();i++) {

                    JSONObject currentService = servicesArray.getJSONObject(i);
                    int organizationId = currentService.getInt("id");
                    String orgName = currentService.getString("org_name");
                    String docs = currentService.getString("docs");




                    Organization organization = new Organization(organizationId, orgName, docs);
                    listOrganizations.add(organization);

                }
                listView.setAdapter(orgCustomListViewAdapter);

            }else{

                Toast.makeText(getApplicationContext(), baseJsonResponse.getString("error_msg"), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void displayServices(String response) {


        try {

            JSONObject baseJsonResponse = new JSONObject(response);
            if(!baseJsonResponse.getBoolean("error")){


                serviceCustomListViewAdapter = new ServiceCustomListViewAdapter(SearchActivity.this, listServices);

                serviceCustomListViewAdapter.clear();
                listServices.clear();

                // which represents a list of features (or mobiles).
                JSONArray servicesArray = baseJsonResponse.getJSONArray("services");

                for (int i=0; i<servicesArray.length();i++) {

                    JSONObject currentService = servicesArray.getJSONObject(i);
                    int serviceId = currentService.getInt("id");
                    int partyId = currentService.getInt("party_id");
                    String partyName = currentService.getString("party_name");
                    String website = currentService.getString("website");
                    String serviceName = currentService.getString("service");




                    Service service = new Service(serviceId, serviceName, partyName, website);
                    listServices.add(service);

                }
                listView.setAdapter(serviceCustomListViewAdapter);

            }else{

                Toast.makeText(getApplicationContext(), baseJsonResponse.getString("error_msg"), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void displayParties(String response){

        try {

            JSONObject baseJsonResponse = new JSONObject(response);
            if(!baseJsonResponse.getBoolean("error")){


                partyCustomListViewAdapter = new PartyCustomListViewAdapter(SearchActivity.this, listParties);

                partyCustomListViewAdapter.clear();
                listParties.clear();

                // which represents a list of features (or mobiles).
                JSONArray partiesArray = baseJsonResponse.getJSONArray("parties");

                for (int i=0; i<partiesArray.length();i++) {

                    JSONObject currentParty = partiesArray.getJSONObject(i);
                    int id = currentParty.getInt("id");
                    String partyName = currentParty.getString("party_name");
                    String website = currentParty.getString("website");

                    listServices.clear();
                    JSONArray servicesArray = currentParty.getJSONArray("services");

                    for (int j=0; j<servicesArray.length();j++) {
                        JSONObject currentService = servicesArray.getJSONObject(j);
                        int serviceId = currentService.getInt("id");
                        int partyId = currentService.getInt("party_id");
                        String serviceName = currentService.getString("service");

                        Service service = new Service(serviceId, serviceName, partyName, website);
                        listServices.add(service);
                    }

                    List<Branch> listBranches = new ArrayList<Branch>();
                    JSONArray branchesArray = currentParty.getJSONArray("branches");

                    for (int k=0; k<branchesArray.length();k++) {
                        JSONObject currentBranch = branchesArray.getJSONObject(k);
                        int branchId = currentBranch.getInt("id");
                        int partyId = currentBranch.getInt("party_id");
                        String branchName = currentBranch.getString("branch_name");
                        String address = currentBranch.getString("address");
                        String phone = currentBranch.getString("phone");
                        String location_longitude = currentBranch.getString("location_lat");
                        String location_latitude = currentBranch.getString("location_lon");

                        Branch branchArray = new Branch(branchId, branchName, address, phone, location_longitude, location_latitude);
                        listBranches.add(branchArray);
                    }



                    OfficialParties officialParty = new OfficialParties(id, partyName, website, listServices, listBranches);

                    listParties.add(officialParty);

                }
                listView.setAdapter(partyCustomListViewAdapter);

            }else{

                Toast.makeText(getApplicationContext(), baseJsonResponse.getString("error_msg"), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

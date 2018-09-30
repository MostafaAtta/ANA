package com.atta.ana;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.atta.ana.classes.Branch;
import com.atta.ana.classes.BranchCustomListViewAdapter;
import com.atta.ana.classes.OfficialParties;
import com.atta.ana.classes.Service;
import com.atta.ana.classes.ServiceCustomListViewAdapter;

import java.util.List;

public class PartyDetailsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    List<Service> listServices ;

    List<Branch> listBranch ;

    OfficialParties officialParties;

    TextView partyNameText;

    ImageView websiteImage;

    ListView servicesListView, branchesListView;

    BranchCustomListViewAdapter branchCustomListViewAdapter;

    ServiceCustomListViewAdapter serviceCustomListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_details);

        if (getIntent().hasExtra("official parties")) {

            officialParties = (OfficialParties) getIntent().getSerializableExtra("official parties");

        }


        partyNameText = (TextView) findViewById(R.id.party_name_text);
        websiteImage = (ImageView) findViewById(R.id.website_image);
        servicesListView = (ListView) findViewById(R.id.service_list);
        branchesListView = (ListView) findViewById(R.id.branches_list);

        websiteImage.setOnClickListener(this);

        partyNameText.setText(officialParties.getPartyName());

        serviceCustomListViewAdapter = new ServiceCustomListViewAdapter(PartyDetailsActivity.this, officialParties.getListServices());

        servicesListView.setAdapter(serviceCustomListViewAdapter);

        branchCustomListViewAdapter = new BranchCustomListViewAdapter(PartyDetailsActivity.this, officialParties.getListBranches());

        branchesListView.setAdapter(branchCustomListViewAdapter);

        branchesListView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        AlertDialog alertDialog = new AlertDialog.Builder(PartyDetailsActivity.this).create();
        final Branch branch = branchCustomListViewAdapter.getItem(position);

        alertDialog.setTitle(branch.getBranchName());
        alertDialog.setMessage("Address: " + branch.getAddress() + "\n \n" + "Phone: " + branch.getPhone());
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Location",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        //shop.getLocationLatitude() +", "+ shop.getLocationLongitude()
                        Uri locationUri = Uri.parse("geo:0,0?q="+ branch.getLocationLatitude() +", "+ branch.getLocationLongitude()
                                + "("+ officialParties.getPartyName() + " " + branch.getBranchName()+")");

                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, locationUri);
                        // Make the Intent explicit by setting the Google Maps package
                        mapIntent.setPackage("com.google.android.apps.maps");
                        // Attempt to start an activity that can handle the Intent

                        startActivity(mapIntent);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Call",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        Intent i = new Intent(Intent.ACTION_DIAL);
                        i.setData(Uri.parse("tel:" + branch.getPhone()));
                        startActivity(i);
                    }
                });

        alertDialog.show();

    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(officialParties.getWebsite()));
        startActivity(i);
    }
}

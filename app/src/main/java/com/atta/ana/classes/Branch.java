package com.atta.ana.classes;

import java.io.Serializable;

public class Branch implements Serializable {

    int id;

    String branchName, phone, address,	locationLatitude, locationLongitude;

    public Branch(int id, String branchName, String address, String phone, String locationLatitude, String locationLongitude) {

        this.id = id;
        this.branchName = branchName;
        this.phone = phone;
        this.address = address;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
    }

    public int getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getLocationLatitude() {
        return locationLatitude;
    }

    public String getLocationLongitude() {
        return locationLongitude;
    }

    public String getBranchName() {
        return branchName;
    }
}

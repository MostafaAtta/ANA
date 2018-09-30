package com.atta.ana.classes;

import java.io.Serializable;
import java.util.List;

public class OfficialParties implements Serializable {

    int id;

    String partyName, website;

    List<Service> listServices;

    List<Branch> listBranches;

    public OfficialParties(int id, String partyName, String website, List<Service> listServices, List<Branch> listBranches) {
        this.id = id;
        this.partyName = partyName;
        this.website = website;
        this.listServices = listServices;
        this.listBranches = listBranches;
    }

    public int getId() {
        return id;
    }

    public String getPartyName() {
        return partyName;
    }

    public String getWebsite() {
        return website;
    }

    public List<Service> getListServices() {
        return listServices;
    }

    public List<Branch> getListBranches() {
        return listBranches;
    }
}

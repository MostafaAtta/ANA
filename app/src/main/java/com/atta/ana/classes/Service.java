package com.atta.ana.classes;

import java.io.Serializable;

public class Service implements Serializable {

    int id;

    String name, partName, website;

    public Service(int id, String name, String partName, String website) {
        this.id = id;
        this.name = name;
        this.partName = partName;
        this.website = website;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPartName() {
        return partName;
    }

    public String getWebsite() {
        return website;
    }
}

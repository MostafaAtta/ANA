package com.atta.ana.classes;

public class Organization {

    int id;

    String name, docuoment;

    public Organization(int id, String name, String docuoment) {
        this.id = id;
        this.name = name;
        this.docuoment = docuoment;

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDocuoment() {
        return docuoment;
    }
}

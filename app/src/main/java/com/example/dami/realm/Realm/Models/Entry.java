package com.example.dami.realm.Realm.Models;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Entry extends RealmObject {
    @PrimaryKey
    private String id;
    private String text;
    private Date createdAt;

    public Entry() {
    }

    public Entry(String text) {
        this.id = UUID.randomUUID().toString();
        this.text = text;
        this.createdAt = new Date();
    }

    public String getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

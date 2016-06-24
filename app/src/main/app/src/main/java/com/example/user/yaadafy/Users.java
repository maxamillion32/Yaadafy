package com.example.user.yaadafy;

import java.util.HashMap;

/**
 * Created by USER on 2/7/2016.
 */
public class Users {
    private String name;
    private String email;
    private HashMap<String, Object> timestampJoined;
    private boolean hasLoggedInWithPassword;


    /**
     * Required public constructor
     */
    public Users() {
    }

    /**
     * Use this constructor to create new User.
     * Takes user name, email and timestampJoined as params
     *
     * @param name
     * @param email
     * @param timestampJoined
     */
    public Users(String name, String email, HashMap<String, Object> timestampJoined) {
        this.name = name;
        this.email = email;
        this.timestampJoined = timestampJoined;
        this.hasLoggedInWithPassword = false;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public HashMap<String, Object> getTimestampJoined() {
        return timestampJoined;
    }

    public boolean isHasLoggedInWithPassword() {
        return hasLoggedInWithPassword;
    }
}
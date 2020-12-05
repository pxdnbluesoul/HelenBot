package com.helen.search;

import java.util.Objects;
import java.util.Optional;

public class AccountCredentials {

    private String devKey;
    private Optional<String> userName;
    private Optional<String> password;
    private Optional<String> userKey;

    public AccountCredentials( String devKey,  String userName,  String password) {
        Objects.requireNonNull(devKey, " The dev key cannot be null!");
        this.devKey = devKey;
        this.password = Optional.ofNullable(password);
        this.userName = Optional.ofNullable(userName);
        this.userKey = Optional.empty();
    }

    public String getDevKey() {
        return devKey;
    }

    public Optional<String> getPassword() {
        return password;
    }

    public Optional<String> getUserName() {
        return userName;
    }

    public Optional<String> getUserSessionKey() {
        return userKey;
    }

   public void setUserKey(Optional<String> userKey) {
        this.userKey = userKey;
    }
}
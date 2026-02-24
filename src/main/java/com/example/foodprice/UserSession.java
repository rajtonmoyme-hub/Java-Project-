package com.example.foodprice;

public final class UserSession {

    // 2 type login role: admin na user
    public enum Role { ADMIN, USER }

    // ekhon ke login ase seta global vabe store
    private static Role currentRole;
    private static String userIdentifier; // email/phone store thakbe

    // object banano jabe na (pure static class)
    private UserSession() {
    }

    // login korle role + identity save hobe
    public static void login(Role role, String identifier) {
        currentRole = role;
        userIdentifier = identifier;
    }

    // admin kina check
    public static boolean isAdmin() {
        return currentRole == Role.ADMIN;
    }

    // normal user kina check
    public static boolean isUser() {
        return currentRole == Role.USER;
    }

    // currently ke login ase return
    public static String getUserIdentifier() {
        return userIdentifier;
    }
}
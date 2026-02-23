package com.example.foodprice;

public final class UserSession {
    public enum Role { ADMIN, USER }

    private static Role currentRole;
    private static String userIdentifier;

    private UserSession() {
    }

    public static void login(Role role, String identifier) {
        currentRole = role;
        userIdentifier = identifier;
    }

    public static void logout() {
        currentRole = null;
        userIdentifier = null;
    }

    public static boolean isAdmin() {
        return currentRole == Role.ADMIN;
    }

    public static boolean isUser() {
        return currentRole == Role.USER;
    }

    public static String getUserIdentifier() {
        return userIdentifier;
    }
}

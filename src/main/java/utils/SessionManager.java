package utils;

import models.Candidat;
import models.Utilisateur;

import java.time.LocalDateTime;

public class SessionManager {
    private static SessionManager instance;
    private Utilisateur currentUser;
    private LocalDateTime loginTime;
    private LocalDateTime lastActivityTime;

    // Session timeout in minutes (default 30 minutes)
    private int sessionTimeout = 30;
    private static Candidat candidat;
    private static boolean isRH;

    public static void setCandidat(Candidat candidat) {
        SessionManager.candidat = candidat;
    }

    public static Candidat getCandidat() {
        return candidat;
    }

    public static void setIsRH(boolean isRH) {
        SessionManager.isRH = isRH;
    }

    public static boolean getIsRH() {
        return isRH;
    }
    private SessionManager() {
        currentUser = null;
    }

    /**
     * Get the singleton instance of SessionManager.
     *
     * @return The SessionManager instance
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Set the current logged-in user and initialize session times.
     *
     * @param user The authenticated user
     */
    public void setCurrentUser(Utilisateur user) {
        this.currentUser = user;
        this.loginTime = LocalDateTime.now();
        this.lastActivityTime = LocalDateTime.now();
    }

    /**
     * Update the last activity time to prevent session timeout.
     */
    public void updateLastActivity() {
        this.lastActivityTime = LocalDateTime.now();
    }

    /**
     * Check if the session has timed out.
     *
     * @return true if session has timed out, false otherwise
     */
    public boolean isSessionTimedOut() {
        if (lastActivityTime == null) return true;

        LocalDateTime timeoutThreshold = lastActivityTime.plusMinutes(sessionTimeout);
        return LocalDateTime.now().isAfter(timeoutThreshold);
    }

    /**
     * Get the current logged-in user.
     *
     * @return The current user or null if no user is logged in
     */
    public Utilisateur getCurrentUser() {
        // Check for timeout before returning the user
        if (isSessionTimedOut()) {
            logout();
            return null;
        }

        // Update last activity time
        updateLastActivity();
        return currentUser;
    }

    /**
     * Check if a user is currently logged in and session is valid.
     *
     * @return true if a user is logged in and session is valid, false otherwise
     */
    public boolean isLoggedIn() {
        if (currentUser == null) return false;
        return !isSessionTimedOut();
    }

    /**
     * Check if the current user has a specific role.
     *
     * @param role The role to check
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(String role) {
        if (!isLoggedIn()) return false;

        String userRole = currentUser.getroles();

        // Handle cases with ROLE_ prefix
        if (userRole.startsWith("ROLE_")) {
            userRole = userRole.substring(5);
        }

        // Handle role parameter with ROLE_ prefix
        String normalizedRole = role;
        if (role.startsWith("ROLE_")) {
            normalizedRole = role.substring(5);
        }

        return userRole.equalsIgnoreCase(normalizedRole);
    }

    /**
     * Set the session timeout duration in minutes.
     *
     * @param minutes The timeout duration in minutes
     */
    public void setSessionTimeout(int minutes) {
        this.sessionTimeout = minutes;
    }

    /**
     * Log out the current user by clearing the session.
     */
    public void logout() {
        currentUser = null;
        loginTime = null;
        lastActivityTime = null;
    }

    /**
     * Get the login time for the current session.
     *
     * @return The login time or null if no active session
     */
    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    /**
     * Get the last activity time for the current session.
     *
     * @return The last activity time or null if no active session
     */
    public LocalDateTime getLastActivityTime() {
        return lastActivityTime;
    }
}

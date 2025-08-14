package models;

import org.mindrot.jbcrypt.BCrypt;

public class MdpHash {
    // Cost parameter for BCrypt - higher is more secure but slower
    // Recommended range is 10-12 for most applications
    private static final int BCRYPT_ROUNDS = 12;

    /**
     * Hashes a password using BCrypt algorithm
     * BCrypt automatically generates and includes the salt
     */
    public static String hashPassword(String password) {
        // Check if the password is null or empty
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        // Check if the password is already a BCrypt hash
        if (isAlreadyHashed(password)) {
            return password; // Already hashed, return as is
        }

        // Generate a BCrypt hash with salt
        return BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    /**
     * Verify if the provided password matches the stored hash
     */
    public static boolean checkPassword(String password, String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }

        try {
            // BCrypt.checkpw handles the comparison securely
            return BCrypt.checkpw(password, storedHash);
        } catch (IllegalArgumentException e) {
            // This catches malformed BCrypt hashes
            System.err.println("Error verifying password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a string is likely already a BCrypt hash
     * BCrypt hashes start with "$2a$", "$2b$", or "$2y$" and are typically 60 characters
     */
    public static boolean isAlreadyHashed(String password) {
        return password != null &&
                (password.startsWith("$2a$") ||
                        password.startsWith("$2b$") ||
                        password.startsWith("$2y$"));
    }

    public static void main(String[] args) {
        String password = "monMotDePasse";
        String hashedPassword = hashPassword(password);
        System.out.println("Mot de passe haché : " + hashedPassword);

        // Verify password
        boolean isValid = checkPassword(password, hashedPassword);
        System.out.println("Password verification result: " + isValid);
    }
}
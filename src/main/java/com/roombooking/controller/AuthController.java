package com.roombooking.controller;

import com.roombooking.dao.UserDAO;
import com.roombooking.model.User;
import org.mindrot.jbcrypt.BCrypt;
import java.util.List;

/**
 * Controller for handling authentication and user management
 */
public class AuthController {
    private final UserDAO userDAO;

    public AuthController() {
        this.userDAO = new UserDAO();
    }

    /**
     * Attempts to log in a user
     * @param username the username
     * @param password the password
     * @return the logged-in user, or null if login failed
     */
    public User login(String username, String password) {
        User user = userDAO.getUserByUsername(username);
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            return user;
        }
        return null;
    }
    
    /**
     * Registers a new user
     * @param username the username
     * @param password the password
     * @param isAdmin true if the user is an admin, false otherwise
     * @return true if registration successful, false otherwise
     */
    public boolean register(String username, String password, boolean isAdmin) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return false;
        }

        if (userDAO.getUserByUsername(username) != null) {
            return false;
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        newUser.setAdmin(isAdmin);
        newUser.setActive(true);
        
        return userDAO.createUser(newUser);
    }
    
    /**
     * Registers a new user
     * @param username the username
     * @param fullName the full name
     * @param email the email
     * @param password the password
     * @return true if registration successful, false otherwise
     */
    public boolean registerUser(String username, String fullName, String email, String password) {
        // Check if username already exists
        if (userDAO.getUserByUsername(username) != null) {
            return false;
        }
        
        // Create new user
        User newUser = new User(username, fullName, email, BCrypt.hashpw(password, BCrypt.gensalt()));
        newUser.setActive(true);
        newUser.setAdmin(false);
        
        return userDAO.createUser(newUser);
    }
    
    /**
     * Gets all users
     * @return a list of all users
     */
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }
    
    /**
     * Gets a user by ID
     * @param id the user ID
     * @return the user with the specified ID, or null if no user exists with that ID
     */
    public User getUser(int id) {
        return userDAO.getUser(id);
    }
    
    /**
     * Updates a user's admin and active status
     * @param id the user ID
     * @param isAdmin true if the user is an admin, false otherwise
     * @param isActive true if the user is active, false otherwise
     * @return true if the user was updated successfully, false otherwise
     */
    public boolean updateUser(int id, boolean isAdmin, boolean isActive) {
        User user = userDAO.getUser(id);
        if (user == null) {
            return false;
        }
        user.setAdmin(isAdmin);
        user.setActive(isActive);
        return userDAO.updateUser(user);
    }
} 
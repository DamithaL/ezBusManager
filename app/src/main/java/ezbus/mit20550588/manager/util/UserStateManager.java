package ezbus.mit20550588.manager.util;

import static ezbus.mit20550588.manager.util.Constants.Log;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import ezbus.mit20550588.manager.data.database.AppDatabase;
import ezbus.mit20550588.manager.data.database.ClearDatabaseAsyncTask;
import ezbus.mit20550588.manager.data.model.UserModel;

public class UserStateManager {
    private static final String KEY_USER_LOGGED_IN = "user_logged_in";
    private static final String KEY_USER_MODEL = "user_model";
    private UserModel user;
    private static UserStateManager instance;
    private SharedPreferences preferences;
    private Gson gson; // Gson library for JSON serialization/deserialization


    public UserStateManager(Context context) {
        preferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        gson = new Gson();
        loadUserModel(); // Load user model from SharedPreferences when the class is instantiated

    }

    public static synchronized UserStateManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("UserStateManager is not initialized. Call init() first.");
        }
        return instance;
    }

    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new UserStateManager(context.getApplicationContext());
        }
    }

    public boolean isUserLoggedIn() {
        return preferences.getBoolean(KEY_USER_LOGGED_IN, false);
    }

    public void setUserLoggedIn(boolean loggedIn) {
        preferences.edit().putBoolean(KEY_USER_LOGGED_IN, loggedIn).apply();
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
        saveUserModel(); // Save user model to SharedPreferences when it's set
    }

    private void saveUserModel() {
        String userModelJson = gson.toJson(user);
        preferences.edit().putString(KEY_USER_MODEL, userModelJson).apply();
    }

    private void loadUserModel() {
        String userModelJson = preferences.getString(KEY_USER_MODEL, null);
        if (userModelJson != null) {
            Type type = new TypeToken<UserModel>() {
            }.getType();
            user = gson.fromJson(userModelJson, type);
        }
    }


    public void logout(Context context) {
        // Clear user authentication data
        clearUserData();

        // Set user as not logged in
        setUserLoggedIn(false);

        // Optionally, set the user to null
        UserStateManager.getInstance().setUser(null);

        // Reset user data in FleetStateManager (replace with your actual class)
        FleetStateManager.getInstance().clearFleetData();

        // Clear tables in the database
        ClearDatabaseAsyncTask task = new ClearDatabaseAsyncTask(AppDatabase.getInstance(context));
        task.performBackgroundTask();
    }

    private void clearUserData() {
        // To REMOVE ALL SHARED PREFERENCES DATA:
        // preferences.edit().clear().apply();

        // Clear both user model and logged-in status
        preferences.edit().remove(KEY_USER_MODEL).remove(KEY_USER_LOGGED_IN).apply();
    }
}

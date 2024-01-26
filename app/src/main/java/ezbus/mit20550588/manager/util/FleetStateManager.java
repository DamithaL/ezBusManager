package ezbus.mit20550588.manager.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import ezbus.mit20550588.manager.data.model.FleetModel;
import ezbus.mit20550588.manager.data.model.UserModel;

public class FleetStateManager {
    private static final String KEY_FLEET_LOGGED_IN = "fleet_logged_in";
    private static final String KEY_FLEET_MODEL = "fleet_model";
    private FleetModel fleet;
    private static FleetStateManager instance;
    private SharedPreferences preferences;
    private Gson gson; // Gson library for JSON serialization/deserialization


    public FleetStateManager(Context context) {
        preferences = context.getSharedPreferences("FleetPreferences", Context.MODE_PRIVATE);
        gson = new Gson();
        loadFleetModel(); // Load fleet model from SharedPreferences when the class is instantiated

    }

    public static synchronized FleetStateManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("FleetStateManager is not initialized. Call init() first.");
        }
        return instance;
    }

    public static synchronized void init(Context context) {
        if (instance == null) {
            instance = new FleetStateManager(context.getApplicationContext());
        }
    }

    public boolean isFleetLoggedIn() {
        return preferences.getBoolean(KEY_FLEET_LOGGED_IN, false);
    }

    public void setFleetLoggedIn(boolean loggedIn) {
        preferences.edit().putBoolean(KEY_FLEET_LOGGED_IN, loggedIn).apply();
    }

    public FleetModel getFleet() {
        return fleet;
    }

    public void setFleet(FleetModel fleet) {
        this.fleet = fleet;
        saveFleetModel(); // Save user model to SharedPreferences when it's set
    }

    private void saveFleetModel() {
        String fleetModelJson = gson.toJson(fleet);
        preferences.edit().putString(KEY_FLEET_MODEL, fleetModelJson).apply();
    }

    private void loadFleetModel() {
        String fleetModelJson = preferences.getString(KEY_FLEET_MODEL, null);
        if (fleetModelJson != null) {
            Type type = new TypeToken<FleetModel>() {
            }.getType();
            fleet = gson.fromJson(fleetModelJson, type);
        }
    }

    // method to clear fleet data
    public void clearFleetData() {

        // Set user as not logged in
        setFleetLoggedIn(false);

        // Optionally, set the user to null
        FleetStateManager.getInstance().setFleet(null);

        // Clear both fleet model and logged-in status
        preferences.edit().remove(KEY_FLEET_MODEL).remove(KEY_FLEET_LOGGED_IN).apply();


    }


}

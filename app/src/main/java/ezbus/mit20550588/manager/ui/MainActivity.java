package ezbus.mit20550588.manager.ui;

import static ezbus.mit20550588.manager.util.Constants.Log;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ezbus.mit20550588.manager.R;
import ezbus.mit20550588.manager.data.model.BusModel;
import ezbus.mit20550588.manager.data.network.requests.BusRegistrationRequest;
import ezbus.mit20550588.manager.data.network.responses.BusRegResponse;
import ezbus.mit20550588.manager.data.viewModel.BusViewModel;
import ezbus.mit20550588.manager.ui.Login.FleetRegistration;
import ezbus.mit20550588.manager.ui.Login.Login;
import ezbus.mit20550588.manager.ui.Settings.Settings;
import ezbus.mit20550588.manager.ui.adapters.BusListAdapter;
import ezbus.mit20550588.manager.util.FleetStateManager;
import ezbus.mit20550588.manager.util.UserStateManager;
import yuku.ambilwarna.AmbilWarnaDialog;


public class MainActivity extends AppCompatActivity {
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private SharedPreferences preferences;
    private int currentColor;
    private TextView colorEditText;
    private List<String> validRouteNames = new ArrayList<>();
    private List<String> validRouteNumbers = new ArrayList<>();
    private AutoCompleteTextView routeNumberAutoCompleteTextView;
    private AutoCompleteTextView routeNameAutoCompleteTextView;
    private BusListAdapter busListAdapter;
    private FleetStateManager fleetStateManager;
    private UserStateManager userManager;
    private BusViewModel busViewModel;

    // ------------------------------- LIFECYCLE METHODS ------------------------------- //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log("MainActivity", "onCreate", "Started");

        findViewById(R.id.loadingProgressBar).setVisibility(View.VISIBLE);
        busViewModel = new ViewModelProvider(this).get(BusViewModel.class);
        managerAuthentication();
        checkPermissions();



    }

    // ------------------------------- PERMISSION RELATED METHODS ------------------------------- //
    private void checkPermissions() {
        checkGoogleServicesAvailability();

    }

    public boolean checkGoogleServicesAvailability() {
        Log("checkGoogleServicesAvailability", "checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            // everything is fine and the user can make map requests
            Log("checkGoogleServicesAvailability", "Google Play Services is working");

            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // an error occurred but we can resolve it
            Log("checkGoogleServicesAvailability", "ERROR", "an error occurred but we can fix it");

            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();

            Log("checkGoogleServicesAvailability", "ERROR", "an error occurred but we can fix it");
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
            Log("checkGoogleServicesAvailability", "ERROR", "You can't make map requests");

        }
        return false;
    }


    // ------------------------------- INITIALIZATION METHODS ------------------------------- //
    private void managerAuthentication() {
        Log("managerAuthentication", "starting");
        userManager = UserStateManager.getInstance();

        // Set up an OnPreDrawListener to the root view.
        final View content = findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        // Check whether the initial data is ready.
//                        if (mViewModel.isReady()) {
                        // Check if the user is authenticated

                        if (userManager.isUserLoggedIn()) {
                            // User is authenticated, proceed to the main part of the app
                            // The content is ready. Start drawing.
                            content.getViewTreeObserver().removeOnPreDrawListener(this);
                            Log("managerAuthentication", "User logged in");
                            fleetAuthentication();
                            return true;
                        } else {
                            // User is not authenticated, start the authentication flow
                            Intent authIntent = new Intent(MainActivity.this, Login.class);
                            startActivity(authIntent);
                            // Finish the MainActivity to prevent it from being shown to the user
                            finish();
                            // Return false to suspend drawing until the authentication flow completes
                            Log("managerAuthentication", "User logged out");
                            return false;
                        }

                    }
                });
    }

    private void fleetAuthentication() {
        Log("fleetAuthentication", "starting");
        fleetStateManager = FleetStateManager.getInstance();
        try {
            Log("fleetAuthentication", "fleetStateManager", "fleetStateManager" + fleetStateManager.toString());
            Log("fleetAuthentication", "fleetStateManager", "fleet" + fleetStateManager.getFleet().toString());
            Log("fleetAuthentication", "fleetStateManager", "FleetRegistrationNumber" + fleetStateManager.getFleet().getFleetRegistrationNumber());
        } catch (Exception e) {
            Log("fleetAuthentication", "ERROR", e.getMessage());
        }


        if (!fleetStateManager.isFleetLoggedIn()) {
            Log("fleetAuthentication", "Fleet not logged in");
            Intent authIntent = new Intent(MainActivity.this, FleetRegistration.class);
            startActivity(authIntent);
            finish();
        } else {
            Log("fleetAuthentication", "Fleet logged in");

            busViewModel.loadBusAccountsLiveData().observe(this, busAccounts -> {
                if (busAccounts != null) {
                    Log("fleetAuthentication", "loadBusAccountsLiveData", "Received");
                    if (busAccounts.size() > 0) {
                        uiInitializations();
                    }
                }
            });

            busViewModel.loadAllBusAccounts(userManager.getUser().getEmail());
        }
    }


    private void uiInitializations() {

        Log("MainActivity", "uiInitializations", "started");

        initSettingsButton();
        busViewModel.busCountLiveData().observe(this, busCount -> {
            if (busCount != null) {
                TextView busCountText = findViewById(R.id.countOfBussesTextView);

                if (busCount == 0) {
                    String newText = "No Buses Added Yet";
                    busCountText.setText(newText);

                    findViewById(R.id.NoBussesLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.AddBusButton).setVisibility(View.GONE);
                    findViewById(R.id.pageTitle).setVisibility(View.GONE);
                    findViewById(R.id.dashboardLayout).setVisibility(View.GONE);

                    findViewById(R.id.AddFirstBusButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            findViewById(R.id.NoBussesLayout).setVisibility(View.GONE);
                            initNewBusForm();
                        }
                    });
                } else {

                    if (busCount == 1) {
                        String newText = "You currently have " + busCount + " bus in your fleet.";
                        busCountText.setText(newText);
                    } else {
                        String newText = "You currently have " + busCount + " buses in your fleet.";
                        busCountText.setText(newText);
                    }
                    initDashboard();
                }
            }

        });
        busViewModel.getBusAccountsCount();

        findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);
    }

    private void initDashboard() {

        Log("MainActivity", "initDashboard", "started");

        findViewById(R.id.BackButton).setVisibility(View.GONE);
        findViewById(R.id.addBusFormLayout).setVisibility(View.GONE);
        findViewById(R.id.AddBusButton).setVisibility(View.VISIBLE);
        findViewById(R.id.pageTitle).setVisibility(View.VISIBLE);
        findViewById(R.id.dashboardLayout).setVisibility(View.VISIBLE);

        findViewById(R.id.AddBusButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initNewBusForm();
            }
        });

        RecyclerView recyclerViewForBusses = (RecyclerView) findViewById(R.id.bus_recycler_view);
        recyclerViewForBusses.setLayoutManager(new LinearLayoutManager(this));
        //  recyclerViewForNewTickets.setHasFixedSize(true);

        busListAdapter = new BusListAdapter();

        recyclerViewForBusses.setAdapter(busListAdapter);
        busListAdapter.setRecyclerView(recyclerViewForBusses);
        busViewModel = new ViewModelProvider(this).get(BusViewModel.class);

    }

    private void initSettingsButton() {
        ImageView settingsButton = findViewById(R.id.SettingsButton);

        // Set an OnClickListener for the Settings Button
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the SettingsActivity when the fab is clicked
                Intent intent = new Intent(MainActivity.this, Settings.class);
                // Intent intent = new Intent(MainActivity.this, CheckoutActivity.class);
                startActivity(intent);
            }
        });

        Log("initSettingsButton", "initialized");
    }


    // ------------------------------- NEW BUS CREATION METHODS ------------------------------- //
    private void initNewBusForm() {

        Log("MainActivity", "initNewBusForm", "started");

        findViewById(R.id.dashboardLayout).setVisibility(View.GONE);
        findViewById(R.id.AddBusButton).setVisibility(View.GONE);
        findViewById(R.id.pageTitle).setVisibility(View.GONE);
        findViewById(R.id.addBusFormLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.BackButton).setVisibility(View.VISIBLE);

        // Back button
        ImageButton backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDashboard();
            }
        });

        // ------------ BUS ROUTE NUMBER AND NAME ------------ //
        routeNumberAutoCompleteTextView = findViewById(R.id.routeNumberAutoCompleteTextView);
        routeNameAutoCompleteTextView = findViewById(R.id.routeNameAutoCompleteTextView);
        initLiveDataObservingForNewBusForm();
        initFocusChangeListenerForRouteNumber();
        initFocusChangeListenerForRouteName();

        // ------------ BUS COLOUR ------------ //
        colorEditText = findViewById(R.id.colorEditText);
        colorEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

        // ------------ ADD BUS BUTTON ------------ //
        initNewBusFormSubmitButton();


    }

    private void initLiveDataObservingForNewBusForm() {

        busViewModel.getRouteNumbersLiveData().observe(this, routeNumbers -> {
            if (routeNumbers != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, routeNumbers);
                routeNumberAutoCompleteTextView.setAdapter(adapter);
                if (validRouteNumbers != null) {
                    validRouteNumbers = routeNumbers;
                }
            }
        });

        busViewModel.getRouteNamesLiveData().observe(this, routeNames -> {
            if (routeNames != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, routeNames);
                routeNameAutoCompleteTextView.setAdapter(adapter);
                validRouteNames = routeNames;
            }
        });

        busViewModel.getErrorMessageLiveData().observe(this, errorMessage -> {
            if (errorMessage != null) {
                TextView errorTextView = findViewById(R.id.errorMessageTextView);
                errorTextView.setText(errorMessage);
            }
        });

        busViewModel.fetchRouteNumbers();
        busViewModel.fetchRouteNames("");
    }

    private void initFocusChangeListenerForRouteNumber() {

        TextInputLayout routeNumberTextInputLayout = findViewById(R.id.routeNumberInputLayout);

        routeNumberAutoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
            Log("MainActivity", "OnFocusChangeListener - hasFocus: " + hasFocus);
            // If the AutoCompleteTextView is not focused
            if (!hasFocus) {
                String enteredText = routeNumberAutoCompleteTextView.getText().toString();
                // If the entered text is not a valid option, show an error
                if (!isValidEntry(enteredText, validRouteNumbers)) {
                    routeNumberTextInputLayout.setEndIconVisible(false);
                    routeNumberAutoCompleteTextView.setError("Invalid route number. Please select from the list.");
                } else {
                    routeNumberAutoCompleteTextView.setError(null);
                    busViewModel.fetchRouteNames(enteredText);
                }
            }
            // If the AutoCompleteTextView is focused, show the dropdown list
            else {
                if (validRouteNumbers.isEmpty()) {
                    busViewModel.fetchRouteNumbers();
                }
                routeNumberAutoCompleteTextView.showDropDown();
            }
        });

        routeNumberAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validRouteNumbers.isEmpty()) {
                    busViewModel.fetchRouteNumbers();
                }
                routeNumberAutoCompleteTextView.showDropDown();
            }
        });

    }

    private void initFocusChangeListenerForRouteName() {

        TextInputLayout routeNameTextInputLayout = findViewById(R.id.routeNameInputLayout);

        routeNameAutoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
            Log("MainActivity", "OnFocusChangeListener - hasFocus: " + hasFocus);
            // If the AutoCompleteTextView is not focused
            if (!hasFocus) {
                String enteredText = routeNameAutoCompleteTextView.getText().toString();
                // If the entered text is not a valid option, show an error
                if (!isValidEntry(enteredText, validRouteNames)) {
                    routeNameTextInputLayout.setEndIconVisible(false);
                    routeNameAutoCompleteTextView.setError("Invalid route name. Please select from the list.");
                } else {
                    routeNameAutoCompleteTextView.setError(null); // Clear any previous error
                }
            }
            // If the AutoCompleteTextView is focused, show the dropdown list
            else {
                String enteredRouteNumber = routeNumberAutoCompleteTextView.getText().toString();
                if (!enteredRouteNumber.isEmpty()) {
                    busViewModel.fetchRouteNames(enteredRouteNumber);
                }
                routeNameAutoCompleteTextView.showDropDown();
            }
        });

        routeNameAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredRouteNumber = routeNumberAutoCompleteTextView.getText().toString();
                if (!enteredRouteNumber.isEmpty()) {
                    busViewModel.fetchRouteNames(enteredRouteNumber);
                }
                routeNameAutoCompleteTextView.showDropDown();
            }
        });
    }


    private void initNewBusFormSubmitButton() {
        Button newBusFormSubmitButton = findViewById(R.id.NewBusFormSubmitButton);
        newBusFormSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log("Main", "initNewBusFormSubmitButton", "clicked");

                String busNickName = ((EditText) findViewById(R.id.editTextBusNickName)).getText().toString();
                String busNumber = ((EditText) findViewById(R.id.editTextRegistrationNumber)).getText().toString();
                String routeNumber = routeNumberAutoCompleteTextView.getText().toString();
                String routeName = routeNameAutoCompleteTextView.getText().toString();
                String emergencyContact = ((EditText) findViewById(R.id.editTextEmergencyContact)).getText().toString();
                String busColor = String.valueOf(currentColor);

                if (validateAddNewBusForm(busNickName, busNumber, routeNumber, routeName, emergencyContact, busColor)) {
                    BusModel newBus = new BusModel(busNickName, busNumber, routeNumber, routeName, emergencyContact, busColor, null);

                    busViewModel.getBusRegResponseLiveData().observe(MainActivity.this, new Observer<BusRegResponse>() {
                        @Override
                        public void onChanged(BusRegResponse response) {
                            if (response != null) {
                                if (response.isSuccess()) {
                                    Log("Main", "initNewBusFormSubmitButton", "new bus added");
                                    Log("Main", "initNewBusFormSubmitButton", "response" + response);
                                    initDashboard();
                                    // clear bus registration form fields
                                    ((EditText) findViewById(R.id.editTextBusNickName)).setText("");
                                    ((EditText) findViewById(R.id.editTextRegistrationNumber)).setText("");
                                    routeNumberAutoCompleteTextView.setText("");
                                    routeNameAutoCompleteTextView.setText("");
                                    ((EditText) findViewById(R.id.editTextEmergencyContact)).setText("");
                                    ((TextView) findViewById(R.id.colorEditText)).setText("");
                                    currentColor = 0;
                                    colorEditText.setBackgroundColor(currentColor);

                                } else {
                                    TextView errorTextView = findViewById(R.id.errorMessageTextView);
                                    errorTextView.setText(response.getMessage());
                                }
                            }
                        }
                    });

                    Log("Main", "initNewBusFormSubmitButton", "FleetRegistrationNumber: " + fleetStateManager.getFleet().getFleetRegistrationNumber());

                    BusRegistrationRequest registrationRequest = new BusRegistrationRequest(newBus);

                    busViewModel.addNewBus(registrationRequest);

                } else {
                    Log("Main", "initNewBusFormSubmitButton", "invalid form");
                }


            }
        });
    }


    // ------------------------------- UTILITY METHODS ------------------------------- //
    private void toggleItemVisibility(View view, boolean isVisible) {
        if (view != null) {
            view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            Log("toggleItemVisibility", view.toString(), String.valueOf(isVisible));
        }
    }

    private void allItemVisibilitySwitcher(Map<View, Boolean> visibilityMap) {
        for (Map.Entry<View, Boolean> entry : visibilityMap.entrySet()) {
            View view = entry.getKey();
            Boolean isVisible = entry.getValue();

            if (view != null && isVisible != null) {
                view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
                // Log("toggleItemsVisibility", view.toString(), String.valueOf(isVisible));
            }
        }
    }

    private void hideKeyboard(View view) {

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, currentColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                // Handle cancel action
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                currentColor = color;
                colorEditText.setBackgroundColor(color);
            }
        });

        colorPicker.show();
    }

    private boolean isValidEntry(String enteredText, List<String> validEntryList) {
        for (String route : validEntryList) {
            if (route.equalsIgnoreCase(enteredText)) {
                return true;
            }
        }
        return false;
    }

    private Boolean validateAddNewBusForm(String busNickName, String busNumber, String routeNumber, String routeName, String emergencyContact, String busColor) {

        Boolean isValid = true;
        ((TextView) findViewById(R.id.colorEditText)).setError(null);

        if (TextUtils.isEmpty(busNickName)) {
            ((EditText) findViewById(R.id.editTextBusNickName)).setError("Please enter a valid bus nickname");
            isValid = false;
        }
        if (TextUtils.isEmpty(busNumber)) {
            ((EditText) findViewById(R.id.editTextRegistrationNumber)).setError("Please enter a valid bus registration number");
            isValid = false;
        }
        if (TextUtils.isEmpty(routeNumber)) {
            routeNumberAutoCompleteTextView.setError("Please enter a valid route number");
            isValid = false;
        }
        if (TextUtils.isEmpty(routeName)) {
            routeNameAutoCompleteTextView.setError("Please enter a valid route name");
            isValid = false;
        }
        if (TextUtils.isEmpty(emergencyContact)) {
            ((EditText) findViewById(R.id.editTextEmergencyContact)).setError("Please enter a valid emergency contact");
            isValid = false;
        }

        if (!emergencyContact.matches("0[0-9]{9}")) {
            ((EditText) findViewById(R.id.editTextEmergencyContact)).setError("Please enter a valid emergency contact");
            isValid = false;
        }

        if (busColor.equals("0")) {
            ((TextView) findViewById(R.id.colorEditText)).setError("Please select a bus color");
            isValid = false;
        }
        if (!isValid) {
            TextView errorTextView = findViewById(R.id.errorMessageTextView);
            errorTextView.setText(getString(R.string.error_message_invalid_form));
        }
        return isValid;

    }
}


// TODO: 2023-12-07  MAKE FONT STYLE instead of using bold etc

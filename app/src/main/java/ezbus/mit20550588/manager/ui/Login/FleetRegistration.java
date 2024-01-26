package ezbus.mit20550588.manager.ui.Login;

import static ezbus.mit20550588.manager.util.Constants.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import ezbus.mit20550588.manager.R;
import ezbus.mit20550588.manager.data.model.FleetModel;
import ezbus.mit20550588.manager.data.network.responses.FleetCheckResponse;
import ezbus.mit20550588.manager.data.viewModel.BusViewModel;
import ezbus.mit20550588.manager.data.viewModel.FleetViewModel;
import ezbus.mit20550588.manager.ui.MainActivity;
import ezbus.mit20550588.manager.ui.Settings.ContactUs;
import ezbus.mit20550588.manager.ui.Settings.Settings;
import ezbus.mit20550588.manager.util.Constants;
import ezbus.mit20550588.manager.util.FleetStateManager;
import ezbus.mit20550588.manager.util.UserStateManager;

public class FleetRegistration extends AppCompatActivity {

    private FleetViewModel fleetViewModel;

    private BusViewModel busViewModel;

    private FleetModel newFleet;

    private FleetStateManager fleetManager;

    private UserStateManager userManager;

    @Override
    protected void onResume() {
        super.onResume();
        Log("FleetRegistration", "onResume", "Started");
        fleetViewModel.checkFleetStatus(userManager.getUser().getEmail());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fleet_registration);

        Log("FleetRegistration", "onCreate", "Started");

        initUi();
        initializeViewModel();
        initSettingsButton();
    }

    private void initUi() {
        // Show the loading progress bar
        findViewById(R.id.loadingProgressBar).setVisibility(View.VISIBLE);

        // Hide the registration layout
        findViewById(R.id.fleetRegistrationLayout).setVisibility(View.GONE);

        // Hide the pending layout
        findViewById(R.id.PendingLayout).setVisibility(View.GONE);

        // Hide the approved layout
        findViewById(R.id.ApprovedLayout).setVisibility(View.GONE);

        // Hide the suspended layout
        findViewById(R.id.SuspendedLayout).setVisibility(View.GONE);

        // Hide the RejectedLayout layout
        findViewById(R.id.RejectedLayout).setVisibility(View.GONE);

        fleetManager = FleetStateManager.getInstance();

        initRegistrationLayout();
    }

    private void initializeViewModel() {

        Log("initializeViewModel", "Started");

        fleetViewModel = new ViewModelProvider(this).get(FleetViewModel.class);

        // Observe check fleet status result
        fleetViewModel.getCheckFleetStatusLiveData().observe(this, FleetStatus -> {
            if (FleetStatus != null) {
                Log("initializeViewModel", "getCheckFleetStatusLiveData", "Received");
                handleFleetResponse(FleetStatus);
            }
        });

        // Observe authentication result
        fleetViewModel.getRegReqResponseLiveData().observe(this, FleetStatus -> {
            if (FleetStatus != null) {
                Log("initializeViewModel", "getRegReqResponseLiveData", "Received");
                handleFleetResponse(FleetStatus);
            }
        });

        // Observe the error message LiveData
        fleetViewModel.getErrorMessageLiveData().observe(this, errorMessage -> {
            if (errorMessage != null) {
                Log("initializeViewModel", "getErrorMessageLiveData", "Received");
                // Hide the loading progress bar
                findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);

                TextView errorTextView = findViewById(R.id.errorMessageTextView);
                errorTextView.setText(errorMessage);
            }
        });

        userManager = UserStateManager.getInstance();
        fleetViewModel.checkFleetStatus(userManager.getUser().getEmail());

        busViewModel = new ViewModelProvider(this).get(BusViewModel.class);
        busViewModel.loadBusAccountsLiveData().observe(this, busAccounts -> {
            if (busAccounts != null) {
                Log("initializeViewModel", "loadBusAccountsLiveData", "Received");
                if (busAccounts.size() > 0) {
                    fleetManager.setFleetLoggedIn(true);

                    // Go to Main Activity
                    Intent intent = new Intent(FleetRegistration.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void initSettingsButton() {
        ImageView settingsButton = findViewById(R.id.SettingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FleetRegistration.this, Settings.class);
                startActivity(intent);
            }
        });

        Constants.Log("initSettingsButton", "initialized");
    }

    private void initContinueButton() {
        findViewById(R.id.ContinueButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log("initContinueButton", "ContinueButton", "Clicked");

                fleetManager.setFleetLoggedIn(true);

                // Go to Main
                Intent intent = new Intent(FleetRegistration.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initRetryButton() {
        findViewById(R.id.RetryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log("initRetryButton", "RetryButton", "Clicked");
                findViewById(R.id.RejectedLayout).setVisibility(View.GONE);
                findViewById(R.id.fleetRegistrationLayout).setVisibility(View.VISIBLE);
            }
        });
    }

    private void initRegistrationLayout() {

        // Hide the loading progress bar
        findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);

        // Show the registration layout
        findViewById(R.id.fleetRegistrationLayout).setVisibility(View.VISIBLE);

        // Help Text
        TextView helpTextView = findViewById(R.id.helpText);

        // Create the SpannableString
        SpannableString spannableStringHelp = new SpannableString(getString(R.string.fleet_register_help));

        // Set a ClickableSpan for the "contact us" part
        ClickableSpan clickableSpanLogin = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),
                        ContactUs.class);
                startActivity(intent);
            }
        };

        // Set the ClickableSpan for the specific range
        spannableStringHelp.setSpan(clickableSpanLogin, 60, 70, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Terms of Service" part bold
        spannableStringHelp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 60, 70, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Terms of Service" part a different color
        spannableStringHelp.setSpan(new ForegroundColorSpan(getColor(R.color.my_primary)), 60, 70, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply the SpannableString to the TextView
        helpTextView.setText(spannableStringHelp);

        // Make the TextView clickable
        helpTextView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());

        initSubmitButton();

    }

    private void initSubmitButton() {
        // Listening to the Signup Button
        findViewById(R.id.SubmitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Show the loading progress bar
                findViewById(R.id.loadingProgressBar).setVisibility(View.VISIBLE);
                Submit();
            }
        });
    }

    private void Submit() {
        TextInputEditText fleetNameText = findViewById(R.id.editTextFleetName);
        TextInputEditText fleetRegNumberText = findViewById(R.id.editTextRegistrationNumber);

        String fleetName = fleetNameText.getText().toString();
        String fleetRegNumber = fleetRegNumberText.getText().toString();

        newFleet = new FleetModel(fleetName, fleetRegNumber, "Pending");

        // Trigger login operation in ViewModel
        fleetViewModel.registerFleet(newFleet, fleetNameText, fleetRegNumberText);

        Log("Submit", "Submit button clicked");
        Log("Submitted", "newFleet", newFleet.toString());

    }


    private void handleFleetResponse(FleetCheckResponse response) {
        if (response != null) {
            Log("handleAuthResult", "Registration request sent", response.getMessage());

            // Update the user newFleet status
            fleetManager.setFleet(response.getFleet());
            Log("handleAuthResult", "response.Fleet: ", response.getFleet().toString());
            Log("handleAuthResult", "fleetManager.Fleet: ", fleetManager.getFleet().toString());

            findViewById(R.id.fleetRegistrationLayout).setVisibility(View.GONE);

            /*
            code       New status           Check/Reg/Retry                                              response

            200 = Pending                 Newest registration                  --> Fleet (=user copy) will be sent to manager
            201 = Approved                Approved (check/Retry)               --> Fleet (server copy) will be sent to manager
            202 = Pending                 Pending (check/Retry)                --> Fleet (server copy/=user copy) will be sent to manager
            203 = Rejected                Rejected (check)                     --> Fleet (server copy) will be sent to manager
            204 = Suspended                Suspended (check/Retry)             --> Fleet (server copy) will be sent to manager

            400-500 = Errors --> No fleets
             */

            if (response.getResponseCode() == 201) {

                // if Approved we have to see if the fleet is already has bus accounts
                busViewModel.loadAllBusAccounts(userManager.getUser().getEmail());

                // Hide
                findViewById(R.id.fleetRegistrationLayout).setVisibility(View.GONE);
                findViewById(R.id.PendingLayout).setVisibility(View.GONE);
                findViewById(R.id.SuspendedLayout).setVisibility(View.GONE);
                findViewById(R.id.RejectedLayout).setVisibility(View.GONE);
                // Show the ApprovedLayout layout
                findViewById(R.id.ApprovedLayout).setVisibility(View.VISIBLE);
                initContinueButton();

            } else if (response.getResponseCode() == 200 || response.getResponseCode() == 202) {
                // Hide
                findViewById(R.id.fleetRegistrationLayout).setVisibility(View.GONE);
                findViewById(R.id.ApprovedLayout).setVisibility(View.GONE);
                findViewById(R.id.SuspendedLayout).setVisibility(View.GONE);
                findViewById(R.id.RejectedLayout).setVisibility(View.GONE);
                // Show the pending layout
                findViewById(R.id.PendingLayout).setVisibility(View.VISIBLE);
            } else if (response.getResponseCode() == 203) {
                // Hide
                findViewById(R.id.fleetRegistrationLayout).setVisibility(View.GONE);
                findViewById(R.id.ApprovedLayout).setVisibility(View.GONE);
                findViewById(R.id.SuspendedLayout).setVisibility(View.GONE);
                findViewById(R.id.PendingLayout).setVisibility(View.GONE);
                // Show the RejectedLayout layout
                findViewById(R.id.RejectedLayout).setVisibility(View.VISIBLE);
                initRetryButton();
            } else if (response.getResponseCode() == 204) {
                // Hide
                findViewById(R.id.fleetRegistrationLayout).setVisibility(View.GONE);
                findViewById(R.id.ApprovedLayout).setVisibility(View.GONE);
                findViewById(R.id.RejectedLayout).setVisibility(View.GONE);
                findViewById(R.id.PendingLayout).setVisibility(View.GONE);
                // Show the SuspendedLayout layout
                findViewById(R.id.SuspendedLayout).setVisibility(View.VISIBLE);
            } else {
                // Hide
                findViewById(R.id.ApprovedLayout).setVisibility(View.GONE);
                findViewById(R.id.RejectedLayout).setVisibility(View.GONE);
                findViewById(R.id.PendingLayout).setVisibility(View.GONE);
                findViewById(R.id.SuspendedLayout).setVisibility(View.GONE);
                // Show
                findViewById(R.id.fleetRegistrationLayout).setVisibility(View.VISIBLE);
            }
            // Hide the loading progress bar
            findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);
        }
    }


    private void showToast(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FleetRegistration.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

}
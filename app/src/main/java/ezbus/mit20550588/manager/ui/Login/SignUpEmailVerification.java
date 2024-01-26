package ezbus.mit20550588.manager.ui.Login;

import static ezbus.mit20550588.manager.util.Constants.AUTH_SUCCESS_DIALOG_DURATION;
import static ezbus.mit20550588.manager.util.Constants.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ezbus.mit20550588.manager.R;
import ezbus.mit20550588.manager.data.model.UserModel;
import ezbus.mit20550588.manager.data.network.responses.AuthResult;
import ezbus.mit20550588.manager.data.viewModel.AuthViewModel;
import ezbus.mit20550588.manager.ui.MainActivity;
import ezbus.mit20550588.manager.util.UserStateManager;

public class SignUpEmailVerification extends AppCompatActivity {

    // Define your EditText fields
    EditText editTextDigit1, editTextDigit2, editTextDigit3, editTextDigit4, editTextDigit5, editTextDigit6;

    // Array to keep track of the highlight state of each EditText
    private boolean[] isHighlighted = new boolean[6];

    // Variable to store the last known text in the EditText field
    private CharSequence lastText = "";

    private AuthViewModel authViewModel;

    private UserModel newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_email_verification);

        Intent intent = getIntent();
        String verificationCode = intent.getStringExtra("verificationCode");
        newUser = (UserModel) intent.getSerializableExtra("user");


        Log("SignUpEmailVerification", "newUser", newUser.toString());
        // EZBus manager app main text

        // Hide the loading progress bar
        findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);

        TextView textView0 = findViewById(R.id.main_app_name);
        String html = "<font color=#025a66>EZBus</font> <font color=#0A969F>Manager</font>";
        textView0.setText(Html.fromHtml(html));


        // Resend Text

        TextView textView = findViewById(R.id.ResendText);

        // Create the SpannableString
        SpannableString spannableString = new SpannableString("If you still haven't received the code, Resend code");

        // Set a ClickableSpan for the "Resend code" part
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                // Handle the click event for "Resend cpde"
                // Update the text to "Please wait until you receive a code"
                textView.setText("Please wait until you receive a code");
            }
        };

        // Set the ClickableSpan for the specific range
        spannableString.setSpan(clickableSpan, 40, 51, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Resend code" part bold
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 40, 51, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Make the "Resend code" part a different color
        spannableString.setSpan(new ForegroundColorSpan(getColor(R.color.my_primary)), 40, 51, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply the SpannableString to the TextView
        textView.setText(spannableString);

        // Make the TextView clickable
        textView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());


        // user entered verification code
        EditText editTextDigit1 = findViewById(R.id.editTextDigit1);
        EditText editTextDigit2 = findViewById(R.id.editTextDigit2);
        EditText editTextDigit3 = findViewById(R.id.editTextDigit3);
        EditText editTextDigit4 = findViewById(R.id.editTextDigit4);
        EditText editTextDigit5 = findViewById(R.id.editTextDigit5);
        EditText editTextDigit6 = findViewById(R.id.editTextDigit6);


        // verify button
        Button VerifyButton = (Button) this.findViewById(R.id.verifyButton);
        VerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(),
//                        AccountCreated.class);
//                startActivity(intent);
                TextView errorTextView = findViewById(R.id.errorMessageTextView);
                errorTextView.setText("");


                // Show the loading progress bar
                findViewById(R.id.loadingProgressBar).setVisibility(View.VISIBLE);

                Log("SignUpEmailVerification", "Verify button clicked");
                String digit1 = editTextDigit1.getText().toString();
                String digit2 = editTextDigit2.getText().toString();
                String digit3 = editTextDigit3.getText().toString();
                String digit4 = editTextDigit4.getText().toString();
                String digit5 = editTextDigit5.getText().toString();
                String digit6 = editTextDigit6.getText().toString();

                String userEnteredCode = digit1 + digit2 + digit3 + digit4 + digit5 + digit6;

                Log("SignUpEmailVerification", "verification code", verificationCode);
                Log("SignUpEmailVerification", "user entered code", userEnteredCode);

                if (userEnteredCode.equals(verificationCode)) {
                    Log("SignUpEmailVerification", "Correct verification code entered");
                    // email is verified
                    verifyUser();
                }
                else {

                    // Hide the loading progress bar
                    findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);

                    errorTextView.setText("The verification code was entered incorrectly. Check the code and try again.");
                }
            }
        });


        // Set the focus change listener for each EditText
        setFocusChangeListener(editTextDigit1, 0);
        setFocusChangeListener(editTextDigit2, 1);
        setFocusChangeListener(editTextDigit3, 2);
        setFocusChangeListener(editTextDigit4, 3);
        setFocusChangeListener(editTextDigit5, 4);
        setFocusChangeListener(editTextDigit6, 5);

        // Set the initial digit focused
        editTextDigit1.requestFocus();

        // Set a TextWatcher for each EditText
        addTextWatcher(editTextDigit1, 0);
        addTextWatcher(editTextDigit2, 1);
        addTextWatcher(editTextDigit3, 2);
        addTextWatcher(editTextDigit4, 3);
        addTextWatcher(editTextDigit5, 4);
        addTextWatcher(editTextDigit6, 5);

    }

    private void verifyUser() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Trigger login operation in ViewModel
        authViewModel.verifyUser(newUser);


        // Observe authentication result
        authViewModel.getAuthResultLiveData().observe(this, authResult -> {
            if (authResult != null) {

                // Hide the loading progress bar
                findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);
                handleAuthResult(authResult);
            }
        });

        // Observe the error message LiveData
        authViewModel.getErrorMessageLiveData().observe(this, errorMessage -> {
            if (errorMessage != null) {

                // Update your UI to display the error message (e.g., show a Toast or update a TextView)

                // Hide the loading progress bar
                findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);

                TextView errorTextView = findViewById(R.id.errorMessageTextView);
                errorTextView.setText(errorMessage);

            }
        });

    }

    private void handleAuthResult(AuthResult authResult) {
        Log("handleAuthResult", "Login successful");

        // Update the user login status
        UserStateManager userManager = UserStateManager.getInstance();

        userManager.setUserLoggedIn(true);
        userManager.setUser(authResult.getUser());

        Log("Signed up", "NEW USER", userManager.getUser().toString());

        showLoginSuccessDialog(authResult.getUser().getName());

    }

    private void showLoginSuccessDialog(String name) {
        Dialog dialog = new Dialog(SignUpEmailVerification.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setContentView(R.layout.rounded_alert_dialog);

        TextView welcomeText = dialog.findViewById(R.id.welcomeText);
        welcomeText.setText(getString(R.string.signup_results_welcome_text_1));
        TextView messageText = dialog.findViewById(R.id.messageTextView);
        messageText.setText(getString(R.string.signup_results_welcome_text_2));
        TextView userNameText = dialog.findViewById(R.id.userNameText);
        userNameText.setText(name);

        // Get the root view of the activity
        ViewGroup rootView = getWindow().getDecorView().findViewById(android.R.id.content);

        // Show a semi-transparent overlay
        View overlay = new View(SignUpEmailVerification.this);
        overlay.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        overlay.setBackgroundColor(Color.parseColor("#80000000")); // #80 for 50% alpha

        // Add the overlay to the root view
        rootView.addView(overlay);

        dialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                navigateToMainActivity();
            }
        }, AUTH_SUCCESS_DIALOG_DURATION); // 5000 milliseconds = 5 seconds


    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SignUpEmailVerification.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void setFocusChangeListener(final EditText editText, final int index) {
        // Set the focus change listener for the EditText
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // Check if the EditText has focus
                if (hasFocus) {

                    if (!isHighlighted[index]) {
                        // Set the background to the focused drawable
                        editText.setBackgroundResource(R.drawable.rounded_edit_digit_focused_bg);

                    }

                } else {

                    //if already completed
                    if (isHighlighted[index]) {
                    } else {

                        // otherwise Set the background to the normal
                        editText.setBackgroundResource(R.drawable.rounded_edittext_bg);
                    }
                }
            }
        });
    }


    private void addTextWatcher(final EditText editText, final int index) {
        editText.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                lastText = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Check if a number is entered
                if (count > 0) {
                    // Set the background to highlighted if not already highlighted
                    if (!isHighlighted[index]) {
                        editText.setBackgroundResource(R.drawable.highlighted_digitbox);
                        isHighlighted[index] = true;
                    }

                    // Move focus to the next EditText field
                    EditText nextEditText = findNextEditText(editText);
                    if (nextEditText != null) {
                        nextEditText.requestFocus();
                    }

                } else {
                    // Set the background to normal if not already normal
                    if (isHighlighted[index]) {
                        editText.setBackgroundResource(R.drawable.rounded_edittext_bg);
                        isHighlighted[index] = false;
                    }

                    // No number entered, delete the previous number
                    deletePreviousNumber(editText, lastText);


                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // Check if the backspace key is pressed
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // Handle backspace key press
                    // Get the current text of the EditText
                    String currentText = editText.getText().toString();
                    // Check if there is text to delete
                    if (!currentText.isEmpty()) {
                        // Delete the last character
                        editText.setText(currentText.substring(0, currentText.length() - 1));
                    }
                    if (currentText.isEmpty()) {
                        // Move focus to the previous EditText field
                        moveFocusToPrevious(index);
                    }


                    return true; // Indicate that the event has been handled
                }
                return false;
            }
        });
    }


    // Find the next EditText programmatically
    private EditText findNextEditText(EditText currentEditText) {
        switch (currentEditText.getId()) {
            case R.id.editTextDigit1:
                return findViewById(R.id.editTextDigit2);
            case R.id.editTextDigit2:
                return findViewById(R.id.editTextDigit3);
            case R.id.editTextDigit3:
                return findViewById(R.id.editTextDigit4);
            case R.id.editTextDigit4:
                return findViewById(R.id.editTextDigit5);
            case R.id.editTextDigit5:
                return findViewById(R.id.editTextDigit6);
            case R.id.editTextDigit6:
                // The last EditText, return null
                return null;
            default:
                return null;
        }
    }


    // Find the previous EditText programmatically
    private EditText findPreviousEditText(int currentIndex) {
        switch (currentIndex) {
            case 0:
                return null; // No previous EditText for the first one
            case 1:
                return findViewById(R.id.editTextDigit1);
            case 2:
                return findViewById(R.id.editTextDigit2);
            case 3:
                return findViewById(R.id.editTextDigit3);
            case 4:
                return findViewById(R.id.editTextDigit4);
            case 5:
                return findViewById(R.id.editTextDigit5);
            default:
                return null;
        }
    }

    // Move focus to the previous EditText programmatically
    private void moveFocusToPrevious(int currentIndex) {
        EditText previousEditText = findPreviousEditText(currentIndex);
        if (previousEditText != null) {
            previousEditText.requestFocus();
        }
    }

    // Delete the previous number in the EditText programmatically
    private void deletePreviousNumber(EditText editText, CharSequence beforeText) {
        if (beforeText.length() > 0) {
            // Only delete the previous number if there was one
            editText.setText(beforeText.subSequence(0, beforeText.length() - 1));
        }
    }


}
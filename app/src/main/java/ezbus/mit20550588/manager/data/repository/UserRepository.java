package ezbus.mit20550588.manager.data.repository;

import static ezbus.mit20550588.manager.util.Constants.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ezbus.mit20550588.manager.data.model.UserModel;
import ezbus.mit20550588.manager.data.network.ApiServiceAuthentication;
import ezbus.mit20550588.manager.data.network.requests.LoginRequest;
import ezbus.mit20550588.manager.data.network.requests.RegistrationRequest;
import ezbus.mit20550588.manager.data.network.responses.AuthResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private ApiServiceAuthentication apiService;
    private MutableLiveData<AuthResult> authResultLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

    private MutableLiveData<String> verificationCodeLiveData = new MutableLiveData<>();


    public UserRepository(ApiServiceAuthentication apiService) {
        this.apiService = apiService;
    }

    public LiveData<AuthResult> getAuthResultLiveData() {
        return authResultLiveData;
    }
    public MutableLiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public MutableLiveData<String> getVerificationCodeLiveData() {
        return verificationCodeLiveData;
    }

    public void loginUser(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);

        apiService.loginUser(loginRequest).enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful()) {
                    authResultLiveData.setValue(new AuthResult(AuthResult.Status.SUCCESS, response.body(), null));
                } else {

                    String errorMessage = "Login failed. ";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                   // authResultLiveData.setValue(new AuthResult(AuthResult.Status.ERROR, null, errorMessage));
                    errorMessageLiveData.setValue(errorMessage); // Set error message LiveData
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Log("User repository","onFailure", t.getMessage());
                String errorMessage = "Network failure.";
               // authResultLiveData.setValue(new AuthResult(AuthResult.Status.ERROR, null, errorMessage));
                errorMessageLiveData.setValue(errorMessage); // Set error message LiveData
            }

        });
    }

    public void registerUser(String name, String email, String password) {
        RegistrationRequest registrationRequest = new RegistrationRequest(name, email, password);

        apiService.registerUser(registrationRequest).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 201){
                        if (response.body() != null) {
                            try {
                                String verificationCode = response.body().get("verificationCode").toString();
                                verificationCodeLiveData.setValue(verificationCode);
                                Log("User repository", "verificationCode", verificationCode);
                                Log("User repository", "verificationCodeLiveData", verificationCodeLiveData.toString());
                            } catch (Exception e) {
                                errorMessageLiveData.setValue(e.getMessage());
                            }

                        }

                    }
                    if (response.code() == 208){
                        String errorMessage = "The email is already registered. Forgot your password? Reset it or sign in.";
                        errorMessageLiveData.setValue(errorMessage);
                    }
                } else {

                    String errorMessage = "Registration failed. ";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    errorMessageLiveData.setValue(errorMessage); // Set error message LiveData

                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Log("User repository","onFailure", t.getMessage());
                String errorMessage = "Network failure.";
                errorMessageLiveData.setValue(errorMessage); // Set error message LiveData
            }
        });
    }


    public void verifyUser(UserModel newUser) {


        Map<String, String> email = new HashMap<>();
        email.put("email", newUser.getEmail());

        apiService.verifyUser(email).enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200){

                        UserModel user = new UserModel(newUser.getName(), newUser.getEmail(),newUser.getHashedPassword(), false);
                       authResultLiveData.setValue(new AuthResult(AuthResult.Status.SUCCESS, user, null));
                    }

                } else {

                    String errorMessage = "Registration failed. ";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    errorMessageLiveData.setValue(errorMessage); // Set error message LiveData

                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                String errorMessage = "Network failure.";
                errorMessageLiveData.setValue(errorMessage); // Set error message LiveData
            }
        });
    }

}



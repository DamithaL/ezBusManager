package ezbus.mit20550588.manager.data.network;

import java.util.Map;

import ezbus.mit20550588.manager.data.model.UserModel;

import ezbus.mit20550588.manager.data.network.requests.LoginRequest;
import ezbus.mit20550588.manager.data.network.requests.RegistrationRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiServiceAuthentication {

    // Base_URL is in RetrofitClient class

    @POST("/auth/login/manager")
    Call<UserModel> loginUser(@Body LoginRequest loginRequest);


    @POST("/auth/signup/manager")
    Call<Map<String, String>> registerUser (@Body RegistrationRequest registrationRequest);

    @POST("/auth/verify/manager")
    Call<UserModel> verifyUser (@Body Map<String, String> email);

//
//    @POST("/auth/login")
//    Call<LoginResult> executeLogin(@Body HashMap<String, String> map);
//
//
//    @POST("/auth/signup")
//    Call<Void> executeSignup (@Body HashMap<String, String> map);
}



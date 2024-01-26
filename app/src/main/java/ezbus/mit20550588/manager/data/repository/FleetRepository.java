package ezbus.mit20550588.manager.data.repository;

import static ezbus.mit20550588.manager.util.Constants.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.Map;

import ezbus.mit20550588.manager.data.model.FleetModel;
import ezbus.mit20550588.manager.data.network.ApiBus;
import ezbus.mit20550588.manager.data.network.responses.FleetCheckResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FleetRepository {
    private ApiBus apiService;
    private MutableLiveData<FleetCheckResponse> checkStatusLiveData = new MutableLiveData<>();

    private MutableLiveData<FleetCheckResponse> regReqResponseLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();


    public FleetRepository(ApiBus apiService) {
        this.apiService = apiService;
    }

    public LiveData<FleetCheckResponse> getCheckFleetStatusLiveData() {
        return checkStatusLiveData;
    }

    public LiveData<FleetCheckResponse> getRegReqResponseLiveData() {
        return regReqResponseLiveData;
    }

    public MutableLiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }


    // TODO: 2024-01-27 More security should be added to this method with the use of a token
    public void checkFleetStatus(String email) {

        Map<String, String> managerEmail = new HashMap<>();
        managerEmail.put("email", email);
        apiService.checkFleetStatus(managerEmail).enqueue(new Callback<FleetCheckResponse>() {
            @Override
            public void onResponse(Call<FleetCheckResponse> call, Response<FleetCheckResponse> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        Log("Fleet repository", "onResponse", "response.body(): " + response.body().toString());
                        Log("Fleet repository", "onResponse", "response.body().getFleet(): " + response.body().getFleet());
                        FleetCheckResponse fleetResponse = new FleetCheckResponse(response.body().getFleet(), response.body().getMessage(), response.code());
                        checkStatusLiveData.setValue(fleetResponse);
                        Log("Fleet repository", "onResponse", "checkStatusLiveData"+  checkStatusLiveData.toString());
                    }
                } catch (Exception e) {
                    errorMessageLiveData.setValue(e.getMessage());
                    Log("Fleet repository", "onResponse", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<FleetCheckResponse> call, Throwable t) {
                Log("Fleet repository", "onFailure", t.getMessage());
                String errorMessage = "Network failure.";
                errorMessageLiveData.setValue(errorMessage); // Set error message LiveData
            }
        });
    }

    public void registerFleet(FleetModel newFleet) {

        apiService.registerFleet(newFleet).enqueue(new Callback<FleetCheckResponse>() {
            @Override
            public void onResponse(Call<FleetCheckResponse> call, Response<FleetCheckResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        // if Bus fleet registration request received
                        if (response.body() != null) {
                            FleetCheckResponse fleetResponse = new FleetCheckResponse(response.body().getFleet(), response.body().getMessage(), response.code());
                            regReqResponseLiveData.setValue(fleetResponse);
                        }
                    } else {
                        String errorMessage = "Registration failed. ";
                        if (response.errorBody() != null) {
                            try {
                                errorMessage += response.errorBody().string();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        errorMessageLiveData.setValue(errorMessage);
                    }
                } catch (Exception e) {
                    errorMessageLiveData.setValue(e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<FleetCheckResponse> call, Throwable t) {

                Log("Fleet repository", "onFailure", t.getMessage());
                String errorMessage = "Network failure.";
                errorMessageLiveData.setValue(errorMessage); // Set error message LiveData
            }
        });
    }


}



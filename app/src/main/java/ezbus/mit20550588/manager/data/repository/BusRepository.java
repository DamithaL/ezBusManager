package ezbus.mit20550588.manager.data.repository;

import static ezbus.mit20550588.manager.util.Constants.Log;
import static kotlinx.coroutines.CoroutineScopeKt.CoroutineScope;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ezbus.mit20550588.manager.data.dao.BusAccountDao;
import ezbus.mit20550588.manager.data.database.AppDatabase;
import ezbus.mit20550588.manager.data.model.BusModel;
import ezbus.mit20550588.manager.data.network.ApiBus;
import ezbus.mit20550588.manager.data.network.requests.BusRegistrationRequest;
import ezbus.mit20550588.manager.data.network.responses.BusRegResponse;
import ezbus.mit20550588.manager.data.network.responses.FleetCheckResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusRepository {

    private ApiBus apiService;
    private Application application;
    private BusAccountDao busAccountDao;
    private MutableLiveData<List<String>> routeNumbersLiveData = new MutableLiveData<>();
    private MutableLiveData<List<String>> routeNamesLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

    private MutableLiveData<BusRegResponse> busRegResponseLiveData = new MutableLiveData<>();

    private MutableLiveData<List<BusModel>> allBusAccountsLiveData = new MutableLiveData<>();

    private LiveData<Integer> allBusCountLiveData = new MutableLiveData<>();

    private MutableLiveData<Boolean> networkStatusLiveData;




    public BusRepository(Application application, ApiBus apiService) {
        AppDatabase database = AppDatabase.getInstance(application);
        busAccountDao = database.busAccountDao();
//        allBusAccountsLiveData.setValue(busAccountDao.getAllBusAccountsLiveData());
        allBusCountLiveData = busAccountDao.getBusCount();

        // Initialize LiveData on the main thread with an empty list
        allBusAccountsLiveData.setValue(new ArrayList<>());

        // Start background coroutine to fetch data from the database
        CoroutineScope(Dispatchers.IO).launch {
            allBusAccountsLiveData.postValue(busAccountDao.getAllBusAccountsLiveData());
            allBusCountLiveData.postValue(busAccountDao.getBusCount());
        };

        this.apiService = apiService;
    }

    public LiveData<List<String>> getRouteNamesLiveData() {
        return routeNamesLiveData;
    }

    public LiveData<List<String>> getRouteNumbersLiveData() {
        return routeNumbersLiveData;
    }

    public MutableLiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public MutableLiveData<BusRegResponse> getBusRegResponseLiveData() {
        return busRegResponseLiveData;
    }

    public LiveData<Integer> allBusCountLiveData() {
        return allBusCountLiveData;
    }

    public LiveData<List<BusModel>> loadAllBusAccountsLiveData(){
        return allBusAccountsLiveData;
    }

    public void loadAllBusAccounts(String email){
        if (isOnline()) {
            fetchAllBusAccounts(email);
        } else {
            errorMessageLiveData.setValue("No network connection.");
        }

        busAccountDao.getAllBusAccountsLiveData();
    }

    public void getAllBusCount(){
        busAccountDao.getBusCount();
    }

    public LiveData<List<String>> fetchRouteNames(String enteredRouteNumber) {
        MutableLiveData<List<String>> routesLiveData = new MutableLiveData<>();

        Map<String, String> routeNumber = new HashMap<>();
        routeNumber.put("routeNumber", enteredRouteNumber);

        apiService.getRouteNames(routeNumber).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    routeNamesLiveData.setValue(response.body());
                    errorMessageLiveData.setValue("");
                    Log("Bus repository", "fetchRouteNames", response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                String errorMessage = "Network failure. " + t.getMessage();
                errorMessageLiveData.setValue(errorMessage);
                Log("Bus repository", "fetchRouteNames: ERROR", t.getMessage());
            }
        });

        return routesLiveData;
    }

    public LiveData<List<String>> fetchRouteNumbers() {
        MutableLiveData<List<String>> routesLiveData = new MutableLiveData<>();

        apiService.getRouteNumbers().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    routeNumbersLiveData.setValue(response.body());
                    errorMessageLiveData.setValue("");
                    Log("Bus repository", "fetchRouteNumbers", response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                String errorMessage = "Network failure. " + t.getMessage();
                errorMessageLiveData.setValue(errorMessage);
                Log("Bus repository", "fetchRouteNumbers: ERROR", t.getMessage());
            }
        });

        return routesLiveData;
    }

    // TODO: 2024-01-27 More security should be added to this method with the use of a token
    public void fetchAllBusAccounts(String email) {

        Map<String, String> managerEmail = new HashMap<>();
        managerEmail.put("email", email);
        apiService.fetchAllBusAccounts(managerEmail).enqueue(new Callback<List<BusModel>>() {
            @Override
            public void onResponse(Call<List<BusModel>> call, Response<List<BusModel>> response) {
                if (response.isSuccessful()) {
                    // Update local database with the received data
                    updateLocalDatabase(response.body());
                    allBusAccountsLiveData.setValue(response.body());

                    errorMessageLiveData.setValue("");
                    Log("Bus repository", "fetchAllBusAccounts", response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<List<BusModel>> call, Throwable t) {
                String errorMessage = "Network failure. " + t.getMessage();
                errorMessageLiveData.setValue(errorMessage);
                Log("Bus repository", "fetchAllBusAccounts: ERROR", t.getMessage());
            }
        });

    }

    // asycn task to update the local database
    private void updateLocalDatabase(List<BusModel> busList) {
        UpdateAllBusAccountsAsyncTask updateTask = new UpdateAllBusAccountsAsyncTask(busAccountDao);
        updateTask.performBackgroundTask(busList);
    }

    // insert new bus to local database
    private void saveNewBusToLocalDatabase(BusModel bus) {
        InsertNewBusAsyncTask insertTask = new InsertNewBusAsyncTask(busAccountDao);
        insertTask.performBackgroundTask(bus);
    }



    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

            // Check if there is an active network connection
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                // Check if the device is actually online by using the LiveData value
                return networkStatusLiveData.getValue() != null && networkStatusLiveData.getValue();
            }
        }

        return false; // No active network connection
    }


//    public void addNewBus(BusModel newBus) {
//        InsertAsyncTask insertTask = new InsertAsyncTask(busAccountDao);
//        insertTask.performBackgroundTask(newBus);
//    }

    public void addNewBus(BusRegistrationRequest registrationRequest) {

        apiService.registerBus(registrationRequest).enqueue(new Callback<BusRegResponse>()  {
            @Override
            public void onResponse(Call<BusRegResponse> call, Response<BusRegResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            saveNewBusToLocalDatabase(registrationRequest.getBus());
                            busRegResponseLiveData.setValue(response.body());
                            errorMessageLiveData.setValue("");
                            Log("Bus repository", "addNewBus", response.body().getMessage());
                        }
                    } else {
                        String errorMessage = "Bus registration failed. ";
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
            public void onFailure(Call<BusRegResponse> call, Throwable t) {
                Log("User repository", "addNewBus", "onFailure"+ t.getMessage());
                String errorMessage = "Network failure.";
                errorMessageLiveData.setValue(errorMessage); // Set error message LiveData
            }
        });
    }

    private static class UpdateAllBusAccountsAsyncTask {

        private Executor executor = Executors.newSingleThreadExecutor();
        private BusAccountDao busAccountDao;

        public UpdateAllBusAccountsAsyncTask(BusAccountDao busAccountDao) {
            this.busAccountDao = busAccountDao;
        }

        public void performBackgroundTask(List<BusModel> busAccounts) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (busAccounts != null && !busAccounts.isEmpty()) {
                        busAccountDao.deleteAllBusAccounts();
                        busAccountDao.insertBusAccounts(busAccounts);
                    }
                }
            });
        }
    }

    // insert new bus async task
    private static class InsertNewBusAsyncTask {

        private Executor executor = Executors.newSingleThreadExecutor();
        private BusAccountDao busAccountDao;

        public InsertNewBusAsyncTask(BusAccountDao busAccountDao) {
            this.busAccountDao = busAccountDao;
        }

        public void performBackgroundTask(BusModel... bus) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (bus.length > 0) {
                        busAccountDao.insertNewBus(bus[0]);
                    }
                }
            });
        }
    }
}

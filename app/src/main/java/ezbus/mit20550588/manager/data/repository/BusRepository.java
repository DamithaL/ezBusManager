package ezbus.mit20550588.manager.data.repository;

import static ezbus.mit20550588.manager.util.Constants.Log;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ezbus.mit20550588.manager.data.dao.BusAccountDao;
import ezbus.mit20550588.manager.data.database.AppDatabase;
import ezbus.mit20550588.manager.data.model.BusModel;
import ezbus.mit20550588.manager.data.network.ApiBus;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusRepository {

    private ApiBus apiService;

    private BusAccountDao busAccountDao;
    private MutableLiveData<List<String>> routeNumbersLiveData = new MutableLiveData<>();
    private MutableLiveData<List<String>> routeNamesLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

    private LiveData<List<BusModel>> allBusAccounts;

    public BusRepository(Application application, ApiBus apiService) {
        AppDatabase database = AppDatabase.getInstance(application);
        busAccountDao = database.busAccountDao();
        allBusAccounts = busAccountDao.getAllBusAccounts();
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

    public LiveData<Integer> getBusCount() {
        return busAccountDao.getBusCount();
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
                    errorMessageLiveData.setValue(null);
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

    public void addNewBus(BusModel newBus) {
        InsertAsyncTask insertTask = new InsertAsyncTask(busAccountDao);
        insertTask.performBackgroundTask(newBus);
    }

    public LiveData<List<BusModel>> getAllBusAccounts() {
        return allBusAccounts;
    }

    private static class InsertAsyncTask {

        private Executor executor = Executors.newSingleThreadExecutor();
        private BusAccountDao busAccountDao;

        public InsertAsyncTask(BusAccountDao busAccountDao) {
            this.busAccountDao = busAccountDao;
        }

        public void performBackgroundTask(BusModel... newBus) {
            executor.execute(() -> {
                if (newBus.length > 0) {
                    BusModel bus = newBus[0];
                    busAccountDao.insertNewBus(bus);
                    Log("Bus repository", "InsertAsyncTask", "Bus saved");
                }
            });
        }
    }
}

package ezbus.mit20550588.manager.data.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import ezbus.mit20550588.manager.data.model.BusModel;
import ezbus.mit20550588.manager.data.network.ApiBus;
import ezbus.mit20550588.manager.data.network.requests.BusRegistrationRequest;
import ezbus.mit20550588.manager.data.network.RetrofitClient;
import ezbus.mit20550588.manager.data.network.responses.BusRegResponse;
import ezbus.mit20550588.manager.data.repository.BusRepository;

public class BusViewModel extends AndroidViewModel {
    private final BusRepository busRepository;
    private final LiveData<List<String>> routesNumbersLiveData;

    private final LiveData<List<String>> routesNamesLiveData;
    private MutableLiveData<String> errorMessageLiveData;

    private MutableLiveData<BusRegResponse> busRegResponseLiveData = new MutableLiveData<>();
    private LiveData<Integer> busCountLiveData;
    private LiveData<List<BusModel>> allBusAccountsLiveData;



    public BusViewModel(@NonNull Application application) {
        super(application);
        this.busRepository = new BusRepository(application, new RetrofitClient().getClient().create(ApiBus.class));
        this.routesNumbersLiveData = busRepository.getRouteNumbersLiveData();
        this.routesNamesLiveData = busRepository.getRouteNamesLiveData();
        this.errorMessageLiveData = busRepository.getErrorMessageLiveData();
        this.busCountLiveData = busRepository.allBusCountLiveData();
        this.allBusAccountsLiveData = busRepository.loadAllBusAccountsLiveData();
        this.busRegResponseLiveData = busRepository.getBusRegResponseLiveData();
    }

//    public BusViewModel(BusRepository busRepository) {
//        this.busRepository = busRepository;
//        this.routesNumbersLiveData = busRepository.getRouteNumbersLiveData();
//        this.routesNamesLiveData = busRepository.getRouteNamesLiveData();
//        this.errorMessageLiveData = busRepository.getErrorMessageLiveData();
//    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public LiveData<List<String>> getRouteNamesLiveData() {
        return routesNamesLiveData;
    }

    public LiveData<List<String>> getRouteNumbersLiveData() {
        return routesNumbersLiveData;
    }

    public LiveData<BusRegResponse> getBusRegResponseLiveData() {
        return busRegResponseLiveData;
    }

    public  LiveData<Integer> busCountLiveData(){
        return busCountLiveData;
    }

    public LiveData<List<BusModel>> loadBusAccountsLiveData() {
        return allBusAccountsLiveData;
    }

    public void fetchRouteNumbers() {
        busRepository.fetchRouteNumbers();
    }

    public void fetchRouteNames(String enteredRouteNumber) {
        busRepository.fetchRouteNames(enteredRouteNumber);
    }

//    public void addNewBus(BusModel newBus) {
//        busRepository.addNewBus(newBus);
//    }

    public void addNewBus(BusRegistrationRequest registrationRequest) {
        busRepository.addNewBus(registrationRequest);
    }
    public void loadAllBusAccounts(String email) {
        busRepository.loadAllBusAccounts(email);
    }

    public void getBusAccountsCount() {
        busRepository.getAllBusCount();
    }




}

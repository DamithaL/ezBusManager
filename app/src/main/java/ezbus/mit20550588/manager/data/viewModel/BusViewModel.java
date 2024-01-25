package ezbus.mit20550588.manager.data.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.Closeable;
import java.util.List;

import ezbus.mit20550588.manager.data.model.BusModel;
import ezbus.mit20550588.manager.data.network.ApiBus;
import ezbus.mit20550588.manager.data.network.RetrofitClient;
import ezbus.mit20550588.manager.data.repository.BusRepository;
import ezbus.mit20550588.manager.data.repository.FleetRepository;

public class BusViewModel extends AndroidViewModel {
    private final BusRepository busRepository;
    private final LiveData<List<String>> routesNumbersLiveData;

    private final LiveData<List<String>> routesNamesLiveData;
    private MutableLiveData<String> errorMessageLiveData;

    private LiveData<List<BusModel>> allBusAccounts;

    public BusViewModel(@NonNull Application application) {
        super(application);
        this.busRepository = new BusRepository(application, new RetrofitClient().getClient().create(ApiBus.class));
        this.routesNumbersLiveData = busRepository.getRouteNumbersLiveData();
        this.routesNamesLiveData = busRepository.getRouteNamesLiveData();
        this.errorMessageLiveData = busRepository.getErrorMessageLiveData();
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

    public  LiveData<Integer> getBusCount(){
        return busRepository.getBusCount();
    }

    public void fetchRouteNumbers() {
        busRepository.fetchRouteNumbers();
    }

    public void fetchRouteNames(String enteredRouteNumber) {
        busRepository.fetchRouteNames(enteredRouteNumber);
    }

    public void addNewBus(BusModel newBus) {
        busRepository.addNewBus(newBus);
    }

    public LiveData<List<BusModel>> getBusAccounts() {
        return allBusAccounts;
    }

}

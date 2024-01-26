package ezbus.mit20550588.manager.data.network;

import java.util.List;
import java.util.Map;

import ezbus.mit20550588.manager.data.model.BusModel;
import ezbus.mit20550588.manager.data.model.FleetModel;
import ezbus.mit20550588.manager.data.network.requests.BusRegistrationRequest;
import ezbus.mit20550588.manager.data.network.responses.BusRegResponse;
import ezbus.mit20550588.manager.data.network.responses.FleetCheckResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiBus {

    // Base_URL is in RetrofitClient class

    @POST("/bus/register/busfleet")
    Call<FleetCheckResponse> registerFleet(@Body FleetModel registrationRequest);

    @POST("/bus/register/busaccount")
    Call<BusRegResponse> registerBus(@Body BusRegistrationRequest registrationRequest);

    @POST("/bus/check/busfleet-status")
    Call<FleetCheckResponse> checkFleetStatus(@Body Map<String, String>  email);

    @GET("/bus/fetch-all-buses")
    Call<List<BusModel>> fetchAllBusAccounts(@Body Map<String, String>  email);

    @GET("/bus/fetch-route-numbers")
    Call<List<String>> getRouteNumbers();

    @POST("/bus/fetch-route-names")
    Call<List<String>> getRouteNames(@Body Map<String, String>  enteredRouteNumber);

}



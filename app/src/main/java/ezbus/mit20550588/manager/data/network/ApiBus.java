package ezbus.mit20550588.manager.data.network;

import java.util.List;
import java.util.Map;

import ezbus.mit20550588.manager.data.model.FleetModel;
import ezbus.mit20550588.manager.data.model.UserModel;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiBus {

    // Base_URL is in RetrofitClient class

    @POST("/bus/register/busfleet")
    Call<Map<String, String>> registerFleet(@Body FleetModel registrationRequest);

    @POST("/bus/check/busfleet-status")
    Call<Map<String, String>> checkFleetStatus(@Body FleetModel registrationRequest);

    @GET("/bus/fetch-route-numbers")
    Call<List<String>> getRouteNumbers();

    @POST("/bus/fetch-route-names")
    Call<List<String>> getRouteNames(@Body Map<String, String>  enteredRouteNumber);

}



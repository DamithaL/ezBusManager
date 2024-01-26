package ezbus.mit20550588.manager.data.network.responses;

import ezbus.mit20550588.manager.data.model.FleetModel;

public class FleetCheckResponse {

    private final FleetModel busFleet;
    private final String message;
    private final int responseCode;

    public FleetCheckResponse(FleetModel fleet, String message, int responseCode) {
        this.busFleet = fleet;
        this.message = message;
        this.responseCode = responseCode;
    }

    public FleetModel getFleet() {
        return busFleet;
    }

    public String getMessage() {
        return message;
    }

    public int getResponseCode() {
        return responseCode;
    }
}


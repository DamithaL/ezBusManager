package ezbus.mit20550588.manager.data.network.responses;

import ezbus.mit20550588.manager.data.model.FleetModel;

public class BusRegResponse {
    private final String message;
    private final int responseCode;

    private final boolean isSuccess;

    public BusRegResponse(String message, int responseCode, boolean isSuccess) {
        this.message = message;
        this.responseCode = responseCode;
        this.isSuccess = isSuccess;
    }

    public String getMessage() {
        return message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}


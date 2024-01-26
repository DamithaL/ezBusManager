package ezbus.mit20550588.manager.data.network.requests;

import ezbus.mit20550588.manager.data.model.BusModel;
import ezbus.mit20550588.manager.util.FleetStateManager;
import ezbus.mit20550588.manager.util.UserStateManager;

public class BusRegistrationRequest {
    private final String  fleetRegistrationNumber;
    private final String  managerEmail;
    private BusModel bus;

    public BusRegistrationRequest(BusModel bus) {
        this.fleetRegistrationNumber = FleetStateManager.getInstance().getFleet().getFleetRegistrationNumber();
        this.managerEmail = UserStateManager.getInstance().getUser().getEmail();
        this.bus = bus;
    }

    public String getFleetRegistrationNumber() {
        return fleetRegistrationNumber;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public BusModel getBus() {
        return bus;
    }

    public void setBus(BusModel bus) {
        this.bus = bus;
    }
}

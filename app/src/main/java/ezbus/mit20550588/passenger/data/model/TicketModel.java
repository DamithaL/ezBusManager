package ezbus.mit20550588.passenger.data.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class TicketModel implements Serializable {
    private String routeName;
    private String routeNumber;
    private String arrivalStopName;
    private String arrivalStopTime;
    private String departureStopName;
    private String departureStopTime;
    private Double farePrice;

    public TicketModel(String routeName, String routeNumber, String arrivalStopName, String arrivalStopTime, String departureStopName, String departureStopTime) {
        this.routeName = routeName;
        this.routeNumber = routeNumber;
        this.arrivalStopName = arrivalStopName;
        this.arrivalStopTime = arrivalStopTime;
        this.departureStopName = departureStopName;
        this.departureStopTime = departureStopTime;
      //  this.farePrice = farePrice;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(String routeNumber) {
        this.routeNumber = routeNumber;
    }

    public String getArrivalStopName() {
        return arrivalStopName;
    }

    public void setArrivalStopName(String arrivalStopName) {
        this.arrivalStopName = arrivalStopName;
    }

    public String getArrivalStopTime() {
        return arrivalStopTime;
    }

    public void setArrivalStopTime(String arrivalStopTime) {
        this.arrivalStopTime = arrivalStopTime;
    }

    public String getDepartureStopName() {
        return departureStopName;
    }

    public void setDepartureStopName(String departureStopName) {
        this.departureStopName = departureStopName;
    }

    public String getDepartureStopTime() {
        return departureStopTime;
    }

    public void setDepartureStopTime(String departureStopTime) {
        this.departureStopTime = departureStopTime;
    }

    public Double getFarePrice() {
        return farePrice;
    }

    public void setFarePrice(Double farePrice) {
        this.farePrice = farePrice;
    }

    @NonNull
    @Override
    public String toString() {
        return "TicketModel{" +
                "routeName='" + routeName + '\'' +
                ", routeNumber='" + routeNumber + '\'' +
                ", arrivalStopName='" + arrivalStopName + '\'' +
                ", arrivalStopTime='" + arrivalStopTime + '\'' +
                ", departureStopName='" + departureStopName + '\'' +
                ", departureStopTime='" + departureStopTime + '\'' +
                '}';
    }
}

package ezbus.mit20550588.manager.data.model;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "bus_account_table")
public class BusModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int busId;
    private String busNickName;
    private String busRegNumber;
    private String busRouteNumber;
    private String busRouteName;
    private String busEmergencyNumber;
    private String busColor;

    @Nullable
    @ColumnInfo(name = "busConductorEmail")
    private String busConductorEmail;

    public BusModel(String busNickName, String busRegNumber, String busRouteNumber, String busRouteName, String busEmergencyNumber, String busColor, String busConductorEmail) {
        this.busNickName = busNickName;
        this.busRegNumber = busRegNumber;
        this.busRouteNumber = busRouteNumber;
        this.busRouteName = busRouteName;
        this.busEmergencyNumber = busEmergencyNumber;
        this.busColor = busColor;
        this.busConductorEmail = busConductorEmail;
    }

    public int getBusId() {
        return busId;
    }

    public String getBusNickName() {
        return busNickName;
    }

    public String getBusRegNumber() {
        return busRegNumber;
    }

    public String getBusRouteNumber() {
        return busRouteNumber;
    }

    public String getBusRouteName() {
        return busRouteName;
    }

    public String getBusEmergencyNumber() {
        return busEmergencyNumber;
    }

    public String getBusColor() {
        return busColor;
    }

    public String getBusConductorEmail() {
        return busConductorEmail;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public void setBusConductorEmail(String busConductorEmail) {
        this.busConductorEmail = busConductorEmail;
    }
}

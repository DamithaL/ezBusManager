package ezbus.mit20550588.manager.data.dao;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ezbus.mit20550588.manager.data.model.BusModel;


@Dao
public interface BusAccountDao {

    @Insert
    void insertNewBus(BusModel busAccountModel);

    @Query("SELECT * FROM bus_account_table ORDER BY busNickName ASC")
    List<BusModel>  getAllBusAccountsLiveData();

    @Update
    void updateBusAccount(BusModel busAccount);

    @Update
    void updateAllBusAccounts(List<BusModel> busAccounts);

    @Delete
    void deleteBusAccount(BusModel busAccount);


    @Query("SELECT * FROM bus_account_table WHERE busId = :busId LIMIT 1")
    BusModel getBusByBusId(String busId);


    @Query("SELECT COUNT(*) FROM bus_account_table")
    LiveData<Integer> getBusCount();

    @Query("DELETE FROM bus_account_table")
    void deleteAllBusAccounts();

    @Insert
    void insertBusAccounts(List<BusModel> busAccounts);
}

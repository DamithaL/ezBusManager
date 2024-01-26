package ezbus.mit20550588.manager.data.database;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ezbus.mit20550588.manager.data.dao.BusAccountDao;

public class ClearDatabaseAsyncTask {


    private Executor executor = Executors.newSingleThreadExecutor();

    private BusAccountDao busAccountDao;

    public ClearDatabaseAsyncTask(AppDatabase database) {
        this.busAccountDao = database.busAccountDao();
    }

    public void performBackgroundTask() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                busAccountDao.deleteAllBusAccounts();
            }
        });
    }
}

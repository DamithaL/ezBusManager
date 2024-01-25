// Location: ezbus.mit20550588.manager.data.database

package ezbus.mit20550588.manager.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ezbus.mit20550588.manager.data.dao.BusAccountDao;
import ezbus.mit20550588.manager.data.model.BusModel;
import ezbus.mit20550588.manager.util.Converters;

@Database(entities = {BusModel.class}, version = 1, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract BusAccountDao busAccountDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "app_database"
                    ).fallbackToDestructiveMigration()
                    .addCallback(roomCallback).build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // add initialization code here if needed
        }
    };





}

package pt.ipleiria.estg.dei.coworkipleiria_02;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;

import pt.ipleiria.estg.dei.coworkipleiria_02.model.Sala;

@Database(entities = {User.class, Reserva.class, Sala.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract ReservaDao reservaDao();
    public abstract pt.ipleiria.estg.dei.coworkipleiria_02.SalaDao salaDao();


    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "cowork_database")
                            .addMigrations(MIGRATION_3_4)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Cria a tabela salas
            database.execSQL("CREATE TABLE IF NOT EXISTS salas (" +
                    "id TEXT PRIMARY KEY NOT NULL, " +
                    "nome TEXT NOT NULL, " +
                    "capacidade INTEGER NOT NULL, " +
                    "tipo TEXT NOT NULL, " +
                    "disponivel INTEGER NOT NULL, " +
                    "preco_por_hora REAL NOT NULL, " +
                    "is_active INTEGER NOT NULL DEFAULT 1" +
                    ")");
        }
    };
}
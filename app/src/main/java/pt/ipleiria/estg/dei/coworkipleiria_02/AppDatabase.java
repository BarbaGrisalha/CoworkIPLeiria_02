package pt.ipleiria.estg.dei.coworkipleiria_02;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;

import pt.ipleiria.estg.dei.coworkipleiria_02.model.Sala;

@Database(entities = {User.class, Reserva.class, Sala.class}, version = 3, exportSchema = false)
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
                            .addMigrations(MIGRATION_2_3)   // ← adiciona esta linha
                            .allowMainThreadQueries()       // Temporário para testes
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    // Coloca esta migration como campo estático (fora do método, dentro da classe)
// ... dentro da classe AppDatabase
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Cria a tabela salas (se não existir)
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
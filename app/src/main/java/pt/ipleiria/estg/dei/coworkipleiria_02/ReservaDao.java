package pt.ipleiria.estg.dei.coworkipleiria_02;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReservaDao {

    @Insert
    void insert(Reserva reserva);

    @Query("SELECT * FROM reservas")
    List<Reserva> getAll();

    @Query("SELECT * FROM reservas WHERE userId = :userId")
    List<Reserva> getByUser(int userId);
}
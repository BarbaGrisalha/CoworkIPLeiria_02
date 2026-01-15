package pt.ipleiria.estg.dei.coworkipleiria_02;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import java.util.List;

@Dao
public interface ReservaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Reserva reserva);  // retorna o ID inserido

    @Query("SELECT * FROM reservas WHERE userId = :userId ORDER BY dataReserva DESC")
    List<Reserva> getByUser(int userId);


    @Delete
    void delete(Reserva reserva);

    @Update
    void update(Reserva reserva);

}
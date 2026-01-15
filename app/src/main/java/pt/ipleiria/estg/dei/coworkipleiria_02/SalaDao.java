package pt.ipleiria.estg.dei.coworkipleiria_02;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import pt.ipleiria.estg.dei.coworkipleiria_02.model.Sala;

import java.util.List;

@Dao
public interface SalaDao {

    @Insert
    void insert(Sala sala);

    @Update
    void update(Sala sala);


    @Query("SELECT * FROM salas WHERE isActive = 1 ORDER BY nome ASC")
    List<Sala> getAllActiveSalas();

    @Query("SELECT * FROM salas WHERE id = :id AND isActive = 1 LIMIT 1")
    Sala getSalaById(String id);

    @Query("UPDATE salas SET isActive = 0 WHERE id = :id")
    int inativar(String id);
}
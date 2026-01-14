package pt.ipleiria.estg.dei.coworkipleiria_02;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.RewriteQueriesToDropUnusedColumns;  // adiciona este import

import pt.ipleiria.estg.dei.coworkipleiria_02.model.Sala;

import java.util.List;

@Dao
public interface SalaDao {

    @Insert
    void insert(Sala sala);

    @Update
    void update(Sala sala);

//    @RewriteQueriesToDropUnusedColumns  // ← adiciona isto para o Room ignorar colunas extras
//    @Query("SELECT * FROM salas WHERE is_active = 1 ORDER BY nome ASC")
//    List<Sala> getAllActiveSalas();
//
//    @RewriteQueriesToDropUnusedColumns  // ← e aqui também
//    @Query("SELECT * FROM salas WHERE id = :id AND is_active = 1 LIMIT 1")
//    Sala getSalaById(String id);
//
//    @Query("UPDATE salas SET is_active = 0 WHERE id = :id")
//    int inativar(String id);
}
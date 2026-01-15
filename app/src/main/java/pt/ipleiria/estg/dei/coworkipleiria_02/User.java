package pt.ipleiria.estg.dei.coworkipleiria_02;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "users")  // Nome da tabela no banco
public class User {

    @PrimaryKey(autoGenerate = true)  // ID auto-incremento
    public int id;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "nome")
    public String nome;

    @ColumnInfo(name = "data_registo")
    public long dataRegisto;


    @Ignore
    public User() {}


    public User(String email, String password, String nome) {
        this.email = email;
        this.password = password;
        this.nome = nome;
        this.dataRegisto = System.currentTimeMillis();
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public long getDataRegisto() { return dataRegisto; }
    public void setDataRegisto(long dataRegisto) { this.dataRegisto = dataRegisto; }
}
package pt.ipleiria.estg.dei.coworkipleiria_02.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;


@Entity(tableName = "salas")
public class Sala implements Serializable{
    @PrimaryKey
    @NonNull
    private String id;
    @ColumnInfo(name = "nome")
    private String nome;
    @ColumnInfo(name = "capacidade")
    private int capacidade;
    @ColumnInfo(name = "tipo")
    private TipoSala tipo;
    @ColumnInfo(name = "disponivel")
    private boolean disponivel;
    @ColumnInfo(name = "precoPorHora")
    private Double precoPorHora;
    @ColumnInfo(name = "isActive")
    private boolean isActive;

    public Sala(String id, String nome, int capacidade, TipoSala tipo, boolean disponivel,
                Double precoPorHora, boolean isActive) {
        this.id = id;
        this.nome = nome;
        this.capacidade = capacidade;
        this.tipo = tipo;
        this.disponivel = disponivel;
        this.precoPorHora = precoPorHora;
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public TipoSala getTipo() {
        return tipo;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public Double getPrecoPorHora() {
        return precoPorHora;
    }

    public enum TipoSala {
        INDIVIDUAL,
        EQUIPE_PEQUENA,
        EQUIPE_MEDIA,
        REUNIAO
    }
}



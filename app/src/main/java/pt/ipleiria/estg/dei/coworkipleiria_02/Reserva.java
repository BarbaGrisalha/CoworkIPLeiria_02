package pt.ipleiria.estg.dei.coworkipleiria_02;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import androidx.room.Update;

import java.io.Serializable;
import java.util.Date;

import pt.ipleiria.estg.dei.coworkipleiria_02.model.Sala;


@Entity(tableName = "reservas")
@TypeConverters(Converters.class)
public class Reserva implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private Sala sala;

    private String data;

    private String horaInicio;

    private String horaFim;

    private int duracaoHoras;

    private double precoTotal;

    private Date dataReserva;

    private String status;
    private int userId;
    private String salaId;


    public Reserva() {}

    public String getSalaId() {
        return salaId;
    }

    public void setSalaId(String salaId) {
        this.salaId = salaId;
    }


    @Ignore
    public Reserva(Sala sala,String data, String horaInicio, String horaFim,
                   int duracaoHoras, double precoTotal) {
        this.sala = sala;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.duracaoHoras = duracaoHoras;
        this.precoTotal = precoTotal;
        this.dataReserva = new Date();
        this.status = "Pendente";
        this.userId = 0;
        this.salaId = sala.getId();
    }

    public int getUserId() {
        return userId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public String getData() {
        return data;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public void setData(String data) {
        this.data = data;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(String horaFim) {
        this.horaFim = horaFim;
    }

    public int getDuracaoHoras() {
        return duracaoHoras;
    }

    public void setDuracaoHoras(int duracaoHoras) {
        this.duracaoHoras = duracaoHoras;
    }

    public double getPrecoTotal() {
        return precoTotal;
    }

    public void setPrecoTotal(double precoTotal) {
        this.precoTotal = precoTotal;
    }

    public Date getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(Date dataReserva) {
        this.dataReserva = dataReserva;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
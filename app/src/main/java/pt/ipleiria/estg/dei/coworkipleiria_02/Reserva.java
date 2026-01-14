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
@TypeConverters(Converters.class)  // Se já tens Converters, mantém
public class Reserva implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private Sala sala;

    private String data;           // ex: "12/01/2026" – String ok, não precisa converter

    private String horaInicio;

    private String horaFim;

    private int duracaoHoras;

    private double precoTotal;

    private Date dataReserva;

    private String status;
    private int userId;
    private String salaId;

    // Construtor vazio OBRIGATÓRIO pro Room
    public Reserva() {}

    public String getSalaId() {
        return salaId;
    }

    public void setSalaId(String salaId) {
        this.salaId = salaId;
    }

    // Novo: Construtor conveniente com os parâmetros que tu usa no PagamentoFragment
    @Ignore
    public Reserva(Sala sala,String data, String horaInicio, String horaFim,
                   int duracaoHoras, double precoTotal) {
        this.sala = sala;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.duracaoHoras = duracaoHoras;
        this.precoTotal = precoTotal;

        // Valores defaults pros outros campos (opcional, mas bom)
        this.dataReserva = new Date();          // data/hora atual da reserva
        this.status = "Pendente";               // ou "Confirmada" se já pagou
        this.userId = 0;                        // Vai setar depois com o user logado
        this.salaId = sala.getId();
    }

    public int getUserId() {
        return userId;
    }
    // GETTERS e SETTERS para TODOS os campos (públicos!)
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
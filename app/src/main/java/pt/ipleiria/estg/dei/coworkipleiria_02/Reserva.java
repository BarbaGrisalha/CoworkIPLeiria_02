package pt.ipleiria.estg.dei.coworkipleiria_02;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;

import pt.ipleiria.estg.dei.coworkipleiria_02.model.Sala;

@Entity(tableName = "reservas")
@TypeConverters(Converters.class)
public class Reserva implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private Sala sala;
    private String salaId;               // ID da sala como String (vem da API e da Sala.getId())
    private int userId;                  // ID do utilizador (customer_id da API)

    private String data;                 // Data da reserva (ex: "2025-01-16")
    private String horaInicio;
    private String horaFim;
    private int duracaoHoras;

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    private double precoTotal;
    private String status;               // "Pendente", "Confirmada", "Cancelada", etc.

    // Campos adicionais que vieram da API e que faz sentido manter
    private String tipoReserva;          // opcional, se a API devolver
    private String dataReserva;
    private int customerId;// String simples (ex: "2025-01-16") - mais fácil para UI

    public int getCustomerId() {
        return customerId;
    }

    // Construtor vazio (obrigatório para Room)
    public Reserva() {}

    // Construtor usado quando crias reserva nova a partir da UI
    @Ignore
    public Reserva(Sala sala, String data, String horaInicio, String horaFim,
                   int duracaoHoras, double precoTotal) {
        this.sala = sala;
        this.salaId = sala.getId();  // assume que Sala tem getId() que retorna String
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.duracaoHoras = duracaoHoras;
        this.precoTotal = precoTotal;
        this.status = "Pendente";
        this.userId = 0;  // será setado depois quando associar ao utilizador logado
        this.dataReserva = data;  // mesma data
        this.tipoReserva = "Normal"; // valor padrão ou vindo da UI
    }

    // Getters e Setters - organizados e completos

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
        this.salaId = sala != null ? sala.getId() : null;
    }

    public String getSalaId() {
        return salaId;
    }

    public void setSalaId(String salaId) {
        this.salaId = salaId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        this.dataReserva = data; // sincroniza os dois campos
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(String dataReserva) {
        this.dataReserva = dataReserva;
        this.data = dataReserva; // mantém sincronizado
    }

    public String getTipoReserva() {
        return tipoReserva;
    }

    public void setTipoReserva(String tipoReserva) {
        this.tipoReserva = tipoReserva;
    }

    // Se precisares de mais campos da API (horaInicioAgendada, etc.), podes adicionar aqui
}
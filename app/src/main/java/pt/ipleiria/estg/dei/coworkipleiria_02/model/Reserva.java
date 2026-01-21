package pt.ipleiria.estg.dei.coworkipleiria_02.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;

/**
 * Entidade Room para reservas.
 * Salva apenas campos primitivos/string para evitar problemas de integridade.
 */
@Entity(tableName = "reservas")

public class Reserva implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String salaId;          // ID da sala como String (principal referência)
    private int userId;             // ID do usuário (do login)
    private int customerId;         // ID do cliente (do backend)
    private String data;            // Data da reserva (YYYY-MM-DD)
    private String horaInicio;      // HH:mm:ss
    private String horaFim;         // HH:mm:ss
    private int duracaoHoras;
    private double precoTotal;
    private String status;          // "Pendente", "Paga", "Confirmada", "Cancelada", etc.
    private String tipoReserva;     // "hora", "diaria", "mensal"
    private String dataReserva;     // Data de criação/reserva (YYYY-MM-DD)
    private String reservationCode; // Código de acesso gerado

    // Construtor vazio (obrigatório para Room)
    public Reserva() {}

    // Construtor para criação rápida (usado no PagamentoFragment)
    @Ignore
    public Reserva(String salaId, String data, String horaInicio, String horaFim,
                   int duracaoHoras, double precoTotal) {
        this.salaId = salaId;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.duracaoHoras = duracaoHoras;
        this.precoTotal = precoTotal;
        this.status = "Pendente";
        this.userId = 0; // será setado depois
        this.dataReserva = data;
        this.tipoReserva = "Normal";
    }

    // Getters e Setters

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getSalaId() { return salaId; }
    public void setSalaId(String salaId) { this.salaId = salaId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getData() { return data; }
    public void setData(String data) {
        this.data = data;
        this.dataReserva = data; // sincroniza os dois campos
    }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFim() { return horaFim; }
    public void setHoraFim(String horaFim) { this.horaFim = horaFim; }

    public int getDuracaoHoras() { return duracaoHoras; }
    public void setDuracaoHoras(int duracaoHoras) { this.duracaoHoras = duracaoHoras; }

    public double getPrecoTotal() { return precoTotal; }
    public void setPrecoTotal(double precoTotal) { this.precoTotal = precoTotal; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTipoReserva() { return tipoReserva; }
    public void setTipoReserva(String tipoReserva) { this.tipoReserva = tipoReserva; }

    public String getDataReserva() { return dataReserva; }
    public void setDataReserva(String dataReserva) { this.dataReserva = dataReserva; }

    public String getReservationCode() { return reservationCode; }
    public void setReservationCode(String reservationCode) { this.reservationCode = reservationCode; }
}
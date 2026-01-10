package pt.ipleiria.estg.dei.coworkipleiria_02;

import java.io.Serializable;
import java.util.Date;

public class Reserva  implements Serializable {
    private Sala sala;
    private String data;          // ex: "12/01/2026"
    private String horaInicio;
    private String horaFim;
    private int duracaoHoras;
    private double precoTotal;
    private Date dataReserva;     // data/hora em que foi feita a reserva
    private String status;        // "Confirmada", "Paga", etc.

    public Reserva(Sala sala, String data, String horaInicio, String horaFim,
                   int duracaoHoras, double precoTotal) {
        this.sala = sala;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.duracaoHoras = duracaoHoras;
        this.precoTotal = precoTotal;
        this.dataReserva = new Date(); // data atual
        this.status = "Confirmada e Paga";
    }
    // Getters
    public Sala getSala() { return sala; }
    public String getData() { return data; }
    public String getHoraInicio() { return horaInicio; }
    public String getHoraFim() { return horaFim; }
    public int getDuracaoHoras() { return duracaoHoras; }
    public double getPrecoTotal() { return precoTotal; }
    public Date getDataReserva() { return dataReserva; }
    public String getStatus() { return status; }

    // Para mostrar no RecyclerView de Minhas Reservas (opcional)
    @Override
    public String toString() {
        return sala.getNome() + " - " + data + " (" + horaInicio + " às " + horaFim + ")";
    }

}

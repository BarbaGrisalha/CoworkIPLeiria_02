package pt.ipleiria.estg.dei.coworkipleiria_02;

import java.util.ArrayList;
import java.util.List;

public class ReservasManager {
    private static final List<Reserva> minhasReservas = new ArrayList<>();

    public static void adicionarReserva(Reserva reserva) {
        minhasReservas.add(reserva);
    }

    public static List<Reserva> getMinhasReservas() {
        return new ArrayList<>(minhasReservas); // cópia para segurança
    }

    public static int getTotalReservas() {
        return minhasReservas.size();
    }
}
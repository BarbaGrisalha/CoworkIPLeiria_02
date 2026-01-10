package pt.ipleiria.estg.dei.coworkipleiria_02;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import pt.ipleiria.estg.dei.coworkipleiria_02.Sala;

public class ReservaFragment extends Fragment {

    private static final String ARG_SALA = "sala";

    private Sala sala;  // ← agora declarado aqui
    private TextView tvTituloSala, tvDetalhesSala, tvPrecoTotal;
    private DatePicker datePicker;
    private TimePicker timePickerInicio, timePickerFim;
    private Button btnVerificarDisponibilidade;

    //Lista fake de horários ocupados da sala 1 e 2 sºo pra testarmos

    private static final List<ReservaFake> reservasOcupadas = new ArrayList<>();

    static {
        reservasOcupadas.add(new ReservaFake("1", 2026, 1, 12, 10, 0, 12, 0)); // sala 1 ocupada 10h-12h dia 12/01
        reservasOcupadas.add(new ReservaFake("2", 2026, 1, 13, 14, 0, 16, 0)); // sala 2 ocupada 14h-16h dia 13/01
    }

    //Classe interna simples para poder simular a reserva.
    private static class ReservaFake {
        String salaId;
        int ano, mes, dia, horaInicio, minInicio, horaFim, minFim;

        ReservaFake(String salaId, int ano, int mes, int dia, int hI, int mI, int hF, int mF) {
            this.salaId = salaId;
            this.ano = ano;
            this.mes = mes;
            this.dia = dia;
            this.horaInicio = hI;
            this.minInicio = mI;
            this.horaFim = hF;
            this.minFim = mF;
        }
    }

    public static ReservaFragment newInstance(Sala sala) {
        ReservaFragment fragment = new ReservaFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SALA, sala);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reserva, container, false);

        tvTituloSala = view.findViewById(R.id.tvTituloSala);
        tvDetalhesSala = view.findViewById(R.id.tvDetalhesSala);
        tvPrecoTotal = view.findViewById(R.id.tvPrecoTotal);
        datePicker = view.findViewById(R.id.datePicker);
        timePickerInicio = view.findViewById(R.id.timePickerInicio);
        timePickerFim = view.findViewById(R.id.timePickerFim);
        btnVerificarDisponibilidade = view.findViewById(R.id.btnVerificarDisponibilidade);

        // Carrega a sala
        if (getArguments() != null) {
            sala = (Sala) getArguments().getSerializable(ARG_SALA);
            if (sala != null) {
                tvTituloSala.setText("Reserva: " + sala.getNome());
                String detalhes = "Capacidade: " + sala.getCapacidade() + " pessoas\n" +
                        "Tipo: " + sala.getTipo().name().replace("_", " ") + "\n" +
                        "Disponível agora: " + (sala.isDisponivel() ? "Sim" : "Não");
                if (sala.getPrecoPorHora() != null && sala.getPrecoPorHora() > 0) {
                    detalhes += "\nPreço: " + String.format("%.2f €/hora", sala.getPrecoPorHora());
                } else {
                    detalhes += "\nGratuito";
                }
                tvDetalhesSala.setText(detalhes);
            }
        }

        timePickerInicio.setIs24HourView(true);
        timePickerFim.setIs24HourView(true);

        // Atualiza preço quando mudar qualquer hora
        timePickerInicio.setOnTimeChangedListener((picker, hourOfDay, minute) -> atualizarPrecoTotal());
        timePickerFim.setOnTimeChangedListener((picker, hourOfDay, minute) -> atualizarPrecoTotal());

        btnVerificarDisponibilidade.setOnClickListener(v -> verificarEDisponibilidade());

        return view;
    }

    private void atualizarPrecoTotal() {
        if (sala == null || sala.getPrecoPorHora() == null || sala.getPrecoPorHora() <= 0) {
            tvPrecoTotal.setText("Preço total: Gratuito");
            return;
        }

        int horaInicio = timePickerInicio.getHour();
        int horaFim = timePickerFim.getHour();

        int horas = horaFim - horaInicio;

        if (horas <= 0) {
            tvPrecoTotal.setText("Hora de fim deve ser posterior à de início");
            return;
        }

        double precoTotal = horas * sala.getPrecoPorHora();
        tvPrecoTotal.setText(String.format("Preço total: %.2f € (%d hora%s)", precoTotal, horas, horas > 1 ? "s" : ""));
    }

    private void verificarEDisponibilidade() {
        int dia = datePicker.getDayOfMonth();
        int mes = datePicker.getMonth() + 1; // Janeiro = 0 → +1
        int ano = datePicker.getYear();

        int horaInicio = timePickerInicio.getHour();
        int minutoInicio = timePickerInicio.getMinute();

        int horaFim = timePickerFim.getHour();
        int minutoFim = timePickerFim.getMinute();

        // 1. Validação: Dentro do horário de funcionamento (09:00–19:00)
        if (horaInicio < 9 || horaFim > 19 ||
                (horaInicio == 19 && minutoInicio > 0) ||
                (horaFim == 19 && minutoFim > 0)) {
            Toast.makeText(getContext(), "Horário fora do funcionamento (09:00–19:00)", Toast.LENGTH_LONG).show();
            return;
        }

        // 2. Apenas horas fechadas (sem minutos)
        if (minutoInicio != 0 || minutoFim != 0) {
            Toast.makeText(getContext(), "Apenas horários fechados (ex: 10:00, 11:00)", Toast.LENGTH_LONG).show();
            return;
        }

        // 3. Hora fim > hora início + duração mínima 1h
        int duracaoHoras = horaFim - horaInicio;
        if (duracaoHoras < 1) {
            Toast.makeText(getContext(), "Duração mínima: 1 hora", Toast.LENGTH_LONG).show();
            return;
        }

        // 4. Simulação: Verificar se o horário está ocupado (lista fake)
        boolean ocupado = false;
        for (ReservaFake reserva : reservasOcupadas) {
            if (reserva.salaId.equals(sala.getId()) &&
                    reserva.ano == ano && reserva.mes == mes && reserva.dia == dia) {
                // Sobreposição simples
                if (horaInicio < reserva.horaFim && horaFim > reserva.horaInicio) {
                    ocupado = true;
                    break;
                }
            }
        }

        if (ocupado) {
            Toast.makeText(getContext(), "Horário já ocupado! Escolha outro.", Toast.LENGTH_LONG).show();
            return;
        }

        // 5. Sucesso total!
        double precoTotal = duracaoHoras * (sala.getPrecoPorHora() != null ? sala.getPrecoPorHora() : 0.0);

        // Feedback rápido para o utilizador
        Toast.makeText(getContext(), "Horário disponível! A redirecionar para pagamento...", Toast.LENGTH_SHORT).show();

        // Abre o fragment de pagamento
        PagamentoFragment pagamentoFragment = PagamentoFragment.newInstance(
                precoTotal,
                String.format("%02d/%02d/%d", dia, mes, ano),  // ex: 12/01/2026
                String.format("%02d:00", horaInicio),          // ex: 10:00
                String.format("%02d:00", horaFim),
                sala// ex: 12:00
        );

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content_container, pagamentoFragment)  // usa o ID correto do teu container
                .addToBackStack(null)  // permite voltar com botão back
                .commit();
    }
}
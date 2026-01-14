package pt.ipleiria.estg.dei.coworkipleiria_02;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.List;

public class EditarReservaFragment extends Fragment {

    private static final String ARG_RESERVA = "reserva";

    private Reserva reserva;
    private TextView tvSalaNome;
    private DatePicker datePicker;
    private TimePicker timePickerInicio;
    private TimePicker timePickerFim;
    private Button btnSalvar;
    private Button btnCancelar;

    public static EditarReservaFragment newInstance(Reserva reserva) {
        EditarReservaFragment fragment = new EditarReservaFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RESERVA, reserva);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_reserva, container, false);

        // Inicializa as views
        tvSalaNome = view.findViewById(R.id.tvSalaNome);
        datePicker = view.findViewById(R.id.datePicker);
        timePickerInicio = view.findViewById(R.id.timePickerInicio);
        timePickerFim = view.findViewById(R.id.timePickerFim);
        btnSalvar = view.findViewById(R.id.btnSalvar);
        btnCancelar = view.findViewById(R.id.btnCancelar);

        // Carrega os dados da reserva atual
        if (getArguments() != null) {
            reserva = (Reserva) getArguments().getSerializable(ARG_RESERVA);
            if (reserva != null) {
                if (reserva.getSala() != null) {
                    tvSalaNome.setText(reserva.getSala().getNome());
                } else {
                    tvSalaNome.setText("Sala_old não identificada");
                }

                // Preenche a data no DatePicker
                String[] partesData = reserva.getData().split("/");
                if (partesData.length == 3) {
                    try {
                        int dia = Integer.parseInt(partesData[0]);
                        int mes = Integer.parseInt(partesData[1]) - 1; // Janeiro = 0
                        int ano = Integer.parseInt(partesData[2]);
                        datePicker.updateDate(ano, mes, dia);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Erro ao carregar data", Toast.LENGTH_SHORT).show();
                    }
                }

                // Preenche horários (formato HH:00)
                try {
                    String[] inicio = reserva.getHoraInicio().split(":");
                    timePickerInicio.setHour(Integer.parseInt(inicio[0]));
                    timePickerInicio.setMinute(0);

                    String[] fim = reserva.getHoraFim().split(":");
                    timePickerFim.setHour(Integer.parseInt(fim[0]));
                    timePickerFim.setMinute(0);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Erro ao carregar horários", Toast.LENGTH_SHORT).show();
                }
            }
        }

        // Configura formato 24h
        timePickerInicio.setIs24HourView(true);
        timePickerFim.setIs24HourView(true);

        // Listeners dos botões
        btnSalvar.setOnClickListener(v -> tentarSalvarAlteracao());
        btnCancelar.setOnClickListener(v -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void tentarSalvarAlteracao() {
        if (reserva == null) {
            Toast.makeText(getContext(), "Nenhuma reserva carregada", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lê os novos valores
        int dia = datePicker.getDayOfMonth();
        int mes = datePicker.getMonth() + 1;
        int ano = datePicker.getYear();
        String novaData = String.format("%02d/%02d/%d", dia, mes, ano);

        int horaInicio = timePickerInicio.getHour();
        int horaFim = timePickerFim.getHour();

        // Validações básicas
        if (horaFim <= horaInicio) {
            Toast.makeText(getContext(), "A hora de fim deve ser posterior à de início", Toast.LENGTH_LONG).show();
            return;
        }

        if (horaInicio < 9 || horaFim > 19) {
            Toast.makeText(getContext(), "Horário deve estar entre 09:00 e 19:00", Toast.LENGTH_LONG).show();
            return;
        }

        String novoInicio = String.format("%02d:00", horaInicio);
        String novoFim = String.format("%02d:00", horaFim);

        // Verifica conflitos (ignorando a própria reserva)
        AppDatabase db = AppDatabase.getDatabase(requireContext());
//        List<Reserva> conflitos = db.reservaDao().findConflitos(
//                reserva.getSalaId(),
//                novaData,
//                novoInicio,
//                novoFim,
//                reserva.getId()
//        );
//
//        if (!conflitos.isEmpty()) {
//            Toast.makeText(getContext(), "Horário já ocupado por outra reserva!", Toast.LENGTH_LONG).show();
//            return;
//        }

        // Atualiza os campos da reserva
        reserva.setData(novaData);
        reserva.setHoraInicio(novoInicio);
        reserva.setHoraFim(novoFim);

        int duracao = horaFim - horaInicio;
        reserva.setDuracaoHoras(duracao);

        Double precoHora = (reserva.getSala() != null) ? reserva.getSala().getPrecoPorHora() : null;
        double precoTotal = (precoHora != null && precoHora > 0) ? duracao * precoHora : 0.0;
        reserva.setPrecoTotal(precoTotal);

        // Salva no banco
        db.reservaDao().update(reserva);

        Toast.makeText(getContext(), "Reserva atualizada com sucesso!", Toast.LENGTH_SHORT).show();

        // Volta para a tela anterior
        if (getParentFragmentManager() != null) {
            getParentFragmentManager().popBackStack();
        }
    }
}
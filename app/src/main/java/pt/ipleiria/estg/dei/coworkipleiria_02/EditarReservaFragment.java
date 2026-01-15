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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditarReservaFragment extends Fragment {

    private static final String ARG_RESERVA = "reserva";

    private Reserva reservaAtual;
    private DatePicker datePicker;
    private TimePicker timePickerInicio, timePickerFim;
    private TextView tvPrecoTotal;
    private Button btnSalvar;

    public static EditarReservaFragment newInstance(Reserva reserva) {
        EditarReservaFragment fragment = new EditarReservaFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RESERVA, reserva);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            reservaAtual = (Reserva) getArguments().getSerializable(ARG_RESERVA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reserva, container, false);

        datePicker = view.findViewById(R.id.datePicker);
        timePickerInicio = view.findViewById(R.id.timePickerInicio);
        timePickerFim = view.findViewById(R.id.timePickerFim);
        tvPrecoTotal = view.findViewById(R.id.tvPrecoTotal);
        btnSalvar = view.findViewById(R.id.btnVerificarDisponibilidade);

        // Muda título e botão
        ((TextView) view.findViewById(R.id.tvTituloSala)).setText("Editar Reserva");
        btnSalvar.setText("Salvar Alterações");

        // Preenche com dados atuais
        preencherCampos();

        // Listener do botão Salvar
        btnSalvar.setOnClickListener(v -> salvarAlteracoes());

        return view;
    }

    private void preencherCampos() {
        String[] dataParts = reservaAtual.getData().split("/");
        if (dataParts.length == 3) {
            int day = Integer.parseInt(dataParts[0]);
            int month = Integer.parseInt(dataParts[1]) - 1;
            int year = Integer.parseInt(dataParts[2]);
            datePicker.updateDate(year, month, day);
        }

        // Horas (formato "HH:mm")
        String[] inicioParts = reservaAtual.getHoraInicio().split(":");
        timePickerInicio.setHour(Integer.parseInt(inicioParts[0]));
        timePickerInicio.setMinute(Integer.parseInt(inicioParts[1]));

        String[] fimParts = reservaAtual.getHoraFim().split(":");
        timePickerFim.setHour(Integer.parseInt(fimParts[0]));
        timePickerFim.setMinute(Integer.parseInt(fimParts[1]));

        // Preço total
        tvPrecoTotal.setText("Preço total: " + String.format("%.2f €", reservaAtual.getPrecoTotal()));
    }

    private void salvarAlteracoes() {
        // Pega nova data/hora
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();
        String novaData = String.format("%02d/%02d/%d", day, month, year);

        int horaInicio = timePickerInicio.getHour();
        int minInicio = timePickerInicio.getMinute();
        String novaHoraInicio = String.format("%02d:%02d", horaInicio, minInicio);

        int horaFim = timePickerFim.getHour();
        int minFim = timePickerFim.getMinute();
        String novaHoraFim = String.format("%02d:%02d", horaFim, minFim);

        // Validação: data/hora no futuro
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date novaDataHora = sdf.parse(novaData + " " + novaHoraInicio);
            if (novaDataHora.before(new Date())) {
                Toast.makeText(requireContext(), "Não pode alterar para data/hora no passado", Toast.LENGTH_LONG).show();
                return;
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Erro ao validar data", Toast.LENGTH_SHORT).show();
            return;
        }

        // Atualiza a reserva
        reservaAtual.setData(novaData);
        reservaAtual.setHoraInicio(novaHoraInicio);
        reservaAtual.setHoraFim(novaHoraFim);

        // Aqui guarda na base de dados - lembrar disso.
        AppDatabase db = AppDatabase.getDatabase(requireContext());
        db.reservaDao().update(reservaAtual);

        Toast.makeText(requireContext(), "Reserva atualizada com sucesso", Toast.LENGTH_SHORT).show();

        // Volta para lista
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
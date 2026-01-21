package pt.ipleiria.estg.dei.coworkipleiria_02;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import pt.ipleiria.estg.dei.coworkipleiria_02.model.Reserva;

public class EditarReservaFragment extends Fragment {

    private static final String ARG_RESERVA = "reserva";
    private Reserva reserva;

    private DatePicker datePicker;
    private TimePicker timeInicio;
    private TimePicker timeFim;
    private Button btnVerificarDisponibilidade;
    private Button btnSalvar;
    private Button btnCancelar;
    private TextView tvPrecoTotal;

    public static EditarReservaFragment newInstance(Reserva reserva) {
        EditarReservaFragment fragment = new EditarReservaFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RESERVA, reserva);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_reserva, container, false);

        datePicker = view.findViewById(R.id.datePicker);
        timeInicio = view.findViewById(R.id.timePickerInicio);
        timeFim = view.findViewById(R.id.timePickerFim);
        btnVerificarDisponibilidade = view.findViewById(R.id.btnVerificarDisponibilidade);
        btnSalvar = view.findViewById(R.id.btnSalvar);
        btnCancelar = view.findViewById(R.id.btnCancelar);
        tvPrecoTotal = view.findViewById(R.id.tvPrecoTotal);

        if (getArguments() != null) {
            reserva = (Reserva) getArguments().getSerializable(ARG_RESERVA);
            preencherCampos();
        }

        btnSalvar.setOnClickListener(v -> salvarAlteracoes());
        btnCancelar.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        btnVerificarDisponibilidade.setOnClickListener(v -> verificarDisponibilidade());

        return view;
    }

    private void preencherCampos() {
        try {
            // Preenche data
            String[] dataParts = reserva.getDataReserva().split("-");
            if (dataParts.length == 3) {
                datePicker.updateDate(
                        Integer.parseInt(dataParts[0]),
                        Integer.parseInt(dataParts[1]) - 1,
                        Integer.parseInt(dataParts[2])
                );
            }

            // Preenche hora início
            String[] inicioParts = reserva.getHoraInicio().split(":");
            if (inicioParts.length >= 2) {
                timeInicio.setHour(Integer.parseInt(inicioParts[0]));
                timeInicio.setMinute(Integer.parseInt(inicioParts[1]));
            }

            // Preenche hora fim
            String[] fimParts = reserva.getHoraFim().split(":");
            if (fimParts.length >= 2) {
                timeFim.setHour(Integer.parseInt(fimParts[0]));
                timeFim.setMinute(Integer.parseInt(fimParts[1]));
            }

            // Preço total (exemplo)
            if (tvPrecoTotal != null) {
                tvPrecoTotal.setText("Preço Total: " + String.format("%.2f €", reserva.getPrecoTotal()));
            }
        } catch (Exception e) {
            Log.e("EDITAR_RESERVA", "Erro ao preencher campos", e);
            Toast.makeText(getContext(), "Erro ao carregar dados da reserva", Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarAlteracoes() {
        try {
            // Coleta novos valores
            String novaData = String.format("%04d-%02d-%02d",
                    datePicker.getYear(),
                    datePicker.getMonth() + 1,
                    datePicker.getDayOfMonth());

            String novaHoraInicio = String.format("%02d:%02d:00",
                    timeInicio.getHour(),
                    timeInicio.getMinute());

            String novaHoraFim = String.format("%02d:%02d:00",
                    timeFim.getHour(),
                    timeFim.getMinute());

            // Validação básica
            if (novaHoraInicio.compareTo(novaHoraFim) >= 0) {
                Toast.makeText(getContext(), "Hora fim deve ser após início", Toast.LENGTH_SHORT).show();
                return;
            }

            // Atualiza objeto local
            reserva.setData(novaData);
            reserva.setHoraInicio(novaHoraInicio);
            reserva.setHoraFim(novaHoraFim);
            int duracao = Integer.parseInt(novaHoraFim.split(":")[0]) - Integer.parseInt(novaHoraInicio.split(":")[0]);
            reserva.setDuracaoHoras(duracao > 0 ? duracao : 1);

            // Envia PUT para backend
            String url = "http://10.0.2.2:8080/cowork/api/web/reservation/" + reserva.getId();

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("data_reserva", novaData);
            jsonBody.put("hora_inicio_agendada", novaData + " " + novaHoraInicio);
            jsonBody.put("hora_fim_agendada", novaData + " " + novaHoraFim);
            jsonBody.put("duracao_horas", reserva.getDuracaoHoras());
            // Adicione outros campos editáveis se precisar

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                    response -> {
                        Log.d("EDITAR_SUCCESS", "Resposta PUT: " + response.toString());
                        Toast.makeText(getContext(), "Alterações salvas com sucesso!", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    },
                    error -> {
                        String body = error.networkResponse != null ? new String(error.networkResponse.data) : "Sem resposta";
                        Log.e("EDITAR_ERRO", "Erro PUT - Status: " + (error.networkResponse != null ? error.networkResponse.statusCode : "null") + " - Body: " + body);
                        Toast.makeText(getContext(), "Erro ao salvar alterações", Toast.LENGTH_LONG).show();
                    });

            Volley.newRequestQueue(getContext()).add(request);
        } catch (Exception e) {
            Log.e("EDITAR_RESERVA", "Erro ao preparar PUT", e);
            Toast.makeText(getContext(), "Erro ao preparar alterações", Toast.LENGTH_SHORT).show();
        }
    }

    private void verificarDisponibilidade() {
        String novaData = String.format("%04d-%02d-%02d",
                datePicker.getYear(),
                datePicker.getMonth() + 1,
                datePicker.getDayOfMonth());

        String novaHoraInicio = String.format("%02d:%02d:00",
                timeInicio.getHour(),
                timeInicio.getMinute());

        Toast.makeText(getContext(), "Verificando disponibilidade para " + novaData + " " + novaHoraInicio, Toast.LENGTH_SHORT).show();

        // Implementação real: Volley GET (ajuste URL conforme seu endpoint)
        // String url = "http://10.0.2.2:8080/cowork/api/web/reservation/availability?data=" + novaData + "&hora_inicio=" + novaHoraInicio;
        // JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, ...);
    }
}
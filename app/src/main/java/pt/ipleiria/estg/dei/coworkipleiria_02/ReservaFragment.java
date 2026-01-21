package pt.ipleiria.estg.dei.coworkipleiria_02;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pt.ipleiria.estg.dei.coworkipleiria_02.model.Sala;

public class ReservaFragment extends Fragment {

    private Sala salaSelecionada;
    private String tipoReserva;

    public static ReservaFragment newInstance(Sala sala, String tipo) {
        ReservaFragment fragment = new ReservaFragment();
        Bundle args = new Bundle();
        args.putSerializable("sala", sala);
        args.putString("tipo", tipo);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reserva, container, false);

        if (getArguments() != null) {
            salaSelecionada = (Sala) getArguments().getSerializable("sala");
            tipoReserva = getArguments().getString("tipo");
        }

        DatePicker datePicker = view.findViewById(R.id.datePicker);
        TimePicker timePickerInicio = view.findViewById(R.id.timePickerInicio);
        TimePicker timePickerFim = view.findViewById(R.id.timePickerFim);
        Button btnConfirmar = view.findViewById(R.id.btnConfirmarReserva);

        if (!"hora".equals(tipoReserva)) {
            timePickerFim.setVisibility(View.GONE);
        }

        btnConfirmar.setOnClickListener(v -> {
            int ano = datePicker.getYear();
            int mes = datePicker.getMonth() + 1;
            int dia = datePicker.getDayOfMonth();

            String horaInicio = String.format("%02d:%02d:00", timePickerInicio.getHour(), timePickerInicio.getMinute());
            String horaFim = String.format("%02d:%02d:00", timePickerFim.getHour(), timePickerFim.getMinute());

            String dataReserva = String.format("%04d-%02d-%02d", ano, mes, dia);
            String dataHoraInicio = dataReserva + " " + horaInicio;
            String dataHoraFim = dataReserva + " " + horaFim;

            // Cria o JSON para POST
            String url = "http://10.0.2.2:8080/cowork/api/web/reservation";

            JSONObject jsonBody = new JSONObject();
            int customerId = getCustomerIdFromPrefs();  // ← Correção: usa customer_id
            if (customerId == -1) {
                Toast.makeText(getContext(), "Usuário não identificado. Faça login novamente.", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                jsonBody.put("room_id", salaSelecionada.getId());
                jsonBody.put("customer_id", customerId);  // Envia o ID correto (ex: 1)
                jsonBody.put("data_reserva", dataReserva);
                jsonBody.put("hora_inicio_agendada", dataHoraInicio);
                jsonBody.put("hora_fim_agendada", dataHoraFim);
                jsonBody.put("status", "pendente");
                jsonBody.put("tipo_reserva", tipoReserva);
                jsonBody.put("total_estimado", 7.00);

                Log.d("RESERVA_REQUEST", "Customer ID enviado: " + customerId);
                Log.d("RESERVA_REQUEST", "JSON: " + jsonBody.toString());
            } catch (JSONException e) {
                Toast.makeText(getContext(), "Erro ao preparar reserva", Toast.LENGTH_SHORT).show();
                return;
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    response -> {
                        Log.d("RESERVA_OK", response.toString());
                        try {
                            if (response.getBoolean("success")) {
                                int reservationId = response.getInt("id");
                                Toast.makeText(getContext(), "Reserva criada! ID: " + reservationId, Toast.LENGTH_LONG).show();

                                // Calcula ou usa o total
                                double totalEstimado = 7.00;

                                // Pega os valores já escolhidos
                                String data = dataReserva;
                                String horaInicioStr = horaInicio;
                                String horaFimStr = horaFim;

                                // Navega passando os 5 parâmetros diretos
                                PagamentoFragment pagamentoFragment = PagamentoFragment.newInstance(
                                        totalEstimado,
                                        data,
                                        horaInicioStr,
                                        horaFimStr,
                                        salaSelecionada,
                                        tipoReserva,
                                        reservationId
                                );

                                requireActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.main_content_container, pagamentoFragment)
                                        .addToBackStack(null)
                                        .commit();
                            } else {
                                Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "Erro na resposta do backend", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Log.e("RESERVA_ERRO", error.toString());
                        if (error.networkResponse != null) {
                            String body = new String(error.networkResponse.data);
                            Log.e("RESERVA_BODY", body);
                            Toast.makeText(getContext(), "Erro: " + error.networkResponse.statusCode + " " + body, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Falha na conexão", Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Accept", "application/json");
                    return headers;
                }
            };

            Volley.newRequestQueue(getContext()).add(request);
        });

        return view;
    }

    private int getCustomerIdFromPrefs() {
        SharedPreferences prefs = requireContext().getSharedPreferences("user", MODE_PRIVATE);
        return prefs.getInt("customer_id", -1);  // Agora lê o customer_id salvo
    }
}
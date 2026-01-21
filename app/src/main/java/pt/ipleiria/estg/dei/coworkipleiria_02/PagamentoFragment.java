package pt.ipleiria.estg.dei.coworkipleiria_02;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pt.ipleiria.estg.dei.coworkipleiria_02.model.Sala;

public class PagamentoFragment extends Fragment {

    private static final String ARG_TOTAL = "total";
    private static final String ARG_DATA = "data";
    private static final String ARG_HORA_INICIO = "hora_inicio";
    private static final String ARG_HORA_FIM = "hora_fim";
    private static final String ARG_SALA = "sala";
    private static final String ARG_TIPO_RESERVA = "tipo_reserva";
    private static final String ARG_RESERVATION_ID = "reservation_id";

    private double total;
    private String data, horaInicio, horaFim;
    private String tipoReserva;
    private Sala sala;
    private long reservaId = -1;  // Inicializa com -1 (inválido)

    private EditText etNumeroCartao, etValidade, etCvv;
    private Button btnPagar;

    public static PagamentoFragment newInstance(double total, String data, String horaInicio, String horaFim, Sala sala, String tipoReserva, long reservationId) {
        PagamentoFragment fragment = new PagamentoFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_TOTAL, total);
        args.putString(ARG_DATA, data);
        args.putString(ARG_HORA_INICIO, horaInicio);
        args.putString(ARG_HORA_FIM, horaFim);
        args.putSerializable(ARG_SALA, sala);
        args.putString(ARG_TIPO_RESERVA, tipoReserva);
        args.putLong(ARG_RESERVATION_ID, reservationId);  // Passa o ID da reserva
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pagamento, container, false);

        TextView tvDetalhes = view.findViewById(R.id.tvDetalhesReserva);
        etNumeroCartao = view.findViewById(R.id.etNumeroCartao);
        etValidade = view.findViewById(R.id.etValidade);
        etCvv = view.findViewById(R.id.etCvv);
        btnPagar = view.findViewById(R.id.btnPagar);

        if (getArguments() != null) {
            total = getArguments().getDouble(ARG_TOTAL, 0.0);
            data = getArguments().getString(ARG_DATA, "");
            horaInicio = getArguments().getString(ARG_HORA_INICIO, "");
            horaFim = getArguments().getString(ARG_HORA_FIM, "");
            sala = (Sala) getArguments().getSerializable(ARG_SALA);
            tipoReserva = getArguments().getString(ARG_TIPO_RESERVA, "hora");
            reservaId = getArguments().getLong(ARG_RESERVATION_ID, -1);  // Recupera ID
            Log.d("PAGAMENTO", "ID recebido: " + reservaId);

            tvDetalhes.setText("Reserva para " + data + "\n" +
                    "Horário: " + horaInicio + " às " + horaFim + "\n" +
                    "Tipo: " + tipoReserva + "\n" +
                    "Total: " + String.format("%.2f €", total));

            btnPagar.setText("Pagar " + String.format("%.2f €", total));
        }

        btnPagar.setOnClickListener(v -> processarPagamento());

        return view;
    }

    private void processarPagamento() {
        String numero = etNumeroCartao.getText().toString().trim();
        String validade = etValidade.getText().toString().trim();
        String cvv = etCvv.getText().toString().trim();

        if (numero.isEmpty() || validade.isEmpty() || cvv.isEmpty()) {
            Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (numero.equals("4111111111111111")) {
            int duracaoHoras;
            try {
                duracaoHoras = Integer.parseInt(horaFim.split(":")[0]) - Integer.parseInt(horaInicio.split(":")[0]);
                if (duracaoHoras <= 0) duracaoHoras = 1;
            } catch (Exception e) {
                Toast.makeText(getContext(), "Erro ao calcular duração", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(getContext(), "Pagamento aprovado! Atualizando status...", Toast.LENGTH_SHORT).show();

            // Atualiza em background
            new Thread(() -> atualizarStatusPagamento(reservaId)).start();

            requireActivity().getSupportFragmentManager().popBackStack();
        } else {
            Toast.makeText(getContext(), "Cartão inválido. Use teste: 4111111111111111", Toast.LENGTH_LONG).show();
        }
    }
    private void atualizarStatusPagamento(long reservaId) {
        if (reservaId == -1) {
            Log.e("PAGAMENTO", "ID da reserva inválido");
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Erro: ID da reserva não recebido", Toast.LENGTH_SHORT).show();
                });
            }
            return;
        }

        String url = "http://10.0.2.2:8080/cowork/api/web/reservation/" + reservaId;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("status", "pago");
        } catch (JSONException e) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Erro ao preparar status", Toast.LENGTH_SHORT).show();
                });
            }
            return;
        }

        // Cria queue fora da thread (mais seguro)
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                response -> {
                    Log.d("PAGAMENTO_UPDATE", "Status atualizado: " + response.toString());
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Reserva paga e confirmada!", Toast.LENGTH_SHORT).show();
                        });
                    }
                },
                error -> {
                    String body = error.networkResponse != null ? new String(error.networkResponse.data) : "Sem resposta";
                    int status = error.networkResponse != null ? error.networkResponse.statusCode : 0;
                    Log.e("PAGAMENTO_UPDATE_ERRO", "Status: " + status + " | Body: " + body);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Erro ao confirmar pagamento: " + status, Toast.LENGTH_LONG).show();
                        });
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                SharedPreferences prefs = requireContext().getSharedPreferences("user", MODE_PRIVATE);
                String token = prefs.getString("auth_token", null);
                if (token != null && !token.isEmpty()) {
                    headers.put("Authorization", "Bearer " + token);
                }
                return headers;
            }
        };

        // Adiciona na queue (já criada fora da thread)
        queue.add(request);
    }
}
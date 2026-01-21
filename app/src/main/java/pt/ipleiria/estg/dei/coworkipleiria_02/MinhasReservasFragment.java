package pt.ipleiria.estg.dei.coworkipleiria_02;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ipleiria.estg.dei.coworkipleiria_02.adapter.MinhasReservasAdapter;
import pt.ipleiria.estg.dei.coworkipleiria_02.model.Reserva;

public class MinhasReservasFragment extends Fragment {

    private RecyclerView rvReservas;
    private MinhasReservasAdapter adapter;
    private List<Reserva> reservasList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_minhas_reservas, container, false);

        rvReservas = view.findViewById(R.id.recyclerMinhasReservas);
        rvReservas.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MinhasReservasAdapter(getContext(), reservasList, new MinhasReservasAdapter.OnReservaEditarListener() {
            @Override
            public void onEditarReserva(Reserva reserva) {
                EditarReservaFragment editarFragment = EditarReservaFragment.newInstance(reserva);
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_content_container, editarFragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onCancelarReserva(Reserva reserva) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Cancelar Reserva")
                        .setMessage("Tem certeza que deseja cancelar a reserva #" + reserva.getId() + "?")
                        .setPositiveButton("Sim", (dialog, which) -> cancelarReserva(reserva))
                        .setNegativeButton("Não", null)
                        .show();
            }
        });

        rvReservas.setAdapter(adapter);

        carregarReservas();

        return view;
    }

    private void carregarReservas() {
        SharedPreferences prefs = requireContext().getSharedPreferences("user", MODE_PRIVATE);
        int customerId = prefs.getInt("customer_id", -1);

        if (customerId == -1) {
            Toast.makeText(getContext(), "Sessão inválida. Faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8080/cowork/api/web/reservation/my?customer_id=" + customerId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray array = response.getJSONArray("reservations");
                            reservasList.clear();

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                Reserva r = new Reserva();
                                r.setId(obj.optLong("id", 0));
                                r.setSalaId(obj.optString("room_id", ""));
                                r.setData(obj.optString("data_reserva", ""));
                                r.setHoraInicio(obj.optString("hora_inicio_agendada", ""));
                                r.setHoraFim(obj.optString("hora_fim_agendada", ""));
                                r.setStatus(obj.optString("status", "Pendente"));
                                r.setPrecoTotal(obj.optDouble("total_estimado", 0.0));
                                r.setReservationCode(obj.optString("reservation_code", ""));
                                reservasList.add(r);
                            }

                            adapter.atualizarLista(reservasList);

                            if (reservasList.isEmpty()) {
                                Toast.makeText(getContext(), "Nenhuma reserva encontrada.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), response.optString("message", "Erro ao carregar"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e("MINHAS_RESERVAS", "Erro parsing JSON", e);
                        Toast.makeText(getContext(), "Erro ao processar reservas", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("MINHAS_RESERVAS_ERRO", error.toString());
                    String msg = "Falha ao carregar reservas";
                    if (error.networkResponse != null) {
                        String body = new String(error.networkResponse.data);
                        Log.e("MINHAS_RESERVAS_BODY", body);
                        msg += " - " + error.networkResponse.statusCode + ": " + body;
                    }
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }

//    private void carregarReservas() {
//        SharedPreferences prefs = requireContext().getSharedPreferences("user", MODE_PRIVATE);
//        int customerId = prefs.getInt("customer_id", -1);
//
//        if (customerId == -1) {
//            Toast.makeText(getContext(), "Sessão inválida. Faça login novamente.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String url = "http://10.0.2.2:8080/cowork/api/web/reservation/my?customer_id=" + customerId;
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
//                response -> {
//                    try {
//                        if (response.getBoolean("success")) {
//                            JSONArray array = response.getJSONArray("reservations");
//                            reservasList.clear();
//
//                            for (int i = 0; i < array.length(); i++) {
//                                JSONObject obj = array.getJSONObject(i);
//                                Reserva r = new Reserva();
//                                r.setId(obj.optLong("id", 0));
//                                r.setSalaId(obj.optString("room_id", ""));
//                                r.setData(obj.optString("data_reserva", ""));
//                                r.setHoraInicio(obj.optString("hora_inicio_agendada", ""));
//                                r.setHoraFim(obj.optString("hora_fim_agendada", ""));
//                                r.setStatus(obj.optString("status", "Pendente"));
//                                r.setPrecoTotal(obj.optDouble("total_estimado", 0.0));
//                                r.setReservationCode(obj.optString("reservation_code", ""));
//                                reservasList.add(r);
//                            }
//
//                            adapter.atualizarLista(reservasList);
//
//                            if (reservasList.isEmpty()) {
//                                Toast.makeText(getContext(), "Nenhuma reserva encontrada.", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(getContext(), response.optString("message", "Erro ao carregar"), Toast.LENGTH_LONG).show();
//                        }
//                    } catch (JSONException e) {
//                        Log.e("MINHAS_RESERVAS", "Erro parsing JSON", e);
//                        Toast.makeText(getContext(), "Erro ao processar reservas", Toast.LENGTH_SHORT).show();
//                    }
//                },
//                error -> {
//                    Log.e("MINHAS_RESERVAS_ERRO", error.toString());
//                    String msg = "Falha ao carregar reservas";
//                    if (error.networkResponse != null) {
//                        String body = new String(error.networkResponse.data);
//                        Log.e("MINHAS_RESERVAS_BODY", body);
//                        msg += " - " + error.networkResponse.statusCode + ": " + body;
//                    }
//                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
//                });
//
//        Volley.newRequestQueue(requireContext()).add(request);
//    }

    // Método para cancelar reserva (único, sem duplicação)
    private void cancelarReserva(Reserva reserva) {
        String url = "http://10.0.2.2:8080/cowork/api/web/reservation/" + reserva.getId();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("status", "cancelada");
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Erro ao preparar cancelamento", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                response -> {
                    Log.d("CANCELAR_SUCCESS", "Resposta: " + response.toString());
                    Toast.makeText(getContext(), "Reserva cancelada com sucesso!", Toast.LENGTH_SHORT).show();
                    carregarReservas(); // Recarrega lista
                },
                error -> {
                    String body = error.networkResponse != null ? new String(error.networkResponse.data) : "Sem resposta";
                    int status = error.networkResponse != null ? error.networkResponse.statusCode : 0;
                    Log.e("CANCELAR_ERRO", "Status: " + status + " | Body: " + body);
                    Toast.makeText(getContext(), "Erro ao cancelar: " + status + " - " + body, Toast.LENGTH_LONG).show();
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

        Volley.newRequestQueue(requireContext()).add(request);
    }
}
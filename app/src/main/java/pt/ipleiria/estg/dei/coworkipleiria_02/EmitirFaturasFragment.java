package pt.ipleiria.estg.dei.coworkipleiria_02;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;

import pt.ipleiria.estg.dei.coworkipleiria_02.adapter.FaturasAdapter;
import pt.ipleiria.estg.dei.coworkipleiria_02.model.Reserva;

public class EmitirFaturasFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvVazio;
    private List<Reserva> faturasList = new ArrayList<>();
    private FaturasAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emitir_faturas, container, false);

        recyclerView = view.findViewById(R.id.recyclerFaturas);
        tvVazio = view.findViewById(R.id.tvVazio);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FaturasAdapter(faturasList);
        recyclerView.setAdapter(adapter);

        carregarFaturas();

        requireActivity().setTitle("Emitir Fatura");

        return view;
    }

    private void carregarFaturas() {
        SharedPreferences prefs = requireContext().getSharedPreferences("user", MODE_PRIVATE);
        int customerId = prefs.getInt("customer_id", -1);

        if (customerId == -1) {
            tvVazio.setText("Faça login para emitir faturas");
            tvVazio.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }

        String url = "http://10.0.2.2:8080/cowork/api/web/faturas/my?customer_id=" + customerId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray array = response.getJSONArray("faturas");
                            faturasList.clear();

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                Reserva r = new Reserva();
                                r.setId(obj.optLong("id", 0));
                                r.setSalaId(obj.optString("room_id", ""));
                                r.setData(obj.optString("data_reserva", ""));
                                r.setHoraInicio(obj.optString("hora_inicio_agendada", ""));
                                r.setHoraFim(obj.optString("hora_fim_agendada", ""));
                                r.setPrecoTotal(obj.optDouble("total_estimado", 0.0));
                                r.setReservationCode(obj.optString("reservation_code", ""));
                                // Adicione campos extras da fatura (ex.: numero_fatura, data_emissao)
                                faturasList.add(r);
                            }

                            adapter.atualizarLista(faturasList);

                            if (faturasList.isEmpty()) {
                                tvVazio.setText("Nenhuma fatura para emitir");
                                tvVazio.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                tvVazio.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(getContext(), response.optString("message", "Erro ao carregar faturas"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e("FATURAS", "Erro parsing JSON", e);
                        Toast.makeText(getContext(), "Erro ao processar faturas", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("FATURAS_ERRO", error.toString());
                    String msg = "Falha ao carregar faturas";
                    if (error.networkResponse != null) {
                        msg += " - " + error.networkResponse.statusCode;
                    }
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }
}
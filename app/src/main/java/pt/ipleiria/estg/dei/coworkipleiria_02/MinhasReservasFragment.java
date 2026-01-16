package pt.ipleiria.estg.dei.coworkipleiria_02;



import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MinhasReservasFragment extends Fragment {

    private MinhasReservasAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_minhas_reservas, container, false);

        adapter = new MinhasReservasAdapter(
                requireContext(),
                new ArrayList<>(),
                reserva -> {
                    // Listener de editar - abre tela de edição
                    Toast.makeText(requireContext(), "Editar reserva ID: " + reserva.getId(), Toast.LENGTH_SHORT).show();
                }
        );

        RecyclerView recyclerView = view.findViewById(R.id.recyclerMinhasReservas); // ajusta ID se for diferente
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        carregarReservas();

        return view;
    }
    private void carregarReservas() {
        String url = ApiConfig.getBaseUrl(requireContext()) + "reservation";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    List<Reserva> lista = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            Reserva r = new Reserva();
                            r.setId(obj.optLong("id"));
                            r.setUserId(obj.optInt("customer_id"));
                            r.setSalaId(String.valueOf(obj.optInt("room_id"))); // converte int → String
                            r.setData(obj.optString("data_reserva"));           // ou r.setDataReserva(...)
                            r.setHoraInicio(obj.optString("hora_inicio_agendada"));
                            r.setHoraFim(obj.optString("hora_fim_agendada"));
                            r.setPrecoTotal(obj.optDouble("total_estimado"));
                            r.setStatus(obj.optString("status"));
                            r.setTipoReserva(obj.optString("tipo_reserva"));    // se existir na API
                            lista.add(r);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.atualizarLista(lista);
                    Toast.makeText(requireContext(), "Carregadas " + lista.size() + " reservas", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    String msg = "Erro ao carregar reservas";
                    if (error.networkResponse != null) {
                        msg += " - Código: " + error.networkResponse.statusCode;
                    }
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }
}
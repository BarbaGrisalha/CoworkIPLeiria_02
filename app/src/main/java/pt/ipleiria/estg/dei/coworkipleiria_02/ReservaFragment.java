package pt.ipleiria.estg.dei.coworkipleiria_02;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import pt.ipleiria.estg.dei.coworkipleiria_02.model.Sala;

public class ReservaFragment extends Fragment {

    private MinhasReservasAdapter adapter;  // Assume que tens um adapter (ajusta o nome)
    private Sala sala;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reserva, container, false);  // teu layout com RecyclerView

        if (getArguments() != null){
            Sala sala = (Sala) getArguments().getSerializable("sala");

        }
        // Inicializa o adapter (ajusta conforme o teu)
        adapter = new MinhasReservasAdapter(
                requireContext(),
                new ArrayList<>(),
                reserva -> {

                });  // teu adapter com lista vazia inicial

        // Configura RecyclerView (ajusta ID do teu layout)
        //RecyclerView recyclerView = view.findViewById(R.id.recycler_reservas);  // ID do teu RecyclerView
        //recyclerView.setAdapter(adapter);
        //ecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        carregarReservas();

        return view;
    }

    private void carregarReservas() {
        String url = ApiConfig.getBaseUrl(requireContext()) + "reservation";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<Reserva> lista = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            Reserva r = new Reserva();
                            r.setId(obj.optInt("id"));
                            r.setCustomerId(obj.optInt("customer_id"));
                            r.setSalaId(obj.optString("room_id"));
                            r.setDataReserva(obj.optString("data_reserva"));
                            r.setHoraInicio(obj.optString("hora_inicio_agendada"));
                            r.setHoraFim(obj.optString("hora_fim_agendada"));
                            r.setPrecoTotal(obj.optDouble("total_estimado"));
                            r.setStatus(obj.optString("status"));
                            r.setTipoReserva(obj.optString("tipo_reserva"));
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
    public static ReservaFragment newInstance(Sala sala) {
        ReservaFragment fragment = new ReservaFragment();
        Bundle args = new Bundle();
        args.putSerializable("sala", sala); // Sala precisa implementar Serializable
        fragment.setArguments(args);
        return fragment;
    }

}
package pt.ipleiria.estg.dei.coworkipleiria_02;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MinhasReservasFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvVazio;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_minhas_reservas, container, false);

        recyclerView = view.findViewById(R.id.recyclerMinhasReservas);
        tvVazio = view.findViewById(R.id.tvVazio);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Carrega as reservas salvas
        List<Reserva> reservas = ReservasManager.getMinhasReservas();

        if (reservas.isEmpty()) {
            tvVazio.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvVazio.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            // Cria adapter simples (vamos melhorar depois)
            MinhasReservasAdapter adapter = new MinhasReservasAdapter(reservas);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }
}
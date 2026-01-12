package pt.ipleiria.estg.dei.coworkipleiria_02;

import android.content.Context;
import android.content.SharedPreferences;
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

        // Pega o userId da sessão
        SharedPreferences prefs = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            tvVazio.setText("Faça login para ver suas reservas");
            tvVazio.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return view;
        }

        // Consulta direta no DAO
        AppDatabase db = AppDatabase.getDatabase(requireContext());
        List<Reserva> reservas = db.reservaDao().getByUser(userId);

        if (reservas == null || reservas.isEmpty()) {
            tvVazio.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvVazio.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            MinhasReservasAdapter adapter = new MinhasReservasAdapter(reservas);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }
}
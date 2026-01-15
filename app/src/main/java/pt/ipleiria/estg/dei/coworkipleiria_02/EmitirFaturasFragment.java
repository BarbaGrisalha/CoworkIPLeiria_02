package pt.ipleiria.estg.dei.coworkipleiria_02;

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

import android.content.Context;

public class EmitirFaturasFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvVazio;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emitir_faturas, container, false);

        recyclerView = view.findViewById(R.id.recyclerFaturas);
        tvVazio = view.findViewById(R.id.tvVazio);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPreferences prefs = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            tvVazio.setText("Faça login para emitir faturas");
            tvVazio.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return view;
        }

        AppDatabase db = AppDatabase.getDatabase(requireContext());
        List<Reserva> reservas = db.reservaDao().getByUser(userId);

        if (reservas.isEmpty()) {
            tvVazio.setText("Nenhuma reserva para emitir fatura");
            tvVazio.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvVazio.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            // Usa o adapter novo
            FaturasAdapter adapter = new FaturasAdapter(reservas);
            recyclerView.setAdapter(adapter);
        }


        requireActivity().setTitle("Emitir Fatura");

        return view;
    }
}
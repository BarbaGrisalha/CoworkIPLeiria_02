package pt.ipleiria.estg.dei.coworkipleiria_02;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pt.ipleiria.estg.dei.coworkipleiria_02.adapter.SalasAdapter;
import pt.ipleiria.estg.dei.coworkipleiria_02.model.Sala;

public class SalasFragment extends Fragment {

    private RecyclerView recyclerView;
    private SalasAdapter adapter;
    private List<Sala> salasList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_salas, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewSalas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Busco as salas na base de dados
        AppDatabase db = AppDatabase.getDatabase(requireContext());
        SalaDao dao = db.salaDao();

        salasList = dao.getAllActiveSalas();
        if (salasList == null || salasList.isEmpty()) {
            salasList = new ArrayList<>();
            Toast.makeText(requireContext(), "Nenhuma sala ativa disponível", Toast.LENGTH_SHORT).show();
        }

        adapter = new SalasAdapter(requireContext(), salasList, sala -> {
            ReservaFragment reservaFragment = ReservaFragment.newInstance(sala);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content_container, reservaFragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(adapter);

        return view;
    }
}
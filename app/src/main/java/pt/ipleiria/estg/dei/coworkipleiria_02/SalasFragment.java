package pt.ipleiria.estg.dei.coworkipleiria_02;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        // Criar lista de exemplo com 10 salas
        salasList = criarListaSalasExemplo();

        adapter = new SalasAdapter(salasList, sala -> {
            //a mágica acontece aqui quando clica.
            //Abre o fragmente de reserva
            ReservaFragment reservaFragment = ReservaFragment.newInstance(sala);
            //substitui o fragmente atual
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content_container, reservaFragment)
                    .addToBackStack(null) //permitir voltar com o raio do botao voltar
                    .commit();

        });
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<Sala> criarListaSalasExemplo() {
        List<Sala> lista = new ArrayList<>();

        lista.add(new Sala("1", "Sala_old 1 - Individual", 1, Sala.TipoSala.INDIVIDUAL, true, 7.0,true));
        lista.add(new Sala("2", "Sala_old 2 - Individual", 1, Sala.TipoSala.INDIVIDUAL, true, 7.0,true));
        lista.add(new Sala("3", "Sala_old 3 - Equipe Pequena", 4, Sala.TipoSala.EQUIPE_PEQUENA, true, 12.0,true));
        lista.add(new Sala("4", "Sala_old 4 - Equipe Pequena", 4, Sala.TipoSala.EQUIPE_PEQUENA, true, 12.0,true));
        lista.add(new Sala("5", "Sala_old 5 - Equipe Média", 8, Sala.TipoSala.EQUIPE_MEDIA, true, 18.0,true));
        lista.add(new Sala("6", "Sala_old 6 - Equipe Média", 8, Sala.TipoSala.EQUIPE_MEDIA, true, 18.0,true));
        lista.add(new Sala("7", "Sala_old 7 - Equipe Grande", 12, Sala.TipoSala.EQUIPE_MEDIA, true, 25.0,true));
        lista.add(new Sala("8", "Sala_old 8 - Equipe Grande", 12, Sala.TipoSala.EQUIPE_MEDIA, true, 25.0,true));
        lista.add(new Sala("9", "Sala_old 9 - Coworking Aberto", 20, Sala.TipoSala.EQUIPE_MEDIA, true, null,true));
        lista.add(new Sala("10", "Sala_old de Reuniões", 12, Sala.TipoSala.REUNIAO, true, 35.0,true));

        return lista;
    }
}
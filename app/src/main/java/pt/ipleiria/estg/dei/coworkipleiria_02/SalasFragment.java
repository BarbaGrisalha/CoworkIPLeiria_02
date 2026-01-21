package pt.ipleiria.estg.dei.coworkipleiria_02;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import pt.ipleiria.estg.dei.coworkipleiria_02.adapter.SalasAdapter;
import pt.ipleiria.estg.dei.coworkipleiria_02.model.Sala;

public class SalasFragment extends Fragment {

    private RecyclerView recyclerView;
    private SalasAdapter adapter;
    private List<Sala> salasList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_salas, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewSalas);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new SalasAdapter(salasList, new SalasAdapter.OnSalaClickListener() {
            @Override
            public void onPorHoraClick(Sala sala) {

                ReservaFragment reservaFragment = ReservaFragment.newInstance(sala, "hora");

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_content_container, reservaFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        recyclerView.setAdapter(adapter);


        carregarSalasComVolley();
        carregarSalas();

        return view;
    }
    private void carregarSalas() {
        String url = "http://10.0.2.2:8080/cowork/api/web/rooms";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d("SALAS_JSON", response);

                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        List<Sala> salas = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject item = jsonArray.getJSONObject(i);

                            Sala sala = new Sala();
                            sala.setId(item.getInt("id"));
                            sala.setNomeSala(item.getString("nome_sala"));
                            sala.setCapacidade(item.getInt("capacidade"));
                            salas.add(sala);
                        }

                        adapter = new SalasAdapter(salas, new SalasAdapter.OnSalaClickListener() {
                            @Override
                            public void onPorHoraClick(Sala sala) {

                                ReservaFragment reservaFragment = ReservaFragment.newInstance(sala, "hora");
                                requireActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.main_content_container, reservaFragment)
                                        .addToBackStack(null)
                                        .commit();
                            }
                        });
                        recyclerView.setAdapter(adapter);


                        Toast.makeText(getContext(), "Salas carregadas: " + salas.size(), Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        Log.e("SALAS", "Erro JSON", e);
                        Toast.makeText(getContext(), "Erro ao processar salas", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("SALAS", "Erro Volley", error);
                    Toast.makeText(getContext(), "Falha na conexão", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(getContext()).add(request);
    }


    private String getTextContent(Element element, String tagName) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list.getLength() > 0) {
            return list.item(0).getTextContent();
        }
        return "";
    }
    private void carregarSalasComVolley() {
        String url = "http://10.0.2.2:8080/cowork/api/web/room";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Log.d("SALAS", "Response: " + response); // veja o XML no Logcat

                    try {
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        InputSource is = new InputSource(new StringReader(response));
                        Document doc = builder.parse(is);

                        NodeList itemList = doc.getElementsByTagName("item");
                        salasList.clear();

                        for (int i = 0; i < itemList.getLength(); i++) {
                            Element item = (Element) itemList.item(i);

                            Sala sala = new Sala();
                            sala.setId(Integer.parseInt(item.getElementsByTagName("id").item(0).getTextContent()));
                            sala.setNomeSala(item.getElementsByTagName("nome_sala").item(0).getTextContent());
                            sala.setCapacidade(Integer.parseInt(item.getElementsByTagName("capacidade").item(0).getTextContent()));

                            salasList.add(sala);
                        }

                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Erro ao parsear salas", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Erro na conexão", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(getContext()).add(request);
    }
    public static ReservaFragment newInstance(Sala sala) {
        ReservaFragment fragment = new ReservaFragment();
        Bundle args = new Bundle();
        args.putSerializable("sala", sala);
        fragment.setArguments(args);
        return fragment;
    }
}
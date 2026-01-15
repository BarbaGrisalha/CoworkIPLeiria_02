package pt.ipleiria.estg.dei.coworkipleiria_02;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.Date;

import pt.ipleiria.estg.dei.coworkipleiria_02.model.Sala;

public class PagamentoFragment extends Fragment {

    private static final String ARG_TOTAL = "total";
    private static final String ARG_DATA = "data";
    private static final String ARG_HORA_INICIO = "hora_inicio";
    private static final String ARG_HORA_FIM = "hora_fim";

    private double total;
    private String data, horaInicio, horaFim;

    private EditText etNumeroCartao, etValidade, etCvv;
    private Button btnPagar;
    private static final String ARG_SALA = "sala";
    private Sala sala;

    public static PagamentoFragment newInstance(double total, String data, String horaInicio, String horaFim, Sala sala) {
        PagamentoFragment fragment = new PagamentoFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_TOTAL, total);
        args.putString(ARG_DATA, data);
        args.putString(ARG_HORA_INICIO, horaInicio);
        args.putString(ARG_HORA_FIM, horaFim);
        args.putSerializable(ARG_SALA, sala);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pagamento, container, false);


        TextView tvDetalhes = view.findViewById(R.id.tvDetalhesReserva);
        etNumeroCartao = view.findViewById(R.id.etNumeroCartao);
        etValidade = view.findViewById(R.id.etValidade);
        etCvv = view.findViewById(R.id.etCvv);
        btnPagar = view.findViewById(R.id.btnPagar);

        if (getArguments() != null) {
            total = getArguments().getDouble(ARG_TOTAL, 0.0);
            data = getArguments().getString(ARG_DATA, "");
            horaInicio = getArguments().getString(ARG_HORA_INICIO, "");
            horaFim = getArguments().getString(ARG_HORA_FIM, "");
            sala = (Sala) getArguments().getSerializable(ARG_SALA);

            tvDetalhes.setText("Reserva para " + data + "\n" +
                    "Horário: " + horaInicio + " às " + horaFim + "\n" +
                    "Total: " + String.format("%.2f €", total));
            btnPagar.setText("Pagar " + String.format("%.2f €", total));
        }

        btnPagar.setOnClickListener(v -> processarPagamento());

        return view;
    }

    private void processarPagamento() {
        String numero = etNumeroCartao.getText().toString().trim();
        String validade = etValidade.getText().toString().trim();
        String cvv = etCvv.getText().toString().trim();

        if (numero.isEmpty() || validade.isEmpty() || cvv.isEmpty()) {
            Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (numero.equals("4111111111111111")) {  // cartão VisaFake

            int duracaoHoras;
            try {
                duracaoHoras = Integer.parseInt(horaFim.split(":")[0]) - Integer.parseInt(horaInicio.split(":")[0]);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Erro ao calcular duração", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cria a reserva
            Reserva novaReserva = new Reserva(
                    sala,
                    data,
                    horaInicio,
                    horaFim,
                    duracaoHoras,
                    total
            );


            novaReserva.setDataReserva(new Date());
            novaReserva.setStatus("Paga");

            // Pega o userId da sessão atual
            SharedPreferences prefs = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE);
            int userId = prefs.getInt("userId", -1);

            if (userId == -1) {
                Toast.makeText(getContext(), "Erro: Nenhum usuário logado. Faça login novamente.", Toast.LENGTH_LONG).show();

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_content_container, new LoginFragment())
                        .commit();
                return;
            }

            novaReserva.setUserId(userId);


            try {
                AppDatabase db = AppDatabase.getDatabase(requireContext());
                db.reservaDao().insert(novaReserva);

                Toast.makeText(getContext(),
                        "Pagamento aprovado!\n" +
                                "Reserva confirmada com sucesso!\n" +
                                "Valor: " + String.format("%.2f €", total) +
                                "\nSalva em Minhas Reservas!",
                        Toast.LENGTH_LONG).show();


                requireActivity().getSupportFragmentManager().popBackStack();

            } catch (Exception e) {
                Toast.makeText(getContext(), "Erro ao salvar a reserva no banco: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("PagamentoFragment", "Erro ao inserir reserva", e);
            }

        } else {
            Toast.makeText(getContext(), "Cartão inválido. Use o teste: 4111 1111 1111 1111", Toast.LENGTH_LONG).show();
        }
    }
}
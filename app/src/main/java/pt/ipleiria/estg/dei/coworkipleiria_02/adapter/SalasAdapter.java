package pt.ipleiria.estg.dei.coworkipleiria_02.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pt.ipleiria.estg.dei.coworkipleiria_02.R;
import pt.ipleiria.estg.dei.coworkipleiria_02.model.Sala;

public class SalasAdapter extends RecyclerView.Adapter<SalasAdapter.SalaViewHolder> {

    private List<Sala> salasList;
    private OnSalaClickListener listener;

    public SalasAdapter(List<Sala> salasList, OnSalaClickListener listener) {
        this.salasList = salasList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SalaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sala, parent, false);
        return new SalaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalaViewHolder holder, int position) {
        Sala sala = salasList.get(position);

        holder.tvNomeSala.setText(sala.getNome());
        holder.tvTipoSala.setText("Tipo: " + sala.getTipo().name().replace("_", " "));
        holder.tvCapacidade.setText("Capacidade: " + sala.getCapacidade() + " pessoas");

        if (sala.getPrecoPorHora() != null) {
            holder.tvPreco.setText(String.format("%.0f € / hora", sala.getPrecoPorHora()));
        } else {
            holder.tvPreco.setText("Gratuito");
        }

        if (sala.isDisponivel()) {
            holder.tvDisponibilidade.setText("Disponível");
            holder.tvDisponibilidade.setBackgroundResource(R.drawable.bg_disponivel);
        } else {
            holder.tvDisponibilidade.setText("Ocupada");
            holder.tvDisponibilidade.setBackgroundResource(R.drawable.bg_ocupada);
        }

        // Aqui está o clique no item inteiro (já tinhas, só garanti a formatação)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSalaClick(sala);
            }
        });
    }

    @Override
    public int getItemCount() {
        return salasList.size();
    }

    static class SalaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNomeSala, tvTipoSala, tvCapacidade, tvPreco, tvDisponibilidade;

        public SalaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNomeSala = itemView.findViewById(R.id.tvNomeSala);
            tvTipoSala = itemView.findViewById(R.id.tvTipoSala);
            tvCapacidade = itemView.findViewById(R.id.tvCapacidade);
            tvPreco = itemView.findViewById(R.id.tvPreco);
            tvDisponibilidade = itemView.findViewById(R.id.tvDisponibilidade);
        }
    }

    public interface OnSalaClickListener {
        void onSalaClick(Sala sala);
    }
}
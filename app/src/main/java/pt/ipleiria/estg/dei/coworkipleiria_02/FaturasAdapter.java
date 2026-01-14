package pt.ipleiria.estg.dei.coworkipleiria_02;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FaturasAdapter extends RecyclerView.Adapter<FaturasAdapter.ViewHolder> {

    private List<Reserva> reservasList;

    public FaturasAdapter(List<Reserva> reservasList) {
        this.reservasList = reservasList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fatura, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reserva reserva = reservasList.get(position);

        // Resumo simples: Código da reserva + Sala_old + Data
        String resumo = "Reserva #" + reserva.getId() +
                " - " + reserva.getSala().getNome() +
                " - " + reserva.getData();

        holder.tvResumo.setText(resumo);

        // Botão Emitir Fatura (reutiliza o gerador que já funciona)
        holder.btnEmitirFatura.setOnClickListener(v -> {
            PdfFaturaGenerator.gerarFatura(holder.itemView.getContext(), reserva);
        });
    }

    @Override
    public int getItemCount() {
        return reservasList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvResumo;
        Button btnEmitirFatura;

        ViewHolder(View itemView) {
            super(itemView);
            tvResumo = itemView.findViewById(R.id.tvResumo);
            btnEmitirFatura = itemView.findViewById(R.id.btnEmitirFatura);
        }
    }
}
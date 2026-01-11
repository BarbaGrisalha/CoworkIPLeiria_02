package pt.ipleiria.estg.dei.coworkipleiria_02;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MinhasReservasAdapter extends RecyclerView.Adapter<MinhasReservasAdapter.ViewHolder> {

    private List<Reserva> reservasList;

    public MinhasReservasAdapter(List<Reserva> reservasList) {
        this.reservasList = reservasList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reserva, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reserva reserva = reservasList.get(position);

        // Preenche os textos
        holder.tvResumo.setText(reserva.getSala().getNome() + " - " + reserva.getData());
        holder.tvDetalhes.setText(
                reserva.getHoraInicio() + " às " + reserva.getHoraFim() + "\n" +
                        "Duração: " + reserva.getDuracaoHoras() + "h\n" +
                        "Total: " + String.format("%.2f €", reserva.getPrecoTotal())
        );

        // Botão Emitir Fatura
        holder.btnEmitirFatura.setOnClickListener(v -> {
            PdfFaturaGenerator.gerarFatura(holder.itemView.getContext(), reserva);
        });
    }

    @Override
    public int getItemCount() {
        return reservasList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvResumo, tvDetalhes;
        Button btnEmitirFatura;

        ViewHolder(View itemView) {
            super(itemView);
            tvResumo = itemView.findViewById(R.id.tvResumo);
            tvDetalhes = itemView.findViewById(R.id.tvDetalhes);
            btnEmitirFatura = itemView.findViewById(R.id.btnEmitirFatura);
        }
    }
}
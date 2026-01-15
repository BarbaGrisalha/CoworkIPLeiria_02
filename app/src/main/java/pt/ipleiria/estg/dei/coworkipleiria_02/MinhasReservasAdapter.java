package pt.ipleiria.estg.dei.coworkipleiria_02;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MinhasReservasAdapter extends RecyclerView.Adapter<MinhasReservasAdapter.ViewHolder> {

    private final Context context;
    private final List<Reserva> reservasList;
    private final OnReservaEditarListener listener;

    public interface OnReservaEditarListener {
        void onEditarReserva(Reserva reserva);
    }

    public MinhasReservasAdapter(Context context, List<Reserva> reservasList, OnReservaEditarListener listener) {
        this.context = context;
        this.reservasList = reservasList;
        this.listener = listener;
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

        // Preenche os textos pra ver reserva
        holder.tvResumo.setText(reserva.getSala().getNome() + " - " + reserva.getData());
        holder.tvDetalhes.setText(
                reserva.getHoraInicio() + " às " + reserva.getHoraFim() + "\n" +
                        "Duração: " + reserva.getDuracaoHoras() + "h\n" +
                        "Total: " + String.format("%.2f €", reserva.getPrecoTotal())
        );

        // Botão Emitir Fatura
        holder.btnEmitirFatura.setOnClickListener(v -> {
            PdfFaturaGenerator.gerarFatura(context, reserva);
        });

        // Botão Editar que chama o listener que tá no fragment
        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditarReserva(reserva);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservasList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvResumo, tvDetalhes;
        Button btnEmitirFatura, btnEditar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvResumo = itemView.findViewById(R.id.tvResumo);
            tvDetalhes = itemView.findViewById(R.id.tvDetalhes);
            btnEmitirFatura = itemView.findViewById(R.id.btnEmitirFatura);
            btnEditar = itemView.findViewById(R.id.btnEditar);
        }
    }
}
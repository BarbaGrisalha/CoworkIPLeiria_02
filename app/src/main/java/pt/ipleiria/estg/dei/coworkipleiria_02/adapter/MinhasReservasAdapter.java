package pt.ipleiria.estg.dei.coworkipleiria_02.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pt.ipleiria.estg.dei.coworkipleiria_02.PdfFaturaGenerator;
import pt.ipleiria.estg.dei.coworkipleiria_02.R;
import pt.ipleiria.estg.dei.coworkipleiria_02.model.Reserva;

public class MinhasReservasAdapter extends RecyclerView.Adapter<MinhasReservasAdapter.ViewHolder> {

    private final Context context;
    private List<Reserva> reservasList;
    private final OnReservaEditarListener listener;

    public interface OnReservaEditarListener {
        void onEditarReserva(Reserva reserva);
        void onCancelarReserva(Reserva reserva);
    }

    public MinhasReservasAdapter(Context context, List<Reserva> reservasList, OnReservaEditarListener listener) {
        this.context = context;
        this.reservasList = reservasList;
        this.listener = listener;
    }

    public void atualizarLista(List<Reserva> novaLista) {
        this.reservasList = novaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reserva, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reserva reserva = reservasList.get(position);

        // Resumo seguro (evita NullPointer)
        String salaNome = reserva.getSalaId() != null ? "Sala " + reserva.getSalaId() : "Sala desconhecida";
        holder.tvResumo.setText(salaNome + " - " + reserva.getData());

        holder.tvResumo.setText(salaNome + " - " + reserva.getData());

        holder.tvDetalhes.setText(
                reserva.getHoraInicio() + " às " + reserva.getHoraFim() + "\n" +
                        "Duração: " + reserva.getDuracaoHoras() + "h\n" +
                        "Total: " + String.format("%.2f €", reserva.getPrecoTotal())
        );

        // Cor por status
        String status = reserva.getStatus() != null ? reserva.getStatus().toLowerCase() : "";
        switch (status) {
            case "paga":
            case "confirmado":
                holder.tvDetalhes.setTextColor(Color.GREEN);
                break;
            case "pendente":
                holder.tvDetalhes.setTextColor(Color.parseColor("#FFA500"));
                break;
            case "cancelada":
                holder.tvDetalhes.setTextColor(Color.RED);
                break;
            default:
                holder.tvDetalhes.setTextColor(Color.GRAY);
        }

        // Código de acesso (se existir)
        if (reserva.getReservationCode() != null && !reserva.getReservationCode().isEmpty()) {
            holder.tvDetalhes.append("\nCódigo: " + reserva.getReservationCode());
        }

        // Emitir fatura
        holder.btnEmitirFatura.setOnClickListener(v -> {
            try {
                PdfFaturaGenerator.gerarFatura(context, reserva);
                Toast.makeText(context, "Fatura gerada com sucesso!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(context, "Erro ao gerar fatura: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Editar (só se pendente)
        boolean podeEditar = "pendente".equalsIgnoreCase(status);
        holder.btnEditar.setEnabled(podeEditar);
        holder.btnEditar.setAlpha(podeEditar ? 1.0f : 0.5f);
        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null && podeEditar) {
                listener.onEditarReserva(reserva);
            } else {
                Toast.makeText(context, "Esta reserva não pode ser editada (status: "
                        + status + ")", Toast.LENGTH_SHORT).show();    }
        });
        // Cancelar (só se pendente / laranja)
        boolean podeCancelar = "pendente".equalsIgnoreCase(status);
        holder.btnCancelar.setVisibility(podeCancelar ? View.VISIBLE : View.GONE);
        holder.btnCancelar.setEnabled(podeCancelar);
        holder.btnCancelar.setAlpha(podeCancelar ? 1.0f : 0.5f);

        holder.btnCancelar.setOnClickListener(v -> {
            if (listener != null && podeCancelar) {
                listener.onCancelarReserva(reserva);
            } else {
                Toast.makeText(context, "Esta reserva não pode ser cancelada.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservasList != null ? reservasList.size() : 0;
    }

    // ViewHolder correto (classe interna estática)
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvResumo, tvDetalhes;
        Button btnEmitirFatura, btnEditar,btnCancelar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvResumo   = itemView.findViewById(R.id.tvResumo);
            tvDetalhes = itemView.findViewById(R.id.tvDetalhes);
            btnEmitirFatura = itemView.findViewById(R.id.btnEmitirFatura);
            btnEditar  = itemView.findViewById(R.id.btnEditar);
            btnCancelar = itemView.findViewById(R.id.btnCancelar);
        }
    }

}

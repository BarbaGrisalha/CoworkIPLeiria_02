package pt.ipleiria.estg.dei.coworkipleiria_02.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pt.ipleiria.estg.dei.coworkipleiria_02.AppDatabase;
import pt.ipleiria.estg.dei.coworkipleiria_02.R;
import pt.ipleiria.estg.dei.coworkipleiria_02.SalaDao;
import pt.ipleiria.estg.dei.coworkipleiria_02.model.Sala;

public class SalasAdapter extends RecyclerView.Adapter<SalasAdapter.ViewHolder> {

    private List<Sala> salasList;
    private OnSalaClickListener listener;
    private Context context;

    public SalasAdapter(Context context, List<Sala> salasList, OnSalaClickListener listener) {
        this.context = context;
        this.salasList = salasList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sala, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Sala sala = salasList.get(position);

        holder.tvNomeSala.setText(sala.getNome());
        holder.tvTipoSala.setText("Tipo: " + sala.getTipo().name());
        holder.tvCapacidade.setText("Capacidade: " + sala.getCapacidade() + " pessoas");
        holder.tvPreco.setText(sala.getPrecoPorHora() != null ? sala.getPrecoPorHora() + " € / hora" : "Preço não definido");
        holder.tvDisponibilidade.setText(sala.isDisponivel() ? "Disponível" : "Indisponível");
        holder.tvDisponibilidade.setBackgroundResource(sala.isDisponivel() ? R.drawable.bg_disponivel : R.drawable.bg_indisponivel);

        // Clique pra abrir reserva
        holder.itemView.setOnClickListener(v -> listener.onSalaClick(sala));

        // Botão Inativar, em vez de excluir pra não detonar base de dados.
        holder.imgInativar.setOnClickListener(v -> confirmarInativarSala(sala, position));
    }

    private void confirmarInativarSala(Sala sala, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Inativar Sala")
                .setMessage("Tem certeza que deseja inativar a sala \"" + sala.getNome() + "\"?\n\nEla deixará de aparecer na lista, mas os dados históricos ficam preservados.")
                .setPositiveButton("Sim, inativar", (dialog, which) -> {
                    AppDatabase db = AppDatabase.getDatabase(context);
                    SalaDao dao = db.salaDao();
                    int updated = dao.inativar(sala.getId());

                    if (updated > 0) {
                        // Remove da lista e atualiza o minha exibiçao.
                        salasList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, salasList.size());
                        Toast.makeText(context, "Sala inativada com sucesso", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Erro ao inativar sala", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return salasList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNomeSala, tvTipoSala, tvCapacidade, tvPreco, tvDisponibilidade;
        ImageView imgInativar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNomeSala = itemView.findViewById(R.id.tvNomeSala);
            tvTipoSala = itemView.findViewById(R.id.tvTipoSala);
            tvCapacidade = itemView.findViewById(R.id.tvCapacidade);
            tvPreco = itemView.findViewById(R.id.tvPreco);
            tvDisponibilidade = itemView.findViewById(R.id.tvDisponibilidade);
            imgInativar = itemView.findViewById(R.id.img_inativar);
        }
    }

    // Interface do listener pra clicar na sala
    public interface OnSalaClickListener {
        void onSalaClick(Sala sala);
    }
}
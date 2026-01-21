package pt.ipleiria.estg.dei.coworkipleiria_02.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import pt.ipleiria.estg.dei.coworkipleiria_02.R;
import pt.ipleiria.estg.dei.coworkipleiria_02.ReservaFragment;
import pt.ipleiria.estg.dei.coworkipleiria_02.model.Sala;
import java.util.List;

public class SalasAdapter extends RecyclerView.Adapter<SalasAdapter.ViewHolder> {

    private List<Sala> salasList;
    private OnSalaClickListener listener;


    public interface OnSalaClickListener{
        void onPorHoraClick(Sala sala);
    }
    private OnSalaClickListener onSalaClickListener;

//    public SalasAdapter(List<Sala> salasList) {
//        this.salasList = salasList;
//    }
public SalasAdapter(List<Sala> salasList, OnSalaClickListener listener) {
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

        holder.tvNomeSala.setText(sala.getNomeSala());
        holder.tvCapacidade.setText("Até " + sala.getCapacidade() + " pessoas");

        holder.btnPorHora.setText("Por Hora");
        holder.btnDiaria.setText("Diária R$ 32");
        holder.btnMensal.setText("Mensal R$ 225");

        holder.btnPorHora.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPorHoraClick(sala);
            }
        });


    }

    @Override
    public int getItemCount() {
        return salasList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSala;
        TextView tvNomeSala, tvCapacidade;
        Button btnPorHora, btnDiaria, btnMensal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSala = itemView.findViewById(R.id.img_sala);
            tvNomeSala = itemView.findViewById(R.id.tv_nome_sala);
            tvCapacidade = itemView.findViewById(R.id.tv_capacidade);
            btnPorHora = itemView.findViewById(R.id.btn_por_hora);
            btnDiaria = itemView.findViewById(R.id.btn_diaria);
            btnMensal = itemView.findViewById(R.id.btn_mensal);
        }
    }


}
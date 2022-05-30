package com.example.piazza.recyclerView.historialHores;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.piazza.commons.OnSwipeTouchListener;
import com.example.testauth.R;

import java.util.List;

public class ListAdapterHistorialHores extends RecyclerView.Adapter<ListAdapterHistorialHores.ViewHolder> {

    private List<ListElementHistorialHores> mData;
    private LayoutInflater mInflater;
    private Context context;
    final onItemClickListener listener;

    public interface onItemClickListener {
        void onItemClickListener(ListElementHistorialHores item);
    }

    public ListAdapterHistorialHores(List<ListElementHistorialHores> itemList, Context context, onItemClickListener listener) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = itemList;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int ViewType) {
        View view = mInflater.from(parent.getContext()).inflate(R.layout.list_element_historial_hores, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.bindData(mData.get(position));
    }

    public void setItems(List<ListElementHistorialHores> items) {
        mData = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView data, total, dia;
        RelativeLayout rl;
        CardView cv;

        ViewHolder(View itemView) {
            super(itemView);
            total = itemView.findViewById(R.id.totalTextView);
            data = itemView.findViewById(R.id.dataTextView);
            dia = itemView.findViewById(R.id.txtDia);
            rl = itemView.findViewById(R.id.relativeLayoutColor);
            cv = itemView.findViewById(R.id.cv);
        }

        void bindData (final ListElementHistorialHores item) {

            String dataStr = String.format("%02d/%02d/%4d",item.getHorario().getDiaEntrada(), item.getHorario().getMesEntrada(), item.getHorario().getAnioEntrada());
            String totalFinalStr = String.format("%dh %02dm",item.getHorario().getTotalMinutsTreballats()/60, item.getHorario().getTotalMinutsTreballats()%60);

            total.setText(totalFinalStr);
            data.setText(dataStr);
            dia.setText(item.getHorario().getDiaEntrada() + "");

            itemView.setOnClickListener(view -> listener.onItemClickListener(item));

            if (item.horario.getModificacio() != null) rl.setBackgroundColor(Color.parseColor("#F5E287"));

        }
    }

}

package com.example.piazza.recyclerView.reportHores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testauth.R;

import java.util.List;

public class ListAdapterReportHores extends RecyclerView.Adapter<ListAdapterReportHores.ViewHolder> {

    private List<ListElementReportHores> mData;
    private LayoutInflater mInflater;
    private Context context;



    public ListAdapterReportHores(List<ListElementReportHores> itemList, Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = itemList;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public ViewHolder onCreateViewHolder (ViewGroup parent, int ViewType) {
        View view = mInflater.from(parent.getContext()).inflate(R.layout.list_element_report_hores, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.bindData(mData.get(position));
    }

    public void setItems(List<ListElementReportHores> items) {
        mData = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView data, entrada, sortida, total;
        CardView cv;

        ViewHolder(View itemView) {
            super(itemView);
            total = itemView.findViewById(R.id.totalTextView);
            data = itemView.findViewById(R.id.dataTextView);
            entrada = itemView.findViewById(R.id.entradaTextView);
            sortida = itemView.findViewById(R.id.sortidaTextView);
            cv = itemView.findViewById(R.id.cv);
        }

        void bindData (final ListElementReportHores item) {

            String dataStr = String.format("%2d",item.getHorari().getDiaEntrada());
            String entradaStr = String.format("%d:%02d",item.getHorari().getHoraEntrada(),item.getHorari().getMinutEntrada()) ;
            String sortidaStr = String.format("%d:%02d",item.getHorari().getHoraSalida(), item.getHorari().getMinutSalida());
            String totalFinalStr = String.format("%dh %02dm",item.getHorari().getTotalMinutsTreballats()/60, item.getHorari().getTotalMinutsTreballats()%60);

            total.setText(totalFinalStr);
            data.setText(dataStr);
            entrada.setText(entradaStr);
            sortida.setText(sortidaStr);

        }
    }

}

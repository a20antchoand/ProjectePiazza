package com.example.piazza.recyclerView.estatTreballadors;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testauth.R;

import java.util.List;

public class ListAdapterEstatTreballadors extends RecyclerView.Adapter<ListAdapterEstatTreballadors.ViewHolder> {

    private List<ListElementEstatTreballadors> mData;
    private LayoutInflater mInflater;
    private Context context;
    final ListAdapterEstatTreballadors.onItemClickListener listener;

    /**
     * Interficie per asignar un onItemClickListener
     */

    public interface onItemClickListener {
        void onItemClickListener(ListElementEstatTreballadors item);
    }

    /**
     * Constructor de l'adaptador de la recycler view
     * @param itemList
     * @param context
     * @param listener
     */
    public ListAdapterEstatTreballadors(List<ListElementEstatTreballadors> itemList, Context context, ListAdapterEstatTreballadors.onItemClickListener listener) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = itemList;
        this.listener = listener;
    }

    /**
     * getter de la cantitat d'items
     * @return cantitat de items
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public ListAdapterEstatTreballadors.ViewHolder onCreateViewHolder (ViewGroup parent, int ViewType) {
        View view = mInflater.from(parent.getContext()).inflate(R.layout.list_element_estat_treballadors, null);
        return new ListAdapterEstatTreballadors.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListAdapterEstatTreballadors.ViewHolder holder, final int position) {
        holder.bindData(mData.get(position));
    }

    public void setItems(List<ListElementEstatTreballadors> items) {
        mData = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView nom, hores, estat;
        CardView cv;

        ViewHolder(View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.iconImageView);
            nom = itemView.findViewById(R.id.nomTextView);
            hores = itemView.findViewById(R.id.totalTextView);
            estat = itemView.findViewById(R.id.estatTextView);
            cv = itemView.findViewById(R.id.cv);
        }

        void bindData (final ListElementEstatTreballadors item) {
            nom.setText(item.getNom());
            hores.setText(item.getHores());
            estat.setText(item.getEstat());
            itemView.setOnClickListener(view -> listener.onItemClickListener(item));
        }
    }

}

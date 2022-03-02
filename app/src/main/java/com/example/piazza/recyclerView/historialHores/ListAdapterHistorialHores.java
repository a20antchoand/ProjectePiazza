package com.example.piazza.recyclerView.historialHores;

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
        View view = mInflater.from(parent.getContext()).inflate(R.layout.list_element_estat_treballadors, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.cv.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition));
        holder.bindData(mData.get(position));
    }

    public void setItems(List<ListElementHistorialHores> items) {
        mData = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView nom, hores, estat;
        CardView cv;

        ViewHolder(View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.iconImageView);
            nom = itemView.findViewById(R.id.nomUsuariTextView);
            hores = itemView.findViewById(R.id.horesUsuariTextView);
            estat = itemView.findViewById(R.id.estadoTextView);
            cv = itemView.findViewById(R.id.cv);
        }

        void bindData (final ListElementHistorialHores item) {
            iconImage.setColorFilter(Color.parseColor(item.getData()), PorterDuff.Mode.SRC_IN);
            nom.setText(item.getEntrada());
            hores.setText(item.getSortida());
            estat.setText(item.getTotal());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClickListener(item);
                }
            });
        }
    }

}

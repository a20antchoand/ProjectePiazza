package com.example.piazza.recyclerView;

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

    public interface onItemClickListener {
        void onItemClickListener(ListElementEstatTreballadors item);
    }

    public ListAdapterEstatTreballadors(List<ListElementEstatTreballadors> itemList, Context context, ListAdapterEstatTreballadors.onItemClickListener listener) {
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
    public ListAdapterEstatTreballadors.ViewHolder onCreateViewHolder (ViewGroup parent, int ViewType) {
        View view = mInflater.from(parent.getContext()).inflate(R.layout.list_element_estat_treballadors, null);
        return new ListAdapterEstatTreballadors.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListAdapterEstatTreballadors.ViewHolder holder, final int position) {
        holder.cv.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition));
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
            nom = itemView.findViewById(R.id.nomUsuariTextView);
            hores = itemView.findViewById(R.id.horesUsuariTextView);
            estat = itemView.findViewById(R.id.estadoTextView);
            cv = itemView.findViewById(R.id.cv);
        }

        void bindData (final ListElementEstatTreballadors item) {
            iconImage.setColorFilter(Color.parseColor(item.getColor()), PorterDuff.Mode.SRC_IN);
            nom.setText(item.getNom());
            hores.setText(item.getHores());
            estat.setText(item.getEstat());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClickListener(item);
                }
            });
        }
    }

}

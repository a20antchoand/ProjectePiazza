package com.example.piazza.recyclerView.treballadors;

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
import com.google.firebase.auth.FirebaseAuthException;

import java.util.List;

public class ListAdapterTreballadors extends RecyclerView.Adapter<ListAdapterTreballadors.ViewHolder> {

    private List<ListElementTreballadors> mData;
    private LayoutInflater mInflater;
    private Context context;
    final onItemClickListener listener;

    public interface onItemClickListener {
        void onItemClickListener(ListElementTreballadors item) throws FirebaseAuthException;
    }

    public ListAdapterTreballadors(List<ListElementTreballadors> itemList, Context context, onItemClickListener listener) {
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
        View view = mInflater.from(parent.getContext()).inflate(R.layout.list_element_treballadors, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.cv.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition));
        holder.bindData(mData.get(position));
    }

    public void setItems(List<ListElementTreballadors> items) {
        mData = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView nom, hores, sou;
        CardView cv;

        ViewHolder(View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.iconImageView);
            nom = itemView.findViewById(R.id.nomTextView);
            hores = itemView.findViewById(R.id.horesMensualsTextView);
            sou = itemView.findViewById(R.id.souTextView);
            cv = itemView.findViewById(R.id.cv);
        }

        void bindData (final ListElementTreballadors item) {
            iconImage.setColorFilter(Color.parseColor(item.getColor()), PorterDuff.Mode.SRC_IN);
            nom.setText(item.getNom());
            hores.setText(item.getHores());
            sou.setText(item.getSou());
            itemView.setOnClickListener(view -> {
                try {
                    listener.onItemClickListener(item);
                } catch (FirebaseAuthException e) {
                    e.printStackTrace();
                }
            });

        }
    }

}

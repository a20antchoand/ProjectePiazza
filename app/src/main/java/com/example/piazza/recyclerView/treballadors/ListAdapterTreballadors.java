package com.example.piazza.recyclerView.treballadors;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.view.menu.MenuView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.testauth.R;
import com.google.firebase.auth.FirebaseAuthException;

import java.util.List;

public class ListAdapterTreballadors extends RecyclerView.Adapter<ListAdapterTreballadors.ViewHolder> implements AuthUserSession {

    private List<ListElementTreballadors> mData;
    private LayoutInflater mInflater;
    private Context context;
    final onItemClickListener listener;

    public interface onItemClickListener {
        void onItemClickListener(ListElementTreballadors item, View itemview) throws FirebaseAuthException;
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
        View view = mInflater.inflate(R.layout.list_element_treballadors, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.bindData(mData.get(position), context);
    }

    public void setItems(List<ListElementTreballadors> items) {
        mData = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView nom;
        View estat;
        View itemView;

        ViewHolder(View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.iconImageView);
            nom = itemView.findViewById(R.id.nom);
            estat = itemView.findViewById(R.id.estatJornada);
            this.itemView = itemView;
        }

        void bindData (final ListElementTreballadors item, Context context) {
            nom.setText(item.getNom() + " " + item.getCognom());

            if (item.getTreballant())
                estat.setBackground(context.getResources().getDrawable(R.drawable.rounded_treballant));
            else
                estat.setBackground(context.getResources().getDrawable(R.drawable.rounded_no_treballant));


        }

    }

}

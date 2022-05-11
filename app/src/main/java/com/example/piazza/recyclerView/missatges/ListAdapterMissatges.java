package com.example.piazza.recyclerView.missatges;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.view.menu.MenuView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.piazza.fireBase.session.AuthUserSession;
import com.example.testauth.R;
import com.google.firebase.auth.FirebaseAuthException;

import java.text.SimpleDateFormat;
import java.util.List;

public class ListAdapterMissatges extends RecyclerView.Adapter<ListAdapterMissatges.ViewHolder> implements AuthUserSession {

    private List<Missatge> mData;
    private LayoutInflater mInflater;
    private Context context;
    final onItemClickListener listener;

    public interface onItemClickListener {
        void onItemClickListener(Missatge item, View itemview) throws FirebaseAuthException;
    }

    public ListAdapterMissatges(List<Missatge> itemList, Context context, onItemClickListener listener) {
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
        View view = mInflater.inflate(R.layout.list_element_missatges, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(mData.get(position), position);
    }

    public void setItems(List<Missatge> items) {
        mData = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nom, missatge, hora;

        ViewHolder(View itemView) {
            super(itemView);
            nom = itemView.findViewById(R.id.nomMissatge);
            missatge = itemView.findViewById(R.id.missatge);
            hora = itemView.findViewById(R.id.hora);
        }

        void bindData (Missatge item, int position) {
            nom.setText(item.getUsuari().getNom());
            missatge.setText(item.getMissatge());

            SimpleDateFormat horaPattern = new SimpleDateFormat("HH:mm");

            hora.setText(horaPattern.format(item.getHora().toDate()));

        }

    }

}

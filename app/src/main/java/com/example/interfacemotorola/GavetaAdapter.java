package com.example.interfacemotorola;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GavetaAdapter extends RecyclerView.Adapter<GavetaAdapter.ViewHolder> {

    private Context context;
    private List<AppInfo> appsList;

    public GavetaAdapter(Context context, List<AppInfo> appsList) {
        this.context = context;
        this.appsList = appsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_app, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppInfo app = appsList.get(position);

        holder.nomeApp.setText(app.label);
        holder.iconeApp.setImageDrawable(app.icon);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(app.packageName);
            if (intent != null) {
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconeApp;
        TextView nomeApp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconeApp = itemView.findViewById(R.id.iconeApp);
            nomeApp = itemView.findViewById(R.id.nomeApp);
        }
    }
}
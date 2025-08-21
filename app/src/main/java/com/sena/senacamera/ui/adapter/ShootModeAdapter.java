package com.sena.senacamera.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sena.senacamera.R;
import com.sena.senacamera.ui.activity.PreviewActivity;

import java.util.List;

public class ShootModeAdapter extends RecyclerView.Adapter<ShootModeAdapter.ShootModeViewHolder> {
    private List<String> shootModeList;
    private Context context;

    static class ShootModeViewHolder extends RecyclerView.ViewHolder {
        TextView modeText;
        public ShootModeViewHolder(View itemView) {
            super(itemView);
            modeText = itemView.findViewById(R.id.shoot_mode_text);
        }
    }

    public ShootModeAdapter(Context context, List<String> modeList) {
        this.context = context;
        this.shootModeList = modeList;
    }

    @NonNull
    @Override
    public ShootModeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shoot_mode, parent, false);
        return new ShootModeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShootModeViewHolder holder, int position) {
        holder.modeText.setText(shootModeList.get(position));
        if (((PreviewActivity) context).getCurrentShootMode().equals(shootModeList.get(position))) {
            holder.modeText.setTextColor(context.getColor(R.color.text_yellow));
        } else {
            holder.modeText.setTextColor(context.getColor(R.color.text_white));
        }

        holder.itemView.setOnClickListener(v -> {
            ((PreviewActivity) context).selectShootMode(position);
            ((PreviewActivity) context).scrollToSelectedShootMode(position);
        });
    }

    @Override
    public int getItemCount() {
        return shootModeList.size();
    }
}

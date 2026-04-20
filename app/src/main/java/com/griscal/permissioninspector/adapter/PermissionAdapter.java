package com.griscal.permissioninspector.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.griscal.permissioninspector.R;
import com.griscal.permissioninspector.model.PermissionInfo;

import java.util.List;

public class PermissionAdapter extends RecyclerView.Adapter<PermissionAdapter.ViewHolder> {

    public interface OnPermissionClickListener {
        void onPermissionClick(PermissionInfo perm);
    }

    private final List<PermissionInfo> permissions;
    private final OnPermissionClickListener listener;

    public PermissionAdapter(List<PermissionInfo> permissions, OnPermissionClickListener listener) {
        this.permissions = permissions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_permission, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PermissionInfo perm = permissions.get(position);
        holder.permName.setText(perm.name);
        holder.permDesc.setText(perm.description);

        int[] dotColors = {
            holder.itemView.getContext().getResources()
                .getColor(R.color.risk_low, holder.itemView.getContext().getTheme()),
            holder.itemView.getContext().getResources()
                .getColor(R.color.risk_medium, holder.itemView.getContext().getTheme()),
            holder.itemView.getContext().getResources()
                .getColor(R.color.risk_high, holder.itemView.getContext().getTheme())
        };
        holder.riskDot.setBackgroundColor(dotColors[perm.riskLevel]);

        holder.permLink.setOnClickListener(v -> listener.onPermissionClick(perm));
    }

    @Override
    public int getItemCount() {
        return permissions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View riskDot;
        TextView permName, permDesc, permLink;

        ViewHolder(View itemView) {
            super(itemView);
            riskDot = itemView.findViewById(R.id.riskDot);
            permName = itemView.findViewById(R.id.permName);
            permDesc = itemView.findViewById(R.id.permDesc);
            permLink = itemView.findViewById(R.id.permLink);
        }
    }
}

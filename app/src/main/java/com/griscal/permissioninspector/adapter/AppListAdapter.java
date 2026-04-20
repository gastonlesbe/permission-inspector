package com.griscal.permissioninspector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.griscal.permissioninspector.R;
import com.griscal.permissioninspector.model.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

    public interface OnAppClickListener {
        void onAppClick(AppInfo app);
    }

    private final Context context;
    private final OnAppClickListener listener;
    private List<AppInfo> allApps = new ArrayList<>();
    private List<AppInfo> filtered = new ArrayList<>();

    public AppListAdapter(Context context, OnAppClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setData(List<AppInfo> apps) {
        allApps = new ArrayList<>(apps);
        filtered = new ArrayList<>(apps);
        applySort(allApps);
        applySort(filtered);
        notifyDataSetChanged();
    }

    public enum SortOrder { PERMISSIONS, NAME, SIZE }

    private SortOrder currentSort = SortOrder.PERMISSIONS;

    public void sort(SortOrder order) {
        currentSort = order;
        applySort(filtered);
        applySort(allApps);
        notifyDataSetChanged();
    }

    private void applySort(java.util.List<AppInfo> list) {
        switch (currentSort) {
            case PERMISSIONS:
                list.sort((a, b) -> b.permissions.size() - a.permissions.size());
                break;
            case NAME:
                list.sort((a, b) -> a.appName.compareToIgnoreCase(b.appName));
                break;
            case SIZE:
                list.sort((a, b) -> parseSizeBytes(b.installSize) - parseSizeBytes(a.installSize));
                break;
        }
    }

    private int parseSizeBytes(String size) {
        try {
            if (size.endsWith(" MB")) return (int)(Float.parseFloat(size.replace(" MB", "")) * 1024);
            if (size.endsWith(" KB")) return (int) Float.parseFloat(size.replace(" KB", ""));
        } catch (NumberFormatException ignored) {}
        return 0;
    }

    public void filter(String query) {
        filtered = new ArrayList<>();
        String q = query.toLowerCase().trim();
        if (q.isEmpty()) {
            filtered.addAll(allApps);
        } else {
            for (AppInfo app : allApps) {
                if (app.appName.toLowerCase().contains(q) ||
                    app.packageName.toLowerCase().contains(q)) {
                    filtered.add(app);
                }
            }
        }
        applySort(filtered);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_app, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppInfo app = filtered.get(position);
        holder.icon.setImageDrawable(app.icon);
        holder.appName.setText(app.appName);
        holder.appCategory.setText(app.category);
        holder.appSize.setText(app.installSize);
        holder.appUpdated.setText("Updated " + app.lastUpdated);
        holder.appVersion.setText("v" + app.versionName);

        holder.permBadge.setText(app.permissions.size() + " perms");

        holder.itemView.setOnClickListener(v -> listener.onAppClick(app));
    }

    @Override
    public int getItemCount() {
        return filtered.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView appName, appCategory, appSize, appUpdated, appVersion, permBadge;

        ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
            appCategory = itemView.findViewById(R.id.appCategory);
            appSize = itemView.findViewById(R.id.appSize);
            appUpdated = itemView.findViewById(R.id.appUpdated);
            appVersion = itemView.findViewById(R.id.appVersion);
            permBadge = itemView.findViewById(R.id.permBadge);
        }
    }
}

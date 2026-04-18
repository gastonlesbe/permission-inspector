package com.lesbeguerisgaston.permissioninspector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lesbeguerisgaston.permissioninspector.R;
import com.lesbeguerisgaston.permissioninspector.model.AppInfo;

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
        notifyDataSetChanged();
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
        holder.permSummary.setText(app.permissions.size() + " permissions");

        String[] riskLabels = {"Low risk", "Medium", "High risk"};
        int[] riskColors = {
            context.getResources().getColor(R.color.risk_low, context.getTheme()),
            context.getResources().getColor(R.color.risk_medium, context.getTheme()),
            context.getResources().getColor(R.color.risk_high, context.getTheme())
        };
        int[] riskBgColors = {
            context.getResources().getColor(R.color.risk_low_bg, context.getTheme()),
            context.getResources().getColor(R.color.risk_medium_bg, context.getTheme()),
            context.getResources().getColor(R.color.risk_high_bg, context.getTheme())
        };

        holder.riskBadge.setText(riskLabels[app.riskLevel]);
        holder.riskBadge.setTextColor(riskColors[app.riskLevel]);
        holder.riskBadge.setBackgroundColor(riskBgColors[app.riskLevel]);

        holder.itemView.setOnClickListener(v -> listener.onAppClick(app));
    }

    @Override
    public int getItemCount() {
        return filtered.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView appName, permSummary, riskBadge;

        ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
            permSummary = itemView.findViewById(R.id.permSummary);
            riskBadge = itemView.findViewById(R.id.riskBadge);
        }
    }
}

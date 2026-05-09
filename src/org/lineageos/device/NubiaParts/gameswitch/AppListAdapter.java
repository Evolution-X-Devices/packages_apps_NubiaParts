package org.lineageos.device.NubiaParts.gameswitch;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

    public interface OnAppClickListener {
        void onClick(ResolveInfo app);
    }

    private final List<ResolveInfo> apps;
    private final OnAppClickListener listener;
    private final PackageManager pm;

    private final Map<String, String> labels = new HashMap<>();
    private final Map<String, Drawable> icons = new HashMap<>();

    public AppListAdapter(List<ResolveInfo> apps, OnAppClickListener listener, PackageManager pm) {
        this.apps = apps;
        this.listener = listener;
        this.pm = pm;

        for (ResolveInfo app : apps) {
            String pkg = app.activityInfo.packageName;
            labels.put(pkg, app.loadLabel(pm).toString());
            icons.put(pkg, app.loadIcon(pm));
        }
        this.apps.sort((a, b) -> Objects.requireNonNull(labels.get(a.activityInfo.packageName))
                .compareToIgnoreCase(labels.get(b.activityInfo.packageName)));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_ID) {
                listener.onClick(apps.get(pos));
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ResolveInfo app = apps.get(position);
        holder.icon.setImageDrawable(icons.get(app.activityInfo.packageName));
        holder.name.setText(labels.get(app.activityInfo.packageName));
        holder.itemView.setOnClickListener(v -> listener.onClick(app));
    }

    @Override
    public int getItemCount() { return apps.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView name;
        public ViewHolder(View view) {
            super(view);
            icon = view.findViewById(R.id.app_icon);
            name = view.findViewById(R.id.app_name);
        }
    }
}

package org.lineageos.device.NubiaParts.gameswitch;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import org.lineageos.device.NubiaParts.gameswitch.R;

public class AppListDialog extends DialogFragment {

    public AppListDialog() {
        super(R.layout.dialog_app_list);
    }

    public interface OnAppSelectedListener {
        void onAppSelected(ResolveInfo app);
    }

    private OnAppSelectedListener listener;

    public void setOnAppSelectedListener(OnAppSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_app_list, null);

        RecyclerView recycler = view.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(new AppListAdapter(getInstalledApps(), app -> {
            if (listener != null) listener.onAppSelected(app);
            dismiss();
        }, requireContext().getPackageManager()));

        return new AlertDialog.Builder(requireContext())
                .setTitle(R.string.app_launch_dialog_title)
                .setView(view)
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    private List<ResolveInfo> getInstalledApps() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return requireContext().getPackageManager()
                .queryIntentActivities(intent, 0);
    }
}

package org.lineageos.device.NubiaParts.gameswitch;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Bundle;

public class UnlockTrampolineActivity extends Activity {

    public static final String EXTRA_TARGET_INTENT =
            "extra_target_intent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent target = getIntent().getParcelableExtra(EXTRA_TARGET_INTENT, Intent.class);
        if (target == null) {
            finish();
            return;
        }

        KeyguardManager km =
                (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (km != null) {
            km.requestDismissKeyguard(this,
                    new KeyguardManager.KeyguardDismissCallback() {
                        @Override
                        public void onDismissSucceeded() {
                            target.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(target);
                            finish();
                        }

                        @Override
                        public void onDismissCancelled() {
                            finish();
                        }

                        @Override
                        public void onDismissError() {
                            finish();
                        }
                    });
        }
    }
}


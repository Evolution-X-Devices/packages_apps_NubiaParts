package org.lineageos.device.NubiaParts.gamekeys;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.lineageos.device.NubiaParts.Utils.ResourceUtils;
import org.lineageos.device.NubiaParts.gamekeys.LeftKeySettingsFragment;
import org.lineageos.device.NubiaParts.gamekeys.RightKeySettingsFragment;

import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;

public class GameKeysActivity extends CollapsingToolbarBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.activity_tabbed_prefs,
                findViewById(com.android.settingslib.collapsingtoolbar.R.id.content_frame));

        setContentView(R.layout.activity_tabbed_prefs);

        ResourceUtils.init(getApplicationContext());

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            private final Fragment[] fragments = new Fragment[] {
                    new LeftKeySettingsFragment(),
                    new RightKeySettingsFragment(),
            };
            private final String[] titles = new String[] {
                ResourceUtils.getString("game_keys_left_title"),
                ResourceUtils.getString("game_keys_right_title")
            };

            @Override
            public Fragment getItem(int position) { return fragments[position]; }

            @Override
            public int getCount() { return fragments.length; }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        });

    }

}

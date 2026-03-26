package cn.leaf.sftpgo.android.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import cn.leaf.sftpgo.android.R;
import cn.leaf.sftpgo.android.databinding.ActivityMainBinding;
import cn.leaf.sftpgo.android.fragment.SettingFragment;
import cn.leaf.sftpgo.android.fragment.SwitchFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Fragment switch_fragment, setting_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        switch_fragment=new SwitchFragment();
        setting_fragment=new SettingFragment();
        binding.viewPager.setAdapter(new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                if (position==0){
                    return switch_fragment;

                }else {
                    return setting_fragment;
                }
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });
        binding.viewPager.setOffscreenPageLimit(1);
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.bottomNav.getMenu().getItem(position).setChecked(true);
            }
        });
        binding.bottomNav.setOnItemSelectedListener(item -> {
            var item_id=item.getItemId();
            if (item_id== R.id.nav_switch){
                binding.viewPager.setCurrentItem(0,true);
                return true;
            }
            else if (item_id== R.id.nav_settings){
                binding.viewPager.setCurrentItem(1,true);
                return true;
            }
            return false;
        });

    }
}
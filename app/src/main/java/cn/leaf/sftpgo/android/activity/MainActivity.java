package cn.leaf.sftpgo.android.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import cn.leaf.sftpgo.android.R;
import cn.leaf.sftpgo.android.databinding.ActivityMainBinding;
import cn.leaf.sftpgo.android.fragment.SettingFragment;
import cn.leaf.sftpgo.android.fragment.SwitchFragment;

public class MainActivity extends AppCompatActivity {

    private Handler handler=new Handler(Looper.getMainLooper());

    private ActivityMainBinding binding;
    private Fragment switch_fragment=new SwitchFragment(), setting_fragment=new SettingFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//      隐藏标题栏
        var title_bar=getSupportActionBar();
        if (title_bar!=null){
            title_bar.hide();
        }
        EdgeToEdge.enable(this);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        获取管理所有文件权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
//        通知权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
//        初始化资源
        checkAndSetupConfDirectory();
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

    private void checkAndSetupConfDirectory() {
        new Thread(()->{
            File confDir = getExternalFilesDir("conf");
            if (confDir == null) {
                Log.e("?", "External files directory is not available");
                handler.post(()->{
                    Toast.makeText(MainActivity.this, "检查资源失败-无法获取conf目录", Toast.LENGTH_SHORT).show();
                });
                return;
            }
            // Check if conf directory exists, create if not
            if (!confDir.exists()) {
                if (!confDir.mkdirs()) {
                    Log.e("?", "Failed to create conf directory: " + confDir.getAbsolutePath());
                    handler.post(()->{
                        Toast.makeText(MainActivity.this, "检查资源失败-无法创建conf目录", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }
                Log.d("?", "Created conf directory: " + confDir.getAbsolutePath());
            }
            // Check if conf directory is empty
            File[] files = confDir.listFiles();
            boolean isEmpty = (files == null || files.length == 0);
            if (isEmpty) {
                Log.d("?", "Conf directory is empty, extracting sftpgo-conf.zip from assets");
                var result=extractConfFromAssets(confDir);
                if (result!=null){
                    handler.post(()->{
                        Toast.makeText(MainActivity.this, "解压资源失败-"+result, Toast.LENGTH_SHORT).show();
                    });
                }
            } else {
                Log.d("?", "Conf directory already contains files, skipping extraction");
            }
        }).start();
    }
    private String extractConfFromAssets(File confDir) {
        try {
            // Open the zip file from assets
            InputStream assetStream = getAssets().open("sftpgo-conf.zip");
            ZipInputStream zipInputStream = new ZipInputStream(assetStream);

            ZipEntry entry;
            byte[] buffer = new byte[1024];

            while ((entry = zipInputStream.getNextEntry()) != null) {
                String entryName = entry.getName();
                File entryFile = new File(confDir, entryName);

                // Create parent directories if needed
                File parentDir = entryFile.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
                }

                // Extract file or create directory
                if (entry.isDirectory()) {
                    entryFile.mkdirs();
                } else {
                    FileOutputStream fos = new FileOutputStream(entryFile);
                    int length;
                    while ((length = zipInputStream.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                    fos.close();
                }

                zipInputStream.closeEntry();
                Log.d("?", "Extracted: " + entryName);
            }

            zipInputStream.close();
            Log.d("?", "Successfully extracted sftpgo-conf.zip to: " + confDir.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("?", "Error extracting conf from assets", e);
            return e.getMessage();
        }
        return null;
    }
}
package cn.leaf.sftpgo.android.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cn.leaf.sftpgo.android.R;
import cn.leaf.sftpgo.android.databinding.FragmentSettingBinding;

public class SettingFragment extends Fragment {
    private FragmentSettingBinding binding;
    private ActivityResultLauncher<Intent> log_export_dir_picker_launcher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData()!=null) {
                    var tree_uri = result.getData().getData();
                    if (tree_uri != null){
                        try {
                            var log_file_name="sftpgo-log.zip";
                            var target_file= DocumentFile.fromTreeUri(requireContext(),tree_uri).findFile(log_file_name);
                            Uri target_uri=null;
                            if (target_file!=null&&target_file.exists()){
                                target_uri=target_file.getUri();
                            } else {
                                target_uri=DocumentFile.fromTreeUri(requireContext(), tree_uri).createFile("application/zip", log_file_name).getUri();
                            }

                            var out_stream=getContext().getContentResolver().openOutputStream(target_uri, "wt");
                            if (out_stream==null){
                                throw new RuntimeException("无法打开输出流");
                            }
                            var zip_output_stream=new ZipOutputStream(out_stream);
                            byte[] buffer=new byte[8192];
                            int length=0;
                            for (var f:requireActivity().getExternalFilesDir("logs").listFiles()){
                                if (f.isDirectory()){
                                    continue;
                                }
                                zip_output_stream.putNextEntry(new ZipEntry(f.getName()));
                                var fis=new FileInputStream(f);
                                while ((length=fis.read(buffer))!=-1){
                                    zip_output_stream.write(buffer, 0, length);
                                    zip_output_stream.flush();
                                }
                                zip_output_stream.closeEntry();
                            }
                            zip_output_stream.close();
                            Toast.makeText(requireContext(), "保存成功", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(requireContext(), "导出日志失败", Toast.LENGTH_SHORT).show();
                            throw new RuntimeException(e);
                        }
                    } else {
                        Toast.makeText(requireContext(),"未获取到目录URI",Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private ActivityResultLauncher<Intent> db_export_dir_picker_launcher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData()!=null) {
                    var tree_uri = result.getData().getData();
                    if (tree_uri != null){
                        var sftpgo_db_file=new File(requireActivity().getExternalFilesDir("conf"), "sftpgo.db");
                        var target_file= DocumentFile.fromTreeUri(requireContext(),tree_uri).findFile("sftpgo-backup.db");
                        Uri target_uri=null;
                        if (target_file!=null&&target_file.exists()){
                            target_uri=target_file.getUri();
                        } else {
                            target_uri=DocumentFile.fromTreeUri(requireContext(), tree_uri).createFile("application/octet-stream", "sftpgo-backup.db").getUri();
                        }
                        try {
                            var in=new FileInputStream(sftpgo_db_file);
                            var out_stream=getContext().getContentResolver().openOutputStream(target_uri, "wt");
                            if (out_stream==null){
                                throw new RuntimeException("无法打开输出流");
                            }
                            var bo=new BufferedOutputStream(out_stream);
                            byte[] buffer=new byte[8192];
                            int length=0;
                            while ((length=in.read(buffer))>0){
                                bo.write(buffer,0,length);
                                bo.flush();
                            }
                            in.close();
                            bo.close();
                            Toast.makeText(requireContext(), "导出配置成功", Toast.LENGTH_SHORT).show();
                        } catch (IOException e){
                            Toast.makeText(requireContext(), "导出配置失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(),"未获取到目录URI",Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private ActivityResultLauncher<Intent> load_db_picker_launcher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->{
                if (result.getResultCode() == Activity.RESULT_OK && result.getData()!=null) {
                    var file_uri = result.getData().getData();
                    if (file_uri != null){
                        try(var in=getContext().getContentResolver().openInputStream(file_uri)){
                            var buffer=new byte[15];
                            var length=in.read(buffer);
                            if (length!=15){
                                throw new IOException("读文件头长度有误，length="+length);
                            }
                            if (!new String(buffer).equals("SQLite format 3")){
                                throw new IOException("不是sqlite3文件");
                            }
                        } catch (IOException e){
                          Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                          e.printStackTrace();
                        }
                        var sftpgo_db_file=new File(requireActivity().getExternalFilesDir("conf"), "sftpgo.db");
                        if (sftpgo_db_file.exists()){
                            sftpgo_db_file.delete();
                        }
                        try (var in=new BufferedInputStream(getContext().getContentResolver().openInputStream(file_uri))){
                            var buffer=new byte[8192];
                            int length=0;
                            var out=new BufferedOutputStream(new FileOutputStream(sftpgo_db_file));
                            while ((length=in.read(buffer))>0){
                                out.write(buffer,0,length);
                                out.flush();
                            }
                        } catch (IOException e){
                            Toast.makeText(requireContext(), "复制外部db到内部目录失败", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private String[] setting_items={"端口配置", "高级设置", "导出日志", "关于", "电量管理优化"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentSettingBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        var adapter=new ArrayAdapter<>(
                requireContext(),
                R.layout.item_setting,
                R.id.setting_item,
                setting_items
        );
        binding.listViewSettings.setAdapter( adapter);
        binding.listViewSettings.setOnItemClickListener((parent, view1, position, id) -> {
            switch (position){
                case 0:
                    new EditPortFragment().show(requireActivity().getSupportFragmentManager(),"编辑端口");
                    break;
                case 1:
                    var file_uri= FileProvider.getUriForFile(requireContext(),"cn.leaf.sftpgo.fileprovider",new File(requireActivity().getExternalFilesDir("conf"),"sftpgo.json"));
                    var i=new Intent(Intent.ACTION_EDIT);
                    i.setDataAndType(file_uri, "text/plain");
                    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    try {
                        startActivity(i);
                    } catch (ActivityNotFoundException e){
                        e.printStackTrace();
                        Toast.makeText(requireContext(),"外部无可用文本编辑器",Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(requireContext(),"打开外部编辑器出错",Toast.LENGTH_SHORT).show();
                        throw new RuntimeException(e);
                    }
                    break;
                case 2:
                    var log_dir=requireActivity().getExternalFilesDir("logs");
                    if (log_dir==null||log_dir.list().length==0){
                        Toast.makeText(requireContext(), "无日志", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    log_export_dir_picker_launcher.launch(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE));
                    break;
                case 3:
                    new AboutFragment().show(requireActivity().getSupportFragmentManager(),"注: 本APP非SFTPGO官方!!!");
                    break;
                case 4:
                    var packageName=getActivity().getPackageName();
                    Log.i("package name", packageName);
                    if(!((PowerManager)getActivity().getSystemService(Context.POWER_SERVICE)).isIgnoringBatteryOptimizations(packageName)){
                        startActivity(new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).setData(Uri.parse("package:"+packageName)));
                    }
                    else {
                        Toast.makeText(requireContext(), "已设置", Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }
}

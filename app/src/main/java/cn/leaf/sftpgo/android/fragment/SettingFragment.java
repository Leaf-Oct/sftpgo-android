package cn.leaf.sftpgo.android.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;

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
                            var sftpgo_log_file=new File(requireActivity().getExternalFilesDir("logs"), "sftpgo.log");
                            var target_file= DocumentFile.fromTreeUri(requireContext(),tree_uri).findFile("sftpgo.log");
                            Uri target_uri=null;
                            if (target_file!=null&&target_file.exists()){
                                target_uri=target_file.getUri();
                            } else {
                                target_uri=DocumentFile.fromTreeUri(requireContext(), tree_uri).createFile("application/octet-stream", "sftpgo.log").getUri();
                            }
                            var in=new FileInputStream(sftpgo_log_file);
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
    private String[] setting_items={"端口配置", "高级设置", "导出日志", "关于"};

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
//                    var edit_port_fragment=new EditPortFragment();
//                    edit_port_fragment.show(requireActivity().getSupportFragmentManager(),"编辑端口");
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
                    var logFile = new File(requireActivity().getExternalFilesDir("logs"), "sftpgo.log");
                    if (!logFile.exists()) {
                        Toast.makeText(requireContext(), "无日志", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    log_export_dir_picker_launcher.launch(intent);
                    break;
                case 3:
                    new AboutFragment().show(requireActivity().getSupportFragmentManager(),"注: 本APP非SFTPGO官方!!!");
                    break;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }
}

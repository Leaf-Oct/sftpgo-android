package cn.leaf.sftpgo.android.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.io.File;

import cn.leaf.sftpgo.android.R;
import cn.leaf.sftpgo.android.databinding.FragmentSettingBinding;

public class SettingFragment extends Fragment {
    private FragmentSettingBinding binding;

    private String[] setting_items={"端口配置", "高级设置", "关于"};

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
//                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PortFragment()).commit();
                    var edit_port_fragment=new EditPortFragment();
                    edit_port_fragment.show(requireActivity().getSupportFragmentManager(),"编辑端口");
                    break;
                case 1:
//                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SwitchFragment()).commit();
//                    var sftpgo_json_config_file=;
                    var file_uri= FileProvider.getUriForFile(requireContext(),"cn.leaf.sftpgo.fileprovider",new File(requireActivity().getExternalFilesDir("conf"),"sftpgo.json"));
                    var i=new Intent(Intent.ACTION_EDIT);
//                    i.setDataAndType(file_uri,"application/json");
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
//                        e.printStackTrace();
                    }
                    break;
                case 2:
//                    requireActivity().getSupportFragmentManager().beginTransaction().replace()

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

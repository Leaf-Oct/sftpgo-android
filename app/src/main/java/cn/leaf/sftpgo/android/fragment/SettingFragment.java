package cn.leaf.sftpgo.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
                    break;
                case 1:
//                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SwitchFragment()).commit();
                    break;
                case 2:
//                    requireActivity().getSupportFragmentManager().beginTransaction().replace()
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }
}

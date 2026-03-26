package cn.leaf.sftpgo.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import cn.leaf.sftpgo.android.databinding.FragmentSwitchBinding;

public class SwitchFragment extends Fragment {
    private FragmentSwitchBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentSwitchBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.switchMain.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                binding.sftpStatus.setText("SFTPGO已启动");
            }
            else {
                binding.sftpStatus.setText("SFTPGO已停止");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }
}

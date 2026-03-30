package cn.leaf.sftpgo.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import cn.leaf.sftpgo.android.databinding.FragmentSwitchBinding;
import cn.leaf.sftpgo.android.service.SftpgoService;

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
                getActivity().startService(new Intent(getActivity(), SftpgoService.class));
            }
            else {
                getActivity().stopService(new Intent(getActivity(), SftpgoService.class));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }
}

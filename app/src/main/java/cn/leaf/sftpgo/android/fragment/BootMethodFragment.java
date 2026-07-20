package cn.leaf.sftpgo.android.fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import cn.leaf.sftpgo.android.R;
import cn.leaf.sftpgo.android.databinding.FragmentBootMethodBinding;


public class BootMethodFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    private FragmentBootMethodBinding binding;
    private String[] methods=new String[]{"默认", "Shizuku", "Root"};
    private ListView methods_listview;


    public BootMethodFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding=FragmentBootMethodBinding.inflate(getLayoutInflater());
        methods_listview=binding.bootMethod;
        var adapter=new ArrayAdapter<>(getActivity(), R.layout.item_setting, R.id.setting_item, methods);
        methods_listview.setAdapter(adapter);
        return new AlertDialog.Builder(getActivity()).setTitle("启动方式").setView(binding.getRoot()).create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:

                break;
            case 1:

                break;
            case 2:

                break;
        }
    }
}
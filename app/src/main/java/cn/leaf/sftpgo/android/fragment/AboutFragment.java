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

import cn.leaf.sftpgo.android.R;
import cn.leaf.sftpgo.android.databinding.FragmentAboutBinding;

public class AboutFragment extends DialogFragment {

    public AboutFragment() {
        super();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity()).setTitle("注: 本APP非SFTPGO官方!!!").setView(FragmentAboutBinding.inflate(getLayoutInflater()).getRoot()).create();
    }

}
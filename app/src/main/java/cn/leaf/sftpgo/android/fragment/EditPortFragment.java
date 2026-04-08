package cn.leaf.sftpgo.android.fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.leaf.sftpgo.android.databinding.FragmentEditPortBinding;

public class EditPortFragment extends DialogFragment implements View.OnClickListener {

    private FragmentEditPortBinding binding;
    EditText sftp_port, ftp_port, webdav_port, http_port;
    Button sftp_default, ftp_default, webdav_default, http_default, btn_confirm, btn_cancel;

    public EditPortFragment() {
        // Required empty public constructor
        super();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding=FragmentEditPortBinding.inflate(getLayoutInflater());
        sftp_port=binding.sftpPort;
        ftp_port=binding.ftpPort;
        webdav_port=binding.webdavPort;
        http_port=binding.httpPort;
        sftp_default=binding.sftpDefault;
        ftp_default=binding.ftpDefault;
        webdav_default=binding.webdavDefault;
        http_default=binding.httpDefault;
        btn_confirm=binding.editPortConfirm;
        btn_cancel=binding.editPortCancel;
        return new AlertDialog.Builder(getActivity()).setTitle("编辑端口").setView(binding.getRoot()).create();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){


        }
    }
}
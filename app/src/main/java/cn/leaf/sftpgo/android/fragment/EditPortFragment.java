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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import cn.leaf.sftpgo.android.R;
import cn.leaf.sftpgo.android.databinding.FragmentEditPortBinding;

public class EditPortFragment extends DialogFragment implements View.OnClickListener {

    private FragmentEditPortBinding binding;
    EditText sftp_port, ftp_port, webdav_port, http_port;
    Button sftp_default, ftp_default, webdav_default, http_default, btn_confirm, btn_cancel;

    Thread[] read_threads=new Thread[4];
//    boolean[] has_object=new boolean[4];
    String[] ports=new String[4];

    JSONObject config, sftp_binding, ftp_binding, webdav_binding, http_binding;

    public EditPortFragment() {
        super();
        read_threads[0]=new Thread(()->{
            try {
                var sftpd=config.getJSONObject("sftpd");
                sftp_binding=sftpd.getJSONArray("bindings").getJSONObject(0);
                ports[0]=String.valueOf(sftp_binding.getInt("port"));
            } catch (Exception e) {
                ports[0]=null;
                e.printStackTrace();
            }
        });
        read_threads[1]=new Thread(()->{
            try {
                var ftpd=config.getJSONObject("ftpd");
                ftp_binding=ftpd.getJSONArray("bindings").getJSONObject(0);
                ports[1]=String.valueOf(ftp_binding.getInt("port"));
            } catch (Exception e) {
                ports[1]=null;
                e.printStackTrace();
            }
        });
        read_threads[2]=new Thread(()->{
            try {
                var webdavd=config.getJSONObject("webdavd");
                webdav_binding=webdavd.getJSONArray("bindings").getJSONObject(0);
                ports[2]=String.valueOf(webdav_binding.getInt("port"));
            } catch (Exception e) {
                ports[2]=null;
                e.printStackTrace();
            }
        });
        read_threads[3]=new Thread(()->{
            try {
                var httpd=config.getJSONObject("httpd");
                http_binding=httpd.getJSONArray("bindings").getJSONObject(0);
                ports[3]=String.valueOf(http_binding.getInt("port"));
            } catch (Exception e) {
                ports[3]=null;
                e.printStackTrace();
            }
        });
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

        var sftpgo_json=new File(getActivity().getExternalFilesDir("conf"), "sftpgo.json");
        try {
            config=new JSONObject(new String(Files.readAllBytes(sftpgo_json.toPath()), StandardCharsets.UTF_8));
            for(var t:read_threads){
                t.start();
            }
            for (var t:read_threads){
                t.join();
            }
            sftp_port.setText(ports[0]==null?"解析sftp端口出错":ports[0]);
            ftp_port.setText(ports[1]==null?"解析ftp端口出错":ports[1]);
            webdav_port.setText(ports[2]==null?"解析webdav端口出错":ports[2]);
            http_port.setText(ports[3]==null?"解析http端口出错":ports[3]);
        } catch (Exception e) {
            sftp_port.setText("解析sftpgo.json配置文件出错");
            ftp_port.setText("解析sftpgo.json配置文件出错");
            webdav_port.setText("解析sftpgo.json配置文件出错");
            http_port.setText("解析sftpgo.json配置文件出错");
            e.printStackTrace();
        }
        sftp_default.setOnClickListener(this);
        ftp_default.setOnClickListener(this);
        webdav_default.setOnClickListener(this);
        http_default.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);

        return new AlertDialog.Builder(getActivity()).setTitle("编辑端口").setView(binding.getRoot()).create();
    }


    @Override
    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.edit_port_cancel:
//                dismiss();
//                break;
//
//        }
//        啥笔google，AGP8.0以上不允许用switch case来匹配id，因为id不再是常量。所以只能用最笨拙的if else
        if (v.getId()==R.id.edit_port_cancel){
            dismiss();
        }
        else if (v.getId()==R.id.edit_port_confirm){
            var sftp_port_text=sftp_port.getText().toString();
            var ftp_port_text=ftp_port.getText().toString();
            var webdav_port_text=webdav_port.getText().toString();
            var http_port_text=http_port.getText().toString();
            int sftp_port_number=-1, ftp_port_number=-1, webdav_port_number=-1, http_port_number=-1;
//            检验端口是否合法
            try {
                sftp_port_number = Integer.parseInt(sftp_port_text);
                ftp_port_number = Integer.parseInt(ftp_port_text);
                webdav_port_number = Integer.parseInt(webdav_port_text);
                http_port_number = Integer.parseInt(http_port_text);
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "请输入有效的数字", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            }
            try {
                sftp_binding.put("port", sftp_port_number);
                ftp_binding.put("port", ftp_port_number);
                webdav_binding.put("port", webdav_port_number);
                http_binding.put("port", http_port_number);
                var sftpgo_json=new File(getActivity().getExternalFilesDir("conf"), "sftpgo.json");
                Files.write(sftpgo_json.toPath(), config.toString(4).getBytes(StandardCharsets.UTF_8));
                Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "保存失败", Toast.LENGTH_SHORT).show();
            }
            dismiss();
        }
        else if (v.getId()==R.id.sftp_default){
            sftp_port.setText("2022");
        }
        else if (v.getId()==R.id.ftp_default){
            ftp_port.setText("0");
        }
        else if (v.getId()==R.id.webdav_default){
            webdav_port.setText("0");
        }
        else if (v.getId()==R.id.http_default){
            http_port.setText("8080");
        }
    }
}
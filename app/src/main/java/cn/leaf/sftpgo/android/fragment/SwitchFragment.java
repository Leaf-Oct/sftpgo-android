package cn.leaf.sftpgo.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

import javax.net.ssl.HttpsURLConnection;

import cn.leaf.sftpgo.android.databinding.FragmentSwitchBinding;
import cn.leaf.sftpgo.android.service.SftpgoService;

public class SwitchFragment extends Fragment {
    private FragmentSwitchBinding binding;
    private TextView sftpgo_status;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSwitchBinding.inflate(inflater, container, false);
        sftpgo_status = binding.sftpgoStatus;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.switchMain.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                getActivity().startService(new Intent(getActivity(), SftpgoService.class));
//                TODO
            } else {
                getActivity().stopService(new Intent(getActivity(), SftpgoService.class));
                sftpgo_status.setText("当前状态：关闭");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (SftpgoService.isRunning) {
//            TODO
        }

    }

    private void healthCheck() {
        sftpgo_status.setText("检查sftpgo服务状态中");
        new Thread(()->{
            try {
                var http_binding = new JSONObject(new String(Files.readAllBytes(new File(getActivity().getExternalFilesDir("conf"), "sftpgo.json").toPath()), StandardCharsets.UTF_8)).getJSONObject("httpd").getJSONArray("bindings").getJSONObject(0);
                var http_port = http_binding.getInt("port");
                var enable_https = http_binding.getBoolean("enable_https");
                if (enable_https) {
                    var url=new URL("https://127.0.0.1:" + http_port+"/healthz");
                    var https_connection=(HttpsURLConnection)url.openConnection();
                    https_connection.setRequestMethod("GET");
                    https_connection.connect();
                    var response_code = https_connection.getResponseCode();
                    if (response_code == 200) {
                        getActivity().runOnUiThread(()->{

                        });
                    } else {

                    }
                    https_connection.disconnect();
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }
}

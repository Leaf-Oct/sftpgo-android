package cn.leaf.sftpgo.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;

import javax.net.ssl.HttpsURLConnection;

import cn.leaf.sftpgo.android.databinding.FragmentSwitchBinding;
import cn.leaf.sftpgo.android.service.SftpgoService;

public class SwitchFragment extends Fragment {
    private FragmentSwitchBinding binding;
    private TextView sftpgo_status;

//    private ImageButton health_check;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSwitchBinding.inflate(inflater, container, false);
        sftpgo_status = binding.sftpgoStatus;
        binding.healthCheck.setEnabled(false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.switchMain.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                getActivity().startService(new Intent(getActivity(), SftpgoService.class));
                healthCheck(3000);
            } else {
                getActivity().stopService(new Intent(getActivity(), SftpgoService.class));
                sftpgo_status.setText("当前状态：关闭");
                sftpgo_status.setClickable(false);
                sftpgo_status.setOnClickListener(null);
                binding.healthCheck.setEnabled(false);
            }
        });
        binding.healthCheck.setOnClickListener(v -> {
            healthCheck(3000);
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
            healthCheck(0);
        }

    }


    private void healthCheck(long time) {
        sftpgo_status.setText("检查sftpgo服务状态中");
        binding.switchMain.setEnabled(false);
        binding.healthCheck.setEnabled(false);
        new Thread(() -> {
            int response_code = 0;
            String response_message = null;
            var sb = new StringBuilder();
            boolean enable_https = false;
            List<String> ips=new ArrayList<>();
            ips.add("127.0.0.1");
            int http_port = 0;
            try {
                var http_binding = new JSONObject(new String(Files.readAllBytes(new File(getActivity().getExternalFilesDir("conf"), "sftpgo.json").toPath()), StandardCharsets.UTF_8)).getJSONObject("httpd").getJSONArray("bindings").getJSONObject(0);
                http_port = http_binding.getInt("port");
                Log.i("http_port", String.valueOf(http_port));
                enable_https = http_binding.getBoolean("enable_https");
                Thread.sleep(time);
                if (enable_https) {
                    var url = new URL("https://127.0.0.1:" + http_port + "/healthz");
                    var https_connection = (HttpsURLConnection) url.openConnection();
                    https_connection.setRequestMethod("GET");
                    https_connection.connect();
                    response_code = https_connection.getResponseCode();
                    response_message = https_connection.getResponseMessage();
                    https_connection.disconnect();
                } else {
                    var url = new URL("http://127.0.0.1:" + http_port + "/healthz");
                    var http_connection = (HttpURLConnection) url.openConnection();
                    http_connection.setRequestMethod("GET");
                    http_connection.connect();
                    response_code = http_connection.getResponseCode();
                    response_message = http_connection.getResponseMessage();
                    http_connection.disconnect();
                }
                Log.i("health", "Response Code: " + response_code);
                Log.i("health", "Response Message: " + response_message);
                ips = getAllAddress();
                for (String ip : ips) {
                    sb.append(enable_https?"https://":"http://").append(ip).append(":").append(http_port).append("\n");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                int finalResponse_code = response_code;
                String finalResponse_message = response_message;
                boolean finalEnable_https = enable_https;
                List<String> finalIps = ips;
                int finalHttp_port = http_port;
                getActivity().runOnUiThread(() -> {
                    sftpgo_status.setText(((finalResponse_code == 200 && finalResponse_message.equalsIgnoreCase("ok")) ? "运行中" : "异常") + " http" + (finalEnable_https ? "s":"") + "://" + finalIps.get(0) + ":" + finalHttp_port);
                    sftpgo_status.setClickable(true);
                    sftpgo_status.setOnClickListener(v -> {
                        var dialog = new AlertDialog.Builder(getActivity()).setTitle("可用地址").setMessage(sb.toString()).setPositiveButton("ok", null).create();
                        dialog.show();
                    });
                    binding.switchMain.setEnabled(true);
                    binding.healthCheck.setEnabled(true);
                });
                System.gc();
            }
        }).start();
    }

    public ArrayList<String> getAllAddress() {
        var ipv4_address = new ArrayList<String>();
        var ipv6_address = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (true) {
                NetworkInterface networkInterface;
                try {
                    networkInterface = networkInterfaces.nextElement();
                } catch (NoSuchElementException e) {
                    break;
                }
                if (networkInterface == null) {
                    break;
                }
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                try {
                    while (true) {
                        InetAddress inetAddress;
                        try {
                            inetAddress = inetAddresses.nextElement();
                        } catch (NoSuchElementException e) {
                            break;
                        }
                        if (inetAddress == null) {
                            break;
                        }
//                        if (!inetAddress.isLoopbackAddress()) {
                            if (inetAddress instanceof Inet4Address) {
                                ipv4_address.add(inetAddress.getHostAddress());
                            }
                            if (inetAddress instanceof Inet6Address) {
                                ipv6_address.add(inetAddress.getHostAddress());
                            }
//                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (ipv4_address.isEmpty()) {
//            ipv4_address.add("127.0.0.1");
//        }
        ipv4_address.addAll(ipv6_address);
        ipv6_address.clear();
        ipv6_address = null;
        System.gc();
        return ipv4_address;
    }
}

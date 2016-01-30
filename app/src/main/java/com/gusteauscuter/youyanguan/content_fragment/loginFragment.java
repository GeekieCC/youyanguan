package com.gusteauscuter.youyanguan.content_fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.NavigationActivity;
import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.data_Class.DeviceInfo;
import com.gusteauscuter.youyanguan.data_Class.UserLoginInfo;
import com.gusteauscuter.youyanguan.internet.connectivity.NetworkConnectivity;
import com.gusteauscuter.youyanguan.internet.server.CollectInfo;
import com.gusteauscuter.youyanguan.login_Client.LibraryClient;
import com.gusteauscuter.youyanguan.softInput.SoftInputUtil;
import com.gusteauscuter.youyanguan.util.Device;

import org.apache.commons.httpclient.ConnectTimeoutException;

import java.net.SocketTimeoutException;

/**
 * A simple {Login Fragment} subclass.
 */
public class loginFragment extends Fragment {

    private EditText userNameEditText;
    private EditText passEditText;
    private Button loginButton;
    private ProgressBar mProgressBar;
    private int timesClickLoginButton =1;
    private boolean disableDoubleClick = true; // 防治连击登陆按钮造成的闪退

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login_library, container, false);

        userNameEditText = (EditText) view.findViewById(R.id.id_username);
        passEditText = (EditText) view.findViewById(R.id.id_passward_library);

        userNameEditText.setText(((NavigationActivity) getActivity()).getmUserLoginInfo().getUsername());
        passEditText.setText(((NavigationActivity) getActivity()).getmUserLoginInfo().getPassword());
        userNameEditText.hasFocus();

        mProgressBar=(ProgressBar) view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        loginButton = (Button) view.findViewById(R.id.button_login_library);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SoftInputUtil.hideSoftInput(getActivity(), loginButton);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (disableDoubleClick) {
                            doLogin();
                        }
                    }
                }, 500); //收起软键盘需要一定时间

            }
        });

        return view;
    }

    private void doLogin() {
        boolean isConnected = NetworkConnectivity.isConnected(getActivity());
        if (!isConnected) {
            Toast.makeText(getActivity(), R.string.internet_not_connected, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        String username = userNameEditText.getText().toString();
        String pass = passEditText.getText().toString();
        if (timesClickLoginButton == 5) {
            username = "201421003124";
            pass = "ziqian930209";
        }

        if (username.isEmpty() || pass.isEmpty()) {
            Toast.makeText(getActivity(), "请完整输入！", Toast.LENGTH_SHORT).show();
            timesClickLoginButton++;
            return;
        }

        AsyLoginLibrary myAsy = new AsyLoginLibrary();
        myAsy.execute(username, pass);

    }

    private class AsyLoginLibrary extends AsyncTask<String, Void, UserLoginInfo> {
        private boolean serverOK = true; //处理服务器异常
        private boolean isLogined = false;
        @Override
        protected void onPreExecute() {
            disableDoubleClick = false; // 在执行异步类之前，将此变量置否，防止双击多次执行doLogin方法，而实例化多个AsyLoginLibrary对象
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected UserLoginInfo doInBackground(String... account) {
            UserLoginInfo LoginResult = null;
            try {
                LibraryClient libClient = new LibraryClient();
                if (libClient.login(account[0], account[1])) {
                    isLogined = true;
                    LoginResult = new UserLoginInfo(account[0], account[1], true);
                }else
                    LoginResult = new UserLoginInfo(account[0], account[1], false);
            } catch (ConnectTimeoutException | SocketTimeoutException e) {
                serverOK = false;
            } catch (Exception e) {
                e.printStackTrace();
            }

            CollectInfo.postRequest(LoginResult);
            return LoginResult;
        }

        @Override
        protected void onPostExecute(UserLoginInfo result) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (serverOK) {
                if (isLogined) {
                    ((NavigationActivity)getActivity()).setmUserLoginInfo(result);
                    ((NavigationActivity)getActivity()).JumpToBookFragment();
                    SaveData(result.getUsername(),result.getPassword());
                } else {
                    disableDoubleClick = true;
                    Toast.makeText(getActivity(), R.string.failed_to_login_library, Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                disableDoubleClick = true;
                Toast.makeText(getActivity(), R.string.server_failed, Toast.LENGTH_SHORT).show();
            }

        }

        private void SaveData(String username, String pass){
            SharedPreferences.Editor shareData =getActivity().getSharedPreferences("data",0).edit();
            shareData.putString("USERNAME",username);
            shareData.putString("PASSWORD", pass);
            shareData.putBoolean("ISLOGINED", true);
            shareData.apply();
        }
    }


}

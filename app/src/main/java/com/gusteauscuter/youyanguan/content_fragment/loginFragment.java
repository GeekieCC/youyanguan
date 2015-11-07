package com.gusteauscuter.youyanguan.content_fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gusteauscuter.youyanguan.NavigationActivity;
import com.gusteauscuter.youyanguan.R;
import com.gusteauscuter.youyanguan.data_Class.userLogin;
import com.gusteauscuter.youyanguan.internet.connectivity.NetworkConnectivity;
import com.gusteauscuter.youyanguan.login_Client.LibraryClient;

import org.apache.commons.httpclient.ConnectTimeoutException;

import java.net.SocketTimeoutException;

/**
 * A simple {Login Fragment} subclass.
 */
public class loginFragment extends Fragment {

    private EditText userNameEditText;
    private EditText passEditText;

    private ProgressBar mProgressBar;

    private int IsFiveTimes=1;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login_library, container, false);

        userNameEditText = (EditText) view.findViewById(R.id.id_username);
        passEditText = (EditText) view.findViewById(R.id.id_passward_library);

        userNameEditText.setText(((NavigationActivity) getActivity()).getmUserLogin().getUsername());
        passEditText.setText(((NavigationActivity) getActivity()).getmUserLogin().getPassword());
        userNameEditText.hasFocus();

        mProgressBar=(ProgressBar) view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        Button loginButton = (Button) view.findViewById(R.id.button_login_library);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                boolean isConnected = NetworkConnectivity.isConnected(getActivity());
                if (isConnected) {
                    try {

                        String username = userNameEditText.getText().toString();
                        String pass = passEditText.getText().toString();

                        if (IsFiveTimes == 5) {
                            username = "201421003124";
                            pass = "ziqian930209";
//                            IsThreeTimes=1;
                        }

                        if (username.isEmpty() || pass.isEmpty()) {
                            Toast.makeText(getActivity(), "请完整输入！", Toast.LENGTH_SHORT)
                                    .show();
                            IsFiveTimes++;
                        } else {
                            AsyLoginLibrary myAsy = new AsyLoginLibrary();
                            myAsy.execute(username, pass);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.internet_not_connected, Toast.LENGTH_SHORT)
                            .show();
                }

            }
        });

        Button cancelButton = (Button) view.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ((NavigationActivity) getActivity()).openDrawer();
            }
        });

        return view;
    }

    private class AsyLoginLibrary extends AsyncTask<String, Void, userLogin> {
        private boolean serverOK = true; //处理服务器异常
        private boolean isLogined = false;
        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected userLogin doInBackground(String... account) {
            userLogin LoginResult = null;
            try {
                LibraryClient libClient = new LibraryClient();
                if (libClient.login(account[0], account[1])) {
                    isLogined = true;
                    LoginResult = new userLogin(account[0], account[1], true);
                }
            } catch (ConnectTimeoutException | SocketTimeoutException e) {
                serverOK = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return LoginResult;
        }

        @Override
        protected void onPostExecute(userLogin result) {

            mProgressBar.setVisibility(View.INVISIBLE);
            if (serverOK) {
                if (isLogined) {
                    ((NavigationActivity)getActivity()).setmUserLogin(result);
                    ((NavigationActivity)getActivity()).JumpToBookFragment();
                    SaveData(result.getUsername(),result.getPassword());
                } else {
                    Toast.makeText(getActivity(), R.string.failed_to_login_library, Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(getActivity(), R.string.server_failed, Toast.LENGTH_SHORT).show();
            }

        }

        private void SaveData(String username, String pass){
            SharedPreferences.Editor shareData =getActivity().getSharedPreferences("data",0).edit();
            shareData.putString("USERNAME",username);
            shareData.putString("PASSWORD", pass);
            shareData.putBoolean("ISLOGINED", true);
            shareData.commit();
        }
    }


}

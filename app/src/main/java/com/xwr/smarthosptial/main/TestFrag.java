package com.xwr.smarthosptial.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xwr.mulkeyboard.KeyboardServer;
import com.xwr.mulkeyboard.UKeyDevice;
import com.xwr.mulkeyboard.UKeyUtil;
import com.xwr.smarthosptial.R;
import com.xwr.smarthosptial.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Create by xwr on 2020/5/7
 * Describe:
 */
public class TestFrag extends BaseFragment {
  KeyboardServer mKeyboardServer;
  Unbinder unbinder;
  private boolean runing = false;
  @SuppressLint("HandlerLeak")
  Handler mHandler = new Handler();

  @Override
  public int getContentLayoutId() {
    return R.layout.frag_password;
  }

  @Override
  protected void initView() {

  }

  @Override
  protected void initData() {
    try {
      UKeyUtil.getInstance(getContext()).initUsbData();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    mKeyboardServer = new KeyboardServer();
    if (UKeyDevice.mDeviceConnection != null) {
      mKeyboardServer.key_init();
    } else {
      Toast.makeText(getContext(), "请获取权限", Toast.LENGTH_LONG).show();
    }
  }

  private void initScanData() {
    System.out.println("--->>>open scan:" + Thread.currentThread().getName());
    String result = mKeyboardServer.read_scan();
    if (result != null) {
      System.out.println(result);
    }
  }


  @Override
  public void onResume() {
    super.onResume();
    initScanData();

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // TODO: inflate a fragment view
    View rootView = super.onCreateView(inflater, container, savedInstanceState);
    unbinder = ButterKnife.bind(this, rootView);
    return rootView;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();

  }
}

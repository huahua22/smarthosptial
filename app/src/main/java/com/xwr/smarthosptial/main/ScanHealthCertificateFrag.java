package com.xwr.smarthosptial.main;

import android.util.Log;

import com.google.gson.Gson;
import com.xwr.mulkeyboard.KeyboardServer;
import com.xwr.mulkeyboard.UKeyDevice;
import com.xwr.mulkeyboard.UKeyUtil;
import com.xwr.smarthosptial.R;
import com.xwr.smarthosptial.base.BaseFragment;
import com.xwr.smarthosptial.comm.Session;
import com.xwr.smarthosptial.util.UiUtil;
import com.zhangke.websocket.WebSocketHandler;

/**
 * Create by xwr on 2020/4/2
 * Describe:
 */
public class ScanHealthCertificateFrag extends BaseFragment {
  KeyboardServer mKeyboardServer;
  boolean scaning = false;


  private void scan() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (scaning) {
          String result = mKeyboardServer.read_scan();
          if (result != null) {
            Log.d("scan", result);
            scaning = false;
            sendCodeData(result);
          } else {
            Log.d("scan", "null");
          }
        }
      }
    }).start();
  }

  private void sendCodeData(String result) {
    Session.mSocketResult.getRecipientData().setResult(result);
    String mrecipient = Session.mSocketResult.getRecipientData().getSender();
    Session.mSocketResult.getRecipientData().setSender(Session.mSocketResult.getRecipientData().getRecipient());
    Session.mSocketResult.getRecipientData().setRecipient(mrecipient);
    Session.mSocketResult.getRecipientData().setSuccess(true);
    WebSocketHandler.getDefault().send(new Gson().toJson(Session.mSocketResult));
  }

  @Override
  public int getContentLayoutId() {
    return R.layout.frag_scan_health_certificate;
  }

  @Override
  protected void initView() {

  }


  @Override
  protected void initData() {
    super.initData();
    boolean isConnect = true;
    while (isConnect) {
      if (UKeyDevice.mDeviceConnection == null) {
        try {
          UKeyUtil.getInstance(getActivity()).initUsbData();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      if (UKeyDevice.mDeviceConnection != null) {
        mKeyboardServer = new KeyboardServer();
        mKeyboardServer.key_init();
        isConnect = false;
      }
    }

    int ret = mKeyboardServer.key_read_init();
    if (ret == 0) {
      scaning = true;
      scan();
    } else {
      UiUtil.showToast(getContext(), "未检测到usb密码键盘");
    }

  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    scaning = false;
  }
}

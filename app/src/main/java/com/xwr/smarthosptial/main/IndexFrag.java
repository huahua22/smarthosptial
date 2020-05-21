package com.xwr.smarthosptial.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.xwr.mulkeyboard.HexUtil;
import com.xwr.mulkeyboard.usbapi.UDevice;
import com.xwr.mulkeyboard.usbapi.UsbApi;
import com.xwr.mulkeyboard.utils.UsbUtil;
import com.xwr.smarthosptial.R;
import com.xwr.smarthosptial.base.BaseFragment;
import com.xwr.smarthosptial.bean.CardBean;
import com.xwr.smarthosptial.comm.Session;
import com.zhangke.websocket.WebSocketHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;

import static com.xwr.smarthosptial.util.UiUtil.println;


/**
 * Create by xwr on 2020/4/2
 * Describe:首页和刷身份证
 */
public class IndexFrag extends BaseFragment {
  private Handler mainHandler;
  private boolean mRunning = false;
  @SuppressLint("HandlerLeak")
  Handler MyHandler = new Handler() {
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case 0:
          println("读取失败，请重新操作");
          break;
        case 1:
          if (UDevice.mDeviceConnection == null) {
            try {
              UsbUtil.getInstance(getContext()).initUsbData(0xffff, 0xffff);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
          if (UDevice.mDeviceConnection != null) {
            mRunning = true;
            initReadData();
          }
          break;
        default:
          break;
      }
    }
  };

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    EventBus.getDefault().register(this);
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override
  public int getContentLayoutId() {
    return R.layout.frag_index;
  }

  @Override
  protected void initView() {

  }

  @Override
  protected void initData() {
    super.initData();
  }

  @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
  public void onMessageEvent(String data) {
    if ("011".equals(data)) {
      MyHandler.sendEmptyMessage(1);
      println("011:start");
    }
  }

  private void initReadData() {
    HandlerThread scanThread = new HandlerThread("readIcThread");
    scanThread.start();
    mainHandler = new Handler(scanThread.getLooper());
    mainHandler.post(mBackgroundRunnable);//将线程post到handler中
  }

  //实现读卡耗时操作
  Runnable mBackgroundRunnable = new Runnable() {
    @Override
    public void run() {
      println(Thread.currentThread().getName());
      while (mRunning) {
        long ret;
        ret = UsbApi.Reader_Init(UDevice.mDeviceConnection, UDevice.usbEpIn, UDevice.usbEpOut);
        println("read init=" + ret);
        byte[] cardInfo = new byte[1300];
        ret = UsbApi.Syn_Get_Card(cardInfo);
        println("get card=" + ret);
        println("data=" + HexUtil.bytesToHexString(cardInfo, 1300));
        if (ret == 0) {
          byte[] name = new byte[30];
          byte[] idnum = new byte[36];
          System.arraycopy(cardInfo, 0, name, 0, 30);
          System.arraycopy(cardInfo, 122, idnum, 0, 36);
          String cardName = null;
          String cardId = null;
          try {
            cardName = getString(name).trim();
            cardId = getString(idnum);
          } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
          }
          CardBean cardBean = new CardBean();
          if ("".equals(name) || null == name) {
            cardBean.setName("张玲");
          } else {
            cardBean.setName(cardName);
          }
          cardBean.setCardNum(cardId);
          Session.mSocketResult.getRecipientData().setResult(cardBean);
          String mrecipient = Session.mSocketResult.getRecipientData().getSender();
          Session.mSocketResult.getRecipientData().setSender(Session.mSocketResult.getRecipientData().getRecipient());
          Session.mSocketResult.getRecipientData().setRecipient(mrecipient);
          Session.mSocketResult.getRecipientData().setSuccess(true);
          WebSocketHandler.getDefault().send(new Gson().toJson(Session.mSocketResult));
          mRunning = false;
        } else {
          MyHandler.sendEmptyMessage(0);
          continue;
        }
      }
    }
  };

  @Override
  public void onResume() {
    super.onResume();
    mRunning = false;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mRunning = false;
    if (UDevice.mDeviceConnection != null) {
      UDevice.mDeviceConnection.close();
      UDevice.mDeviceConnection = null;
      println("index read card device close");
    }
    if (mainHandler != null) {
      mainHandler.removeCallbacks(mBackgroundRunnable);
    }
    EventBus.getDefault().unregister(this);
  }

  private String getString(byte[] bytes) throws UnsupportedEncodingException {
    if (bytes != null && bytes.length > 0) {

      return new String(bytes, "UTF-16LE");
    }
    return "";
  }
}

package com.xwr.smarthosptial.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.xwr.smarthosptial.R;
import com.xwr.smarthosptial.base.FragmentFactory;
import com.xwr.smarthosptial.bean.RecipientBean;
import com.xwr.smarthosptial.bean.SocketResult;
import com.xwr.smarthosptial.comm.FragmentParms;
import com.xwr.smarthosptial.comm.Session;
import com.xwr.smarthosptial.inter.ChangeFragment;
import com.xwr.smarthosptial.util.UiUtil;
import com.zhangke.websocket.SimpleListener;
import com.zhangke.websocket.SocketListener;
import com.zhangke.websocket.WebSocketHandler;
import com.zhangke.websocket.response.ErrorResponse;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

public class MainActivity extends AppCompatActivity implements ChangeFragment {

  @BindView(R.id.fl_content)
  FrameLayout mFlContent;
  private static final String TAG = "xwr";
  private Context mContext;
  private SocketResult result;
  boolean setConnect = true;
  private ConnectThread mConnectThread;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_main);
    initView();
    if (savedInstanceState == null) {
      commitFrag(0);
      EventBus.getDefault().postSticky("000");
    }

  }


  private void initView() {
    mContext = this;
    FragmentParms.setFragmentSelected(this);
    mConnectThread = new ConnectThread();
    mConnectThread.start();
  }

  public class ConnectThread extends Thread {

    @Override
    public void run() {
      while (setConnect) {
        if (WebSocketHandler.getDefault().isConnect()) {
          if (socketListener != null) {
            WebSocketHandler.getDefault().addListener(socketListener);
            setConnect = false;
            Log.d(TAG, "建立websocket监听");
          }
        }
      }
    }
  }

  public void commitFrag(int position) {
    getSupportFragmentManager().beginTransaction()
      .replace(R.id.fl_content, FragmentFactory.getNewFrag(position))
      .commit();
  }

  private SocketListener socketListener = new SimpleListener() {
    @Override
    public void onConnected() {
      Log.d(TAG, "onConnected");
    }

    @Override
    public void onConnectFailed(Throwable e) {
      if (e != null) {
        UiUtil.showToast(mContext, "onConnectFailed:" + e.toString());
      } else {
        UiUtil.showToast(mContext, "onConnectFailed:null");
      }
    }

    @Override
    public void onDisconnect() {
      UiUtil.showToast(MainActivity.this, "onDisconnect");
    }

    @Override
    public void onSendDataError(ErrorResponse errorResponse) {
      UiUtil.showToast(mContext, "onSendDataError:" + errorResponse.toString());
      errorResponse.release();
    }

    @Override
    public <T> void onMessage(String message, T data) {
      Log.d(TAG, "onMessage(string, T):" + message);
      result = new Gson().fromJson(message, SocketResult.class);
      Session.mSocketResult = null;
      Session.mSocketResult = result;
      if (result.getMsgType().equals("instruction")) {
        RecipientBean recipientData = result.getRecipientData();
        if ("000".equals(recipientData.getRecipientNo())) {//初始化
          commitFrag(0);
          EventBus.getDefault().postSticky("000");
        } else if ("011".equals(recipientData.getRecipientNo())) {//身份证
          commitFrag(0);
          EventBus.getDefault().postSticky("011");
        } else if ("012".equals(recipientData.getRecipientNo())) {//医保卡
          commitFrag(8);
        } else if ("013".equals(recipientData.getRecipientNo())) {//银联
          UiUtil.showToast(mContext, "请刷银联卡");
        } else if ("014".equals(recipientData.getRecipientNo())) {//门诊卡
          UiUtil.showToast(mContext, "请刷门诊卡");
          //          commitFrag(13);
        } else if ("021".equals(recipientData.getRecipientNo())) {//医保电子凭证
          commitFrag(2);
        } else if ("022".equals(recipientData.getRecipientNo())) {//支付宝扫码
          commitFrag(3);
        } else if ("023".equals(recipientData.getRecipientNo())) {//微信扫码
          commitFrag(3);
        } else if ("031".equals(recipientData.getRecipientNo())) {//人脸采集
          commitFrag(12);
          EventBus.getDefault().postSticky("031");
        } else if ("032".equals(recipientData.getRecipientNo())) {//人脸校验
          commitFrag(12);
          EventBus.getDefault().postSticky("032");
        } else if ("041".equals(recipientData.getRecipientNo())) {//输密
          commitFrag(6);
//          EventBus.getDefault().postSticky("041");
        } else if ("051".equals(recipientData.getRecipientNo())) {//结算页面显示
          commitFrag(14);
        } else if ("052".equals(recipientData.getRecipientNo())) {//预结算页面显示
          commitFrag(1);
        } else if ("053".equals(recipientData.getRecipientNo())) {//自费
          commitFrag(10);
        } else if ("054".equals(recipientData.getRecipientNo())) {//未申领电子医保凭证
          commitFrag(5);
        } else if ("055".equals(recipientData.getRecipientNo())) {//结算成功
          commitFrag(9);
          EventBus.getDefault().postSticky(recipientData.getRecipientNo());
        } else if ("056".equals(recipientData.getRecipientNo())) {//结算失败
          commitFrag(9);
          EventBus.getDefault().postSticky(recipientData.getRecipientNo());
        }
      }
    }
  };


  @Override
  protected void onDestroy() {
    super.onDestroy();
    WebSocketHandler.getDefault().removeListener(socketListener);
//    WebSocketHandler.getDefault().destroy();
  }

  @Override
  public void change(int position) {
    commitFrag(position);
  }
}


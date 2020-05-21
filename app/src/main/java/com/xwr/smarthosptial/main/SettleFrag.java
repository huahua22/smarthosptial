package com.xwr.smarthosptial.main;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xwr.smarthosptial.R;
import com.xwr.smarthosptial.base.BaseFragment;
import com.xwr.smarthosptial.bean.IncidentalBean;
import com.xwr.smarthosptial.bean.RecipientBean;
import com.xwr.smarthosptial.comm.FragmentParms;
import com.xwr.smarthosptial.comm.Session;
import com.xwr.smarthosptial.util.QRCodeUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Create by xwr on 2020/4/21
 * Describe:
 */
public class SettleFrag extends BaseFragment {
  @BindView(R.id.tv_name)
  TextView mTvName;
  @BindView(R.id.tv_total_money)
  TextView mTvTotalMoney;
  Unbinder unbinder;
  @BindView(R.id.tv_money)
  TextView mTvMoney;
  @BindView(R.id.tv_timer)
  TextView mTvTimer;
  @BindView(R.id.iv_code)
  ImageView mIvCode;
  private CountDownTimer timer;
  RecipientBean mRecipientBean = null;

  @Override
  public int getContentLayoutId() {
    return R.layout.frag_settle;
  }

  @Override
  protected void initView() {

  }

  @Override
  protected void initData() {
    super.initData();
    timer = new CountDownTimer(50 * 1000, 1000) {
      @Override
      public void onTick(long millisUntilFinished) {
        // TODO Auto-generated method stub
        mTvTimer.setText(millisUntilFinished / 1000 + "s");
      }

      @Override
      public void onFinish() {
        FragmentParms.sChangeFragment.change(0);
        EventBus.getDefault().postSticky("000");
      }
    }.start();
    if (Session.mSocketResult != null) {
      mRecipientBean = Session.mSocketResult.getRecipientData();
      if (mRecipientBean != null) {
        IncidentalBean incidentalBean = mRecipientBean.getIncidentalData();
        mTvName.setText(incidentalBean.getName());
        mTvTotalMoney.setText(incidentalBean.getTotalMoney());
        mTvMoney.setText(incidentalBean.getTotalMoney());
      }
    }
    //生成的二维码图片
    Bitmap qr = QRCodeUtil.createQRImage("hello world", 140, 140, null);
    mIvCode.setImageBitmap(qr);
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
    timer.cancel();
    System.out.println("--->>>settleFrag timer cancel");
  }
}

package com.xwr.smarthosptial.main;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xwr.smarthosptial.R;
import com.xwr.smarthosptial.base.BaseFragment;
import com.xwr.smarthosptial.comm.FragmentParms;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.xwr.smarthosptial.util.UiUtil.println;


/**
 * Create by xwr on 2020/4/2
 * Describe:
 */
public class NotClaimedFrag extends BaseFragment {
  CountDownTimer timer;
  @BindView(R.id.tv_timer)
  TextView mTvTimer;
  Unbinder unbinder;

  @Override
  public int getContentLayoutId() {
    return R.layout.frag_not_claimed;
  }

  @Override
  protected void initView() {
    timer = new CountDownTimer(30 * 1000, 1000) {
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
    println("notClaimedFrag timer cancel");
  }

}

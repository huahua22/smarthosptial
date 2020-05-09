package com.xwr.smarthosptial.base;

import com.xwr.smarthosptial.main.CardFrag;
import com.xwr.smarthosptial.main.FaceCollectFrag;
import com.xwr.smarthosptial.main.FaceRecogFrag;
import com.xwr.smarthosptial.main.IccFrag;
import com.xwr.smarthosptial.main.IdCardFrag;
import com.xwr.smarthosptial.main.IndexFrag;
import com.xwr.smarthosptial.main.NotClaimedFrag;
import com.xwr.smarthosptial.main.OwnExpenseFrag;
import com.xwr.smarthosptial.main.PasswordFrag;
import com.xwr.smarthosptial.main.ScanHealthCertificateFrag;
import com.xwr.smarthosptial.main.ScanPayBarcodeFrag;
import com.xwr.smarthosptial.main.SettleFrag;
import com.xwr.smarthosptial.main.SettleInfoFrag;
import com.xwr.smarthosptial.main.SettleResultFrag;
import com.xwr.smarthosptial.main.TestFrag;
import com.xwr.smarthosptial.main.UnionPaycardsFrag;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Create by xwr on 2020/4/2
 * Describe:
 */
public class FragmentFactory {
  private static ConcurrentHashMap<Integer, BaseFragment> fragMap = new ConcurrentHashMap<>();

  public static BaseFragment getNewFrag(int position) {
    BaseFragment fragment = fragMap.get(position);
    if (fragment == null) {
      switch (position) {
        case 0://首页
          fragment = new IndexFrag();
          break;
        case 1://结算信息
          fragment = new SettleInfoFrag();
          break;
        case 2://扫描医保凭证
          fragment = new ScanHealthCertificateFrag();
          break;
        case 3://扫描付款码
          fragment = new ScanPayBarcodeFrag();
          break;
        case 4://人脸采集
          fragment = new FaceCollectFrag();
          break;
        case 5://未申领
          fragment = new NotClaimedFrag();
          break;
        case 6://输密
          fragment = new PasswordFrag();
          break;
        case 7:
          fragment = new IdCardFrag();
          break;
        case 8:
          fragment = new IccFrag();
          break;
        case 9://结算结果
          fragment = new SettleResultFrag();
          break;
        case 10://自费
          fragment = new OwnExpenseFrag();
          break;
        case 11://人脸采集
          fragment = new UnionPaycardsFrag();
          break;
        case 12://人脸识别
          fragment = new FaceRecogFrag();
          break;
        case 13://银联卡
          fragment = new CardFrag();
          break;
        case 14:
          fragment = new SettleFrag();
          break;
        case 15://测试
          fragment = new TestFrag();
          break;
      }
      assert fragment != null;
      fragMap.put(position, fragment);
    }
    return fragment;
  }
}

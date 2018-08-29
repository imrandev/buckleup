package com.codzunk.buckleup.advert;

import com.codzunk.buckleup.constant.Config;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class CustomAdListener extends AdListener implements RewardedVideoAdListener {

    private RewardedVideoAdListener adListener;

    CustomAdListener() {
        //default constructor
    }

    void loadRewardedVideoAd(RewardedVideoAd mRewardedVideoAd) {
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        if (!mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.loadAd(Config.REWARD_AD_ID, new AdRequest.Builder().build());
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        this.onAdLoaded();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        this.onAdOpened();
    }

    @Override
    public void onRewardedVideoStarted() {
        adListener.onRewardedVideoStarted();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        this.onAdClosed();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        adListener.onRewarded(rewardItem);
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        this.onAdLeftApplication();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        this.onAdFailedToLoad(i);
    }

    @Override
    public void onRewardedVideoCompleted() {
        adListener.onRewardedVideoCompleted();
    }

    interface RewardedVideoAdListener {
        void onRewarded(RewardItem rewardItem);
        void onRewardedVideoStarted();
        void onRewardedVideoCompleted();
    }
}

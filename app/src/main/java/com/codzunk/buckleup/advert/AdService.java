package com.codzunk.buckleup.advert;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codzunk.buckleup.constant.Config;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;

import static com.codzunk.buckleup.advert.CustomAdListener.*;

public class AdService extends CustomAdListener implements
        UnifiedNativeAd.OnUnifiedNativeAdLoadedListener, RewardedVideoAdListener {

    private AdView mAdView;
    private Context context;
    private InterstitialAd interstitialAd;
    private AdLoader adLoader;
    private int ADs_TYPE;
    private RewardedVideoAd rewardedVideoAd;

    public AdService(Context context) {
        this.context = context;
        MobileAds.initialize(context, Config.ADMOB_APP_ID);
    }

    public void initBanner(AdView adView){
        this.mAdView = adView;
        this.ADs_TYPE = 1;
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(this);
    }

    public void initInterstitialAd(InterstitialAd interstitialAd){
        this.interstitialAd = interstitialAd;
        this.ADs_TYPE = 2;
        interstitialAd.setAdUnitId(Config.ADMOB_INTERESTITIAL_ID);
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(this);
    }

    public void initNativeAds(){
        this.ADs_TYPE = 3;
        adLoader = new AdLoader.Builder(context, Config.NATIVE_ADS_ID)
                .forUnifiedNativeAd(this)
                .withAdListener(this)
                .withNativeAdOptions(new NativeAdOptions.Builder().build())
                .build();
    }

    public void initRewardedAdsAds(RewardedVideoAd rewardedVideoAd){
        this.ADs_TYPE = 4;
        this.rewardedVideoAd = rewardedVideoAd;
        loadRewardedVideoAd(rewardedVideoAd);
    }

    @Override
    public void onAdLoaded() {
        switch (ADs_TYPE){
            case 1:
                mAdView.setVisibility(View.VISIBLE);
                break;
            case 2:
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
                break;
            case 3:
                break;
            case 4:
                Toast.makeText(context, "onAdLoaded", Toast.LENGTH_SHORT).show();
                if (rewardedVideoAd.isLoaded()) {
                    rewardedVideoAd.show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onAdFailedToLoad(int errorCode) {
        switch (ADs_TYPE){
            case 1:
                mAdView.setVisibility(View.GONE);
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                Toast.makeText(context, "onAdFailedToLoad", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        Log.d("AdsError", getErrorReason(errorCode));
    }

    @Override
    public void onAdClosed() {
        switch (ADs_TYPE){
            case 1:
                mAdView.setVisibility(View.GONE);
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                Toast.makeText(context, "onAdClosed", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
        if (adLoader.isLoading()) {
            //do work
        } else {
            //do work
        }
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Toast.makeText(context, "rewardItem", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
        Toast.makeText(context, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoCompleted() {
        Toast.makeText(context, "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show();
    }

    private String getErrorReason(int errorCode) {

        String errorReason = "";
        switch (errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                errorReason = "Internal Error";
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                errorReason = "Invalid Request";
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                errorReason = "Network Error";
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                errorReason = "No Fill";
                break;
        }
        return errorReason;
    }
}

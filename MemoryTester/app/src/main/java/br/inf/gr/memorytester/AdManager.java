package br.inf.gr.memorytester;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.Arrays;

public class AdManager {

    private static final String AD_UNIT_ID = "ca-app-pub-9194227743591310/6382045368"; // Substitua pelo seu Ad Unit ID real
    private static final String TAG = "AdManager";

    private RewardedAd rewardedAd;
    private boolean isLoading;
    private final Context context;
    private final AdListener adListener;
    private boolean isRewarded = false;

    public AdManager(Context context, AdListener adListener) {
        this.context = context;
        this.adListener = adListener;

        // Configurar IDs de dispositivos de teste
        RequestConfiguration configuration = new RequestConfiguration.Builder()
                .setTestDeviceIds(Arrays.asList("ABCDEF012345")) // Substitua pelo ID de teste do seu dispositivo
                .build();
        MobileAds.setRequestConfiguration(configuration);

        // Inicializar o SDK do Mobile Ads
        MobileAds.initialize(context, initializationStatus -> {
            Log.d(TAG, "Mobile Ads SDK initialized.");
            loadRewardedAd();
        });
    }

    public void loadRewardedAd() {
        if (rewardedAd == null && !isLoading) {
            isLoading = true; // Marque que o carregamento está em andamento
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(context, AD_UNIT_ID, adRequest, new RewardedAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Log.d(TAG, "Ad failed to load: " + loadAdError.getMessage());
                    rewardedAd = null;
                    isLoading = false;
                    adListener.onAdFailedToLoad(loadAdError); // Notifique o listener do erro
                }

                @Override
                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                    AdManager.this.rewardedAd = rewardedAd;
                    Log.d(TAG, "Ad loaded successfully.");
                    isLoading = false;
                    adListener.onAdLoaded(); // Notifique que o anúncio foi carregado
                }
            });
        }
    }

    public void showRewardedVideo(Activity activity) {
        if (rewardedAd == null) {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
            Toast.makeText(context, "O anúncio ainda está carregando. Tente novamente em breve.", Toast.LENGTH_SHORT).show();
            adListener.onAdNotLoaded();
            loadRewardedAd(); // Tente recarregar o anúncio
            return;
        }

        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdShowedFullScreenContent() {
                Log.d(TAG, "onAdShowedFullScreenContent");
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                Log.d(TAG, "onAdFailedToShowFullScreenContent: " + adError.getMessage());
                rewardedAd = null;
                adListener.onAdFailedToShow(); // Notifique o listener que falhou ao exibir
                loadRewardedAd(); // Recarregar o próximo anúncio
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                Log.d(TAG, "onAdDismissedFullScreenContent");
                rewardedAd = null;
                adListener.onAdDismissed(); // Notifique que o anúncio foi descartado
                loadRewardedAd(); // Carregar o próximo anúncio
            }
        });

        rewardedAd.show(activity, new OnUserEarnedRewardListener() {
            @Override
            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                Log.d(TAG, "The user earned the reward.");
                isRewarded = true;
                adListener.onUserEarnedReward(true); // Notifique o listener que o usuário foi recompensado
            }
        });
    }

    public boolean isAdLoaded() {
        return rewardedAd != null;
    }

    public boolean isUserRewarded() {
        return isRewarded;
    }

    public interface AdListener {
        void onAdLoaded();
        void onAdFailedToLoad(LoadAdError loadAdError);
        void onUserEarnedReward(boolean rewarded);
        void onAdNotLoaded();
        void onAdFailedToShow();
        void onAdDismissed();
    }
}

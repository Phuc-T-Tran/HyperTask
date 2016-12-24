package com.phuctran.hypertask;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.example.games.basegameutils.GameHelper;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import google.AdController;
import google.PlayServices;

public class AndroidLauncher extends AndroidApplication implements AdController, PlayServices {
	private GameHelper gameHelper;
	private final static int requestCode = 1;
	
	// Ads
	private static final String INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-6457271090326629/9228603795";
	private InterstitialAd interstitialAd;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;
		
		setupPlayServices();
		setupAds();
	
		initialize(new HyperTask(this, this), config);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		gameHelper.onStart(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		gameHelper.onStop();
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gameHelper.onActivityResult(requestCode, resultCode, data);
        
        // check for "inconsistent state"
        if ( resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED && requestCode == this.requestCode )  {  
           // force a disconnect to sync up state, ensuring that mClient reports "not connected"
           gameHelper.disconnect();
        }
    }
	
	
	private void setupPlayServices() {
		gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
		gameHelper.enableDebugLog(false);
		 
		GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener()
		{
		    @Override
		    public void onSignInFailed(){ }
		 
		    @Override
		    public void onSignInSucceeded(){ }
		};
		 
		gameHelper.setup(gameHelperListener);
	}
	
	private void setupAds() {
		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId(INTERSTITIAL_AD_UNIT_ID);
		loadNewAd();
	}
	
	private void loadNewAd() {
		AdRequest ad = new AdRequest.Builder().build();
		interstitialAd.loadAd(ad);
	}

	@Override
	public void showInterstitialAd(final Runnable then) {
		runOnUiThread(new Runnable() {
	           @Override
	           public void run() {
	               if (then != null) {
	                   interstitialAd.setAdListener(new AdListener() {
	                       @Override
	                       public void onAdClosed() {
	                           Gdx.app.postRunnable(then);
	                           loadNewAd();
	                       }
	                   });
	               }
	               interstitialAd.show();
	           }
	       });
	}

	@Override
	public boolean isWifiConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		return (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected());
	}

	@Override
	public void signIn() {
	    try {
	        runOnUiThread(new Runnable() {
	            @Override
	            public void run() {
	                gameHelper.beginUserInitiatedSignIn();
	            }
	        });
	    }
	    catch (Exception e) {
	         Gdx.app.log("MainActivity", "Log in failed: " + e.getMessage() + ".");
	    }
	}
	 
	@Override
	public void signOut() {
	    try {
	        runOnUiThread(new Runnable() {
	            @Override
	            public void run() {
	                gameHelper.signOut();
	            }
	        });
	    }
	    catch (Exception e) {
	        Gdx.app.log("MainActivity", "Log out failed: " + e.getMessage() + ".");
	    }
	}
	 
	@Override
	public void rateGame() {
	    String str = "Your PlayStore Link";
	    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
	}
	 
	@Override
	public void unlockAchievement(String achievementID) {
		if (isSignedIn() == true) {
		    Games.Achievements.unlock(gameHelper.getApiClient(), achievementID);
		    Games.Achievements.reveal(gameHelper.getApiClient(), achievementID);
		}
	}
	 
	@Override
	public void submitScore(int score) {
	    if (isSignedIn() == true) {
	        Games.Leaderboards.submitScore(gameHelper.getApiClient(), getString(R.string.board_classic), score);
	    }
	}
	 
	@Override
	public void showAchievement() {
	    if (isSignedIn() == true)
	        startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()), requestCode);
	    else
	        signIn();
	}
	 
	@Override
	public void showLeaderboard(){
	    if (isSignedIn() == true)
	        startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(), getString(R.string.board_classic)), requestCode);
	    else
	        signIn();
	}
	 
	@Override
	public boolean isSignedIn()  {
	    return gameHelper.isSignedIn();
	}
}

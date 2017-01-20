package com.example.android.sunshine;

import android.os.Bundle;
import android.util.Log;

import com.example.android.sunshine.utilities.NotificationUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.concurrent.TimeUnit;

public class WeatherWatchFaceListenerService extends WearableListenerService
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "WearableListenerService";

    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo(BuildConfig.PATH_MOBILE_DATA_CHANGED) == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    String updateWeather = dataMap.getString(BuildConfig.UPDATE_WEATHER);
                    if (updateWeather != null) {

                        if (mGoogleApiClient == null) {
                            mGoogleApiClient = new GoogleApiClient.Builder(this)
                                    .addConnectionCallbacks(this)
                                    .addOnConnectionFailedListener(this)
                                    .addApi(Wearable.API).build();
                        }
                        if (!mGoogleApiClient.isConnected()) {
                            ConnectionResult connectionResult =
                                    mGoogleApiClient.blockingConnect(30, TimeUnit.SECONDS);

                            if (!connectionResult.isSuccess()) {
                                Log.e(TAG, "Failed to connect to GoogleApiClient.");
                                return;
                            }
                        }
                        NotificationUtils.notifyWearOfNewWeather(this, mGoogleApiClient);
                    }
                }
            }
        }
    }


    @Override // GoogleApiClient.ConnectionCallbacks
    public void onConnected(Bundle connectionHint) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onConnected: " + connectionHint);
        }
    }

    @Override  // GoogleApiClient.ConnectionCallbacks
    public void onConnectionSuspended(int cause) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onConnectionSuspended: " + cause);
        }
    }

    @Override  // GoogleApiClient.OnConnectionFailedListener
    public void onConnectionFailed(ConnectionResult result) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onConnectionFailed: " + result);
        }
    }
}

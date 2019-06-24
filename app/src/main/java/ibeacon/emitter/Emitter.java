package ibeacon.emitter;

import java.util.*;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.gson.Gson;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

import userinterface.responsemodel.BeaconResponse;
import userinterface.helperconfiguration.ConfigurationConstant;
import userinterface.helperconfiguration.PreferenceConnector;
import networklayer.ServiceLayer;
import networklayer.interfaces.ServiceLayerCallback;

import static com.androidquery.util.AQUtility.getContext;


public class Emitter extends Service {


    Activity activity;
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private BeaconTransmitter beaconTransmitter;
    private String major;
    private String minor;

    //    int REQ_CODE = 1;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setAdvertiseData() {
        String nickname = PreferenceConnector.readString(getApplicationContext(), ConfigurationConstant.nickname, null);

        //calling service to get uuid,major,minor for nickname
        ServiceLayer.GetBeaconByNickname(nickname, getContext(), new ServiceLayerCallback() {
            @Override
            public void onSuccess(String obj) {
                if(!obj.equals("")) {
                    Gson gson = new Gson();
                    BeaconResponse name = gson.fromJson(obj, BeaconResponse.class);

                    if (name.getNickname() != null) {
                        major = name.getMajor();
                        minor = name.getMinor();
                        Beacon beacon = new Beacon.Builder()
                                .setId1(ConfigurationConstant.UUID_Broadcast) // UUID for beacon
                                .setId2(major) // Major for beacon
                                .setId3(minor) // Minor for beacon
                                .setManufacturer(0x004C) // Radius Networks.0x0118  Change this for other beacon layouts//0x004C for iPhone
                                .setTxPower(-56) // Power in dB
                                .setDataFields(Collections.singletonList(0l)) // Remove this for beacon layouts without d: fields
                                .build();

                        BeaconParser beaconParser = new BeaconParser()
                                .setBeaconLayout(ConfigurationConstant.M_2_3_0215_I_4_19_I_20_21_I_22_23_P_24_24);

                        beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);
                        beaconTransmitter.startAdvertising(beacon, new AdvertiseCallback() {

                            @Override
                            public void onStartFailure(int errorCode) {
                                Log.e("", ConfigurationConstant.ADVERTISEMENT_START_FAILED_WITH_CODE + errorCode);
                            }

                            @Override
                            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                                Log.i("", ConfigurationConstant.ADVERTISEMENT_START_SUCCEEDED);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(String obj) {

            }
        });

    }






    public IBinder onBind(Intent intent) {
        return new Emit();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();

        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mAdapter == null || !mAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }
        if (mAdapter == null || !mAdapter.isEnabled()) {
            return;
        }
        setAdvertiseData();

    }




    @Override
    public void onDestroy() {
        try {

            beaconTransmitter.stopAdvertising();
        } catch (Exception e) {
            Log.d(ConfigurationConstant.CAUGHT, ConfigurationConstant.ERROR_CODE + e);
        }
    }

    private class Emit extends Binder {
        Emitter getService() {
            return Emitter.this;
        }
    }


}

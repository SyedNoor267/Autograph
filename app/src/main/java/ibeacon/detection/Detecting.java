package ibeacon.detection;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.appiapi.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import networklayer.AsyncServiceCall;
import networklayer.interfaces.Callback;
import userinterface.helperconfiguration.ConfigurationConstant;

import userinterface.responsemodel.UserDetailsResponse;

import static android.app.Activity.RESULT_OK;

@TargetApi(21)
public class Detecting extends Fragment implements View.OnClickListener, ResultCallback<LocationSettingsResult> {

    private BluetoothLeScanner mLEScanner;
    private BluetoothAdapter btAdapter;
    private Handler scanHandler;
    private Handler mHandler;
    private String MLTBEACON;
    private FloatingActionButton checkInBtn;

    private ScanSettings settings;
    private ArrayList<ScanFilter> filters;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private RecyclerView nearyou;

    private List<String> beaconDeviceList = new ArrayList<>();

    private int count = 0;
    private final ArrayList<Contact> fb = new ArrayList<Contact>();

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int REQUEST_CHECK_SETTINGS = 14;


    public static Detecting newInstance() {
        return new Detecting();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isShowDialog = false;
        scanHandler = new Handler();
        mHandler = new Handler();

        settingBlueTooth();

        settingLocationRequest();
        checkLocationPermission();

        if (isLocationEnabled()) {

        } else {
            displayLocationSettingsRequest();
        }

    }

    private void settingBlueTooth() {
        // init BLE
        BluetoothManager btManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();

        if (Build.VERSION.SDK_INT >= 21) {
            mLEScanner = btAdapter.getBluetoothLeScanner();
            settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            filters = new ArrayList<>();
        }
    }

    private void settingLocationRequest() {
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API).build();
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.near_you, container, false);

        checkInBtn = view.findViewById(R.id.fab);
        checkInBtn.setOnClickListener(this);
        checkInBtn.show();
        nearyou = view.findViewById(R.id.rvcontacts);


//        Log.i(ON_VIEW_CREATE, ES);

        return view;
    }


    private void startScan() {
        checkInBtn.hide();
        scanLeDevice(true);
    }

    private void stopScan() {
        checkInBtn.show();
        scanLeDevice(false);
    }


    private void scanLeDevice(final boolean enable) {
        try{
            if (enable) {
                long SCAN_PERIOD = 10000;
                mHandler.postDelayed(() -> {
                    mLEScanner.stopScan(mScanCallback);
                    checkInBtn.show();
//                    if (!isShowDialog){}
//                        Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getContext(), "Signal Not found. Please, Try again.", Toast.LENGTH_SHORT).show();
                }, SCAN_PERIOD);
//            Log.i(TAG, BLE_START_SCAN);
                if (Build.VERSION.SDK_INT < 21) {
//                Log.i(TAG, START_SDK_INT_211);
                    btAdapter.startLeScan(leScanCallback);
                } else {
//                Log.i(TAG, START_SDK_INT_21);
                    mLEScanner.startScan(filters, settings, mScanCallback);
                }
            } else {
//            Log.i(TAG, BLE_STOP_SCAN);
                if (Build.VERSION.SDK_INT < 21) {
//                Log.i(TAG, STOP_SDK_INT_211);
                    btAdapter.stopLeScan(leScanCallback);
                } else {
//                Log.i(TAG, STOP_SDK_INT_21);
                    mLEScanner.stopScan(mScanCallback);
                }
            }
        }catch(Exception ex){
            Log.e("TTTT", ex.getLocalizedMessage());
        }
    }

    private final ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
//            Log.i(TAG, CALLBACK_TYPE + String.valueOf(callbackType));
            byte[] scanRecord = result.getScanRecord().getBytes();
            findBeaconPattern(scanRecord);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
//            for (ScanResult sr : results) {
////                Log.i(TAG, SCAN_RESULT_RESULTS + sr.toString());
//            }
        }

        @Override
        public void onScanFailed(int errorCode) {
//            Log.e(TAG, SCAN_FAILED_ERROR_CODE + errorCode);
        }
    };


    private boolean isBlueToothOn() {
        return btAdapter != null && btAdapter.isEnabled();
    }


    private final BluetoothAdapter.LeScanCallback leScanCallback = (device, rssi, scanRecord) -> findBeaconPattern(scanRecord);

    private void findBeaconPattern(byte[] scanRecord) {
        int startByte = 2;
        boolean patternFound = false;
        while (startByte <= 5) {
            if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                    ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                patternFound = true;
                break;
            }
            startByte++;
        }

        if (patternFound) {
            byte[] uuidBytes = new byte[16];
            System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
            String hexString = bytesToHex(uuidBytes);

            String uuid = hexString.substring(0, 8) + "-" +
                    hexString.substring(8, 12) + "-" +
                    hexString.substring(12, 16) + "-" +
                    hexString.substring(16, 20) + "-" +
                    hexString.substring(20, 32);

            final int major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);

            final int minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);

            //Log.i(TAG, "UUID: " + uuid + "\\nmajor: " + major + "\\nminor" + minor);
//            foundBeacon(uuid,major,minor);

            //Duplication beacon check, if the beacon is other than MLT it will not add the beacon
            String UUID = ConfigurationConstant.UUID_Broadcast;
            String MLTBeacon = uuid + major + minor;
//            beaconDeviceList.add(MLTBeacon);
            if (uuid.equalsIgnoreCase(UUID)) {

                if (fb.size() == 0) {
                    //Add in the list and show in the listview
//                                getUser(uuid,String.valueOf(major),String.valueOf(minor));
                    String url= ConfigurationConstant.url_GetUserList;



                    JSONObject postData = new JSONObject();

                    //nickname, password, facebookid, twittered, linkedinid, xmppid, name, surname, displayName,
//           age, birthDate, sex, workid, email, phoneNumber, nationId, img_link, img_link_thumb, datacreation, dataupdate, status
                    try {
                        postData.put(ConfigurationConstant.UUID,uuid);
                        postData.put(ConfigurationConstant.MAJOR,major);
                        postData.put(ConfigurationConstant.MINOR,minor);
                    } catch (JSONException e) {
                        Log.e("TTTT", e.getMessage()+"");
                    }


                    AsyncServiceCall asyncTask = new AsyncServiceCall(url, getContext(), new Callback() {
                        @Override
                        public void onProgress() {

                        }

                        @Override
                        public void onResult(String result) {
                            Gson gson=new Gson();

                            UserDetailsResponse[] userDetailsResponse;
                            userDetailsResponse = gson.fromJson(result,UserDetailsResponse[].class);
                            if(userDetailsResponse!=null)
                            {


                                count = 0;
                                for (int i = 0; i < fb.size(); i++) {
                                    if (MLTBeacon.equalsIgnoreCase(fb.get(i).uuid)) {
                                        count++;//duplicate found in the list
                                    }

                                }
                                if(count==0)
                                {
                                    MLTBEACON = userDetailsResponse[0].getNickname();
                                    String MLTBEACONNAME = userDetailsResponse[0].getDisplayName();
                                    MLTBEACONNAME = MLTBEACONNAME.contains("@") ? MLTBEACONNAME.substring(0, MLTBEACONNAME.indexOf("@")) : MLTBEACONNAME;
                                    String image=userDetailsResponse[0].getImg_link_thumb();
                                    fb.add(new Contact(MLTBeacon,MLTBEACON,MLTBEACONNAME,image));

                                    nearyou.setAdapter(new ContactsAdapter(getContext(), fb));
                                    nearyou.setLayoutManager(new LinearLayoutManager(getActivity()));

                                }
                                //result.
                                //                        JSONObject result1 = new JSONObject().getJSONObject(result);


                            }

                            else {
                                Toast.makeText(getContext(), ConfigurationConstant.PLEASE_TURN_ON_INTERNET,Toast.LENGTH_SHORT).show();
//                                return;
                            }
                        }

                        @Override
                        public void onCancel() {

                        }
                    }, null, postData, ConfigurationConstant.POSTLIST);

                    asyncTask.execute();



                } else {
                    count = 0;
                    for (int i = 0; i < fb.size(); i++) {
                        if (MLTBeacon.equalsIgnoreCase(fb.get(i).uuid)) {
                            count++;//duplicate found in the list
                        }

                    }
                    //if count is 0 add in the list else donot add
                    if (count == 0) {
                        //Add in the list and show in the listview
//                                    getUser(uuid,String.valueOf(major),String.valueOf(minor));
                        String url= ConfigurationConstant.url_GetUserList;



                        JSONObject postData = new JSONObject();

                        //nickname, password, facebookid, twittered, linkedinid, xmppid, name, surname, displayName,
//           age, birthDate, sex, workid, email, phoneNumber, nationId, img_link, img_link_thumb, datacreation, dataupdate, status
                        try {
                            postData.put(ConfigurationConstant.UUID,uuid);
                            postData.put(ConfigurationConstant.MAJOR,major);
                            postData.put(ConfigurationConstant.MINOR,minor);
                        } catch (JSONException e) {
                            Log.e("TTTT", e.getMessage()+"");
                        }


                        AsyncServiceCall asyncTask = new AsyncServiceCall(url, getContext(), new Callback() {
                            @Override
                            public void onProgress() {

                            }

                            @Override
                            public void onResult(String result) {
                                Gson gson=new Gson();


                                if(!result.equals(ConfigurationConstant.HTTP_1_1_200_SUCCESSFUL))
                                {
                                    UserDetailsResponse[] userDetailsResponse;
                                    userDetailsResponse = gson.fromJson(result,UserDetailsResponse[].class);

                                    count = 0;
                                    for (int i = 0; i < fb.size(); i++) {
                                        if (MLTBeacon.equalsIgnoreCase(fb.get(i).uuid)) {
                                            count++;//duplicate found in the list
                                        }

                                    }
                                    if(count==0)
                                    {
                                        MLTBEACON = userDetailsResponse[0].getNickname();
                                        String MLTBEACONNAME = userDetailsResponse[0].getDisplayName();
                                        String image=userDetailsResponse[0].getImg_link_thumb();
                                        fb.add(new Contact(MLTBeacon,MLTBEACON,MLTBEACONNAME,image));
                                        nearyou.setAdapter(new ContactsAdapter(getContext(), fb));
                                        nearyou.setLayoutManager(new LinearLayoutManager(getActivity()));

                                    }


                                    //result.
                                    //                        JSONObject result1 = new JSONObject().getJSONObject(result);
//                                    String MLTBEACON = userDetailsResponse[0].getNickname();
//                                    String MLTBEACONNAME = userDetailsResponse[0].getDisplayName();
//                                    String image=userDetailsResponse[0].getImg_link_thumb();
//                                    fb.add(new Contact(MLTBeacon,MLTBEACON,MLTBEACONNAME,image));
//                                    nearyou.setAdapter(new ContactsAdapter(getContext(), fb));
//
//                                    nearyou.setLayoutManager(new LinearLayoutManager(getActivity()));


                                }

                                else {
//                                    Toast.makeText(getContext(),"Please turn on internet",Toast.LENGTH_SHORT).show();
//                                    return;
                                }
                            }

                            @Override
                            public void onCancel() {

                            }
                        }, null, postData, ConfigurationConstant.POSTLIST);

                        asyncTask.execute();





                    }
                }



                //**************************************




                //*****************************************

            }
            else {
                return;
            }
        }
        ContactsAdapter contactsAdapter=new ContactsAdapter(getContext(), fb);
        contactsAdapter.notifyDataSetChanged();
        nearyou.setAdapter(contactsAdapter);
        nearyou.setLayoutManager(new LinearLayoutManager(getActivity()));


    }

    private static final String ABCDEF = "0123456789ABCDEF";
    private static final char[] hexArray = ABCDEF.toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scanHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            settingBlueTooth();
            startScan();
        }
//        else if (resultCode == RESULT_OK && requestCode == 14) { }
        else {
//            Log.e(TAG, RESULT_NOT_OK);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.fab: {
                checkInBtnClicked();
                break;
            }
        }
    }

    private void checkInBtnClicked() {
//        Log.i(TAG, BUTTON_IS + String.valueOf(checkInBtn.isExpanded()));
        if (!checkInBtn.isExpanded()) {

            boolean isValid = true;

            if (!isLocationEnabled()) {
                isValid = false;
                displayLocationSettingsRequest();
            }

            if (!isBlueToothOn()) {
                isValid = false;
                UiHelper.showInformationMessage(getActivity(), ConfigurationConstant.ENABLE_BLUETOOTH, ConfigurationConstant.PLEASE_ENABLE_BLUETOOTH_BEFORE_CHECK_IN,
                        false, (dialogInterface, i) -> {
                            if (i == DialogInterface.BUTTON_POSITIVE) {
                                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableIntent, 1);
                            }
                        });
            }

            if (isValid) {
                fb.clear();
                startScan();
            }

        } else {

            stopScan();
        }
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getContext())
                        .setTitle(ConfigurationConstant.REQUESTING_FOR_PERMISSION)
                        .setMessage(ConfigurationConstant.REQUIRES_ACCESS_TO_LOCATION)
                        .setPositiveButton(R.string.ok, (dialogInterface, i) -> ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION))
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
//            Log.i(TAG, PERSMISSION_GRANTED);
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                    }

                }
//                return;
            }

        }
    }

    private boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(getContext().getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                Log.e("TTTT", e.getMessage()+"");
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    private synchronized void displayLocationSettingsRequest() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(this);
    }



    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
//                Log.i(TAG, ALL_LOCATION_SETTINGS_ARE_SATISFIED);
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                Log.i(TAG, LOCATION_SETTINGS_ARE_NOT_SATISFIED_SHOW_THE_USER_A_DIALOG_TO_UPGRADE_LOCATION_SETTINGS);

                try {
                    status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.e("TTTT", e.getMessage()+"");
//                    Log.i(TAG, PENDING_INTENT_UNABLE_TO_EXECUTE_REQUEST);
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                Log.i(TAG, LOCATION_SETTINGS_ARE_INADEQUATE_AND_CANNOT_BE_FIXED_HERE_DIALOG_NOT_CREATED);
                break;
        }
    }


}
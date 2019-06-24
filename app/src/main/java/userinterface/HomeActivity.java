package userinterface;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import helpers.HeaderMenu;
import ibeacon.emitter.Emitter;
import adapter_classes.ContentPagerAdapter;
import adapter_classes.MenuItemDecoration;
import adapter_classes.ScrollPaneAdapter;
import instagramlogin.Constants;
import networklayer.ServiceLayer;
import networklayer.interfaces.ServiceLayerCallback;
import userinterface.helperconfiguration.ConfigurationConstant;
import userinterface.helperconfiguration.PreferenceConnector;
import userinterface.responsemodel.UserImage;
import userinterface.responsemodel.UserResponse;
import userinterface.signup_signin.SignInFragement;

import com.example.appiapi.R;
import com.google.gson.Gson;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import org.jxmpp.jid.DomainBareJid;

import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


import javax.net.ssl.HostnameVerifier;


public class HomeActivity extends Fragment {
    private ViewPager contentDisplay;
    private Button backButton;
    private DiscreteScrollView scrollPane;
    Switch txSwitch;
    private ArrayList<HeaderMenu> headerMenu;
    private Uri imageuri;
    private AbstractXMPPConnection mConnection;
    BluetoothAdapter mBluetoothAdapter;
    ContentPagerAdapter contentPagerAdapter;

    private String imagedb;
    private Uri peoplenear , chat;

    private static final int REQUEST_BLUETOOTH = 1;
    public static HomeActivity homeActivity;
    private ProgressDialog progressDialog = null;

    public static HomeActivity newInstance() {
        homeActivity = new HomeActivity();
        return homeActivity;
    }

    public static HomeActivity getInstance(){
        return homeActivity;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.application_layout_interface, container, false);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        peoplenear= Uri.parse(ConfigurationConstant.peoplenear);
        chat= Uri.parse(ConfigurationConstant.chatimage);

        String user = PreferenceConnector.readString(getContext(), ConfigurationConstant.nickname, null);
        String pass = PreferenceConnector.readString(getContext(), ConfigurationConstant.password, null);

        setConnection(user, pass);

        contentDisplay = view.findViewById(R.id.content_display);
        scrollPane = view.findViewById(R.id.scroll_pane);
        txSwitch = view.findViewById(R.id.txSwitch);
        backButton = view.findViewById(R.id.back_button);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        checkInternet();

        txSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                requestBluetoothPermission();
            } else {
                disableBluetooth();
                Objects.requireNonNull(getActivity()).stopService(new Intent(getActivity(), Emitter.class));
            }

        });

        if ( mBluetoothAdapter.isEnabled() ) {
            txSwitch.setChecked(true);
            requestLocationPermission();
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        headerMenu = new ArrayList<>();
        ServiceLayer.GetUserByNickname(user, getContext(), new ServiceLayerCallback() {
            @Override
            public void onSuccess(String obj) {
                Gson gson = new Gson();
                UserResponse name = gson.fromJson(obj, UserResponse.class);

                if(name!=null){

                    String displayName = name.getDisplayName();
                    displayName = displayName.contains("@") ? displayName.substring(0, displayName.indexOf("@")) : displayName;
                    PreferenceConnector.writeString( getContext(), ConfigurationConstant.name, displayName );
                    PreferenceConnector.writeString(getContext(), ConfigurationConstant.birthDate, name.getBirthDate());
                    PreferenceConnector.writeString(getContext(), ConfigurationConstant.sex, name.getSex());
                    try {
//                        imagedb=name.getImg_link() != null ? name.getImg_link() : "android.resource://com.example.appiapi/drawable/"+name.getName().charAt(0);
                        String imageType = null;
                        if(name.getImg_link() != null){
                            imagedb = name.getImg_link();
                            imageType = "URL";
                        }else{
                            //imagedb = name.getName().charAt(0)+"";
                            imagedb = "";
                            String[] names = name.getDisplayName().trim().split(" ");
                            for(int count = 0; count<names.length; count++){
                                imagedb = imagedb+names[count].charAt(0)+"";
                                if(count==1){
                                    break;
                                }
                            }
                            imageType = "BITMAP";
                            //imagedb = UserImage.getBitmapFromString(name.getName().charAt(0)+"", 32.0f, getContext());
                        }
                        imageuri = Uri.parse(imagedb);
                        headerMenu.add( new HeaderMenu( imageuri, ConfigurationConstant.PROFILES, imageType ) );
                        headerMenu.add( new HeaderMenu( Uri.parse( ConfigurationConstant.peoplenear ), ConfigurationConstant.PEOPLE_NEAR_YOU, "URL"));
                        headerMenu.add( new HeaderMenu( Uri.parse( ConfigurationConstant.chatimage ), ConfigurationConstant.CHATS, "URL"));
                        scrollPane.setAdapter(new ScrollPaneAdapter(getContext(), headerMenu));
                    } catch ( Exception e ) {
                        //Log.e("TTTTT", e.getMessage());
                        Uri profilepic = Uri.parse(ConfigurationConstant.ANDROID_RESOURCE_COM_EXAMPLE_APPIAPI_DRAWABLE_IC_ACCOUNT_CIRCLE_GREY_600_48_DP);
                        //String profilepic = ConfigurationConstant.ANDROID_RESOURCE_COM_EXAMPLE_APPIAPI_DRAWABLE_IC_ACCOUNT_CIRCLE_GREY_600_48_DP;
                        headerMenu.add( new HeaderMenu( profilepic, ConfigurationConstant.PROFILES, "URL" ) );
                        headerMenu.add( new HeaderMenu( Uri.parse( ConfigurationConstant.peoplenear ), ConfigurationConstant.PEOPLE_NEAR_YOU, "URL" ) );
                        headerMenu.add( new HeaderMenu( Uri.parse( ConfigurationConstant.chatimage ), ConfigurationConstant.CHATS, "URL" ) );
                        scrollPane.setAdapter( new ScrollPaneAdapter( getContext(), headerMenu ) );
                        e.getStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(String obj) {

            }
        });


        scrollPane.addScrollListener(scrollListener);
        scrollPane.addItemDecoration(new MenuItemDecoration());
        scrollPane.setOverScrollEnabled(true);
        scrollPane.setItemTransformer(new ScaleTransformer.Builder()
                .setMaxScale(1.05f)
                .setMinScale(0.6f)
                .setPivotX(Pivot.X.CENTER).setPivotY(Pivot.Y.CENTER).build());
        scrollPane.setNestedScrollingEnabled(false);
        onScrollInitiated();
        //ViewCompat.setNestedScrollingEnabled(scrollPane, false);
        if(contentPagerAdapter == null) {
            contentPagerAdapter = new ContentPagerAdapter(getFragmentManager(), getContext());
        }
        contentDisplay.setAdapter(contentPagerAdapter);
        //contentDisplay.
        contentDisplay.setOnTouchListener((v, event) -> true);
        return view;
    }

    private void checkInternet(){
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Wait as you get Authenticated");
        progressDialog.show();
        asyncTask.execute("http://www.google.com");
    }


    AsyncTask<String, Integer, Boolean> asyncTask = new AsyncTask<String, Integer, Boolean>() {
        @Override
        protected Boolean doInBackground(String... strings) {
            return isNetworkConnected(strings[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
            if(!aBoolean) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("No Internet Connection");
                alertDialog.setMessage("Please eneble network connection to your device");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                getActivity().finish();
                            }
                        });
                alertDialog.show();

            }
        }

        //Checks Internets
        public boolean isNetworkConnected(String checkURL) {
            ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            Boolean networkState = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
            if(!networkState.booleanValue()){
                return false;
            }else {
                Boolean state = false;
                try {
                    URL url = new URL(checkURL);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.connect();
                    if(connection.getResponseCode() != HttpURLConnection.HTTP_ACCEPTED){
                        state = true;
                        connection.disconnect();
                    }
                } catch (Exception e1) {
                    Log.e("TTT", e1.getLocalizedMessage());
                }
                return state;
            }
        }
    };




    /*
    * Check if bluetooth is enabled,
    * if false, then continue and enable bluetooth
    * else,  continue and check whether Location is enabled*/
    private void requestBluetoothPermission() {
        if ( mBluetoothAdapter == null ) {
            // Device does not support Bluetooth
            Toast.makeText(getContext(), "Device is not supported", Toast.LENGTH_SHORT).show();
        }else if ( !mBluetoothAdapter.isEnabled() ) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_BLUETOOTH);
        }else if ( mBluetoothAdapter.isEnabled() ) {
            requestLocationPermission();
        }

    }

    /*
    * Disable bluetooth then the */
    private void disableBluetooth() {
        boolean bluetoothState = mBluetoothAdapter.disable();
        while( bluetoothState == true ) {
            if (!mBluetoothAdapter.isEnabled()) {
                txSwitch.setChecked(false);
                break;
            }/*else{
                txSwitch.setChecked(true);
                break;
            }*/
        }
    }


    /*
    * Check if Location is enabled,
    * if false, then continue and enable Location*/
    private void requestLocationPermission() {
        LocationManager locationManager = (LocationManager)getContext().getSystemService(getContext().LOCATION_SERVICE);
        try {
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(!gpsEnabled && !networkEnabled) {
//                Enable location services on the device.
                Intent enableLocation = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(enableLocation);
            }
        }catch(Exception ex){
            Toast.makeText(getContext(), ex.getMessage()+"", Toast.LENGTH_SHORT).show();
        }
        Objects.requireNonNull(getActivity()).startService(new Intent(getActivity(), Emitter.class));

    }


    private void setConnection(String user, String pass) {
        // Create the configuration for this new connection
        //this function or code given in official documention give an error in openfire run locally to solve this error
        //first off firewall
        //then follow my steps
        new Thread() {
            @Override
            public void run() {
                InetAddress addr = null;
                try {

                    // inter your ip4address now checking it
                    addr = InetAddress.getByName(ConfigurationConstant.NIPSDB_02_DYNDNS_ORG);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                HostnameVerifier verifier = (hostname, session) -> false;
                DomainBareJid serviceName = null;
                try {
                    serviceName = JidCreate.domainBareFrom(ConfigurationConstant.NIPSDB_02_DYNDNS_ORG);
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }
                XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()

                        .setUsernameAndPassword(user, pass)
                        .setPort(5222)
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                        .setXmppDomain(serviceName).setSendPresence(true)
                        .setHostnameVerifier(verifier)
                        .setHostAddress(addr)
                        .setDebuggerEnabled(true)
                        .build();
                mConnection = new XMPPTCPConnection(config);

                SASLAuthentication.unBlacklistSASLMechanism(ConfigurationConstant.PLAIN);
                SASLAuthentication.blacklistSASLMechanism(ConfigurationConstant.DIGEST_MD_5);
                try {
                    mConnection.connect().login();
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private final DiscreteScrollView.ScrollListener scrollListener = new DiscreteScrollView.ScrollListener() {
        @Override
        public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable RecyclerView.ViewHolder currentHolder, @Nullable RecyclerView.ViewHolder newCurrent) {
            //Toast.makeText(getContext(), newPosition+"", Toast.LENGTH_SHORT).show();
            if(scrollPosition==1.0 || scrollPosition== -1.0){
                PreferenceConnector.writeInteger(getContext(), Constants.CURRENT_VIEW_PAGE, newPosition);
                contentDisplay.setCurrentItem(newPosition);
            }
            if(newPosition==1){
                txSwitch.setVisibility(View.VISIBLE);
            }else{
                txSwitch.setVisibility(View.INVISIBLE);
            }
        }
    };

    @TargetApi(Build.VERSION_CODES.M)
    public void onScrollInitiated() {
        super.onStart();
        int currentViewPage = PreferenceConnector.readInteger(getContext(), Constants.CURRENT_VIEW_PAGE, 0);
        contentDisplay.setCurrentItem(currentViewPage, false);
        scrollPane.setScrollIndicators(View.SCROLL_INDICATOR_BOTTOM);
        scrollPane.scrollToPosition(currentViewPage);
    }

    @Override
    public void onStop() {
        super.onStop();
        Presence presence ;
        try {
            Roster roster1 = Roster.getInstanceFor(mConnection);
            Collection<RosterEntry> entries = roster1.getEntries();
            for (RosterEntry entry : entries) {
                presence = new Presence(Presence.Type.unavailable);
                // presence.setPacketID(Packet.ID_NOT_AVAILABLE);
                presence.setFrom(mConnection.getUser());
                presence.setTo(entry.getUser());
                mConnection.sendStanza(presence);
                System.out.println(presence.toXML());
            }
            // To the other client sends the same user stealth
            presence = new Presence(Presence.Type.unavailable);
            // presence.setPacketID(Packet.ID_NOT_AVAILABLE);
            presence.setFrom(mConnection.getUser());
            //presence.setTo(StringUtils.parseBareAddress(Config.conn1.getUser()));
            mConnection.sendPacket(presence);
//            Log.e(STATE, SET_UP_STEALTH);

//            presence = new Presence(Presence.Type.unavailable);
//            presence.setFrom(JidCreate.domainBareFrom(user+"@nipsdb02.dyndns.org"));
//            mConnection.sendStanza(presence);
//            mConnection.disconnect();
        }
        catch (Exception e)
        {
            e.getStackTrace();
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Presence presence ;
        try {
            if(mConnection!= null) {
                Roster roster1 = Roster.getInstanceFor(mConnection);
                Collection<RosterEntry> entries = roster1.getEntries();
                for (RosterEntry entry : entries) {
                    presence = new Presence(Presence.Type.unavailable);
                    // presence.setPacketID(Packet.ID_NOT_AVAILABLE);
                    presence.setFrom(mConnection.getUser());
                    presence.setTo(entry.getUser());
                    mConnection.sendStanza(presence);
                    System.out.println(presence.toXML());
                }
                // To the other client sends the same user stealth
                presence = new Presence(Presence.Type.unavailable);
                // presence.setPacketID(Packet.ID_NOT_AVAILABLE);
                presence.setFrom(mConnection.getUser());
                //presence.setTo(StringUtils.parseBareAddress(Config.conn1.getUser()));
                mConnection.sendPacket(presence);
//            Log.e(STATE, SET_UP_STEALTH);
            }

        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
    * When the bluetooth has been switched on use its resultCode to start LocationServices*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( resultCode != 0 ) {
            txSwitch.setChecked(true);
            requestLocationPermission();
        }else{
            txSwitch.setChecked(false);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //super.onSaveInstanceState(outState);
       outState.putInt( "CURRENT_PAGE", contentDisplay.getCurrentItem() );
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored( savedInstanceState );
        //contentDisplay.setCurrentItem((savedInstanceState != null) ? savedInstanceState.getInt( "CURRENT_PAGE" ) : 0 );
        //contentPagerAdapter.r
    }

    @Override
    public void onResume() {
        super.onResume();
        String user = PreferenceConnector.readString(getContext(), ConfigurationConstant.nickname, null);
        String pass = PreferenceConnector.readString(getContext(), ConfigurationConstant.password, null);
        if( user == null && pass == null ){
            //getFragmentManager().beginTransaction().replace(R.id.main, new SignInFragement()).commit();
            getActivity().finish();
        }
    }
}

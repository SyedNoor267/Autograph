package networklayer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.appiapi.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import networklayer.interfaces.Callback;
import userinterface.helperconfiguration.ConfigurationConstant;


public class AsyncServiceCall extends AsyncTask<String, Void, String> {

    private final Callback callback;
    //private ProgressDialog dialog;

    private final LinkedHashMap<Object, Object> mParams = new LinkedHashMap<>();
    private final String mTypeofRequest;
    private String mStrToAppend = "";
    private boolean isPostDataInJson = false;
    private JSONObject jsonPostData;
    private String setURL;

    public AsyncServiceCall(String url, Context context, Callback c, HashMap<Object, Object> data, JSONObject jsonObject, String request) {
        setURL = url;
        callback = c;
        mTypeofRequest = request;
        jsonPostData = jsonObject;

        HashMap<Object, Object> mData = null;
        if ((data != null) && (jsonObject == null)) {
            try {
                mData = data;

            } catch (Exception e) {
                Log.d("", e.getLocalizedMessage());
            }

            if (mTypeofRequest.equalsIgnoreCase(ConfigurationConstant.GET)) {
                Object key ;

                Iterator<Object> it = mData.keySet().iterator();
                while (it.hasNext()) {
                    key = it.next();
                    mParams.put(key, mData.get(key));
//                    Log.d("Data", key.toString() + " " + mData.get(key).toString());
                }

                Iterator<Object> itParams = mParams.keySet().iterator();
                int sizeOfParams = mParams.size();
                int index = 0;
                while (itParams.hasNext()) {
                    Object keyParams = itParams.next();
                    index++;
                    if (index == sizeOfParams) {
                        mStrToAppend += keyParams + "=" + mParams.get(keyParams);
                        break;
                    }

                    mStrToAppend =  mStrToAppend.concat(keyParams.toString()).concat("=").concat(mParams.get(keyParams).toString()).concat("&");

                }

            }


            //Post Request

            if (mTypeofRequest.equalsIgnoreCase(ConfigurationConstant.POST)) {
                Object key ;
                isPostDataInJson = false;
                Iterator<Object> it = mData.keySet().iterator();

                while (it.hasNext()) {
                    key = it.next();
                    mParams.put(key, mData.get(key));
                }
            }


        }


        if ((mData == null) && (jsonPostData != null) && (mTypeofRequest.equalsIgnoreCase(ConfigurationConstant.POST))) {
            isPostDataInJson = true;
        }

//        if ((mData == null) && (jsonPostData != null) && (mTypeofRequest.equalsIgnoreCase("PUT") == true)) {
//        }
        try {
            /*if(dialog == null) {
                dialog = ProgressDialog.show(context, ConfigurationConstant.PLEASE_WAIT, ConfigurationConstant.PAGE_IS_LOADING);
                dialog.setIndeterminate(true);
                dialog.setIcon(R.mipmap.autograph_icon_new);
                //here is the trick:
                //dialog.setIndeterminateDrawable(getResources().getDrawable(R.mipmap.autograph_icon, null));
                dialog.setCancelable(false);
            }*/

        } catch (Exception e) {
            e.getStackTrace();
        }


    }


    @Override

    protected String doInBackground(String... baseUrls) {
        //publishProgress(null);
        if (mTypeofRequest.equalsIgnoreCase(ConfigurationConstant.GET)) {
            String finalURL = setURL + "?" + mStrToAppend;
            try {
                HTTPUtility.GET(finalURL);
            } catch (Exception e) {
                Log.d("", e.getLocalizedMessage());
            }
            return HTTPUtility.GET(finalURL);
        }

        if (mTypeofRequest.equalsIgnoreCase(ConfigurationConstant.POST)) {

            if (!isPostDataInJson) {
                return HTTPUtility.POST(setURL, mParams);
            }
            if (isPostDataInJson) {
                Log.i(ConfigurationConstant.JSONPOSTMETHOD, ConfigurationConstant.JSON_METHOD_TO_BE_CALLED);
                return HTTPUtility.POST(setURL, jsonPostData);
            }
        }
        if (mTypeofRequest.equalsIgnoreCase(ConfigurationConstant.PUT)) {
            return HTTPUtility.POST(setURL, jsonPostData);
        }
        if (mTypeofRequest.equalsIgnoreCase(ConfigurationConstant.POSTLIST)) {
            JSONArray ja = new JSONArray();
            ja.put(jsonPostData);
            return HTTPUtility.POST(setURL, ja);

        }

        return null;
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        /*try {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.getStackTrace();
        }*/

        callback.onResult(result);
    }

    @Override
    protected void onProgressUpdate(Void... voids) {
        callback.onProgress();
    }
}

package networklayer;

import android.util.Log;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import userinterface.helperconfiguration.ConfigurationConstant;

class HTTPUtility {

private final static HttpClient mHttpClient=new DefaultHttpClient();



    public static String GET(String url)
{
    InputStream inputStream=null;
    String result="";
    try
    {

        //make get request to give url
        HttpGet httpGet=new HttpGet(url);
        httpGet.setHeader(ConfigurationConstant.AUTHORIZATION, ConfigurationConstant.BASIC_BMLWCZ_IW_MTK);
      // httpGet.addHeader(BasicScheme.authenticate( new UsernamePasswordCredentials("Authorization", "bmlwczIwMTk="), "UTF-8", false));

        HttpResponse httpResponse=mHttpClient.execute(httpGet);
        inputStream=httpResponse.getEntity().getContent();

        if(inputStream!=null)
        {
            result=convertInputStreamToString(inputStream);
        }
        else {result= ConfigurationConstant.DID_NOT_WORK;}

    }
    catch (Exception e)
    {
        Log.e(ConfigurationConstant.EXCEPTION,e.getLocalizedMessage());
    }

    return result;
}


public static String POST(String url, HashMap<Object,Object> mParams)
{
    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

    InputStream inputStream=null;
    String result="";
    try {
        Object key=null;
        Iterator<Object> it=mParams.keySet().iterator();
        while (it.hasNext())
        {
            key=it.next();
            nameValuePairs.add(new BasicNameValuePair(key.toString(),mParams.get(key.toString()).toString()));
            //Log.d("Data", key.toString() + " " + mData.get(key).toString());
        }
        HttpPost httpPost=new HttpPost(url);
        httpPost.setHeader(ConfigurationConstant.AUTHORIZATION,ConfigurationConstant. BASIC_BMLWCZ_IW_MTK);
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, ConfigurationConstant.UTF_8));
        HttpResponse httpResponse=mHttpClient.execute(httpPost);

        inputStream=httpResponse.getEntity().getContent();

        if(inputStream!=null)
        {
            result=convertInputStreamToString(inputStream);
        }
        else {result= ConfigurationConstant.DIDNT_WORK;}
    }
    catch (Exception e)
    {
        Log.e(ConfigurationConstant.EXCEPTION_POST,e.getLocalizedMessage());
    }

    return result;
}


    public static String POST(String url, JSONObject obj){
        Log.i(ConfigurationConstant.JSONPOSTBEGIN, ConfigurationConstant.BEGINNING_OF_JSON_POST);
//        InputStream inputStream = null;
        String result = "";
        //HttpClient httpclient = new DefaultHttpClient();
        try{
            HttpPost post = new HttpPost(url);
            post.setHeader(ConfigurationConstant.CONTENT_TYPE, ConfigurationConstant.APPLICATION_JSON);
            post.setHeader(ConfigurationConstant.ACCEPT, ConfigurationConstant.APPLICATION_JSON);
            post.setHeader(ConfigurationConstant.AUTHORIZATION, ConfigurationConstant.BASIC_BMLWCZ_IW_MTK);
            post.setHeader(ConfigurationConstant.ACCEPT_LANGUAGE, ConfigurationConstant.EN_US);


            StringEntity se = new StringEntity(obj.toString());
            //se.setContentType("application/json;charset=UTF-8");
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, ConfigurationConstant.APPLICATION_JSON));
            post.setEntity(se);
            HttpResponse httpResponse = mHttpClient.execute(post);

            result = httpResponse.getStatusLine().toString();


        } catch (Exception e) {
            Log.d(ConfigurationConstant.INPUT_STREAM, e.getLocalizedMessage());
        }
        Log.i(ConfigurationConstant.JSONPOSTEND, ConfigurationConstant.END_OF_JSON_DATA_POST_METHOS);
        return result;
    }


    public static String POST(String url, JSONArray obj){
        List<String> list = new ArrayList<String>();
        Log.i(ConfigurationConstant.JSONPOSTBEGIN, ConfigurationConstant.BEGINNING_OF_JSON_POST);
        InputStream inputStream = null;
        String result = "";
        //HttpClient httpclient = new DefaultHttpClient();
        try{
            HttpPost post = new HttpPost(url);
            post.setHeader(ConfigurationConstant.CONTENT_TYPE, ConfigurationConstant.APPLICATION_JSON);
            post.setHeader(ConfigurationConstant.ACCEPT, ConfigurationConstant.APPLICATION_JSON);
            post.setHeader(ConfigurationConstant.AUTHORIZATION, ConfigurationConstant.BASIC_BMLWCZ_IW_MTK);
            post.setHeader(ConfigurationConstant.ACCEPT_LANGUAGE, ConfigurationConstant.EN_US);

            for (int i=0; i<obj.length(); i++) {
                list.add(obj.getString(i));
            }

            StringEntity se = new StringEntity(list.toString());
            //se.setContentType("application/json;charset=UTF-8");
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, ConfigurationConstant.APPLICATION_JSON));
            post.setEntity(se);
            HttpResponse httpResponse = mHttpClient.execute(post);

            //result = httpResponse.getStatusLine().toString();
            inputStream=httpResponse.getEntity().getContent();

            if(inputStream!=null)
            {
                result=convertInputStreamToString(inputStream);
            }
            else {result= ConfigurationConstant.DID_NOT_WORK;}

        } catch (Exception e) {
            Log.d(ConfigurationConstant.INPUT_STREAM, e.getLocalizedMessage());
        }
        Log.i(ConfigurationConstant.JSONPOSTEND, ConfigurationConstant.END_OF_JSON_DATA_POST_METHOS);
        return result;
    }


//    public static String PUT(String url, JSONObject obj){
//        Log.i("JSONPOSTBEGIN", "Beginning of JSON POST");
//        InputStream inputStream = null;
//        String result = "";
//        //HttpClient httpclient = new DefaultHttpClient();
//        try{
//            HttpPut put = new HttpPut(url);
//            put.setHeader("Content-type", "application/json");
//            put.setHeader("Accept", "application/json");
//            put.setHeader("Authorization","Basic bmlwczIwMTk=");
//
//
//            StringEntity se = new StringEntity(obj.toString());
//            //se.setContentType("application/json;charset=UTF-8");
//            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//            put.setEntity(se);
//            HttpResponse httpResponse = mHttpClient.execute(put);
//            // receive response as inputStream
//            inputStream = httpResponse.getEntity().getContent();
//            // convert inputstream to string
//            if(inputStream != null){
//                result = convertInputStreamToString(inputStream);
//            }
//            else
//                result = "Did not work!";
//        } catch (Exception e) {
//            Log.d("InputStream", e.getLocalizedMessage());
//        }
//        Log.i("JSONPOSTEND", "End of JSON data post methos...");
//        return result;
//    }



    private static String convertInputStreamToString(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
        String line="";
        String result="";

        while ((line=bufferedReader.readLine())!=null)
        {
            result=result.concat(line);
        }
        inputStream.close();
        return result;
    }
}

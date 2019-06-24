package networklayer.interfaces;

import org.json.JSONException;

public interface ServiceLayerCallback {

    void onSuccess(String obj) throws JSONException;
    void onFailure(String obj);
}

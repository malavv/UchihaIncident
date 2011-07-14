package com.ninja.exMenu;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;


public class FbConnect {
  private static FbConnect instance_;
  private Facebook fb_;
  private FbConnectCallback callback_;
  private Activity activity_;
  private boolean isConnected_;
  
  public FbConnect() { isConnected_ = false; }
  
  public static FbConnect Get() {
    if (instance_ == null)  instance_ = new FbConnect();
    return instance_;
  }
  
  public void Login(Activity a, String appId, String[] permissions,
      FbConnectCallback authCallback) {
    activity_ = a;
    callback_ = authCallback;
    
    final Facebook fb = new Facebook(appId);
    Session.waitForAuthCallback(fb);
    fb.authorize(activity_, permissions, new LoginListener(this, fb));
  }
  
  public void Logout() {
    try {
      fb_.logout(activity_.getApplicationContext());
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  public boolean IsConnected() { return isConnected_; }
  
  public Identity GetIdentity() {
    if (!IsConnected())  return null;
    
    try {
      JSONObject response = Util.parseJson(fb_.request("/me"));
      return new Identity(response.getString("name"), response.getString("id")); 
      
    } catch (Throwable e) {
      Log.e("FbConnect", "Erreure majeure dans l'obtention de l'identitée.");
      e.printStackTrace();
      return null;
    }
  }
  
  public ArrayList<Identity> GetFriends() {
    if (!IsConnected())  return null;
    
    try {
      JSONObject response = Util.parseJson(fb_.request("/me/friends"));
      JSONArray friends = response.getJSONArray("data");
      ArrayList<Identity> friendArray = new ArrayList<FbConnect.Identity>();
      for (int i = 0; i < friends.length(); i++)
        friendArray.add(new Identity(friends.getJSONObject(i).getString("name"), friends.getJSONObject(i).getString("id")));
      return friendArray;
      
    } catch (Throwable e) {
      Log.e("FbConnect", "Erreure majeure dans l'obtention de l'identitée.");
      e.printStackTrace();
      return null;
    }
  }
  
  public void PostOnFeed(String str) {
    if (!IsConnected())  return;
    
    Bundle params = new Bundle();
    params.putString("message", str);
    try {
      fb_.request("/me/feed", params, "POST");
    } catch (Throwable e) {
      Log.e("FbConnect", "Erreure majeure dans l'obtention de l'identitée.");
      e.printStackTrace();
      return;
    }
  }
  
  private void onSuccesfulLogin(Facebook fb) {
    fb_ = fb;
    isConnected_ = true; 
    callback_.onComplete(true);
  }
  private void onFailedLogin() { callback_.onComplete(false); }
  
  
  public interface FbConnectCallback { public abstract void onComplete(boolean isAuth); }
  
  public class Identity {
    public Identity(String name, String uid) { Name = name; Id = uid; }
    public String Name;
    public String Id;
    @Override public String toString() { return "{" + Id + ":" + Name + "}"; } 
  }
  
  private class LoginListener implements DialogListener {
    private FbConnect callback_;
    private Facebook fb_;
    public LoginListener(FbConnect callback, Facebook fb) { callback_ = callback; fb_ = fb; }
    @Override public void onComplete(Bundle values) { callback_.onSuccesfulLogin(fb_); }

    @Override public void onFacebookError(FacebookError e) {
      Log.d("FbConnect", "Throwed FbErr : " + e.getMessage());
      callback_.onFailedLogin();
    }

    @Override public void onError(DialogError e) {
      Log.d("FbConnect", "Throwed DiagError : " + e.getMessage());
      callback_.onFailedLogin();
    }

    @Override public void onCancel() {
      Log.d("FbConnect", "Login Cancelled");
      callback_.onFailedLogin();  
    }
  }
  
  abstract class AsyncRequestListener implements RequestListener {
    public void onComplete(String response, final Object state) {
      try {
        JSONObject obj = Util.parseJson(response);
        onComplete(obj, state);
      } catch (JSONException e) {
        e.printStackTrace();
        Log.e("facebook-stream", "JSON Error:" + e.getMessage());
      } catch (FacebookError e) {
        Log.e("facebook-stream", "Facebook Error:" + e.getMessage());
      }
    }

    public abstract void onComplete(JSONObject obj, final Object state);

    public void onFacebookError(FacebookError e, final Object state) {
      Log.e("FbConnect", "Facebook Error:" + e.getMessage());
    }

    public void onFileNotFoundException(FileNotFoundException e, final Object state) {
      Log.e("FbConnect", "Resource not found:" + e.getMessage());      
    }

    public void onIOException(IOException e, final Object state) {
      Log.e("FbConnect", "Network Error:" + e.getMessage());      
    }

    public void onMalformedURLException(MalformedURLException e, final Object state) {
      Log.e("FbConnect", "Invalid URL:" + e.getMessage());            
    }
  }
}

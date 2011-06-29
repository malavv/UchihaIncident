package com.ninja.exMenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

public class Dialogs {
  
  private static Activity activity_;
  private static View curWindow_;
  private static Dialogs instance_;
  
  private static AlertDialog alert_;
  
  public static Dialogs Get() { return instance_; }
  public static void Destroy() { instance_ = null; }
  
  public static void Create(Activity a) {
    activity_ = a;
    AlertDialog.Builder builder = new AlertDialog.Builder(activity_);
    builder.setPositiveButton("GTFO", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) { dialog.cancel(); }
           });
    alert_ = builder.create();
  }
  
  public static void Resume(Activity activity, View window) {
    if (curWindow_ != null)
      curWindow_.setOnLongClickListener(null);
    
    activity_ = activity;
    curWindow_ = window;
  }
  
  public static void Alert(CharSequence cs) {
    alert_.setTitle(cs);
    alert_.show();
  }  
}

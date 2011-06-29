package com.ninja.exMenu;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;

public class DialogManager {
  
  private static HashMap<Integer, Dialog> dialogs_ = new HashMap<Integer, Dialog>();
  private static int index_ = 0;
  
  private static Activity activity_;
  private static View curWindow_;
  private static int alertID_ = -1;
  
  public static boolean Exist(int dialogID) { return dialogs_.containsKey(dialogID); }
  public static int Push(Dialog d) { dialogs_.put(++index_, d);  return index_; }
  public static Dialog Get(int dialogID) { return dialogs_.get(dialogID); }

  public static void SetNewParent(Activity a, View window) {
    if (curWindow_ == window)  return;
    if (curWindow_ != null)  curWindow_.setOnLongClickListener(null);
    curWindow_ = window;
    activity_ = a;
  }
  
  public static void Alert(CharSequence cs) {
    if (alertID_ == -1) {
      AlertDialog.Builder builder = new AlertDialog.Builder(activity_);
      builder.setPositiveButton("GTFO", new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int id) { dialog.cancel(); }
             });
      alertID_ = Push(builder.create());
    }
    
    Dialog d = Get(alertID_);
    d.setTitle(cs);
    d.show();
  }  
}

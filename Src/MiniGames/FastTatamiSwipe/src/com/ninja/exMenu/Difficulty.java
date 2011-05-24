package com.ninja.exMenu;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SimpleAdapter;

public class Difficulty extends ListActivity {

  private GameContext info;
  
  //private ArrayList<Opponent> opponentList;
  
	/**
	 * @param 
	 */
  @Override
  public void onCreate(Bundle savedInstanceState) {
  	super.onCreate(savedInstanceState);
  	
  	
  	// Trouver le context global.
  	info = getIntent().getParcelableExtra("com.ninja.ExMenu.GameContext");
    setContentView(R.layout.difficulty);
    
    
  }
  
  private class OpponentAdaptor implements SimpleAdapter.ViewBinder {
    @Override
    public boolean setViewValue(View view, Object data,
        String textRepresentation) {
      return true;
    }
  }
  
//  public class OpponentsAdapter extends SimpleAdapter {
//
//    public OpponentsAdapter(Context context,
//        List<? extends Map<String, ?>> data, int resource, String[] from,
//        int[] to) {
//      super(context, data, resource, from, to);
//      // TODO Auto-generated constructor stub
//    }
//  }
  
//  public class OpponentAdapter extends ArrayAdapter<Opponent> {
//    
//    private Context mContext;
//    
//    public OpponentAdapter(Context context, int textViewResourceId) {
//      super(context, textViewResourceId);
//      mContext = context;
//    }
//    
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//      View row;
//   
//      if (null == convertView) {
//        LayoutInflater mInflater = getLayoutInflater();
//        row = mInflater.inflate(R.layout.opponents, null);
//      } else {
//        row = convertView;
//      }
//   
//      ImageView opImg = (ImageView) row.findViewById(R.id.op_img);
//      TextView opName = (TextView) row.findViewById(R.id.op_name);
//      TextView opDots = (TextView) row.findViewById(R.id.op_dots);
//      TextView opTime = (TextView) row.findViewById(R.id.op_time);
//      
//      opImg.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.levels));
//      opName.setText("Item 1");
//      opDots.setText("1");
//      opDots.setText("1700");
//   
//      return row;
//    }
     
//  }
}

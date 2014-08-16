package com.rubchick.marsrovers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class Main extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final EditText etPos = (EditText) findViewById(R.id.setPosAndDir);
        final EditText etExp = (EditText) findViewById(R.id.setExplore);

        Button run = (Button) findViewById(R.id.btnRun);
        run.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(!checkPosFormat(etPos.getText().toString() )
					|| !checkExploreFormat(etExp.getText().toString())
					) {
					
					errorToastMsg();
					
					return;
				}
				
				Intent intent = new Intent(Main.this, RoverBoard.class);
				
				String[] values = {etPos.getText().toString()
									, etExp.getText().toString()
									};
				
				intent.putExtra("values", values);
				startActivity(intent);
			}
        });
    }
    
    /// clears EditText control when it was clicked by user
    /// @param v
    public void doClick(View v) {
    	EditText tv = (EditText) v;
    	tv.setText("");
    }
    
    /// Checks if string with position has correct format
    /// @param pos
    /// @return true is string has correct format, false otherwise
    public boolean checkPosFormat(String pos) {
    	
    	if(pos.length() < 5)
    		return false;
    	
    	if(pos.charAt(0) < '0' 
			|| pos.charAt(0) > '9'
	    	|| pos.charAt(2) > '9'
	    	|| pos.charAt(2) > '9'
	    	|| pos.charAt(1) != ' '
	    	|| pos.charAt(3) != ' '
	    	|| pos.charAt(4) < 'A'
	    	|| pos.charAt(4) > 'Z'
    		) {
    		return false;
    	} 
    	
    	return true;
    }
    
    /// Checks if string with explore has correct format
    /// @param explore
    /// @return true is string has correct format, false otherwise
    public boolean checkExploreFormat(String explore) {
    	for(int i = 0; i < explore.length(); i++) {
    		if(explore.charAt(i) != 'L'
    			&& explore.charAt(i) != 'R'
    			&& explore.charAt(i) != 'M'
    			)
    			return false;
    	}
    	
    	return true;
    }
    
    /// shows toast message
    private void errorToastMsg() {
		Context context		= getApplicationContext();
		CharSequence text	= "Wrong format!";
		int duration 		= Toast.LENGTH_SHORT;

		Toast toast 		= Toast.makeText(context, text, duration);
		toast.show();
    }
}
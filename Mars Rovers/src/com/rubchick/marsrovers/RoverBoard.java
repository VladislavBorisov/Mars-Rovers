package com.rubchick.marsrovers;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.*;

public class RoverBoard extends Activity {
	
	public static final int CELLS_CNT		= 64;
	public static final int COLUMN_LENGTH	= 8;
	public static final int MILLIS_DELAY	= 1000;
	public static final int ROTATION_DEGREE	= 90;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);
        
        mRedrawHandler			= new RefreshHandler();
        final Context context	= getApplicationContext();
        
        db = ValuesDB.getInstance();
        db.setContext(context);
        db.open();
        
        db.createSession(Calendar.getInstance().getTime().toString());
        
	    Bundle extras = getIntent().getExtras();
	    String[] vals = extras.getStringArray("values");
	    
        /// get from 1st activity
        final String initPos	= vals[0];
        
        /// get from 1st activity
        final String route		= vals[1];
        
        final Rover rover = new Rover(initPos);

        rt		= route;
        rvr		= rover;

        iv = (ImageView) findViewById(R.id.imgRover);
        iv.setVisibility(View.INVISIBLE);
        
        final Bitmap bm = BitmapFactory
        		.decodeResource(context.getResources(), R.drawable.airplane);
        
        rotated = bm;
        
        if(bm!= null)
        	iv.setImageBitmap(bm);
        
        currentTextId = R.id.tv1 + rover.getXPos()*COLUMN_LENGTH 
        					+ rover.getYPos();
        setStartDirection(initPos.charAt(initPos.length() - 1 ), rotated);

        thread = new Thread(new Runnable() {
			@Override
			public void run() {
				
				while(idx < route.length() && flag == true) {
					
					computeStepCoords(route, rover);
					
					Message msg = mRedrawHandler.obtainMessage();
					
					msg.arg1 = prevTextId;
					msg.arg2 = currentTextId;
					msg.what = idx - 1;
					
					mRedrawHandler.sendMessage(msg);

					/// delay for every rover's step 
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						//e.printStackTrace();
					}
				}
			}
        });
        
        thread.start();
    }
    
    /// Inner class RefreshHandler for updating current layout
	class RefreshHandler extends Handler {
		
		@Override
		public void handleMessage(Message msg) {
			RoverBoard.this.updateUI(rt, rvr, msg.what, msg.arg1, msg.arg2);
		}
	};
    
	protected void onStop() {
		super.onStop();
		
		if(db != null)
			db.close();
		
		flag = false;
		
		if(!thread.isInterrupted())
			thread.interrupt();
	}
	
	/// sets rover's direction before its first move
    private void setStartDirection(char dir, final Bitmap src) {
    	if(dir == 'N')
    		rotated = rotate(src, ROTATION_DEGREE);
    	else if(dir == 'S')
    		rotated = rotate(src, -ROTATION_DEGREE);
    	else if(dir == 'W')
    		rotated = rotate(src, ROTATION_DEGREE*2);
    	else if(dir == 'E')
    		rotated = rotate(src, -ROTATION_DEGREE*2);
    	else
    		rotated = rotate(src, 0);
    	
    	iv.setImageBitmap(rotated);
    	
        TextView tvWithImg = (TextView) findViewById(currentTextId);
        tvWithImg.setBackgroundDrawable(iv.getDrawable());
    }
    
    /// rotates specified image
    /// @param src - Bitmap image
    /// @param degree
    /// @return rotatedBmp
    private Bitmap rotate(final Bitmap src, float degree) {
    	// create new matrix
    	Matrix matrix = new Matrix();
    	// setup rotation degree
    	matrix.postRotate(degree);

    	Bitmap rotatedBmp = Bitmap.createBitmap(src
    	    								, 0, 0
    	    								, src.getWidth(), src.getHeight()
    	    								, matrix
    	    								, true
    	    								);
    	
    	// free the native object associated with this bitmap
    	src.recycle();
    	
    	// return rotated bitmap
    	return rotatedBmp;
    }
    
    /// updates current layout
    /// @param route
    /// @param rover
	private void updateUI(final String	route
						, final Rover	rover
						, int index
						, int prevTextId
						, int currentTextId
						){
		if(!flag)
			return;
		
		if(prevTextId < R.id.tv1 || prevTextId >= R.id.tv1 + CELLS_CNT){
			return;
		}
		
		///////////////////////////////////////////////////////////////////////
		if(prevTextId != currentTextId) {
			TextView tvEmpty = (TextView) findViewById(prevTextId);
			tvEmpty.setBackgroundDrawable(null);
			
			if(prevTextId == R.id.tv1) {
				tvEmpty = (TextView) findViewById(R.id.tv1 + CELLS_CNT - 1);
				tvEmpty.setBackgroundDrawable(null);
			}
		}
		///////////////////////////////////////////////////////////////////////

		if(route.charAt(index) == 'L') {
			rotated = rotate(rotated, -ROTATION_DEGREE);
			iv.setImageBitmap(rotated);
		} else if(route.charAt(index) == 'R') {
			rotated = rotate(rotated, ROTATION_DEGREE);
			iv.setImageBitmap(rotated);
		}
		
		TextView tvWithImg = (TextView) findViewById(currentTextId);
		tvWithImg.setBackgroundDrawable(iv.getDrawable());
		
		String type = route.charAt(index) + "";
		String result = "prevTextId: " + prevTextId + ", currentTextId: " + currentTextId;
		
		db.createOperation(type, result);
	}
	
	private void computeStepCoords(String route
									, Rover rover
									) {
		
		prevTextId = currentTextId;
		
		rover.run(route.charAt(idx));
		
		currentTextId = R.id.tv1 + rover.getXPos()*COLUMN_LENGTH + rover.getYPos();
		
		idx++;
	}
    
	private int		prevTextId		= R.id.tv1;
	private int		currentTextId	= R.id.tv1;
	private int		idx 			= 0;
	private String	rt;
	
	private volatile boolean flag 	= true;
	private Thread	thread;
	
	
	private Bitmap		rotated;
	private ImageView	iv;
	
	private Rover		rvr;
	private ValuesDB	db;
	
	private RefreshHandler mRedrawHandler; 
}

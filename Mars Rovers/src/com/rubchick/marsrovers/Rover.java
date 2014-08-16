package com.rubchick.marsrovers;

public class Rover {
	public static final int ROTATION_DEGREE	= 90;
	
    public static final int X_POS			= 0;
    public static final int Y_POS			= 2;
    
    public static final int DIRECTION_POS	= 4;
    
    /// Constructor
    /// @param userData
    public Rover(final String userData){
    	parseStringData(userData);
    }
    
    /// move the rover
    /// @param moves
    public void run(final String moves){
        for(int i = 0; i < moves.length(); i++)
           oneStep( moves.charAt(i) );
    }
    
    /// move the rover
    /// @param toMove
    public void run(char toMove){
    	oneStep( toMove );
    }
 
    /// returns x position
    /// @return x
    public int getXPos(){
    	return x;
    }
    
    /// returns y position
    /// @return y
    public int getYPos() {
    	return y;
    }
    
    /// returns angle's degree
    /// @return angle
    public int getAngle() {
    	return angle;
    }
    
    /// one step of the rover
    /// @param step
    private void oneStep(char step){
    	
    	if(step == 'L')
    		setAngle(-ROTATION_DEGREE);
    	else if(step == 'R')
    		setAngle(ROTATION_DEGREE);
    	else
    		setCoordsWithAngle();
    }
	
    private void setAngle(int an) {
		angle += an;
		
		if(angle == 360 || angle == -360)
			angle = 0;
    }
    
    private void setCoordsWithAngle() {
		if(angle / 90 == 1 || angle / 90 == -1 )
			x += angle / 90;
		else if(angle / 270 == 1 || angle / 270 == -1 )
			x -= angle / 270;
		else if(angle == 0)
			y++;
		else if(angle / 180 == 1 || angle / 180 == -1)
			y--;
		
		if(y < 0)
			y = 0;
		if(x < 0)
			x = 0;
    }
    
    /// parse user input data from String
    /// @param str
    private void parseStringData(final String str) {
        if(str.charAt(X_POS + 1) != ' ' || str.charAt(Y_POS + 1) != ' ') {
        	return;
        }
        
        try {
        	x = Integer.parseInt(str.substring(X_POS, X_POS + 1));
        	y = Integer.parseInt(str.substring(Y_POS, Y_POS + 1));
        } catch(NumberFormatException e) {
        	
        }
        
        int tmp = 0;
        
        if(str.length() > DIRECTION_POS)
        	tmp = checkValue( str.charAt(DIRECTION_POS) );

        if(tmp == 'W')
        	angle = -90;
        else if(tmp == 'S')
        	angle = 180;
        else if(tmp == 'E')
        	angle = 90;
        else if(tmp == 'N')
        	angle = 0;
    }
    
    /// checks int value
    /// @param val
    /// @return tmp;
    private int checkValue(int val) {
    	int tmp = val;
    	
    	if(tmp < 0)
    		tmp = 0;
    	
    	return tmp;
    }
    
    private int x;
    private int y;
    
    private int angle;
}
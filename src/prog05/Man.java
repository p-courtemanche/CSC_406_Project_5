package prog05;

import processing.core.PApplet;
import processing.core.PGraphics;

//NEW
//	The Face class is now derived from GraphicObject
/** Graphic class to draw a man
 * 
 * @author jyh and PaigeCourtemanche
 *
 */
public class Man extends GraphicObject implements ApplicationConstants {
	
	public static final int LEFT_HAND = 0;
	public static final int RIGHT_HAND = 1;
	public static final int WHOLE_BODY = 2;
	public static final int NUM_OF_PARTS = 2;
	public static final int []PART_COLOR =  {	0xFF00FF00,	//	LEFT_HAND
												0xFF00FF00,	//	RIGHT_HAND
												0xFF00FF00,	//	WHOLE_BODY
											};
	
	public final static int DO_NOTHING = 0;
	public final static int JUMPING = 1;
	public final static int WALKING_LEFT = 2;
	public final static int WALKING_RIGHT = 3;
	
	public static final float TORSO_Y2 = -2;
	
	public static final float LEFT_LEG_X2 = TORSO_Y2/2;
	public static final float RIGHT_LEG_X2 = -LEFT_LEG_X2;
	
	public static final float LEFT_ARM_X2 = 0.7f*TORSO_Y2;
	public static final float RIGHT_ARM_X2 = -LEFT_ARM_X2;

	public static final float LEG_LENGTH = 0.7f*TORSO_Y2;
	public static final float ARM_LENGTH = -2*LEG_LENGTH/3;
	
	public static final float HAND_DIAMETER = 0.3f*TORSO_Y2;
	public static final float LEFT_HAND_X = LEFT_ARM_X2;
	public static final float RIGHT_HAND_X = RIGHT_ARM_X2;
	public static final float HAND_Y = ARM_LENGTH*2;
	
	public static final float NECK_Y2 = -1.4f*TORSO_Y2;
	public static final float HEAD_DIAMETER = 0.8f*TORSO_Y2;
	
	public final static float ANGLE_INCR = 0.1f;
	
	private KeyframeInterpolator jump;
	private KeyframeInterpolator walkRight;
	private KeyframeInterpolator walkLeft;
	
	private int state_;
	private float animTime_;
							   //L thigh  L calf  R thigh  R calf  L lower  L upper  R lower  R upper
    private float[] theta = {0,  -0.2f  , 0.2f ,  0.2f  , -0.2f ,  -0.9f  ,  0.3f ,  0.9f  , -0.3f };
	
							    // y  |----------------------leg angles---------------------| time
	float [][]jumpKeyFrames = {{ 0.0f, -0.2f, 0.2f, 0.2f, -0.2f, -0.9f,  0.3f,  0.9f,  -0.3f, 0.0f},  // resting
							   {-0.5f, -1.0f, 1.2f, 1.0f, -1.2f, -0.9f,  0.3f,  0.9f,  -0.3f, 0.1f},  // prepare to jump
							   { 1.0f, -0.6f, 0.7f, 0.6f, -0.7f, -0.5f,  0.0f,  0.5f,   0.0f, 0.2f},  // jump
						       { 2.0f, -0.2f, 0.2f, 0.2f, -0.2f, -0.3f,  0.0f,  0.3f,   0.0f, 0.4f},  // reach highest point
							   { 1.0f, -0.6f, 0.7f, 0.6f, -0.7f, -0.5f,  0.0f,  0.5f,   0.0f, 0.6f},  // coming down
							   {-0.3f, -1.0f, 1.2f, 1.0f, -1.2f, -0.9f,  0.3f,  0.9f,  -0.3f, 0.7f},  // landing
							   { 0.0f, -0.6f, 0.6f, 0.6f, -0.6f, -0.9f,  0.3f,  0.9f,  -0.3f, 0.8f},  // bring legs back to resting point
							   { 0.0f, -0.2f, 0.2f, 0.2f, -0.2f, -0.9f,  0.3f,  0.9f,  -0.3f, 0.9f}}; // neutral stance
	
								    //vx  |----------------------leg angles----------------------| time
	float [][]walkRightKeyFrames = {{ 1.0f, -0.2f,  0.2f,  0.2f, -0.2f, -0.9f,  0.3f,  0.9f, -0.3f, 0.0f}, // start in neutral
								    { 1.5f,  0.0f, -1.1f,  0.0f, -0.2f, -1.7f,  0.0f,  1.7f,  0.0f, 0.1f}, // right foot step to the left
								    { 2.0f,  0.6f, -1.0f, -0.2f, -0.3f, -2.3f,  0.0f,  2.3f,  0.0f, 0.2f},
								    { 2.0f,  1.2f, -0.8f, -0.4f, -0.3f, -3.0f,  0.0f,  3.0f,  0.0f, 0.4f},
							        { 3.0f,  0.8f, -0.5f, -0.2f, -0.2f, -3.6f,  0.0f,  3.6f,  0.0f, 0.5f}, 
							        { 3.0f,  0.5f, -0.3f, -0.2f, -0.2f, -3.6f,  0.0f,  3.6f,  0.0f, 0.6f}, 
							        { 2.0f, -0.3f, -0.2f,  0.1f, -0.0f, -3.0f,  0.0f,  3.0f,  0.0f, 0.7f}, 
							        { 1.5f, -0.2f,  0.2f,  0.2f, -0.0f, -2.3f,  0.0f,  2.3f,  0.0f, 0.8f},
							        { 1.0f, -0.2f,  0.2f,  0.2f, -0.2f, -1.7f,  0.0f,  1.7f,  0.0f, 0.9f}, 
								    { 0.0f, -0.2f,  0.2f,  0.2f, -0.2f, -0.9f,  0.3f,  0.9f,  -0.3f, 1.0f}}; // end in neutral
	
								   // vx   |----------------------leg angles----------------------| time
	float [][]walkLeftKeyFrames = {{ -1.0f, -0.2f,  0.2f,  0.2f, -0.2f, -0.9f,  0.3f,  0.9f, -0.3f, 0.0f}, // start in neutral
								   { -1.5f,  0.0f,  0.2f, -0.6f,  1.1f, -1.7f,  0.0f,  1.7f,  0.0f, 0.1f}, // left foot step right
								   { -2.0f,  0.2f,  0.3f, -1.2f,  1.0f, -2.3f,  0.0f,  2.3f,  0.0f, 0.2f}, 
								   { -2.0f,  0.4f,  0.3f, -0.8f,  0.8f, -3.0f,  0.0f,  3.0f,  0.0f, 0.4f}, 
							       { -3.0f,  0.2f,  0.2f, -0.5f,  0.5f, -3.6f,  0.0f,  3.6f,  0.0f, 0.5f}, 
							       { -3.0f,  0.0f,  0.2f,  0.3f,  0.3f, -3.6f,  0.0f,  3.6f,  0.0f, 0.6f}, 
							       { -2.0f, -0.1f,  0.0f,  0.2f, 0.2f,  -3.0f,  0.0f,  3.0f,  0.0f, 0.7f},
							       { -1.5f, -0.2f,  0.0f,  0.2f, -0.2f, -2.3f,  0.0f,  2.3f,  0.0f, 0.8f},
							       { -1.0f, -0.2f,  0.2f,  0.2f, -0.2f, -1.7f,  0.0f,  1.7f,  0.0f, 0.9f}, 
						     	   {  0.0f, -0.2f,  0.2f,  0.2f, -0.2f, -0.9f,  0.3f,  0.9f, -0.3f, 1.0f}}; // end in neutral

	private BoundingBox []relativeBox_;

	/** Constructor
	 * 
	 */
	public Man() {
		super();
		
		// these are zero so he isn't spinning around or moving across the screen.
		vx_ = 0;
		vy_ = 0;
		spin_ = 0;
		
		jump = new LinearKeyframeInterpolator(jumpKeyFrames);
		walkLeft = new LinearKeyframeInterpolator(walkLeftKeyFrames);
		walkRight = new LinearKeyframeInterpolator(walkRightKeyFrames);
		
		//	Create the relative bounding boxes
		relativeBox_ = new BoundingBox[NUM_OF_PARTS+1];
		relativeBox_[LEFT_HAND] = new BoundingBox(LEFT_HAND_X - HAND_DIAMETER/2, 	//	xmin
												 LEFT_HAND_X + HAND_DIAMETER/2,	//	xmax
												 HAND_Y - HAND_DIAMETER/2, 	//	ymin
												 HAND_Y + HAND_DIAMETER/2, 	//	ymax
												 PART_COLOR[LEFT_HAND]);
		relativeBox_[RIGHT_HAND] = new BoundingBox(RIGHT_HAND_X - HAND_DIAMETER/2, 	//	xmin
												  RIGHT_HAND_X + HAND_DIAMETER/2,		//	xmax
												  HAND_Y - HAND_DIAMETER/2, 	//	ymin
												  HAND_Y + HAND_DIAMETER/2, 	//	ymax
												  PART_COLOR[RIGHT_HAND]);
		relativeBox_[WHOLE_BODY] = new BoundingBox( -ARM_LENGTH*2, 	//	leftmost hand
													ARM_LENGTH*2,	//	rightmost hand
													TORSO_Y2 + NECK_Y2 + HEAD_DIAMETER, //	top of head
													TORSO_Y2 + LEG_LENGTH*2, 	//	bottom of feet
													PART_COLOR[WHOLE_BODY]);
		
		//	create the absolute boxes
		absoluteBox_ = new BoundingBox[NUM_OF_PARTS+1];
		
		//	So here we first create the boxes, and then "update" their state.
		for (int k=0; k<= NUM_OF_PARTS; k++) {
			absoluteBox_[k] = new BoundingBox(PART_COLOR[k]);
		}
		updateAbsoluteBoxes_();
	}

	/**	renders the bounding boxes
	 * 
	 * @param g		    The Processing application in which the action takes place
	 * @param boxMode	the type of box that is being drawn, if any 
	 */
	public void draw(PGraphics g, BoundingBoxMode boxMode) {
		//	Invokes method declared in the parent class, that draws the object
		draw_(g);
		
		//	Then draw the boxes, if needed

		if (boxMode == BoundingBoxMode.RELATIVE_BOX) {
			// we use this object's instance variable to access the application's instance methods and variables
			g.pushMatrix();

			g.translate(x_,  y_);
			g.rotate(angle_);		

			for (BoundingBox box : relativeBox_)
				box.draw(g);
			
			g.popMatrix();	
		}
		
		else if (boxMode == BoundingBoxMode.ABSOLUTE_BOX) {
			for (BoundingBox box : absoluteBox_)
				if (box != null)
					box.draw(g);
			
		}
	}

	/** draws all quadrants
	 * 
	 * @param g         The Processing application in which the action takes place
	 * @param boxMode   the type of box that is being drawn, if any 
	 */
	public void drawAllQuadrants(PGraphics g, BoundingBoxMode boxMode) {
//		draw(g, boxMode);
//		
//		if (shouldGetDrawn_[NORTH_WEST]) {
//			g.pushMatrix();
//			g.translate(XMIN-XMAX, YMIN-YMAX);
//			draw(g, boxMode);
//			g.popMatrix();
//		}
//		if (shouldGetDrawn_[NORTH]) {
//			g.pushMatrix();
//			g.translate(0, YMIN-YMAX);
//			draw(g, boxMode);
//			g.popMatrix();
//		}
//		if (shouldGetDrawn_[NORTH_EAST]) {
//			g.pushMatrix();
//			g.translate(XMAX-XMIN, YMIN-YMAX);
//			draw(g, boxMode);
//			g.popMatrix();
//		}
//		if (shouldGetDrawn_[EAST]) {
//			g.pushMatrix();
//			g.translate(XMAX-XMIN, 0);
//			draw(g, boxMode);
//			g.popMatrix();
//		}
//		if (shouldGetDrawn_[SOUTH_EAST]) {
//			g.pushMatrix();
//			g.translate(XMAX-XMIN, YMAX-YMIN);
//			draw(g, boxMode);
//			g.popMatrix();
//		}
//		if (shouldGetDrawn_[SOUTH]) {
//			g.pushMatrix();
//			g.translate(0, YMAX-YMIN);
//			draw(g, boxMode);
//			g.popMatrix();
//		}
//		if (shouldGetDrawn_[SOUTH_WEST]) {
//			g.pushMatrix();
//			g.translate(XMIN-XMAX, YMAX-YMIN);
//			draw(g, boxMode);
//			g.popMatrix();
//		}
//		if (shouldGetDrawn_[WEST]) {
//			g.pushMatrix();
//			g.translate(XMIN-XMAX, 0);
//			draw(g, boxMode);
//			g.popMatrix();
//		}
	}


	/**	renders the Man object
	 * 
	 * @param g	   The Processing application in which the action takes place
	 */
	public void draw_(PGraphics g) {
		g.pushMatrix();	
		g.strokeWeight(0.2f);
	   
	    // lower body
		g.pushMatrix();
	    g.line(0, 0, 0, TORSO_Y2);
		                  
	    // left thigh
	    g.translate(0, TORSO_Y2);
	    g.pushMatrix();

	    g.rotate(theta[1]);
	    g.line(0, 0, 0, LEG_LENGTH);
	    g.translate(0, LEG_LENGTH);
	    
	    
	    // left calf
	    g.rotate(theta[2]);
        g.line(0, 0, 0, LEG_LENGTH);
        g.popMatrix();
	    
	                  
	    // right thigh
        g.pushMatrix();
        g.rotate(theta[3]);
	    g.line(0, 0, 0, LEG_LENGTH); 
        g.translate(0, LEG_LENGTH); 
	    
	    // right calf
	    g.rotate(theta[4]);
        g.line(0, 0, 0, LEG_LENGTH);
        g.popMatrix();
 	    
	    g.popMatrix();
	           
	    // right upper arm
	    g.pushMatrix();
	    g.rotate(theta[5]);
        g.line(0, 0, 0, ARM_LENGTH); 
	    
	    // right forearm
	    g.pushMatrix();
        g.translate(0, ARM_LENGTH);
	    g.rotate(theta[6]);
	    g.line(0, 0, 0, ARM_LENGTH); 

	    //right hand
	    g.pushMatrix();
        g.translate(0, ARM_LENGTH);
        g.ellipse(0, 0, HAND_DIAMETER, HAND_DIAMETER);
        g.popMatrix();
	    
	    g.popMatrix();
	    
	    g.popMatrix();
	          
	    // left upper arm
	    g.pushMatrix();
	    g.rotate(theta[7]);
	    g.line(0, 0, 0, ARM_LENGTH); 
	    
	    // left forearm
	    g.translate(0, ARM_LENGTH);
	    g.rotate(theta[8]);
	    g.line(0, 0, 0, ARM_LENGTH); 
	    
	    //left hand
	    g.pushMatrix();
        g.translate(0, ARM_LENGTH);
        g.ellipse(0, 0, HAND_DIAMETER, HAND_DIAMETER);
        g.popMatrix();	    

        g.popMatrix();
	                  
	    // neck
	    g.line(0, 0, 0, NECK_Y2); 
	    
	    // head
	    g.pushMatrix();
	    g.translate(0, NECK_Y2);
	    g.ellipse(0, 0, HEAD_DIAMETER, HEAD_DIAMETER);
	    g.popMatrix();
	    
	    g.popMatrix();
	}
	
	/** The man takes a step to the left
	 * 
	 */
	public void walkLeft() {
		if (state_ == DO_NOTHING) {
			state_ = WALKING_LEFT;
			animTime_ = 0;
		}
	}
	
	/** The man takes a step to the right
	 * 
	 */
	public void walkRight() {
		if (state_ == DO_NOTHING) {
			state_ = WALKING_RIGHT;
			animTime_ = 0;
		}
	}
	
	/** The man jumps and raises his hands
	 * 
	 */
	public void jump() {
		if (state_ == DO_NOTHING) {
			state_ = JUMPING;
			animTime_ = 0;
		}
	}
	
	/** This function updates the man and changes his position based on what state he is in
	 *  It also sets his current state back to neutral at the end of each state
	 * 
	 * @param dt   how much time has gone by
	 */
	public void update(float dt) {		
		// controls where the man moves to
		x_ += vx_ * dt;
		y_ += vy_ * dt;
		
		switch (state_) {
		
		case JUMPING:
			animTime_ += dt;
			
			float []stateVect = jump.computeStateVector(animTime_);
			
			// when the animation completes, the man reverts to the neutral state
			if (jump.animationIsOver(animTime_)) 
				state_ = DO_NOTHING;
			
			// controls how fast the man jumps up
			y_ = stateVect[0];
			
			// each joint is updated according to the animation keyframes
			for(int i = 0; i < theta.length; ++i){
				theta[i] = stateVect[i];
			}
			break;
			
		
		case WALKING_LEFT:
			animTime_ += dt;
			
			float []stateVector = walkLeft.computeStateVector(animTime_);

			// when the animation completes, the man reverts to the neutral state
			if (walkLeft.animationIsOver(animTime_)) 
				state_ = DO_NOTHING;
			
			// controls how fast the man walks left
			vx_ = stateVector[0];
			
			// each joint is updated according to the animation keyframes
			for(int i = 0; i < theta.length; ++i){
				theta[i] = stateVector[i];
			}
			break;
			
		case WALKING_RIGHT:
			animTime_ += dt;
			
			float []stateVec = walkRight.computeStateVector(animTime_);

			// when the animation completes, the man reverts to the neutral state
			if (walkRight.animationIsOver(animTime_)) 
				state_ = DO_NOTHING;
			
			// controls how fast the man walks right
			vx_ = stateVec[0];
			
			// each joint is updated according to the animation keyframes
			for(int i = 0; i < theta.length; ++i){
				theta[i] = stateVec[i];
			}
			break;
		}
		
	}
	
	/**
	 * 	Computes the new dimensions of the object's absolute bounding boxes, for
	 * the object's current position and orientation.
	 */
	protected void updateAbsoluteBoxes_() {
//		float cA = PApplet.cos(angle_), sA = PApplet.sin(angle_);
//		float 	centerLeftEarX = x_ + cA*LEFT_EAR_X - sA*LEFT_EAR_Y,
//				centerLeftEarY = y_ + cA*LEFT_EAR_Y + sA*LEFT_EAR_X,
//				centerRightEarX = x_ + cA*RIGHT_EAR_X - sA*RIGHT_EAR_Y,
//				centerRightEarY = y_ + cA*RIGHT_EAR_Y + sA*RIGHT_EAR_X;
//				
//		
//		absoluteBox_[LEFT_EAR].updatePosition(centerLeftEarX - EAR_DIAMETER/2,	//	xmin
//											  centerLeftEarX + EAR_DIAMETER/2,	//	xmax
//											  centerLeftEarY - EAR_DIAMETER/2,	//	ymin
//											  centerLeftEarY + EAR_DIAMETER/2);	//	ymax
//		absoluteBox_[RIGHT_EAR].updatePosition(centerRightEarX - EAR_DIAMETER/2,	//	xmin
//											   centerRightEarX + EAR_DIAMETER/2,	//	xmax
//											   centerRightEarY - EAR_DIAMETER/2,	//	ymin
//											   centerRightEarY + EAR_DIAMETER/2);	//	ymax
//		absoluteBox_[FACE].updatePosition(x_ - FACE_DIAMETER/2,		// xmin
//										  x_ + FACE_DIAMETER/2,		//	xmax
//										  y_ - FACE_DIAMETER/2,		//	ymin
//										  y_ + FACE_DIAMETER/2);	//	ymax)
//
//		absoluteBox_[HEAD].updatePosition(	PApplet.min(absoluteBox_[LEFT_EAR].getXmin(),
//														 absoluteBox_[RIGHT_EAR].getXmin(),
//														 absoluteBox_[FACE].getXmin()),	// xmin
//											PApplet.max(absoluteBox_[LEFT_EAR].getXmax(),
//													 absoluteBox_[RIGHT_EAR].getXmax(),
//													 absoluteBox_[FACE].getXmax()),	// xmax
//											PApplet.min(absoluteBox_[LEFT_EAR].getYmin(),
//													 absoluteBox_[RIGHT_EAR].getYmin(),
//													 absoluteBox_[FACE].getYmin()),	// ymin
//											PApplet.max(absoluteBox_[LEFT_EAR].getYmax(),
//													 absoluteBox_[RIGHT_EAR].getYmax(),
//													 absoluteBox_[FACE].getYmax()));	// xmax;
//		
	}


	/**	Performs a hierarchical search to determine whether the point received
	 * as parameter is inside the face.  OK, it's a pretty simple hierarchy, being
	 * only one level deep, but it gives us a chance to think about the problem.
	 * 
	 * @param x		x coordinate of a point in the world reference frame
	 * @param y		y coordinate of a point in the world reference frame
	 * @return	true if the point at (x, y) lies inside this face object.
	 */
	public boolean isInside(float x, float y) {
		//	If the point is inside the global absolute bounding box
		if (relativeBox_[LEFT_HAND].isInside(x, y) == true || relativeBox_[RIGHT_HAND].isInside(x, y) == true) {
			//	test if the point is inside one of the sub-boxes
			//	Remember that Java (like C, C++, Python) uses lazy evaluation,
			//	so as soon as one test returns true, the evaluation ends.
			return true;
		}

		return false;
	}
}

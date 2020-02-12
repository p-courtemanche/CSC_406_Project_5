package prog05;
import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

/*--------------------------------------------------------------------------+
|	Keyboard commands:														|
|		• ' ' makes the man jump											|
|	    • LEFT makes the man walk left  									|
|	    • RIGHT makes the man walk right						            |
|		• 'b' set the animation mode to "BOX_WORLD"							|
|		• 'c' set the animation mode to "CYLINDER_WORLD"					|
|		• 'f' displays the objects' reference frames						|
|		• 'q' displays the objects' absolute bounding boxes					|
|		• 'r' displays the objects' relative bounding boxes					|
|		• 'n' disables display of the objects' bounding boxes				|
|		• 'a' makes the absolute boxes show 								|	
|																			|
|																			|
|																			|
|	Jean-Yves Hervé, Nov. 2012 (version for design grad students).			|
|					 Revised Nov. 2019 for CSC406.							|
|				Revised Nov. 2019 by Paige Courtemanche						|
+--------------------------------------------------------------------------*/


/** This draws and updates all the objects
 *  The user can interact with the application 
 * 
 * @author jyh and edited by paigecourtemanche
 *
 */
public class MainClass extends PApplet implements ApplicationConstants 
{
	//-----------------------------
	//	graphical objects
	//-----------------------------
	KeyframedMan man_; // the single man
	//ArrayList<GraphicObject> manList_;
	ArrayList<GraphicObject> ellipseList_; // the list of ellipses

	//-----------------------------
	//	Various status variables
	//-----------------------------
	/**	Desired rendering frame rate
	 * 
	 */
	static final float RENDERING_FRAME_RATE = 60;
	
	/**	Ratio of animation frames over rendering frames 
	 * 
	 */
	static final int ANIMATION_RENDERING_FRAME_RATIO = 5;
	
	/**	computed animation frame rate
	 * 
	 */
	static final float ANIMATION_FRAME_RATE = RENDERING_FRAME_RATE * ANIMATION_RENDERING_FRAME_RATIO;
	
	
	/**	Variable keeping track of the last time the update method was invoked.
	 * 	The different between current time and last time is sent to the update
	 * 	method of each object. 
	 */
	int lastUpdateTime_;
	
	/**	A counter for animation frames
	 * 
	 */
	int frameCount;

	
	long frame_ = 0L;
	BoundingBoxMode boxMode_ = BoundingBoxMode.NO_BOX;
	AnimationMode animationMode_ = AnimationMode.BOX_WORLD;
	boolean drawRefFrame_ = false;
	boolean animate_ = true;
	
	//	Instead of rendering directly in the frame, we will draw in this object
	PGraphics offScreenBuffer_;
	boolean doDoubleBuffer = false;
	PGraphics lastBuffer_;
	
	PImage img; // The image that fills the ellipses is defined here
	

	/** This is the settings function where the window is defined
	 * 
	 */
	public void settings() {
		//  dimension of window in pixels
		size(WINDOW_WIDTH, WINDOW_HEIGHT);
	}

	/** The world is setup and the man and ellipses are created
	 * 
	 */
	public void setup() {    
		img = loadImage("jamMan.jpg"); // The actual image is set to the variable image
		
		if (BAIL_OUT_IF_ASPECT_RATIOS_DONT_MATCH) {
			if (Math.abs(WORLD_HEIGHT - PIXEL_TO_WORLD*WINDOW_HEIGHT) > 1.0E5) {
				System.out.println("World and Window aspect ratios don't match");
				System.exit(1);
			}
		}
		
		frameRate(ANIMATION_FRAME_RATE);
		frameCount = 0;

		//manList_ = new ArrayList<GraphicObject>();
		ellipseList_ = new ArrayList<GraphicObject>();
		
		//				  	 	       x       y  alpha   w   h    color       vx    vy       spin
		//	I allocate my off-screen buffer at the same dimensions as the window
		offScreenBuffer_ = createGraphics(width, height);
		
							 	//	   x    y    theta   red   green   t
		float [][]manKeyFrames = { {  0,   0,  0.f,  255,  255,   0},		//	frame 0 [center, red]
								   {  0,   0,  0.f,  255,    0,   2},		//	frame 1 [center, red]
								   {-15,  -5,  0.f,    0,  255,   6},		//	frame 2 [upper-left, green]
								   {-10,  -5,  0.f,  255,  255,   8},		//	frame 3 [upper-left, yellow]
								   {-10,   0,  0.f,  255,  127,  12},		//	frame 4 [upper-right, orange]
								   {-10,  -5,  0.f,  127,  127,  22},		//	frame 5 [lower-right, khaki] 
								   {+15,  -5,  0.f,  255,    0,  30}};		//	frame 6 [lower-right, red]
		
		LinearKeyframeInterpolator manInterpolator = new LinearKeyframeInterpolator(manKeyFrames);
		man_ = new KeyframedMan(manInterpolator);
		
		for (int k=0; k<3; k++) {
			ellipseList_.add(new AnimatedEllipse(img, 5)); //list of ellipses
		}
	}

	/** Processing sketch rendering callback function
	 * 
	 */
	public void draw() {
		//================================================================
		//	Only render one frame out of ANIMATION_RENDERING_FRAME_RATIO
		//================================================================
		if (frameCount % ANIMATION_RENDERING_FRAME_RATIO == 0) {
			PGraphics gc;
			if (doDoubleBuffer) {
				//	I say that the drawing will take place inside of my off-screen buffer
				gc = offScreenBuffer_;
				offScreenBuffer_.beginDraw();
			}
			else
				gc = this.g;

			gc.background(127);

			// define world reference frame:  
			//    Origin at windows's center and 
			//    y pointing up
			//    scaled in world units
			gc.translate(WORLD_X, WORLD_Y); 
			gc.scale(DRAW_IN_WORLD_UNITS_SCALE, -DRAW_IN_WORLD_UNITS_SCALE);

			if (drawRefFrame_)
				drawReferenceFrame(gc);

			if (animationMode_ == AnimationMode.BOX_WORLD) {
				//  Since the Man and AnimatedEllipse classes are derived from GraphicalObject, 
				//  their draw methods take as argument a reference to the gc.
				man_.draw(gc);
					
				for (GraphicObject ellipse : ellipseList_)
					ellipse.draw(gc);
					
			} 
			
			if (doDoubleBuffer) {
				offScreenBuffer_.endDraw();

				image(offScreenBuffer_, 0, 0);				
	
				//	For some reason this doesn't work and I don't understand why.
				lastBuffer_.beginDraw();
				lastBuffer_.image(offScreenBuffer_, 0, 0);
				lastBuffer_.endDraw();

				int []pixelLB = lastBuffer_.pixels;
				int []pixelOB = offScreenBuffer_.pixels;
				for (int k=0; k<width*height; k++)
					pixelLB[k] = pixelOB[k];

				
				lastBuffer_.updatePixels();
			}
		}

		//  and then update their state
		if (animate_) {
			update();
		}
		
		frameCount++;
	}
	
	/** Updates the locations of the objects based on how much time has passed
	 * 
	 */
	public void update() {

		int time = millis();

		if (animate_) {
			//  update the state of the objects ---> physics
			float dt = (time - lastUpdateTime_)*0.001f;
			
			man_.update(dt);
			
//			for (GraphicObject obj : manList_) {
//				obj.update(animationMode_, dt);
//			}
			
			for (GraphicObject obj : ellipseList_) {
				obj.update(dt);
			}
		}

		lastUpdateTime_ = time;
	}
	
	/** Draws the reference frame
	 * 
	 * @param g  The Processing application in which the action takes place
	 */
	private void drawReferenceFrame(PGraphics g) {
		g.strokeWeight(PIXEL_TO_WORLD);
		g.stroke(255, 0, 0);
		g.line(0, 0, WORLD_WIDTH/20, 0);
		g.stroke(0, 255, 0);
		g.line(0, 0, 0, WORLD_WIDTH/20);
	}

	/** Processing callback function for keyboard events
	 *  See the top of file to see what each key press means
	 */
	public void keyReleased() {
		if (key == CODED) {
			switch(keyCode) {
			case LEFT:
				man_.walkLeft();
				break;
			case RIGHT:
				man_.walkRight();
				break;
			}
		} else switch(key) {
			case ' ':
				man_.jump();
				break;
			case 'z':
				animate_ = !animate_;
				if (animate_)
					lastUpdateTime_ = millis();
				break;
				
			case 'n':
				boxMode_ = BoundingBoxMode.NO_BOX;
				GraphicObject.setBoundingBoxMode(boxMode_);;
				break;
	
			case 'r':
				boxMode_ = BoundingBoxMode.RELATIVE_BOX;
				GraphicObject.setBoundingBoxMode(boxMode_);;
				break;
	
			case 'a':
				boxMode_ = BoundingBoxMode.ABSOLUTE_BOX;
				GraphicObject.setBoundingBoxMode(boxMode_);;
				break;
	
			case 'f':
				drawRefFrame_ = !drawRefFrame_;
				break;
	
			case 'b':
				animationMode_ = AnimationMode.BOX_WORLD;
				break;
	
			case 'c':
				animationMode_ = AnimationMode.CYLINDER_WORLD;
				break;
	
			case 'd':
				doDoubleBuffer = !doDoubleBuffer;
				break;
				
			default:
				break;
		}
	}

	public static void main(String[] argv) {
		PApplet.main("prog05.MainClass");
	}

}

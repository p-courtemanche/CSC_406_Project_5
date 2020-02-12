package prog05;

import processing.core.PGraphics;

/** A class that creates bounding boxes
 * 
 * @author jyh
 *
 */
public class BoundingBox implements ApplicationConstants {
	private float xmin_, xmax_, ymin_, ymax_;
	private int color_;
	
	/**	Creates a new bounding box at set dimensions and color.
	 * Note: Should verify that xmin ≤ xmax and ymin ≤ ymax
	 * 
	 * @param xmin	left bound of the box
	 * @param xmax	right bound of the box
	 * @param ymin	lower bound of the box
	 * @param ymax	upper bound of the box
	 * @param color	color of the box's contour
	 */
	public BoundingBox(float xmin, float xmax, float ymin, float ymax, int color) {
		xmin_ = xmin;
		xmax_ = xmax;
		ymin_ = ymin;
		ymax_ = ymax;
		color_ = color;
	}

	/**	Creates an empty bounding box with the specified contour color
	 * 
	 * @param color	color of the box's contour
	 */
	public BoundingBox(int color) {
		color_ = color;
	}

	/**	Creates an empty black box
	 * 
	 */
	public BoundingBox() {
		xmin_ = xmax_ = ymin_ = ymax_ = 0.f;
		color_ = 0xFF000000;
	}

	/** updates the positions of all the mins and maxes
	 * 
	 * @param xmin  minimum x-value of the box
	 * @param xmax  maximum x-value of the box
	 * @param ymin  minimum y-value of the box
	 * @param ymax  maximum y-value of the box
	 */
	public void updatePosition(float xmin, float xmax, float ymin, float ymax) {
		xmin_ = xmin;
		xmax_ = xmax;
		ymin_ = ymin;
		ymax_ = ymax;
	}

	/** gets the minimum x-value 
	 * 
	 * @return  the minimum x-value
	 */
	public float getXmin() {
		return xmin_;
		
	}
	
	/** gets the maximum x-value 
	 * 
	 * @return maximum x-value 
	 */
	public float getXmax() {
		return xmax_;
		
	}
	
	/** gets the minimum y-value 
	 * 
	 * @return  minimum y-value 
	 */
	public float getYmin() {
		return ymin_;
		
	}
	
	/** gets the minimum x-value 
	 * 
	 * @return  minimum x-value 
	 */
	public float getYmax() {
		return ymax_;
		
	}
	
	
	/** Checks to see if a set of coordinates are within the minimum and maximum bounds
	 * 
	 * @param x  x-coordinate being checked
	 * @param y  y-coordinate being checked
	 * @return   the bool value, returns false if the value is outside the ellipse, returns true otherwise
	 */
	public boolean isInside(float x, float y) {		
		return ((x>=xmin_) && (x<=xmax_) && (y>=ymin_) && (y<=ymax_));
	}
	
	/** draws the bounding boxes
	 * 
	 * @param g The Processing application in which the action takes place
	 */
	public void draw(PGraphics g) {
		g.strokeWeight(1.0f*PIXEL_TO_WORLD);
		g.stroke(color_);
		g.noFill();
		g.rect(xmin_, ymin_, xmax_-xmin_, ymax_-ymin_);
	}
}

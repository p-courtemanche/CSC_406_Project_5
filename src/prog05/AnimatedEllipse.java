package prog05;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

/**	The AnimatedEllipse class written as a subclass of Graphic Object
 * 
 * @author PaigeCourtemanche and jyh
 *
 */
public class AnimatedEllipse extends GraphicObject {
	float scale_;
	PImage copy_;
	
	/**	Constructor. Initializes all instance variables to the values set by the arguments
	 * 
	 * @param img
	 * @param ellipseWidth
	 */
	public AnimatedEllipse(PImage img, float ellipseWidth) {
		super();
		width_ = ellipseWidth;
		height_ = (int)(img.height * ellipseWidth/img.width);
		spin_ = 0.3f;
		setupDefaultBoundingBoxes_();
		scale_ = ellipseWidth/img.width;
		copy_ = img.copy();
		copy_.loadPixels();
		
		//every pixel in the image that is outside the bounds of the ellipse becomes transparent
		for (int i = 0; i < copy_.height; i++) {
			for (int j = 0; j < copy_.width; j++){ 
				if (checkInside(j, i) == false) {
					copy_.pixels[i*copy_.pixelWidth + j] = 0x01FFFFFF; //1 is the minimum transparency, so the image will be full transparent
				}
			}
		}
		copy_.updatePixels();
	}
	
	

	/**	Rendering code specific to ellipses
	 * 
	 * @param g	The Processing application in which the action takes place
	 */
	protected void draw_(PGraphics g) {
		// the ellipse is filled with a color if there is no image
		if (copy_  == null) {
			g.ellipse(0,  0,  width_, height_);
		// if there is an image, it is applied to the ellipse
		} else {
			g.pushMatrix();
			g.scale(scale_, scale_);
			g.image(copy_, -copy_.width/2, -copy_.height/2,  copy_.width, copy_.height);
			g.popMatrix();
		}
	}

	/** updates the Absolute Boxes of the ellipse
	 * 
	 */
	protected void updateAbsoluteBoxes_() {
            // could definitely be optimized
			float cA = PApplet.cos(angle_), sA = PApplet.sin(angle_);
			float hwidth = width_/2, hheight = height_/2;
			float []cornerX;
			float []cornerY;
			
			//----------------------------------------------
			//	General case first
			//----------------------------------------------
			if (Math.abs(cA) > 1E-4 && Math.abs(sA)> 1E-4) {
				//	parametric equation of the ellipse is {w/2 cos(t), h/2 sin(t), 0≤t≤2π

				//	Compute the values of t that give us horizontal and vertical tangents
				float tV = (float) Math.atan(-(height_*sA)/(width_*cA));
				float tH = (float) Math.atan((height_*cA)/(width_*sA));
				
				float dxH = (float) (cA*hwidth*Math.cos(tH) - sA*hheight*Math.sin(tH));
				float dyH = (float) (sA*hwidth*Math.cos(tH) + cA*hheight*Math.sin(tH));
				float dxV = (float) (cA*hwidth*Math.cos(tV) - sA*hheight*Math.sin(tV));
				float dyV = (float) (sA*hwidth*Math.cos(tV) + cA*hheight*Math.sin(tV));
				
				float	[]tempCX = {	x_ - Math.abs(dxV),		//	upper left
										x_ + Math.abs(dxV),		//	upper right
										x_ + Math.abs(dxV),		//	lower right
										x_ - Math.abs(dxV)};	//	lower left

				float	[]tempCY = {	y_ + Math.abs(dyH),	//	upper left
										y_ + Math.abs(dyH),	//	upper right
										y_ - Math.abs(dyH),	//	lower right
										y_ - Math.abs(dyH)};	//	lower left
				cornerX = tempCX; cornerY = tempCY;
			}
			//	case of ellipse rotated by ± π/2
			else if (Math.abs(cA) <= 1E-4) {
				float	[]tempCX = {	x_ - hheight,	//	upper left
										x_ + hheight,	//	upper right
										x_ + hheight,	//	lower right
										x_ - hheight};	//	lower left

				float	[]tempCY = {	y_ + hwidth,	//	upper left
										y_ + hwidth,	//	upper right
										y_ - hwidth,	//	lower right
										y_ - hwidth};	//	lower left
				cornerX = tempCX; cornerY = tempCY;
			}
			//	case of horizontal ellipse
			else //	Math.abs(sA) ≤ 1E-4) 
			{
				float	[]tempCX = {	x_ - hwidth,	//	upper left
										x_ + hwidth,	//	upper right
										x_ + hwidth,	//	lower right
										x_ - hwidth};	//	lower left

				float	[]tempCY = {	y_ + hheight,	//	upper left
										y_ + hheight,	//	upper right
										y_ - hheight,	//	lower right
										y_ - hheight};	//	lower left
				cornerX = tempCX; cornerY = tempCY;
			}
					
			absoluteBox_[0].updatePosition(	PApplet.min(cornerX),	//	xmin
											PApplet.max(cornerX),	//	xmax
											PApplet.min(cornerY),	//	ymin
											PApplet.max(cornerY));	//	ymax
		
	}
	
	/** This checks if a pair of coordinates is within the bounds of an image
	 *  This function applies the equation provided in the assignment
	 * 
	 * @param pixelX     the X-value of the current pixel in the image
	 * @param pixelY     the Y-value of the current pixel in the image
	 * @return 			 the bool value, returns false if the value is outside the ellipse, returns true otherwise
	 * 	
	 */
	public boolean checkInside(int pixelX, int pixelY) {
		float distX = pixelX - copy_.width/2.0f;   // distance from pixel X to center of ellipse
		float distY = pixelY - copy_.height/2.0f;  // distance from pixel Y to center of ellipse
		float a = copy_.width/2.0f;				// half of the image's width
		float b = copy_.height/2.0f;				// half of the image's height
		float value = 1.0f/(a*a) * (distX*distX) + 1.0f/(b*b) * (distY*distY);  // this is the equation provided
		return (value <= 1);
//		System.out.println(value);
	}
	
	/** This checks if a pair of coordinates are inside a set of bounds
	 * 
	 * @param   x-coordinate
	 * @param   y-coordinate
	 * @return  the bool value, returns false if the value is outside the ellipse, returns true otherwise
	 * 
	 *  I left this in because it is a method specified in the class it extends from
	 */
	public boolean isInside(float x, float y) {
		//	Convert x and y into this object's reference frame coordinates
		double cosAngle = Math.cos(angle_), sinAngle = Math.sin(angle_);
		double xb = cosAngle*(x - x_) + sinAngle*(y - y_);
		double yb = cosAngle*(y - y_) + sinAngle*(x_ - x);

		float dx = 2*((float)xb)/width_, dy = 2*((float) yb)/height_;
		return dx*dx + dy*dy <= 1.0f;
	}
}
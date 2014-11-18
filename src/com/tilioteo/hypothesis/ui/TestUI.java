/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import javax.servlet.annotation.WebServlet;

import org.vaadin.maps.shared.ui.Style;
import org.vaadin.maps.ui.LayerLayout;
import org.vaadin.maps.ui.control.DrawPathControl;
import org.vaadin.maps.ui.control.DrawPointControl;
import org.vaadin.maps.ui.control.DrawPolygonControl;
import org.vaadin.maps.ui.control.PanControl;
import org.vaadin.maps.ui.feature.VectorFeature;
import org.vaadin.maps.ui.featurecontainer.VectorFeatureContainer;
import org.vaadin.maps.ui.handler.PathHandler.FinishStrategy;
import org.vaadin.maps.ui.layer.ControlLayer;
import org.vaadin.maps.ui.layer.ImageLayer;
import org.vaadin.maps.ui.layer.ImageSequenceLayer;
import org.vaadin.maps.ui.layer.VectorFeatureLayer;
import org.vaadin.maps.ui.layer.WMSLayer;
import org.vaadin.maps.ui.tile.ImageSequenceTile;
import org.vaadin.maps.ui.tile.ImageSequenceTile.ChangeEvent;
import org.vaadin.maps.ui.tile.ImageSequenceTile.LoadEvent;
import org.vaadin.maps.ui.tile.WMSTile;
import org.vaadin.tltv.vprocjs.ui.Processing;
import org.vaadin.websocket.ui.WebSocket;
import org.vaadin.websocket.ui.WebSocket.CloseEvent;
import org.vaadin.websocket.ui.WebSocket.MessageEvent;
import org.vaadin.websocket.ui.WebSocket.OpenEvent;

import com.tilioteo.hypothesis.plugin.map.ui.Map;
import com.tilioteo.hypothesis.ui.Image.LoadListener;
import com.tilioteo.hypothesis.ui.Media.CanPlayThroughEvent;
import com.tilioteo.hypothesis.ui.Media.StartEvent;
import com.tilioteo.hypothesis.ui.Media.StopEvent;
import com.tilioteo.hypothesis.ui.ShortcutKey.KeyPressEvent;
import com.tilioteo.hypothesis.ui.Video.ClickEvent;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@Theme("hypothesis")
public class TestUI extends HUI {

	@WebServlet(value = "/test/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = TestUI.class, widgetset = "com.tilioteo.hypothesis.HypothesisWidgetset")
	public static class Servlet extends VaadinServlet {
	}

	public static final String CODE =
			"ArrayList<Bubble> foregroundBubbles;"+
			"ArrayList<Bubble> backgroundBubbles;"+
			"ArrayList<Food> foods;"+
			"Fish myFish;"+
			""+
			"public void setup() {"+
			"    int pondWidth = 575;"+
			"    int pondHeight = 480;"+
			""+
			"	size(pondWidth, pondHeight);"+
			""+
			"	frameRate(45); //45"+
			"	"+
			"    PVector location = new PVector(random(0.2 * pondWidth, 0.8 * pondWidth), "+
			"        random(0.2 * pondHeight, 0.8 * pondHeight));"+
			"    myFish = new Fish(location, random( 2.0, 2.5 ), 0.2);"+
			""+
			"    foregroundBubbles = new ArrayList<Bubble>();"+
			"    backgroundBubbles = new ArrayList<Bubble>();"+
			""+
			"    foods = new ArrayList<Food>();"+
			"}"+
			""+
			""+
			"public void draw() {"+
			"	background( 105, 210, 231, 0);"+
			""+
			"    randomNumber = random(0, 1000);"+
			""+
			"    if(randomNumber > 980) {"+
			"        foregroundBubbles.add(new Bubble( color(int(random(100, 255)), 200)));"+
			"    } "+
			"    else if(randomNumber < 20) {"+
			"        backgroundBubbles.add(new Bubble( color(int(random(100, 255)), 100)));"+
			"    }"+
			""+
			"    for(int i = backgroundBubbles.size()-1; i >= 0; i--) {"+
			"        Bubble bubble = backgroundBubbles.get(i);"+
			"        if (bubble.getLocation().y < -50)"+
			"            backgroundBubbles.remove(i);"+
			"        else {"+
			"            bubble.update();"+
			"            bubble.render();"+
			"        }"+
			"    }"+
			"     "+
			"    myFish.update();"+
			"    myFish.render();"+
			"    if(myFish.getFoodEaten() > 11 && !myFish.getIsExploding()) {"+
			"        myFish.explode();"+
			"    }"+
			""+
			"    for(int i = foregroundBubbles.size()-1; i >= 0; i--) {"+
			"        Bubble bubble = foregroundBubbles.get(i);"+
			"        if (bubble.getLocation().y < -50)"+
			"            foregroundBubbles.remove(i);"+
			"        else {"+
			"            bubble.update();"+
			"            bubble.render();"+
			"        }"+
			"    }"+
			""+
			"    if(randomNumber <= 6) {"+
			"        PVector foodLocation = new PVector(random(100, width-100), random(100,height-100));"+
			"        Food food = new Food(foodLocation);"+
			"        foods.add(food);"+
			"    }"+
			""+
			"    for(int i = foods.size()-1; i >= 0; i--) {"+
			"        Food food = foods.get(i);"+
			"        boolean foodEaten = false;"+
			"        if (!food.getIsDead()) {"+
			"            food.update();"+
			"            food.render();"+
			"            foodEaten = myFishAteFood(food);"+
			"        }"+
			"        else"+
			"            foods.remove(i);"+
			"        if (foodEaten)"+
			"            foods.remove(i);"+
			"    }"+
			"}"+
			""+
			"public boolean myFishAteFood(Food food) {"+
			"    boolean foodEaten = false;"+
			""+
			"    float distanceToFood = PVector.sub(myFish.getLocation(), food.getLocation()).mag();"+
			"    if (distanceToFood < 10){"+
			"        foodEaten = true;"+
			"        gulp.play();"+
			"        myFish.setBodySizeH(myFish.getBodySizeH() * 1.1);"+
			"        myFish.setBodySizeW(myFish.getBodySizeW() * 1.1);"+
			"        myFish.setLastAteTimer(int(frameRate/2));"+
			"        myFish.setFoodEaten(myFish.getFoodEaten() + 1);"+
			"    }"+
			""+
			"    return foodEaten;"+
			"}"+
			""+
			"public void mouseMoved() {"+
			"    PVector mousePosition = new PVector(mouseX, mouseY);"+
			"    myFish.setMousePosition(mousePosition);"+
			"}"+
			""+
			"class Food {"+
			"	"+
			"	private PVector location;"+
			"	private PVector velocity;"+
			"	"+
			"	private int age;"+
			"	private int ageSpan;"+
			"	private int color;"+
			"	private int[] colours = new int();"+
			"	"+
			"	private int numSides	= 8;"+
			"	private float[][] verts = new float[numSides][2];"+
			"	"+
			"	private boolean isDead;"+
			"	private boolean isDummy;"+
			""+
			"	//constructor used to create your personal local food"+
			"	public Food(PVector location) {"+
			"		velocity 	= new PVector( random( -.5, .5 ), random( -.5, .5 ) );"+
			"		"+
			"		age 		= 0;"+
			"		"+
			"		colors 		= [#C96164, #E8C64D, #9EB53F, #5D966C];"+
			"		int c 		= int(random( 4 ));"+
			""+
			"		createFood(location, colors[c], false);"+
			"	}"+
			""+
			"    private void createFood(PVector location, int color, boolean isDead) {"+
			"		this.location 	= location;"+
			"		ageSpan 	= 300; //random( 100, 200 );"+
			"		this.color = color;"+
			"		this.isDead = isDead;"+
			"		this.isDummy = false;"+
			""+
			"		float k = TWO_PI / (float) numSides;"+
			"		for ( int i = 0; i < numSides; i++ ) {"+
			"			verts[i][0] = cos( k * i ) * random( 3, 15 );"+
			"			verts[i][1] = sin( k * i ) * random( 3, 15 );"+
			"		}"+
			"    }"+
			"	"+
			"	public void update() {"+
			"		location.add( velocity );"+
			"		"+
			"		age++;"+
			"		if ( age >= ageSpan  ) {"+
			"			isDead = true;"+
			"		}"+
			"	}"+
			"		"+
			"	public void render() {"+
			"		noStroke();"+
			"		fill( color );"+
			"		pushMatrix();"+
			"		translate( location.x, location.y );"+
			"		scale(1 - age / ageSpan);"+
			"		beginShape( TRIANGLE_FAN );"+
			"		for ( int i = 0; i < numSides; i++ ) {"+
			"			vertex( verts[i][0], verts[i][1] );"+
			"		}"+
			"		endShape();"+
			"		popMatrix();"+
			"	}"+
			""+
			"	public boolean getIsDead() {"+
			"		return isDead;"+
			"	}"+
			""+
			"	public void setIsDead(boolean isDead) {"+
			"		this.isDead = isDead;"+
			"	}"+
			""+
			"	public PVector getVelocity() {"+
			"		return velocity;"+
			"	}"+
			""+
			"	public PVector getLocation() {"+
			"		return location;"+
			"	}"+
			""+
			"	public void setLocation(PVector location){"+
			"		this.location = location;"+
			"	}"+
			""+
			"	public int getColor() {"+
			"		return this.color;"+
			"	}"+
			""+
			"	public int getAge() {"+
			"		return age;"+
			"	}"+
			""+
			"	public void setAge(int age) {"+
			"		this.age = age;"+
			"	}"+
			""+
			"	public void setIsDummy(boolean isDummy) {"+
			"		this.isDummy = isDummy;"+
			"	}"+
			""+
			"	public boolean getIsDummy() {"+
			"		return this.isDummy;"+
			"	}"+
			"}"+
			""+
			"class Boid {"+
			"	"+
			"	private PVector location;"+
			""+
			"	private PVector velocity;"+
			"	private PVector acceleration;"+
			"    private PVector location;"+
			""+
			"	private float maxForce;"+
			"	private float maxSpeed;"+
			"	private float wanderTheta;"+
			"	"+
			"	private boolean hasArrive;"+
			"	"+
			"	// constructor used by fish"+
			"	public Boid( PVector _location, float _maxSpeed, float _maxForce) {"+
			"        createBoid(_location, _maxSpeed, _maxForce);"+
			"		velocity 		= new PVector( random( -maxSpeed, maxSpeed ), random( -maxSpeed, maxSpeed ) );"+
			"    }"+
			""+
			"    // constructor used by bubbles"+
			"    public Boid( PVector _location, float _maxSpeed, float _maxForce, PVector velocity) {"+
			"        this.velocity = velocity.get();"+
			"        createBoid(_location, _maxSpeed, _maxForce);"+
			"    }"+
			""+
			"    private void createBoid(PVector _location, float _maxSpeed, float _maxForce) {"+
			"		location 		= _location.get();"+
			"		maxSpeed 		= _maxSpeed;"+
			"		maxForce 		= _maxForce;"+
			"		acceleration 	= new PVector( 0, 0 );"+
			"		wanderTheta		= 0;"+
			"		hasArrive 		= false;"+
			"	}"+
			"	"+
			"	protected void update() {"+
			"		velocity.add(acceleration);"+
			"		velocity.limit(maxSpeed);"+
			"		location.add(velocity);"+
			"		acceleration.mult(0);"+
			"	}"+
			"	"+
			"	protected void debugRender() {"+
			"		noStroke();"+
			"		fill(255, 0, 0);"+
			"		ellipse(location.x, location.y, 10, 10);"+
			"	}"+
			"	"+
			"	private PVector steer( PVector _target, boolean _slowdown ) {"+
			"		PVector steer;"+
			"		PVector desired = PVector.sub( _target, location );"+
			"		"+
			"		float dist = desired.mag();"+
			"		"+
			"		if ( dist > 0 ) {"+
			"			desired.normalize();"+
			"			"+
			"			if ( _slowdown && dist < 60 ) {"+
			"				desired.mult( maxSpeed * (dist / 60) );"+
			"				if ( dist < 10 ) {"+
			"					hasArrive = true;"+
			"				}"+
			"			}"+
			"			else {"+
			"				desired.mult( maxSpeed );"+
			"			}"+
			"			"+
			"			steer = PVector.sub( desired, velocity );"+
			"			steer.limit( maxForce );"+
			"		}"+
			"		else {"+
			"			steer = PVector( 0, 0 );"+
			"		}"+
			"		"+
			"		return steer;"+
			"	}"+
			"	"+
			"	protected void seek(PVector _target) {"+
			"		acceleration.add( steer( _target, false ) );"+
			"	}"+
			"	"+
			"	protected void arrive( PVector _target ) {"+
			"		acceleration.add( steer( _target, true ) );"+
			"	}"+
			"	"+
			"	protected void flee( PVector _target ) {"+
			"		acceleration.sub( steer( _target, false ) );"+
			"	}"+
			"	"+
			"	protected void wander() {"+
			"		float wanderR 	= 5;"+
			"		float wanderD 	= 100;"+
			"		float change 	= 0.05;"+
			"		"+
			"		wanderTheta += random( -change, change );"+
			"		"+
			"		PVector circleLocation = velocity.get();"+
			"		circleLocation.normalize();"+
			"		circleLocation.mult( wanderD );"+
			"		circleLocation.add( location );"+
			"		"+
			"		PVector circleOffset = new PVector( wanderR * cos( wanderTheta), wanderR * sin( wanderTheta ) );"+
			"		PVector target= PVector.add( circleLocation, circleOffset );"+
			"		"+
			"		seek( target );"+
			"	}"+
			"	"+
			"	protected void evade( PVector _target ) {"+
			"		float lookAhead = location.dist( _target ) / (maxSpeed * 2);"+
			"		PVector predictedTarget = new PVector( _target.x - lookAhead, _target.y - lookAhead );"+
			"		flee( predictedTarget );"+
			"	}"+
			""+
			"    public float getMaxSpeed() {"+
			"        return maxSpeed;"+
			"    }"+
			""+
			"    public float getMaxForce() {"+
			"        return maxForce;"+
			"    }"+
			""+
			"    public PVector getVelocity() {"+
			"    	return velocity;"+
			"    }"+
			""+
			"    public void setVelocity(PVector velocity) {"+
			"    	this.velocity = velocity;"+
			"    }"+
			"	"+
			"    public PVector getLocation() {"+
			"    	return this.location;"+
			"    }"+
			"}"+
			""+
			"class Flagellum {"+
			"	"+
			"	int numNodes;"+
			"	"+
			"	float[][] spine;"+
			"	"+
			"	float MUSCLE_RANGE 	= 0.15;"+
			"	float muscleFreq	= 0.08;"+
			"	"+
			"	float sizeW, sizeH;"+
			"	float spaceX, spaceY;"+
			"	float theta;"+
			"	float count;"+
			"	"+
			"	"+
			"	"+
			"	Flagellum( float _sizeW, float _sizeH, int _numNodes ) {"+
			"		"+
			"		sizeW		= _sizeW;"+
			"		sizeH		= _sizeH;"+
			"		"+
			"		numNodes	= _numNodes;"+
			"		"+
			"		spine 		= new float[numNodes][2];"+
			"		"+
			"		spaceX 		= sizeW / float(numNodes + 1);"+
			"		spaceY 		= sizeH / 2.0;"+
			"		"+
			"		count 		= 0;"+
			"		theta 		= PI;"+
			"		thetaVel 	= 0;"+
			"		"+
			"		"+
			"		// Initialize spine positions"+
			"		for ( int n = 0; n < numNodes; n++ ) {"+
			"			float x	= spaceX * n;"+
			"			float y = spaceY;"+
			"			"+
			"			spine[n][0] = x;"+
			"			spine[n][1] = y;"+
			"		}"+
			"	}"+
			"	"+
			"	"+
			"	void swim() {"+
			"		spine[0][0] = cos( theta );"+
			"		spine[0][1] = sin( theta );"+
			"		"+
			"		count += muscleFreq;"+
			"		float thetaMuscle = MUSCLE_RANGE * sin( count );"+
			"		"+
			"		spine[1][0] = -spaceX * cos( theta + thetaMuscle ) + spine[0][0];"+
			"		spine[1][1] = -spaceX * sin( theta + thetaMuscle ) + spine[0][1];"+
			"		"+
			"		for ( int n = 2; n < numNodes; n++ ) {"+
			"			float x	= spine[n][0] - spine[n - 2][0];"+
			"			float y = spine[n][1] - spine[n - 2][1];"+
			"			float l = sqrt( (x * x) + (y * y) );"+
			"			"+
			"			if ( l > 0 ) {"+
			"				spine[n][0] = spine[n - 1][0] + (x * spaceX) / l;"+
			"				spine[n][1] = spine[n - 1][1] + (y * spaceX) / l;"+
			"			}"+
			"		}"+
			"	}"+
			"	"+
			"	"+
			"	void debugRender() {"+
			"		for ( int n = 0; n < numNodes; n++ ) {"+
			"			stroke( 0 );"+
			"			if ( n < numNodes - 1 ) {"+
			"				line( spine[n][0], spine[n][1], spine[n + 1][0], spine[n + 1][1] );"+
			"			}"+
			"			fill( 90 );"+
			"			ellipse( spine[n][0], spine[n][1], 6, 6 );"+
			"		}"+
			"	}"+
			"	"+
			"}"+
			""+
			"class Fish extends Boid {"+
			"	"+
			"	private Flagellum body;"+
			"	private Flagellum tailR;"+
			"	private Flagellum tailL;"+
			"	private Flagellum finR;"+
			"	private Flagellum finL;"+
			"	"+
			"	// After eating, this gets set to frameRate/2. It is decremented"+
			"	// each loop by one until it hits zero. While it is non-zero, we display"+
			"	// the fish's mouth"+
			"	private int lastAteTimer;"+
			"	private int numBodySegments;"+
			"	private int numTailSegments;"+
			"	private int numFinSegments;"+
			"	private int foodEaten;"+
			"	private int transparency;"+
			"	private int explosionTimer;"+
			"	"+
			"	private float bodySizeW;"+
			"	private float bodySizeH;"+
			"	private float tailSizeW;"+
			"	private float tailSizeH;"+
			""+
			"    private Color mainColor;"+
			"    private Color stripeColor;"+
			"    private Color outlineColor;"+
			""+
			"    private PVector mousePosition;"+
			"    private PVector mousePositionOld;"+
			""+
			"    private boolean isExploding;"+
			"	"+
			"	// Constructor to create your personal, local fish"+
			"	public Fish(PVector location, float maxSpeed, float maxForce) {"+
			"        stripeColor = color(int(random(255)), int(random(255)), int(random(255)));"+
			"		bodySizeW		= random( 100, 200 );"+
			"		bodySizeH		= (bodySizeW * 0.3 + random( 5 ));"+
			"        mousePosition = new PVector(0, 0);"+
			"        lastAteTimer = 0;"+
			"        isExploding = false;"+
			"        foodEaten = 0;"+
			"        transparency = 255;"+
			""+
			"        createFish(location, maxSpeed, maxForce);"+
			"    }"+
			""+
			"    private void createFish(PVector location, float maxSpeed, float maxForce) {"+
			"    	mousePositionOld = new PVector(mousePosition.x, mousePosition.y);"+
			"		super(location, maxSpeed, maxForce);"+
			"        mainColor = #000000;"+
			"        outlineColor = #D8D8C0;"+
			"		"+
			"		numBodySegments = 10;"+
			"		"+
			"		numTailSegments = 10;"+
			"		tailSizeW		= bodySizeW * 0.6;"+
			"		tailSizeH		= bodySizeH * 0.25;"+
			"		"+
			"		body = new Flagellum( bodySizeW, bodySizeH, numBodySegments );"+
			"		"+
			"		tailR = new Flagellum( tailSizeW, tailSizeH, numTailSegments );"+
			"		tailL = new Flagellum( tailSizeW * 0.8, tailSizeH * 0.8, numTailSegments );"+
			"		"+
			"		numFinSegments = 9;"+
			"		finR = new Flagellum( tailSizeW * 0.5, tailSizeH, numFinSegments );"+
			"		finL = new Flagellum( tailSizeW * 0.5, tailSizeH, numFinSegments );"+
			"	}"+
			"	"+
			"	public void update() {"+
			"		super.update();"+
			""+
			"		checkBorders();"+
			"		// super.wander();"+
			"		"+
			"		body.muscleFreq = norm(super.velocity.mag(), 0, 1) * 0.05;"+
			"		"+
			"		// Align body to velocity"+
			"		body.theta 	= super.velocity.heading2D();"+
			"		body.swim();"+
			"		"+
			"		float diffX 		= body.spine[numBodySegments-1][0] - body.spine[numBodySegments-2][0];"+
			"		float diffY 		= body.spine[numBodySegments-1][1] - body.spine[numBodySegments-2][1];"+
			"		float angle			= atan2( diffY, diffX );"+
			"		"+
			"		tailR.muscleFreq 	= norm( super.velocity.mag(), 0, 1 ) * 0.08;"+
			"		tailR.theta 		= angle + (PI * 0.95);"+
			"		tailR.swim();"+
			"		"+
			"		tailL.muscleFreq 	= norm( super.velocity.mag(), 0, 1 ) * 0.08;"+
			"		tailL.theta 		= angle + (PI * 1.05);"+
			"		tailL.swim();"+
			"		"+
			"		finR.muscleFreq 	= norm( super.velocity.mag(), 0, 1 ) * 0.04;"+
			"		finR.swim();"+
			"		"+
			"		finL.muscleFreq 	= norm( super.velocity.mag(), 0, 1 ) * 0.04;"+
			"		finL.swim();"+
			""+
			"		// only change fish velocity if the mouse moved. this makes the fish keep moving as opposed "+
			"		// to \"seizuring up\" when they reach the mouse"+
			"		if(mousePosition.x != mousePositionOld.x ||"+
			"			mousePosition.y != mousePositionOld.y)"+
			"        	seek(mousePosition);"+
			"        mousePositionOld = mousePosition;"+
			""+
			"        // logic to explode if fish ate too much (called from explode method)"+
			"        if(isExploding) {"+
			"        	bodySizeW = bodySizeW * 1.3;"+
			"        	bodySizeH = bodySizeH * 1.3;"+
			""+
			"        	transparency = (explosionTimer / frameRate) * 255;"+
			""+
			"        	explosionTimer--;"+
			"        }"+
			"	}"+
			"	"+
			"	public void render() {"+
			"		noStroke();"+
			""+
			"		// render fins"+
			"		PVector finLLocation = new PVector( super.location.x + body.spine[3][0], super.location.y + body.spine[3][1] );"+
			"		PVector finRLocation = new PVector( super.location.x + body.spine[3][0], super.location.y + body.spine[3][1] );"+
			"		"+
			"		fill(mainColor, transparency);"+
			"		renderFin(finR, finLLocation,  bodySizeH * 0.5, 1);"+
			"		fill(mainColor, transparency);		"+
			"		renderFin(finL, finRLocation, -bodySizeH * 0.5, -1);"+
			"		"+
			"		// render body"+
			"		fill(outlineColor, transparency);"+
			"		renderBody( body, super.location, 1.1, 0.1 );"+
			"		fill(stripeColor, transparency);"+
			"		renderBody( body, super.location, 0.8, 0.15 );"+
			"		fill(mainColor, transparency);"+
			"		renderBody( body, super.location, 0.5, 0.25 );"+
			"		"+
			"		// render tails"+
			"		PVector tailLocation = new PVector( super.location.x + body.spine[numBodySegments - 1][0], super.location.y + body.spine[numBodySegments - 1][1] );"+
			"		fill(mainColor, transparency);"+
			"		renderTail( tailR, tailLocation, 0.75 );"+
			"		fill(mainColor, transparency);"+
			"		renderTail( tailL, tailLocation, 0.75 );"+
			"		"+
			"		// render head"+
			"		PVector headLocation = new PVector( super.location.x + body.spine[1][0], super.location.y + body.spine[1][1] );"+
			"		renderHead( headLocation, bodySizeW * 0.1, bodySizeW * 0.06 );"+
			""+
			"		// render mouth if fish ate recently"+
			"		fill(250, 128, 114, transparency);"+
			"		if(lastAteTimer > 0) {"+
			"			lastAteTimer--;"+
			"			float mouthSize = (bodySizeW*.05) * (float) lastAteTimer / (frameRate/2)"+
			"			ellipse(location.x,location.y,mouthSize,mouthSize);"+
			"		}"+
			"	}"+
			"	"+
			"	private void renderHead( PVector _location, float _eyeSize, float _eyeDist ) {"+
			"		float diffX = body.spine[2][0] - body.spine[1][0];"+
			"		float diffY = body.spine[2][1] - body.spine[1][1];"+
			"		float angle	= atan2( diffY, diffX );"+
			"		"+
			"		pushMatrix();"+
			"		translate( _location.x, _location.y );"+
			"		rotate( angle );"+
			"		"+
			"		fill(mainColor, transparency);"+
			"		ellipse( 0, _eyeDist, _eyeSize, _eyeSize );"+
			"		"+
			"		fill(stripeColor, transparency);"+
			"		ellipse( -3, _eyeDist, _eyeSize * 0.35, _eyeSize * 0.35 );"+
			"		"+
			"		popMatrix();"+
			"		"+
			"		pushMatrix();"+
			"		translate( _location.x, _location.y );"+
			"		rotate( angle );"+
			"		"+
			"		fill(mainColor, transparency);"+
			"		ellipse( 0, -_eyeDist, _eyeSize, _eyeSize );"+
			"		"+
			"		fill(stripeColor, transparency);"+
			"		ellipse( -3, -_eyeDist, _eyeSize * 0.35, _eyeSize * 0.35 );"+
			"		"+
			"		popMatrix();"+
			"	}"+
			"	"+
			"	private void renderBody( Flagellum _flag, PVector _location, float _sizeOffsetA, float _sizeOffsetB ) {"+
			"		pushMatrix();"+
			"		translate( _location.x, _location.y );"+
			"		beginShape( TRIANGLE_STRIP );"+
			"		for ( int n = 0; n < _flag.numNodes; n++ ) {"+
			"			float dx, dy;"+
			"			if ( n == 0 ) {"+
			"				dx = _flag.spine[1][0] - _flag.spine[0][0];"+
			"				dy = _flag.spine[1][1] - _flag.spine[0][1];"+
			"			}"+
			"			else {"+
			"				dx = _flag.spine[n][0] - _flag.spine[n - 1][0];"+
			"				dy = _flag.spine[n][1] - _flag.spine[n - 1][1];"+
			"			}"+
			"			"+
			"			float theta = -atan2( dy, dx );"+
			"			"+
			"			float t 	= n / float(_flag.numNodes - 1);"+
			"			float b		= bezierPoint( 3, bodySizeH * _sizeOffsetA, bodySizeH * _sizeOffsetB, 2, t );"+
			"			"+
			"			float x1	= _flag.spine[n][0] - sin( theta ) * b;"+
			"			float y1 	= _flag.spine[n][1] - cos( theta ) * b;"+
			"			"+
			"			float x2 	= _flag.spine[n][0] + sin( theta ) * b;"+
			"			float y2 	= _flag.spine[n][1] + cos( theta ) * b;"+
			"			"+
			"			vertex( x1, y1 );"+
			"			vertex( x2, y2 );"+
			"		}"+
			""+
			"		endShape();"+
			"		popMatrix();"+
			"	}"+
			"	"+
			"	"+
			"	private void renderTail( Flagellum _flag, PVector _location, float _sizeOffset ) {"+
			"		pushMatrix();"+
			"		translate( _location.x, _location.y );"+
			"		"+
			"		beginShape( TRIANGLE_STRIP );"+
			"		for ( int n = 0; n < _flag.numNodes; n++ ) {"+
			"			float dx, dy;"+
			"			if ( n == 0 ) {"+
			"				dx = _flag.spine[1][0] - _flag.spine[0][0];"+
			"				dy = _flag.spine[1][1] - _flag.spine[0][1];"+
			"			}"+
			"			else {"+
			"				dx = _flag.spine[n][0] - _flag.spine[n - 1][0];"+
			"				dy = _flag.spine[n][1] - _flag.spine[n - 1][1];"+
			"			}"+
			"			"+
			"			float theta = -atan2( dy, dx );"+
			"			"+
			"			float t 	= n / float(_flag.numNodes - 1);"+
			"			float b		= bezierPoint( 2, _flag.sizeH, _flag.sizeH * _sizeOffset, 0, t );"+
			"			"+
			"			float x1	= _flag.spine[n][0] - sin( theta ) * b;"+
			"			float y1 	= _flag.spine[n][1] - cos( theta ) * b;"+
			"			"+
			"			float x2 	= _flag.spine[n][0] + sin( theta ) * b;"+
			"			float y2 	= _flag.spine[n][1] + cos( theta ) * b;"+
			"			"+
			"			vertex( x1, y1 );"+
			"			vertex( x2, y2 );"+
			"		}"+
			"		endShape();"+
			"		"+
			"		popMatrix();"+
			"	}"+
			"	"+
			"	"+
			"	private void renderFin( Flagellum _flag, PVector _location, float _posOffset, int _flip ) {"+
			"		float diffX = body.spine[2][0] - body.spine[1][0];"+
			"		float diffY = body.spine[2][1] - body.spine[1][1];"+
			"		float angle	= atan2( diffY, diffX );"+
			"		"+
			"		pushMatrix();"+
			"		translate( _location.x, _location.y );"+
			"		rotate( angle );"+
			"		"+
			"		pushMatrix();"+
			"		translate( 0, _posOffset );"+
			"		"+
			"		beginShape(TRIANGLE_STRIP);"+
			"		for ( int n = 0; n < _flag.numNodes; n++ ) {"+
			"			float dx, dy;"+
			"			if ( n == 0 ) {"+
			"				dx = _flag.spine[1][0] - _flag.spine[0][0];"+
			"				dy = _flag.spine[1][1] - _flag.spine[0][1];"+
			"			}"+
			"			else {"+
			"				dx = _flag.spine[n][0] - _flag.spine[n - 1][0];"+
			"				dy = _flag.spine[n][1] - _flag.spine[n - 1][1];"+
			"			}"+
			"			"+
			"			float theta = -atan2( dy, dx );"+
			"			"+
			"			float t 	= n / float(_flag.numNodes - 1);"+
			"			float b		= bezierPoint( 0, _flip * _flag.sizeH * 0.75, _flip * _flag.sizeH * 0.75, 0, t );"+
			"			float v		= bezierPoint( 0, _flip * _flag.sizeH * 0.05, _flip * _flag.sizeH * 0.65, 0, t );"+
			"			"+
			"			float x1	= _flag.spine[n][0] - sin( theta ) * v;"+
			"			float y1 	= _flag.spine[n][1] - cos( theta ) * v;"+
			"			"+
			"			float x2 	= _flag.spine[n][0] + sin( theta ) * b;"+
			"			float y2 	= _flag.spine[n][1] + cos( theta ) * b;"+
			"			"+
			"			vertex( x1, y1 );"+
			"			vertex( x2, y2 );"+
			"		}"+
			""+
			"		endShape();"+
			"		popMatrix();"+
			"		popMatrix();"+
			"	}"+
			""+
			"	/**"+
			"	 *	ensure that the fish wraps around the the other end of the pond"+
			"	 *	if it goes past the edge. Also keeps unModdedLocations in sync with"+
			"	 *	location"+
			"	 */"+
			"	private void checkBorders() {"+
			"		if ( location.x < -bodySizeW ) {"+
			"			location.x = width;"+
			"		}"+
			"		if ( location.x > width + bodySizeW ) {"+
			"			location.x = 0;"+
			"		}"+
			"		if ( location.y < -bodySizeW ) {"+
			"			location.y = height;"+
			"		}"+
			"		if ( location.y > height + bodySizeW ) {"+
			"			location.y = 0;"+
			"		}"+
			"	}	"+
			""+
			"	public void explode() {"+
			"		explosion.play(); // play explosion sound with buzz"+
			"		//prevent the mouth from rendering during explosion"+
			"		lastAteTimer = 0;"+
			"		isExploding = true;"+
			"		explosionTimer = frameRate;"+
			"	}"+
			""+
			"    // Getters"+
			"    public Color getStripeColor() {"+
			"        return stripeColor;"+
			"    }"+
			""+
			"    public float getBodySizeW() {"+
			"        return bodySizeW;"+
			"    }"+
			""+
			"    public float getBodySizeH() {"+
			"        return bodySizeH;"+
			"    }"+
			"    "+
			"    public PVector getMousePosition() {"+
			"        return mousePosition;"+
			"    }"+
			""+
			"    public void setMousePosition(PVector position) {"+
			"        mousePosition = position;"+
			"    }"+
			""+
			"    public float getBodySizeH(){"+
			"    	return bodySizeH;"+
			"    }"+
			""+
			"    public void setBodySizeH(float bodySizeH){"+
			"    	this.bodySizeH = bodySizeH;"+
			"    }"+
			""+
			"    public float getBodySizeW(){"+
			"    	return bodySizeW;"+
			"    }"+
			""+
			"    public void setBodySizeW(float bodySizeW){"+
			"    	this.bodySizeW = bodySizeW;"+
			"    }"+
			""+
			"    public int getLastAteTimer() {"+
			"    	return lastAteTimer;"+
			"    }"+
			""+
			"    public void setLastAteTimer(int lastAteTimer) {"+
			"    	this.lastAteTimer = lastAteTimer;"+
			"    }"+
			""+
			"    public boolean getIsExploding(){"+
			"    	return isExploding;"+
			"    }"+
			""+
			"    public int getFoodEaten(){"+
			"    	return foodEaten;"+
			"    }"+
			""+
			"    public void setFoodEaten(int foodEaten){"+
			"    	this.foodEaten = foodEaten;"+
			"    }"+
			""+
			"    public int getTransparency() {"+
			"    	return transparency;"+
			"    }"+
			""+
			"    public void setTransparency(int transparency) {"+
			"    	this.transparency = transparency;"+
			"    }"+
			""+
			"    public int getExplosionTimer() {"+
			"    	return explosionTimer;"+
			"    }"+
			"}"+
			""+
			"class Bubble extends Boid {"+
			"    float diameter;"+
			"    Color mainColor;"+
			"    String id;"+
			""+
			"    Bubble(Color myColor) {"+
			"        PVector location = new PVector(int(random(0, width - 1)), height - 1);"+
			"        diameter = int(random(30, 50));"+
			"        super(location, 0.8, 0.2, new PVector(0, -1)); "+
			"        mainColor = myColor;"+
			"    }"+
			""+
			"    void render() {"+
			"        smooth();"+
			"        stroke(mainColor);"+
			"        strokeWeight(3);"+
			"        noFill();"+
			"        ellipseMode(CENTER);"+
			"        ellipse(location.x, location.y, diameter, diameter);"+
			"    }"+
			""+
			"    void update() {"+
			"        velocity.x = random(-0.4, 0.4);"+
			"        super.update();"+
			"    }"+
			"}";
	
	public static final String CODE2 = 
			"float[] x = new float[20];\n" +
			"float[] y = new float[20];\n" +
			"float segLength = 10;\n\n" +
			"void setup() {\n  size(320, 240);\n" +
			"  smooth();\n" +
			"}\n\n" +
			"void draw() {\n" +
			"  background(226);\n" +
			"  dragSegment(0, mouseX - 8, mouseY - 8);\n" +
			"  for(int i=0; i < x.length-1; i++) {\n" +
			"    dragSegment(i+1, x[i], y[i]);\n" +
			"  }\n" +
			"}\n\n" +
			"void dragSegment(int i, float xin, float yin) {\n" +
			"  float dx = xin - x[i];\n" +
			"  float dy = yin - y[i];\n" +
			"  float angle = atan2(dy, dx);\n" +
			"  x[i] = xin - cos(angle) * segLength;\n" +
			"  y[i] = yin - sin(angle) * segLength;\n" +
			"  pushMatrix();\n" +
			"  translate(x[i], y[i]);\n" +
			"  rotate(angle);\n" +
			"  color c;\n" +
			"  if ( i % 3 == 1 )\n" +
			"    c = color(0, 0, 0, 255);\n" +
			"  else if ( i % 3 == 2 )\n" +
			"    c = color(255, 255, 0, 255);\n" +
			"  else\n" +
			"    c = color(255, 0, 0, 255);\n\n" +
			"  stroke( c );" +
			"  strokeWeight(10);\n" +
			"  line(0, 0, segLength, 0);\n" +
			"  if ( i == x.length - 1 ) {\n" +
			"    fill( c );\n    noStroke();\n" +
			"    beginShape(TRIANGLES);\n" +
			"    vertex(0, 5);\n" +
			"    vertex(-2 * segLength, 0);\n" +
			"    vertex(0, -5);\n    endShape();\n" +
			"  }\n" +
			"  if ( i == 0 ) {\n" +
			"    noStroke();\n    fill(0, 255);\n" +
			"    ellipse(segLength, -2, 3, 3);\n" +
			"    ellipse(segLength, 2, 3, 3);\n" +
			"  }\n" +
			"  popMatrix();\n" +
			"}";
	
	@Override
	protected void init(VaadinRequest request) {

		/*WrappedSession session = request.getWrappedSession();
		HttpSession httpSession = ((WrappedHttpSession)session).getHttpSession();
		ServletContext servletContext = httpSession.getServletContext();
		ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		
		config = (HypothesisConfig)context.getBean(HypothesisConfig.class);
		*/
		//config.getSecretKey();
		
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setSizeFull();
		setContent(verticalLayout);
		
		/*
		final Image image = new Image();
		image.setSource(new ExternalResource("http://hypothesis.cz/gallery/albums/userpics/10001/02a.png"));
		image.setWidth("80%");
		image.setHeight("80%");
		image.addLoadListener(new LoadListener() {
			@Override
			public void load(com.tilioteo.hypothesis.ui.Image.LoadEvent event) {
				image.unmask();
			}
		});
		
		verticalLayout.addComponent(image);
		image.mask();
		
		/*
		Video video = new Video();
		video.setSource(new ExternalResource("http://media.w3.org/2010/05/sintel/trailer.ogv"));
		video.setAutoplay(true);
		
		video.addClickListener(new Video.ClickListener() {
			@Override
			public void click(ClickEvent event) {
				Notification.show(String.format("Clicked: x=%d, y=%d, time=%f", event.getRelativeX(), event.getRelativeY(), event.getTime()));
			}
		});
		
		video.addCanPlayThroughListener(new Media.CanPlayThroughListener() {
			@Override
			public void canPlayThrough(CanPlayThroughEvent event) {
				Notification.show("Can play through!");
			}
		});
		
		video.addStartListener(new Media.StartListener() {
			@Override
			public void start(StartEvent event) {
				Notification.show("Playing");
			}
		});
		
		video.addStopListener(new Media.StopListener() {
			@Override
			public void stop(StopEvent event) {
				Notification.show("Finished");
			}
		});
		
		verticalLayout.addComponent(video);
		video.mask();
		*/
		
		final Map map = new Map();
		verticalLayout.addComponent(map);
		map.setWidth("80%");
		map.setHeight("80%");
		//map.mask();

		
		/*ImageLayer imageLayer = new ImageLayer("http://www.imagehosting.cz/images/mapaukol7.jpg");
		*/
		/*imageLayer.addClickListener(new MouseEvents.ClickListener() {
			@Override
			public void click(com.vaadin.event.MouseEvents.ClickEvent event) {
				Notification.show("Image tile clicked");
			}
		});
		imageLayer.addLoadListener(new ProxyTile.LoadListener() {
			@Override
			public void load(ProxyTile.LoadEvent event) {
				Notification.show("Image tile loaded");
			}
		});
		layerLayout.addComponent(imageLayer);*/
/*		map.addComponent(imageLayer);
		*/
		
/*		final ImageSequenceLayer imageSequenceLayer = new ImageSequenceLayer();
		imageSequenceLayer.addTileUrl("http://hypothesis.cz/gallery/albums/userpics/10001/02a.png");
		imageSequenceLayer.addTileUrl("http://hypothesis.cz/gallery/albums/userpics/10001/03a.png");
		imageSequenceLayer.addTileUrl("http://hypothesis.cz/gallery/albums/userpics/10001/04a.png");
		imageSequenceLayer.addTileUrl("http://hypothesis.cz/gallery/albums/userpics/10001/05a.png");
		imageSequenceLayer.addTileUrl("http://hypothesis.cz/gallery/albums/userpics/10001/06a.png");
		imageSequenceLayer.addTileUrl("http://hypothesis.cz/gallery/albums/userpics/10001/07a.png");
		imageSequenceLayer.addTileUrl("http://hypothesis.cz/gallery/albums/userpics/10001/08a.png");
		imageSequenceLayer.addTileUrl("http://hypothesis.cz/gallery/albums/userpics/10001/09a.png");
		imageSequenceLayer.addTileUrl("http://hypothesis.cz/gallery/albums/userpics/10001/10a.png");
		
		imageSequenceLayer.addLoadListener(new ImageSequenceTile.LoadListener() {
			@Override
			public void load(LoadEvent event) {
				map.unmask();
				Notification.show("Image sequence loaded");
			}
		});
		
		imageSequenceLayer.addChangeListener(new ImageSequenceTile.ChangeListener() {
			@Override
			public void change(ChangeEvent event) {
				Notification.show("Image changed");
			}
		});
		
		map.addComponent(imageSequenceLayer);
		*/
		
/*		VectorFeatureLayer vectorLayer = new VectorFeatureLayer();
*/		/*vectorLayer.addClickListener(new VectorFeatureContainer.ClickListener() {
			@Override
			public void click(VectorFeatureContainer.ClickEvent event) {
				Notification.show("Vector layer container clicked");
				int index = imageSequenceLayer.getTileIndex();
				imageSequenceLayer.setTileIndex(++index);
			}
		});*/
		
/*		map.addComponent(vectorLayer);
*/
		/*
		WKTReader wktReader = new WKTReader();
		try {
		 	Geometry geometry = wktReader.read("POLYGON ((50 50,200 50,200 200,50 200,50 50),(100 100,150 100,150 150,100 150,100 100))");
		 	
		 	VectorFeature feature = new VectorFeature(geometry);
		 	feature.addClickListener(new org.vaadin.maps.ui.feature.VectorFeature.ClickListener() {
				@Override
				public void click(org.vaadin.maps.ui.feature.VectorFeature.ClickEvent event) {
					Notification.show("Feature clicked");
				}
			});
*/		 	/*
		 	feature.addDoubleClickListener(new DoubleClickListener() {
				@Override
				public void doubleClick(DoubleClickEvent event) {
					Notification.show("Feature double clicked");
				}
			});
			*/
/*		 	vectorLayer.addComponent(feature);
		 	
		 			 	
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ShortcutKey key1 = new ShortcutKey(ShortcutAction.KeyCode.ARROW_LEFT);
		key1.addKeyPressListener(new ShortcutKey.KeyPressListener() {
			@Override
			public void keyPress(KeyPressEvent event) {
				Notification.show("Prior key shortcut clicked");
				imageSequenceLayer.priorTile();
			}
		});
		addShortcutKey(key1);

		ShortcutKey key2 = new ShortcutKey(ShortcutAction.KeyCode.ARROW_RIGHT);
		key2.addKeyPressListener(new ShortcutKey.KeyPressListener() {
			@Override
			public void keyPress(KeyPressEvent event) {
				Notification.show("Next key shortcut clicked");
				imageSequenceLayer.nextTile();
			}
		});
		addShortcutKey(key2);

		ShortcutKey key3 = new ShortcutKey(ShortcutAction.KeyCode.ARROW_UP);
		key3.addKeyPressListener(new ShortcutKey.KeyPressListener() {
			@Override
			public void keyPress(KeyPressEvent event) {
				map.mask();
			}
		});
		addShortcutKey(key3);

		ShortcutKey key4 = new ShortcutKey(ShortcutAction.KeyCode.ARROW_DOWN);
		key4.addKeyPressListener(new ShortcutKey.KeyPressListener() {
			@Override
			public void keyPress(KeyPressEvent event) {
				map.unmask();
			}
		});
		addShortcutKey(key4);
*/
		
		ControlLayer controlLayer = new ControlLayer();
		map.addComponent(controlLayer);
		
/*		Style style = new Style();
		style.pointShape = "square";
		style.fillColor = "red";
		style.fillOpacity = 0.3;
		style.strokeColor = "red";
		style.strokeWidth = 2;
*/		
		/*DrawPathControl drawControl = new DrawPathControl(vectorLayer);
		drawControl.setStrategy(FinishStrategy.DoubleClick);*/
/*		DrawPointControl drawControl = new DrawPointControl(vectorLayer);
*/		/*DrawPolygonControl drawControl = new DrawPolygonControl(vectorLayer);
		drawControl.setStrategy(FinishStrategy.DoubleClick);*/
/*		vectorLayer.setStyle(style);
		//drawControl.setCursorStyle(style);
		controlLayer.addComponent(drawControl);
		drawControl.activate();
*/
		
		/*Processing processing = new Processing();
		//processing.setSizeFull();
		processing.setProcessingCode(CODE2);
		verticalLayout.addComponent(processing);*/
		
		
		/*WMSTile tile = new WMSTile("http://giswebservices.massgis.state.ma.us/geoserver/wms");
		tile.setLayers("massgis:GISDATA.TOWNS_POLYM,massgis:GISDATA.NAVTEQRDS_ARC,massgis:GISDATA.NAVTEQRDS_ARC_INT");
		tile.setSRS("EPSG:26986");
		tile.setBBox("232325.38526025353,898705.3447384972,238934.49648710093,903749.1401484597");
		tile.setWidth(570);
		tile.setHeight(435);
		tile.setFormat("image/png");
		tile.setStyles("Black_Lines,GISDATA.NAVTEQRDS_ARC::ForOrthos,GISDATA.NAVTEQRDS_ARC_INT::Default");
		tile.setTransparent(true);
		
		verticalLayout.addComponent(tile);*/
		
		WMSLayer wmsLayer = new WMSLayer("http://giswebservices.massgis.state.ma.us/geoserver/wms");
		wmsLayer.setLayers("massgis:GISDATA.TOWNS_POLYM,massgis:GISDATA.NAVTEQRDS_ARC,massgis:GISDATA.NAVTEQRDS_ARC_INT");
		wmsLayer.setSRS("EPSG:26986");
		wmsLayer.setBBox("232325.38526025353,898705.3447384972,238934.49648710093,903749.1401484597");
		
		map.addComponent(wmsLayer);
		
		PanControl panControl = new PanControl(map);
		controlLayer.addComponent(panControl);
		panControl.activate();

	
		/*final WebSocket socket = new WebSocket("ws://localhost:9876/app/service/");
		socket.addOpenListener(new WebSocket.OpenListener() {
			@Override
			public void open(OpenEvent event) {
				Notification.show("Web socket connected");
			}
		});
		socket.addCloseListener(new WebSocket.CloseListener() {
			@Override
			public void close(CloseEvent event) {
				Notification.show("Web socket disconnected");
			}
		});
		socket.addMessageListener(new WebSocket.MessageListener() {
			@Override
			public void message(MessageEvent event) {
				Notification.show("Message from server received");
			}
		});
		
		com.vaadin.ui.Button buttonSend = new Button("Send", new Button.ClickListener() {
			@Override
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				socket.send("Message to client");
			}
		}); 

		com.vaadin.ui.Button buttonClose = new Button("Close", new Button.ClickListener() {
			@Override
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				socket.close();
			}
		});
		
		verticalLayout.addComponent(buttonSend);
		verticalLayout.addComponent(buttonClose);
		verticalLayout.addComponent(socket);*/
}

}

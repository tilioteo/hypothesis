/**
 * 
 */
package org.vaadin.maps.client.drawing;

import org.vaadin.gwtgraphics.client.shape.Path;
import org.vaadin.gwtgraphics.client.shape.path.Arc;
import org.vaadin.gwtgraphics.client.shape.path.MoveTo;

/**
 * @author kamil
 *
 */
public class Donut extends Path {
	
	private int x;
	private int y;
	private int r1;
	private int r2;
	private Arc a11;
	private Arc a12;
	private Arc a21;
	private Arc a22;
	private MoveTo mt1;
	private MoveTo mt2;
	
	public Donut(int x, int y, int r1, int r2) {
		super(6);
		setRedrawingType(RedrawType.MANUAL);
		
		this.x = x;
		this.y = y;
		this.r1 = r1;
		this.r2 = r2;
		
		int t1 = y - r1;
		int b1 = y + r1;
		int t2 = y - r2;
		int b2 = y + r2;
		
		mt1 = new MoveTo(false, x, t1);
		a11 = new Arc(false, r1, r1, 0, false, true, x, b1);
		a12 = new Arc(false, r1, r1, 0, false, true, x, t1);
		mt2 = new MoveTo(false, x, t2);
		a21 = new Arc(false, r2, r2, 0, false, true, x, b2);
		a22 = new Arc(false, r2, r2, 0, false, true, x, t2);
		
		setFillEventOdd();
		addStep(mt1);
		addStep(a11);
		addStep(a12);
		close();
		addStep(mt2);
		addStep(a21);
		addStep(a22);
		close();
		issueRedraw(true);
	}
	
	@Override
	public int getX() {
		return x;
	}

	@Override
	public void setX(int x) {
		this.x = x;
		mt1.setX(x);
		a11.setX(x);
		a12.setX(x);
		mt2.setX(x);
		a21.setX(x);
		a22.setX(x);
		issueRedraw(true);
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public void setY(int y) {
		this.y = y;

		int t1 = y - r1;
		int b1 = y + r1;
		int t2 = y - r2;
		int b2 = y + r2;

		mt1.setY(t1);
		a11.setY(b1);
		a12.setY(t1);
		mt2.setY(t2);
		a21.setY(b2);
		a22.setY(t2);
		issueRedraw(true);
	}
	
	public int getR1() {
		return r1;
	}
	
	public void setR1(int r1) {
		this.r1 = r1;

		int t1 = y - r1;
		int b1 = y + r1;
		
		mt1.setY(t1);
		a11.setRx(r1);
		a11.setRy(r1);
		a11.setY(b1);

		a12.setRx(r1);
		a12.setRy(r1);
		a12.setY(t1);
		issueRedraw(true);
	}
	
	public int getR2() {
		return r2;
	}

	public void setR2(int r2) {
		this.r2 = r2;

		int t2 = y - r2;
		int b2 = y + r2;

		mt1.setY(t2);
		a21.setRx(r2);
		a21.setRy(r2);
		a21.setY(b2);

		a22.setRx(r2);
		a22.setRy(r2);
		a22.setY(t2);
		issueRedraw(true);
	}
}

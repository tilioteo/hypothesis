/**
 * 
 */
package org.vaadin.maps.ui.control;

import java.lang.reflect.InvocationTargetException;

import org.vaadin.maps.server.ClassUtility;
import org.vaadin.maps.shared.ui.Style;
import org.vaadin.maps.shared.ui.control.DrawFeatureControlState;
import org.vaadin.maps.ui.CanCancel;
import org.vaadin.maps.ui.CanUndoRedo;
import org.vaadin.maps.ui.StyleUtility;
import org.vaadin.maps.ui.handler.FeatureHandler;
import org.vaadin.maps.ui.handler.FeatureHandler.DrawFeatureListener;
import org.vaadin.maps.ui.handler.RequiresVectorFeatureLayer;
import org.vaadin.maps.ui.layer.VectorFeatureLayer;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
@SuppressWarnings("serial")
public abstract class DrawFeatureControl<H extends FeatureHandler> extends AbstractControl implements CanUndoRedo, CanCancel {
	private final Class<H> handlerClass;
	
	protected VectorFeatureLayer layer = null;
	protected H handlerInstance = null;
	
	protected Style featureStyle = null;
	protected Style cursorStyle = null;
	
	public DrawFeatureControl(VectorFeatureLayer layer) {
		super();
		controlType = ControlType.TOOL;
		
		this.handlerClass = getGenericHandlerTypeClass();
		
		setLayer(layer);
		initHandler();
		setCursorStyle(Style.DEFAULT_DRAW_CURSOR);
	}
	
	@SuppressWarnings("unchecked")
	private Class<H> getGenericHandlerTypeClass() {
		return (Class<H>) ClassUtility.getGenericTypeClass(this.getClass(), 0);
	}

	private void initHandler() {
		handlerInstance = createHandler();
		if (handlerInstance != null) {
			setHandler(handlerInstance);
		}
		provideLayerToHandler();
		provideFeatureStyleToHandler();
	}
	
	private void provideLayerToHandler() {
		if (handler != null && handler instanceof RequiresVectorFeatureLayer) {
			((RequiresVectorFeatureLayer)handler).setLayer(layer);
		}
	}

	private void provideFeatureStyleToHandler() {
		if (handler != null && handler instanceof FeatureHandler) {
			((FeatureHandler)handler).setFeatureStyle(featureStyle);
		}
	}

	private H createHandler() {
		try {
			return handlerClass.getDeclaredConstructor(Control.class).newInstance(this);
			
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void setLayer(VectorFeatureLayer layer) {
		this.layer = layer;
		getState().layer = layer;
		provideLayerToHandler();
	}
	
	@Override
	protected DrawFeatureControlState getState() {
		return (DrawFeatureControlState) super.getState();
	}
	
	public Style getFeatureStyle() {
		return featureStyle;
	}

	public void setFeatureStyle(Style style) {
		this.featureStyle = style;
		provideFeatureStyleToHandler();
	}

	public Style getCursorStyle() {
		return cursorStyle;
	}
	
	public void setCursorStyle(Style style) {
		this.cursorStyle = style;
		getState().cursorStyle = StyleUtility.getStyleMap(style);
		markAsDirty();
	}
	
	@Override
	public boolean undo() {
		return handler != null && handler instanceof CanUndoRedo && ((CanUndoRedo)handler).undo();
	}

	@Override
	public boolean redo() {
		return handler != null && handler instanceof CanUndoRedo && ((CanUndoRedo)handler).redo();
	}

	@Override
	public void cancel() {
		if (handler != null)
			handler.cancel();
	}
	
	public void addDrawFeatureListener(DrawFeatureListener listener) {
		if (handlerInstance != null) {
			handlerInstance.addDrawFeatureListener(listener);
		}
	}
	
	public void removeDrawFeatureListener(DrawFeatureListener listener) {
		if (handlerInstance != null) {
			handlerInstance.removeDrawFeatureListener(listener);
		}
	}
	
	public H getHandler() {
		return handlerInstance;
	}

}

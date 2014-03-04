/**
 * 
 */
package org.vaadin.maps.ui.control;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.vaadin.maps.shared.ui.control.DrawFeatureControlState;
import org.vaadin.maps.ui.CanCancel;
import org.vaadin.maps.ui.CanUndoRedo;
import org.vaadin.maps.ui.handler.AbstractHandler;
import org.vaadin.maps.ui.handler.RequiresVectorFeatureLayer;
import org.vaadin.maps.ui.layer.VectorFeatureLayer;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
@SuppressWarnings("serial")
public abstract class DrawFeatureControl<H extends AbstractHandler> extends AbstractControl implements CanUndoRedo, CanCancel {
	private final Class<H> handlerClass;
	
	protected VectorFeatureLayer layer = null;
	protected H handlerInstance = null;
	
	public DrawFeatureControl(VectorFeatureLayer layer) {
		super();
		
		this.handlerClass = getGenericHandlerTypeClass();
		
		setLayer(layer);
		initHandler();
	}
	
	@SuppressWarnings("unchecked")
	private Class<H> getGenericHandlerTypeClass() {
		Class<?> superClass = this.getClass().getSuperclass();
		Type genericSuperClass = this.getClass().getGenericSuperclass();
		while (!(genericSuperClass instanceof ParameterizedType)) {
			genericSuperClass = superClass.getGenericSuperclass();
			superClass = superClass.getSuperclass();
		}

		return (Class<H>) ((ParameterizedType) genericSuperClass).getActualTypeArguments()[0];
	}

	private void initHandler() {
		handlerInstance = createHandler();
		if (handlerInstance != null) {
			setHandler(handlerInstance);
		}
		provideLayerToHandler();
	}
	
	private void provideLayerToHandler() {
		if (handler != null && handler instanceof RequiresVectorFeatureLayer) {
			((RequiresVectorFeatureLayer)handler).setLayer(layer);
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

}

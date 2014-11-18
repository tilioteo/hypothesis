/**
 * 
 */
package org.vaadin.maps.ui.control;

import java.lang.reflect.InvocationTargetException;

import org.vaadin.maps.server.ClassUtility;
import org.vaadin.maps.shared.ui.control.NavigateControlState;
import org.vaadin.maps.ui.LayerLayout;
import org.vaadin.maps.ui.handler.NavigateHandler;
import org.vaadin.maps.ui.handler.RequiresLayerLayout;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public abstract class NavigateControl<H extends NavigateHandler> extends AbstractControl {
	private final Class<H> handlerClass;
	
	LayerLayout layout = null;
	protected H handlerInstance = null;
	
	public NavigateControl(LayerLayout layout) {
		super();
		controlType = ControlType.TOOL;
		
		this.handlerClass = getGenericHandlerTypeClass();
		
		setLayout(layout);
		initHandler();
	}
	
	private void initHandler() {
		handlerInstance = createHandler();
		if (handlerInstance != null) {
			setHandler(handlerInstance);
		}
		provideLayoutToHandler();
	}
	
	private void provideLayoutToHandler() {
		if (handler != null && handler instanceof RequiresLayerLayout) {
			((RequiresLayerLayout)handler).setLayout(layout);
		}
	}

	@SuppressWarnings("unchecked")
	private Class<H> getGenericHandlerTypeClass() {
		return (Class<H>) ClassUtility.getGenericTypeClass(this.getClass(), 0);
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
	
	public void setLayout(LayerLayout layout) {
		this.layout = layout;
		getState().layout = layout;
		provideLayoutToHandler();
	}

	@Override
	protected NavigateControlState getState() {
		return (NavigateControlState)super.getState();
	}
	
	public H getHandler() {
		return handlerInstance;
	}

}

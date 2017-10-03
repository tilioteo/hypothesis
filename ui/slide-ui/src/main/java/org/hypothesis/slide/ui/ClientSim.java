/**
 * 
 */
package org.hypothesis.slide.ui;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Random;

import org.hypothesis.interfaces.TimerHandler;
import org.vaadin.special.event.ComponentEvent;
import org.vaadin.special.ui.Image.LoadListener;
import org.vaadin.special.ui.Timer;
import org.vaadin.special.ui.Timer.StopEvent;
import org.vaadin.special.ui.Timer.StopListener;

import com.vaadin.event.ConnectorEventListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.util.ReflectTools;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class ClientSim extends Panel {

	private enum State {
		STOPPED, RUNNING, STOPPING
	}

	public static String CLIENT_EVENT = "client";

	private State state;
	private Timer timer;
	private Button button;

	public ClientSim() {
		super();

		setWidth(100, Unit.PIXELS);
		setHeight(100, Unit.PIXELS);

		timer = new Timer();
		timer.addStopListener(new StopListener() {
			@Override
			public void stop(StopEvent event) {
				Date now = new Date();
				fireEvent(new ClientEvent(now.getTime(), ClientSim.this));
				if (state == State.STOPPING) {
					setStoppedState();
				} else if (state == State.RUNNING) {
					timer.start(generateTime());
				}
			}
		});

		button = new Button();
		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				switch (state) {
				case STOPPED:
					start();
					break;
				case RUNNING:
					stop();
					break;
				default:
					break;
				}
			}
		});
		VerticalLayout layout = new VerticalLayout(button);
		layout.setSizeFull();
		layout.addComponent(button);
		setContent(layout);

		setStoppedState();
	}

	private void setStoppedState() {
		state = State.STOPPED;

		button.setCaption("Start");
		button.setEnabled(true);
	}

	private void setStoppingState() {
		state = State.STOPPING;

		button.setCaption("Stop");
		button.setEnabled(false);
	}

	private void setRunningState() {
		state = State.RUNNING;

		button.setCaption("Stop");
		button.setEnabled(true);
	}

	public void start() {
		if (state == State.STOPPED) {
			setRunningState();
			timer.start(generateTime());
		}
	}

	public void stop() {
		if (state == State.RUNNING) {
			setStoppingState();
			timer.stop();
		}
	}

	private long generateTime() {
		Random random = new Random();
		return 10 * random.nextInt(500);
	}
	
	@Override
	public void attach() {
		super.attach();
		
		UI ui = getUI();
		if (ui instanceof TimerHandler) {
			((TimerHandler) ui).addTimer(timer);
		}
	}
	
	@Override
	public void detach() {
		UI ui = getUI();
		if (ui instanceof TimerHandler) {
			((TimerHandler) ui).removeTimer(timer);
		}

		super.detach();
	}

	public static class ClientEvent extends ComponentEvent {

		public ClientEvent(long timestamp, Component source) {
			super(timestamp, source);
		}

	}

	public void addClientListener(ClientListener listener) {
		addListener(CLIENT_EVENT, ClientEvent.class, listener, ClientListener.clientMethod);
	}

	public void removeLoadListener(LoadListener listener) {
		removeListener(CLIENT_EVENT, ClientEvent.class, listener);
	}

	/**
	 * Interface for listening for a {@link ClientEvent} fired by a
	 * {@link ClientSim}.
	 * 
	 * @see ClientEvent
	 * @author kamil
	 */
	public interface ClientListener extends ConnectorEventListener {

		public static final Method clientMethod = ReflectTools.findMethod(ClientListener.class, CLIENT_EVENT,
				ClientEvent.class);

		public void client(ClientEvent event);
	}

}

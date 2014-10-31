/*
 * Copyright 2013 Tomi Virtanen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vaadin.tltv.vprocjs.ui;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.vaadin.tltv.vprocjs.client.ui.ProcessingEventServerRpc;
import org.vaadin.tltv.vprocjs.shared.ui.processing.ProcessingServerRpc;
import org.vaadin.tltv.vprocjs.shared.ui.processing.ProcessingState;

import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

/**
 * Server side Vaadin component wrapping processing.js javascript library.
 * </br></br>
 * 
 * Requires that processing.min.js file exist in the
 * org.vaadin.tltv.vprocjs.ui package.
 * 
 * @author Tltv
 * 
 */
@JavaScript({ "processing.min.js" })
@SuppressWarnings("serial")
public class Processing extends AbstractComponent {

	private static final long serialVersionUID = 1L;

	private ProcessingServerRpc rpc = new ProcessingServerRpc() {

		@Override
		public void mouseWheel(boolean up, int deltaY) {
			fireEvent(new MouseWheelEvent(Processing.this, up, deltaY));
		}

		@Override
		public void mouseRelease(int mouseX, int mouseY) {
			fireEvent(new MouseReleaseEvent(Processing.this, mouseX, mouseY));
		}

		@Override
		public void mousePress(int mouseX, int mouseY) {
			fireEvent(new MousePressEvent(Processing.this, mouseX, mouseY));
		}

		@Override
		public void mouseLeave() {
			fireEvent(new MouseLeaveEvent(Processing.this));
		}

		@Override
		public void mouseEnter() {
			fireEvent(new MouseEnterEvent(Processing.this));
		}

		@Override
		public void keyRelease() {
			fireEvent(new KeyReleaseEvent(Processing.this));
		}

		@Override
		public void keyPress(int key) {
			fireEvent(new KeyPressEvent(Processing.this, key));
		}

		@Override
		public void click() {
			fireEvent(new ClickEvent(Processing.this));
		}
	};

	private ProcessingEventServerRpc eventServerRpc = new ProcessingEventServerRpc() {

		@Override
		public void mouseReleased() {
		}

		@Override
		public void mousePressed() {
		}

		@Override
		public void mouseMoved() {
		}

		@Override
		public void mouseDragged() {
		}

		@Override
		public void mouseClicked() {
		}

		@Override
		public void keyTyped() {
		}

		@Override
		public void keyReleased() {
		}

		@Override
		public void keyPressed() {
		}
	};

	/** Current Processing Visualization Language code. */
	private String processingCode = "";

	private final List<MouseClickListener> mouseClickListeners = new ArrayList<MouseClickListener>();
	private final List<MousePressListener> mousePressListeners = new ArrayList<MousePressListener>();
	private final List<MouseReleaseListener> mouseReleaseListeners = new ArrayList<MouseReleaseListener>();
	private final List<MouseEnterListener> mouseEnterListeners = new ArrayList<MouseEnterListener>();
	private final List<MouseLeaveListener> mouseLeaveListeners = new ArrayList<MouseLeaveListener>();
	private final List<MouseWheelListener> mouseWheelListeners = new ArrayList<MouseWheelListener>();

	private final List<KeyPressListener> keyPressListeners = new ArrayList<KeyPressListener>();
	private final List<KeyReleaseListener> keyReleaseListeners = new ArrayList<KeyReleaseListener>();

	//private AbsoluteLayout mainLayout;

	public Processing() {
		registerRpc(rpc);
		registerRpc(eventServerRpc);
		/*buildMainLayout();
		setCompositionRoot(mainLayout);*/
	}

	/*private void buildMainLayout() {
		mainLayout = new AbsoluteLayout();
	}*/

	@Override
	public ProcessingState getState() {
		return (ProcessingState) super.getState();
	}

	public String getProcessingCode() {
		return processingCode;
	}

	public void setProcessingCode(String code) {
		processingCode = code;
		getState().processingCode = code != null ? code : "";
	}

	public void addMouseClickListener(MouseClickListener listener) {
		if (!mouseClickListeners.contains(listener)) {
			mouseClickListeners.add(listener);
			getState().mouseClickListened = true;
			addListener(ClickEvent.class, listener, MouseClickListener.MOUSE_CLICK_METHOD);
		}
	}

	public void removeMouseClickListener(MouseClickListener listener) {
		removeListener(ClickEvent.class, listener, MouseClickListener.MOUSE_CLICK_METHOD);
		mouseClickListeners.remove(listener);
		if (mouseClickListeners.isEmpty()) {
			getState().mouseClickListened = false;
		}
	}

	public void addMousePressListener(MousePressListener listener) {
		if (!mousePressListeners.contains(listener)) {
			mousePressListeners.add(listener);
			getState().mousePressListened = true;
			addListener(MousePressEvent.class, listener, MousePressListener.MOUSE_PRESS_METHOD);
		}
	}

	public void removeMousePressListener(MousePressListener listener) {
		removeListener(MousePressEvent.class, listener, MousePressListener.MOUSE_PRESS_METHOD);
		mousePressListeners.remove(listener);
		if (mousePressListeners.isEmpty()) {
			getState().mousePressListened = false;
		}
	}

	public void addMouseReleaseListener(MouseReleaseListener listener) {
		if (!mouseReleaseListeners.contains(listener)) {
			mouseReleaseListeners.add(listener);
			getState().mouseReleaseListened = true;
			addListener(MouseReleaseEvent.class, listener, MouseReleaseListener.MOUSE_RELEASE_METHOD);
		}
	}

	public void removeMouseReleaseListener(MouseReleaseListener listener) {
		removeListener(MouseReleaseEvent.class, listener, MouseReleaseListener.MOUSE_RELEASE_METHOD);
		mouseReleaseListeners.remove(listener);
		if (mouseReleaseListeners.isEmpty()) {
			getState().mouseReleaseListened = false;
		}
	}

	public void addKeyPressListener(KeyPressListener listener) {
		if (!keyPressListeners.contains(listener)) {
			keyPressListeners.add(listener);
			getState().keyPressListened = true;
			addListener(KeyPressEvent.class, listener, KeyPressListener.KEY_PRESS_METHOD);
		}
	}

	public void removeKeyPressListener(KeyPressListener listener) {
		removeListener(KeyPressEvent.class, listener, KeyPressListener.KEY_PRESS_METHOD);
		keyPressListeners.remove(listener);
		if (keyPressListeners.isEmpty()) {
			getState().keyPressListened = false;
		}
	}

	public void addKeyReleaseListener(KeyReleaseListener listener) {
		if (!keyReleaseListeners.contains(listener)) {
			keyReleaseListeners.add(listener);
			getState().keyReleaseListened = true;
			addListener(KeyReleaseEvent.class, listener, KeyReleaseListener.KEY_RELEASE_METHOD);
		}
	}

	public void removeKeyReleaseListener(KeyReleaseListener listener) {
		removeListener(KeyReleaseEvent.class, listener, KeyReleaseListener.KEY_RELEASE_METHOD);
		keyReleaseListeners.remove(listener);
		if (keyReleaseListeners.isEmpty()) {
			getState().keyReleaseListened = false;
		}
	}

	public void addMouseEnterListener(MouseEnterListener listener) {
		if (!mouseEnterListeners.contains(listener)) {
			mouseEnterListeners.add(listener);
			getState().mouseEnterListened = true;
			addListener(MouseEnterEvent.class, listener, MouseEnterListener.MOUSE_ENTER_METHOD);
		}
	}

	public void removeMouseEnterListener(MouseEnterListener listener) {
		removeListener(MouseEnterEvent.class, listener, MouseEnterListener.MOUSE_ENTER_METHOD);
		mouseEnterListeners.remove(listener);
		if (mouseEnterListeners.isEmpty()) {
			getState().mouseEnterListened = false;
		}
	}

	public void addMouseLeaveListener(MouseLeaveListener listener) {
		if (!mouseLeaveListeners.contains(listener)) {
			mouseLeaveListeners.add(listener);
			getState().mouseLeaveListened = true;
			addListener(MouseLeaveEvent.class, listener, MouseLeaveListener.MOUSE_LEAVE_METHOD);
		}
	}

	public void removeMouseLeaveListener(MouseLeaveListener listener) {
		removeListener(MouseLeaveEvent.class, listener, MouseLeaveListener.MOUSE_LEAVE_METHOD);
		mouseLeaveListeners.remove(listener);
		if (mouseLeaveListeners.isEmpty()) {
			getState().mouseEnterListened = false;
		}
	}

	public void addMouseWheelListener(MouseWheelListener listener) {
		if (!mouseWheelListeners.contains(listener)) {
			mouseWheelListeners.add(listener);
			getState().mouseWheelListened = true;
			addListener(MouseWheelEvent.class, listener, MouseWheelListener.MOUSE_WHEEL_METHOD);
		}
	}

	public void removeMouseWheelListener(MouseWheelListener listener) {
		removeListener(MouseWheelEvent.class, listener, MouseWheelListener.MOUSE_WHEEL_METHOD);
		mouseWheelListeners.remove(listener);
		if (mouseWheelListeners.isEmpty()) {
			getState().mouseWheelListened = false;
		}
	}

	public class ClickEvent extends Component.Event {

		public ClickEvent(Component source) {
			super(source);
		}

		@Override
		public Processing getSource() {
			return (Processing) super.getSource();
		}
	}

	public interface MouseClickListener extends Serializable {

		public static final Method MOUSE_CLICK_METHOD = ReflectTools
				.findMethod(MouseClickListener.class, "mouseClick",
						ClickEvent.class);

		public void mouseClick(ClickEvent event);
	}

	public class MousePressEvent extends Component.Event {

		private final int x;

		private final int y;

		public MousePressEvent(Component source, int x, int y) {
			super(source);
			this.x = x;
			this.y = y;
		}

		@Override
		public Processing getSource() {
			return (Processing) super.getSource();
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
	}

	public interface MousePressListener extends Serializable {

		public static final Method MOUSE_PRESS_METHOD = ReflectTools
				.findMethod(MousePressListener.class, "mousePress",
						MousePressEvent.class);

		public void mousePress(MousePressEvent event);
	}

	public class MouseReleaseEvent extends Component.Event {

		private final int x;

		private final int y;

		public MouseReleaseEvent(Component source, int x, int y) {
			super(source);
			this.x = x;
			this.y = y;
		}

		@Override
		public Processing getSource() {
			return (Processing) super.getSource();
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
	}

	public interface MouseReleaseListener extends Serializable {

		public static final Method MOUSE_RELEASE_METHOD = ReflectTools
				.findMethod(MouseReleaseListener.class, "mouseRelease",
						MouseReleaseEvent.class);

		public void mouseRelease(MouseReleaseEvent event);
	}

	public class KeyPressEvent extends Component.Event {

		private final int key;

		public KeyPressEvent(Component source, int key) {
			super(source);
			this.key = key;
		}

		@Override
		public Processing getSource() {
			return (Processing) super.getSource();
		}

		public int getKey() {
			return key;
		}
	}

	public interface KeyPressListener extends Serializable {

		public static final Method KEY_PRESS_METHOD = ReflectTools.findMethod(
				KeyPressListener.class, "keyPress", KeyPressEvent.class);

		public void keyPress(KeyPressEvent event);
	}

	public class KeyReleaseEvent extends Component.Event {

		public KeyReleaseEvent(Component source) {
			super(source);
		}

		@Override
		public Processing getSource() {
			return (Processing) super.getSource();
		}
	}

	public interface KeyReleaseListener extends Serializable {

		public static final Method KEY_RELEASE_METHOD = ReflectTools
				.findMethod(KeyReleaseListener.class, "keyRelease",
						KeyReleaseEvent.class);

		public void keyRelease(KeyReleaseEvent event);
	}

	public class MouseEnterEvent extends Component.Event {

		public MouseEnterEvent(Component source) {
			super(source);
		}

		@Override
		public Processing getSource() {
			return (Processing) super.getSource();
		}
	}

	public interface MouseEnterListener extends Serializable {

		public static final Method MOUSE_ENTER_METHOD = ReflectTools
				.findMethod(MouseEnterListener.class, "mouseEnter",
						MouseEnterEvent.class);

		public void mouseEnter(MouseEnterEvent event);
	}

	public class MouseLeaveEvent extends Component.Event {

		public MouseLeaveEvent(Component source) {
			super(source);
		}

		@Override
		public Processing getSource() {
			return (Processing) super.getSource();
		}
	}

	public interface MouseLeaveListener extends Serializable {

		public static final Method MOUSE_LEAVE_METHOD = ReflectTools
				.findMethod(MouseLeaveListener.class, "mouseLeave",
						MouseLeaveEvent.class);

		public void mouseLeave(MouseLeaveEvent event);
	}

	public class MouseWheelEvent extends Component.Event {

		private final boolean up;

		private final int deltaY;

		public MouseWheelEvent(Component source, boolean up, int deltaY) {
			super(source);
			this.up = up;
			this.deltaY = deltaY;
		}

		@Override
		public Processing getSource() {
			return (Processing) super.getSource();
		}

		public boolean isUp() {
			return up;
		}

		public boolean isDown() {
			return !up;
		}

		public int getDeltaY() {
			return deltaY;
		}
	}

	public interface MouseWheelListener extends Serializable {

		public static final Method MOUSE_WHEEL_METHOD = ReflectTools
				.findMethod(MouseWheelListener.class, "mouseWheel",
						MouseWheelEvent.class);

		public void mouseWheel(MouseWheelEvent event);
	}

}

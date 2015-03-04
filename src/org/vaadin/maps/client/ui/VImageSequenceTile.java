/**
 * 
 */
package org.vaadin.maps.client.ui;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author kamil
 * 
 */
public class VImageSequenceTile extends VImageTile {

	public static final String CLASSNAME = "v-imagesequencetile";

	private int index = 0;
	private LinkedList<String> urls = new LinkedList<String>();
	
	private int loadErrors;

	public VImageSequenceTile() {
		setStylePrimaryName(CLASSNAME);
	}

	public void setUrls(List<String> urls) {
		this.urls.clear();
		this.urls.addAll(urls);
		loadImages();
		setIndex(0);
	}

	private final void incLoadErrors() {
		++loadErrors;
	}

	private void loadImages() {
		loadErrors = 0;
		final HashSet<String> urlsToLoad = new HashSet<String>();
		for (String url : urls) {
			url = url.trim();
			if (url != null && url.length() > 0 && !urlsToLoad.contains(url)) {
				urlsToLoad.add(url);
			}
		}

		if (urlsToLoad.isEmpty()) {
			fireEvent(new SequenceErrorEvent(VImageSequenceTile.this, loadErrors));
		} else {
			for (final String url : urlsToLoad) {
				final Image image = new Image(url);
				
				image.addLoadHandler(new LoadHandler() {
					@Override
					public void onLoad(LoadEvent event) {
						RootPanel.get().remove(image);
						urlsToLoad.remove(url);

						if (urlsToLoad.isEmpty()) {
							if (loadErrors > 0) {
								fireEvent(new SequenceErrorEvent(VImageSequenceTile.this, loadErrors));
							} else {
								fireEvent(new SequenceLoadedEvent(VImageSequenceTile.this));
							}
						}
					}
				});

				image.addErrorHandler(new ErrorHandler() {
					@Override
					public void onError(ErrorEvent event) {
						incLoadErrors();
						RootPanel.get().remove(image);
						urlsToLoad.remove(url);

						if (urlsToLoad.isEmpty()) {
							if (loadErrors > 0) {
								fireEvent(new SequenceErrorEvent(VImageSequenceTile.this, loadErrors));
							} else {
								fireEvent(new SequenceLoadedEvent(VImageSequenceTile.this));
							}
						}
					}
				});

				image.setVisible(false);
				image.setSize("0px", "0px");
				RootPanel.get().add(image);
			}
		}
	}
	
	private void updateImage() {
		setUrl(urls.get(index));
    	setVisible(true);
	}
	
	public void setIndex(int index) {
		if (index < urls.size()) {
			this.index = index;
		
			updateImage();
		}
	}
	
	public int getIndex() {
		return index;
	}
	
	public interface SequenceLoadedHandler extends EventHandler {
		void loaded(SequenceLoadedEvent event);
	}

	public static class SequenceLoadedEvent extends GwtEvent<SequenceLoadedHandler> {

		public static final Type<SequenceLoadedHandler> TYPE = new Type<SequenceLoadedHandler>();

		public SequenceLoadedEvent(VImageSequenceTile imageSequenceTile) {
			setSource(imageSequenceTile);
		}

		@Override
		public Type<SequenceLoadedHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(SequenceLoadedHandler handler) {
			handler.loaded(this);
		}
	}

	public interface SequenceErrorHandler extends EventHandler {
		void error(SequenceErrorEvent event);
	}

	public static class SequenceErrorEvent extends GwtEvent<SequenceErrorHandler> {

		public static final Type<SequenceErrorHandler> TYPE = new Type<SequenceErrorHandler>();

		private int errorCount;
		
		public SequenceErrorEvent(VImageSequenceTile imageSequenceTile, int errorCount) {
			setSource(imageSequenceTile);
		}
		
		public int getErrorCount() {
			return errorCount;
		}

		@Override
		public Type<SequenceErrorHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(SequenceErrorHandler handler) {
			handler.error(this);
		}
	}
	
	public HandlerRegistration addSequenceLoadedHandler(SequenceLoadedHandler handler) {
		return addHandler(handler, SequenceLoadedEvent.TYPE);
	}
	
	public HandlerRegistration addSequenceErrorHandler(SequenceErrorHandler handler) {
		return addHandler(handler, SequenceErrorEvent.TYPE);
	}

}

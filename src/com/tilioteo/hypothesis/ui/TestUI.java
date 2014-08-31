/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import javax.servlet.annotation.WebServlet;

import org.vaadin.maps.ui.LayerLayout;
import org.vaadin.maps.ui.control.DrawPathControl;
import org.vaadin.maps.ui.feature.VectorFeature;
import org.vaadin.maps.ui.feature.VectorFeature.DoubleClickEvent;
import org.vaadin.maps.ui.feature.VectorFeature.DoubleClickListener;
import org.vaadin.maps.ui.featurecontainer.VectorFeatureContainer;
import org.vaadin.maps.ui.layer.ControlLayer;
import org.vaadin.maps.ui.layer.ImageLayer;
import org.vaadin.maps.ui.layer.ImageSequenceLayer;
import org.vaadin.maps.ui.layer.VectorFeatureLayer;
import org.vaadin.maps.ui.tile.ImageSequenceTile;
import org.vaadin.maps.ui.tile.ImageSequenceTile.ChangeEvent;
import org.vaadin.maps.ui.tile.ImageSequenceTile.LoadEvent;
import org.vaadin.maps.ui.tile.ProxyTile;
import org.vaadin.maps.ui.tile.ProxyTile.LoadListener;

import com.tilioteo.hypothesis.ui.Media.CanPlayThroughEvent;
import com.tilioteo.hypothesis.ui.Media.StartEvent;
import com.tilioteo.hypothesis.ui.Media.StopEvent;
import com.tilioteo.hypothesis.ui.Video.ClickEvent;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
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
		*/
		
		LayerLayout layerLayout = new LayerLayout();
		verticalLayout.addComponent(layerLayout);
		
		/*
		ImageLayer imageLayer = new ImageLayer("http://www.imagehosting.cz/images/mapaukol7.jpg");
		imageLayer.addClickListener(new MouseEvents.ClickListener() {
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
		layerLayout.addComponent(imageLayer);
		*/
		
		final ImageSequenceLayer imageSequenceLayer = new ImageSequenceLayer();
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
				Notification.show("Image sequence loaded");
			}
		});
		
		imageSequenceLayer.addChangeListener(new ImageSequenceTile.ChangeListener() {
			@Override
			public void change(ChangeEvent event) {
				Notification.show("Image changed");
			}
		});
		
		layerLayout.addComponent(imageSequenceLayer);
		
		VectorFeatureLayer vectorLayer = new VectorFeatureLayer();
		vectorLayer.addClickListener(new VectorFeatureContainer.ClickListener() {
			@Override
			public void click(VectorFeatureContainer.ClickEvent event) {
				Notification.show("Vector layer container clicked");
				int index = imageSequenceLayer.getTileIndex();
				imageSequenceLayer.setTileIndex(++index);
			}
		});
		layerLayout.addComponent(vectorLayer);

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
		 	/*
		 	feature.addDoubleClickListener(new DoubleClickListener() {
				@Override
				public void doubleClick(DoubleClickEvent event) {
					Notification.show("Feature double clicked");
				}
			});
			*/
		 	vectorLayer.addComponent(feature);
		 			 	
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		ControlLayer controlLayer = new ControlLayer();
		layerLayout.addComponent(controlLayer);
		
		DrawPathControl drawControl = new DrawPathControl(vectorLayer);
		//DrawPointControl drawControl = new DrawPointControl(vectorLayer);
		controlLayer.addComponent(drawControl);
		drawControl.activate();
		*/

	}

}

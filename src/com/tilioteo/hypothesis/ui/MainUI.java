/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import javax.servlet.annotation.WebServlet;

import org.vaadin.maps.ui.LayerLayout;
import org.vaadin.maps.ui.control.DrawPathControl;
import org.vaadin.maps.ui.control.DrawPointControl;
import org.vaadin.maps.ui.feature.VectorFeature;
import org.vaadin.maps.ui.feature.VectorFeature.ClickEvent;
import org.vaadin.maps.ui.feature.VectorFeature.ClickListener;
import org.vaadin.maps.ui.feature.VectorFeature.DoubleClickEvent;
import org.vaadin.maps.ui.feature.VectorFeature.DoubleClickListener;
import org.vaadin.maps.ui.featurecontainer.VectorFeatureContainer;
import org.vaadin.maps.ui.layer.ControlLayer;
import org.vaadin.maps.ui.layer.ImageLayer;
import org.vaadin.maps.ui.layer.VectorFeatureLayer;
import org.vaadin.maps.ui.tile.ImageTile;
import org.vaadin.maps.ui.tile.ImageTile.LoadEvent;

import com.tilioteo.hypothesis.servlet.HibernateServlet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
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
public class MainUI extends HUI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = MainUI.class, widgetset = "com.tilioteo.hypothesis.HypothesisWidgetset")
	public static class Servlet extends HibernateServlet {
	}

	private Navigator navigator;

	@Override
	protected void init(VaadinRequest request) {
		getPage().setTitle("Hypothesis");

		/*navigator = new Navigator(this, this);

		navigator.addView("/packs", PacksView.class);

		navigator.navigateTo("/packs");*/
		
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setSizeFull();
		setContent(verticalLayout);
		
		/*Timer timer = new Timer();
		addTimer(timer);
		timer.setDirection(Direction.DOWN);
		
		TimerLabel timerLabel = new TimerLabel();
		//timerLabel.setTimeFormat("kk:mm:ss.S");
		timerLabel.setTimer(timer);
		
		verticalLayout.addComponent(timerLabel);
		
		timer.addStopListener(new StopListener() {
			@Override
			public void stop(StopEvent event) {
				Notification.show("Time out!", Type.HUMANIZED_MESSAGE);
			}
		});
		
		timer.addUpdateListener(2000, new UpdateListener() {
			@Override
			public void update(UpdateEvent event) {
				Notification.show(String.format("%d miliseconds, interval %d ms", event.getTime(), event.getInterval()));
			}
		});
		
		timer.addUpdateListener(5000, new UpdateListener() {
			@Override
			public void update(UpdateEvent event) {
				Notification.show(String.format("%d miliseconds, interval %d ms", event.getTime(), event.getInterval()));
			}
		});
		
		timer.start(30000);
		*/
		
		LayerLayout layerLayout = new LayerLayout();
		verticalLayout.addComponent(layerLayout);
		
		ControlLayer controlLayer = new ControlLayer();
		layerLayout.addComponent(controlLayer);
		
		ImageLayer imageLayer = new ImageLayer("http://www.imagehosting.cz/images/mapaukol7.jpg");
		imageLayer.addClickListener(new ImageTile.ClickListener() {
			@Override
			public void click(ImageTile.ClickEvent event) {
				Notification.show("Image tile clicked");
			}
		});
		imageLayer.addLoadListener(new ImageTile.LoadListener() {
			@Override
			public void load(LoadEvent event) {
				Notification.show("Image tile loaded");
			}
		});
		layerLayout.addComponent(imageLayer);
		
		VectorFeatureLayer vectorLayer = new VectorFeatureLayer();
		vectorLayer.addClickListener(new VectorFeatureContainer.ClickListener() {
			@Override
			public void click(VectorFeatureContainer.ClickEvent event) {
				Notification.show("Vector layer container clicked");
			}
		});
		layerLayout.addComponent(vectorLayer);

		WKTReader wktReader = new WKTReader();
		try {
		 	Geometry geometry = wktReader.read("POLYGON ((50 50,200 50,200 200,50 200,50 50),(100 100,150 100,150 150,100 150,100 100))");
		 	
		 	VectorFeature feature = new VectorFeature(geometry);
		 	feature.addClickListener(new ClickListener() {
				@Override
				public void click(ClickEvent event) {
					Notification.show("Feature clicked");
				}
			});
		 	
		 	/*feature.addDoubleClickListener(new DoubleClickListener() {
				@Override
				public void doubleClick(DoubleClickEvent event) {
					Notification.show("Feature double clicked");
				}
			});*/
		 	vectorLayer.addComponent(feature);
		 			 	
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DrawPathControl drawControl = new DrawPathControl(vectorLayer);
		//DrawPointControl drawControl = new DrawPointControl(vectorLayer);
		controlLayer.addComponent(drawControl);
		drawControl.activate();
	}

}
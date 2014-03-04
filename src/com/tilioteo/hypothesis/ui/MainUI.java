/**
 * 
 */
package com.tilioteo.hypothesis.ui;

import javax.servlet.annotation.WebServlet;

import com.tilioteo.hypothesis.context.HypothesisConfig;
import com.tilioteo.hypothesis.servlet.HibernateVaadinServlet;
import com.tilioteo.hypothesis.ui.view.PacksView;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

/**
 * @author kamil
 * 
 */
@SuppressWarnings("serial")
@Theme("hypothesis")
public class MainUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = MainUI.class, widgetset = "com.tilioteo.hypothesis.HypothesisWidgetset")
	public static class Servlet extends HibernateVaadinServlet {
	}

	private Navigator navigator;
	
	@Override
	protected void init(VaadinRequest request) {
		
		/*WrappedSession session = request.getWrappedSession();
		HttpSession httpSession = ((WrappedHttpSession)session).getHttpSession();
		ServletContext servletContext = httpSession.getServletContext();
		ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		
		config = (HypothesisConfig)context.getBean(HypothesisConfig.class);
		*/
		//config.getSecretKey();

		getPage().setTitle("Hypothesis");

		navigator = new Navigator(this, this);
		
		PacksView packsView = new PacksView();
		navigator.addView("/packs", packsView);
		
		navigator.navigateTo("/packs");
		
/*		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setSizeFull();
		setContent(verticalLayout);
*/		
		
/*		LayerLayout layerLayout = new LayerLayout();
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
*/
/*		WKTReader wktReader = new WKTReader();
		try {
		 	Geometry geometry = wktReader.read("POLYGON ((50 50,200 50,200 200,50 200,50 50),(100 100,150 100,150 150,100 150,100 100))");
		 	
		 	VectorFeature feature = new VectorFeature(geometry);
		 	feature.addClickListener(new ClickListener() {
				@Override
				public void click(ClickEvent event) {
					Notification.show("Feature clicked");
				}
			});
*/		 	
		 	/*feature.addDoubleClickListener(new DoubleClickListener() {
				@Override
				public void doubleClick(DoubleClickEvent event) {
					Notification.show("Feature double clicked");
				}
			});*/
/*		 	vectorLayer.addComponent(feature);
		 			 	
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/		
/*		DrawPathControl drawControl = new DrawPathControl(vectorLayer);
		//DrawPointControl drawControl = new DrawPointControl(vectorLayer);
		controlLayer.addComponent(drawControl);
		drawControl.activate();
*/
	}
}
/**
 * 
 */
package com.tilioteo.hypothesis.ui.view;

import java.util.List;

import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.model.PacksModel;
import com.tilioteo.hypothesis.ui.PackPanel;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class SimplePacksView extends HypothesisView {
	private PacksModel packsModel;
	private VerticalLayout packLayout;
	
	public SimplePacksView(String hash) {
		packsModel = new PacksModel();
		
		setSizeFull();
		
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setSizeFull();
		addComponent(verticalLayout);
		
		Panel topPanel = new Panel();
		topPanel.setWidth("100%");
		topPanel.setHeight("100px");
		verticalLayout.addComponent(topPanel);
		
		VerticalLayout panelLayout = new VerticalLayout();
		panelLayout.setSizeFull();
		topPanel.setContent(panelLayout);
		
		Label label = new Label("<H1>Hypothesis<H1>");
		label.setContentMode(ContentMode.HTML);
		label.setWidth(null);
		panelLayout.addComponent(label);
		panelLayout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
		panelLayout.setMargin(true);
		panelLayout.setSpacing(true);
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSizeFull();
		verticalLayout.addComponent(horizontalLayout);
		verticalLayout.setExpandRatio(horizontalLayout, 1.0f);
		
		Panel mainPanel = new Panel();
		mainPanel.setSizeFull();
		horizontalLayout.addComponent(mainPanel);
		
		packLayout = new VerticalLayout();
		packLayout.setHeight(null);
		packLayout.setWidth("100%");
		mainPanel.setContent(packLayout);

		
		List<Pack> packs = (hash != null ? packsModel.getPackByHash(hash) : packsModel.getSimplePublicPacks());
				
		for (Pack pack : packs) {
			PackPanel packPanel = new PackPanel(pack);
			
			packLayout.addComponent(packPanel);
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}

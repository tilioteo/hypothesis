package org.hypothesis.presenter;

import java.util.ArrayList;
import java.util.List;

import org.hypothesis.data.CaseInsensitiveItemSorter;
import org.hypothesis.data.dto.PackDto;
import org.hypothesis.data.dto.PackSetDto;
import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.service.PackSetService;
import org.hypothesis.data.service.impl.PackSetServiceImpl;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.presenter.UserManagementVNPresenter.UserTableFilterDecorator;
import org.hypothesis.server.Messages;
import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomTable;
import com.vaadin.ui.CustomTable.ColumnGenerator;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import net.engio.mbassy.listener.Handler;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class PackSetManagementVNPresenter extends AbstractManagementPresenter implements ColumnGenerator {

	private final PackSetService packSetService;

	private PackSetWindowVNPresenter packSetWindowPresenter;

	public PackSetManagementVNPresenter() {
		packSetService = new PackSetServiceImpl();
	}

	@Override
	public void init() {
		super.init();

		packSetWindowPresenter = new PackSetWindowVNPresenter(getBus());
	}

	@Override
	public Component buildHeader() {
		HorizontalLayout header = new HorizontalLayout();
		header.setWidth("100%");
		header.setSpacing(true);

		Label title = new Label(Messages.getString("Caption.Label.PackSetsManagement"));
		title.addStyleName(ValoTheme.LABEL_HUGE);
		header.addComponent(title);
		header.addComponent(buildTools());
		header.setExpandRatio(title, 1);

		return header;
	}

	@Override
	protected Button buildAddButton() {
		final Button addButton = new Button(Messages.getString("Caption.Button.Add"));
		addButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		addButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(final ClickEvent event) {
				packSetWindowPresenter.showWindow();
			}
		});

		return addButton;
	}

	@Override
	protected ComboBox buildSelection() {
		return null;
	}

	@Override
	protected Button buildUpdateButton() {
		Button updateButton = new Button(Messages.getString("Caption.Button.Update"));
		updateButton.setClickShortcut(KeyCode.ENTER);
		updateButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(final ClickEvent event) {
				PackSetDto packSet = getSelectedPackSet();

				if (packSet != null) {
					packSetWindowPresenter.showWindow(packSet);
				}
			}

		});
		return updateButton;
	}

	@SuppressWarnings("unchecked")
	private PackSetDto getSelectedPackSet() {
		return ((BeanItem<PackSetDto>) table.getItem(table.getValue())).getBean();
	}

	@Override
	protected Button buildDeleteButton() {
		return null;
	}

	@Override
	protected Resource getExportResource() {
		return null;
	}

	@Override
	public Component buildTable() {
		FilterTable table = new FilterTable();
		table.setSizeFull();
		table.addStyleName(ValoTheme.TABLE_SMALL);
		table.setSelectable(true);
		// table.setMultiSelect(true);
		table.setColumnCollapsingAllowed(true);
		table.setSortContainerPropertyId(FieldConstants.USERNAME);

		BeanContainer<Long, PackSetDto> dataSource = new BeanContainer<Long, PackSetDto>(PackSetDto.class);
		dataSource.setBeanIdProperty(FieldConstants.ID);

		List<PackSetDto> packSets = packSetService.findAll();
		for (PackSetDto packSet : packSets) {
			dataSource.addBean(packSet);
		}
		table.setContainerDataSource(dataSource);
		dataSource.setItemSorter(new CaseInsensitiveItemSorter());
		table.sort();

		table.addGeneratedColumn(FieldConstants.AVAILABLE_PACKS, this);

		table.setVisibleColumns(FieldConstants.ID, FieldConstants.NAME, FieldConstants.AVAILABLE_PACKS);

		table.setColumnHeaders(Messages.getString("Caption.Field.Id"), Messages.getString("Caption.Field.Name"),
				Messages.getString("Caption.Field.AvailablePacks"));

		table.setFilterBarVisible(true);

		table.setFilterDecorator(new UserTableFilterDecorator());
		table.setFilterFieldVisible(FieldConstants.ID, false);
		table.setFilterFieldVisible(FieldConstants.AVAILABLE_PACKS, false);

		table.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(final ValueChangeEvent event) {
				// bus.post(new MainUIEvent.UserSelectionChangedEvent());
			}
		});

		table.addItemClickListener(new ItemClickListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void itemClick(ItemClickEvent event) {
				if (event.isDoubleClick()) {
					PackSetDto packSet = ((BeanItem<PackSetDto>) event.getItem()).getBean();
					packSetWindowPresenter.showWindow(packSet);
				}
			}
		});

		this.table = table;
		return table;
	}

	@Override
	public Object generateCell(CustomTable source, Object itemId, Object columnId) {
		if (columnId.equals(FieldConstants.AVAILABLE_PACKS)) {
			@SuppressWarnings("unchecked")
			PackSetDto packSet = ((BeanItem<PackSetDto>) source.getItem(itemId)).getBean();

			List<PackDto> packs = packSet.getPacks();
			List<String> packNames = new ArrayList<>();
			List<String> packDescs = new ArrayList<>();
			for (PackDto pack : packs) {
				packNames.add(Messages.getString("Caption.Item.PackLabel", pack.getName(), pack.getId()));
				packDescs.add(Messages.getString("Caption.Item.PackDescription", pack.getName(), pack.getId(),
						pack.getDescription()));
			}

			StringBuilder labelBuilder = new StringBuilder();
			for (String pack : packNames) {
				if (labelBuilder.length() != 0) {
					labelBuilder.append(", ");
				}
				labelBuilder.append(pack);
			}
			StringBuilder descriptionBuilder = new StringBuilder();
			descriptionBuilder.append("<ul>");
			for (String pack : packDescs) {
				descriptionBuilder.append("<li>" + pack + "</li>");
			}
			descriptionBuilder.append("</ul>");

			Label packsLabel = new Label();
			packsLabel.setDescription(descriptionBuilder.toString());

			if (packs.size() < 5) {
				packsLabel.setValue(labelBuilder.toString());
			} else {
				packsLabel.setValue(Messages.getString("Caption.Label.MultiplePacks", packs.size()));
			}

			return packsLabel;
		}

		return null;
	}

	@Override
	public void onClose(ConfirmDialog arg0) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	@Handler
	public void addPackSetIntoTable(final MainUIEvent.PackSetAddedEvent event) {
		PackSetDto packSet = event.getPackSet();
		BeanContainer<Long, PackSetDto> container = (BeanContainer<Long, PackSetDto>) table.getContainerDataSource();

		container.addItem(packSet.getId(), packSet);

		((FilterTable) table).sort();
	}

	@SuppressWarnings("unchecked")
	@Handler
	public void changePackSet(final MainUIEvent.PackSetChangedEvent event) {
		PackSetDto packSet = event.getPackSet();
		BeanContainer<Long, PackSetDto> container = (BeanContainer<Long, PackSetDto>) table.getContainerDataSource();

		container.removeItem(packSet.getId());
		container.addItem(packSet.getId(), packSet);

		((FilterTable) table).sort();
	}

}

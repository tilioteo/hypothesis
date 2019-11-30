package org.hypothesis.presenter;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.CustomTable.ColumnGenerator;
import com.vaadin.ui.themes.ValoTheme;
import net.engio.mbassy.listener.Handler;
import org.hypothesis.data.CaseInsensitiveItemSorter;
import org.hypothesis.data.model.FieldConstants;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.PackSet;
import org.hypothesis.data.service.PackSetService;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.presenter.UserManagementVNPresenter.UserTableFilterDecorator;
import org.hypothesis.server.Messages;
import org.tepi.filtertable.FilterTable;
import org.vaadin.dialogs.ConfirmDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
@SuppressWarnings("serial")
public class PackSetManagementVNPresenter extends AbstractManagementPresenter implements ColumnGenerator {

    private final PackSetService packSetService;

    private PackSetWindowVNPresenter packSetWindowPresenter;

    public PackSetManagementVNPresenter() {
        packSetService = PackSetService.newInstance();
    }

    @Override
    public void init() {
        super.init();

        packSetWindowPresenter = new PackSetWindowVNPresenter();
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
        addButton.addClickListener(e -> packSetWindowPresenter.showWindow());

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
        updateButton.addClickListener(e -> {
            PackSet packSet = getSelectedPackSet();

            if (packSet != null) {
                packSetWindowPresenter.showWindow(packSet);
            }
        });
        return updateButton;
    }

    @SuppressWarnings("unchecked")
    private PackSet getSelectedPackSet() {
        return ((BeanItem<PackSet>) table.getItem(table.getValue())).getBean();
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

        BeanContainer<Long, PackSet> dataSource = new BeanContainer<>(PackSet.class);
        dataSource.setBeanIdProperty(FieldConstants.ID);

        List<PackSet> packSets = packSetService.findAll();
        for (PackSet packSet : packSets) {
            packSet = packSetService.merge(packSet);
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

        table.addValueChangeListener(e -> {
            // bus.post(new MainUIEvent.UserSelectionChangedEvent());
        });

        table.addItemClickListener(e -> {
            if (e.isDoubleClick()) {
                PackSet packSet = ((BeanItem<PackSet>) e.getItem()).getBean();
                packSetWindowPresenter.showWindow(packSet);
            }
        });

        this.table = table;
        return table;
    }

    @Override
    public Object generateCell(CustomTable source, Object itemId, Object columnId) {
        if (columnId.equals(FieldConstants.AVAILABLE_PACKS)) {
            @SuppressWarnings("unchecked")
            PackSet packSet = ((BeanItem<PackSet>) source.getItem(itemId)).getBean();

            List<Pack> packs = packSet.getPacks();
            List<String> packNames = new ArrayList<>();
            List<String> packDescs = new ArrayList<>();
            for (Pack pack : packs) {
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
        PackSet packSet = event.getPackSet();
        BeanContainer<Long, PackSet> container = (BeanContainer<Long, PackSet>) table.getContainerDataSource();

        container.addItem(packSet.getId(), packSet);

        ((FilterTable) table).sort();
    }

    @SuppressWarnings("unchecked")
    @Handler
    public void changePackSet(final MainUIEvent.PackSetChangedEvent event) {
        PackSet packSet = event.getPackSet();
        BeanContainer<Long, PackSet> container = (BeanContainer<Long, PackSet>) table.getContainerDataSource();

        container.removeItem(packSet.getId());
        container.addItem(packSet.getId(), packSet);

        ((FilterTable) table).sort();
    }

}

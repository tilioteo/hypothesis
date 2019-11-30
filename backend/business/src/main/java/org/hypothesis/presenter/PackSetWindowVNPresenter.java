package org.hypothesis.presenter;

import com.vaadin.data.Container;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.And;
import com.vaadin.event.dd.acceptcriteria.Or;
import com.vaadin.event.dd.acceptcriteria.SourceIs;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.ui.*;
import com.vaadin.ui.AbstractSelect.AbstractSelectTargetDetails;
import com.vaadin.ui.AbstractSelect.AcceptItem;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.themes.ValoTheme;
import org.hypothesis.data.LongItemSorter;
import org.hypothesis.data.model.FieldConstants;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.PackSet;
import org.hypothesis.data.service.PackService;
import org.hypothesis.data.service.PackSetService;
import org.hypothesis.data.service.PermissionService;
import org.hypothesis.data.service.RoleService;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.server.Messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * <p>
 * Hypothesis
 */
@SuppressWarnings("serial")
public class PackSetWindowVNPresenter extends AbstractWindowPresenter {

    private final PackSetService packSetService;
    private final PermissionService permissionService;
    private final PackService packService;

    private TextField idField;
    private TextField nameField;
    private Table availablePacks;
    private Table packsField;

    private Button btnUp;
    private Button btnDown;
    private Button btnRight;
    private Button btnLeft;

    private boolean committed = false;

    public PackSetWindowVNPresenter() {
        packSetService = PackSetService.newInstance();
        permissionService = PermissionService.newInstance();
        packService = PackService.newInstance();
    }

    private void buildIdField() {
        if (idField == null) {
            idField = new TextField(Messages.getString("Caption.Field.Id"));
            idField.setEnabled(false);
        }
    }

    private void buildNameField() {
        if (nameField == null) {
            nameField = new TextField(Messages.getString("Caption.Field.Name"));
            nameField.setNullRepresentation("");
            nameField.setRequired(true);
            nameField.setRequiredError(Messages.getString("Message.Error.NameRequired"));
        }
    }

    private void buildPacksField() {
        if (packsField == null) {
            btnUp = new Button(FontAwesome.ARROW_UP);
            btnDown = new Button(FontAwesome.ARROW_DOWN);
            btnRight = new Button(FontAwesome.ARROW_RIGHT);
            btnLeft = new Button(FontAwesome.ARROW_LEFT);

            final Table table = new Table(Messages.getString("Caption.Field.SelectedPacks"));
            table.setSizeFull();
            table.addStyleName(ValoTheme.TABLE_SMALL);
            table.setSelectable(true);

            final BeanItemContainer<Pack> dataSource = new BeanItemContainer<>(Pack.class);

            table.setContainerDataSource(dataSource);

            table.addGeneratedColumn(FieldConstants.ORDER, (source, itemId, columnId) -> {
                Container.Indexed container = (Container.Indexed) source.getContainerDataSource();
                return Integer.toString(container.indexOfId(itemId) + 1);
            });

            table.setVisibleColumns(FieldConstants.ORDER, FieldConstants.NAME);
            table.setColumnHeaders(Messages.getString("Caption.Field.Order"), Messages.getString("Caption.Field.Name"));
            table.setSortEnabled(false);

            table.setDragMode(TableDragMode.ROW);

            table.setDropHandler(new DropHandler() {
                @Override
                public AcceptCriterion getAcceptCriterion() {
                    return new And(new Or(new SourceIs(packsField), new SourceIs(availablePacks)), AcceptItem.ALL);
                }

                @Override
                public void drop(DragAndDropEvent event) {
                    DataBoundTransferable t = (DataBoundTransferable) event.getTransferable();
                    Pack sourceItemId = (Pack) t.getItemId();

                    AbstractSelectTargetDetails dropData = (AbstractSelectTargetDetails) event.getTargetDetails();

                    Pack targetItemId = (Pack) dropData.getItemIdOver();

                    if (t.getSourceContainer() == dataSource
                            && (sourceItemId == targetItemId || targetItemId == null)) {
                        return;
                    }

                    t.getSourceContainer().removeItem(sourceItemId);

                    if (dropData.getDropLocation() == VerticalDropLocation.BOTTOM) {
                        dataSource.addItemAfter(targetItemId, sourceItemId);
                    } else {
                        Object prevItemId = dataSource.prevItemId(targetItemId);
                        dataSource.addItemAfter(prevItemId, sourceItemId);
                    }
                }
            });

            btnUp.addClickListener(e -> {
                Pack pack = (Pack) table.getValue();
                if (pack != null) {
                    List<Pack> list = dataSource.getItemIds();
                    int idx = list.indexOf(pack);
                    if (idx > 0) {
                        dataSource.removeItem(pack);
                        dataSource.addItemAt(idx - 1, pack);
                    }
                }
            });

            btnDown.addClickListener(e -> {
                Pack pack = (Pack) table.getValue();
                if (pack != null) {
                    List<Pack> list = dataSource.getItemIds();
                    int idx = list.indexOf(pack);
                    if (idx < list.size() - 1) {
                        dataSource.removeItem(pack);
                        dataSource.addItemAt(idx + 1, pack);
                    }
                }
            });

            packsField = table;

            final Table availTable = new Table(Messages.getString("Caption.Field.EnabledPacks"));
            availTable.setSizeFull();
            availTable.addStyleName(ValoTheme.TABLE_SMALL);
            availTable.setSelectable(true);

            final BeanItemContainer<Pack> availDataSource = new BeanItemContainer<>(Pack.class);

            availTable.setContainerDataSource(availDataSource);
            availDataSource.setItemSorter(new LongItemSorter());

            availTable.setVisibleColumns(FieldConstants.ID, FieldConstants.NAME);
            availTable.setColumnHeaders(Messages.getString("Caption.Field.Id"),
                    Messages.getString("Caption.Field.Name"));
            availTable.setSortEnabled(false);
            availTable.setSortContainerPropertyId(FieldConstants.ID);

            availTable.setDragMode(TableDragMode.ROW);

            availTable.setDropHandler(new DropHandler() {
                @Override
                public AcceptCriterion getAcceptCriterion() {
                    return new And(new SourceIs(packsField), AcceptItem.ALL);
                }

                @Override
                public void drop(DragAndDropEvent event) {
                    DataBoundTransferable t = (DataBoundTransferable) event.getTransferable();
                    Pack sourceItemId = (Pack) t.getItemId();

                    AbstractSelectTargetDetails dropData = (AbstractSelectTargetDetails) event.getTargetDetails();

                    Pack targetItemId = (Pack) dropData.getItemIdOver();

                    if (sourceItemId == targetItemId || targetItemId == null) {
                        return;
                    }

                    t.getSourceContainer().removeItem(sourceItemId);

                    if (dropData.getDropLocation() == VerticalDropLocation.BOTTOM) {
                        availDataSource.addItemAfter(targetItemId, sourceItemId);
                    } else {
                        Object prevItemId = availDataSource.prevItemId(targetItemId);
                        availDataSource.addItemAfter(prevItemId, sourceItemId);
                    }

                    availTable.sort();
                }
            });

            btnRight.addClickListener(e -> {
                Pack pack = (Pack) availTable.getValue();
                if (pack != null) {
                    availDataSource.removeItem(pack);
                    dataSource.addItem(pack);
                }
            });

            btnLeft.addClickListener(e -> {
                Pack pack = (Pack) table.getValue();
                if (pack != null) {
                    dataSource.removeItem(pack);
                    availDataSource.addItem(pack);

                    availTable.sort();
                }
            });

            table.addItemClickListener(e -> {
                Pack pack = (Pack) e.getItemId();
                if (e.isDoubleClick() && pack != null) {
                    dataSource.removeItem(pack);
                    availDataSource.addItem(pack);

                    availTable.sort();
                }
            });

            availTable.addItemClickListener(e -> {
                Pack pack = (Pack) e.getItemId();
                if (e.isDoubleClick() && pack != null) {
                    availDataSource.removeItem(pack);
                    dataSource.addItem(pack);
                }
            });

            availablePacks = availTable;
        }
    }

    @Override
    protected void initFields() {
        fields = new ArrayList<>();

        // ID
        buildIdField();

        // name
        buildNameField();

        // packs
        buildPacksField();

        if (WindowState.CREATE == state) {
            Collection<Pack> allPacks;
            if (loggedUser.hasRole(RoleService.ROLE_SUPERUSER)) {
                allPacks = permissionService.findAllPacks();
            } else {
                allPacks = permissionService.findUserPacks2(loggedUser, false);
            }

            for (Pack pack : allPacks) {
                availablePacks.addItem(pack);
            }

            availablePacks.sort();
        }
    }

    @Override
    protected void fillFields() {
        PackSet packSet = (PackSet) source;
        packSet = packSetService.merge(packSet);

        idField.setValue(packSet.getId().toString());
        nameField.setValue(packSet.getName());

        // packs
        List<Pack> packs;

        if (WindowState.UPDATE == state) {
            packs = packSet.getPacks();
        } else {
            packs = new LinkedList<>();
        }

        for (Pack pack : packs) {
            packsField.addItem(pack);
        }

        Collection<Pack> allPacks;
        if (loggedUser.hasRole(RoleService.ROLE_SUPERUSER)) {
            allPacks = permissionService.findAllPacks();
        } else {
            allPacks = permissionService.findUserPacks2(loggedUser, false);
        }

        for (Pack pack : allPacks) {
            if (!packs.contains(pack)) {
                availablePacks.addItem(pack);
            }
        }

        availablePacks.sort();
    }

    @Override
    protected void clearFields() {
        fields.clear();

        idField = null;
        nameField = null;
        packsField = null;
    }

    @Override
    protected void buildContent() {
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setMargin(true);
        content.setSpacing(true);
        window.setContent(content);

        content.addComponent(buildPackSetDetail());
        Component packSetPacks = buildPackSetPacks();
        content.addComponent(packSetPacks);
        content.setExpandRatio(packSetPacks, 1f);

        content.addComponent(buildFooter());

        setValidationVisible(false);
    }

    private Component buildPackSetDetail() {
        Panel panel = new Panel();

        panel.setContent(buildPackSetDetailsForm());

        return panel;
    }

    private Component buildPackSetDetailsForm() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        GridLayout form = new GridLayout();
        form.setColumns(2);
        form.setMargin(true);
        form.setSpacing(true);

        // ID
        if (WindowState.UPDATE == state) {
            addField(form, idField);
        }

        addField(form, nameField);

        layout.addComponent(form);
        return layout;
    }

    private Component buildPackSetPacks() {
        Panel panel = new Panel();
        panel.setSizeFull();

        panel.setContent(buildPackSetPacksForm());

        return panel;
    }

    private Component buildPackSetPacksForm() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.setSpacing(true);

        VerticalLayout buttons1 = new VerticalLayout();
        buttons1.setWidthUndefined();
        buttons1.setSpacing(true);
        buttons1.addComponent(btnRight);
        buttons1.addComponent(btnLeft);

        VerticalLayout buttons2 = new VerticalLayout();
        buttons2.setWidthUndefined();
        buttons2.setSpacing(true);
        buttons2.addComponent(btnUp);
        buttons2.addComponent(btnDown);

        layout.addComponent(availablePacks);
        layout.setExpandRatio(availablePacks, 1f);
        layout.addComponent(buttons1);
        layout.addComponent(packsField);
        layout.setExpandRatio(packsField, 1f);
        layout.addComponent(buttons2);

        return layout;
    }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);
        footer.setSpacing(true);

        Button ok = new Button(Messages.getString("Caption.Button.OK"));
        ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
        ok.addClickListener(e -> commitFormWithMessage());
        ok.focus();
        footer.addComponent(ok);
        footer.setComponentAlignment(ok, Alignment.TOP_RIGHT);

        Button cancel = new Button(Messages.getString("Caption.Button.Cancel"));
        cancel.addClickListener(e -> window.close());
        footer.addComponent(cancel);

        return footer;
    }

    protected void commitFormWithMessage() {
        try {
            commitForm();

            if (committed) {
                final Notification success;
                if (WindowState.CREATE == state) {
                    success = new Notification(Messages.getString("Message.Info.PackSetAdded"));
                } else if (WindowState.UPDATE == state) {
                    success = new Notification(Messages.getString("Message.Info.PackSetUpdated"));
                } else {
                    success = new Notification(Messages.getString("Message.Info.PackSetsUpdated"));
                }
                success.setDelayMsec(2000);
                success.setPosition(Position.BOTTOM_CENTER);
                success.show(Page.getCurrent());

                window.close();
            }

        } catch (CommitException e) {
            Notification.show(e.getMessage(), Type.WARNING_MESSAGE);
        }
    }

    protected void commitForm() throws CommitException {
        committed = false;

        for (AbstractField<?> field : fields) {
            try {
                if (field.isEnabled()) {
                    field.validate();
                }
            } catch (InvalidValueException e) {
                field.focus();
                setValidationVisible(true);
                throw new CommitException(e.getMessage());
            }
        }

        PackSet packSet;
        if (WindowState.CREATE == state) {
            packSet = new PackSet();
        } else {
            packSet = (PackSet) source;
        }
        packSet = savePackSet(packSet);
        if (packSet != null) {
            if (WindowState.CREATE == state) {
                getBus().post(new MainUIEvent.PackSetAddedEvent(packSet));
            } else if (WindowState.UPDATE == state) {
                getBus().post(new MainUIEvent.PackSetChangedEvent(packSet));
            }
        }

        committed = true;
    }

    private PackSet savePackSet(PackSet packSet) {
        if (nameField.isVisible()) {
            packSet.setName(nameField.getValue());
        }

        if (packsField != null && packsField.isVisible() && packsField.isEnabled()) {
            packSet.removeAllPacks();
            for (Object itemId : packsField.getItemIds()) {
                Pack pack = packService.merge((Pack) itemId);
                packSet.addPack(pack);
            }
        }

        packSet = packSetService.add(packSet);

        return packSet;
    }

    private void setValidationVisible(boolean visible) {
        idField.setValidationVisible(visible);
        nameField.setValidationVisible(visible);
        packsField.setValidationVisible(visible);
    }

    public void showWindow(PackSet packSet) {
        showWindow(WindowState.UPDATE, packSet);
    }

}

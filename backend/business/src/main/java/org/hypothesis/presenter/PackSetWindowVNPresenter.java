package org.hypothesis.presenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.hypothesis.data.LongItemSorter;
import org.hypothesis.data.model.FieldConstants;
import org.hypothesis.data.model.Pack;
import org.hypothesis.data.model.PackSet;
import org.hypothesis.data.service.PackService;
import org.hypothesis.data.service.PackSetService;
import org.hypothesis.data.service.PermissionService;
import org.hypothesis.data.service.RoleService;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.eventbus.MainEventBus;
import org.hypothesis.server.Messages;

import com.vaadin.data.Container;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
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
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect.AbstractSelectTargetDetails;
import com.vaadin.ui.AbstractSelect.AcceptItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
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

	public PackSetWindowVNPresenter(MainEventBus bus) {
		super(bus);

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

			final BeanItemContainer<Pack> dataSource = new BeanItemContainer<Pack>(Pack.class);

			table.setContainerDataSource(dataSource);

			table.addGeneratedColumn(FieldConstants.ORDER, new ColumnGenerator() {
				@Override
				public Object generateCell(final Table source, final Object itemId, final Object columnId) {
					Container.Indexed container = (Container.Indexed) source.getContainerDataSource();
					return Integer.toString(container.indexOfId(itemId) + 1);
				}
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

			btnUp.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					Pack pack = (Pack) table.getValue();
					if (pack != null) {
						List<Pack> list = dataSource.getItemIds();
						int idx = list.indexOf(pack);
						if (idx > 0) {
							dataSource.removeItem(pack);
							dataSource.addItemAt(idx - 1, pack);
						}
					}
				}
			});

			btnDown.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					Pack pack = (Pack) table.getValue();
					if (pack != null) {
						List<Pack> list = dataSource.getItemIds();
						int idx = list.indexOf(pack);
						if (idx < list.size() - 1) {
							dataSource.removeItem(pack);
							dataSource.addItemAt(idx + 1, pack);
						}
					}
				}
			});

			packsField = table;

			final Table availTable = new Table(Messages.getString("Caption.Field.EnabledPacks"));
			availTable.setSizeFull();
			availTable.addStyleName(ValoTheme.TABLE_SMALL);
			availTable.setSelectable(true);

			final BeanItemContainer<Pack> availDataSource = new BeanItemContainer<Pack>(Pack.class);

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

			btnRight.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					Pack pack = (Pack) availTable.getValue();
					if (pack != null) {
						availDataSource.removeItem(pack);
						dataSource.addItem(pack);
					}
				}
			});

			btnLeft.addClickListener(new ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					Pack pack = (Pack) table.getValue();
					if (pack != null) {
						dataSource.removeItem(pack);
						availDataSource.addItem(pack);

						availTable.sort();
					}
				}
			});

			table.addItemClickListener(new ItemClickListener() {
				@Override
				public void itemClick(ItemClickEvent event) {
					Pack pack = (Pack) event.getItemId();
					if (event.isDoubleClick() && pack != null) {
						dataSource.removeItem(pack);
						availDataSource.addItem(pack);

						availTable.sort();
					}
				}
			});

			availTable.addItemClickListener(new ItemClickListener() {
				@Override
				public void itemClick(ItemClickEvent event) {
					Pack pack = (Pack) event.getItemId();
					if (event.isDoubleClick() && pack != null) {
						availDataSource.removeItem(pack);
						dataSource.addItem(pack);
					}
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

		// Collection<Pack> packs;
		// if (loggedUser.hasRole(RoleService.ROLE_SUPERUSER)) {
		// packs = permissionService.findAllPacks();
		// } else {
		// packs = permissionService.findUserPacks2(loggedUser, false);
		// }
		//
		// for (Pack pack : packs) {
		// packsField.addItem(pack);
		// Item row = packsField.getItem(pack);
		// // row.getItemProperty(FieldConstants.NAME).setValue(pack.getName());
		// }

		// ((IndexedContainer)
		// packsField.getContainerDataSource()).setItemSorter(new
		// CaseInsensitiveItemSorter());
		// packsField.sort(new Object[] { FieldConstants.NAME }, new boolean[] {
		// true });

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

		// HorizontalLayout innerLayout = new HorizontalLayout();
		// innerLayout.setSizeFull();

		// content.addComponent(innerLayout);
		// content.setExpandRatio(innerLayout, 1f);

		// innerLayout.addComponent(buildPackSetDetail());
		// innerLayout.addComponent(buildPackSetPacks());
		content.addComponent(buildPackSetDetail());
		Component packSetPacks = buildPackSetPacks();
		content.addComponent(packSetPacks);
		content.setExpandRatio(packSetPacks, 1f);

		content.addComponent(buildFooter());

		setValidationVisible(false);
	}

	private Component buildPackSetDetail() {
		Panel panel = new Panel();
		// panel.setSizeFull();

		// panel.setCaption(Messages.getString("Caption.Tab.UserDetails"));
		// panel.setIcon(FontAwesome.USER);

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
		// panel.setCaption(Messages.getString("Caption.Tab.UserPacks"));
		// panel.setIcon(FontAwesome.COG);

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

		// Panel panel = new Panel();
		// panel.setSizeFull();
		// panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		// panel.setContent(packsField);
		//
		// layout.addComponent(panel);
		// layout.setExpandRatio(panel, 1f);

		return layout;
	}

	private Component buildFooter() {
		HorizontalLayout footer = new HorizontalLayout();
		footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
		footer.setWidth(100.0f, Unit.PERCENTAGE);
		footer.setSpacing(true);

		Button ok = new Button(Messages.getString("Caption.Button.OK"));
		ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
		ok.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				commitFormWithMessage();
			}
		});
		ok.focus();
		footer.addComponent(ok);
		footer.setComponentAlignment(ok, Alignment.TOP_RIGHT);

		Button cancel = new Button(Messages.getString("Caption.Button.Cancel"));
		cancel.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				window.close();
			}
		});
		footer.addComponent(cancel);

		return footer;
	}

	protected void commitFormWithMessage() {
		try {
			commitForm();

			if (committed) {
				Notification success = null;
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

		if (WindowState.CREATE == state) {
			// final User oldUser =
			// userService.findByUsernamePassword(usernameField.getValue(),
			// passwordField.getValue());
			// if (oldUser != null) {
			// ConfirmDialog.show(UI.getCurrent(),
			// Messages.getString("Caption.Dialog.ConfirmReplace"),
			// Messages.getString("Caption.Confirm.User.OverwriteExisting"),
			// Messages.getString("Caption.Button.Confirm"),
			// Messages.getString("Caption.Button.Cancel"),
			// new Listener() {
			// @Override
			// public void onClose(ConfirmDialog dialog) {
			// if (dialog.isConfirmed()) {
			// state = WindowState.UPDATE;
			// source = oldUser;
			// loggedUser = SessionManager.getLoggedUser();
			//
			// commitFormWithMessage();
			// }
			// }
			// });
			//
			// return;
			// }
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
				bus.post(new MainUIEvent.PackSetAddedEvent(packSet));
			} else if (WindowState.UPDATE == state) {
				bus.post(new MainUIEvent.PackSetChangedEvent(packSet));
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

/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.presenter;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toSet;
import static org.hypothesis.data.api.Roles.ROLE_MANAGER;
import static org.hypothesis.utility.UserUtility.userHasAnyRole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hypothesis.business.CancelableExportRunnable;
import org.hypothesis.business.ExportRunnableImpl;
import org.hypothesis.business.ExportThread;
import org.hypothesis.business.ThreadUtility;
import org.hypothesis.context.HibernateUtil;
import org.hypothesis.data.api.Status;
import org.hypothesis.data.dto.PackDto;
import org.hypothesis.data.dto.SimpleUserDto;
import org.hypothesis.data.dto.TestDto;
import org.hypothesis.data.interfaces.FieldConstants;
import org.hypothesis.data.service.PermissionService;
import org.hypothesis.data.service.TestService;
import org.hypothesis.data.service.impl.PermissionServiceImpl;
import org.hypothesis.data.service.impl.TestServiceImpl;
import org.hypothesis.event.interfaces.MainUIEvent;
import org.hypothesis.interfaces.ExportPresenter;
import org.hypothesis.server.Messages;
import org.hypothesis.ui.view.ExportView;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import net.engio.mbassy.listener.Handler;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings({ "serial" })
public class ExportPresenterImpl extends AbstractMainBusPresenter implements ExportPresenter {

	private static final Logger log = Logger.getLogger(ExportPresenterImpl.class);

	private final PermissionService permissionService;
	private final TestService testService;

	private final List<String> sortedPacks = new ArrayList<>();
	private final HashMap<String, PackDto> packMap = new HashMap<>();

	private VerticalLayout content;
	private VerticalLayout testSelection;
	private Button exportButton;
	private Button cancelExportButton;
	private ComboBox exportSelectionType;
	private ComboBox packsSelect;
	private PopupDateField dateFieldFrom;
	private PopupDateField dateFieldTo;
	private Table table;

	private boolean allTestsSelected = false;

	private HorizontalLayout toolsLayout;
	private ExportThread currentExport = null;
	private ProgressBar exportProgressBar = null;

	private ThreadGroup threadGroup = ThreadUtility.createExportGroup();

	public ExportPresenterImpl() {
		permissionService = new PermissionServiceImpl();
		testService = new TestServiceImpl();
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

	@Override
	public Component buildHeader() {
		HorizontalLayout header = new HorizontalLayout();
		header.setWidth("100%");
		header.setSpacing(true);

		Label title = new Label(Messages.getString("Caption.Label.TestsExport"));
		title.addStyleName("huge");
		header.addComponent(title);
		header.addComponent(buildTools());
		header.setExpandRatio(title, 1);

		return header;
	}

	private Component buildTools() {
		toolsLayout = new HorizontalLayout();
		toolsLayout.setSpacing(true);

		buildExportControls();

		setExportSelection();

		return toolsLayout;
	}

	private void setExportSelection() {
		toolsLayout.removeAllComponents();

		CssLayout layout = new CssLayout();
		layout.addStyleName("v-component-group");

		layout.addComponent(exportSelectionType);
		layout.addComponent(exportButton);

		toolsLayout.addComponent(layout);
	}

	private void setExportProgressIndeterminate() {
		toolsLayout.removeAllComponents();

		exportProgressBar.setValue(0f);
		exportProgressBar.setIndeterminate(true);
		CssLayout layout = new CssLayout();
		layout.addComponent(exportProgressBar);

		cancelExportButton.setEnabled(false);

		toolsLayout.addComponent(layout);
		toolsLayout.addComponent(cancelExportButton);
	}

	private void setExportProgress() {
		toolsLayout.removeAllComponents();

		exportProgressBar.setIndeterminate(false);

		cancelExportButton.setEnabled(true);

		toolsLayout.addComponent(exportProgressBar);
		toolsLayout.addComponent(cancelExportButton);
	}

	private void buildExportControls() {
		buildSelection();
		buildExportButton();
		buildProgress();
		buildExportCancelButton();
	}

	private void buildExportCancelButton() {
		cancelExportButton = new Button(Messages.getString("Caption.Button.Cancel"), new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				getBus().post(new MainUIEvent.ExportFinishedEvent(true));
			}
		});
	}

	private void buildProgress() {
		exportProgressBar = new ProgressBar();
		exportProgressBar.setCaption(Messages.getString("Caption.Label.ExportProgress"));
		exportProgressBar.setWidth("200px");
	}

	private void buildSelection() {
		exportSelectionType = new ComboBox();
		exportSelectionType.setTextInputAllowed(false);
		exportSelectionType.setNullSelectionAllowed(false);
		exportSelectionType.setEnabled(false);

		exportSelectionType.addItem(Messages.getString("Caption.Item.Selected"));
		exportSelectionType.addItem(Messages.getString("Caption.Item.All"));
		exportSelectionType.select(Messages.getString("Caption.Item.Selected"));

		exportSelectionType.addValueChangeListener(new Property.ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				allTestsSelected = exportSelectionType.getValue().equals(Messages.getString("Caption.Item.All"));
				getBus().post(new MainUIEvent.PackSelectionChangedEvent());
			}
		});
	}

	private void buildExportButton() {
		exportButton = new Button(Messages.getString("Caption.Button.Export"), new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				startExport();
			}
		});
		exportButton.setEnabled(false);

	}

	@SuppressWarnings("unchecked")
	private void startExport() {
		setExportProgressIndeterminate();

		Collection<Long> testIds = null;
		if (allTestsSelected) {
			testIds = (Collection<Long>) table.getItemIds();
		} else {
			testIds = (Collection<Long>) table.getValue();
		}

		CancelableExportRunnable runnable = new ExportRunnableImpl(getBus(), testIds.stream().collect(toSet()),
				HibernateUtil::closeCurrent);

		currentExport = new ExportThread(threadGroup, runnable);
		currentExport.start();

		UI.getCurrent().setPollInterval(1000);
	}

	@Override
	public Component buildContent() {
		content = new VerticalLayout();
		content.setSizeFull();
		content.setSpacing(true);

		content.addComponent(buildForm());

		testSelection = new VerticalLayout();
		testSelection.setSizeFull();
		content.addComponent(testSelection);
		content.setExpandRatio(testSelection, 1);

		Label infoLabel = new Label(Messages.getString("Caption.Label.ChoosePack"));
		infoLabel.setSizeUndefined();
		testSelection.addComponent(infoLabel);
		testSelection.setComponentAlignment(infoLabel, Alignment.MIDDLE_CENTER);

		return content;
	}

	private Component buildForm() {
		HorizontalLayout form = new HorizontalLayout();
		form.setWidth("100%");

		initPacksSources();

		packsSelect = new ComboBox();
		packsSelect.setInputPrompt(Messages.getString("Caption.Button.ChoosePack"));
		for (String packTitle : sortedPacks) {
			packsSelect.addItem(packTitle);
		}
		packsSelect.setTextInputAllowed(false);
		packsSelect.setNullSelectionAllowed(false);
		packsSelect.setRequired(true);
		packsSelect.setRequiredError(Messages.getString("Message.Error.NoPackSelected"));
		packsSelect.setValidationVisible(false);
		form.addComponent(packsSelect);

		dateFieldFrom = new PopupDateField();
		dateFieldFrom.setResolution(Resolution.SECOND);
		dateFieldFrom.setDateFormat(Messages.getString("Format.DateTime"));
		dateFieldFrom.setInputPrompt(Messages.getString("Caption.Field.DateFrom"));
		dateFieldFrom.setImmediate(true);
		dateFieldFrom.setValidationVisible(false);
		form.addComponent(dateFieldFrom);

		dateFieldTo = new PopupDateField();
		dateFieldTo.setResolution(Resolution.SECOND);
		dateFieldTo.setDateFormat(Messages.getString("Format.DateTime"));
		dateFieldTo.setInputPrompt(Messages.getString("Caption.Field.DateTo"));
		dateFieldTo.setImmediate(true);
		dateFieldTo.setValidationVisible(false);
		form.addComponent(dateFieldTo);

		Validator dateValidator = new Validator() {
			@Override
			public void validate(Object value) throws InvalidValueException {
				if (dateFieldFrom.getValue() == null && dateFieldTo.getValue() == null) {
					throw new InvalidValueException(Messages.getString("Message.Error.NoDateSelected"));
				}

			}
		};
		dateFieldFrom.addValidator(dateValidator);
		dateFieldTo.addValidator(dateValidator);

		Button selectionButton = new Button(Messages.getString("Caption.Button.ShowTests"));
		selectionButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					packsSelect.validate();
					dateFieldFrom.validate();
					dateFieldTo.validate();

					PackDto pack = packMap.get(packsSelect.getValue());
					Date dateFrom = (Date) dateFieldFrom.getValue();
					Date dateTo = (Date) dateFieldTo.getValue();

					showTests(pack, dateFrom, dateTo);

				} catch (InvalidValueException e) {
					packsSelect.setValidationVisible(!packsSelect.isValid());
					dateFieldFrom.setValidationVisible(!dateFieldFrom.isValid());
					dateFieldTo.setValidationVisible(!dateFieldTo.isValid());
					Notification.show(e.getMessage(), Type.WARNING_MESSAGE);
				}
			}
		});
		form.addComponent(selectionButton);

		return form;
	}

	private void initPacksSources() {
		List<PackDto> packs = permissionService.findUserPacks2(getLoggedUser().getId(), false);

		sortedPacks.clear();
		packMap.clear();

		if (packs != null) {
			for (PackDto pack : packs) {
				String key = Messages.getString("Caption.Item.PackSelect", pack.getName(), pack.getId(),
						pack.getDescription());
				sortedPacks.add(key);
				packMap.put(key, pack);
			}

			Collections.sort(sortedPacks);
		}
	}

	protected void showTests(PackDto pack, Date dateFrom, Date dateTo) {
		testSelection.removeAllComponents();
		// testSelection.setSpacing(true);

		// MANAGER see only tests created by himself and his users
		SimpleUserDto loggedUser = getLoggedUser();
		List<TestDto> tests = userHasAnyRole(loggedUser, ROLE_MANAGER)
				? testService.findManagedTestsOverview(loggedUser.getId(), pack.getId(), dateFrom, dateTo)
				: emptyList();

		if (tests.isEmpty()) {
			Label label = new Label(Messages.getString("Caption.Label.NoTestsFound"));
			label.setSizeUndefined();
			testSelection.addComponent(label);
			testSelection.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
			exportSelectionType.setEnabled(false);
		} else {
			testSelection.addComponent(buildTestsTable(tests));
			exportSelectionType.setEnabled(true);
			getBus().post(new MainUIEvent.PackSelectionChangedEvent());
		}
	}

	private Table buildTestsTable(Collection<TestDto> tests) {
		table = new Table();
		table.setSelectable(true);
		table.setMultiSelect(true);
		table.setMultiSelectMode(MultiSelectMode.DEFAULT);
		// table.setWidth("100%");
		table.setSizeFull();

		table.setSortContainerPropertyId(FieldConstants.ID);

		final BeanContainer<Long, TestDto> dataSource = new BeanContainer<Long, TestDto>(TestDto.class);
		dataSource.setBeanIdProperty(FieldConstants.ID);
		dataSource.addNestedContainerProperty(FieldConstants.NESTED_USER_ID);
		dataSource.addNestedContainerProperty(FieldConstants.NESTED_USER_USERNAME);
		dataSource.addAll(tests);
		table.setContainerDataSource(dataSource);

		table.addGeneratedColumn(FieldConstants.USER_ID, new ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				TestDto test = dataSource.getItem(itemId).getBean();
				return test.getUser() != null ? test.getUser().getId() : null;
			}
		});

		table.addGeneratedColumn(FieldConstants.USERNAME, new ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				TestDto test = dataSource.getItem(itemId).getBean();
				return test.getUser() != null ? test.getUser().getUsername() : null;
			}
		});

		table.addGeneratedColumn(FieldConstants.STATUS, new ColumnGenerator() {
			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				TestDto test = dataSource.getItem(itemId).getBean();
				Status status = test.getStatus();
				if (status != null) {
					switch (status) {
					case CREATED:
						return Messages.getString("Status.Created");
					case STARTED:
						return Messages.getString("Status.Started");
					case BROKEN_BY_CLIENT:
						return Messages.getString("Status.BrokenClient");
					case BROKEN_BY_ERROR:
						return Messages.getString("Status.BrokenError");
					case FINISHED:
						return Messages.getString("Status.Finished");
					default:
						break;
					}
				}
				return null;
			}
		});

		table.setVisibleColumns(FieldConstants.ID, FieldConstants.USER_ID,
				// FieldConstants.USERNAME,
				// FieldConstants.NESTED_USER_ID,
				// FieldConstants.NESTED_USER_USERNAME,
				FieldConstants.CREATED, FieldConstants.STATUS);

		table.setColumnHeaders(Messages.getString("Caption.Field.TestID"), Messages.getString("Caption.Field.UserID"),
				// Messages.getString("Caption.Field.Username"),
				// Messages.getString("Caption.Field.UserID"),
				// Messages.getString("Caption.Field.Username"),
				Messages.getString("Caption.Field.Created"), Messages.getString("Caption.Field.Status"));

		table.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(final ValueChangeEvent event) {
				getBus().post(new MainUIEvent.PackSelectionChangedEvent());
			}
		});

		table.setPageLength(table.size());

		return table;
	}

	@SuppressWarnings("unchecked")
	@Handler
	public void setExportEnabled(final MainUIEvent.PackSelectionChangedEvent event) {
		boolean itemsSelected = ((Set<Object>) table.getValue()).size() > 0;
		boolean exportEnabled = allTestsSelected || itemsSelected;
		exportButton.setEnabled(exportEnabled);
	}

	@Handler
	public void updateExportProgress(final MainUIEvent.ExportProgressEvent event) {
		if (exportProgressBar.isIndeterminate() && event.getProgress() >= 0) {
			setExportProgress();
		}
		exportProgressBar.setValue(event.getProgress());
	}

	@Handler
	public void exportFinished(final MainUIEvent.ExportFinishedEvent event) {
		afterExportFinnished(event.isCanceled());
	}

	@Handler
	public void exportError(final MainUIEvent.ExportErrorEvent event) {
		afterExportFinnished(false);
		Notification.show("Export failed", null, Type.WARNING_MESSAGE);
	}

	@Handler
	public void changeUserPacks(final MainUIEvent.UserPacksChangedEvent event) {
		initPacksSources();

		packsSelect.removeAllItems();
		testSelection.removeAllComponents();
		for (String packTitle : sortedPacks) {
			packsSelect.addItem(packTitle);
		}
	}

	private void afterExportFinnished(boolean canceled) {
		if (currentExport != null) {
			if (canceled) {
				currentExport.cancel();
			}
			currentExport = null;
		}
		setExportSelection();
		UI.getCurrent().setPollInterval(-1);
	}

	@Override
	public View createView() {
		return new ExportView(this);
	}

}

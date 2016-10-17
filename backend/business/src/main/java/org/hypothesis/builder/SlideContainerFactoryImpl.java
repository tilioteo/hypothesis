/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hypothesis.business.ShortcutUtility;
import org.hypothesis.business.ShortcutUtility.ShortcutKeys;
import org.hypothesis.common.AlignmentWrapperImpl;
import org.hypothesis.common.ComponentWrapperImpl;
import org.hypothesis.common.ValidationSets;
import org.hypothesis.common.utility.ComponentUtility;
import org.hypothesis.common.utility.DocumentUtility;
import org.hypothesis.common.utility.EvaluableUtility;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.event.model.MessageEvent;
import org.hypothesis.event.model.ProcessEventTypes;
import org.hypothesis.extension.PluginManager;
import org.hypothesis.interfaces.AlignmentWrapper;
import org.hypothesis.interfaces.ComponentEventCallback;
import org.hypothesis.interfaces.ComponentWrapper;
import org.hypothesis.interfaces.Document;
import org.hypothesis.interfaces.DocumentConstants;
import org.hypothesis.interfaces.Element;
import org.hypothesis.interfaces.SlideComponentPlugin;
import org.hypothesis.interfaces.SlidePresenter;
import org.hypothesis.presenter.SlideContainerPresenter;
import org.hypothesis.slide.ui.Audio;
import org.hypothesis.slide.ui.Button;
import org.hypothesis.slide.ui.ButtonPanel;
import org.hypothesis.slide.ui.ComboBox;
import org.hypothesis.slide.ui.DateField;
import org.hypothesis.slide.ui.HorizontalLayout;
import org.hypothesis.slide.ui.Image;
import org.hypothesis.slide.ui.Label;
import org.hypothesis.slide.ui.Panel;
import org.hypothesis.slide.ui.SelectPanel;
import org.hypothesis.slide.ui.TextArea;
import org.hypothesis.slide.ui.TextField;
import org.hypothesis.slide.ui.Timer;
import org.hypothesis.slide.ui.TimerLabel;
import org.hypothesis.slide.ui.VerticalLayout;
import org.hypothesis.slide.ui.Video;
import org.hypothesis.slide.ui.Window;
import org.hypothesis.ui.SlideContainer;
import org.vaadin.special.data.DateRangeValidator;
import org.vaadin.special.data.EmptyValidator;
import org.vaadin.special.data.IntegerValidator;
import org.vaadin.special.data.NumberRangeValidator;
import org.vaadin.special.data.NumberValidator;
import org.vaadin.special.data.SelectPanelEmptyValidator;
import org.vaadin.special.ui.KeyAction;
import org.vaadin.special.ui.SelectButton;

import com.tilioteo.common.Strings;
import com.tilioteo.common.collections.StringMap;
import com.vaadin.data.Validatable;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Layout;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class SlideContainerFactoryImpl implements SlideContainerFactory {

	private static Logger log = Logger.getLogger(SlideContainerFactoryImpl.class);

	@Override
	public SlideContainer buildSlideContainer(String template, String content, DocumentReader reader) {

		Document templateDocument = reader.readString(template);
		Document contentDocument = reader.readString(content);

		if (null == templateDocument) {
			log.warn("Template document is NULL");
		}
		if (null == contentDocument) {
			log.warn("Content document is NULL");
		}

		if (null == templateDocument || null == contentDocument) {
			return null;
		}

		try {
			Document document = DocumentFactory.mergeSlideDocument(templateDocument, contentDocument);

			if (DocumentUtility.isValidSlideDocument(document)) {
				return buildSlideContainer(document);
			}
		} catch (Exception e) {
			log.error("buildFromString()");
		}

		return null;
	}

	protected SlideContainerPresenter createSlideContainerPresenter() {
		return new SlideContainerPresenter();
	}

	private SlideContainer buildSlideContainer(Document document) {
		SlideContainerPresenter presenter = createSlideContainerPresenter();

		Element rootElement = document.root();

		EvaluableUtility.createActions(rootElement, presenter);
		createTimers(rootElement, presenter);

		createInputExpressions(rootElement, presenter);
		createOutputExpressions(rootElement, presenter);

		createWindows(rootElement, presenter);
		createViewport(rootElement, presenter);

		EvaluableUtility.createVariables(rootElement, presenter, (name, id, eval) -> {
			if (StringUtils.isNotEmpty(id) && eval instanceof SlideContainerPresenter) {
				SlideContainerPresenter pres = (SlideContainerPresenter) eval;

				if (DocumentConstants.COMPONENT.equals(name)) {
					return pres.getComponent(id);
				} else if (DocumentConstants.TIMER.equals(name)) {
					return pres.getTimer(id);
				} else if (DocumentConstants.WINDOW.equals(name)) {
					return pres.getWindow(id);
				}
			}
			return null;
		});

		return presenter.getSlideContainer();
	}

	private void createInputExpressions(Element rootElement, SlideContainerPresenter presenter) {
		List<Element> inputElements = DocumentUtility.getInputValueElements(rootElement);

		if (inputElements != null) {
			inputElements.stream().map(m -> EvaluableUtility.createValueExpression(m, DocumentConstants.INPUT_VALUE))
					.filter(f -> f != null).forEach(e -> presenter.setInputExpression(e.getIndex(), e));
		}
	}

	private void createOutputExpressions(Element rootElement, SlideContainerPresenter presenter) {
		List<Element> outputElements = DocumentUtility.getOutputValueElements(rootElement);

		if (outputElements != null) {
			outputElements.stream().map(m -> EvaluableUtility.createValueExpression(m, DocumentConstants.OUTPUT_VALUE))
					.filter(f -> f != null).forEach(e -> presenter.setOutputExpression(e.getIndex(), e));
		}
	}

	private void createTimers(Element rootElement, SlideContainerPresenter presenter) {
		List<Element> elements = DocumentUtility.getTimersElements(rootElement);

		if (elements != null) {
			elements.forEach(e -> {
				String id = DocumentUtility.getId(e);
				if (StringUtils.isNotEmpty(id)) {

					Timer timer = createTimer(e, presenter);
					presenter.setTimer(id, timer);
				}
			});
		}
	}

	private Timer createTimer(Element element, SlideContainerPresenter presenter) {
		StringMap properties = DocumentUtility.getPropertyValueMap(element);

		Timer component = new Timer();
		SlideComponentUtility.setTimerProperties(component, element, properties);
		addTimerHandlers(component, element, presenter);

		return component;
	}

	private void addTimerHandlers(Timer component, Element element, SlideContainerPresenter presenter) {
		DocumentUtility.iterateHandlers(component, element, presenter, (co, el, h, n, ac, a, pr) -> {
			final Timer timer = (Timer) co;

			if (DocumentConstants.START.equals(n)) {
				timer.addStartListener(e -> pr.handleEvent(timer, DocumentConstants.TIMER, ProcessEventTypes.TimerStart,
						a, c -> c.setProperty("time", e.getTime())));

			} else if (DocumentConstants.STOP.equals(n)) {
				timer.addStopListener(e -> pr.handleEvent(timer, DocumentConstants.TIMER, ProcessEventTypes.TimerStop,
						a, c -> c.setProperty("time", e.getTime())));

			} else if (DocumentConstants.UPDATE.equals(n)) {
				Integer interval = Strings.toInteger(el.getAttribute(DocumentConstants.INTERVAL));
				if (interval != null) {
					timer.addUpdateListener(interval, e -> presenter.handleEvent(timer, DocumentConstants.TIMER,
							ProcessEventTypes.TimerUpdate, a, c -> c.setProperty("time", e.getTime())));
				}
			}
		});
	}

	private void createWindows(Element rootElement, SlideContainerPresenter presenter) {
		List<Element> elements = DocumentUtility.getWindowsElements(rootElement);

		if (elements != null) {
			elements.forEach(e -> {
				String id = DocumentUtility.getId(e);
				if (StringUtils.isNotEmpty(id)) {
					Element element = DocumentUtility.getViewportOrWindowRootElement(e);

					if (element != null) {
						Window window = createWindow(element, presenter);
						presenter.setWindow(id, window);
					}
				}
			});
		}
	}

	private Window createWindow(Element element, SlideContainerPresenter presenter) {
		StringMap properties = DocumentUtility.getPropertyValueMap(element);

		Window component = new Window();
		SlideComponentUtility.setWindowProperties(component, element, properties, new AlignmentWrapperImpl());
		addWindowComponents(component, element, presenter);
		addWindowHandlers(component, element, presenter);

		return component;
	}

	// FIXME similar to addHorizontalLayoutComponents and
	// addVerticalLayoutComponents
	// FIXME copy-paste code of addPanelComponents
	private void addWindowComponents(Window container, Element element, SlideContainerPresenter presenter) {
		List<Element> elements = DocumentUtility.getContainerComponents(element, ValidationSets.VALID_WINDOW_CHILDREN);

		if (elements != null) {
			elements.stream().map(m -> createComponentFromElement(m, presenter)).filter(f -> f != null).forEach(e -> {
				Component component = e.getComponent();

				if (elements.size() == 1 && component instanceof Layout) {
					container.setContent((Layout) component);
				} else {
					GridLayout gridLayout = new GridLayout(1, 1);
					gridLayout.setSizeFull();
					container.setContent(gridLayout);
					gridLayout.addComponent(component);
					gridLayout.setComponentAlignment(component, e.getAlignment());
				}
			});
		}
	}

	private void addWindowHandlers(Window component, Element element, SlideContainerPresenter presenter) {
		DocumentUtility.iterateHandlers(component, element, presenter, (co, el, h, n, ac, a, pr) -> {
			final Window window = (Window) co;

			if (DocumentConstants.INIT.equals(n)) {
				window.addInitListener(e -> pr.handleEvent(window, DocumentConstants.WINDOW,
						ProcessEventTypes.WindowInit, a, ComponentEventCallback.DEFAULT));
			} else if (DocumentConstants.OPEN.equals(n)) {
				window.addOpenListener(e -> pr.handleEvent(window, DocumentConstants.WINDOW,
						ProcessEventTypes.WindowOpen, a, ComponentEventCallback.DEFAULT));
			} else if (DocumentConstants.CLOSE.equals(n)) {
				window.addCloseListener(e -> pr.handleEvent(window, DocumentConstants.WINDOW,
						ProcessEventTypes.WindowClose, a, ComponentEventCallback.DEFAULT));
			}
		});
	}

	private void createViewport(Element rootElement, SlideContainerPresenter presenter) {
		Element componentElement = DocumentUtility.getViewportInnerComponent(rootElement);
		ComponentWrapper componentWrapper = createComponentFromElement(componentElement, presenter);

		SlideContainer container = createSlideContainer(presenter, componentWrapper.getComponent());
		addViewportHandlers(container, rootElement, presenter);
	}

	private SlideContainer createSlideContainer(SlideContainerPresenter presenter, Component component) {
		SlideContainer container = new SlideContainer(presenter);
		container.addComponent(component);
		presenter.setSlideContainer(container);
		return container;
	}

	private void addViewportHandlers(SlideContainer component, Element element, SlideContainerPresenter presenter) {
		DocumentUtility.iterateHandlers(component, element, presenter, (co, el, h, n, ac, a, pr) -> {
			final SlideContainer container = (SlideContainer) co;
			if (DocumentConstants.INIT.equals(n)) {
				pr.addViewportInitListener(e -> pr.handleEvent(container, DocumentConstants.SLIDE,
						ProcessEventTypes.SlideInit, a, ComponentEventCallback.DEFAULT));
			} else if (DocumentConstants.SHOW.equals(n)) {
				pr.addViewportShowListener(e -> pr.handleEvent(container, DocumentConstants.SLIDE,
						ProcessEventTypes.SlideShow, a, ComponentEventCallback.DEFAULT));
			} else if (DocumentConstants.FINISH.equals(n)) {
				pr.addViewportFinishListener(e -> pr.handleEvent(container, DocumentConstants.SLIDE, null, a,
						ComponentEventCallback.DEFAULT));
			} else if (DocumentConstants.SHORTCUT.equals(n)) {
				String key = DocumentUtility.getKey(h);
				ShortcutKeys shortcutKeys = ShortcutUtility.parseShortcut(key);
				if (shortcutKeys != null) {
					KeyAction keyAction = new KeyAction(shortcutKeys.getKeyCode(), shortcutKeys.getModifiers());
					final String shortcut = keyAction.toString();

					keyAction.addKeypressListener(e -> pr.handleEvent(container, DocumentConstants.SLIDE,
							ProcessEventTypes.ShortcutKey, a, c -> {
								c.setTimestamp(e.getServerDatetime());
								c.setClientTimestamp(e.getClientDatetime());
								c.setProperty("shortcutKey", shortcut, "shortcut@key");
							}));
					pr.addKeyAction(keyAction);
				}
			} else if (DocumentConstants.MESSAGE.equals(n)) {
				String uid = DocumentUtility.getUid(h);
				if (StringUtils.isNotEmpty(uid)) {
					pr.addMessageListener(uid, e -> {
						final MessageEvent messageEvent = (MessageEvent) e;
						presenter.handleEvent(container, DocumentConstants.SLIDE, ProcessEventTypes.Message, a, c -> {
							c.setProperty("message", messageEvent.getMessage(), "");
							c.setProperty("messageUID", messageEvent.getMessage().getUid(), "message@UID");
						});
					});
				}
			}
		});

	}

	private ComponentWrapper createComponentFromElement(Element element, SlideContainerPresenter presenter) {
		if (element != null) {
			String id = DocumentUtility.getId(element);

			String name = element.getName();

			Component component = null;

			StringMap properties = DocumentUtility.getPropertyValueMap(element);
			AlignmentWrapper alignmentWrapper = new AlignmentWrapperImpl();

			if (name.equals(DocumentConstants.VERTICAL_LAYOUT))
				component = createVerticalLayout(element, properties, alignmentWrapper, presenter);
			else if (name.equals(DocumentConstants.HORIZONTAL_LAYOUT))
				component = createHorizontalLayout(element, properties, alignmentWrapper, presenter);
			else if (name.equals(DocumentConstants.FORM_LAYOUT))
				component = createFormLayout(element, properties, alignmentWrapper, presenter);

			else if (name.equals(DocumentConstants.PANEL))
				component = createPanel(element, properties, alignmentWrapper, presenter);
			else if (name.equals(DocumentConstants.IMAGE))
				component = createImage(element, properties, alignmentWrapper, presenter);
			else if (name.equals(DocumentConstants.VIDEO))
				component = createVideo(element, properties, alignmentWrapper, presenter);
			else if (name.equals(DocumentConstants.AUDIO))
				component = createAudio(element, properties, alignmentWrapper, presenter);
			else if (name.equals(DocumentConstants.BUTTON))
				component = createButton(element, properties, alignmentWrapper, presenter);
			else if (name.equals(DocumentConstants.BUTTON_PANEL))
				component = createButtonPanel(element, properties, alignmentWrapper, presenter);

			else if (name.equals(DocumentConstants.SELECT_PANEL))
				component = createSelectPanel(element, properties, alignmentWrapper, presenter);
			else if (name.equals(DocumentConstants.TEXT_FIELD))
				component = createTextField(element, properties, alignmentWrapper, presenter);
			else if (name.equals(DocumentConstants.TEXT_AREA))
				component = createTextArea(element, properties, alignmentWrapper, presenter);
			else if (name.equals(DocumentConstants.DATE_FIELD))
				component = createDateField(element, properties, alignmentWrapper, presenter);
			else if (name.equals(DocumentConstants.COMBOBOX))
				component = createComboBox(element, properties, alignmentWrapper, presenter);
			else if (name.equals(DocumentConstants.TIMER_LABEL))
				component = createTimerLabel(element, properties, alignmentWrapper, presenter);
			else if (name.equals(DocumentConstants.LABEL))
				component = createLabel(element, properties, alignmentWrapper, presenter);

			if (component != null) {
				if (id != null) {
					presenter.setComponent(id, component);
				}

				return new ComponentWrapperImpl(component, alignmentWrapper.getAlignment());
			} else {
				return createPluginComponent(element, presenter);
			}
		}

		return null;
	}

	private VerticalLayout createVerticalLayout(Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper, SlideContainerPresenter presenter) {
		VerticalLayout component = new VerticalLayout();
		ComponentUtility.setCommonLayoutProperties(component, element, properties, alignmentWrapper);
		addVerticalLayoutComponents(component, element, presenter);

		return component;
	}

	// FIXME very close to addHorizontalLayoutComponents
	private void addVerticalLayoutComponents(VerticalLayout container, Element element,
			SlideContainerPresenter presenter) {
		List<Element> elements = DocumentUtility.getContainerComponents(element,
				ValidationSets.VALID_CONTAINER_CHILDREN);

		if (elements != null) {
			elements.stream().map(m -> createComponentFromElement(m, presenter)).filter(f -> f != null).forEach(e -> {
				Component component = e.getComponent();

				container.addComponent(component);
				container.setComponentAlignment(component, e.getAlignment());

				float ratio = 1.0f;
				if (component.getHeightUnits() == Unit.PERCENTAGE) {
					ratio = component.getHeight() / 100;
					component.setHeight("100%");
				}

				container.setExpandRatio(component, ratio);
			});
		}
	}

	private HorizontalLayout createHorizontalLayout(Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper, SlideContainerPresenter presenter) {
		HorizontalLayout component = new HorizontalLayout();
		ComponentUtility.setCommonLayoutProperties(component, element, properties, alignmentWrapper);
		addHorizontalLayoutComponents(component, element, presenter);

		return component;
	}

	// FIXME very close to addVerticalLayoutComponents
	private void addHorizontalLayoutComponents(HorizontalLayout container, Element element,
			SlideContainerPresenter presenter) {
		List<Element> elements = DocumentUtility.getContainerComponents(element,
				ValidationSets.VALID_CONTAINER_CHILDREN);

		if (elements != null) {
			elements.stream().map(m -> createComponentFromElement(m, presenter)).filter(f -> f != null).forEach(e -> {
				Component component = e.getComponent();

				container.addComponent(component);
				container.setComponentAlignment(component, e.getAlignment());

				float ratio = 1.0f;
				if (component.getWidthUnits() == Unit.PERCENTAGE) {
					ratio = component.getWidth() / 100;
					component.setWidth("100%");
				}

				container.setExpandRatio(component, ratio);
			});
		}
	}

	private FormLayout createFormLayout(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		FormLayout component = new FormLayout();
		ComponentUtility.setCommonLayoutProperties(component, element, properties, alignmentWrapper);
		addFormLayoutComponents(component, element, presenter);

		return component;
	}

	// FIXME similar to addHorizontalLayoutComponents and
	// addVerticalLayoutComponents
	private void addFormLayoutComponents(FormLayout container, Element element, SlideContainerPresenter presenter) {
		List<Element> elements = DocumentUtility.getContainerComponents(element,
				ValidationSets.VALID_CONTAINER_CHILDREN);

		if (elements != null) {
			elements.stream().map(m -> createComponentFromElement(m, presenter)).filter(f -> f != null).forEach(e -> {
				Component component = e.getComponent();

				container.addComponent(component);
				container.setComponentAlignment(component, e.getAlignment());
			});
		}
	}

	private Panel createPanel(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		Panel component = new Panel();
		SlideComponentUtility.setPanelProperties(component, element, properties, alignmentWrapper);
		addPanelComponents(component, element, presenter);

		return component;
	}

	// FIXME copy-paste code of addWindowComponents
	private void addPanelComponents(Panel container, Element element, SlideContainerPresenter presenter) {
		List<Element> elements = DocumentUtility.getContainerComponents(element,
				ValidationSets.VALID_CONTAINER_CHILDREN);

		if (elements != null) {
			elements.stream().map(m -> createComponentFromElement(m, presenter)).filter(f -> f != null).forEach(e -> {
				Component component = e.getComponent();

				if (elements.size() == 1 && component instanceof Layout) {
					container.setContent((Layout) component);
				} else {
					GridLayout gridLayout = new GridLayout(1, 1);
					gridLayout.setSizeFull();
					container.setContent(gridLayout);
					gridLayout.addComponent(component);
					gridLayout.setComponentAlignment(component, e.getAlignment());
				}
			});
		}
	}

	private Image createImage(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		Image component = new Image();
		SlideComponentUtility.setImageProperties(component, element, properties, alignmentWrapper);
		addImageHandlers(component, element, presenter);

		return component;
	}

	private void addImageHandlers(Image component, Element element, SlidePresenter presenter) {
		DocumentUtility.iterateHandlers(component, element, presenter, (co, el, h, n, ac, a, pr) -> {
			final Image image = (Image) co;

			if (DocumentConstants.CLICK.equals(n)) {
				image.addClickListener(
						e -> pr.handleEvent(image, DocumentConstants.IMAGE, ProcessEventTypes.ImageClick, a, c -> {
							c.setTimestamp(e.getServerDatetime());
							c.setClientTimestamp(e.getClientDatetime());

							c.setProperty("x", e.getRelativeX());
							c.setProperty("y", e.getRelativeY());
						}));
			} else if (DocumentConstants.LOAD.equals(n)) {
				image.addLoadListener(e -> pr.handleEvent(image, DocumentConstants.IMAGE, ProcessEventTypes.ImageLoad,
						a, ComponentUtility.createDefaultEventCallback(e.getServerDatetime(), e.getClientDatetime())));
			} else if (DocumentConstants.ERROR.equals(n)) {
				image.addErrorListener(e -> pr.handleEvent(image, DocumentConstants.IMAGE, ProcessEventTypes.ImageError,
						a, ComponentUtility.createDefaultEventCallback(e.getServerDatetime(), e.getClientDatetime())));
			}
		});
	}

	private Video createVideo(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		Video component = new Video();
		SlideComponentUtility.setVideoProperties(component, element, properties, alignmentWrapper);
		addVideoHandlers(component, element, presenter);

		return component;
	}

	private void addVideoHandlers(Video component, Element element, SlidePresenter presenter) {
		DocumentUtility.iterateHandlers(component, element, presenter, (co, el, h, n, ac, a, pr) -> {
			final Video video = (Video) co;
			if (DocumentConstants.CLICK.equals(n)) {
				video.addClickListener(
						e -> pr.handleEvent(video, DocumentConstants.VIDEO, ProcessEventTypes.VideoClick, a, c -> {
							c.setTimestamp(e.getServerDatetime());
							c.setClientTimestamp(e.getClientDatetime());

							c.setProperty("x", e.getRelativeX());
							c.setProperty("y", e.getRelativeY());
						}));
			} else if (DocumentConstants.LOAD.equals(n)) {
				video.addCanPlayThroughListener(e -> pr.handleEvent(video, DocumentConstants.VIDEO,
						ProcessEventTypes.VideoLoad, a,
						ComponentUtility.createDefaultEventCallback(e.getServerDatetime(), e.getClientDatetime())));
			} else if (DocumentConstants.START.equals(n)) {
				video.addStartListener(e -> presenter.handleEvent(video, DocumentConstants.VIDEO,
						ProcessEventTypes.VideoStart, a, c -> c.setProperty("time", e.getMediaTime())));
			} else if (DocumentConstants.STOP.equals(n)) {
				video.addStopListener(
						e -> pr.handleEvent(video, DocumentConstants.VIDEO, ProcessEventTypes.VideoStop, a, c -> {
							c.setTimestamp(e.getServerDatetime());
							c.setClientTimestamp(e.getClientDatetime());

							c.setProperty("time", e.getMediaTime());
						}));
			}
		});
	}

	private Audio createAudio(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		Audio component = new Audio();
		SlideComponentUtility.setAudioProperties(component, element, properties, alignmentWrapper);
		addAudioHandlers(component, element, presenter);

		return component;
	}

	private void addAudioHandlers(Audio component, Element element, SlidePresenter presenter) {
		DocumentUtility.iterateHandlers(component, element, presenter, (co, el, h, n, ac, a, pr) -> {
			final Audio audio = (Audio) co;

			if (DocumentConstants.LOAD.equals(n)) {
				audio.addCanPlayThroughListener(e -> pr.handleEvent(audio, DocumentConstants.AUDIO,
						ProcessEventTypes.AudioLoad, a,
						ComponentUtility.createDefaultEventCallback(e.getServerDatetime(), e.getClientDatetime())));
			} else if (DocumentConstants.START.equals(n)) {
				audio.addStartListener(
						e -> pr.handleEvent(audio, DocumentConstants.AUDIO, ProcessEventTypes.AudioStart, a, c -> {
							c.setTimestamp(e.getServerDatetime());
							c.setClientTimestamp(e.getClientDatetime());

							c.setProperty("time", e.getMediaTime());
						}));
			} else if (DocumentConstants.STOP.equals(n)) {
				audio.addStopListener(
						e -> pr.handleEvent(audio, DocumentConstants.AUDIO, ProcessEventTypes.AudioStop, a, c -> {
							c.setTimestamp(e.getServerDatetime());
							c.setClientTimestamp(e.getClientDatetime());

							c.setProperty("time", e.getMediaTime());
						}));
			}
		});
	}

	private Button createButton(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		Button component = new Button();
		SlideComponentUtility.setButtonProperties(component, element, properties, alignmentWrapper);
		addButtonHandlers(component, element, presenter);

		return component;
	}

	private void addButtonHandlers(Button component, Element element, SlidePresenter presenter) {
		DocumentUtility.iterateHandlers(component, element, presenter, (co, el, h, n, ac, a, pr) -> {
			final Button button = (Button) co;

			if (DocumentConstants.CLICK.equals(n)) {
				button.addClickListener(e -> pr.handleEvent(button, DocumentConstants.BUTTON,
						ProcessEventTypes.ButtonClick, a,
						ComponentUtility.createDefaultEventCallback(e.getServerDatetime(), e.getClientDatetime())));
			}
		});
	}

	private ButtonPanel createButtonPanel(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		ButtonPanel component = new ButtonPanel();
		SlideComponentUtility.setButtonPanelProperties(component, element, properties, alignmentWrapper);
		addButtonPanelHandlers(component, element, presenter);

		return component;
	}

	private void addButtonPanelHandlers(ButtonPanel component, Element element, SlidePresenter presenter) {
		DocumentUtility.iterateHandlers(component, element, presenter, (co, el, h, n, ac, a, pr) -> {
			final ButtonPanel buttonPanel = (ButtonPanel) co;

			if (DocumentConstants.CLICK.equals(n)) {
				buttonPanel.addButtonClickListener(e -> pr.handleEvent(buttonPanel, DocumentConstants.BUTTON_PANEL,
						ProcessEventTypes.ButtonPanelClick, a, c -> {
							c.setTimestamp(e.getServerDatetime());
							c.setClientTimestamp(e.getClientDatetime());

							c.setProperty("button", e.getButton(), "");
							c.setProperty("selectedCaption", e.getButton().getCaption(), "selected");
							c.setProperty("selectedIndex", buttonPanel.getChildIndex(e.getButton()) + 1,
									"selected@index");
						}));
			}
		});
	}

	private SelectPanel createSelectPanel(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		SelectPanel component = new SelectPanel();
		SlideComponentUtility.setSelectPanelProperties(component, element, properties, alignmentWrapper);
		addSelectPanelHandlers(component, element, presenter);
		addSelectPanelValidators(component, element);

		return component;
	}

	private void addSelectPanelHandlers(final SelectPanel component, Element element, final SlidePresenter presenter) {
		DocumentUtility.iterateHandlers(component, element, presenter, (co, el, h, n, ac, a, pr) -> {
			final SelectPanel selectPanel = (SelectPanel) co;

			if (DocumentConstants.CLICK.equals(n)) {
				selectPanel.addButtonClickListener(e -> pr.handleEvent(selectPanel, DocumentConstants.SELECT_PANEL,
						ProcessEventTypes.SelectPanelClick, a, c -> {
							c.setTimestamp(e.getServerDatetime());
							c.setClientTimestamp(e.getClientDatetime());

							c.setProperty("button", SelectButton.class, e.getSelectButton(), "");
							c.setProperty("selectedCaption", e.getSelectButton().getCaption(), "selected");
							c.setProperty("selectedIndex", selectPanel.getChildIndex(e.getSelectButton()) + 1,
									"selected@index");
							c.setProperty("selectedValue", e.getSelectButton().getValue() ? "true" : "false",
									"selected@value");
						}));
			}
		});

		// add default click handler if none defined
		if (!component.hasClickListener()) {
			component.addButtonClickListener(e -> presenter.handleEvent(component, DocumentConstants.SELECT_PANEL,
					ProcessEventTypes.SelectPanelClick, null, c -> {
						c.setTimestamp(e.getServerDatetime());
						c.setClientTimestamp(e.getClientDatetime());

						c.setProperty("button", e.getSource());
						c.setProperty("selectedIndex", component.getChildIndex(e.getSelectButton()) + 1,
								"selected@index");
					}));
		}
	}

	private void iterateValidators(Component component, Element element, ValidatorCallback callback) {
		if (component instanceof Validatable) {
			List<Element> validators = DocumentUtility.getComponentValidators(element);

			if (validators != null) {
				validators.forEach(e -> {
					String name = e.getName();
					String message = DocumentUtility.getValidatorMessage(e, "");

					callback.setComponentValidator((Validatable) component, e, name, message);
				});

				if (!validators.isEmpty() && component instanceof AbstractComponent) {
					((AbstractComponent) component).setImmediate(true);
				}
			}
		}
	}

	private void addSelectPanelValidators(SelectPanel component, Element element) {
		iterateValidators(component, element, (co, el, n, m) -> {
			if (DocumentConstants.EMPTY.equals(n)) {
				co.addValidator(new SelectPanelEmptyValidator(m));
			}
		});
	}

	private TextField createTextField(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		TextField component = new TextField();
		SlideComponentUtility.setTextFieldProperties(component, element, properties, alignmentWrapper);
		addTextFieldValidators(component, element);

		return component;
	}

	private void addTextFieldValidators(TextField component, Element element) {
		iterateValidators(component, element, (co, el, n, m) -> {
			if (DocumentConstants.EMPTY.equals(n)) {
				co.addValidator(new EmptyValidator(m));
			} else if (DocumentConstants.INTEGER.equals(n)) {
				co.addValidator(new IntegerValidator(m));
			} else if (DocumentConstants.NUMBER.equals(n)) {
				co.addValidator(new NumberValidator(m));
			} else if (DocumentConstants.RANGE.equals(n)) {
				Double minValue = DocumentUtility.getNumberValidatorMinValue(el);
				Double maxValue = DocumentUtility.getNumberValidatorMaxValue(el);

				if (minValue != null || maxValue != null) {
					co.addValidator(new NumberRangeValidator(m, minValue, maxValue));
				}
			}
		});
	}

	private TextArea createTextArea(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		TextArea component = new TextArea();
		SlideComponentUtility.setTextAreaProperties(component, element, properties, alignmentWrapper);
		// addTextAreaValidators(component, element);

		return component;
	}

	private DateField createDateField(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		DateField component = new DateField();
		SlideComponentUtility.setDateFieldProperties(component, element, properties, alignmentWrapper);
		addDateFieldValidators(component, element);

		return component;
	}

	private void addDateFieldValidators(DateField component, Element element) {
		iterateValidators(component, element, (co, el, n, m) -> {
			if (DocumentConstants.EMPTY.equals(n)) {
				co.addValidator(new EmptyValidator(m));
			} else if (DocumentConstants.RANGE.equals(n)) {
				Date minValue = DocumentUtility.getDateValidatorMinValue(el, "yyyy-MM-dd");
				Date maxValue = DocumentUtility.getDateValidatorMaxValue(el, "yyyy-MM-dd");

				if (minValue != null || maxValue != null) {
					co.addValidator(new DateRangeValidator(m, minValue, maxValue));
				}
			}
		});

	}

	private ComboBox createComboBox(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		ComboBox component = new ComboBox();
		SlideComponentUtility.setComboBoxProperties(component, element, properties, alignmentWrapper);
		addComboBoxItems(component, element);
		addComboBoxValidators(component, element);

		return component;
	}

	private void addComboBoxItems(ComboBox component, Element element) {
		List<Element> items = DocumentUtility.getComponentItems(element);

		if (items != null) {
			items.stream().forEach(e -> {
				String value = DocumentUtility.getValue(e);
				if (StringUtils.isNotEmpty(value)) {
					component.addItem(value);

					String caption = DocumentUtility.getCaption(e);
					if (StringUtils.isNotEmpty(caption))
						component.setItemCaption(value, caption);
				}
			});
		}

		component.setImmediate(true);
	}

	private void addComboBoxValidators(ComboBox component, Element element) {
		iterateValidators(component, element, (co, el, n, m) -> {
			if (DocumentConstants.EMPTY.equals(n)) {
				co.addValidator(new EmptyValidator(m));
			}
		});
	}

	private TimerLabel createTimerLabel(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		TimerLabel component = new TimerLabel();
		SlideComponentUtility.setTimerLabelProperties(component, element, properties, alignmentWrapper);

		Component buddy = presenter.getTimer(properties.get(DocumentConstants.TIMER_ID));
		if (buddy instanceof Timer) {
			component.setTimer((Timer) buddy);
		}

		return component;
	}

	private Label createLabel(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		Label component = new Label();
		SlideComponentUtility.setLabelProperties(component, element, properties, alignmentWrapper);

		return component;
	}

	private ComponentWrapper createPluginComponent(Element element, SlidePresenter presenter) {
		String name = element.getShortName();
		String namespace = element.getNamespace();

		if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(namespace)) {
			// find registered plugin
			SlideComponentPlugin componentPlugin = PluginManager.get().getComponentPlugin(namespace, name);

			if (componentPlugin != null) {
				return componentPlugin.createComponentFromElement(element, presenter);
			}
		}

		return null;
	}

}

/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.builder;

import static com.vaadin.server.Sizeable.Unit.PERCENTAGE;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.hypothesis.common.ValidationSets.VALID_CONTAINER_CHILDREN;
import static org.hypothesis.common.ValidationSets.VALID_WINDOW_CHILDREN;
import static org.hypothesis.common.utility.StringUtility.toInteger;
import static org.hypothesis.event.model.ProcessEventTypes.AudioLoad;
import static org.hypothesis.event.model.ProcessEventTypes.AudioStart;
import static org.hypothesis.event.model.ProcessEventTypes.AudioStop;
import static org.hypothesis.event.model.ProcessEventTypes.ButtonClick;
import static org.hypothesis.event.model.ProcessEventTypes.ButtonPanelClick;
import static org.hypothesis.event.model.ProcessEventTypes.ClientSimEvent;
import static org.hypothesis.event.model.ProcessEventTypes.ImageClick;
import static org.hypothesis.event.model.ProcessEventTypes.ImageError;
import static org.hypothesis.event.model.ProcessEventTypes.ImageLoad;
import static org.hypothesis.event.model.ProcessEventTypes.Message;
import static org.hypothesis.event.model.ProcessEventTypes.SelectPanelClick;
import static org.hypothesis.event.model.ProcessEventTypes.ShortcutKey;
import static org.hypothesis.event.model.ProcessEventTypes.SlideInit;
import static org.hypothesis.event.model.ProcessEventTypes.SlideShow;
import static org.hypothesis.event.model.ProcessEventTypes.TimerStart;
import static org.hypothesis.event.model.ProcessEventTypes.TimerStop;
import static org.hypothesis.event.model.ProcessEventTypes.TimerUpdate;
import static org.hypothesis.event.model.ProcessEventTypes.VideoClick;
import static org.hypothesis.event.model.ProcessEventTypes.VideoLoad;
import static org.hypothesis.event.model.ProcessEventTypes.VideoStart;
import static org.hypothesis.event.model.ProcessEventTypes.VideoStop;
import static org.hypothesis.event.model.ProcessEventTypes.WindowClose;
import static org.hypothesis.event.model.ProcessEventTypes.WindowInit;
import static org.hypothesis.event.model.ProcessEventTypes.WindowOpen;
import static org.hypothesis.interfaces.ComponentEventCallback.DEFAULT;
import static org.hypothesis.interfaces.DocumentConstants.AUDIO;
import static org.hypothesis.interfaces.DocumentConstants.BUTTON;
import static org.hypothesis.interfaces.DocumentConstants.BUTTON_PANEL;
import static org.hypothesis.interfaces.DocumentConstants.CLICK;
import static org.hypothesis.interfaces.DocumentConstants.CLIENT;
import static org.hypothesis.interfaces.DocumentConstants.CLIENT_SIM;
import static org.hypothesis.interfaces.DocumentConstants.COMBOBOX;
import static org.hypothesis.interfaces.DocumentConstants.COMPONENT;
import static org.hypothesis.interfaces.DocumentConstants.DATE_FIELD;
import static org.hypothesis.interfaces.DocumentConstants.EMPTY;
import static org.hypothesis.interfaces.DocumentConstants.ERROR;
import static org.hypothesis.interfaces.DocumentConstants.FINISH;
import static org.hypothesis.interfaces.DocumentConstants.FORM_LAYOUT;
import static org.hypothesis.interfaces.DocumentConstants.HORIZONTAL_LAYOUT;
import static org.hypothesis.interfaces.DocumentConstants.IMAGE;
import static org.hypothesis.interfaces.DocumentConstants.INIT;
import static org.hypothesis.interfaces.DocumentConstants.INPUT_VALUE;
import static org.hypothesis.interfaces.DocumentConstants.INTEGER;
import static org.hypothesis.interfaces.DocumentConstants.INTERVAL;
import static org.hypothesis.interfaces.DocumentConstants.LABEL;
import static org.hypothesis.interfaces.DocumentConstants.LOAD;
import static org.hypothesis.interfaces.DocumentConstants.MESSAGE;
import static org.hypothesis.interfaces.DocumentConstants.NUMBER;
import static org.hypothesis.interfaces.DocumentConstants.OUTPUT_VALUE;
import static org.hypothesis.interfaces.DocumentConstants.PANEL;
import static org.hypothesis.interfaces.DocumentConstants.RANGE;
import static org.hypothesis.interfaces.DocumentConstants.SELECT_PANEL;
import static org.hypothesis.interfaces.DocumentConstants.SHORTCUT;
import static org.hypothesis.interfaces.DocumentConstants.SHOW;
import static org.hypothesis.interfaces.DocumentConstants.SLIDE;
import static org.hypothesis.interfaces.DocumentConstants.START;
import static org.hypothesis.interfaces.DocumentConstants.STOP;
import static org.hypothesis.interfaces.DocumentConstants.TEXT_AREA;
import static org.hypothesis.interfaces.DocumentConstants.TEXT_FIELD;
import static org.hypothesis.interfaces.DocumentConstants.TIMER;
import static org.hypothesis.interfaces.DocumentConstants.TIMER_ID;
import static org.hypothesis.interfaces.DocumentConstants.TIMER_LABEL;
import static org.hypothesis.interfaces.DocumentConstants.UPDATE;
import static org.hypothesis.interfaces.DocumentConstants.VERTICAL_LAYOUT;
import static org.hypothesis.interfaces.DocumentConstants.VIDEO;
import static org.hypothesis.interfaces.DocumentConstants.WINDOW;

import java.util.Date;
import java.util.EventObject;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hypothesis.business.ShortcutUtility;
import org.hypothesis.business.ShortcutUtility.ShortcutKeys;
import org.hypothesis.common.AlignmentWrapperImpl;
import org.hypothesis.common.ComponentWrapperImpl;
import org.hypothesis.common.utility.ComponentUtility;
import org.hypothesis.common.utility.DocumentUtility;
import org.hypothesis.common.utility.EvaluableUtility;
import org.hypothesis.data.DocumentReader;
import org.hypothesis.evaluation.IndexedExpression;
import org.hypothesis.event.model.MessageEvent;
import org.hypothesis.extension.PluginManager;
import org.hypothesis.interfaces.Action;
import org.hypothesis.interfaces.AlignmentWrapper;
import org.hypothesis.interfaces.ComponentEvent;
import org.hypothesis.interfaces.ComponentEventCallback;
import org.hypothesis.interfaces.ComponentWrapper;
import org.hypothesis.interfaces.Document;
import org.hypothesis.interfaces.DocumentConstants;
import org.hypothesis.interfaces.Element;
import org.hypothesis.interfaces.Evaluator;
import org.hypothesis.interfaces.HandlerCallback;
import org.hypothesis.interfaces.MessageEventListener;
import org.hypothesis.interfaces.ReferenceCallback;
import org.hypothesis.interfaces.SlideComponentPlugin;
import org.hypothesis.interfaces.SlidePresenter;
import org.hypothesis.interfaces.ViewportEventListener;
import org.hypothesis.presenter.SlideContainerPresenter;
import org.hypothesis.slide.ui.Audio;
import org.hypothesis.slide.ui.Button;
import org.hypothesis.slide.ui.ButtonPanel;
import org.hypothesis.slide.ui.ClientSim;
import org.hypothesis.slide.ui.ClientSim.ClientEvent;
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
import org.vaadin.special.event.MouseEvents;
import org.vaadin.special.ui.KeyAction;
import org.vaadin.special.ui.KeyAction.KeyActionEvent;
import org.vaadin.special.ui.KeyAction.KeyActionListener;
import org.vaadin.special.ui.Media;
import org.vaadin.special.ui.SelectButton;

import com.vaadin.data.Validatable;
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

	private static final Logger log = Logger.getLogger(SlideContainerFactoryImpl.class);

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
		} catch (Throwable e) {
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
		createScoreExpressions(rootElement, presenter);

		createWindows(rootElement, presenter);
		createViewport(rootElement, presenter);

		EvaluableUtility.createVariables(rootElement, presenter, new ReferenceCallback() {
			@Override
			public Object getReference(String name, String id, Evaluator evaluator) {
				if (isNotEmpty(id) && evaluator instanceof SlideContainerPresenter) {
					SlideContainerPresenter presenter = (SlideContainerPresenter) evaluator;

					if (COMPONENT.equals(name)) {
						return presenter.getComponent(id);
					} else if (TIMER.equals(name)) {
						return presenter.getTimer(id);
					} else if (WINDOW.equals(name)) {
						return presenter.getWindow(id);
					}
				}
				return null;
			}
		});

		return presenter.getSlideContainer();
	}

	private void createInputExpressions(Element rootElement, SlideContainerPresenter presenter) {
		List<Element> inputElements = DocumentUtility.getInputValueElements(rootElement);

		if (inputElements != null) {
			for (Element inputElement : inputElements) {
				IndexedExpression indexedExpression = EvaluableUtility.createValueExpression(inputElement, INPUT_VALUE);
				if (indexedExpression != null) {
					presenter.setInputExpression(indexedExpression.getIndex(), indexedExpression);
				}
			}
		}
	}

	private void createOutputExpressions(Element rootElement, SlideContainerPresenter presenter) {
		List<Element> outputElements = DocumentUtility.getOutputValueElements(rootElement);

		if (outputElements != null) {
			for (Element outputElement : outputElements) {
				IndexedExpression indexedExpression = EvaluableUtility.createValueExpression(outputElement,
						OUTPUT_VALUE);
				if (indexedExpression != null) {
					presenter.setOutputExpression(indexedExpression.getIndex(), indexedExpression);
				}
			}
		}
	}

	private void createScoreExpressions(Element rootElement, SlideContainerPresenter presenter) {
		List<Element> scoreElements = DocumentUtility.getScoresElements(rootElement);

		if (scoreElements != null) {
			for (Element scoreElement : scoreElements) {
				IndexedExpression indexedExpression = EvaluableUtility.createScoreExpression(scoreElement);
				if (indexedExpression != null) {
					presenter.setScoreExpression(indexedExpression.getIndex(), indexedExpression);
				}
			}
		}
	}

	private void createTimers(Element rootElement, SlideContainerPresenter presenter) {
		List<Element> elements = DocumentUtility.getTimersElements(rootElement);

		if (elements != null) {
			for (Element element : elements) {
				String id = DocumentUtility.getId(element);
				if (isNotEmpty(id)) {

					Timer timer = createTimer(element, presenter);
					presenter.setTimer(id, timer);
				}
			}
		}
	}

	private Timer createTimer(Element element, SlideContainerPresenter presenter) {
		Map<String, String> properties = DocumentUtility.getPropertyValueMap(element);

		Timer component = new Timer();
		SlideComponentUtility.setTimerProperties(component, element, properties);
		addTimerHandlers(component, element, presenter);

		return component;
	}

	private void addTimerHandlers(Timer component, Element element, SlideContainerPresenter presenter) {
		DocumentUtility.iterateHandlers(component, element, presenter, new HandlerCallback() {
			@Override
			public void setComponentHandler(Component component, Element element, Element handler, String name,
					String actionId, final Action action, final SlidePresenter presenter) {
				final Timer timer = (Timer) component;

				if (START.equals(name)) {
					timer.addStartListener(new Timer.StartListener() {
						@Override
						public void start(final Timer.StartEvent event) {
							presenter.handleEvent(timer, TIMER, TimerStart, action, new ComponentEventCallback() {
								@Override
								public void initEvent(ComponentEvent componentEvent) {
									componentEvent.setProperty("time", event.getTime());
								}
							});
						}
					});
				} else if (STOP.equals(name)) {
					timer.addStopListener(new Timer.StopListener() {
						@Override
						public void stop(final Timer.StopEvent event) {
							presenter.handleEvent(timer, TIMER, TimerStop, action, new ComponentEventCallback() {
								@Override
								public void initEvent(ComponentEvent componentEvent) {
									componentEvent.setProperty("time", event.getTime());
								}
							});
						}
					});
				} else if (UPDATE.equals(name)) {
					Integer interval = toInteger((element.getAttribute(INTERVAL)));
					if (interval != null) {
						timer.addUpdateListener(interval, new Timer.UpdateListener() {
							@Override
							public void update(final Timer.UpdateEvent event) {
								presenter.handleEvent(timer, TIMER, TimerUpdate, action, new ComponentEventCallback() {
									@Override
									public void initEvent(ComponentEvent componentEvent) {
										componentEvent.setProperty("time", event.getTime());
									}
								});
							}
						});
					}
				}
			}
		});
	}

	private void createWindows(Element rootElement, SlideContainerPresenter presenter) {
		List<Element> elements = DocumentUtility.getWindowsElements(rootElement);

		if (elements != null) {
			for (Element windowElement : elements) {
				String id = DocumentUtility.getId(windowElement);
				if (isNotEmpty(id)) {
					Element element = DocumentUtility.getViewportOrWindowRootElement(windowElement);

					if (element != null) {
						Window window = createWindow(element, presenter);
						presenter.setWindow(id, window);
					}
				}
			}
		}
	}

	private Window createWindow(Element element, SlideContainerPresenter presenter) {
		Map<String, String> properties = DocumentUtility.getPropertyValueMap(element);

		Window component = new Window();
		SlideComponentUtility.setWindowProperties(component, element, properties, new AlignmentWrapperImpl());
		addWindowComponents(component, element, presenter);
		addWindowHandlers(component, element, presenter);

		return component;
	}

	private void addWindowComponents(Window container, Element element, SlideContainerPresenter presenter) {
		List<Element> elements = DocumentUtility.getContainerComponents(element, VALID_WINDOW_CHILDREN);

		if (elements != null) {
			for (Element childElement : elements) {
				ComponentWrapper componentWrapper = createComponentFromElement(childElement, presenter);
				if (componentWrapper != null) {
					Component component = componentWrapper.getComponent();

					if (elements.size() == 1 && component instanceof Layout) {
						container.setContent((Layout) component);
					} else {
						GridLayout gridLayout = new GridLayout(1, 1);
						gridLayout.setSizeFull();
						container.setContent(gridLayout);
						gridLayout.addComponent(component);
						gridLayout.setComponentAlignment(component, componentWrapper.getAlignment());
					}
				}
			}
		}
	}

	private void addWindowHandlers(Window component, Element element, SlideContainerPresenter presenter) {
		DocumentUtility.iterateHandlers(component, element, presenter, new HandlerCallback() {
			@Override
			public void setComponentHandler(Component component, Element element, Element handler, String name,
					String actionId, final Action action, final SlidePresenter presenter) {
				final Window window = (Window) component;

				if (DocumentConstants.INIT.equals(name)) {
					window.addInitListener(new Window.InitListener() {
						@Override
						public void initWindow(Window.InitEvent event) {
							presenter.handleEvent(window, WINDOW, WindowInit, action, DEFAULT);
						}
					});
				} else if (DocumentConstants.OPEN.equals(name)) {
					window.addOpenListener(new Window.OpenListener() {
						@Override
						public void openWindow(Window.OpenEvent event) {
							presenter.handleEvent(window, WINDOW, WindowOpen, action, DEFAULT);
						}
					});
				} else if (DocumentConstants.CLOSE.equals(name)) {
					window.addCloseListener(new Window.CloseListener() {
						@Override
						public void windowClose(Window.CloseEvent event) {
							presenter.handleEvent(window, WINDOW, WindowClose, action, DEFAULT);
						}
					});
				}
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
		DocumentUtility.iterateHandlers(component, element, presenter, new HandlerCallback() {
			@Override
			public void setComponentHandler(Component component, Element element, Element handler, String name,
					String actionId, final Action action, final SlidePresenter presenter) {
				final SlideContainer container = (SlideContainer) component;

				if (INIT.equals(name)) {
					presenter.addViewportInitListener(new ViewportEventListener() {
						@Override
						public void handleEvent(EventObject event) {
							presenter.handleEvent(container, SLIDE, SlideInit, action, DEFAULT);
						}
					});
				} else if (SHOW.equals(name)) {
					presenter.addViewportShowListener(new ViewportEventListener() {
						@Override
						public void handleEvent(EventObject event) {
							presenter.handleEvent(container, SLIDE, SlideShow, action, DEFAULT);
						}
					});
				} else if (FINISH.equals(name)) {
					presenter.addViewportFinishListener(new ViewportEventListener() {
						@Override
						public void handleEvent(EventObject event) {
							presenter.handleEvent(container, SLIDE, null, action, DEFAULT);
						}
					});
				} else if (SHORTCUT.equals(name)) {
					String key = DocumentUtility.getKey(handler);
					ShortcutKeys shortcutKeys = ShortcutUtility.parseShortcut(key);
					if (shortcutKeys != null) {
						KeyAction keyAction = new KeyAction(shortcutKeys.getKeyCode(), shortcutKeys.getModifiers());

						// ShortcutKey shortcutKey = new
						// ShortcutKey(shortcutKeys.getKeyCode(),
						// shortcutKeys.getModifiers());

						final String shortcut = keyAction.toString();

						keyAction.addKeypressListener(new KeyActionListener() {
							@Override
							public void keyPressed(final KeyActionEvent keyPressEvent) {
								presenter.handleEvent(container, SLIDE, ShortcutKey, action,
										new ComponentEventCallback() {
											@Override
											public void initEvent(ComponentEvent componentEvent) {
												componentEvent.setTimestamp(keyPressEvent.getServerDatetime());
												componentEvent.setClientTimestamp(keyPressEvent.getClientDatetime());
												componentEvent.setProperty("shortcutKey", shortcut, "shortcut@key");
											}
										});
							}
						});

						// shortcutKey.addKeyPressListener(new
						// KeyPressListener() {
						// @Override
						// public void keyPress(final KeyPressEvent event) {
						// presenter.handleEvent(container,
						// DocumentConstants.SLIDE,
						// ProcessEventTypes.ShortcutKey,
						// action, new ComponentEventCallback() {
						// @Override
						// public void initEvent(ComponentEvent componentEvent)
						// {
						// componentEvent.setProperty("shortcutKey", shortcut,
						// "shortcut@key");
						// }
						// });
						// }
						// });

						// presenter.addShortcutKey(shortcutKey);
						presenter.addKeyAction(keyAction);
					}
				} else if (MESSAGE.equals(name)) {
					String uid = DocumentUtility.getUid(handler);
					if (isNotEmpty(uid)) {
						presenter.addMessageListener(uid, new MessageEventListener() {
							@Override
							public void handleEvent(EventObject event) {
								if (event instanceof MessageEvent) {
									final MessageEvent messageEvent = (MessageEvent) event;

									presenter.handleEvent(container, SLIDE, Message, action,
											new ComponentEventCallback() {
												@Override
												public void initEvent(ComponentEvent componentEvent) {
													componentEvent.setProperty("message", messageEvent.getMessage(),
															"");
													componentEvent.setProperty("messageUID",
															messageEvent.getMessage().getUid(), "message@UID");
												}
											});
								}
							}
						});
					}
				}
			}
		});
	}

	private ComponentWrapper createComponentFromElement(Element element, SlideContainerPresenter presenter) {
		if (element != null) {
			String id = DocumentUtility.getId(element);

			String name = element.getName();

			Component component = null;

			Map<String, String> properties = DocumentUtility.getPropertyValueMap(element);
			AlignmentWrapper alignmentWrapper = new AlignmentWrapperImpl();

			if (name.equals(VERTICAL_LAYOUT))
				component = createVerticalLayout(element, properties, alignmentWrapper, presenter);
			else if (name.equals(HORIZONTAL_LAYOUT))
				component = createHorizontalLayout(element, properties, alignmentWrapper, presenter);
			else if (name.equals(FORM_LAYOUT))
				component = createFormLayout(element, properties, alignmentWrapper, presenter);

			else if (name.equals(PANEL))
				component = createPanel(element, properties, alignmentWrapper, presenter);
			else if (name.equals(IMAGE))
				component = createImage(element, properties, alignmentWrapper, presenter);
			else if (name.equals(VIDEO))
				component = createVideo(element, properties, alignmentWrapper, presenter);
			else if (name.equals(AUDIO))
				component = createAudio(element, properties, alignmentWrapper, presenter);
			else if (name.equals(BUTTON))
				component = createButton(element, properties, alignmentWrapper, presenter);
			else if (name.equals(BUTTON_PANEL))
				component = createButtonPanel(element, properties, alignmentWrapper, presenter);

			else if (name.equals(SELECT_PANEL))
				component = createSelectPanel(element, properties, alignmentWrapper, presenter);
			else if (name.equals(TEXT_FIELD))
				component = createTextField(element, properties, alignmentWrapper, presenter);
			else if (name.equals(TEXT_AREA))
				component = createTextArea(element, properties, alignmentWrapper, presenter);
			else if (name.equals(DATE_FIELD))
				component = createDateField(element, properties, alignmentWrapper, presenter);
			else if (name.equals(COMBOBOX))
				component = createComboBox(element, properties, alignmentWrapper, presenter);
			else if (name.equals(TIMER_LABEL))
				component = createTimerLabel(element, properties, alignmentWrapper, presenter);
			else if (name.equals(LABEL))
				component = createLabel(element, properties, alignmentWrapper, presenter);
			else if (name.equals(CLIENT_SIM))
				component = createClientSim(element, properties, alignmentWrapper, presenter);

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

	private VerticalLayout createVerticalLayout(Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper, SlideContainerPresenter presenter) {
		VerticalLayout component = new VerticalLayout();
		ComponentUtility.setCommonLayoutProperties(component, element, properties, alignmentWrapper);
		addVerticalLayoutComponents(component, element, presenter);

		return component;
	}

	private void addVerticalLayoutComponents(VerticalLayout container, Element element,
			SlideContainerPresenter presenter) {
		List<Element> elements = DocumentUtility.getContainerComponents(element, VALID_CONTAINER_CHILDREN);

		if (elements != null) {
			for (Element childElement : elements) {
				ComponentWrapper componentWrapper = createComponentFromElement(childElement, presenter);
				if (componentWrapper != null) {
					Component component = componentWrapper.getComponent();

					container.addComponent(component);
					container.setComponentAlignment(component, componentWrapper.getAlignment());

					float ratio = 1.0f;
					if (component.getHeightUnits() == PERCENTAGE) {
						ratio = component.getHeight() / 100;
						component.setHeight("100%");
					}

					container.setExpandRatio(component, ratio);
				}
			}
		}
	}

	private HorizontalLayout createHorizontalLayout(Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper, SlideContainerPresenter presenter) {
		HorizontalLayout component = new HorizontalLayout();
		ComponentUtility.setCommonLayoutProperties(component, element, properties, alignmentWrapper);
		addHorizontalLayoutComponents(component, element, presenter);

		return component;
	}

	private void addHorizontalLayoutComponents(HorizontalLayout container, Element element,
			SlideContainerPresenter presenter) {
		List<Element> elements = DocumentUtility.getContainerComponents(element, VALID_CONTAINER_CHILDREN);

		if (elements != null) {
			for (Element childElement : elements) {
				ComponentWrapper componentWrapper = createComponentFromElement(childElement, presenter);
				if (componentWrapper != null) {
					Component component = componentWrapper.getComponent();

					container.addComponent(component);
					container.setComponentAlignment(component, componentWrapper.getAlignment());

					float ratio = 1.0f;
					if (component.getWidthUnits() == PERCENTAGE) {
						ratio = component.getWidth() / 100;
						component.setWidth("100%");
					}

					container.setExpandRatio(component, ratio);
				}
			}
		}
	}

	private FormLayout createFormLayout(Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper, SlideContainerPresenter presenter) {
		FormLayout component = new FormLayout();
		ComponentUtility.setCommonLayoutProperties(component, element, properties, alignmentWrapper);
		addFormLayoutComponents(component, element, presenter);

		return component;
	}

	private void addFormLayoutComponents(FormLayout container, Element element, SlideContainerPresenter presenter) {
		List<Element> elements = DocumentUtility.getContainerComponents(element, VALID_CONTAINER_CHILDREN);

		if (elements != null) {
			for (Element childElement : elements) {
				ComponentWrapper componentWrapper = createComponentFromElement(childElement, presenter);
				if (componentWrapper != null) {
					Component component = componentWrapper.getComponent();
					container.addComponent(component);
					container.setComponentAlignment(component, componentWrapper.getAlignment());
				}
			}
		}
	}

	private Panel createPanel(Element element, Map<String, String> properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		Panel component = new Panel();
		SlideComponentUtility.setPanelProperties(component, element, properties, alignmentWrapper);
		addPanelComponents(component, element, presenter);

		return component;
	}

	private void addPanelComponents(Panel container, Element element, SlideContainerPresenter presenter) {
		List<Element> elements = DocumentUtility.getContainerComponents(element, VALID_CONTAINER_CHILDREN);

		if (elements != null) {
			for (Element childElement : elements) {
				ComponentWrapper componentWrapper = createComponentFromElement(childElement, presenter);
				if (componentWrapper != null) {
					Component component = componentWrapper.getComponent();

					if (elements.size() == 1 && component instanceof Layout) {
						container.setContent((Layout) component);
					} else {
						GridLayout gridLayout = new GridLayout(1, 1);
						gridLayout.setSizeFull();
						container.setContent(gridLayout);
						gridLayout.addComponent(component);
						gridLayout.setComponentAlignment(component, componentWrapper.getAlignment());
					}
				}
			}
		}
	}

	private Image createImage(Element element, Map<String, String> properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		Image component = new Image();
		SlideComponentUtility.setImageProperties(component, element, properties, alignmentWrapper);
		addImageHandlers(component, element, presenter);

		return component;
	}

	private void addImageHandlers(Image component, Element element, SlidePresenter presenter) {
		DocumentUtility.iterateHandlers(component, element, presenter, new HandlerCallback() {
			@Override
			public void setComponentHandler(Component component, Element element, Element handler, String name,
					String actionId, final Action action, final SlidePresenter presenter) {
				final Image image = (Image) component;

				if (CLICK.equals(name)) {
					image.addClickListener(new MouseEvents.ClickListener() {
						@Override
						public void click(final MouseEvents.ClickEvent event) {
							presenter.handleEvent(image, IMAGE, ImageClick, action, new ComponentEventCallback() {
								@Override
								public void initEvent(ComponentEvent componentEvent) {
									componentEvent.setTimestamp(event.getServerDatetime());
									componentEvent.setClientTimestamp(event.getClientDatetime());

									componentEvent.setProperty("x", event.getRelativeX());
									componentEvent.setProperty("y", event.getRelativeY());
								}
							});
						}
					});
				} else if (LOAD.equals(name)) {
					image.addLoadListener(new Image.LoadListener() {
						@Override
						public void load(Image.LoadEvent event) {
							presenter.handleEvent(image, IMAGE, ImageLoad, action, ComponentUtility
									.createDefaultEventCallback(event.getServerDatetime(), event.getClientDatetime()));
						}
					});
				} else if (ERROR.equals(name)) {
					image.addErrorListener(new Image.ErrorListener() {
						@Override
						public void error(Image.ErrorEvent event) {
							presenter.handleEvent(image, IMAGE, ImageError, action, ComponentUtility
									.createDefaultEventCallback(event.getServerDatetime(), event.getClientDatetime()));
						}
					});
				}
			}
		});
	}

	private Video createVideo(Element element, Map<String, String> properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		Video component = new Video();
		SlideComponentUtility.setVideoProperties(component, element, properties, alignmentWrapper);
		addVideoHandlers(component, element, presenter);

		return component;
	}

	private void addVideoHandlers(Video component, Element element, SlidePresenter presenter) {
		DocumentUtility.iterateHandlers(component, element, presenter, new HandlerCallback() {
			@Override
			public void setComponentHandler(Component component, Element element, Element handler, String name,
					String actionId, final Action action, final SlidePresenter presenter) {
				final Video video = (Video) component;

				if (CLICK.equals(name)) {
					video.addClickListener(new Video.ClickListener() {
						@Override
						public void click(final Video.ClickEvent event) {
							presenter.handleEvent(video, VIDEO, VideoClick, action, new ComponentEventCallback() {
								@Override
								public void initEvent(ComponentEvent componentEvent) {
									componentEvent.setTimestamp(event.getServerDatetime());
									componentEvent.setClientTimestamp(event.getClientDatetime());

									componentEvent.setProperty("x", event.getRelativeX());
									componentEvent.setProperty("y", event.getRelativeY());
								}
							});
						}
					});
				} else if (LOAD.equals(name)) {
					video.addCanPlayThroughListener(new Media.CanPlayThroughListener() {
						@Override
						public void canPlayThrough(Media.CanPlayThroughEvent event) {
							presenter.handleEvent(video, VIDEO, VideoLoad, action, ComponentUtility
									.createDefaultEventCallback(event.getServerDatetime(), event.getClientDatetime()));
						}
					});
				} else if (START.equals(name)) {
					video.addStartListener(new Media.StartListener() {
						@Override
						public void start(final Media.StartEvent event) {
							presenter.handleEvent(video, VIDEO, VideoStart, action, new ComponentEventCallback() {
								@Override
								public void initEvent(ComponentEvent componentEvent) {
									componentEvent.setProperty("time", event.getMediaTime());
								}
							});
						}
					});
				} else if (STOP.equals(name)) {
					video.addStopListener(new Media.StopListener() {
						@Override
						public void stop(final Media.StopEvent event) {
							presenter.handleEvent(video, VIDEO, VideoStop, action, new ComponentEventCallback() {
								@Override
								public void initEvent(ComponentEvent componentEvent) {
									componentEvent.setTimestamp(event.getServerDatetime());
									componentEvent.setClientTimestamp(event.getClientDatetime());

									componentEvent.setProperty("time", event.getMediaTime());
								}
							});
						}
					});
				}
			}
		});
	}

	private Audio createAudio(Element element, Map<String, String> properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		Audio component = new Audio();
		SlideComponentUtility.setAudioProperties(component, element, properties, alignmentWrapper);
		addAudioHandlers(component, element, presenter);

		return component;
	}

	private void addAudioHandlers(Audio component, Element element, SlidePresenter presenter) {
		DocumentUtility.iterateHandlers(component, element, presenter, new HandlerCallback() {
			@Override
			public void setComponentHandler(Component component, Element element, Element handler, String name,
					String actionId, final Action action, final SlidePresenter presenter) {
				final Audio audio = (Audio) component;

				if (LOAD.equals(name)) {
					audio.addCanPlayThroughListener(new Media.CanPlayThroughListener() {
						@Override
						public void canPlayThrough(Media.CanPlayThroughEvent event) {
							presenter.handleEvent(audio, AUDIO, AudioLoad, action, ComponentUtility
									.createDefaultEventCallback(event.getServerDatetime(), event.getClientDatetime()));
						}
					});
				} else if (START.equals(name)) {
					audio.addStartListener(new Media.StartListener() {
						@Override
						public void start(final Media.StartEvent event) {
							presenter.handleEvent(audio, AUDIO, AudioStart, action, new ComponentEventCallback() {
								@Override
								public void initEvent(ComponentEvent componentEvent) {
									componentEvent.setTimestamp(event.getServerDatetime());
									componentEvent.setClientTimestamp(event.getClientDatetime());

									componentEvent.setProperty("time", event.getMediaTime());
								}
							});
						}
					});
				} else if (STOP.equals(name)) {
					audio.addStopListener(new Media.StopListener() {
						@Override
						public void stop(final Media.StopEvent event) {
							presenter.handleEvent(audio, AUDIO, AudioStop, action, new ComponentEventCallback() {
								@Override
								public void initEvent(ComponentEvent componentEvent) {
									componentEvent.setTimestamp(event.getServerDatetime());
									componentEvent.setClientTimestamp(event.getClientDatetime());

									componentEvent.setProperty("time", event.getMediaTime());
								}
							});
						}
					});
				}
			}
		});
	}

	private Button createButton(Element element, Map<String, String> properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		Button component = new Button();
		SlideComponentUtility.setButtonProperties(component, element, properties, alignmentWrapper);
		addButtonHandlers(component, element, presenter);

		return component;
	}

	private void addButtonHandlers(Button component, Element element, SlidePresenter presenter) {
		DocumentUtility.iterateHandlers(component, element, presenter, new HandlerCallback() {
			@Override
			public void setComponentHandler(Component component, Element element, Element handler, String name,
					String actionId, final Action action, final SlidePresenter presenter) {
				final Button button = (Button) component;

				if (CLICK.equals(name)) {
					button.addClickListener(new Button.ClickListener() {
						@Override
						public void buttonClick(Button.ClickEvent event) {
							presenter.handleEvent(button, BUTTON, ButtonClick, action, ComponentUtility
									.createDefaultEventCallback(event.getServerDatetime(), event.getClientDatetime()));
						}
					});
				}
			}
		});
	}

	private ButtonPanel createButtonPanel(Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper, SlideContainerPresenter presenter) {
		ButtonPanel component = new ButtonPanel();
		SlideComponentUtility.setButtonPanelProperties(component, element, properties, alignmentWrapper);
		addButtonPanelHandlers(component, element, presenter);

		return component;
	}

	private void addButtonPanelHandlers(ButtonPanel component, Element element, SlidePresenter presenter) {
		DocumentUtility.iterateHandlers(component, element, presenter, new HandlerCallback() {
			@Override
			public void setComponentHandler(Component component, Element element, Element handler, String name,
					String actionId, final Action action, final SlidePresenter presenter) {
				final ButtonPanel buttonPanel = (ButtonPanel) component;

				if (CLICK.equals(name)) {
					buttonPanel.addButtonClickListener(new Button.ClickListener() {
						@Override
						public void buttonClick(final Button.ClickEvent event) {
							presenter.handleEvent(buttonPanel, BUTTON_PANEL, ButtonPanelClick, action,
									new ComponentEventCallback() {
										@Override
										public void initEvent(ComponentEvent componentEvent) {
											componentEvent.setTimestamp(event.getServerDatetime());
											componentEvent.setClientTimestamp(event.getClientDatetime());

											componentEvent.setProperty("button", event.getButton(), "");
											componentEvent.setProperty("selectedCaption",
													event.getButton().getCaption(), "selected");
											componentEvent.setProperty("selectedIndex",
													buttonPanel.getChildIndex(event.getButton()) + 1, "selected@index");
										}
									});
						}
					});
				}
			}
		});
	}

	private SelectPanel createSelectPanel(Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper, SlideContainerPresenter presenter) {
		SelectPanel component = new SelectPanel();
		SlideComponentUtility.setSelectPanelProperties(component, element, properties, alignmentWrapper);
		addSelectPanelHandlers(component, element, presenter);
		addSelectPanelValidators(component, element);

		return component;
	}

	private void addSelectPanelHandlers(final SelectPanel component, Element element, final SlidePresenter presenter) {
		DocumentUtility.iterateHandlers(component, element, presenter, new HandlerCallback() {
			@Override
			public void setComponentHandler(Component component, Element element, Element handler, String name,
					String actionId, final Action action, final SlidePresenter presenter) {
				final SelectPanel selectPanel = (SelectPanel) component;

				if (CLICK.equals(name)) {
					selectPanel.addButtonClickListener(new SelectButton.ClickListener() {
						@Override
						public void buttonClick(final SelectButton.ClickEvent event) {
							presenter.handleEvent(selectPanel, SELECT_PANEL, SelectPanelClick, action,
									new ComponentEventCallback() {
										@Override
										public void initEvent(ComponentEvent componentEvent) {
											componentEvent.setTimestamp(event.getServerDatetime());
											componentEvent.setClientTimestamp(event.getClientDatetime());

											componentEvent.setProperty("button", SelectButton.class,
													event.getSelectButton(), "");
											componentEvent.setProperty("selectedCaption",
													event.getSelectButton().getCaption(), "selected");
											componentEvent.setProperty("selectedIndex",
													selectPanel.getChildIndex(event.getSelectButton()) + 1,
													"selected@index");
											componentEvent.setProperty("selectedValue",
													event.getSelectButton().getValue() ? "true" : "false",
													"selected@value");
										}
									});
						}
					});
				}
			}
		});

		// add default click handler if none defined
		if (!component.hasClickListener()) {
			component.addButtonClickListener(new SelectButton.ClickListener() {
				@Override
				public void buttonClick(final SelectButton.ClickEvent event) {
					presenter.handleEvent(component, SELECT_PANEL, SelectPanelClick, null,
							new ComponentEventCallback() {
								@Override
								public void initEvent(ComponentEvent componentEvent) {
									componentEvent.setTimestamp(event.getServerDatetime());
									componentEvent.setClientTimestamp(event.getClientDatetime());

									componentEvent.setProperty("button", event.getSource());
									componentEvent.setProperty("selectedIndex",
											component.getChildIndex(event.getSelectButton()) + 1, "selected@index");
								}
							});
				}
			});
		}
	}

	private void iterateValidators(Component component, Element element, ValidatorCallback callback) {
		if (component instanceof Validatable) {
			List<Element> validators = DocumentUtility.getComponentValidators(element);

			if (validators != null) {
				for (Element validatorElement : validators) {
					String name = validatorElement.getName();
					String message = DocumentUtility.getValidatorMessage(validatorElement, "");

					callback.setComponentValidator((Validatable) component, validatorElement, name, message);
				}

				if (!validators.isEmpty() && component instanceof AbstractComponent) {
					((AbstractComponent) component).setImmediate(true);
				}
			}
		}
	}

	private void addSelectPanelValidators(SelectPanel component, Element element) {
		iterateValidators(component, element, new ValidatorCallback() {
			@Override
			public void setComponentValidator(Validatable component, Element element, String name, String message) {
				if (EMPTY.equals(name)) {
					component.addValidator(new SelectPanelEmptyValidator(message));
				}
			}
		});
	}

	private TextField createTextField(Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper, SlideContainerPresenter presenter) {
		TextField component = new TextField();
		SlideComponentUtility.setTextFieldProperties(component, element, properties, alignmentWrapper);
		addTextFieldValidators(component, element);

		return component;
	}

	private void addTextFieldValidators(TextField component, Element element) {
		iterateValidators(component, element, new ValidatorCallback() {
			@Override
			public void setComponentValidator(Validatable component, Element element, String name, String message) {
				if (EMPTY.equals(name)) {
					component.addValidator(new EmptyValidator(message));
				} else if (INTEGER.equals(name)) {
					component.addValidator(new IntegerValidator(message));
				} else if (NUMBER.equals(name)) {
					component.addValidator(new NumberValidator(message));
				} else if (RANGE.equals(name)) {
					Double minValue = DocumentUtility.getNumberValidatorMinValue(element);
					Double maxValue = DocumentUtility.getNumberValidatorMaxValue(element);

					if (minValue != null || maxValue != null) {
						component.addValidator(new NumberRangeValidator(message, minValue, maxValue));
					}
				}
			}
		});
	}

	private TextArea createTextArea(Element element, Map<String, String> properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		TextArea component = new TextArea();
		SlideComponentUtility.setTextAreaProperties(component, element, properties, alignmentWrapper);
		// addTextAreaValidators(component, element);

		return component;
	}

	private DateField createDateField(Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper, SlideContainerPresenter presenter) {
		DateField component = new DateField();
		SlideComponentUtility.setDateFieldProperties(component, element, properties, alignmentWrapper);
		addDateFieldValidators(component, element);

		return component;
	}

	private void addDateFieldValidators(DateField component, Element element) {
		iterateValidators(component, element, new ValidatorCallback() {
			@Override
			public void setComponentValidator(Validatable component, Element element, String name, String message) {
				if (EMPTY.equals(name)) {
					component.addValidator(new EmptyValidator(message));
				} else if (RANGE.equals(name)) {
					Date minValue = DocumentUtility.getDateValidatorMinValue(element, "yyyy-MM-dd");
					Date maxValue = DocumentUtility.getDateValidatorMaxValue(element, "yyyy-MM-dd");

					if (minValue != null || maxValue != null) {
						component.addValidator(new DateRangeValidator(message, minValue, maxValue));
					}
				}
			}
		});
	}

	private ComboBox createComboBox(Element element, Map<String, String> properties, AlignmentWrapper alignmentWrapper,
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
			for (Element item : items) {
				String value = DocumentUtility.getValue(item);
				if (isNotEmpty(value)) {
					component.addItem(value);

					String caption = DocumentUtility.getCaption(item);
					if (isNotEmpty(caption))
						component.setItemCaption(value, caption);
				}
			}
		}

		component.setImmediate(true);
	}

	private void addComboBoxValidators(ComboBox component, Element element) {
		iterateValidators(component, element, new ValidatorCallback() {
			@Override
			public void setComponentValidator(Validatable component, Element element, String name, String message) {
				if (EMPTY.equals(name)) {
					component.addValidator(new EmptyValidator(message));
				}
			}
		});
	}

	private TimerLabel createTimerLabel(Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper, SlideContainerPresenter presenter) {
		TimerLabel component = new TimerLabel();
		SlideComponentUtility.setTimerLabelProperties(component, element, properties, alignmentWrapper);

		Component buddy = presenter.getTimer(properties.get(TIMER_ID));
		if (buddy instanceof Timer) {
			component.setTimer((Timer) buddy);
		}

		return component;
	}

	private Label createLabel(Element element, Map<String, String> properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		Label component = new Label();
		SlideComponentUtility.setLabelProperties(component, element, properties, alignmentWrapper);

		return component;
	}

	private ClientSim createClientSim(Element element, Map<String, String> properties,
			AlignmentWrapper alignmentWrapper, SlideContainerPresenter presenter) {
		ClientSim component = new ClientSim();
		SlideComponentUtility.setClientSimProperties(component, element, properties, alignmentWrapper);
		addClientSimHandlers(component, element, presenter);

		return component;
	}

	private void addClientSimHandlers(ClientSim component, Element element, SlideContainerPresenter presenter) {
		DocumentUtility.iterateHandlers(component, element, presenter, new HandlerCallback() {
			@Override
			public void setComponentHandler(Component component, Element element, Element handler, String name,
					String actionId, final Action action, final SlidePresenter presenter) {
				final ClientSim clientSim = (ClientSim) component;

				if (CLIENT.equals(name)) {
					clientSim.addClientListener(new ClientSim.ClientListener() {
						@Override
						public void client(ClientEvent event) {
							presenter.handleEvent(clientSim, CLIENT_SIM, ClientSimEvent, action, DEFAULT);
						}
					});
				}
			}
		});
	}

	private ComponentWrapper createPluginComponent(Element element, SlidePresenter presenter) {
		String name = element.getShortName();
		String namespace = element.getNamespace();

		if (isNotEmpty(name) && isNotEmpty(namespace)) {
			// find registered plugin
			SlideComponentPlugin componentPlugin = PluginManager.get().getComponentPlugin(namespace, name);

			if (componentPlugin != null) {
				return componentPlugin.createComponentFromElement(element, presenter);
			}
		}

		return null;
	}

}

/**
 * 
 */
package com.tilioteo.hypothesis.builder.xml;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.vaadin.special.data.DateRangeValidator;
import org.vaadin.special.data.EmptyValidator;
import org.vaadin.special.data.IntegerValidator;
import org.vaadin.special.data.NumberRangeValidator;
import org.vaadin.special.data.NumberValidator;
import org.vaadin.special.data.SelectPanelEmptyValidator;
import org.vaadin.special.event.MouseEvents;
import org.vaadin.special.ui.Media;
import org.vaadin.special.ui.SelectButton;
import org.vaadin.special.ui.ShortcutKey;
import org.vaadin.special.ui.ShortcutKey.KeyPressEvent;
import org.vaadin.special.ui.ShortcutKey.KeyPressListener;

import com.tilioteo.common.Strings;
import com.tilioteo.common.collections.StringMap;
import com.tilioteo.hypothesis.builder.AlignmentWrapper;
import com.tilioteo.hypothesis.builder.BuilderConstants;
import com.tilioteo.hypothesis.builder.SlideContainerFactory;
import com.tilioteo.hypothesis.business.EventManager;
import com.tilioteo.hypothesis.business.EventManager.Callback;
import com.tilioteo.hypothesis.business.ShortcutUtility;
import com.tilioteo.hypothesis.business.ShortcutUtility.ShortcutKeys;
import com.tilioteo.hypothesis.evaluation.AbstractBaseAction;
import com.tilioteo.hypothesis.evaluation.IndexedExpression;
import com.tilioteo.hypothesis.event.interfaces.MessageEventListener;
import com.tilioteo.hypothesis.event.interfaces.ViewportEventListener;
import com.tilioteo.hypothesis.event.model.ComponentEvent;
import com.tilioteo.hypothesis.event.model.MessageEvent;
import com.tilioteo.hypothesis.event.model.ProcessEventTypes;
import com.tilioteo.hypothesis.event.model.ViewportEvent;
import com.tilioteo.hypothesis.extension.PluginManager;
import com.tilioteo.hypothesis.interfaces.ComponentWrapper;
import com.tilioteo.hypothesis.interfaces.Evaluator;
import com.tilioteo.hypothesis.interfaces.SlideComponentPlugin;
import com.tilioteo.hypothesis.presenter.SlideContainerPresenter;
import com.tilioteo.hypothesis.slide.ui.Audio;
import com.tilioteo.hypothesis.slide.ui.Button;
import com.tilioteo.hypothesis.slide.ui.ButtonPanel;
import com.tilioteo.hypothesis.slide.ui.ComboBox;
import com.tilioteo.hypothesis.slide.ui.DateField;
import com.tilioteo.hypothesis.slide.ui.HorizontalLayout;
import com.tilioteo.hypothesis.slide.ui.Image;
import com.tilioteo.hypothesis.slide.ui.Label;
import com.tilioteo.hypothesis.slide.ui.Panel;
import com.tilioteo.hypothesis.slide.ui.SelectPanel;
import com.tilioteo.hypothesis.slide.ui.TextArea;
import com.tilioteo.hypothesis.slide.ui.TextField;
import com.tilioteo.hypothesis.slide.ui.Timer;
import com.tilioteo.hypothesis.slide.ui.TimerLabel;
import com.tilioteo.hypothesis.slide.ui.VerticalLayout;
import com.tilioteo.hypothesis.slide.ui.Video;
import com.tilioteo.hypothesis.slide.ui.Window;
import com.tilioteo.hypothesis.ui.SlideContainer;
import com.tilioteo.hypothesis.utility.XmlUtility;
import com.vaadin.data.Validatable;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Layout;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
public class SlideContainerXmlFactory implements SlideContainerFactory {

	private static Logger log = Logger.getLogger(SlideContainerXmlFactory.class);

	@Override
	public SlideContainer buildSlideContainer(String template, String content) {

		Document templateDocument = null;
		Document contentDocument = null;

		try {
			templateDocument = XmlUtility.readString(template);
		} catch (Throwable e) {
		}

		try {
			contentDocument = XmlUtility.readString(content);
		} catch (Throwable e) {
		}

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
			Document document = XmlDocumentFactory.mergeSlideDocument(templateDocument, contentDocument);

			if (XmlDocumentUtility.isValidSlideXml(document)) {
				return buildSlideContainer(document);
			}
		} catch (Throwable t) {
			log.error("buildFromString()");
		}

		return null;
	}

	private SlideContainer buildSlideContainer(Document document) {
		SlideContainerPresenter presenter = new SlideContainerPresenter();
		SlideContainer slideContainer = new SlideContainer(presenter);

		Element rootElement = document.getRootElement();

		EvaluableXmlUtility.createActions(rootElement, presenter);
		createTimers(rootElement, presenter);

		createInputExpressions(rootElement, presenter);
		createOutputExpressions(rootElement, presenter);

		createWindows(rootElement, presenter);
		createViewport(rootElement, presenter);

		EvaluableXmlUtility.createVariables(rootElement, presenter, new ReferenceCallback() {
			@Override
			public Object getReference(String name, String id, Evaluator evaluator) {
				if (!Strings.isNullOrEmpty(id) && evaluator instanceof SlideContainerPresenter) {
					SlideContainerPresenter presenter = (SlideContainerPresenter) evaluator;

					if (BuilderConstants.COMPONENT.equals(name)) {
						return presenter.getComponent(id);
					} else if (BuilderConstants.TIMER.equals(name)) {
						return presenter.getTimer(id);
					} else if (BuilderConstants.WINDOW.equals(name)) {
						return presenter.getWindow(id);
					}
				}
				return null;
			}
		});

		return slideContainer;
	}

	private void createInputExpressions(Element rootElement, SlideContainerPresenter presenter) {
		List<Element> inputElements = XmlDocumentUtility.getInputValueElements(rootElement);
		for (Element inputElement : inputElements) {
			IndexedExpression indexedExpression = EvaluableXmlUtility.createValueExpression(inputElement,
					BuilderConstants.INPUT_VALUE);
			if (indexedExpression != null) {
				presenter.setInputExpression(indexedExpression.getIndex(), indexedExpression);
			}
		}
	}

	private void createOutputExpressions(Element rootElement, SlideContainerPresenter presenter) {
		List<Element> outputElements = XmlDocumentUtility.getOutputValueElements(rootElement);
		for (Element outputElement : outputElements) {
			IndexedExpression indexedExpression = EvaluableXmlUtility.createValueExpression(outputElement,
					BuilderConstants.OUTPUT_VALUE);
			if (indexedExpression != null) {
				presenter.setOutputExpression(indexedExpression.getIndex(), indexedExpression);
			}
		}
	}

	private void createTimers(Element rootElement, SlideContainerPresenter presenter) {
		List<Element> elements = XmlDocumentUtility.getTimersElements(rootElement);
		for (Element element : elements) {
			String id = XmlDocumentUtility.getId(element);
			if (!Strings.isNullOrEmpty(id)) {

				Timer timer = createTimer(element, presenter);
				presenter.setTimer(id, timer);
			}
		}
	}

	private Timer createTimer(Element element, SlideContainerPresenter presenter) {
		StringMap properties = XmlDocumentUtility.getPropertyValueMap(element);

		Timer component = new Timer();
		ComponentUtility.setTimerProperties(component, element, properties);
		addTimerHandlers(component, element, presenter);

		return component;
	}

	private void iterateHandlers(Component component, Element element, SlideContainerPresenter presenter,
			HandlerCallback callback) {
		List<Element> handlers = XmlDocumentUtility.getComponentHandlers(element);

		for (Element handler : handlers) {
			String name = handler.getName();
			String actionId = null;

			final AbstractBaseAction anonymousAction = EvaluableXmlUtility.createAnonymousAction(handler, presenter);
			if (anonymousAction != null) {
				actionId = anonymousAction.getId();
				presenter.setAction(actionId, anonymousAction);
			}

			if (!Strings.isNullOrEmpty(actionId) && !Strings.isNullOrEmpty(name)) {
				callback.setComponentHandler(component, element, name, actionId, anonymousAction, presenter);
			}
		}
	}

	private void addTimerHandlers(Timer component, Element element, SlideContainerPresenter presenter) {
		iterateHandlers(component, element, presenter, new HandlerCallback() {
			@Override
			public void setComponentHandler(Component component, Element element, String name, String actionId,
					final AbstractBaseAction action, final SlideContainerPresenter presenter) {
				final Timer timer = (Timer) component;

				if (BuilderConstants.START.equals(name)) {
					timer.addStartListener(new Timer.StartListener() {
						@Override
						public void start(final Timer.StartEvent event) {
							presenter.getEventManager().handleEvent(timer, BuilderConstants.TIMER,
									ProcessEventTypes.TimerStart, action, new Callback() {
								@Override
								public void initEvent(ComponentEvent componentEvent) {
									componentEvent.setProperty("time", event.getTime());
								}
							});
						}
					});
				} else if (BuilderConstants.STOP.equals(name)) {
					timer.addStopListener(new Timer.StopListener() {
						@Override
						public void stop(final Timer.StopEvent event) {
							presenter.getEventManager().handleEvent(timer, BuilderConstants.TIMER,
									ProcessEventTypes.TimerStop, action, new Callback() {
								@Override
								public void initEvent(ComponentEvent componentEvent) {
									componentEvent.setProperty("time", event.getTime());
								}
							});
						}
					});
				} else if (BuilderConstants.UPDATE.equals(name)) {
					Integer interval = Strings.toInteger((element.attributeValue(BuilderConstants.INTERVAL)));
					if (interval != null) {
						timer.addUpdateListener(interval, new Timer.UpdateListener() {
							@Override
							public void update(final Timer.UpdateEvent event) {
								presenter.getEventManager().handleEvent(timer, BuilderConstants.TIMER,
										ProcessEventTypes.TimerUpdate, action, new Callback() {
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
		List<Element> elements = XmlDocumentUtility.getWindowsElements(rootElement);
		for (Element windowElement : elements) {
			String id = XmlDocumentUtility.getId(windowElement);
			if (!Strings.isNullOrEmpty(id)) {
				Element element = XmlDocumentUtility.getViewportOrWindowRootElement(windowElement);

				Window window = createWindow(element, presenter);
				presenter.setWindow(id, window);
			}
		}
	}

	private Window createWindow(Element element, SlideContainerPresenter presenter) {
		StringMap properties = XmlDocumentUtility.getPropertyValueMap(element);

		Window component = new Window();
		ComponentUtility.setWindowProperties(component, element, properties, new AlignmentWrapper());
		addWindowComponents(component, element, presenter);
		addWindowHandlers(component, element, presenter);

		return component;
	}

	private void addWindowComponents(Window container, Element element, SlideContainerPresenter presenter) {
		List<Element> elements = XmlDocumentUtility.getContainerComponents(element,
				BuilderConstants.VALID_WINDOW_CHILDREN);
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

	private void addWindowHandlers(Window component, Element element, SlideContainerPresenter presenter) {
		iterateHandlers(component, element, presenter, new HandlerCallback() {
			@Override
			public void setComponentHandler(Component component, Element element, String name, String actionId,
					final AbstractBaseAction action, final SlideContainerPresenter presenter) {
				final Window window = (Window) component;

				if (BuilderConstants.INIT.equals(name)) {
					window.addInitListener(new Window.InitListener() {
						@Override
						public void initWindow(Window.InitEvent event) {
							presenter.getEventManager().handleEvent(window, BuilderConstants.WINDOW,
									ProcessEventTypes.WindowInit, action, EventManager.DEFAULT_CALLBACK);
						}
					});
				} else if (BuilderConstants.OPEN.equals(name)) {
					window.addOpenListener(new Window.OpenListener() {
						@Override
						public void openWindow(Window.OpenEvent event) {
							presenter.getEventManager().handleEvent(window, BuilderConstants.WINDOW,
									ProcessEventTypes.WindowOpen, action, EventManager.DEFAULT_CALLBACK);
						}
					});
				} else if (BuilderConstants.CLOSE.equals(name)) {
					window.addCloseListener(new Window.CloseListener() {
						@Override
						public void windowClose(Window.CloseEvent event) {
							presenter.getEventManager().handleEvent(window, BuilderConstants.WINDOW,
									ProcessEventTypes.WindowClose, action, EventManager.DEFAULT_CALLBACK);
						}
					});
				}
			}
		});
	}

	private void createViewport(Element rootElement, SlideContainerPresenter presenter) {
		Element componentElement = XmlDocumentUtility.getViewportInnerComponent(rootElement);
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
		iterateHandlers(component, element, presenter, new HandlerCallback() {
			@Override
			public void setComponentHandler(Component component, Element element, String name, String actionId,
					final AbstractBaseAction action, final SlideContainerPresenter presenter) {
				final SlideContainer container = (SlideContainer) component;

				if (BuilderConstants.INIT.equals(name)) {
					presenter.addViewportInitListener(new ViewportEventListener() {
						@Override
						public void handleEvent(ViewportEvent event) {
							presenter.getEventManager().handleEvent(container, BuilderConstants.SLIDE,
									ProcessEventTypes.SlideInit, action, EventManager.DEFAULT_CALLBACK);
						}
					});
				} else if (BuilderConstants.SHOW.equals(name)) {
					presenter.addViewportShowListener(new ViewportEventListener() {
						@Override
						public void handleEvent(ViewportEvent event) {
							presenter.getEventManager().handleEvent(container, BuilderConstants.SLIDE,
									ProcessEventTypes.SlideShow, action, EventManager.DEFAULT_CALLBACK);
						}
					});
				} else if (BuilderConstants.SHORTCUT.equals(name)) {
					String key = XmlDocumentUtility.getKey(element);
					ShortcutKeys shortcutKeys = ShortcutUtility.parseShortcut(key);
					if (shortcutKeys != null) {
						ShortcutKey shortcutKey = new ShortcutKey(shortcutKeys.getKeyCode(),
								shortcutKeys.getModifiers());
						final String shortcut = shortcutKey.toString();
						shortcutKey.addKeyPressListener(new KeyPressListener() {
							@Override
							public void keyPress(final KeyPressEvent event) {
								presenter.getEventManager().handleEvent(container, BuilderConstants.SLIDE,
										ProcessEventTypes.ShortcutKey, action, new Callback() {
									@Override
									public void initEvent(ComponentEvent componentEvent) {
										componentEvent.setProperty("shortcutKey", shortcut, "shortcut@key");
									}
								});
							}
						});

						presenter.addShortcutKey(shortcutKey);
					}
				} else if (BuilderConstants.MESSAGE.equals(name)) {
					String uid = XmlDocumentUtility.getUid(element);
					if (!Strings.isNullOrEmpty(uid)) {
						presenter.addMessageListener(uid, new MessageEventListener() {
							@Override
							public void handleEvent(final MessageEvent event) {
								presenter.getEventManager().handleEvent(container, BuilderConstants.SLIDE,
										ProcessEventTypes.Message, action, new Callback() {
									@Override
									public void initEvent(ComponentEvent componentEvent) {
										componentEvent.setProperty("message", event.getMessage(), "");
										componentEvent.setProperty("messageUID", event.getMessage(), "message@UID");
									}
								});
							}
						});
					}
				}
			}
		});
	}

	private ComponentWrapper createComponentFromElement(Element element, SlideContainerPresenter presenter) {
		if (element != null) {
			String id = XmlDocumentUtility.getId(element);

			if (!Strings.isNullOrEmpty(id)) {
				String name = element.getName();

				Component component = null;

				StringMap properties = XmlDocumentUtility.getPropertyValueMap(element);
				AlignmentWrapper alignmentWrapper = new AlignmentWrapper();

				if (name.equals(BuilderConstants.VERTICAL_LAYOUT))
					component = createVerticalLayout(element, properties, alignmentWrapper, presenter);
				else if (name.equals(BuilderConstants.HORIZONTAL_LAYOUT))
					component = createHorizontalLayout(element, properties, alignmentWrapper, presenter);
				else if (name.equals(BuilderConstants.FORM_LAYOUT))
					component = createFormLayout(element, properties, alignmentWrapper, presenter);

				else if (name.equals(BuilderConstants.PANEL))
					component = createPanel(element, properties, alignmentWrapper, presenter);
				else if (name.equals(BuilderConstants.IMAGE))
					component = createImage(element, properties, alignmentWrapper, presenter);
				else if (name.equals(BuilderConstants.VIDEO))
					component = createVideo(element, properties, alignmentWrapper, presenter);
				else if (name.equals(BuilderConstants.AUDIO))
					component = createAudio(element, properties, alignmentWrapper, presenter);
				else if (name.equals(BuilderConstants.BUTTON))
					component = createButton(element, properties, alignmentWrapper, presenter);
				else if (name.equals(BuilderConstants.BUTTON_PANEL))
					component = createButtonPanel(element, properties, alignmentWrapper, presenter);

				else if (name.equals(BuilderConstants.SELECT_PANEL))
					component = createSelectPanel(element, properties, alignmentWrapper, presenter);
				else if (name.equals(BuilderConstants.TEXT_FIELD))
					component = createTextField(element, properties, alignmentWrapper, presenter);
				else if (name.equals(BuilderConstants.TEXT_AREA))
					component = createTextArea(element, properties, alignmentWrapper, presenter);
				else if (name.equals(BuilderConstants.DATE_FIELD))
					component = createDateField(element, properties, alignmentWrapper, presenter);
				else if (name.equals(BuilderConstants.COMBOBOX))
					component = createComboBox(element, properties, alignmentWrapper, presenter);
				else if (name.equals(BuilderConstants.TIMER_LABEL))
					component = createTimerLabel(element, properties, alignmentWrapper, presenter);
				else if (name.equals(BuilderConstants.LABEL))
					component = createLabel(element, properties, alignmentWrapper, presenter);

				if (component != null) {
					presenter.setComponent(id, component);
					return new ComponentWrapper(component, alignmentWrapper.getAlignment());
				} else {
					return createPluginComponent(element, presenter);
				}
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

	private void addVerticalLayoutComponents(VerticalLayout container, Element element,
			SlideContainerPresenter presenter) {
		List<Element> elements = XmlDocumentUtility.getContainerComponents(element,
				BuilderConstants.VALID_CONTAINER_CHILDREN);
		for (Element childElement : elements) {
			ComponentWrapper componentWrapper = createComponentFromElement(childElement, presenter);
			if (componentWrapper != null) {
				Component component = componentWrapper.getComponent();

				container.addComponent(component);
				container.setComponentAlignment(component, componentWrapper.getAlignment());

				float ratio = 1.0f;
				if (component.getHeightUnits() == Unit.PERCENTAGE) {
					ratio = component.getHeight() / 100;
					component.setHeight("100%");
				}

				container.setExpandRatio(component, ratio);
			}
		}
	}

	private HorizontalLayout createHorizontalLayout(Element element, StringMap properties,
			AlignmentWrapper alignmentWrapper, SlideContainerPresenter presenter) {
		HorizontalLayout component = new HorizontalLayout();
		ComponentUtility.setCommonLayoutProperties(component, element, properties, alignmentWrapper);
		addHorizontalLayoutComponents(component, element, presenter);

		return component;
	}

	private void addHorizontalLayoutComponents(HorizontalLayout container, Element element,
			SlideContainerPresenter presenter) {
		List<Element> elements = XmlDocumentUtility.getContainerComponents(element,
				BuilderConstants.VALID_CONTAINER_CHILDREN);
		for (Element childElement : elements) {
			ComponentWrapper componentWrapper = createComponentFromElement(childElement, presenter);
			if (componentWrapper != null) {
				Component component = componentWrapper.getComponent();

				container.addComponent(component);
				container.setComponentAlignment(component, componentWrapper.getAlignment());

				float ratio = 1.0f;
				if (component.getWidthUnits() == Unit.PERCENTAGE) {
					ratio = component.getWidth() / 100;
					component.setWidth("100%");
				}

				container.setExpandRatio(component, ratio);
			}
		}
	}

	private FormLayout createFormLayout(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		FormLayout component = new FormLayout();
		ComponentUtility.setCommonLayoutProperties(component, element, properties, alignmentWrapper);
		addFormLayoutComponents(component, element, presenter);

		return component;
	}

	private void addFormLayoutComponents(FormLayout container, Element element, SlideContainerPresenter presenter) {
		List<Element> elements = XmlDocumentUtility.getContainerComponents(element,
				BuilderConstants.VALID_CONTAINER_CHILDREN);
		for (Element childElement : elements) {
			ComponentWrapper componentWrapper = createComponentFromElement(childElement, presenter);
			if (componentWrapper != null) {
				Component component = componentWrapper.getComponent();
				container.addComponent(component);
				container.setComponentAlignment(component, componentWrapper.getAlignment());
			}
		}
	}

	private Panel createPanel(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		Panel component = new Panel();
		ComponentUtility.setPanelProperties(component, element, properties, alignmentWrapper);
		addPanelComponents(component, element, presenter);

		return component;
	}

	private void addPanelComponents(Panel container, Element element, SlideContainerPresenter presenter) {
		List<Element> elements = XmlDocumentUtility.getContainerComponents(element,
				BuilderConstants.VALID_CONTAINER_CHILDREN);
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

	private Image createImage(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		Image component = new Image();
		ComponentUtility.setImageProperties(component, element, properties, alignmentWrapper);
		addImageHandlers(component, element, presenter);

		return component;
	}

	private void addImageHandlers(Image component, Element element, SlideContainerPresenter presenter) {
		iterateHandlers(component, element, presenter, new HandlerCallback() {
			@Override
			public void setComponentHandler(Component component, Element element, String name, String actionId,
					final AbstractBaseAction action, final SlideContainerPresenter presenter) {
				final Image image = (Image) component;

				if (BuilderConstants.CLICK.equals(name)) {
					image.addClickListener(new MouseEvents.ClickListener() {
						@Override
						public void click(final MouseEvents.ClickEvent event) {
							presenter.getEventManager().handleEvent(image, BuilderConstants.IMAGE,
									ProcessEventTypes.ImageClick, action, new Callback() {
								@Override
								public void initEvent(ComponentEvent componentEvent) {
									componentEvent.setProperty("x", event.getRelativeX());
									componentEvent.setProperty("y", event.getRelativeY());
								}
							});
						}
					});
				} else if (BuilderConstants.LOAD.equals(name)) {
					image.addLoadListener(new Image.LoadListener() {
						@Override
						public void load(Image.LoadEvent event) {
							presenter.getEventManager().handleEvent(image, BuilderConstants.IMAGE,
									ProcessEventTypes.ImageLoad, action, EventManager.DEFAULT_CALLBACK);
						}
					});
				} else if (BuilderConstants.ERROR.equals(name)) {
					image.addErrorListener(new Image.ErrorListener() {
						@Override
						public void error(Image.ErrorEvent event) {
							presenter.getEventManager().handleEvent(image, BuilderConstants.IMAGE,
									ProcessEventTypes.ImageError, action, EventManager.DEFAULT_CALLBACK);
						}
					});
				}
			}
		});
	}

	private Video createVideo(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		Video component = new Video();
		ComponentUtility.setVideoProperties(component, element, properties, alignmentWrapper);
		addVideoHandlers(component, element, presenter);

		return component;
	}

	private void addVideoHandlers(Video component, Element element, SlideContainerPresenter presenter) {
		iterateHandlers(component, element, presenter, new HandlerCallback() {
			@Override
			public void setComponentHandler(Component component, Element element, String name, String actionId,
					final AbstractBaseAction action, final SlideContainerPresenter presenter) {
				final Video video = (Video) component;

				if (BuilderConstants.CLICK.equals(name)) {
					video.addClickListener(new Video.ClickListener() {
						@Override
						public void click(final Video.ClickEvent event) {
							presenter.getEventManager().handleEvent(video, BuilderConstants.VIDEO,
									ProcessEventTypes.VideoClick, action, new Callback() {
								@Override
								public void initEvent(ComponentEvent componentEvent) {
									componentEvent.setProperty("x", event.getRelativeX());
									componentEvent.setProperty("y", event.getRelativeY());
								}
							});
						}
					});
				} else if (BuilderConstants.LOAD.equals(name)) {
					video.addCanPlayThroughListener(new Media.CanPlayThroughListener() {
						@Override
						public void canPlayThrough(Media.CanPlayThroughEvent event) {
							presenter.getEventManager().handleEvent(video, BuilderConstants.VIDEO,
									ProcessEventTypes.VideoLoad, action, EventManager.DEFAULT_CALLBACK);
						}
					});
				} else if (BuilderConstants.START.equals(name)) {
					video.addStartListener(new Media.StartListener() {
						@Override
						public void start(final Media.StartEvent event) {
							presenter.getEventManager().handleEvent(video, BuilderConstants.VIDEO,
									ProcessEventTypes.VideoStart, action, new Callback() {
								@Override
								public void initEvent(ComponentEvent componentEvent) {
									componentEvent.setProperty("time", event.getMediaTime());
								}
							});
						}
					});
				} else if (BuilderConstants.STOP.equals(name)) {
					video.addStopListener(new Media.StopListener() {
						@Override
						public void stop(final Media.StopEvent event) {
							presenter.getEventManager().handleEvent(video, BuilderConstants.VIDEO,
									ProcessEventTypes.VideoStop, action, new Callback() {
								@Override
								public void initEvent(ComponentEvent componentEvent) {
									componentEvent.setProperty("time", event.getMediaTime());
								}
							});
						}
					});
				}
			}
		});
	}

	private Audio createAudio(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		Audio component = new Audio();
		ComponentUtility.setAudioProperties(component, element, properties, alignmentWrapper);
		addAudioHandlers(component, element, presenter);

		return component;
	}

	private void addAudioHandlers(Audio component, Element element, SlideContainerPresenter presenter) {
		iterateHandlers(component, element, presenter, new HandlerCallback() {
			@Override
			public void setComponentHandler(Component component, Element element, String name, String actionId,
					final AbstractBaseAction action, final SlideContainerPresenter presenter) {
				final Audio audio = (Audio) component;

				if (BuilderConstants.LOAD.equals(name)) {
					audio.addCanPlayThroughListener(new Media.CanPlayThroughListener() {
						@Override
						public void canPlayThrough(Media.CanPlayThroughEvent event) {
							presenter.getEventManager().handleEvent(audio, BuilderConstants.AUDIO,
									ProcessEventTypes.AudioLoad, action, EventManager.DEFAULT_CALLBACK);
						}
					});
				} else if (BuilderConstants.START.equals(name)) {
					audio.addStartListener(new Media.StartListener() {
						@Override
						public void start(final Media.StartEvent event) {
							presenter.getEventManager().handleEvent(audio, BuilderConstants.AUDIO,
									ProcessEventTypes.AudioStart, action, new Callback() {
								@Override
								public void initEvent(ComponentEvent componentEvent) {
									componentEvent.setProperty("time", event.getMediaTime());
								}
							});
						}
					});
				} else if (BuilderConstants.STOP.equals(name)) {
					audio.addStopListener(new Media.StopListener() {
						@Override
						public void stop(final Media.StopEvent event) {
							presenter.getEventManager().handleEvent(audio, BuilderConstants.AUDIO,
									ProcessEventTypes.AudioStop, action, new Callback() {
								@Override
								public void initEvent(ComponentEvent componentEvent) {
									componentEvent.setProperty("time", event.getMediaTime());
								}
							});
						}
					});
				}
			}
		});
	}

	private Button createButton(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		Button component = new Button();
		ComponentUtility.setButtonProperties(component, element, properties, alignmentWrapper);
		addButtonHandlers(component, element, presenter);

		return component;
	}

	private void addButtonHandlers(Button component, Element element, SlideContainerPresenter presenter) {
		iterateHandlers(component, element, presenter, new HandlerCallback() {
			@Override
			public void setComponentHandler(Component component, Element element, String name, String actionId,
					final AbstractBaseAction action, final SlideContainerPresenter presenter) {
				final Button button = (Button) component;

				if (BuilderConstants.CLICK.equals(name)) {
					button.addClickListener(new Button.ClickListener() {
						@Override
						public void buttonClick(Button.ClickEvent event) {
							presenter.getEventManager().handleEvent(button, BuilderConstants.BUTTON,
									ProcessEventTypes.ButtonClick, action, EventManager.DEFAULT_CALLBACK);
						}
					});
				}
			}
		});
	}

	private ButtonPanel createButtonPanel(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		ButtonPanel component = new ButtonPanel();
		ComponentUtility.setButtonPanelProperties(component, element, properties, alignmentWrapper);
		addButtonPanelHandlers(component, element, presenter);

		return component;
	}

	private void addButtonPanelHandlers(ButtonPanel component, Element element, SlideContainerPresenter presenter) {
		iterateHandlers(component, element, presenter, new HandlerCallback() {
			@Override
			public void setComponentHandler(Component component, Element element, String name, String actionId,
					final AbstractBaseAction action, final SlideContainerPresenter presenter) {
				final ButtonPanel buttonPanel = (ButtonPanel) component;

				if (BuilderConstants.CLICK.equals(name)) {
					buttonPanel.addButtonClickListener(new Button.ClickListener() {
						@Override
						public void buttonClick(final Button.ClickEvent event) {
							presenter.getEventManager().handleEvent(buttonPanel, BuilderConstants.BUTTON_PANEL,
									ProcessEventTypes.ButtonPanelClick, action, new Callback() {
								@Override
								public void initEvent(ComponentEvent componentEvent) {
									componentEvent.setProperty("button", event.getButton(), "");
									componentEvent.setProperty("selectedCaption", event.getButton().getCaption(),
											"selected");
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

	private SelectPanel createSelectPanel(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		SelectPanel component = new SelectPanel();
		ComponentUtility.setSelectPanelProperties(component, element, properties, alignmentWrapper);
		addSelectPanelHandlers(component, element, presenter);
		addSelectPanelValidators(component, element);

		return component;
	}

	private void addSelectPanelHandlers(final SelectPanel component, Element element,
			final SlideContainerPresenter presenter) {
		iterateHandlers(component, element, presenter, new HandlerCallback() {
			@Override
			public void setComponentHandler(Component component, Element element, String name, String actionId,
					final AbstractBaseAction action, final SlideContainerPresenter presenter) {
				final SelectPanel selectPanel = (SelectPanel) component;

				if (BuilderConstants.CLICK.equals(name)) {
					selectPanel.addButtonClickListener(new SelectButton.ClickListener() {
						@Override
						public void buttonClick(final SelectButton.ClickEvent event) {
							presenter.getEventManager().handleEvent(selectPanel, BuilderConstants.SELECT_PANEL,
									ProcessEventTypes.SelectPanelClick, action, new Callback() {
								@Override
								public void initEvent(ComponentEvent componentEvent) {
									componentEvent.setProperty("button", event.getSource(), "");
									componentEvent.setProperty("selectedCaption", event.getSelectButton().getCaption(),
											"selected");
									componentEvent.setProperty("selectedIndex",
											selectPanel.getChildIndex(event.getSelectButton()) + 1, "selected@index");
									componentEvent.setProperty("selectedValue",
											event.getSelectButton().getValue() ? "true" : "false", "selected@value");
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
					presenter.getEventManager().handleEvent(component, BuilderConstants.SELECT_PANEL,
							ProcessEventTypes.SelectPanelClick, null, new Callback() {
						@Override
						public void initEvent(ComponentEvent componentEvent) {
							componentEvent.setProperty("button", event.getSource());
							componentEvent.setProperty("selectedIndex",
									component.getChildIndex(event.getSelectButton()) + 1, "selected/index");
						}
					});
				}
			});
		}
	}

	private void iterateValidators(Component component, Element element, ValidatorCallback callback) {
		if (component instanceof Validatable) {
			List<Element> validators = XmlDocumentUtility.getComponentValidators(element);

			for (Element validatorElement : validators) {
				String name = validatorElement.getName();
				String message = XmlDocumentUtility.getValidatorMessage(validatorElement, "");

				callback.setComponentValidator((Validatable) component, element, name, message);
			}

			if (!validators.isEmpty() && component instanceof AbstractComponent) {
				((AbstractComponent) component).setImmediate(true);
			}
		}
	}

	private void addSelectPanelValidators(SelectPanel component, Element element) {
		iterateValidators(component, element, new ValidatorCallback() {
			@Override
			public void setComponentValidator(Validatable component, Element element, String name, String message) {
				if (BuilderConstants.EMPTY.equals(name)) {
					component.addValidator(new SelectPanelEmptyValidator(message));
				}
			}
		});
	}

	private TextField createTextField(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		TextField component = new TextField();
		ComponentUtility.setTextFieldProperties(component, element, properties, alignmentWrapper);
		addTextFieldValidators(component, element);

		return component;
	}

	private void addTextFieldValidators(TextField component, Element element) {
		iterateValidators(component, element, new ValidatorCallback() {
			@Override
			public void setComponentValidator(Validatable component, Element element, String name, String message) {
				if (BuilderConstants.EMPTY.equals(name)) {
					component.addValidator(new EmptyValidator(message));
				} else if (BuilderConstants.INTEGER.equals(name)) {
					component.addValidator(new IntegerValidator(message));
				} else if (BuilderConstants.NUMBER.equals(name)) {
					component.addValidator(new NumberValidator(message));
				} else if (BuilderConstants.RANGE.equals(name)) {
					Double minValue = XmlDocumentUtility.getNumberValidatorMinValue(element);
					Double maxValue = XmlDocumentUtility.getNumberValidatorMaxValue(element);

					if (minValue != null || maxValue != null) {
						component.addValidator(new NumberRangeValidator(message, minValue, maxValue));
					}
				}
			}
		});
	}

	private TextArea createTextArea(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		TextArea component = new TextArea();
		ComponentUtility.setTextAreaProperties(component, element, properties, alignmentWrapper);
		// addTextAreaValidators(component, element);

		return component;
	}

	private DateField createDateField(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		DateField component = new DateField();
		ComponentUtility.setDateFieldProperties(component, element, properties, alignmentWrapper);
		addDateFieldValidators(component, element);

		return component;
	}

	private void addDateFieldValidators(DateField component, Element element) {
		iterateValidators(component, element, new ValidatorCallback() {
			@Override
			public void setComponentValidator(Validatable component, Element element, String name, String message) {
				if (BuilderConstants.EMPTY.equals(name)) {
					component.addValidator(new EmptyValidator(message));
				} else if (BuilderConstants.RANGE.equals(name)) {
					Date minValue = XmlDocumentUtility.getDateValidatorMinValue(element, "yyyy-MM-dd");
					Date maxValue = XmlDocumentUtility.getDateValidatorMaxValue(element, "yyyy-MM-dd");

					if (minValue != null || maxValue != null) {
						component.addValidator(new DateRangeValidator(message, minValue, maxValue));
					}
				}
			}
		});
	}

	private ComboBox createComboBox(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		ComboBox component = new ComboBox();
		ComponentUtility.setComboBoxProperties(component, element, properties, alignmentWrapper);
		addComboBoxItems(component, element);
		addComboBoxValidators(component, element);

		return component;
	}

	private void addComboBoxItems(ComboBox component, Element element) {
		List<Element> items = XmlDocumentUtility.getComponentItems(element);
		for (Element item : items) {
			String value = XmlDocumentUtility.getValue(item);
			if (!Strings.isNullOrEmpty(value)) {
				component.addItem(value);

				String caption = XmlDocumentUtility.getCaption(item);
				if (!Strings.isNullOrEmpty(caption))
					component.setItemCaption(value, caption);
			}
		}

		component.setImmediate(true);
	}

	private void addComboBoxValidators(ComboBox component, Element element) {
		iterateValidators(component, element, new ValidatorCallback() {
			@Override
			public void setComponentValidator(Validatable component, Element element, String name, String message) {
				if (BuilderConstants.EMPTY.equals(name)) {
					component.addValidator(new EmptyValidator(message));
				}
			}
		});
	}

	private TimerLabel createTimerLabel(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		TimerLabel component = new TimerLabel();
		ComponentUtility.setTimerLabelProperties(component, element, properties, alignmentWrapper);

		Component buddy = presenter.getTimer(properties.get(BuilderConstants.TIMER_ID));
		if (buddy instanceof Timer) {
			component.setTimer((Timer) buddy);
		}

		return component;
	}

	private Label createLabel(Element element, StringMap properties, AlignmentWrapper alignmentWrapper,
			SlideContainerPresenter presenter) {
		Label component = new Label();
		ComponentUtility.setLabelProperties(component, element, properties, alignmentWrapper);

		return component;
	}

	private ComponentWrapper createPluginComponent(Element element, SlideContainerPresenter presenter) {
		String namespace = element.getNamespacePrefix();
		if (namespace != null && !"".equals(namespace.trim())) {
			// find registered plugin
			SlideComponentPlugin componentPlugin = PluginManager.get().getComponentPlugin(namespace, element.getName());

			if (componentPlugin != null) {
				return componentPlugin.createComponentFromElement(element, presenter);
			}
		}

		return null;
	}

}

/**
 * 
 */
package org.vaadin.jre.ui;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;

import org.vaadin.jre.shared.ui.installlink.InstallLinkServerRpc;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.JavaScriptFunction;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.util.ReflectTools;

import elemental.json.JsonArray;

/**
 * @author kamil
 *
 */
@SuppressWarnings("serial")
@JavaScript({ "deployJava.js" })
public class DeployJava extends AbstractExtension {
	
	public static final String JAVA_INSTALL_URL = "http://java.com/dt-redirect";
	public static final int POLLING_INTERVAL = 10000;
	public static final int NUMBER_OF_CHECKS = 30;
	
	private static HashMap<UI, DeployJava> map = new HashMap<UI, DeployJava>();
	
	private UI ui;
	
	private JavaScriptFunction checkVersion = new JavaScriptFunction() {
		@Override
		public void call(JsonArray arguments) {
			try {
				boolean result = arguments.getBoolean(0);
				fireEvent(new JavaCheckedEvent(ui, result));
			} catch (Exception e) {
				fireEvent(new JavaCheckedEvent(ui, null));
			}
			
			getUI().setPollInterval(-1);
		}
	};

	com.vaadin.ui.JavaScript javaScript;
	
	protected DeployJava(UI ui) {
		super.extend(ui);
		this.ui = ui;
		
		javaScript = com.vaadin.ui.JavaScript.getCurrent();
		String url = getContextURL((VaadinServletRequest)VaadinService.getCurrentRequest()) + "/APP/PUBLISHED/deployJava.js";
		String head = "<script type='text/javascript' src='" + url + "'></script>";
		javaScript.execute("var i=document.createElement(\"iframe\");i.id=\"$ifrm\";i.style=\"position:absolute;width:0px;height:0px;border:medium none;left:-1000px;top:-1000px;\";i.tabIndex=\"-1\";i.src='javascript:\"\"';document.body.appendChild(i);i.contentWindow.document.open('text/html','replace');i.contentWindow.document.write(\"<html><head>"+head+"</head><body></body></html>\");i.contentWindow.document.close();");
		
		javaScript.addFunction("cth_cjv", checkVersion);
	}
	
	public void checkJavaVersion(String pattern) {
		javaScript.execute(String.format("var c=0;var l;var i=document.getElementById(\"$ifrm\");if(i&&typeof i.contentWindow.deployJava!=\"undefined\"){cth_cjv(i.contentWindow.deployJava.versionCheck(\"%s\"))}else{l=setInterval(function(){if(typeof i.contentWindow.deployJava!=\"undefined\"){clearInterval(l);cth_cjv(i.contentWindow.deployJava.versionCheck(\"%s\"))}else if(++c>20){clearInterval(l);cth_cjv(null)}},250)};", pattern, pattern));
	}
	
	public void launchJavaWebStart(String jnlp) {
		javaScript.execute(String.format("var i=document.getElementById(\"$ifrm\");if(i&&typeof i.contentWindow.deployJava!=\"undefined\")i.contentWindow.deployJava.launchWebStartApplication(\"%s\");", jnlp));
	}
	
	private String getContextURL(HttpServletRequest request) {
		String scheme = request.getScheme(); // http
		String serverName = request.getServerName(); // hostname.com
		int serverPort = request.getServerPort(); // 80
		String contextPath = request.getContextPath(); // /mywebapp

		// Reconstruct original requesting URL
		StringBuffer url = new StringBuffer();
		url.append(scheme).append("://").append(serverName);

		if ((serverPort != 80) && (serverPort != 443)) {
			url.append(":").append(serverPort);
		}

		url.append(contextPath);

		return url.toString();
	}

	public static class JavaCheckedEvent extends Component.Event {
		
		public static final String EVENT_ID = "javaChecked";

		private Boolean result;

		public JavaCheckedEvent(Component source, Boolean result) {
			super(source);
			
			this.result = result;
		}
		
		public Boolean getResult() {
			return result;
		}
	}
	
	public interface JavaCheckedListener extends Serializable {
		
		public static final Method JAVA_CHECKED_METHOD = ReflectTools
				.findMethod(JavaCheckedListener.class, JavaCheckedEvent.EVENT_ID, JavaCheckedEvent.class);

		public void javaChecked(JavaCheckedEvent event);
	}

	public static class JavaWindowClosedEvent extends Component.Event {
		
		public static final String EVENT_ID = "javaWindowClosed";

		public JavaWindowClosedEvent(Component source) {
			super(source);
		}
	}
	
	public interface JavaWindowClosedListener extends Serializable {
		
		public static final Method JAVA_WINDOW_CLOSED_METHOD = ReflectTools
				.findMethod(JavaWindowClosedListener.class, JavaWindowClosedEvent.EVENT_ID, JavaWindowClosedEvent.class);

		public void javaWindowClosed(JavaWindowClosedEvent event);
	}

	public void addJavaCheckedListener(JavaCheckedListener listener) {
		addListener(JavaCheckedEvent.EVENT_ID, JavaCheckedEvent.class, listener,
				JavaCheckedListener.JAVA_CHECKED_METHOD);
	}

	public void removeJavaCheckedListener(JavaCheckedListener listener) {
		removeListener(JavaCheckedEvent.EVENT_ID, JavaCheckedEvent.class, listener);
	}
	
	public void addJavaWindowClosedListener(JavaWindowClosedListener listener) {
		addListener(JavaWindowClosedEvent.EVENT_ID, JavaWindowClosedEvent.class, listener,
				JavaWindowClosedListener.JAVA_WINDOW_CLOSED_METHOD);
	}

	public void removeJavaWindowClosedListener(JavaWindowClosedListener listener) {
		removeListener(JavaWindowClosedEvent.EVENT_ID, JavaWindowClosedEvent.class, listener);
	}
	
	public static class InstallLink extends Link {
		
		private InstallLinkServerRpc rpc = new InstallLinkServerRpc() {
			@Override
			public void windowClosed() {
				DeployJava.get(getUI()).fireEvent(new JavaWindowClosedEvent(InstallLink.this));
			}
		};
		
		public InstallLink(String caption) {
			super(caption, new ExternalResource(JAVA_INSTALL_URL), "java_window", -1, -1, BorderStyle.NONE);
			
			registerRpc(rpc);
		}
		
	}
	
	public static class JavaInfoPanel extends Panel {
		
		private String checkInfoText = "Checking Java...";
		private String installLinkText = "Click here to get latest Java JRE.";
		private String javaNotInstalledText = "Java not installed!";
		private String javaInstalledText = "Java installed.";
		
		private boolean javaInstallWindowClosed = false;
		private int checkCount = 0;
		private Label label = new Label();
		private InstallLink link = new InstallLink(installLinkText);
		private transient Timer timer = new Timer();
		
		private DeployJava deployJava;
		private String javaVersion;
		
		private boolean javaOk = false;
		
		private JavaCheckedListener checkedListener = new JavaCheckedListener() {
			@Override
			public void javaChecked(JavaCheckedEvent event) {
				if (!event.getResult()) {
					javaOk = false;
					
					removeStyleName("info-java");
					addStyleName("info-nojava");
					
					label.setValue(javaNotInstalledText);
					link.setVisible(true);
					
					if (javaInstallWindowClosed) {
						if (checkCount < NUMBER_OF_CHECKS) {
							
							timer.schedule(new TimerTask() {
								@Override
								public void run() {
									getUI().access(new Runnable() {
										@Override
										public void run() {
											deployJava.checkJavaVersion(javaVersion);
										}
									});
								}
							}, POLLING_INTERVAL);
							
							if (0 == checkCount) {
								getUI().setPollInterval(POLLING_INTERVAL);
							}
							++checkCount;
						} else {
							javaInstallWindowClosed = false;
							checkCount = 0;
							getUI().setPollInterval(-1);
						}
					}
				} else {
					javaOk = true;
					
					removeStyleName("info-nojava");
					addStyleName("info-java");

					label.setValue(javaInstalledText);;
					link.setVisible(false);
					
					javaInstallWindowClosed = false;
					checkCount = 0;
					getUI().setPollInterval(-1);
				}
			}
			
		};
		
		@Override
		public void detach() {
			getUI().setPollInterval(-1);
			timer.cancel();
			
			super.detach();
		}
		
		private JavaWindowClosedListener closedListener = new JavaWindowClosedListener() {
			@Override
			public void javaWindowClosed(JavaWindowClosedEvent event) {
				javaInstallWindowClosed = true;
				checkCount = 0;
				deployJava.checkJavaVersion(javaVersion);
			}
		};
		
		public JavaInfoPanel(String javaVersion) {
			this.javaVersion = javaVersion;
			
			VerticalLayout content = new VerticalLayout();
			setContent(content);
			content.addComponent(label);
			label.setWidthUndefined();
			content.addComponent(link);
			link.setWidthUndefined();
			content.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
			content.setComponentAlignment(link, Alignment.MIDDLE_CENTER);
			link.setVisible(false);
			
			addAttachListener(new AttachListener() {
				@Override
				public void attach(AttachEvent event) {
					deployJava = DeployJava.get(getUI());
					
					deployJava.addJavaCheckedListener(checkedListener);
					deployJava.addJavaWindowClosedListener(closedListener);

					//label.setValue(checkInfoText);
					//deployJava.checkJavaVersion(JavaInfoPanel.this.javaVersion);
				}
			});
			
			addDetachListener(new DetachListener() {
				@Override
				public void detach(DetachEvent event) {
					deployJava.removeJavaCheckedListener(checkedListener);
					deployJava.removeJavaWindowClosedListener(closedListener);
				}
			});
		}
		
		public String getCheckInfoText() {
			return checkInfoText;
		}

		public void setCheckInfoText(String text) {
			if (label.getValue().equals(this.checkInfoText)) {
				label.setValue(text);
			}
			this.checkInfoText = text;
		}

		public String getInstallLinkText() {
			return installLinkText;
		}

		public void setInstallLinkText(String text) {
			this.installLinkText = text;
			link.setCaption(text);
		}

		public String getJavaNotInstalledText() {
			return javaNotInstalledText;
		}

		public void setJavaNotInstalledText(String text) {
			if (label.getValue().equals(this.javaNotInstalledText)) {
				label.setValue(text);
			}
			this.javaNotInstalledText = text;
		}

		public String getJavaInstalledText() {
			return javaInstalledText;
		}

		public void setJavaInstalledText(String text) {
			if (label.getValue().equals(this.javaInstalledText)) {
				label.setValue(text);
			}
			this.javaInstalledText = text;
		}

		public void checkJavaVersion() {
			link.setVisible(false);

			removeStyleName("info-java");
			removeStyleName("info-nojava");

			label.setValue(checkInfoText);
			deployJava.checkJavaVersion(JavaInfoPanel.this.javaVersion);
		}

		public boolean isJavaOk() {
			return javaOk;
		}
	}
	

	public static final DeployJava get(UI ui) {
		DeployJava deployJava = map.get(ui);
		
		if (null == deployJava) {
			deployJava = new DeployJava(ui);
			map.put(ui, deployJava);
		}
		return deployJava;
	}
}

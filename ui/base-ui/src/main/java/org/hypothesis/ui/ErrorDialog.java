/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class ErrorDialog extends Window {

	// System wide defaults
	private static final double MIN_WIDTH = 20d;
	private static final double MAX_WIDTH = 40d;
	private static final double MIN_HEIGHT = 1d;
	private static final double MAX_HEIGHT = 30d;

	private final Button button;

	public ErrorDialog(String caption, String message) {
		super(caption);

		VerticalLayout verticalLayout = new VerticalLayout();
		setContent(verticalLayout);
		verticalLayout.setSizeFull();
		verticalLayout.setSpacing(true);
		verticalLayout.setMargin(true);

		VerticalLayout scrollContent = new VerticalLayout();
		Panel panel = new Panel(scrollContent);
		verticalLayout.addComponent(panel);
		panel.setSizeFull();
		panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		verticalLayout.setExpandRatio(panel, 1.0f);

		Label text = new Label(message);
		scrollContent.addComponent(text);

		HorizontalLayout buttons = new HorizontalLayout();
		verticalLayout.addComponent(buttons);
		buttons.setSpacing(true);

		buttons.setWidth(100.0f, Unit.PERCENTAGE);

		button = new Button("OK");
		button.addStyleName(ValoTheme.BUTTON_PRIMARY);
		button.setData(false);
		button.setClickShortcut(KeyCode.ENTER, null);
		button.focus();
		button.addClickListener(e -> getUI().removeWindow(ErrorDialog.this));
		buttons.addComponent(button);
		buttons.setComponentAlignment(button, Alignment.MIDDLE_CENTER);

		// Approximate the size of the dialog
		double[] dim = getDialogDimensions(message, true);
		setWidth(format(dim[0]) + "em");
		setHeight(format(dim[1]) + "em");
		setResizable(false);
	}

	/**
	 * Approximates the dialog dimensions based on its message length.
	 * 
	 * @param message
	 *            Message string
	 * @return
	 */
	protected double[] getDialogDimensions(String message, boolean newLines) {

		// Based on Reindeer style:
		double chrW = 0.51d;
		double chrH = 1.5d;
		double length = message != null ? chrW * message.length() : 0;
		double rows = Math.ceil(length / MAX_WIDTH);

		// Estimate extra lines
		if (newLines) {
			rows += message != null ? count("\n", message) : 0;
		}

		// System.out.println(message.length() + " = " + length + "em");
		// System.out.println("Rows: " + (length / MAX_WIDTH) + " = " + rows);

		// Obey maximum size
		double width = Math.min(MAX_WIDTH, length);
		double height = Math.ceil(Math.min(MAX_HEIGHT, rows * chrH));

		// Obey the minimum size
		width = Math.max(width, MIN_WIDTH);
		height = Math.max(height, MIN_HEIGHT);

		// Based on Reindeer style:
		double btnHeight = 4d;
		double vmargin = 5d;
		double hmargin = 1d;

		// System.out.println(res[0] + "," + res[1]);
		return new double[] { width + hmargin, height + btnHeight + vmargin };
	}

	/**
	 * Count the number of needles within a haystack.
	 * 
	 * @param needle
	 *            The string to search for.
	 * @param haystack
	 *            The string to process.
	 * @return
	 */
	private static int count(final String needle, final String haystack) {
		int count = 0;
		int pos = -1;
		while ((pos = haystack.indexOf(needle, pos + 1)) >= 0) {
			count++;
		}
		return count;
	}

	/**
	 * Format a double single fraction digit.
	 * 
	 * @param n
	 * @return
	 */
	private String format(double n) {
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(1);
		nf.setGroupingUsed(false);
		return nf.format(n);
	}

	public void show(UI ui) {
		center();
		setModal(true);
		ui.addWindow(this);
	}

	public void setButtonCaption(String caption) {
		button.setCaption(caption);
	}

}

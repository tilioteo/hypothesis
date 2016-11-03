/**
 * 
 */
package org.hypothesis.application.junit;

import com.vaadin.ui.Component;
import org.dom4j.Document;
import org.hypothesis.application.collector.core.SlideFactory;
import org.hypothesis.application.collector.core.SlideManager;
import org.hypothesis.application.collector.events.ProcessEventManager;
import org.hypothesis.common.xml.Utility;
import org.hypothesis.entity.Slide;
import org.hypothesis.entity.SlideContent;
import org.hypothesis.entity.SlideTemplate;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 
 * @author Kamil Morong - Hypothesis
 *
 */
@SuppressWarnings("unused")
public class SlideFactoryTest {

	/**
	 * Test method for {@link org.hypothesis.application.collector.core.SlideFactory#createSlideControls(org.hypothesis.application.collector.core.SlideManager)}.
	 */
	@Test
	public Component testCreateSlideControls(ProcessEventManager eventManager) {
		Slide slide = testCreateSlide();
		SlideManager slideManager = new SlideManager(eventManager);
		slideManager.setCurrent(slide);
		slideManager.current();
		SlideFactory slideFactory = SlideFactory.getInstatnce();
		slideFactory.createSlideControls(slideManager);
		return slideManager.getViewport() != null ? slideManager.getViewport().getComponent() : null;
	}
	
	private static Slide testCreateSlideFromStrings(Long id, String templateString, String contentString) {
		Document templateDocument = Utility.readString(templateString);
		Document contentDocument = Utility.readString(contentString);
		SlideTemplate slideTemplate = new SlideTemplate();
		slideTemplate.setDocument(templateDocument);
		SlideContent slideContent = new SlideContent(slideTemplate);
		try {
			slideContent.setDocument(contentDocument);
		} catch (Exception e) {}
		
		Slide slide = new Slide(slideContent);
		slide.setId(id);
		return slide;
	}
	
	public static Slide testCreateSlide() {
		return testCreateSlideFromStrings(new Long(1), SlideConstants.TEMPLATE_XML, SlideConstants.CONTENT_XML);
	}

	public static Slide testCreateSlide1() {
		return testCreateSlideFromStrings(new Long(11), SlideConstants.TEMPLATE_XML1, SlideConstants.CONTENT_XML1);
	}

	public static Slide testCreateSlide2() {
		return testCreateSlideFromStrings(new Long(12), SlideConstants.TEMPLATE_XML1, SlideConstants.CONTENT_XML2);
	}

	public static Slide testCreateSlide3() {
		return testCreateSlideFromStrings(new Long(13), SlideConstants.TEMPLATE_XML1, SlideConstants.CONTENT_XML3);
	}

}

/**
 * 
 */
package org.hypothesis.application.junit;

import org.dom4j.Document;
import org.hypothesis.application.collector.BranchMap;
import org.hypothesis.application.collector.core.BranchFactory;
import org.hypothesis.application.collector.core.BranchManager;
import org.hypothesis.common.xml.Utility;
import org.hypothesis.entity.Branch;
import org.hypothesis.entity.Slide;
import org.hypothesis.entity.Task;
import org.junit.Test;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
public class BranchFactoryTest {

	/**
	 * Test method for {@link org.hypothesis.application.collector.core.BranchFactory#createBranchControls(org.hypothesis.application.collector.core.BranchManager)}.
	 */
	@Test
	public void testCreateBranchControls() {
		Document document = Utility.readString(BranchConstants.BRANCH_XML);
		Branch branch = createBranch("startovaci", SlideFactoryTest.testCreateSlide());
		Branch branch1 = createBranch("prvni", SlideFactoryTest.testCreateSlide1());
		Branch branch2 = createBranch("druha", SlideFactoryTest.testCreateSlide2());
		Branch branch3 = createBranch("treti", SlideFactoryTest.testCreateSlide3());
		
		BranchMap branchMap = branch.getBranchMap();
		branchMap.put("prvni", branch1);
		branchMap.put("druha", branch2);
		branchMap.put("default", branch3);
		
		try {
			branch.setDocument(document);
		} catch (Exception e) {}
		
		BranchManager branchManager = new BranchManager();
		branchManager.setCurrent(branch);
		branchManager.current();
		BranchFactory branchFactory = BranchFactory.getInstance();
		branchFactory.createBranchControls(branchManager);
	}
	
	public static Branch testCreateBranchTree() {
		Document document = Utility.readString(BranchConstants.BRANCH_XML);
		Branch branch = createBranch("startovaci", SlideFactoryTest.testCreateSlide());
		Branch branch1 = createBranch("prvni", SlideFactoryTest.testCreateSlide1());
		Branch branch2 = createBranch("druha", SlideFactoryTest.testCreateSlide2());
		Branch branch3 = createBranch("treti", SlideFactoryTest.testCreateSlide3());
		
		BranchMap branchMap = branch.getBranchMap();
		branchMap.put("prvni", branch1);
		branchMap.put("druha", branch2);
		branchMap.put("default", branch3);
		
		try {
			branch.setDocument(document);
		} catch (Exception e) {}
		
		return branch;
	}
	
	private static Branch createBranch(String name, Slide slide) {
		Branch branch = new Branch();
		branch.setNote(name + " branch");
		Task task = new Task();
		task.setName(name + " task");
		task.addSlide(slide);
		branch.addTask(task);
		return branch;
	}

}

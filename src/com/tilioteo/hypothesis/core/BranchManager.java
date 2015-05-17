/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.tilioteo.hypothesis.dom.XmlUtility;
import com.tilioteo.hypothesis.entity.Branch;
import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.Slide;
import com.tilioteo.hypothesis.interfaces.ExchangeVariable;
import com.tilioteo.hypothesis.processing.AbstractBasePath;
import com.tilioteo.hypothesis.processing.DefaultPath;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class BranchManager extends KeySetManager<Pack, Branch, Long> {

	private static Logger log = Logger.getLogger(BranchManager.class);

	private BranchFactory branchFactory;

	private List<Path> paths = new ArrayList<Path>();
	private DefaultPath defaultPath = null;

	private Document branchXml = null;
	private Branch current = null;

	private HashMap<Long, Map<Integer, ExchangeVariable>> slideOutputs = new HashMap<Long, Map<Integer, ExchangeVariable>>();

	private String nextKey = null;

	public BranchManager() {
		branchFactory = BranchFactory.getInstance(this);
	}

	public void addPath(AbstractBasePath path) {
		if (path instanceof Path) {
			this.paths.add((Path) path);
		} else if (path instanceof DefaultPath) {
			this.defaultPath = (DefaultPath) path;
		}
	}

	public void addSlideOutputs(Slide slide, Map<Integer, ExchangeVariable> outputValues) {
		if (slide != null && slide.getId() != null && !outputValues.isEmpty()) {
			// copy map of variables because it will be erased at the slide finish 
			HashMap<Integer, ExchangeVariable> map = new HashMap<Integer, ExchangeVariable>();
			for (Integer index : outputValues.keySet()) {
				map.put(index, outputValues.get(index));
			}
			
			slideOutputs.put(slide.getId(), map);
		}
	}

	private void buildBranch() {
		log.debug("buildBranch");
		clearBranchRelatives();
		Branch branch = super.current();
		if (current != null) {
			branchXml = XmlUtility.readString(branch.getXmlData());
			branchFactory.createBranchControls();
		}
	}

	private void clearBranchRelatives() {
		log.debug("clearBranchRelatives");
		
		this.branchXml = null;
		this.defaultPath = null;
		this.paths.clear();
		this.slideOutputs.clear();
	}

	@Override
	public Branch current() {
		Branch branch = super.current();
		if (current != branch) {
			current = branch;
			
			buildBranch();
		}
		
		return current;
	}

	/*@Override
	public Branch find(Branch item) {
		if (item != curent) {
			clearBranchRelatives();
			super.find(item);
		}
		return current();
	}*/

	/*@Override
	public Branch get(Long key) {
		clearBranchRelatives();
		super.get(key);
		return current();
	}*/

	public Document getBranchXml() {
		return branchXml;
	}

	public String getNextBranchKey() {
		nextKey = null;
		boolean pathFound = false;

		for (Path path : paths) {
			if (path.isValid(slideOutputs)) {
				nextKey = path.getBranchKey();
				pathFound = true;
				break;
			}
		}
		if (!pathFound && defaultPath != null) {
			nextKey = defaultPath.getBranchKey();
		}
		return nextKey;
	}

	public String getSerializedData() {
		return nextKey;
	}

	/*@Override
	public void setCurrent(Branch item) {
		if (item != super.current()) {
			clearBranchRelatives();
			super.setCurrent(item);
			buildBranch();
		}
	}*/

	
}

/**
 * 
 */
package com.tilioteo.hypothesis.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.tilioteo.hypothesis.dom.XmlUtility;
import com.tilioteo.hypothesis.entity.Branch;
import com.tilioteo.hypothesis.entity.Pack;
import com.tilioteo.hypothesis.entity.Slide;
import com.tilioteo.hypothesis.processing.AbstractBasePath;
import com.tilioteo.hypothesis.processing.DefaultPath;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class BranchManager extends KeySetManager<Pack, Branch, Long> {

	private static Logger log = Logger.getLogger(BranchManager.class);

	private BranchFactory branchFactory;

	private List<Path> paths = new ArrayList<Path>();
	private DefaultPath defaultPath = null;

	private Document branchXml = null;
	private Branch current = null;

	private PairList<Slide, Object> slideOutputValues = new PairList<Slide, Object>();

	//private String nextKey = null;

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

	public void addSlideOutputValue(Slide slide, Object outputValue) {
		slideOutputValues.addObjectPair(slide, outputValue);
	}

	private void buildBranch() {
		log.debug("buildBranch");
		clearBranchRelatives();
		Branch branch = super.current();
		if (current != null) {
			branchXml = XmlUtility.readString(branch.getXmlData());
			branchFactory.createBranchControls(this);
		}
	}

	private void clearBranchRelatives() {
		log.debug("clearBranchRelatives");
		
		this.branchXml = null;
		this.defaultPath = null;
		this.paths.clear();
		this.slideOutputValues.clear();
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
		String nextKey = null;
		boolean pathFound = false;

		for (Path path : paths) {
			if (path.isValid(slideOutputValues)) {
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
		// TODO serialize slide outputs
		return null;
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

/**
 * 
 */
package org.hypothesis.application.collector.core;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.hypothesis.application.collector.KeySetManager;
import org.hypothesis.common.PairList;
import org.hypothesis.entity.Branch;
import org.hypothesis.entity.Pack;
import org.hypothesis.entity.Slide;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class BranchManager extends KeySetManager<Pack, Branch, Long> {

	private BranchFactory branchFactory;

	private Document branchXml = null;
	private Branch lastBranch = null;
	private List<Path> paths = new ArrayList<Path>();
	private DefaultPath defaultPath = null;

	private PairList<Slide, Object> slideOutputValues = new PairList<Slide, Object>();

	private String nextKey = null;

	public BranchManager() {
		branchFactory = BranchFactory.getInstance();
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
		Branch branch = super.current();
		if (lastBranch != branch) {
			if (branch != null) {
				this.branchXml = branch.getDocument();
				branchFactory.createBranchControls(this);
			}
			lastBranch = branch;
		}
	}

	private void clearBranchRelatives() {
		this.nextKey = null;
		this.branchXml = null;
		this.lastBranch = null;
		this.defaultPath = null;
		this.paths.clear();
		this.slideOutputValues.clear();
	}

	@Override
	public Branch current() {
		buildBranch();
		return super.current();
	}

	@Override
	public Branch find(Branch item) {
		if (item != lastBranch) {
			clearBranchRelatives();
			super.find(item);
		}
		return current();
	}

	@Override
	public Branch get(Long key) {
		clearBranchRelatives();
		super.get(key);
		return current();
	}

	public Document getBranchXml() {
		return branchXml;
	}

	public String getNextBranchKey() {
		return nextKey;
	}

	public String getSerializedData() {
		// TODO serialize slide outputs
		return null;
	}

	@Override
	public void setCurrent(Branch item) {
		if (item != super.current()) {
			clearBranchRelatives();
			super.setCurrent(item);
			buildBranch();
		}
	}

	public void updateNextBranchKey() {
		nextKey = null;

		for (Path path : paths) {
			if (path.isValid(slideOutputValues))
				nextKey = path.getBranchKey();
		}
		if (defaultPath != null)
			nextKey = defaultPath.getBranchKey();
	}
}

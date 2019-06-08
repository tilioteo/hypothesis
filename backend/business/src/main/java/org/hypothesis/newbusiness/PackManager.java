package org.hypothesis.newbusiness;

import java.util.ListIterator;
import java.util.Objects;

import org.hypothesis.data.dto.BranchDto;
import org.hypothesis.data.dto.PackDto;
import org.hypothesis.data.dto.SlideDto;
import org.hypothesis.data.dto.TaskDto;

public class PackManager {

	private final PackDto pack;

	private BranchDto currentBranch = null;
	private TaskDto currentTask = null;
	private SlideDto currentSlide = null;

	private ListIterator<BranchDto> branchIterator;
	private ListIterator<TaskDto> taskIterator;
	private ListIterator<SlideDto> slideIterator;

	public PackManager(PackDto pack) {
		Objects.requireNonNull(pack);

		this.pack = pack;

		init();
	}

	private void init() {
		branchIterator = pack.getBranches().listIterator();
		nextBranch();

		if (currentBranch != null) {
			taskIterator = currentBranch.getTasks().listIterator();
			nextTask();
		}

		if (currentTask != null) {
			slideIterator = currentTask.getSlides().listIterator();
			nextSlide();
		}

	}

	private void nextBranch() {
		if (branchIterator.hasNext()) {
			currentBranch = branchIterator.next();
		}
	}

	private void nextTask() {
		if (taskIterator.hasNext()) {
			currentTask = taskIterator.next();
		}
	}

	private void nextSlide() {
		if (slideIterator.hasNext()) {
			currentSlide = slideIterator.next();
		} else {
		}
	}

}

package org.hypothesis.data.service;

import java.util.List;

import org.hypothesis.data.dto.SlideOrderDto;

public interface SlideOrderService {

	SlideOrderDto findSlideOrder(long testId, long taskId);

	SlideOrderDto saveSlideOrder(long testId, long taskId, List<Integer> order);

}

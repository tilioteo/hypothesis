package org.hypothesis.data.interfaces;

import java.io.Serializable;

import org.hypothesis.data.model.Slide;

public interface SlideService extends Serializable {

	Slide findById(Long id);

}
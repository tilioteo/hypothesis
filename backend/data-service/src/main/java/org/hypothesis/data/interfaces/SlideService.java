package org.hypothesis.data.interfaces;

import org.hypothesis.data.model.Slide;

import java.io.Serializable;

public interface SlideService extends Serializable {

	Slide findById(Long id);

}
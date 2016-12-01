package org.hypothesis.data.interfaces;

import org.hypothesis.data.model.Pack;

import java.io.Serializable;

public interface PackService extends Serializable {

	Pack findPackById(Long id);

}
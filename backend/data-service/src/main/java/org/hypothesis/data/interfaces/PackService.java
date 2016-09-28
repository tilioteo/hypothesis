package org.hypothesis.data.interfaces;

import java.io.Serializable;

import org.hypothesis.data.model.Pack;

public interface PackService extends Serializable {

	Pack findPackById(Long id);

}
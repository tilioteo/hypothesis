/*
 * Copyright 2011 Henri Kerola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.gwtgraphics.client;

/**
 * Group is a container, which can contain one or more VectorObjects.
 * 
 * @author Henri Kerola
 * 
 */
public class Group extends AbstractDrawingContainer implements Positionable {

	/**
	 * Creates an empty Group.
	 */
	public Group() {
	}

	@Override
	protected Class<? extends AbstractDrawing> getType() {
		return Group.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.vaadin.gwtgraphics.client.VectorObjectContainer#bringToFront(org.
	 * vaadin.gwtgraphics.client.VectorObject)
	 */
	/*public AbstractDrawing bringToFront(AbstractDrawing vo) {
		if (vo.getParent() != this) {
			return null;
		}
		getImpl().bringToFront(getElement(), vo.getElement());
		return vo;
	}*/

	@Override
	public int getX() {
		return getImpl().getX(getElement());
	}

	@Override
	public void setX(int x) {
		getImpl().setX(getElement(), x, isAttached());
	}

	@Override
	public int getY() {
		return getImpl().getY(getElement());
	}

	@Override
	public void setY(int y) {
		getImpl().setY(getElement(), y, isAttached());
	}
}

/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui.table;

import com.vaadin.ui.Table;
import org.apache.commons.lang3.BooleanUtils;
import org.hypothesis.data.model.FieldConstants;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class CheckTable extends Table {

	public CheckTable() {
		super();
		setMultiSelect(true);
	}

	public CheckTable(String caption) {
		super(caption);
		setMultiSelect(true);
	}

	@Override
	public Set<Object> getValue() {
		return getItemIds().stream()
				.filter(e -> BooleanUtils
						.isTrue((Boolean) getItem(e).getItemProperty(FieldConstants.SELECTED).getValue()))
				.collect(Collectors.toSet());
	}

}

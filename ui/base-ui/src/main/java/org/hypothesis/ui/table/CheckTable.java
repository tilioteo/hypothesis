/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.ui.table;

import java.util.HashSet;
import java.util.Set;

import org.hypothesis.data.model.FieldConstants;
import com.vaadin.data.Item;
import com.vaadin.ui.Table;

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
		Set<Object> value = new HashSet<>();

		for (Object itemId : getItemIds()) {
			Item item = getItem(itemId);
			Boolean selected = (Boolean) item.getItemProperty(FieldConstants.SELECTED).getValue();

			if (selected != null && selected.equals(true)) {
				value.add(item);
			}
		}

		return value;
	}

}

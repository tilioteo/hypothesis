/**
 * 
 */
package org.vaadin.maps.ui;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.vaadin.maps.event.LayoutEvents.LayoutClickEvent;
import org.vaadin.maps.event.LayoutEvents.LayoutClickListener;
import org.vaadin.maps.event.LayoutEvents.LayoutClickNotifier;
import org.vaadin.maps.exception.OutOfBoundsException;
import org.vaadin.maps.exception.OverlapsException;
import org.vaadin.maps.shared.ui.gridlayout.GridLayoutServerRpc;
import org.vaadin.maps.shared.ui.gridlayout.GridLayoutState;
import org.vaadin.maps.shared.ui.gridlayout.GridLayoutState.ChildComponentData;

import com.vaadin.shared.Connector;
import com.vaadin.shared.EventId;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.Component;

/**
 * @author morong
 * 
 */
@SuppressWarnings("serial")
public class GridLayout<C extends Component> extends AbstractLayout<C> implements LayoutClickNotifier<C> {

	private GridLayoutServerRpc rpc = new GridLayoutServerRpc() {

		@Override
		public void layoutClick(MouseEventDetails mouseDetails,
				Connector clickedConnector) {
			fireEvent(LayoutClickEvent.createEvent(GridLayout.this,
					mouseDetails, clickedConnector));

		}
	};
	/**
	 * Cursor X position: this is where the next component with unspecified x,y
	 * is inserted
	 */
	private int cursorX = 0;

	/**
	 * Cursor Y position: this is where the next component with unspecified x,y
	 * is inserted
	 */
	private int cursorY = 0;

	private final LinkedList<C> components = new LinkedList<C>();

	/**
	 * Constructor for a grid of given size (number of columns and rows).
	 * 
	 * The grid may grow or shrink later. Grid grows automatically if you add
	 * components outside its area.
	 * 
	 * @param columns
	 *            Number of columns in the grid.
	 * @param rows
	 *            Number of rows in the grid.
	 */
	public GridLayout(int columns, int rows) {
		setColumns(columns);
		setRows(rows);
		registerRpc(rpc);
	}

	/**
	 * Constructs an empty (1x1) grid layout that is extended as needed.
	 */
	public GridLayout() {
		this(1, 1);
	}

	/**
	 * Constructs a GridLayout of given size (number of columns and rows) and
	 * adds the given components in order to the grid.
	 * 
	 * @see #addComponents(Component...)
	 * 
	 * @param columns
	 *            Number of columns in the grid.
	 * @param rows
	 *            Number of rows in the grid.
	 * @param children
	 *            Components to add to the grid.
	 */
	@SuppressWarnings("unchecked")
	public GridLayout(int columns, int rows, C... children) {
		this(columns, rows);
		addComponents(children);
	}

	@Override
	protected GridLayoutState getState() {
		return (GridLayoutState) super.getState();
	}

	/**
	 * <p>
	 * Adds a component to the grid in the specified area. The area is defined
	 * by specifying the upper left corner (column1, row1) and the lower right
	 * corner (column2, row2) of the area. The coordinates are zero-based.
	 * </p>
	 * 
	 * <p>
	 * If the area overlaps with any of the existing components already present
	 * in the grid, the operation will fail and an {@link OverlapsException} is
	 * thrown.
	 * </p>
	 * 
	 * @param component
	 *            the component to be added, not <code>null</code>.
	 * @param column1
	 *            the column of the upper left corner of the area <code>c</code>
	 *            is supposed to occupy. The leftmost column has index 0.
	 * @param row1
	 *            the row of the upper left corner of the area <code>c</code> is
	 *            supposed to occupy. The topmost row has index 0.
	 * @param column2
	 *            the column of the lower right corner of the area
	 *            <code>c</code> is supposed to occupy.
	 * @param row2
	 *            the row of the lower right corner of the area <code>c</code>
	 *            is supposed to occupy.
	 * @throws OverlapsException
	 *             if the new component overlaps with any of the components
	 *             already in the grid.
	 * @throws OutOfBoundsException
	 *             if the cells are outside the grid area.
	 */
	public void addComponent(C component, int column1, int row1, int column2,
			int row2) throws OverlapsException, OutOfBoundsException {

		if (component == null) {
			throw new NullPointerException("Component must not be null");
		}

		// Checks that the component does not already exist in the container
		if (components.contains(component)) {
			throw new IllegalArgumentException(
					"Component is already in the container");
		}

		// Creates the area
		final Area area = new Area(component, column1, row1, column2, row2);

		// Checks the validity of the coordinates
		if (column2 < column1 || row2 < row1) {
			throw new IllegalArgumentException(
					"Illegal coordinates for the component");
		}
		if (column1 < 0 || row1 < 0 || column2 >= getColumns()
				|| row2 >= getRows()) {
			throw new OutOfBoundsException(area);
		}

		// Checks that newItem does not overlap with existing items
		checkExistingOverlaps(area);

		// Inserts the component to right place at the list
		// Respect top-down, left-right ordering
		// component.setParent(this);
		final Iterator<C> i = components.iterator();
		final Map<Connector, ChildComponentData> childDataMap = getState().childData;
		int index = 0;
		boolean done = false;
		while (!done && i.hasNext()) {
			final ChildComponentData existingArea = childDataMap.get(i.next());
			if ((existingArea.row1 >= row1 && existingArea.column1 > column1)
					|| existingArea.row1 > row1) {
				components.add(index, component);
				done = true;
			}
			index++;
		}
		if (!done) {
			components.addLast(component);
		}

		childDataMap.put(component, area.childData);

		// Attempt to add to super
		try {
			super.addComponent(component);
		} catch (IllegalArgumentException e) {
			childDataMap.remove(component);
			components.remove(component);
			throw e;
		}

		// update cursor position, if it's within this area; use first position
		// outside this area, even if it's occupied
		if (cursorX >= column1 && cursorX <= column2 && cursorY >= row1
				&& cursorY <= row2) {
			// cursor within area
			cursorX = column2 + 1; // one right of area
			if (cursorX >= getColumns()) {
				// overflowed columns
				cursorX = 0; // first col
				// move one row down, or one row under the area
				cursorY = (column1 == 0 ? row2 : row1) + 1;
			} else {
				cursorY = row1;
			}
		}

		markAsDirty();
	}

	/**
	 * Tests if the given area overlaps with any of the items already on the
	 * grid.
	 * 
	 * @param area
	 *            the Area to be checked for overlapping.
	 * @throws OverlapsException
	 *             if <code>area</code> overlaps with any existing area.
	 */
	@SuppressWarnings("unchecked")
	private void checkExistingOverlaps(Area area) throws OverlapsException {
		for (Entry<Connector, ChildComponentData> entry : getState().childData
				.entrySet()) {
			if (componentsOverlap(entry.getValue(), area.childData)) {
				// Component not added, overlaps with existing component
				throw new OverlapsException(new Area(entry.getValue(),
						(C) entry.getKey()));
			}
		}
	}

	/**
	 * Adds the component to the grid in cells column1,row1 (NortWest corner of
	 * the area.) End coordinates (SouthEast corner of the area) are the same as
	 * column1,row1. The coordinates are zero-based. Component width and height
	 * is 1.
	 * 
	 * @param component
	 *            the component to be added, not <code>null</code>.
	 * @param column
	 *            the column index, starting from 0.
	 * @param row
	 *            the row index, starting from 0.
	 * @throws OverlapsException
	 *             if the new component overlaps with any of the components
	 *             already in the grid.
	 * @throws OutOfBoundsException
	 *             if the cell is outside the grid area.
	 */
	public void addComponent(C component, int column, int row)
			throws OverlapsException, OutOfBoundsException {
		this.addComponent(component, column, row, column, row);
	}

	/**
	 * Forces the next component to be added at the beginning of the next line.
	 * 
	 * <p>
	 * Sets the cursor column to 0 and increments the cursor row by one.
	 * </p>
	 * 
	 * <p>
	 * By calling this function you can ensure that no more components are added
	 * right of the previous component.
	 * </p>
	 * 
	 * @see #space()
	 */
	public void newLine() {
		cursorX = 0;
		cursorY++;
	}

	/**
	 * Moves the cursor forward by one. If the cursor goes out of the right grid
	 * border, it is moved to the first column of the next row.
	 * 
	 * @see #newLine()
	 */
	public void space() {
		cursorX++;
		if (cursorX >= getColumns()) {
			cursorX = 0;
			cursorY++;
		}
	}

	/**
	 * Adds the component into this container to the cursor position. If the
	 * cursor position is already occupied, the cursor is moved forwards to find
	 * free position. If the cursor goes out from the bottom of the grid, the
	 * grid is automatically extended.
	 * 
	 * @param component
	 *            the component to be added, not <code>null</code>.
	 */
	@Override
	public void addComponent(C component) {
		if (component == null) {
			throw new IllegalArgumentException("Component must not be null");
		}

		// Finds first available place from the grid
		Area area;
		boolean done = false;
		while (!done) {
			try {
				area = new Area(component, cursorX, cursorY, cursorX, cursorY);
				checkExistingOverlaps(area);
				done = true;
			} catch (final OverlapsException e) {
				space();
			}
		}

		// Extends the grid if needed
		if (cursorX >= getColumns()) {
			setColumns(cursorX + 1);
		}
		if (cursorY >= getRows()) {
			setRows(cursorY + 1);
		}

		addComponent(component, cursorX, cursorY);
	}

	/**
	 * Removes the specified component from the layout.
	 * 
	 * @param component
	 *            the component to be removed.
	 */
	@Override
	public void removeComponent(C component) {

		// Check that the component is contained in the container
		if (component == null || !components.contains(component)) {
			return;
		}

		getState().childData.remove(component);
		components.remove(component);

		super.removeComponent(component);
	}

	/**
	 * Removes the component specified by its cell coordinates.
	 * 
	 * @param column
	 *            the component's column, starting from 0.
	 * @param row
	 *            the component's row, starting from 0.
	 */
	public void removeComponent(int column, int row) {

		// Finds the area
		for (final Iterator<C> i = components.iterator(); i.hasNext();) {
			final C component = i.next();
			final ChildComponentData childData = getState().childData
					.get(component);
			if (childData.column1 == column && childData.row1 == row) {
				removeComponent(component);
				return;
			}
		}
	}

	/**
	 * Gets an Iterator for the components contained in the layout. By using the
	 * Iterator it is possible to step through the contents of the layout.
	 * 
	 * @return the Iterator of the components inside the layout.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Component> iterator() {
		return (Iterator<Component>) Collections.unmodifiableCollection(
				components).iterator();
	}

	/**
	 * Gets an Iterator for the components contained in the layout. By using the
	 * Iterator it is possible to step through the contents of the layout.
	 * 
	 * @return the Iterator of the components inside the layout.
	 */
	@Override
	public Iterator<C> typedIterator() {
		return Collections.unmodifiableCollection(components).iterator();
	}

	/**
	 * Gets the number of components contained in the layout. Consistent with
	 * the iterator returned by {@link #getComponentIterator()}.
	 * 
	 * @return the number of contained components
	 */
	@Override
	public int getComponentCount() {
		return components.size();
	}

	/**
	 * Defines a rectangular area of cells in a GridLayout.
	 * 
	 * <p>
	 * Also maintains a reference to the component contained in the area.
	 * </p>
	 * 
	 * <p>
	 * The area is specified by the cell coordinates of its upper left corner
	 * (column1,row1) and lower right corner (column2,row2). As otherwise with
	 * GridLayout, the column and row coordinates start from zero.
	 * </p>
	 * 
	 * @author Vaadin Ltd.
	 * @since 3.0
	 */
	public class Area implements Serializable {
		private final ChildComponentData childData;
		private final C component;
		
		/**
		 * <p>
		 * Construct a new area on a grid.
		 * </p>
		 * 
		 * @param component
		 *            the component connected to the area.
		 * @param column1
		 *            The column of the upper left corner cell of the area. The
		 *            leftmost column has index 0.
		 * @param row1
		 *            The row of the upper left corner cell of the area. The
		 *            topmost row has index 0.
		 * @param column2
		 *            The column of the lower right corner cell of the area. The
		 *            leftmost column has index 0.
		 * @param row2
		 *            The row of the lower right corner cell of the area. The
		 *            topmost row has index 0.
		 */
		public Area(C component, int column1, int row1, int column2,
				int row2) {
			this.component = component;
			childData = new ChildComponentData();
			childData.column1 = column1;
			childData.row1 = row1;
			childData.column2 = column2;
			childData.row2 = row2;
		}

		public Area(ChildComponentData childData, C component) {
			this.childData = childData;
			this.component = component;
		}

		public ChildComponentData getChildData() {
			return childData;
		}

		/**
		 * Tests if this Area overlaps with another Area.
		 * 
		 * @param other
		 *            the other Area that is to be tested for overlap with this
		 *            area
		 * @return <code>true</code> if <code>other</code> area overlaps with
		 *         this on, <code>false</code> if it does not.
		 */
		public boolean overlaps(Area other) {
			return componentsOverlap(childData, other.childData);
		}

		/**
		 * Gets the component connected to the area.
		 * 
		 * @return the Component.
		 */
		public C getComponent() {
			return component;
		}

		/**
		 * Gets the column of the top-left corner cell.
		 * 
		 * @return the column of the top-left corner cell.
		 */
		public int getColumn1() {
			return childData.column1;
		}

		/**
		 * Gets the column of the bottom-right corner cell.
		 * 
		 * @return the column of the bottom-right corner cell.
		 */
		public int getColumn2() {
			return childData.column2;
		}

		/**
		 * Gets the row of the top-left corner cell.
		 * 
		 * @return the row of the top-left corner cell.
		 */
		public int getRow1() {
			return childData.row1;
		}

		/**
		 * Gets the row of the bottom-right corner cell.
		 * 
		 * @return the row of the bottom-right corner cell.
		 */
		public int getRow2() {
			return childData.row2;
		}

	}

	private static boolean componentsOverlap(ChildComponentData a,
			ChildComponentData b) {
		return a.column1 <= b.column2 && a.row1 <= b.row2
				&& a.column2 >= b.column1 && a.row2 >= b.row1;
	}

	/**
	 * Sets the number of columns in the grid. The column count can not be
	 * reduced if there are any areas that would be outside of the shrunk grid.
	 * 
	 * @param columns
	 *            the new number of columns in the grid.
	 */
	@SuppressWarnings("unchecked")
	public void setColumns(int columns) {

		// The the param
		if (columns < 1) {
			throw new IllegalArgumentException(
					"The number of columns and rows in the grid must be at least 1");
		}

		// In case of no change
		if (getColumns() == columns) {
			return;
		}

		// Checks for overlaps
		if (getColumns() > columns) {
			for (Entry<Connector, ChildComponentData> entry : getState().childData
					.entrySet()) {
				if (entry.getValue().column2 >= columns) {
					throw new OutOfBoundsException(new Area(entry.getValue(),
							(C) entry.getKey()));
				}
			}
		}
		// TODO forget expands for removed columns

		getState().columns = columns;
	}

	/**
	 * Get the number of columns in the grid.
	 * 
	 * @return the number of columns in the grid.
	 */
	public int getColumns() {
		return getState().columns;
	}

	/**
	 * Sets the number of rows in the grid. The number of rows can not be
	 * reduced if there are any areas that would be outside of the shrunk grid.
	 * 
	 * @param rows
	 *            the new number of rows in the grid.
	 */
	@SuppressWarnings("unchecked")
	public void setRows(int rows) {

		// The the param
		if (rows < 1) {
			throw new IllegalArgumentException(
					"The number of columns and rows in the grid must be at least 1");
		}

		// In case of no change
		if (getRows() == rows) {
			return;
		}

		// Checks for overlaps
		if (getRows() > rows) {
			for (Entry<Connector, ChildComponentData> entry : getState().childData
					.entrySet()) {
				if (entry.getValue().row2 >= rows) {
					throw new OutOfBoundsException(new Area(entry.getValue(),
							(C) entry.getKey()));
				}
			}
		}
		// TODO forget expands for removed rows

		getState().rows = rows;
	}

	/**
	 * Get the number of rows in the grid.
	 * 
	 * @return the number of rows in the grid.
	 */
	public int getRows() {
		return getState().rows;
	}

	/**
	 * Gets the current x-position (column) of the cursor.
	 * 
	 * <p>
	 * The cursor position points the position for the next component that is
	 * added without specifying its coordinates (grid cell). When the cursor
	 * position is occupied, the next component will be added to first free
	 * position after the cursor.
	 * </p>
	 * 
	 * @return the grid column the cursor is on, starting from 0.
	 */
	public int getCursorX() {
		return cursorX;
	}

	/**
	 * Sets the current cursor x-position. This is usually handled automatically
	 * by GridLayout.
	 * 
	 * @param cursorX
	 */
	public void setCursorX(int cursorX) {
		this.cursorX = cursorX;
	}

	/**
	 * Gets the current y-position (row) of the cursor.
	 * 
	 * <p>
	 * The cursor position points the position for the next component that is
	 * added without specifying its coordinates (grid cell). When the cursor
	 * position is occupied, the next component will be added to the first free
	 * position after the cursor.
	 * </p>
	 * 
	 * @return the grid row the Cursor is on.
	 */
	public int getCursorY() {
		return cursorY;
	}

	/**
	 * Sets the current y-coordinate (row) of the cursor. This is usually
	 * handled automatically by GridLayout.
	 * 
	 * @param cursorY
	 *            the row number, starting from 0 for the topmost row.
	 */
	public void setCursorY(int cursorY) {
		this.cursorY = cursorY;
	}

	/* Documented in superclass */
	@Override
	public void replaceComponent(C oldComponent, C newComponent) {

		// Gets the locations
		ChildComponentData oldLocation = getState().childData.get(oldComponent);
		ChildComponentData newLocation = getState().childData.get(newComponent);

		if (oldLocation == null) {
			addComponent(newComponent);
		} else if (newLocation == null) {
			removeComponent(oldComponent);
			addComponent(newComponent, oldLocation.column1, oldLocation.row1,
					oldLocation.column2, oldLocation.row2);
		} else {
			getState().childData.put(newComponent, oldLocation);
			getState().childData.put(oldComponent, newLocation);
		}
	}

	/*
	 * Removes all components from this container.
	 * 
	 * @see com.vaadin.ui.ComponentContainer#removeAllComponents()
	 */
	@Override
	public void removeAllComponents() {
		super.removeAllComponents();
		cursorX = 0;
		cursorY = 0;
	}

	/**
	 * Inserts an empty row at the specified position in the grid.
	 * 
	 * @param row
	 *            Index of the row before which the new row will be inserted.
	 *            The leftmost row has index 0.
	 */
	public void insertRow(int row) {
		if (row > getRows()) {
			throw new IllegalArgumentException("Cannot insert row at " + row
					+ " in a gridlayout with height " + getRows());
		}

		for (ChildComponentData existingArea : getState().childData.values()) {
			// Areas ending below the row needs to be moved down or stretched
			if (existingArea.row2 >= row) {
				existingArea.row2++;

				// Stretch areas that span over the selected row
				if (existingArea.row1 >= row) {
					existingArea.row1++;
				}

			}
		}

		if (cursorY >= row) {
			cursorY++;
		}

		setRows(getRows() + 1);
		markAsDirty();
	}

	/**
	 * Removes a row and all the components in the row.
	 * 
	 * <p>
	 * Components which span over several rows are removed if the selected row
	 * is on the first row of such a component.
	 * </p>
	 * 
	 * <p>
	 * If the last row is removed then all remaining components will be removed
	 * and the grid will be reduced to one row. The cursor will be moved to the
	 * upper left cell of the grid.
	 * </p>
	 * 
	 * @param row
	 *            Index of the row to remove. The leftmost row has index 0.
	 */
	public void removeRow(int row) {
		if (row >= getRows()) {
			throw new IllegalArgumentException("Cannot delete row " + row
					+ " from a gridlayout with height " + getRows());
		}

		// Remove all components in row
		for (int col = 0; col < getColumns(); col++) {
			removeComponent(col, row);
		}

		// Shrink or remove areas in the selected row
		for (ChildComponentData existingArea : getState().childData.values()) {
			if (existingArea.row2 >= row) {
				existingArea.row2--;

				if (existingArea.row1 > row) {
					existingArea.row1--;
				}
			}
		}

		if (getRows() == 1) {
			/*
			 * Removing the last row means that the dimensions of the Grid
			 * layout will be truncated to 1 empty row and the cursor is moved
			 * to the first cell
			 */
			cursorX = 0;
			cursorY = 0;
		} else {
			setRows(getRows() - 1);
			if (cursorY > row) {
				cursorY--;
			}
		}

		markAsDirty();

	}

	/**
	 * Gets the Component at given index.
	 * 
	 * @param x
	 *            The column index, starting from 0 for the leftmost column.
	 * @param y
	 *            The row index, starting from 0 for the topmost row.
	 * @return Component in given cell or null if empty
	 */
	public Component getComponent(int x, int y) {
		for (Entry<Connector, ChildComponentData> entry : getState().childData
				.entrySet()) {
			ChildComponentData childData = entry.getValue();
			if (childData.column1 <= x && x <= childData.column2
					&& childData.row1 <= y && y <= childData.row2) {
				return (Component) entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Returns information about the area where given component is laid in the
	 * GridLayout.
	 * 
	 * @param component
	 *            the component whose area information is requested.
	 * @return an Area object that contains information how component is laid in
	 *         the grid
	 */
	public Area getComponentArea(C component) {
		ChildComponentData childComponentData = getState().childData
				.get(component);
		if (childComponentData == null) {
			return null;
		} else {
			return new Area(childComponentData, component);
		}
	}

	@Override
	public void addLayoutClickListener(LayoutClickListener<C> listener) {
		addListener(EventId.LAYOUT_CLICK_EVENT_IDENTIFIER,
				LayoutClickEvent.class, listener,
				LayoutClickListener.clickMethod);
	}

	@Override
	public void removeLayoutClickListener(LayoutClickListener<C> listener) {
		removeListener(EventId.LAYOUT_CLICK_EVENT_IDENTIFIER,
				LayoutClickEvent.class, listener);
	}

}

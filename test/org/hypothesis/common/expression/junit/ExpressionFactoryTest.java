/**
 * 
 */
package org.hypothesis.common.expression.junit;

import static org.junit.Assert.*;

import java.awt.Point;

import org.hypothesis.common.expression.Expression;
import org.hypothesis.common.expression.ExpressionFactory;
import org.junit.Test;

/**
 * @author Kamil Morong - Hypothesis
 *
 */
public class ExpressionFactoryTest {

	/**
	 * Test method for {@link org.hypothesis.common.expression.ExpressionFactory#parseString(java.lang.String)}.
	 */
	@SuppressWarnings("unused")
	@Test
	public void testParseString() {
		/*Expression expression = ExpressionFactory.parseString("(a+b)*(c-d)/2");
		assertNotNull(expression);

		expression.setVariableValue("a", 15);
		expression.setVariableValue("b", 3);
		expression.setVariableValue("c", 21);
		expression.setVariableValue("d", 10);
		
		double value = expression.getDouble();*/
		
		int i = 0;
		i = i+i+i+i+1;
		
		Expression expression = ExpressionFactory.parseString("pocitadlo=pocitadlo+1");
		//Expression expression = ExpressionFactory.parseString("pocitadlo");
		
		expression.setVariableValue("pocitadlo", 1);
		int value = expression.getInteger();
		assertEquals(2, value, 0);
		
		//Point point = new Point();
		//point.setLocation(1, 2);
		
		//Expression expression = ExpressionFactory.parseString("pocitadlo=obj->getX()");
		//Expression expression = ExpressionFactory.parseString("obj->setLocation(3,4)");
		//expression.setVariableValue("obj", point);
		//Object val = expression.getValue();

		//assertEquals(3.0, point.getX(), 0.0);
		//fail("Not yet implemented");
	}

}

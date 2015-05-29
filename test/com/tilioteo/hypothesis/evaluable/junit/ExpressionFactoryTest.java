/**
 * 
 */
package com.tilioteo.hypothesis.evaluable.junit;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Test;

import com.tilioteo.hypothesis.evaluable.Expression;
import com.tilioteo.hypothesis.evaluable.ExpressionFactory;

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
		
		//int i = 0;
		//i = i+i+i+i+1;
		
		//Expression expression = ExpressionFactory.parseString("pocitadlo=pocitadlo+1");
		//Expression expression = ExpressionFactory.parseString("pocitadlo");
		
		//expression.setVariableValue("pocitadlo", 1);
		//int value = expression.getInteger();
		//assertEquals(2, value, 0);
		
		//Point point = new Point();
		//point.setLocation(1, 2);
		
		//Expression expression = ExpressionFactory.parseString("pocitadlo=obj->getX()");
		//Expression expression = ExpressionFactory.parseString("obj->setLocation(3,4)");
		//Expression expression = ExpressionFactory.parseString("x1=obj->x");
		//Expression expression = ExpressionFactory.parseString("x1=obj->getX()");
		//expression.setVariableValue("obj", point);
		//Object val = expression.getValue();

		String parsed = ExpressionFactory.parseString("initMsg=Document->createMessage(\"3F66DCC0-BA8F-4825-A8EE-CB70EF118C93\")").toString();
		assertEquals("(initMsg=(Document->createMessage(\"3F66DCC0-BA8F-4825-A8EE-CB70EF118C93\")))", parsed);
		//fail("Not yet implemented");
	}

}

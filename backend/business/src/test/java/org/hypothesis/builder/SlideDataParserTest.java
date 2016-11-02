package org.hypothesis.builder;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNull;

public class SlideDataParserTest {

	@Test
	public void test() {
		List<String> list = Arrays.asList(new String[10]);
		
		assertNull(list.get(0));
	}

}

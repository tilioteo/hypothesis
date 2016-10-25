package org.hypothesis.builder;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class SlideDataParserTest {

	@Test
	public void test() {
		List<String> list = Arrays.asList(new String[10]);
		
		assertNull(list.get(0));
	}

}

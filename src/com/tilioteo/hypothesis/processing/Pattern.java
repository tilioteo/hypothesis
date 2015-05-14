/**
 * 
 */
package com.tilioteo.hypothesis.processing;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.tilioteo.hypothesis.interfaces.ExchangeVariable;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
@SuppressWarnings("serial")
public class Pattern implements Formula {
	
	private static Logger log = Logger.getLogger(Pattern.class);

	private HashMap<Long, Nick> nickMap = new HashMap<Long, Nick>();

	public void addNick(int number, Nick nick) {
		if (nick != null)
			nickMap.put(getSlideSerial(number, nick.getSlideId()), nick);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean evaluate(Object input) {
		log.debug("evaluate::");
		Map<Long, Map<Integer, ExchangeVariable>> inputs = new HashMap<Long, Map<Integer,ExchangeVariable>>();
		if (input != null && inputs.getClass().isAssignableFrom(input.getClass())) {
			inputs = (Map<Long, Map<Integer, ExchangeVariable>>)input;
			
			boolean result = true;
			try {
				for (int i = 1; i <= nickMap.size(); ++i) {
					Nick nick = null;
					for (Long slideId : inputs.keySet()) {
						nick = nickMap.get(getSlideSerial(i, slideId));
						if (nick != null) {
							result = nick.pass(inputs.get(slideId));
							break;
						}
					}
					if (null == nick) {
						result = false;
					}
					
					if (!result) {
						break;
					}
				}
				return result;

			} catch (Exception e) {
				log.error(e.getMessage());
				// TODO: handle exception
			}
		}
		return false;
	}

	private Long getSlideSerial(int slideNumber, Long slideId) {

		final int prime1 = 443;
		final int prime2 = 773;

		Long result = (long) slideNumber;
		result = (prime1 * result + (slideId != null ? prime2
				* slideId.longValue() : 0));
		return result;
	}

}

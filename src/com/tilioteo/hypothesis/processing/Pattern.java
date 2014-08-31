/**
 * 
 */
package com.tilioteo.hypothesis.processing;

import java.util.HashMap;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import com.tilioteo.hypothesis.core.Pair;
import com.tilioteo.hypothesis.core.PairList;
import com.tilioteo.hypothesis.entity.Slide;

/**
 * @author Kamil Morong - Hypothesis
 * 
 */
public class Pattern extends AbstractBaseFormula {
	
	private static Logger log = Logger.getLogger(Pattern.class);

	private HashMap<Long, Nick> nickMap = new HashMap<Long, Nick>();

	public void addNick(int number, Nick nick) {
		// nicks.add(nick);
		if (nick != null)
			nickMap.put(getSlideSerial(number, nick.getSlideId()), nick);
	}

	// private List<Nick> nicks = new LinkedList<Nick>();

	@Override
	@SuppressWarnings("unchecked")
	public boolean evaluate(Object... parameters) {
		log.debug("evaluate::");
		if (parameters.length == 1
				&& parameters[0] instanceof PairList<?, ?>) {
			boolean result = true;
			try {
				int i = 0;
				ListIterator<Pair<Slide, Object>> listIterator = ((PairList<Slide, Object>) parameters[0])
						.listIterator();
				while (listIterator.hasNext()) {
					Pair<Slide, Object> objectPair = listIterator.next();
					Nick nick = nickMap.get(getSlideSerial(++i, objectPair
							.getFirst().getId()));
					if (nick != null) {
						result = nick.pass(objectPair.getSecond());
					} else
						result = false;
					if (result)
						break;
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

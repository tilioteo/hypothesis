/**
 * Apache Licence Version 2.0
 * Please read the LICENCE file
 */
package org.hypothesis.servlet;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Kamil Morong, Tilioteo Ltd
 * 
 *         Hypothesis
 *
 */
@SuppressWarnings("serial")
public class BroadcastService implements Serializable {
	static final ExecutorService executorService = Executors.newSingleThreadExecutor();

	@FunctionalInterface
	public interface BroadcastListener {
		void receiveBroadcast(final String message);
	}

	private static final List<BroadcastListener> listeners = new LinkedList<>();

	public static synchronized void register(BroadcastListener listener) {
		listeners.add(listener);
	}

	public static synchronized void unregister(BroadcastListener listener) {
		listeners.remove(listener);
	}

	public static synchronized void broadcast(final String message) {
		for (final BroadcastListener listener : listeners)
			executorService.execute(() -> listener.receiveBroadcast(message));
	}

	public static synchronized void broadcastExcept(final BroadcastListener exceptListener, final String message) {
		for (final BroadcastListener listener : listeners)
			if (listener != exceptListener) {
				executorService.execute(() -> listener.receiveBroadcast(message));
			}
	}
}
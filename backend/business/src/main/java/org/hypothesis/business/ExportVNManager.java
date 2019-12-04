package org.hypothesis.business;

import org.apache.commons.lang3.StringUtils;
import org.hypothesis.business.emul.UVNClient;
import org.hypothesis.configuration.ConfigManager;
import org.hypothesis.context.HibernateUtil;

public class ExportVNManager {

	private final ThreadGroup threadGroup = ThreadUtility.createExportGroup();
	private final UVNClient client;

	public ExportVNManager() {
		client = new UVNClient();
	}

	public void exportSingleTestScore(Long id) {
		final String path = ConfigManager.get().getValue("exportScorePath");

		if (StringUtils.isNotBlank(path)) {
			SimpleExportRunnable runnable = new SimpleExportScoreRunnableImpl(id, path, (fileName) -> {
				HibernateUtil.closeCurrent();
				client.anounceTestScore(fileName);
			});

			ExportThread currentExport = new ExportThread(threadGroup, runnable);
			currentExport.start();
		}
	}

}

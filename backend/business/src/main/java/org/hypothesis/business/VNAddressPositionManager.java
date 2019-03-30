package org.hypothesis.business;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.hypothesis.configuration.ConfigManager;

public class VNAddressPositionManager {

	public static Properties getProperties() {
		Properties properties = new Properties();

		String filePath = ConfigManager.get().getValue("vnPositionFilePath");
		Path path = Paths.get(filePath);
		if (Files.exists(path)) {
			try {
				properties.load(new FileReader(path.toFile()));

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return properties;
	}

}

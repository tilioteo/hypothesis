package org.hypothesis.ws.utility;

import org.hypothesis.data.dto.PackDto;
import org.hypothesis.ws.entity.Pack;

public class ConversionUtility {

	public static Pack dtoToWs(PackDto dto) {
		if (dto != null) {
			Pack pack = new Pack();
			pack.setId(dto.getId());
			pack.setName(dto.getName());
			pack.setDescription(dto.getDescription());
			pack.setNote(dto.getNote());

			return pack;
		}

		return null;
	}

}

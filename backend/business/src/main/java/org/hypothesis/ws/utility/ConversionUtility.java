package org.hypothesis.ws.utility;

import org.hypothesis.ws.entity.Pack;

public class ConversionUtility {
	
	public static Pack entityToWs(org.hypothesis.data.model.Pack entity) {
		if (entity != null) {
			Pack pack = new Pack();
			pack.setId(entity.getId());
			pack.setName(entity.getName());
			pack.setDescription(entity.getDescription());
			pack.setNote(entity.getNote());
			
			return pack;
		}
		
		return null;
	}

}

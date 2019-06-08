package org.hypothesis.data.service.impl;

import org.hypothesis.data.dto.SlideDto;
import org.hypothesis.data.dto.TemplateDto;
import org.hypothesis.data.model.Slide;
import org.hypothesis.data.model.SlideTemplate;

class SlideConverter {

	public SlideDto toDto(Slide slide) {
		if (slide == null) {
			return null;
		}

		final SlideDto dto = new SlideDto();

		dto.setId(slide.getId());
		dto.setData(slide.getData());
		dto.setNote(slide.getNote());
		dto.setTemplate(toDto(slide.getTemplate()));

		return dto;
	}

	private TemplateDto toDto(SlideTemplate entity) {
		final TemplateDto dto = new TemplateDto();

		dto.setId(entity.getId());
		dto.setData(entity.getData());
		dto.setNote(entity.getNote());

		return dto;
	}

}

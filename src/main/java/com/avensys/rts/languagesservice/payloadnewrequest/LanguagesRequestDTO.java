package com.avensys.rts.languagesservice.payloadnewrequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LanguagesRequestDTO {

	private String entityType;
	private Integer entityId;

	// Form Submission
	private String formData;
	private Integer formId;

	private Long createdBy;
	private Long updatedBy;
}

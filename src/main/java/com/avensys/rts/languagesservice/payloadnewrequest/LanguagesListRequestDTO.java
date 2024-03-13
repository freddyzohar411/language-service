package com.avensys.rts.languagesservice.payloadnewrequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LanguagesListRequestDTO {
	private List<LanguagesRequestDTO> languagesList;
}

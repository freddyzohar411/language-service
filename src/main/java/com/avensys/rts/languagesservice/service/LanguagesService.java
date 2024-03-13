package com.avensys.rts.languagesservice.service;

import java.util.List;

import com.avensys.rts.languagesservice.payloadnewrequest.LanguagesListRequestDTO;
import com.avensys.rts.languagesservice.payloadnewrequest.LanguagesRequestDTO;
import com.avensys.rts.languagesservice.payloadnewresponse.LanguagesResponseDTO;

public interface LanguagesService {

    LanguagesResponseDTO createLanguages(LanguagesRequestDTO contactNewRequestDTO);

    void createLanguagesList(LanguagesListRequestDTO languagesListRequestDTO);

    LanguagesResponseDTO getLanguagesById(Integer id);

    LanguagesResponseDTO updateLanguages(Integer id, LanguagesRequestDTO contactNewRequestDTO);

    void deleteLanguages(Integer id);

    List<LanguagesResponseDTO> getLanguagesByEntityTypeAndEntityId(String entityType, Integer entityId);

    void deleteLanguagesByEntityTypeAndEntityId(String entityType, Integer entityId);
}

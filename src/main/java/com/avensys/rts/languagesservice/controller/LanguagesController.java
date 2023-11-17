package com.avensys.rts.languagesservice.controller;

import org.slf4j.Logger;


import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.avensys.rts.languagesservice.constant.MessageConstants;
import com.avensys.rts.languagesservice.payloadnewrequest.LanguagesRequestDTO;
import com.avensys.rts.languagesservice.payloadnewresponse.LanguagesResponseDTO;
import com.avensys.rts.languagesservice.service.LanguagesServiceImpl;
import com.avensys.rts.languagesservice.util.JwtUtil;
import com.avensys.rts.languagesservice.util.ResponseUtil;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LanguagesController {

	private final Logger log = LoggerFactory.getLogger(LanguagesController.class);
	private final LanguagesServiceImpl languagesServiceImpl;
	private final MessageSource messageSource;

	@Autowired
	private JwtUtil jwtUtil;

	public LanguagesController(LanguagesServiceImpl languagesServiceImpl, MessageSource messageSource) {
		this.languagesServiceImpl = languagesServiceImpl;
		this.messageSource = messageSource;
	}

	@PostMapping("/languages")
	public ResponseEntity<Object> createLanguages(@Valid @RequestBody LanguagesRequestDTO languagesRequestDTO,
			@RequestHeader(name = "Authorization") String token) {
		log.info("Create Languages : Controller ");
		Long userId = jwtUtil.getUserId(token);
		languagesRequestDTO.setCreatedBy(userId);
		languagesRequestDTO.setUpdatedBy(userId);
		LanguagesResponseDTO languagesResponseDTO = languagesServiceImpl.createLanguages(languagesRequestDTO);
		return ResponseUtil.generateSuccessResponse(languagesResponseDTO, HttpStatus.CREATED,
				messageSource.getMessage(MessageConstants.MESSAGE_CREATED, null, LocaleContextHolder.getLocale()));
	}

	@GetMapping("/languages/entity/{entityType}/{entityId}")
	public ResponseEntity<Object> getLanguagesByEntityTypeAndEntityId(@PathVariable String entityType,
			@PathVariable Integer entityId) {
		log.info("Get Languages by entity type and entity id : Controller ");
		return ResponseUtil.generateSuccessResponse(
				languagesServiceImpl.getLanguagesByEntityTypeAndEntityId(entityType, entityId), HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	@DeleteMapping("/languages/{id}")
	public ResponseEntity<Object> deleteLanguages(@PathVariable Integer id) {
		log.info("Delete Languages : Controller ");
		languagesServiceImpl.deleteLanguages(id);
		return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	@PutMapping("/languages/{id}")
	public ResponseEntity<Object> updateLanguages(@PathVariable Integer id,
			@Valid @RequestBody LanguagesRequestDTO languagesRequestDTO,
			@RequestHeader(name = "Authorization") String token) {
		log.info("Update Languages : Controller ");
		Long userId = jwtUtil.getUserId(token);
		languagesRequestDTO.setUpdatedBy(userId);
		LanguagesResponseDTO languagesResponseDTO = languagesServiceImpl.updateLanguages(id, languagesRequestDTO);
		return ResponseUtil.generateSuccessResponse(languagesResponseDTO, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	/**
	 * This endpoint is to delete Languages by entity type and entity id
	 * 
	 * @param entityType
	 * @param entityId
	 * @return
	 */
	@DeleteMapping("/languages/entity/{entityType}/{entityId}")
	public ResponseEntity<Object> deleteLanguagesByEntityTypeAndEntityId(@PathVariable String entityType,
			@PathVariable Integer entityId) {
		log.info("Delete Languages by entity type and entity id : Controller ");
		languagesServiceImpl.deleteLanguagesByEntityTypeAndEntityId(entityType, entityId);
		return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}
}

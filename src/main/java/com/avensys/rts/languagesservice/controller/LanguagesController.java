package com.avensys.rts.languagesservice.controller;

import com.avensys.rts.languagesservice.payloadnewrequest.LanguagesListRequestDTO;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.avensys.rts.languagesservice.constant.MessageConstants;
import com.avensys.rts.languagesservice.payloadnewrequest.LanguagesRequestDTO;
import com.avensys.rts.languagesservice.payloadnewresponse.LanguagesResponseDTO;
import com.avensys.rts.languagesservice.service.LanguagesServiceImpl;
import com.avensys.rts.languagesservice.util.JwtUtil;
import com.avensys.rts.languagesservice.util.ResponseUtil;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/languages")
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

	@PostMapping("/add")
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

	@PostMapping("/add/list")
	public ResponseEntity<Object> createLanguagesList(@RequestBody LanguagesListRequestDTO languagesListRequestDTO,
			@RequestHeader(name = "Authorization") String token) {
		log.info("Create Languages : Controller ");
		Long userId = jwtUtil.getUserId(token);
		languagesListRequestDTO.getLanguagesList().forEach(languagesRequestDTO -> {
			languagesRequestDTO.setCreatedBy(userId);
			languagesRequestDTO.setUpdatedBy(userId);
		});
		languagesServiceImpl.createLanguagesList(languagesListRequestDTO);
		return ResponseUtil.generateSuccessResponse(null, HttpStatus.CREATED,
				messageSource.getMessage(MessageConstants.MESSAGE_CREATED, null, LocaleContextHolder.getLocale()));
	}

	@GetMapping("/entity/{entityType}/{entityId}")
	public ResponseEntity<Object> getLanguagesByEntityTypeAndEntityId(@PathVariable String entityType,
			@PathVariable Integer entityId) {
		log.info("Get Languages by entity type and entity id : Controller ");
		return ResponseUtil.generateSuccessResponse(
				languagesServiceImpl.getLanguagesByEntityTypeAndEntityId(entityType, entityId), HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteLanguages(@PathVariable Integer id) {
		log.info("Delete Languages : Controller ");
		languagesServiceImpl.deleteLanguages(id);
		return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	@PutMapping("/{id}")
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
	@DeleteMapping("/entity/{entityType}/{entityId}")
	public ResponseEntity<Object> deleteLanguagesByEntityTypeAndEntityId(@PathVariable String entityType,
			@PathVariable Integer entityId) {
		log.info("Delete Languages by entity type and entity id : Controller ");
		languagesServiceImpl.deleteLanguagesByEntityTypeAndEntityId(entityType, entityId);
		return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}
}

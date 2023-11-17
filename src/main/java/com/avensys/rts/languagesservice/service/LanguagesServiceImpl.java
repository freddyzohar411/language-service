package com.avensys.rts.languagesservice.service;

import java.util.List;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.avensys.rts.languagesservice.APIClient.FormSubmissionAPIClient;
import com.avensys.rts.languagesservice.APIClient.UserAPIClient;
import com.avensys.rts.languagesservice.customresponse.HttpResponse;
import com.avensys.rts.languagesservice.entity.LanguagesEntity;
import com.avensys.rts.languagesservice.entity.UserEntity;
import com.avensys.rts.languagesservice.payloadnewrequest.LanguagesRequestDTO;
import com.avensys.rts.languagesservice.payloadnewrequest.FormSubmissionsRequestDTO;
import com.avensys.rts.languagesservice.payloadnewresponse.LanguagesResponseDTO;
import com.avensys.rts.languagesservice.payloadnewresponse.FormSubmissionsResponseDTO;
import com.avensys.rts.languagesservice.repository.LanguagesRepository;
import com.avensys.rts.languagesservice.repository.UserRepository;
import com.avensys.rts.languagesservice.util.MappingUtil;

import jakarta.transaction.Transactional;

@Service
public class LanguagesServiceImpl implements LanguagesService {

	private final String CANDIDATE_LANGUAGES_TYPE = "candidate_languages";

	private final Logger log = LoggerFactory.getLogger(LanguagesServiceImpl.class);
	private final LanguagesRepository languagesRepository;

	@Autowired
	private UserAPIClient userAPIClient;

	@Autowired
	private FormSubmissionAPIClient formSubmissionAPIClient;

	@Autowired
	private UserRepository userRepository;

	public LanguagesServiceImpl(LanguagesRepository languagesRepository, UserAPIClient userAPIClient,
			FormSubmissionAPIClient formSubmissionAPIClient) {
		this.languagesRepository = languagesRepository;
		this.userAPIClient = userAPIClient;
		this.formSubmissionAPIClient = formSubmissionAPIClient;
	}

	@Override
	@Transactional
	public LanguagesResponseDTO createLanguages(LanguagesRequestDTO languagesRequestDTO) {
		log.info("Creating Languages: service");
		System.out.println("Languages: " + languagesRequestDTO);
		LanguagesEntity savedLanguagesEntity = languagesRequestDTOToLanguagesEntity(languagesRequestDTO);

		// Save form data to form submission microservice
		FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
		formSubmissionsRequestDTO.setUserId(languagesRequestDTO.getCreatedBy());
		formSubmissionsRequestDTO.setFormId(languagesRequestDTO.getFormId());
		formSubmissionsRequestDTO
				.setSubmissionData(MappingUtil.convertJSONStringToJsonNode(languagesRequestDTO.getFormData()));
		formSubmissionsRequestDTO.setEntityId(savedLanguagesEntity.getId());
		formSubmissionsRequestDTO.setEntityType(languagesRequestDTO.getEntityType());
		HttpResponse formSubmissionResponse = formSubmissionAPIClient.addFormSubmission(formSubmissionsRequestDTO);
		FormSubmissionsResponseDTO formSubmissionData = MappingUtil
				.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);

		savedLanguagesEntity.setFormSubmissionId(formSubmissionData.getId());

		return languagesEntityToLanguagesResponseDTO(savedLanguagesEntity);
	}

	@Override
	public LanguagesResponseDTO getLanguagesById(Integer id) {
		LanguagesEntity languagesEntityFound = languagesRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Language not found"));
		return languagesEntityToLanguagesResponseDTO(languagesEntityFound);
	}

	@Override
	@Transactional
	public LanguagesResponseDTO updateLanguages(Integer id, LanguagesRequestDTO languagesRequestDTO) {
		LanguagesEntity languagesEntityFound = languagesRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Language not found"));
		LanguagesEntity updatedLanguagesEntity = updateLanguagesRequestDTOToLanguagesEntity(languagesEntityFound,
				languagesRequestDTO);

		// Update form submission
		FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
		formSubmissionsRequestDTO.setUserId(languagesRequestDTO.getUpdatedBy());
		formSubmissionsRequestDTO.setFormId(languagesRequestDTO.getFormId());
		formSubmissionsRequestDTO
				.setSubmissionData(MappingUtil.convertJSONStringToJsonNode(languagesRequestDTO.getFormData()));
		formSubmissionsRequestDTO.setEntityId(updatedLanguagesEntity.getId());
		formSubmissionsRequestDTO.setEntityType(languagesRequestDTO.getEntityType());
		HttpResponse formSubmissionResponse = formSubmissionAPIClient
				.updateFormSubmission(updatedLanguagesEntity.getFormSubmissionId(), formSubmissionsRequestDTO);
		FormSubmissionsResponseDTO formSubmissionData = MappingUtil
				.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);

		updatedLanguagesEntity.setFormSubmissionId(formSubmissionData.getId());
		return languagesEntityToLanguagesResponseDTO(updatedLanguagesEntity);
	}

	@Override
	@Transactional
	public void deleteLanguages(Integer id) {
		LanguagesEntity languagesEntityFound = languagesRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Languages not found"));
		languagesRepository.delete(languagesEntityFound);
	}

	@Override
	public List<LanguagesResponseDTO>getLanguagesByEntityTypeAndEntityId(String entityType, Integer entityId) {
		List<LanguagesEntity> languagesEntityList = languagesRepository.findByEntityTypeAndEntityId(entityType,
				entityId);
		List<LanguagesResponseDTO> languagesResponseDTOList = languagesEntityList.stream()
				.map(this::languagesEntityToLanguagesResponseDTO).toList();
		return languagesResponseDTOList;
	}

	@Override
	@Transactional
	public void deleteLanguagesByEntityTypeAndEntityId(String entityType, Integer entityId) {
		List<LanguagesEntity> languagesEntityList = languagesRepository.findByEntityTypeAndEntityId(entityType,
				entityId);
		if (!languagesEntityList.isEmpty()) {
			// Delete each Language form submission before deleting
			languagesEntityList.forEach(languagesEntity -> {
				formSubmissionAPIClient.deleteFormSubmission(languagesEntity.getFormSubmissionId());
				languagesRepository.delete(languagesEntity);
			});
		}
	}

	private LanguagesResponseDTO languagesEntityToLanguagesResponseDTO(LanguagesEntity languagesEntity) {
		LanguagesResponseDTO languagesResponseDTO = new LanguagesResponseDTO();
		languagesResponseDTO.setId(languagesEntity.getId());
		languagesResponseDTO.setCreatedAt(languagesEntity.getCreatedAt());
		languagesResponseDTO.setUpdatedAt(languagesEntity.getUpdatedAt());
		languagesResponseDTO.setEntityType(languagesEntity.getEntityType());
		languagesResponseDTO.setEntityId(languagesEntity.getEntityId());
		languagesResponseDTO.setFormId(languagesEntity.getFormId());
		languagesResponseDTO.setFormSubmissionId(languagesEntity.getFormSubmissionId());

		// Get created by User data from user microservice
		Optional<UserEntity> userEntity = userRepository.findById(languagesEntity.getCreatedBy());
		UserEntity userData = userEntity.get();
		languagesResponseDTO.setCreatedBy(userData.getFirstName() + " " + userData.getLastName());

		// Get updated by user data from user microservice
		if (languagesEntity.getUpdatedBy() == languagesEntity.getCreatedBy()) {
			languagesResponseDTO.setUpdatedBy(userData.getFirstName() + " " + userData.getLastName());
		} else {
			userEntity = userRepository.findById(languagesEntity.getUpdatedBy());
			userData = userEntity.get();
			languagesResponseDTO.setUpdatedBy(userData.getFirstName() + " " + userData.getLastName());
		}

		// Get form submission data
		HttpResponse formSubmissionResponse = formSubmissionAPIClient
				.getFormSubmission(languagesEntity.getFormSubmissionId());
		FormSubmissionsResponseDTO formSubmissionData = MappingUtil
				.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);
		languagesResponseDTO
				.setSubmissionData(MappingUtil.convertJsonNodeToJSONString(formSubmissionData.getSubmissionData()));
		return languagesResponseDTO;
	}

	private LanguagesEntity updateLanguagesRequestDTOToLanguagesEntity(LanguagesEntity languagesEntity,
			LanguagesRequestDTO languagesRequestDTO) {
		languagesEntity.setEntityType(languagesRequestDTO.getEntityType());
		languagesEntity.setEntityId(languagesRequestDTO.getEntityId());
		languagesEntity.setUpdatedBy(languagesRequestDTO.getUpdatedBy());
		languagesEntity.setFormId(languagesRequestDTO.getFormId());
		return languagesRepository.save(languagesEntity);
	}

	private LanguagesEntity languagesRequestDTOToLanguagesEntity(LanguagesRequestDTO languagesRequestDTO) {
		LanguagesEntity languagesEntity = new LanguagesEntity();
		languagesEntity.setEntityType(languagesRequestDTO.getEntityType());
		languagesEntity.setEntityId(languagesRequestDTO.getEntityId());
		languagesEntity.setCreatedBy(languagesRequestDTO.getCreatedBy());
		languagesEntity.setUpdatedBy(languagesRequestDTO.getUpdatedBy());
		languagesEntity.setFormId(languagesRequestDTO.getFormId());
		return languagesRepository.save(languagesEntity);
	}

}

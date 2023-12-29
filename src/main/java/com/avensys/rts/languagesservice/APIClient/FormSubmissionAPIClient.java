package com.avensys.rts.languagesservice.APIClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.avensys.rts.languagesservice.customresponse.HttpResponse;
import com.avensys.rts.languagesservice.interceptor.JwtTokenInterceptor;
import com.avensys.rts.languagesservice.payloadnewrequest.FormSubmissionsRequestDTO;

@FeignClient(name = "form-service", url = "${api.form-submission.url}", configuration = JwtTokenInterceptor.class)
public interface FormSubmissionAPIClient {
    @PostMapping("/form-submissions")
    HttpResponse addFormSubmission(@RequestBody FormSubmissionsRequestDTO formSubmissionsRequestDTO);

    @GetMapping("/form-submissions/{formSubmissionId}")
    HttpResponse getFormSubmission(@PathVariable int formSubmissionId);

    @PutMapping("/form-submissions/{formSubmissionId}")
    HttpResponse updateFormSubmission(@PathVariable int formSubmissionId, @RequestBody FormSubmissionsRequestDTO formSubmissionsRequestDTO);

    @DeleteMapping("/form-submissions/{formSubmissionId}")
    HttpResponse deleteFormSubmission(@PathVariable int formSubmissionId);

}

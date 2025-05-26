package com.both.testing_pilot_backend.service.impl;

import com.both.testing_pilot_backend.dto.request.RequestRequest;
import com.both.testing_pilot_backend.exceptions.NotFoundException;
import com.both.testing_pilot_backend.model.Request;
import com.both.testing_pilot_backend.repository.RequestRepository;
import com.both.testing_pilot_backend.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    @Override
    public Request createRequest(RequestRequest requestDto) {
        Request request = Request.builder()
                .name(requestDto.getName())
                .collectionId(requestDto.getCollectionId())
                .method(requestDto.getMethod())
                .details(requestDto.getDetails())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return requestRepository.save(request);
    }

    @Override
    public Request getRequestById(UUID requestId) {
        Request request = requestRepository.findById(requestId);
        if (request == null) {
            throw new NotFoundException("Request not found with ID: " + requestId);
        }
        return request;
    }

    @Override
    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    @Override
    public List<Request> getRequestsByCollectionId(UUID collectionId) {
        return requestRepository.findByCollectionId(collectionId);
    }

    @Override
    public Request updateRequest(UUID requestId, RequestRequest requestDto) {
        Request existingRequest = requestRepository.findById(requestId);
        if (existingRequest == null) {
            throw new NotFoundException("Request not found with ID: " + requestId);
        }

        existingRequest.setName(requestDto.getName());
        existingRequest.setCollectionId(requestDto.getCollectionId());
        existingRequest.setMethod(requestDto.getMethod());
        existingRequest.setDetails(requestDto.getDetails());
        existingRequest.setUpdatedAt(LocalDateTime.now());
        existingRequest.setId(requestId); // Ensure ID is set for update query

        return requestRepository.updateRequest(existingRequest);
    }

    @Override
    public void deleteRequest(UUID requestId) {
        if (requestRepository.findById(requestId) == null) {
            throw new NotFoundException("Request not found with ID: " + requestId);
        }
        requestRepository.deleteById(requestId);
    }
}

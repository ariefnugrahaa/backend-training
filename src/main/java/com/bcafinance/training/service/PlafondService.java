package com.bcafinance.training.service;

import com.bcafinance.training.dto.request.PlafondRequest;
import com.bcafinance.training.dto.response.PlafondResponse;
import com.bcafinance.training.entity.Plafond;
import com.bcafinance.training.repository.PlafondRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlafondService {

    private final PlafondRepository plafondRepository;

    public List<PlafondResponse> getAll() {
        return plafondRepository.findAll().stream()
                .map(p -> PlafondResponse.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .description(p.getDescription())
                        .type(p.getType())
                        .maxAmount(p.getMaxAmount())
                        .interestRate(p.getInterestRate())
                        .build())
                .collect(Collectors.toList());
    }

    public PlafondResponse create(PlafondRequest request) {
        Plafond plafond = Plafond.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .maxAmount(request.getMaxAmount())
                .interestRate(request.getInterestRate())
                .build();
        Plafond saved = plafondRepository.save(plafond);
        return PlafondResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .description(saved.getDescription())
                .type(saved.getType())
                .maxAmount(saved.getMaxAmount())
                .interestRate(saved.getInterestRate())
                .build();
    }
}

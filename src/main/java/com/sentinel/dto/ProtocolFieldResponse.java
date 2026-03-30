package com.sentinel.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProtocolFieldResponse {

    private Long id;
    private String fieldKey;
    private String fieldLabel;
    private Boolean required;
    private String fieldType;
}
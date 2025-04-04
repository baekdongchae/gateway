package com.hanait.gateway.model;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TermsVersionId implements Serializable {
    private Terms terms;
    private String termsVersion;
}

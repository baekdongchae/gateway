package com.hanait.gateway.model;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TermsAgreementId implements Serializable {
    private User user;
    private Terms terms;
}

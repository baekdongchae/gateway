package com.hanait.gateway.model;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipientCaregiverId implements Serializable {
    private User caregiver;
    private User recipient;
}

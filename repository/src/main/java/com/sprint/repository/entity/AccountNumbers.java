package com.sprint.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountNumbers {
    @Column(name = "primary_account_number")
    String primaryAccountNumber;

    @Column(name = "secondary_account_number")
    String secondaryAccountNumber;
}

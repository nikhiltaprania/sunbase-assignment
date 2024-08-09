package com.sunbaseassignment.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Embeddable
public class CustomerAddress {
    private String street;
    private String address;
    private String city;
    private String state;
}
package com.wyme.hotail.modules.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wyme.hotail.shared.kernel.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_accounts")
public class UserAccount extends BaseEntity {

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    @JsonIgnore
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_partner")
    private Boolean isPartner = false;

    @Column(name = "is_admin")
    private Boolean isAdmin = false;

    @Column(name = "hotel_name")
    private String hotelName;

    @Column(name = "hotel_city")
    private String hotelCity;

    @Column(name = "hotel_phone")
    private String hotelPhone;

    @Column(name = "hotel_status")
    private String hotelStatus; // 'pending', 'approved', 'rejected'

    @Column(name = "verified")
    private Boolean verified = false;

    @Column(name = "verification_code")
    @JsonIgnore
    private String verificationCode;

    @Column(name = "provider")
    private String provider = "local"; // 'local' or 'google'

    @Column(name = "avatar_url")
    private String avatarUrl;
}

package com.dev.nbbang.auth.entity;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "MEMBER")
@Getter
public class Member {
    @Id
    @Column(name = "MEMBER_ID", nullable = false)
    private String memberId;

    @Column(name = "NICKNAME", nullable = false)
    private String nickname;

    @Column(name = "BANK_ID")
    private Integer bankId;

    @Column(name = "BANK_ACCOUNT")
    private String bankAccount;

    @Column(name = "GRADE")
    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Column(name = "POINT")
    private Long point;

    @Column(name = "EXP")
    private Long exp;

    @Column(name = "BILLING_KEY")
    private String billingKey;

    @Column(name = "PARTY_INVITE_YN")
    private String partyInviteYn;

    public Member() {
    }

    @Builder
    public Member(String memberId, String nickname) {
        this.memberId = memberId;
        this.nickname = nickname;
    }

    @PrePersist
    private void prePersist() {
        if (this.grade == null) grade = Grade.BRONZE;
        if (this.point == null) point = 0L;
        if (this.exp == null) exp = 0L;
        if (this.partyInviteYn == null) partyInviteYn = "Y";
    }
}

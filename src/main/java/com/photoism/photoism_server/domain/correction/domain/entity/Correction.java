package com.photoism.photoism_server.domain.correction.domain.entity;

import com.photoism.photoism_server.domain.correction.domain.enums.*;
import com.photoism.photoism_server.domain.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Correction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Shape shape;

    private Skin skin;

    private Long eyeSize; // 눈 크기

    private Long eyeAngle; // 눈 각도

    private Long ala; // 콧볼

    private Long nasalBridge; // 콧등

    private Long upper; // 윗입술

    private Long lower; // 아랫입술

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

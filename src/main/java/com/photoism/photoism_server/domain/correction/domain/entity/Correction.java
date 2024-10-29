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

    private Eyes eye;

    private Lips lips;

    private Nose nose;

    private Shape shape;

    private Skin skin;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

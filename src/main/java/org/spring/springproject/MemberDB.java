package org.spring.springproject;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="members")
@Data
public class MemberDB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private long id;
    @Column(name="name")
    private String name;
    @Column(name="age")
    private int age;
    @Enumerated(EnumType.STRING)
    @Column(name="gender")
    private Gender gender;
    @JoinColumn(name="family_id")
    @ManyToOne
    private FamilyDB familyId;
}

package org.spring.springproject;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="family")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FamilyDB {
    @Id
    @Column(name="id")
    private long id;
    @Column(name="name")
    private String name;
    @Column(name="origin")
    private String origin;
    @OneToOne
    @JoinColumn(name="head")
    private MemberDB head;
}

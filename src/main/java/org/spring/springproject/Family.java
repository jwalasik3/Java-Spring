package org.spring.springproject;

import jakarta.persistence.Entity;
import lombok.*;

import java.util.List;

@Data // same as sum of: @Getter + @Setter + @AllArgsConstructor + @toString
// @ToString // auto-cast into String, replaces method toString()
public class Family {
    private final String uid;
    private String familyName;
    private List<Member> members;

    public Family(String uid, String familyName, List<Member> members) {
        this.uid = uid;
        this.familyName = familyName;
        this.members = members;
    }
}

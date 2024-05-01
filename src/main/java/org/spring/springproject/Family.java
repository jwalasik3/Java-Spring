package org.spring.springproject;

import java.util.List;

public class Family {
    private final String uid;
    private String familyName;
    private List<Member> members;

    public Family(String uid, String familyName, List<Member> members) {
        this.uid = uid;
        this.familyName = familyName;
        this.members = members;
    }

    public String getUid() {
        return uid;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }
}

package org.spring.springproject;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

@RestController
@RequestMapping(value = "/api/v1/family")
public class FamilyController {

    List<Family> familyList = new ArrayList<>();
    List<Member> members = new ArrayList<>();

    @RequestMapping(value = "/getall", method = RequestMethod.GET)
    public List<Family> getAll() {
        members.add(new Member("Adam", 12, "male"));
        members.add(new Member("Ania", 22, "female"));
        members.add(new Member("Roman", 24, "male"));
        familyList.add(new Family(UUID.randomUUID().toString(), "Kowalski", members));
        familyList.add(new Family(UUID.randomUUID().toString(), "Nowak", members));
        return familyList;
    }

    @RequestMapping(value = "/getByName", method = RequestMethod.GET)
    public Family getByName(@RequestParam String familyName) {
        members.add(new Member("Adam", 12, "male"));
        members.add(new Member("Ania", 22, "female"));
        members.add(new Member("Roman", 24, "male"));
        familyList.add(new Family(UUID.randomUUID().toString(), "Kowalski", members));
        familyList.add(new Family(UUID.randomUUID().toString(), "Nowak", members));
        return familyList.stream().filter(family -> family.getFamilyName().equals(familyName)).findFirst().orElseThrow();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void createFamily(@RequestBody Family family, HttpServletResponse response) throws IOException {
        if(family.getFamilyName() != null && !family.getMembers().isEmpty()){
            familyList.add(family);
            response.sendError(HttpServletResponse.SC_OK, "The family has been added to the list");
            return;
        }
        response.sendError(HttpServletResponse.SC_CONFLICT, "Family name cannot be null and must contain at least one member");
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.PATCH, consumes = "application/json")
    public void editFamily(@RequestBody Map<Object, Object> fields, @PathVariable String id, HttpServletResponse response) throws IOException {
        Optional<Family> family = familyList.stream().filter(value -> value.getUid().equals(id)).findFirst();
        try {
            if (family.isPresent()) {
                fields.forEach((k, v) -> {
                    Field field = ReflectionUtils.findField(Family.class, (String) k);
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, family.get(), v);

                });
                response.sendError(HttpServletResponse.SC_OK, "Updated family info");
                return;
            }
        } catch (NullPointerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Fields are not correct");
            return;
        }
        response.sendError(HttpServletResponse.SC_NO_CONTENT, "There is no such family in the database");
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public void updateFamily(@PathVariable String id, @RequestBody Family family, HttpServletResponse response) throws IOException {
        for (int i = 0; i < familyList.size(); i++) {
            if (familyList.get(i).getUid().equals(id)) {
                familyList.set(i, family);
                response.sendError(HttpServletResponse.SC_OK, "Value updated");
                break;
            }
            if (familyList.size() - 1 == i) {
                familyList.add(family);
                response.sendError(HttpServletResponse.SC_OK, "Value has been added");
            }
        }
    }
}

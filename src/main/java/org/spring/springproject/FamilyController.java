package org.spring.springproject;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping(value = "/api/v1/family")
public class FamilyController {

    List<Family> familyList = new ArrayList<>();
    List<Member> members = new ArrayList<>();

    @PostConstruct
    public void loadFamily() {
        members.add(new Member("Adam", 12, "male"));
        members.add(new Member("Ania", 22, "female"));
        members.add(new Member("Roman", 24, "male"));
        familyList.add(new Family(UUID.randomUUID().toString(), "Kowalski", members));
        familyList.add(new Family(UUID.randomUUID().toString(), "Nowak", members));
    }

    @RequestMapping(value = "/getall", method = RequestMethod.GET)
    public List<Family> getAll(HttpServletResponse response) {
        response.setHeader("Length", String.valueOf(familyList.size()));
        Cookie cookie = new Cookie("length", String.valueOf(familyList.size()));
        cookie.setMaxAge(10); // setting cookie expiration for 10 seconds (learning purposes)
        response.addCookie(cookie);
        return familyList;
    }

    @RequestMapping(value = "/getByName", method = RequestMethod.GET)
    public Family getByName(@RequestParam String familyName) {
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

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public void deleteFamily(@PathVariable String id, HttpServletResponse response) throws IOException {
        Optional<Family> family = familyList.stream().filter(value -> value.getUid().equals(id)).findFirst();
        if (family.isPresent()) {
            familyList.remove(family.get());
            response.sendError(HttpServletResponse.SC_OK);
            return;
        }
        response.sendError(HttpServletResponse.SC_CONFLICT, "Family not found");
    }

    @RequestMapping(value = "/getALLRD", method = RequestMethod.GET)
    public ResponseEntity<Void> getALLRD() {
        URI location = URI.create("/api/v1/family/getall");
        return ResponseEntity.status(HttpStatus.FOUND).location(location).build();
    }

    @RequestMapping(value = "/google", method = RequestMethod.GET)
    public ResponseEntity<Void> getGoogle() {
        URI location = URI.create("https://google.com");
        return ResponseEntity.status(HttpStatus.FOUND).location(location).build();
    }

    @RequestMapping(value = "/getHeader", method = RequestMethod.GET)
    public void getHeader(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            System.out.println(headerName + ": " + headerValue);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                System.out.println(cookie.getName() + ": " + cookie.getValue());
            }
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile() throws IOException {
        File file = new File("src/main/resources/static/test.jpg");
        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @GetMapping(value = "/video")
    public StreamingResponseBody streamVideo(HttpServletResponse response) throws IOException {
        response.setContentType("video/mp4");
        InputStream videoFileStream = new FileInputStream(new File("src/main/resources/static/video.mp4"));
        return outputStream -> {
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = videoFileStream.read(data, 0, data.length)) != -1) {
                outputStream.write(data, 0, nRead);
            }
            videoFileStream.close();
        };
    }
}

package com.backend.controller;

import com.backend.util.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.backend.util.StaticData.CURRENT_MAP;
import static com.backend.util.StaticData.DATABASE_PATH;
import static com.backend.util.StaticData.VOICE_PATH;

@CrossOrigin
@RestController
public class HomeController {
    @ResponseBody
    @PostMapping(value = "/fact",
            produces = "application/json;charset=UTF-8")
    public void factorize() {
        File file_map = new File(DATABASE_PATH);
        File file_voice = new File(VOICE_PATH);
        for (File f: Objects.requireNonNull(file_map.listFiles())) {
            f.delete();
        }
        for (File f: Objects.requireNonNull(file_voice.listFiles())) {
            f.delete();
        }
    }

    @ResponseBody
    @PostMapping(value = "/save",
            produces = "application/json;charset=UTF-8")
    public void saveMap(HttpServletRequest request) {
        CURRENT_MAP = request.getParameter("map");
        System.out.println(CURRENT_MAP);
    }

    @ResponseBody
    @PostMapping(value = "/current",
            produces = "application/json;charset=UTF-8")
    public String getCurrentMap() throws JsonProcessingException {
        return JSONUtil.toJSON(CURRENT_MAP);
    }

    @ResponseBody
    @PostMapping(value = "/switch",
            produces = "application/json;charset=UTF-8")
    public String switchMap() throws JsonProcessingException {
        File file = new File(DATABASE_PATH);
        List<String> files = Arrays.stream(Objects.requireNonNull(file.list())).toList();
        files = files.stream().map(s -> s.replaceAll("\\.txt", "")).toList();
        files = files.stream().filter(s -> !s.isBlank()).toList();
        System.out.println(files);
        return JSONUtil.toJSON(files);
    }
}

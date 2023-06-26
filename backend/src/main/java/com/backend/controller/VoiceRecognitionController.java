package com.backend.controller;

import com.backend.VoiceRecognition;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static com.backend.util.StaticData.CURRENT_MAP;
import static com.backend.util.StaticData.DATABASE_PATH;

@CrossOrigin
@RestController
public class VoiceRecognitionController {
    private String getStartFinishPoint(String start, String finish) throws IOException {
        String fn = DATABASE_PATH + "\\" + CURRENT_MAP + ".txt";
        FileInputStream fis = new FileInputStream(fn);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        String[] data = br.readLine().replaceAll("\n", "").split(" ");
        ArrayList<String> waypoints = new ArrayList<>();
        for (String s : data) {
            waypoints.add(s.split(":")[0]);
        }
        int start_index = waypoints.indexOf(start) + 1;
        int finish_index = waypoints.indexOf(finish) + 1;
        if (start_index == 0 || finish_index == 0) {
            return "UNDEFINED";
        }
        return "n " + start_index + " " + finish_index;
    }

    private String getPoint(String start) throws IOException {
        String fn = DATABASE_PATH + "\\" + CURRENT_MAP + ".txt";
        FileInputStream fis = new FileInputStream(fn);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        String[] data = br.readLine().replaceAll("\n", "").split(" ");
        ArrayList<String> waypoints = new ArrayList<>();
        for (String s : data) {
            waypoints.add(s.split(":")[0]);
        }
        int index = waypoints.indexOf(start) + 1;
        if (index == 0) {
            return "UNDEFINED";
        }
        return "v " + index;
    }

    @ResponseBody
    @PostMapping(value = "/vr/upload",
            produces = "application/json;charset=UTF-8")
    public String wav2text(MultipartFile file) throws IOException {
        long sec = Long.parseLong(Objects.requireNonNull(file.getOriginalFilename())
                .replaceAll(".wav", ""));
        Instant in = Instant.ofEpochMilli(sec);
        ZonedDateTime zdt = in.atZone(ZoneId.systemDefault());
        LocalDateTime ldt = zdt.toLocalDateTime();
        String time = ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
        String wavPath = "C:\\Users\\alxzz\\Desktop\\backend\\VoiceRecords\\" + time + ".wav";
        File des = new File(wavPath);
        file.transferTo(des);
        String plaintext = VoiceRecognition.recognize(wavPath);
        System.out.println(plaintext);
        if (plaintext.length() > 1) {
            if (plaintext.contains("拿东西放到")) {
                String text = plaintext.substring(1, plaintext.length() - 1);
                String[] points = text.split("拿东西放到");
                if (points.length != 2) {
                    return plaintext;
                }
                System.out.println(Arrays.toString(points));
                return getStartFinishPoint(points[0], points[1]);
            }
            if (plaintext.length() >= 4 && plaintext.charAt(0) == '到' &&
                    plaintext.charAt(plaintext.length() - 2) == '去') {
                String point = plaintext.substring(1, plaintext.length() - 2);
                return getPoint(point);
            }
        }
        return plaintext;
    }
}

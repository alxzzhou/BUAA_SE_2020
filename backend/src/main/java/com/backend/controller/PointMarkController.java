package com.backend.controller;


import com.backend.util.FileUtil;
import com.backend.util.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.backend.util.StaticData.CURRENT_MAP;
import static com.backend.util.StaticData.DATABASE_PATH;
import static com.backend.util.StaticData.DEFAULT_HEIGHT;

@CrossOrigin
@RestController
public class PointMarkController {
    private void savePoint(List<String> names) throws IOException {
        String fn = DATABASE_PATH + "\\" + CURRENT_MAP + ".txt";
        System.out.println(fn);
        names = names.stream().map(s -> s + ":" + DEFAULT_HEIGHT).toList();
        System.out.println(String.join(" ", names));
        FileUtil.writeFile(fn, String.valueOf(String.join(" ", names)));
    }

    @ResponseBody
    @PostMapping(value = "/pm/check",
            produces = "application/json;charset=UTF-8")
    public String checkMapExist() throws JsonProcessingException {
        File database = new File(DATABASE_PATH);
        File[] files = database.listFiles();
        assert files != null;
        long num = Arrays.stream(files).filter(File::isFile).count();
        return JSONUtil.toJSON(num != 0);
    }

    @ResponseBody
    @PostMapping(value = "/pm/name",
            produces = "application/json;charset=UTF-8")
    public String pointNaming(HttpServletRequest request) throws IOException {
        String points = request.getParameter("input");
        ArrayList<String> pointsArray = new ArrayList<>(Arrays.asList(points.split(" ")));
        savePoint(pointsArray);
        System.out.println(pointsArray);
        return JSONUtil.toJSON(pointsArray);
    }
}

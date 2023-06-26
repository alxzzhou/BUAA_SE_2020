package com.backend.controller;

import com.backend.util.FileUtil;
import com.backend.util.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;

import static com.backend.util.StaticData.CURRENT_MAP;
import static com.backend.util.StaticData.DATABASE_PATH;

@CrossOrigin
@RestController
public class TableHeightController {
    @ResponseBody
    @PostMapping(value = "/th/get",
            produces = "application/json;charset=UTF-8")
    public String getHeights() throws IOException {
        String fn = DATABASE_PATH + "\\" + CURRENT_MAP + ".txt";
        FileInputStream fis = new FileInputStream(fn);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        String[] data = br.readLine().replaceAll("\n", "").split(" ");
        System.out.println(Arrays.toString(data));
        return JSONUtil.toJSON(data);
    }

    @ResponseBody
    @PostMapping(value = "/th/getOne",
            produces = "application/json;charset=UTF-8")
    public String getHeight(HttpServletRequest request) throws IOException {
        String fn = DATABASE_PATH + "\\" + CURRENT_MAP + ".txt";
        FileInputStream fis = new FileInputStream(fn);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        String[] data = br.readLine().replaceAll("\n", "").split(" ");
        System.out.println(Arrays.toString(data));
        int index = Integer.parseInt(request.getParameter("name"));
        if (index == 0) return "0";
        String res = data[index - 1].split(":")[1];
        System.out.println(res);
        return res;
    }

    @ResponseBody
    @PostMapping(value = "/th/set",
            produces = "application/json;charset=UTF-8")
    public void setHeights(HttpServletRequest request) throws IOException {
        String fn = DATABASE_PATH + "\\" + CURRENT_MAP + ".txt";
        String data = request.getParameter("data");
        FileUtil.writeFile(fn, data);
    }
}

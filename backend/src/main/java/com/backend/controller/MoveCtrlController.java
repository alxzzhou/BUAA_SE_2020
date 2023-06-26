package com.backend.controller;

import com.backend.SocketServer;
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
import java.util.ArrayList;
import java.util.Arrays;

import static com.backend.util.StaticData.CURRENT_MAP;
import static com.backend.util.StaticData.DATABASE_PATH;
import static com.backend.util.StaticData.ROS_HOST;

@CrossOrigin
@RestController
public class MoveCtrlController {
    private int getDestIndex(String dest) throws IOException {
        String fn = DATABASE_PATH + "\\" + CURRENT_MAP + ".txt";
        FileInputStream fis = new FileInputStream(fn);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        String[] data = br.readLine().replaceAll("\n", "").split(" ");
        ArrayList<String> waypoints = new ArrayList<>();
        for (String s : data) {
            waypoints.add(s.split(":")[0]);
        }
        return waypoints.indexOf(dest) + 1;
    }

    @ResponseBody
    @PostMapping(value = "/mc/sf",
            produces = "application/json;charset=UTF-8")
    public String getControlChar(HttpServletRequest request) throws IOException {
        String dest = request.getParameter("dest");
        System.out.println(dest);
        int index = getDestIndex(dest);
        return JSONUtil.toJSON(index);
    }

    @ResponseBody
    @PostMapping(value = "/mc/sfs",
            produces = "application/json;charset=UTF-8")
    public String getControlChars(HttpServletRequest request) throws IOException {
        String dest1 = request.getParameter("dest1");
        String dest2 = request.getParameter("dest2");
        int index1 = getDestIndex(dest1);
        int index2 = getDestIndex(dest2);
        int[] arr = new int[]{index1, index2};
        return JSONUtil.toJSON(arr);
    }

    @ResponseBody
    @PostMapping(value = "/mc/get",
            produces = "application/json;charset=UTF-8")
    public String getItems() throws IOException {
        String fn = DATABASE_PATH + "\\" + CURRENT_MAP + ".txt";
        FileInputStream fis = new FileInputStream(fn);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        String[] data = br.readLine().replaceAll("\n", "").split(" ");
        Object[] res = Arrays.stream(data).map(d -> d.split(":")[0]).toArray();
        System.out.println(Arrays.toString(res));
        return JSONUtil.toJSON(res);
    }
}

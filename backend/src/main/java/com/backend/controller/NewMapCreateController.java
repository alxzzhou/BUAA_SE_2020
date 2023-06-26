package com.backend.controller;

import com.backend.SocketServer;
import com.backend.util.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

import static com.backend.util.StaticData.CURRENT_MAP;
import static com.backend.util.StaticData.DATABASE_PATH;
import static com.backend.util.StaticData.ROS_HOST;

@CrossOrigin
@RestController
public class NewMapCreateController {
    private boolean createMap(String name) throws IOException {
        String fn = DATABASE_PATH + "\\" + name + ".txt";
        File file = new File(fn);
        boolean res =  file.createNewFile();
        System.out.println(res);
        return res;
    }

    @ResponseBody
    @PostMapping(value = "/nmc/ctrl",
            produces = "application/json;charset=UTF-8")
    public String getCtrlChar(HttpServletRequest request) {
        String s = request.getParameter("ch");
        System.out.println(s);
        SocketServer.sendData(ROS_HOST, s);
        return "type: 1";
    }

    @ResponseBody
    @PostMapping(value = "/nmc/create",
            produces = "application/json;charset=UTF-8")
    public String createNewMap(HttpServletRequest request) throws IOException {
        String name = request.getParameter("input");
        System.out.println(name);

        if (createMap(name)) {
            CURRENT_MAP = name;
            return JSONUtil.toJSON("SUCCESS");
        }
        else return JSONUtil.toJSON("FAILED");
    }
}

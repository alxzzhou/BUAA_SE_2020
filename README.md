# BUAA_SE_2020
北航计算机系2020级大三下嵌入式软工项目，本仓库为ROS机器人的前后端（用户界面）
## 1. `frontend`
- 保留了所有`node_modules`中的文件，不需要运行`yarn install`，运行后`vuetify`会出问题，需要手动改。
- 需要在`main.js`和`util.js`中修改机载电脑端的IP地址。
- `Webstorm`中直接运行即可。
## 2. `backend`
- 需要修改`StaticData`中`DATABASE_PATH`和`VOICE_PATH`的值。
- `IDEA`中直接运行即可。

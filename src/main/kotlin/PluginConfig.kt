package top.colter.mirai.plugin

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object PluginConfig : AutoSavePluginConfig("config") {
    // 登陆的QQ号
//    var loginQQId : Long by value()
    // 管理群,报错都会发送此群
    val adminGroup : Long by value()
    // 插件的数据路径 基于启动器根目录
    val basePath : String by value("/DynamicPlugin")

    //---------------动态检测----------------//
    val dynamic : MutableMap<String,String> by value(mutableMapOf(
        //动态检测总开关
        "enable" to "true",
        //访问间隔 单位:秒  范围:[6,∞]
        //这个间隔是每次访问b站api时就会触发
        "interval" to "15",
        //慢速模式开启时间段 不开启则填000-000
        //例：200..800就是凌晨2点到8点
        "lowSpeed" to "200-800",
        //是否保存动态图片
        "saveDynamicImage" to "true"
    ))

    //---------------直播检测----------------//
    val live : MutableMap<String,String> by value(mutableMapOf(
        //直播检测总开关
        "enable" to "true"
    ))

    //---------------百度翻译----------------//
    val baiduTranslate : Map<String,String> by value(mapOf(
        //是否开启百度翻译
        "enable" to "true",
        //百度翻译api密钥
        "APP_ID" to "",
        "SECURITY_KEY" to ""
    ))

    //---------------BiliBiliApi(BPI) B站API----------------//
    val BPI : Map<String,String> by value(mapOf(
        // 动态API
        "dynamic" to "https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/space_history?visitor_uid=1111111111&offset_dynamic_id=0&need_top=0&host_uid=",
        // 粉丝数API
        "followNum" to "https://api.bilibili.com/x/relation/stat?vmid=",
        // 直播状态API
        "liveStatus" to "https://api.live.bilibili.com/xlive/web-room/v1/index/getInfoByRoom?room_id=",
        // 直播id API
        "liveRoom" to "https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=",
        // 大航海数 需要参数 用户id:ruid 直播间id:roomid  eg: ruid=487550002&roomid=21811136
        "guard" to "https://api.live.bilibili.com/xlive/app-room/v2/guardTab/topList?page=1&page_size=1&",
        // cookie
        "COOKIE" to ""
    ))
//    // 获取b站动态API
//    val dynamicApi by value("https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/space_history?visitor_uid=1111111111&offset_dynamic_id=0&need_top=0&host_uid=")
//    // 获取b站粉丝数API
//    val followNumApi by value("https://api.bilibili.com/x/relation/stat?vmid=")
//    // 获取直播状态API
//    val liveStatusApi by value("https://api.live.bilibili.com/xlive/web-room/v1/index/getInfoByRoom?room_id=")
//    // 获取直播id API
//    val liveRoomApi by value("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=")
//    // 大航海数 需要参数 用户id:ruid 直播间id:roomid  eg: ruid=487550002&roomid=21811136
//    val guardApi by value("https://api.live.bilibili.com/xlive/app-room/v2/guardTab/topList?page=1&page_size=1&")
//    // b站cookie
//    val COOKIE by value("")
}
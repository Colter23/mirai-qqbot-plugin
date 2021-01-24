package top.colter.mirai.plugin

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object PluginConfig : AutoSavePluginConfig("config") {
    // 登陆的QQ号
    var loginQQId : Long by value()
    // 管理群 私聊bot,报错都会发送此群
    val adminGroup : Long by value()

    //百度翻译api密钥
    val APP_ID by value("")
    val SECURITY_KEY by value("")

    val emojiNum : Int by value(88)

    // 插件的数据路径 基于启动器根目录
    val basePath : String by value("/DynamicPlugin")

    // 获取b站动态API时访问的UID (通过哪个用户访问，会有一些特化信息，比如自己的关注列表谁点赞了等 需要登陆)
    val visitorUID by value(111111111)
    // 获取b站动态API
    val dynamicApi by value("https://api.vc.bilibili.com/dynamic_svr/v1/dynamic_svr/space_history?visitor_uid=$visitorUID&offset_dynamic_id=0&need_top=0&host_uid=")
    // 获取b站粉丝数API
    val followNumApi by value("https://api.bilibili.com/x/relation/stat?vmid=")
    // 获取直播状态API
    val liveStatusApi by value("https://api.live.bilibili.com/xlive/web-room/v1/index/getInfoByRoom?room_id=")
    // 获取直播id API
    val liveRoomApi by value("https://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid=")
    // 大航海数 需要参数 用户id:ruid 直播间id:roomid  eg: ruid=487550002&roomid=21811136
    val guardApi by value("https://api.live.bilibili.com/xlive/app-room/v2/guardTab/topList?page=1&page_size=1&")
    // b站cookie
    val COOKIE by value("")
}
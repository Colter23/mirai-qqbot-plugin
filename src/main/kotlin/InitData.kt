package top.colter.mirai.plugin

import kotlinx.coroutines.delay
import top.colter.mirai.plugin.PluginConfig.BPI
import top.colter.mirai.plugin.bean.User
import top.colter.mirai.plugin.utils.httpGet

suspend fun InitData() {
    PluginData.userData = mutableListOf<User>()
    PluginData.followList.forEach { uid ->
        PluginData.userData.add(getFollowInfo(uid))
    }
}

suspend fun getFollowInfo(uid:String): User{
    delay(2000)
    val res = httpGet(BPI["dynamic"]+uid,BPI["COOKIE"]!!).getJSONObject("data").getJSONArray("cards").getJSONObject(0)
    val userProfile = res.getJSONObject("desc").getJSONObject("user_profile")
    val name = userProfile.getJSONObject("info").getString("uname")
    val user : User = User()
    user.uid = uid
    user.name = name
    user.dynamicId = res.getJSONObject("desc").getBigInteger("dynamic_id").toString()
    try {
        user.liveStatus = res.getJSONObject("display").getJSONObject("live_info").getInteger("live_status")
    }catch (e:Exception){
        user.liveStatus = 0
    }

    val face = userProfile.getJSONObject("info").getString("face")
    val pendant = userProfile.getJSONObject("pendant").getString("image")

    delay(2000)
    val liveRoom = httpGet(BPI["liveRoom"]+uid,BPI["COOKIE"]!!).getJSONObject("data").getBigInteger("roomid").toString()
    user.liveRoom = liveRoom

//    map["fan"] = ""
//    map["guard"] = ""
//    generateImg(uid,name,face,pendant)

    delay(2000)
    user.fan = httpGet(BPI["followNum"] + uid).getJSONObject("data").getInteger("follower")

    delay(2000)
    user.guard = httpGet(BPI["guard"] +"ruid="+uid+"&roomid="+liveRoom).getJSONObject("data").getJSONObject("info").getInteger("num")

    return user
}
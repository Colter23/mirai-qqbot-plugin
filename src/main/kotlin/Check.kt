package top.colter.mirai.plugin

import kotlinx.coroutines.delay
import net.mamoe.mirai.Bot
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.info
import top.colter.mirai.plugin.bean.Dynamic
import top.colter.mirai.plugin.utils.buildSummaryImage
import top.colter.mirai.plugin.utils.httpGet
import java.io.File
import java.text.SimpleDateFormat

suspend fun check(bot: Bot){
    while (true){
        try {
            PluginMain.logger.info {"->Start testing...开始检测..."}

            val shortDelay = 2000L..5000L
            val middleDelay = 8000L..13000L
            val longDelay = 20000L..30000L
            var delay = middleDelay

            //每日总结 每天23:58生成
            val timestamp = System.currentTimeMillis()
            val time = SimpleDateFormat("HHmm").format(timestamp)
            val currDate = SimpleDateFormat("MMdd").format(timestamp)
            var summaryData : MutableMap<String, MutableMap<String, Int>>? = null
            val summary = (time=="2357"||time=="2358")&&currDate!=PluginData.summaryDate

            if (time.toInt() in 200..800){
                delay = longDelay
            }


            PluginData.userData.forEach { user ->

                // 获取一条动态
                delay(delay.random())
//            println("检测 "+user.name)
                val rawDynamic = httpGet(PluginConfig.dynamicApi+user.uid ,PluginConfig.COOKIE).getJSONObject("data").getJSONArray("cards").getJSONObject(0)
                val desc = rawDynamic.getJSONObject("desc")
                val dynamicId = desc.getBigInteger("dynamic_id").toString()
                val liveStatus =
                    try {
                        rawDynamic.getJSONObject("display").getJSONObject("live_info").getInteger("live_status")
                    }catch (e:Exception){
                        0
                    }

                // 判断是否为最新动态
                if (dynamicId != user.dynamicId && !PluginMain.historyDynamic.contains(dynamicId)){
                    user.dynamicId = dynamicId
                    PluginMain.historyDynamic.add(dynamicId)
                    sendDynamic(bot, rawDynamic, user)
                }

                // 判断直播状态
                if (liveStatus == 1 && (user.liveStatus==0||user.liveStatus==2)){
                    delay(shortDelay.random())
                    val roomInfo = httpGet(PluginConfig.liveStatusApi + user.liveRoom).getJSONObject("data").getJSONObject("room_info")

                    val dynamic = Dynamic()
                    dynamic.did = user.liveRoom
                    dynamic.timestamp = roomInfo.getBigInteger("live_start_time").toLong()
                    dynamic.content = "直播: ${roomInfo.getString("title")}"
                    dynamic.uid = user.uid
                    dynamic.isDynamic = false
                    dynamic.pictures = mutableListOf()
                    val cover = roomInfo.getString("cover")
                    val keyframe = roomInfo.getString("keyframe")
                    if (cover!=""){
                        dynamic.pictures?.add(cover)
                    }else if(keyframe!=""){
                        dynamic.pictures?.add(keyframe)
                    }
                    sendMessage(bot,user.uid,buildResMessage(bot, dynamic, user))
                }
                user.liveStatus = liveStatus

                // 每日总结
                if (summary && PluginData.summaryList.contains(user.uid)){

                    PluginMain.ys.clear()
                    if (summaryData == null){
                        summaryData = mutableMapOf()
                    }

                    val infoMap = mutableMapOf<String,Int>()
                    delay(shortDelay.random())
                    val followNum = httpGet(PluginConfig.followNumApi + user.uid).getJSONObject("data").getInteger("follower")
                    infoMap["fan"] = followNum
                    infoMap["riseFan"] = followNum - "${user.fan}".toInt()
                    delay(shortDelay.random())
                    val guardNum = httpGet(PluginConfig.guardApi +"ruid="+user.uid+"&roomid="+user.liveRoom).getJSONObject("data").getJSONObject("info").getInteger("num")
                    infoMap["guard"] = guardNum
                    infoMap["riseGuard"] = guardNum - "${user.guard}".toInt()
                    summaryData!![user.uid] = infoMap

                    user.fan = followNum
                    user.guard = guardNum
                }
            }

            // 发送每日总结
            if (summary){
                val resImg = File(buildSummaryImage(timestamp, summaryData!!)).toExternalResource()
                val chain = buildMessageChain {
                    +Image(""+bot.getGroup(PluginConfig.adminGroup)?.uploadImage(resImg)?.imageId)
                }
                resImg.close()
                sendMessage(bot,"487550002",chain)
                PluginData.summaryDate = currDate
            }
            delay(20000L)
        }catch (e:Exception){
            bot.getGroup(PluginConfig.adminGroup)?.sendMessage("检测动态失败，十分钟后重试\n"+e.message)
            delay(600000)
        }
    }
}
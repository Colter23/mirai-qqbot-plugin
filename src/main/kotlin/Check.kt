package top.colter.mirai.plugin

import com.alibaba.fastjson.JSONObject
import kotlinx.coroutines.delay
import net.mamoe.mirai.Bot
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.info
import top.colter.mirai.plugin.PluginConfig.BPI
import top.colter.mirai.plugin.bean.Dynamic
import top.colter.mirai.plugin.utils.buildSummaryImage
import top.colter.mirai.plugin.utils.httpGet
import java.text.SimpleDateFormat

suspend fun check(bot: Bot){
    while (true){
        try {
            PluginMain.logger.info {"Start testing...开始检测..."}

            //每日总结 每天23:58生成
            val timestamp = System.currentTimeMillis()
            val time = SimpleDateFormat("HHmm").format(timestamp)
            val currDate = SimpleDateFormat("MMdd").format(timestamp)
            var summaryData : MutableMap<String, MutableMap<String, Int>>? = null
            val summary = (time=="2355"||time=="2356"||time=="2357"||time=="2358")&&currDate!=PluginData.summaryDate

            val interval = PluginConfig.dynamic["interval"]!!.toLong()
            val shortDelay = 2000L..5000L
            val middleDelay = (interval-5)*1000..(interval+5)*1000
            val longDelay = (interval+5)*1000..(interval+15)*1000
            var delay = middleDelay

            val s = PluginConfig.dynamic["lowSpeed"]!!.split("-")
            if (time.toInt() in s[0].toLong()..s[1].toLong()){
                delay = longDelay
            }

            PluginData.userData.forEach { user ->
                //获取动态
                delay(delay.random())
                val rawDynamicList = httpGet(BPI["dynamic"]+user.uid ,BPI["COOKIE"]!!).getJSONObject("data").getJSONArray("cards")
                val rawDynamicOne = rawDynamicList.getJSONObject(0)

                //动态检测
                if (PluginConfig.dynamic["enable"]=="true") {
                    var r = false
                    // 判断是否为最新动态
                    for (i in rawDynamicList.size downTo 1){
                        val rawDynamic = rawDynamicList[i-1] as JSONObject
                        val dynamicId = rawDynamic.getJSONObject("desc").getBigInteger("dynamic_id").toString()
                        if (!PluginMain.historyDynamic.contains(dynamicId)&&r){
                            user.dynamicId = dynamicId
                            PluginMain.historyDynamic.add(dynamicId)
                            sendDynamic(bot, rawDynamic, user)
                        }
                        if (dynamicId==user.dynamicId){
                            r = true
                        }
                    }
                }

                //直播检测
                if (PluginConfig.live["enable"]=="true") {
                    val liveStatus =
                        try {
                            rawDynamicOne.getJSONObject("display").getJSONObject("live_info").getInteger("live_status")
                        }catch (e:Exception){
                            0
                        }
                    if (liveStatus == 1 && (user.liveStatus==0||user.liveStatus==2)){
                        delay(shortDelay.random())
                        val roomInfo = httpGet(BPI["liveStatus"] + user.liveRoom).getJSONObject("data").getJSONObject("room_info")

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
                }

                //每日总结
                if (summary && PluginData.summaryList.contains(user.uid)){
                    PluginMain.ys.clear()
                    if (summaryData == null){
                        summaryData = mutableMapOf()
                    }

                    val infoMap = mutableMapOf<String,Int>()
                    delay(shortDelay.random())
                    val followNum = httpGet(BPI["followNum"] + user.uid).getJSONObject("data").getInteger("follower")
                    infoMap["fan"] = followNum
                    infoMap["riseFan"] = followNum - "${user.fan}".toInt()
                    delay(shortDelay.random())
                    val guardNum = httpGet(BPI["guard"] +"ruid="+user.uid+"&roomid="+user.liveRoom).getJSONObject("data").getJSONObject("info").getInteger("num")
                    infoMap["guard"] = guardNum
                    infoMap["riseGuard"] = guardNum - "${user.guard}".toInt()
                    summaryData!![user.uid] = infoMap

                    user.fan = followNum
                    user.guard = guardNum
                }
            }

            // 发送每日总结
            if (summary){
                val chain = buildMessageChain {
                    +Image(""+buildSummaryImage(timestamp, summaryData!!))
                }
                sendMessage(bot,"487550002",chain)
                PluginData.summaryDate = currDate
            }
            PluginMain.logger.info {"检测结束"}
            delay(20000L)

        }catch (e:Exception){
            if(e.message!="Remote host terminated the handshake"){
                bot.getGroup(PluginConfig.adminGroup)?.sendMessage("检测动态失败，1分钟后重试\n"+e.message)
            }
            delay(60000L)
        }
    }
}
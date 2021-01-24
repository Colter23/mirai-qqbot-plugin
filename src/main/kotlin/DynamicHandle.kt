package top.colter.mirai.plugin

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import kotlinx.coroutines.delay
import net.mamoe.mirai.Bot
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import top.colter.mirai.plugin.bean.Dynamic
import top.colter.mirai.plugin.bean.User
import top.colter.mirai.plugin.utils.buildMessageImage
import top.colter.mirai.plugin.utils.httpGet
import java.lang.Exception
import java.net.URL
import javax.imageio.ImageIO

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * 解析动态 发送动态
 */
suspend fun sendDynamic(bot: Bot, rawDynamic: JSONObject, user: User){
    try {
        val desc = rawDynamic.getJSONObject("desc")
        val dynamicId = desc.getBigInteger("dynamic_id").toString()

        // 封装动态
        val dynamic = Dynamic()
        dynamic.did = dynamicId
        dynamic.timestamp = desc.getBigInteger("timestamp").toLong()
        dynamic.type = desc.getInteger("type")
        dynamic.contentJson = JSON.parseObject(rawDynamic.getString("card"))
        dynamic.uid = user.uid
        dynamic.display = rawDynamic.getJSONObject("display")

        // 格式化动态信息
        dynamicFormat(dynamic)
        // 构建消息链
        val resMag = buildResMessage(bot, dynamic, user)
        // 发送消息
        sendMessage(bot, user.uid, resMag)

    }catch (e : Exception){
        PluginMain.logger.error("检测 " + user.name + "的动态失败!")
    }
}

/**
 * 动态格式化为消息 封装在dynamic中的content
 */
fun dynamicFormat(dynamic: Dynamic){
    var content = ""
    when (dynamic.type){
        // 转发动态
        1 -> {
            val card = dynamic.contentJson
            content = "转发动态 : \n"+card.getJSONObject("item").getString("content")+"\n\n"
            val origType = card.getJSONObject("item").getInteger("orig_type")
            val origin = JSON.parseObject(card.getString("origin"))
            val originUser = card.getJSONObject("origin_user").getJSONObject("info").getString("uname")
            when (origType){
                //直播动态
                1 -> {
                }
                //带图片的动态
                2 -> {
                    content += "原动态 $originUser : \n"
                    content += origin.getJSONObject("item").getString("description")
                    dynamic.pictures = mutableListOf()
                    for (pic in origin.getJSONObject("item").getJSONArray("pictures")) {
                        dynamic.pictures?.add((pic as JSONObject).getString("img_src"))
                    }
                }
                //带表情的文字动态
                4 -> {
                    content += "原动态 $originUser : \n"
                    content += origin.getJSONObject("item").getString("content")
                }
                //视频动态
                8 -> {
                    content += "来自 $originUser 的视频 : ${origin.getString("title")}"
                    dynamic.pictures = mutableListOf()
                    dynamic.pictures?.add(origin.getString("pic"))
                }
            }
            try {
                val emojiJson = dynamic.display.getJSONObject("emoji_info").getJSONArray("emoji_details")
                putEmoji(emojiJson)
            } catch (e: Exception) { }
            try {
                val emojiJson = dynamic.display.getJSONObject("origin").getJSONObject("emoji_info").getJSONArray("emoji_details")
                putEmoji(emojiJson)
            } catch (e: Exception) { }
        }
        //带图片的动态
        2 -> {
            val card = dynamic.contentJson
            content = card.getJSONObject("item").getString("description")
            dynamic.pictures = mutableListOf()
            for (pic in card.getJSONObject("item").getJSONArray("pictures")) {
                dynamic.pictures?.add((pic as JSONObject).getString("img_src"))
            }
            try {
                val emojiJson = dynamic.display.getJSONObject("emoji_info").getJSONArray("emoji_details")
                putEmoji(emojiJson)
            } catch (e: Exception) { }
        }
        //带表情的文字动态
        4 -> {
            val card = dynamic.contentJson
            content = card.getJSONObject("item").getString("content")
            try {
                val emojiJson = dynamic.display.getJSONObject("emoji_info").getJSONArray("emoji_details")
                putEmoji(emojiJson)
            } catch (e: Exception) { }
        }
        //视频更新动态
        8 -> {
            val card = dynamic.contentJson
            content = "视频: ${card.getString("title")}"
            dynamic.pictures = mutableListOf()
            dynamic.pictures?.add(card.getString("pic"))
        }

        else -> {
            content = "不支持此类型动态"
        }
    }

    dynamic.content = content
}

/**
 * 解析emojiJson 获取图片封装进map
 */
fun putEmoji(emojiJson: JSONArray){
    for (emojiItem in emojiJson){
        val em = emojiItem as JSONObject
        val emojiName = em.getString("emoji_name")
        if (PluginMain.emojiMap[emojiName] == null)
            PluginMain.emojiMap[emojiName] = ImageIO.read(URL(em.getString("url")))
    }
}

/**
 * 构建发送消息链
 */
suspend fun buildResMessage(bot: Bot, dynamic: Dynamic, user: User): MessageChain {
    // 消息图片
    val resImg = File(buildMessageImage(dynamic,user)).toExternalResource()
    // 消息链接
    val link = if (dynamic.isDynamic){
        "https://t.bilibili.com/${dynamic.did}"
    }else{
        "https://live.bilibili.com/${user.liveRoom}"
    }

    // 消息链
    val chain = buildMessageChain {
        +Image(""+bot.getGroup(PluginConfig.adminGroup)?.uploadImage(resImg)?.imageId)
        +link
    }
    resImg.close()
    return chain
}

/**
 * 发送消息
 */
suspend fun sendMessage(bot: Bot, uid: String, resMsg: MessageChain){
    PluginData.followMemberGroup[uid]?.forEach { id ->
        if (PluginData.groupList.contains(id)){
            bot.getGroup(id)?.sendMessage(resMsg)
        }else if (PluginData.friendList.contains(id)){
            bot.getFriend(id)?.sendMessage(resMsg)
        }
        delay(500)
    }
}
package top.colter.mirai.plugin

import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.EventHandler
import net.mamoe.mirai.event.ListenerHost
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.sendAsImageTo
import top.colter.mirai.plugin.utils.buildFanImage
import java.io.File
import java.text.SimpleDateFormat

val emoji = listOf<String>("( •̀ ω •́ )✧","φ(゜▽゜*)♪","(oﾟvﾟ)ノ","(o゜▽゜)o☆",
    "(っ °Д °;)っ","ヽ(*。>Д<)o゜","￣へ￣","(￣▽￣)\"","(。・ω・)ノ","(´-ω-)",
    "(੭ˊ꒳ˋ)੭✧","（っ ' ᵕ ' ｃ）","(੭ ᐕ))？","ฅ^•ω•^ฅ","(  `꒳´ )","(っ ॑꒳ ॑c)",
    "⸜(* ॑꒳ ॑*  )⸝✩°｡⋆","(´･ω･`)?","`(*>﹏<*)′","(●'◡'●)","( •̀ ω •́ )y","(づ￣ 3￣)づ",
    "=￣ω￣=","＞﹏＜","(￣3￣)","＞︿＜","≧ ﹏ ≦","o((>ω< ))o","ヽ(゜▽゜　)","(￣﹏￣；)",
    "つ﹏⊂","(☆-ｖ-)","（〃｀ 3′〃）","(ง •_•)ง","o(〃＾▽＾〃)o","(。・ω・。)","╰(￣ω￣ｏ)",
    "（○｀ 3′○）","(°ー°〃)","o(≧口≦)o","✧(≖ ◡ ≖✿)","(｡･∀･)ﾉﾞ","ヾ(≧∇≦*)ゝ","(๑•̀ㅂ•́)و✧",
    "ヽ(✿ﾟ▽ﾟ)ノ","(๑´ㅂ`๑)","(/≧▽≦)/","（´v｀）","ε = = (づ′▽`)づ","")
val goodWork = listOf<String>("( •̀ ω •́ )✧","(oﾟvﾟ)ノ","(o゜▽゜)o☆","(￣▽￣)\"","(。・ω・)ノ",
    "(੭ˊ꒳ˋ)੭✧","⸜(* ॑꒳ ॑*  )⸝✩°｡⋆","( •̀ ω •́ )y","(￣3￣)","(ง •_•)ง","o(〃＾▽＾〃)o",
    "ヽ(✿ﾟ▽ﾟ)ノ","(/≧▽≦)/","(๑•̀ㅂ•́)و✧","(p≧▽≦)p"
    )

val ysr : Map<Int,MutableList<String>> =
    mapOf(
        5 to mutableListOf("七七","刻晴","甘雨","钟离","魈","达达利亚","可莉","温迪","琴","莫娜","迪卢克","阿贝多"),
        4 to mutableListOf("凝光","北斗","行秋","辛焱","重云","香菱","丽莎","凯亚","安柏","班尼特","砂糖","芭芭拉","菲谢尔","诺艾尔","迪奥娜","雷泽")
    )

val ysw : Map<Int,MutableList<String>> =
    mapOf(
        5 to mutableListOf("天空之刃","斫峰之刃","风鹰剑","无工之剑","狼的末路","天空之傲","天空之翼","阿莫斯之弓","四风原典","尘世之锁","天空之卷","天空之脊","贯虹之槊","和璞鸢"),
        4 to mutableListOf("笛剑","降临之剑","匣里龙吟","祭礼剑","西风剑","腐殖之剑","黑剑","黑岩长剑","试作斩岩","铁蜂刺","宗室长剑","钟剑","黑岩斩刀","雨裁","白影剑","雪葬的星银","螭骨剑","宗室大剑","祭礼大剑","试作古华","西风大剑","苍翠猎弓","祭礼弓","钢轮弓","绝弦","弓藏","黑岩战弓","试作澹月","西风猎弓","宗室长弓","忍冬之果","试作金珀","西风秘典","祭礼残章","昭心","黑岩绯玉","万国诸海图谱","宗室秘法录","匣里日月","流浪乐章","决斗之枪","龙脊长枪","宗室猎枪","流月针","匣里灭辰","试作星镰","西风长枪","黑岩刺枪"),
        3 to mutableListOf("冷刃","飞天御剑","吃虎鱼刀","黎明神剑","旅行剑","暗铁剑","以理服人","沐浴龙血的剑","白铁大剑","铁影阔剑","飞天大御剑","鸦羽弓","信使","弹弓","反曲弓","神射手之誓","魔导绪论","讨龙英杰谭","异世界行记","翡玉法球","甲级宝珏","黑缨枪","钺矛","白缨枪")
    )

/**
 * 同意好友申请
 */
object NewFriendRequestListener : ListenerHost {
    val coroutineContext = SupervisorJob()
    @EventHandler
    suspend fun NewFriendRequestEvent.onMessage(){
        this.accept()
        delay(2000)
        bot.getFriend(fromId)?.sendMessage("( •̀ ω •́ )✧")
    }
}

object MemberJoinListener : ListenerHost {
    val coroutineContext = SupervisorJob()
    @EventHandler
    suspend fun MemberJoinEvent.onMessage() {
        group.sendMessage(At(user)+" 欢迎"+goodWork[(goodWork.indices).random()])

    }
}

/**
 * 监听群消息
 */
object GroupListener : ListenerHost {
    val coroutineContext = SupervisorJob()

    @EventHandler
    suspend fun GroupMessageEvent.onMessage() {
        val content = message.content
        if (content.contains("辛苦了")||content.contains("辛苦啦")||content.contains("苦了")||content.contains("苦啦")){
            val time = System.currentTimeMillis()
            if(PluginMain.goodWorkCount==0){
                PluginMain.goodWorkCount++
                PluginMain.tempTime = time
            }else if((time-PluginMain.tempTime)<60000){
                PluginMain.goodWorkCount++
                if(PluginMain.goodWorkCount>=3){
                    PluginMain.goodWorkCount = 0
                    subject.sendMessage("辛苦了"+goodWork[(goodWork.indices).random()])
                }
            }else{
                PluginMain.tempTime = time
                PluginMain.goodWorkCount = 1
            }
            this.intercept()
        }
        when(content){
            "#?","#？","#help","#帮助","#功能","#菜单" -> {
                subject.sendMessage(QuoteReply(source)+
                    "#? 或 #help 或 #帮助 : 功能列表\n" +
                    "#r : 从0-10随机一个数\n" +
                    "#原神抽卡 : 每天三次" +
                    "@机器人 : 随机回复表情\n" +
                    "bell粉丝数 或 贝尔粉丝数\n" +
                    "memory粉丝数 或 泡沫粉丝数\n" +
                    "lily粉丝数 或 白百合粉丝数\n" +
                    "xx.xx总结 : 对应日期的每日总结")
                this.intercept()
            }
            "#r" -> {
                subject.sendMessage(QuoteReply(source)
                    +PlainText("你抽到的数字为: ${(0..10).random()}\n")
                    +PlainText(emoji[(emoji.indices).random()])
                )
                this.intercept()
            }
            "@全体成员" -> {
                subject.sendMessage("收到"+goodWork[(goodWork.indices).random()])
                this.intercept()
            }
        }
    }
}


/**
 * 监听好友消息
 */
object FriendListener : ListenerHost {
    val coroutineContext = SupervisorJob()
    @EventHandler
    suspend fun FriendMessageEvent.onMessage() {
        val content = message.content
        when (content) {
            "#?", "#？", "help", "帮助", "功能", "菜单" -> {
                subject.sendMessage("如要使用b站动态推送功能请回复 ‘开启私人订阅’(功能被阉割)\n回复 ‘功能’ 或 ‘帮助’ 查看命令列表\n如果有问题或事情请加 3375582524\n玩的开心ヾ(≧∇≦*)ゝ")
                subject.sendMessage(
                    "开启私人订阅\n关闭私人订阅\n" +
                        "#r 或 随机数 : 从0-10随机一个数\n" +
                        "添加删除暂时被阉割，如有情况请联系Colter(3375582524)"
                )
                this.intercept()
            }
            "开启私人订阅" -> {
                if (PluginData.friendList.contains(friend.id)) {
                    subject.sendMessage("你已经开启过私人订阅了\n＞﹏＜")
                } else {
                    PluginData.friendList.add(friend.id)
                    subject.sendMessage("开启私人订阅成功\n(oﾟvﾟ)ノ")
                    subject.sendMessage(
                        "注意事项：\n" +
                            "不要添加过多订阅，访问周期会很长\n" +
                            "不要过度依赖机器人，机器人并不稳定，随时可能爆炸\n" +
                            "玩的开心( •̀ ω •́ )y"
                    )
                }
                this.intercept()
            }
            "关闭私人订阅" -> {
                if (PluginData.friendList.contains(friend.id)) {
                    PluginData.friendList.remove(friend.id)
                    subject.sendMessage("关闭私人订阅成功\n(°ー°〃)")
                } else {
                    subject.sendMessage("啊这(°ー°〃)")
                }
                this.intercept()
            }
            "#r", "随机数" -> {
                subject.sendMessage("你抽到的数字为: ${(0..10).random()}\n${emoji[(emoji.indices).random()]}")
            }
        }
    }
}

/**
 * 监听消息
 */
object MessageListener : ListenerHost {
    val coroutineContext = SupervisorJob()
    @EventHandler
    suspend fun MessageEvent.onMessage() {
        val content = message.content
        when (content) {
            "#bell", "bell粉丝数", "猫芒粉丝数", "贝尔粉丝数", "猫猫粉丝数" -> {
                val path = buildFanImage(487550002)
                if (path != "") {
                    val img = File(path)
                    img.sendAsImageTo(subject)
                } else subject.sendMessage("发送失败")
            }
            "#memory", "memory粉丝数", "泡沫粉丝数" -> {
                val path = buildFanImage(487551829)
                if (path != "") {
                    val img = File(path)
                    img.sendAsImageTo(subject)
                } else subject.sendMessage("发送失败")
            }
            "#lily", "lily粉丝数", "白百合粉丝数", "派派粉丝数" -> {
                val path = buildFanImage(421347849)
                if (path != "") {
                    val img = File(path)
                    img.sendAsImageTo(subject)
                } else subject.sendMessage("发送失败")
            }
            "#原神抽卡" -> {
                var chou = true
                if (PluginMain.ys.contains(sender.id)) {
                    if (PluginMain.ys[sender.id]!! >= 3) {
                        chou = false
                    }
                }
                if (chou) {
                    var r5 = 0
                    var r4 = 0
                    var w5 = 0
                    var w4 = 0
                    var w3 = 0
                    for (s in 1..10) {
                        val random = (1..100).random()
                        if (random in 1..3) { // 1  2  3  -> 3%
                            r5++
                        } else if (random in 4..6) {  //  4  5  6   ->  3%
                            w5++
                        } else if (random in 7..16) {  // 7 8 9 10 11 12 13 14 15 16 -> 10%
                            r4++
                        } else if (random in 17..26) { //  17 18 19 20 21 22 23 24 25 26  ->  10%
                            w4++
                        } else if (random in 27..100) { // 75%
                            w3++
                        }
                    }
                    var msg = ""
                    if (r5 != 0 || w5 != 0) {
                        msg += "☆☆☆☆☆\n"
                        for (i in 1..r5) {
                            msg += "${ysr[5]?.get((0..(ysr[5]?.size?.minus(1) ?: 0)).random())}  "
                        }
                        for (i in 1..w5) {
                            msg += "${ysw[5]?.get((0..(ysw[5]?.size?.minus(1) ?: 0)).random())}  "
                        }
                        msg += "\n"
                    }
                    if (r4 != 0 || w4 != 0) {
                        msg += "☆☆☆☆\n"
                        for (i in 1..r4) {
                            msg += "${ysr[4]?.get((0..(ysr[4]?.size?.minus(1) ?: 0)).random())}  "
                        }
                        for (i in 1..w4) {
                            msg += "${ysw[4]?.get((0..(ysw[4]?.size?.minus(1) ?: 0)).random())}  "
                        }
                        msg += "\n"
                    }
                    if (w3 != 0) {
                        msg += "☆☆☆\n"
                        for (i in 1..w3) {
                            msg += "${ysw[3]?.get((0..(ysw[3]?.size?.minus(1) ?: 0)).random())}  "
                        }
                    }
                    PluginMain.ys[sender.id] = PluginMain.ys[sender.id]?.plus(1) ?: 1
                    subject.sendMessage(QuoteReply(source) + msg)
                } else {
                    subject.sendMessage(QuoteReply(source) + "你今天抽的次数太多了，明天在来吧 >_<")
                }
            }
        }
        if (content.contains("@" + bot.id)) {
            if ((0..1).random() == 0) {
                subject.sendMessage(emoji[(emoji.indices).random()])
            } else {
                val emoji =
                    File("${PluginData.runPath}${PluginConfig.basePath}/emoji/${(1..PluginConfig.emojiNum).random()}.png")
                emoji.sendAsImageTo(subject)
            }
        }
        val regex1 = Regex("""\d{1,2}.\d{1,2}总结""")
        val regex2 = Regex("""\d{2,4}.\d{1,2}.\d{1,2}总结""")
        if (content.contains(regex1) || content.contains(regex2)) {
            val dateArr = content.substring(0, content.indexOf('总')).split('.').toMutableList()
            var date = ""
            if (dateArr.size == 2) {
                date += SimpleDateFormat("yyyy").format(System.currentTimeMillis())
            } else if (dateArr.size == 3) {
                if (dateArr[0].length == 2) dateArr[0] = "20" + dateArr[0]
            }
            for (d in dateArr) {
                if (d.length == 1) date += "0$d"
                else date += d
            }

            try {
                val resImg = File("${PluginData.runPath}${PluginConfig.basePath}/img/summary/$date.jpg")
                resImg.sendAsImageTo(subject)
            } catch (e: Exception) {
                subject.sendMessage("没有找到此日的总结(*>﹏<*)\n格式: 2020.12.1总结 或 1.1总结\n注意: 搜索今年的可以不用加年份，往年的需要加年份(两位或四位都可)")
            }
        }
    }
}


//    bot.eventChannel.exceptionHandler { e ->
//        PluginMain.logger.error("检测失败")
//        Thread.sleep(20000)
//    }

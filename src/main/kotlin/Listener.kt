package top.colter.mirai.plugin

import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.EventHandler
import net.mamoe.mirai.event.ListenerHost
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.sendAsImageTo
import top.colter.mirai.plugin.GroupListener.onMessage
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


val ys : Map<Int,MutableList<String>> =
    mapOf(
        5 to mutableListOf("白百合Lily","泡沫Memory","猫芒Bell","勇凪Elena","二和餅Anko","魔王Lock","门门子","胡桃","七七","刻晴","甘雨","钟离","魈","达达利亚","可莉","温迪","琴","莫娜","迪卢克","阿贝多","天空之刃","斫峰之刃","风鹰剑","无工之剑","狼的末路","天空之傲","天空之翼","阿莫斯之弓","四风原典","尘世之锁","天空之卷","天空之脊","贯虹之槊","和璞鸢"),
        4 to mutableListOf("凝光","北斗","行秋","辛焱","重云","香菱","丽莎","凯亚","安柏","班尼特","砂糖","芭芭拉","菲谢尔","诺艾尔","迪奥娜","雷泽","笛剑","匣里龙吟","祭礼剑","西风剑","铁蜂刺","宗室长剑","钟剑","雨裁","白影剑","宗室大剑","祭礼大剑","西风大剑","苍翠猎弓","祭礼弓","绝弦","弓藏","西风猎弓","宗室长弓","西风秘典","祭礼残章","昭心","宗室秘法录","匣里日月","流浪乐章","决斗之枪","龙脊长枪","宗室猎枪","匣里灭辰","试作星镰","西风长枪"),
        3 to mutableListOf("冷刃","飞天御剑","吃虎鱼刀","黎明神剑","旅行剑","暗铁剑","以理服人","沐浴龙血的剑","白铁大剑","铁影阔剑","飞天大御剑","鸦羽弓","信使","弹弓","反曲弓","神射手之誓","魔导绪论","讨龙英杰谭","异世界行记","翡玉法球","甲级宝珏","黑缨枪","钺矛","白缨枪")
    )
val fz : Map<Int,MutableList<String>> =
    mapOf(
        6 to mutableListOf("能天使","黑","安洁莉娜","银灰","莫斯提马","夜莺","星熊","陈","年","阿","煌","麦哲伦","赫拉格","斯卡蒂","塞雷娅","闪灵","艾雅法拉","伊芙利特","推进之王","刻俄柏","风笛","傀影","温蒂","W","早露","铃兰","棘刺","森蚺","史尔特尔","瑕光","迷迭香","泥岩","山","空弦","嵯峨","夕"),
        5 to mutableListOf("狮蝎","食铁兽","蓝毒","拉普兰德","幽灵鲨","德克萨斯","槐琥","赫默","红","白面鸮","空","吽","雪雉","灰喉","布洛卡","苇草","拜松","微风","送葬人","炎客","星极","格劳克斯","锡兰","诗怀雅","格拉尼","夜魔","暴行","真理","初雪","崖心","守林人","普罗旺斯","火神","可颂","雷蛇","临光","华法琳","梅尔","天火","陨星","白金","因陀罗","芙兰卡","凛冬","惊蛰","柏喙","慑砂","铸铁","巫恋","极境","石棉","月禾","苦艾","莱恩哈特","亚叶","断崖","蜜蜡","贾维","稀音","安哲拉","燧石","特米米","薄绿","四月","鞭刃","奥斯塔","絮雨","罗宾","卡夫卡","爱丽丝","图耶","乌有","炎狱炎熔"),
        4 to mutableListOf("安比尔","梅","伊桑","红云","坚雷","桃金娘","苏苏洛","格雷伊","猎蜂","阿消","地灵","深海色","古米","蛇屠箱","角峰","调香师","嘉维尔","末药","暗索","砾","慕斯","霜叶","缠丸","杜宾","红豆","清道夫","讯使","白雪","流星","杰西卡","远山","夜烟","艾丝黛尔","清流","断罪者","宴","刻刀","波登可","卡达","孑","酸糖","芳汀","泡泡","杰克","松果","豆苗"),
        3 to mutableListOf("斑点","泡普卡","月见夜","空爆","梓兰","史都华德","安赛尔","芙蓉","炎熔","安德切尔","克洛丝","米格鲁","卡缇","玫兰莎","翎羽","香草","芬")
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
        bot.getFriend(fromId)?.sendMessage("此号为机器人，有事请联系 Colter( 3375582524 )")
        bot.getFriend(fromId)?.sendMessage("如需要推送ViViD成员B站动态,请回复  开启VVD动态推送")
    }
}

object MemberJoinListener : ListenerHost {
    val coroutineContext = SupervisorJob()
    @EventHandler
    suspend fun MemberJoinEvent.onMessage() {
        if (group.id==391163028L){
            group.sendMessage(At(user)+" 欢迎"+goodWork[(goodWork.indices).random()])
            group.sendMessage(At(user)+" 新人请先去工作表内填写职能表\nhttps://shimo.im/sheets/vWlArzoyB4KTYA2o/")
        }else{
            group.sendMessage(At(user)+" 欢迎"+goodWork[(goodWork.indices).random()])
        }


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
                    "#骰子 : 一个骰子\n" +
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
//            "@全体成员" -> {
//                subject.sendMessage("收到"+goodWork[(goodWork.indices).random()])
//                this.intercept()
//            }
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
//                subject.sendMessage("如要使用b站动态推送功能请回复 ‘开启私人订阅’(功能被阉割)\n回复 ‘功能’ 或 ‘帮助’ 查看命令列表\n如果有问题或事情请加 3375582524\n玩的开心ヾ(≧∇≦*)ゝ")
                subject.sendMessage(
//                    "开启私人订阅\n关闭私人订阅\n" +
//                        "#r 或 随机数 : 从0-10随机一个数\n"
//                        "添加删除暂时被阉割，如有情况请联系Colter(3375582524)"
                    "订阅VVD动态\n取消订阅VVD动态\n" +
                    "#? 或 #help 或 #帮助 : 功能列表\n" +
                    "#r : 从0-10随机一个数\n" +
                    "#骰子 : 一个骰子\n" +
                    "#原神抽卡 : 每天三次\n" +
                    "#方舟抽卡 : 每天三次\n" +
                    "bell粉丝数 或 贝尔粉丝数\n" +
                    "memory粉丝数 或 泡沫粉丝数\n" +
                    "lily粉丝数 或 白百合粉丝数\n" +
                    "xx.xx总结 : 对应日期的每日总结"
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
            "订阅VVD动态" -> {
                if (!PluginData.friendList.contains(friend.id)) {
                    PluginData.friendList.add(friend.id)
                }
                for (uid in PluginData.summaryList){
                    if (PluginData.followMemberGroup[uid]?.contains(friend.id) != true){
                        PluginData.followMemberGroup[uid]?.add(friend.id)
                    }
                }
                subject.sendMessage("开启VVD动态推送成功\n(oﾟvﾟ)ノ")
                subject.sendMessage("Bot并不是很稳定，如漏掉推送可能是爆炸了")
                this.intercept()
            }
            "取消订阅VVD动态" -> {

                for (uid in PluginData.summaryList){
                    try {
                        PluginData.followMemberGroup[uid]?.remove(friend.id)
                    }catch (e:Exception){
                    }
                }
                subject.sendMessage("已取消\n(°ー°〃)")

                this.intercept()
            }
            "#r", "随机数" -> {
                subject.sendMessage("你抽到的数字为: ${(0..10).random()}\n${emoji[(emoji.indices).random()]}")
            }
        }

        // 私人个性化订阅
//        if (content.contains("添加")||content.contains("add")){
//            if (!PluginData.friendList.contains(friend.id)) {
//                subject.sendMessage("请先开启私人订阅功能\n＞﹏＜")
//                this.intercept()
//                return
//            }
//            var uid = ""
//            var name = ""
//            uid = if (content.contains("add")){
//                content.substring(4)
//            }else{
//                content.substring(3)
//            }
//            try {
//                PluginData.followMemberGroup[uid]!!.add(friend.id)
//                PluginData.userData.forEach { item ->
//                    if (item.uid == uid){
//                        name = item.name.toString()
//                        return@forEach
//                    }
//                }
//                subject.sendMessage("添加 $name 成功\n( •̀ ω •́ )y")
//            }catch (e:Exception){
//                subject.sendMessage("添加并初始化信息中，请耐心等待，大概需要10s")
//                try {
//                    val map = getFollowInfo(uid)
//                    PluginData.userData.add(map)
//                    name = map.name.toString()
//                    if (!PluginData.followList.contains(uid)){
//                        PluginData.followList.add(uid)
//                    }
//                    PluginData.followMemberGroup[uid] = mutableListOf(friend.id)
//
////                    println(PluginMain.followMemberGroup.toString())
//
//                    subject.sendMessage("添加 $name 成功\n( •̀ ω •́ )y")
//                }catch (e:Exception){
//                    subject.sendMessage("添加 $uid 失败! 内部错误 或 检查uid是否正确\n$e")
//                }
//            }
//
//            this.intercept()
//        }else
//            if (content.contains("删除")||content.contains("del")){
//                if (!PluginData.friendList.contains(friend.id)) {
//                    subject.sendMessage("请先开启私人订阅功能\n＞﹏＜")
//                    this.intercept()
//                    return
//                }
//                var uid = ""
//                var name = ""
//                try {
//                    uid = if (content.contains("del")){
//                        content.substring(4)
//                    }else{
//                        content.substring(3)
//                    }
//
//                    PluginData.followMemberGroup[uid]?.remove(friend.id)
//                    if (PluginData.followMemberGroup[uid]?.size==0){
//                        PluginData.followList.remove(uid)
//                        PluginData.followMemberGroup.remove(uid)
//                        PluginData.userData.forEach { u ->
//                            if (u.uid==uid) {
//                                PluginData.userData.remove(u)
//                                return@forEach
//                            }
//                        }
//                    }
//                    subject.sendMessage("删除 $uid 成功")
//                }catch (e:Exception){
//                    subject.sendMessage("删除 $uid 失败! 内部错误 或 检查uid是否正确\n$e")
//                }
//                this.intercept()
//            }
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
            "#骰子","/骰子" -> {
                subject.sendMessage(Dice((1..6).random()))
                this.intercept()
            }
            "#原神抽卡" -> {
                var chou = false
                // subject.id==959053105L ||
                if (subject !is Group){
                    chou = true
                    if (PluginMain.ys.contains(subject.id.toString() + sender.id)) {
                        if (PluginMain.ys[subject.id.toString() + sender.id]!! >= 3) {
                            chou = false
                            subject.sendMessage(QuoteReply(source) + "你今天抽的次数太多了，明天再来吧 >_<")
                        }
                    }
                }
                if (chou) {
                    var r5 = 0
                    var r4 = 0
                    var r3 = 0
                    for (s in 1..10) {
                        val random = (1..100).random()
                        if (random in 97..100) { // 98..100
                            r5++
                        } else if (random in 6..20) {  // 6..20
                            r4++
                        }else{
                            r3++
                        }
                    }
                    if (r5==0&&r4==0){
                        r4++
                        r3--
                    }
                    var msg = ""
                    if (r5 != 0) {
                        msg += "☆☆☆☆☆\n"
                        for (i in 1..r5) {
                            msg += "${ys[5]?.get((0..(ys[5]?.size?.minus(1) ?: 0)).random())}  "
                        }
                        msg += "\n"
                    }
                    if (r4 != 0) {
                        msg += "☆☆☆☆\n"
                        for (i in 1..r4) {
                            msg += "${ys[4]?.get((0..(ys[4]?.size?.minus(1) ?: 0)).random())}  "
                        }
                        msg += "\n"
                    }
                    if (r3 != 0) {
                        msg += "☆☆☆\n"
                        for (i in 1..r3) {
                            msg += "${ys[3]?.get((0..(ys[3]?.size?.minus(1) ?: 0)).random())}  "
                        }
                    }
                    PluginMain.ys[subject.id.toString() + sender.id ] = PluginMain.ys[subject.id.toString() + sender.id]?.plus(1) ?: 1
                    subject.sendMessage(QuoteReply(source) + msg)
                }
            }
            "#方舟抽卡" -> {
                var chou = false
                // subject.id==959053105L ||
                if (subject !is Group){
                    chou = true
                    if (PluginMain.ys.contains(subject.id.toString() + sender.id)) {
                        if (PluginMain.ys[subject.id.toString() + sender.id]!! >= 3) {
                            chou = false
                            subject.sendMessage(QuoteReply(source) + "你今天抽的次数太多了，明天再来吧 >_<")
                        }
                    }
                }
                if (chou) {
                    var r6 = 0
                    var r5 = 0
                    var r4 = 0
                    var r3 = 0
                    for (s in 1..10) {
                        val random = (1..100).random()
                        if (random in 97..100) { // 48..50
                            r6++
                        } else if (random in 6..15) { //6..15
                            r5++
                        }else if (random in 40..70) {  // 16..47||random in 51..75
                            r4++
                        }else {
                            r3++
                        }
                    }
                    var msg = ""
                    if (r6 != 0) {
                        msg += "☆☆☆☆☆☆\n"
                        for (i in 1..r6) {
                            msg += "${fz[6]?.get((0..(fz[6]?.size?.minus(1) ?: 0)).random())}  "
                        }
                        msg += "\n"
                    }
                    if (r5 != 0) {
                        msg += "☆☆☆☆☆\n"
                        for (i in 1..r5) {
                            msg += "${fz[5]?.get((0..(fz[5]?.size?.minus(1) ?: 0)).random())}  "
                        }
                        msg += "\n"
                    }
                    if (r4 != 0) {
                        msg += "☆☆☆☆\n"
                        for (i in 1..r4) {
                            msg += "${fz[4]?.get((0..(fz[4]?.size?.minus(1) ?: 0)).random())}  "
                        }
                        msg += "\n"
                    }
                    if (r3 != 0) {
                        msg += "☆☆☆\n"
                        for (i in 1..r3) {
                            msg += "${fz[3]?.get((0..(fz[3]?.size?.minus(1) ?: 0)).random())}  "
                        }
                    }
                    PluginMain.ys[subject.id.toString() + sender.id ] = PluginMain.ys[subject.id.toString() + sender.id]?.plus(1) ?: 1
                    subject.sendMessage(QuoteReply(source) + msg)
                }
            }
            else ->{
                if (sender.id==3375582524 && content.contains("@" + bot.id)||subject !is Group) {
                    subject.sendMessage(emoji[(emoji.indices).random()])
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
                        date += if (d.length == 1) "0$d"
                        else d
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
    }
}


//    bot.eventChannel.exceptionHandler { e ->
//        PluginMain.logger.error("检测失败")
//        Thread.sleep(20000)
//    }

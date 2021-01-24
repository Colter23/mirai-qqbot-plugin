package org.example.mirai.plugin

import kotlin.random.Random

fun main() {
    val 原神人物 : Map<Int,MutableList<String>> =
        mapOf(
            5 to mutableListOf("七七","刻晴","甘雨","钟离","魈","达达利亚","可莉","温迪","琴","莫娜","迪卢克","阿贝多"),
            4 to mutableListOf("凝光","北斗","行秋","辛焱","重云","香菱","丽莎","凯亚","安柏","班尼特","砂糖","芭芭拉","菲谢尔","诺艾尔","迪奥娜","雷泽")
        )

    val 原神武器 : Map<Int,MutableList<String>> =
        mapOf(
            5 to mutableListOf("天空之刃","斫峰之刃","风鹰剑","无工之剑","狼的末路","天空之傲","天空之翼","阿莫斯之弓","四风原典","尘世之锁","天空之卷","天空之脊","贯虹之槊","和璞鸢"),
            4 to mutableListOf("笛剑","降临之剑","匣里龙吟","祭礼剑","西风剑","腐殖之剑","黑剑","黑岩长剑","试作斩岩","铁蜂刺","宗室长剑","钟剑","黑岩斩刀","雨裁","白影剑","雪葬的星银","螭骨剑","宗室大剑","祭礼大剑","试作古华","西风大剑","苍翠猎弓","祭礼弓","钢轮弓","绝弦","弓藏","黑岩战弓","试作澹月","西风猎弓","宗室长弓","忍冬之果","试作金珀","西风秘典","祭礼残章","昭心","黑岩绯玉","万国诸海图谱","宗室秘法录","匣里日月","流浪乐章","决斗之枪","龙脊长枪","宗室猎枪","流月针","匣里灭辰","试作星镰","西风长枪","黑岩刺枪"),
            3 to mutableListOf("冷刃","飞天御剑","吃虎鱼刀","黎明神剑","旅行剑","暗铁剑","以理服人","沐浴龙血的剑","白铁大剑","铁影阔剑","飞天大御剑","鸦羽弓","信使","弹弓","反曲弓","神射手之誓","魔导绪论","讨龙英杰谭","异世界行记","翡玉法球","甲级宝珏","黑缨枪","钺矛","白缨枪")
        )

    var r5 = 0
    var r4 = 0
    var w5 = 0
    var w4 = 0
    var w3 = 0
    for (s in 1..10){

//        val random = (1..100).random()
//        if (random in 1..3){ // 1  2  3  -> 3%
//            r5++
//        }else if (random in 4..6){  //  4  5  6   ->  3%
//            w5++
//        }else if (random in 7..16){  // 7 8 9 10 11 12 13 14 15 16 -> 10%
//            r4++
//        }else if (random in 17..26){ //  17 18 19 20 21 22 23 24 25 26  ->  10%
//            w4++
//        }else if (random in 27..100){ // 75%
//            w3++
//        }

        val random = (1..1000).random()
        if (random in 1..30){ // 30
            r5++
        }else if (random in 31..60){  //  30
            w5++
        }else if (random in 61..160){  // 100
            r4++
        }else if (random in 161..260){ // 100
            w4++
        }else if (random in 261..1000){ // 75%
            w3++
        }

//        print()
//        print("  ")
//        if (s%40==0) print("\n")
    }
    val rw = mapOf<String,Int>("r5" to r5, "w5" to w5, "r4" to r4, "w4" to w4, "w3" to w3)
    val rwCount = mapOf<String,Int>("r5" to (原神人物[5]?.size ?: 0), "w5" to (原神武器[5]?.size ?: 0), "r4" to (原神人物[4]?.size ?: 0), "w4" to (原神武器[4]?.size ?: 0), "w3" to (原神武器[3]?.size ?: 0))
    val list = mutableListOf<String>()
    rw.forEach { (t, u) ->
        for (i in 1..u){
            list.add(t+"-"+(1..rwCount[t]!!).random())
        }
    }
    println(list)

    println("⭐⭐⭐⭐⭐人物：$r5")
    println("⭐⭐⭐⭐⭐武器：$w5")
    println("⭐⭐⭐⭐人物：$r4")
    println("⭐⭐⭐⭐武器：$w4")
    println("⭐⭐⭐武器：$w3")


}
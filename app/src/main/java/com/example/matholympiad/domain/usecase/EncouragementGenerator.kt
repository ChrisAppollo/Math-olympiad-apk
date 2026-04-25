package com.example.matholympiad.domain.usecase

import java.util.Random

class EncouragementGenerator {
    
    private val encouragementPool = listOf(
        "太棒了！继续加油！",
        "你真是个数学小天才！",
        "完美！就是这样！",
        "好厉害，保持这个状态！",
        "聪明绝顶，再接再厉！",
        "非常棒，距离满分越来越近啦！",
        "答对了！真聪明！",
        "太牛了，继续挑战！",
        "数学小达人就是你！",
        "超棒的表现，满分在向你招手！"
    )
    
    private val wrongAnswerHints = listOf(
        "别灰心，看看解析再试一次哦！",
        "这道题有点难，但没关系！",
        "仔细看提示，下次一定能做对！",
        "加油，你已经很接近正确答案啦！",
        "理解思路很重要，继续加油！"
    )
    
    private val random = Random()
    
    fun getCorrectEncouragement(): String {
        return encouragementPool[random.nextInt(encouragementPool.size)]
    }
    
    fun getWrongHint(): String {
        return wrongAnswerHints[random.nextInt(wrongAnswerHints.size)]
    }
}

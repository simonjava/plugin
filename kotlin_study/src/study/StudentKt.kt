package study

import study.StudentKt.Companion.TAG

/**
 * Created by chengsimin on 2019/5/27.
 */

internal class StudentKt constructor() : UserKt(){

    val gongju by lazy {
        "我可以延迟初始化"
    }

    lateinit var gongju2: String

    var grade: Int = 0

    constructor(age: Int, name: String, nameChangeListener: NameChangeListener, grade: Int) : this() {
        this.grade = grade
    }

    constructor(age: Int, name: String, grade: Int) : this() {
        this.grade = grade
    }

    companion object{
        val TAG = "StudentKtTag"
        var no = 0
        fun getGlobalNo():Int{
            return no++
        }
    }

}

@file:JvmName("TopLevelUtils")
package study

import java.io.Serializable

/**
 * Created by chengsimin on 2019/5/27.
 */
open class UserKt : Serializable {

    /**
     *  重新覆盖 set 与 get 方法
     */
    var age: Int = 0 // 相当于public
    set(value){
        field = value+1000
    }

    var name: String? = null
    set(value){
        field = value
        nameChangeListener?.onchange()

    }
    get() = field?.toUpperCase()

    internal var nameChangeListener: NameChangeListener? = null // 相当于没写修饰符，包权限

    companion object{
        val TAG = "StudentKtTag"
    }

    constructor(){
        println("constructor1")
    }

    constructor(age: Int, name: String, nameChangeListener: NameChangeListener?) {
        this.age = age
        this.name = name
        this.nameChangeListener = nameChangeListener
    }

    constructor(age: Int, name: String):this(age,name,null) {
        this.nameChangeListener = object : NameChangeListener {
            override fun onchange() {
                println("名字改变了")
            }
        }
    }

    init {
        println("主构造器初始化时我会执行啊")
    }


    fun getName1() = 1

    override fun toString(): String {
        return "UserKt{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}'
    }

    class Builder {
        private var age: Int = 0
        private var name: String = ""

        fun setName(name: String): Builder {
            this.name = name
            return this
        }

        fun setAge(age: Int): Builder {
            this.age = age
            return this
        }

        fun build(): UserKt {
            val userJava = UserKt(this.age, this.name)
            return userJava
        }
    }

    interface NameChangeListener {
        fun onchange()
    }

    inner class Book(private val bookName: String) {

        fun desc() {
            println("this is $name 's bookName.$bookName")
        }
    }
}

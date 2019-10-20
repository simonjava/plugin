import study.StudentKt
import study.UserJava
import study.UserKt

/**
 * Created by chengsimin on 2019/5/26.
 */
fun main(args: Array<String>) {
    var userJava = UserJava.Builder().setName("userJava").build()
    println("${userJava}")
    userJava.Book("userJavaBook").desc()
    var userKt = UserKt.Builder().setName("userKt").build()
    userKt.age = 1
    println("${userKt}")
    userKt.Book("userKtBook").desc()
    println(userKt.name)
    for(i in 1..10){
        println(StudentKt.getGlobalNo())
    }
    var student = StudentKt()
    println(student.gongju)
    StudentKt::class.java
    if(student::gongju2.isLateinit){
        println("1")
        println(student.gongju2)
    }

}



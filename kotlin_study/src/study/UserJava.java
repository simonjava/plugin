package study;

import java.io.Serializable;

/**
 * Created by chengsimin on 2019/5/27.
 */

public class UserJava implements Serializable {
    public final static String TAG = "UserJava";
    private int age;
    private String name;
    NameChangeListener nameChangeListener;

    public UserJava(int age, String name, NameChangeListener nameChangeListener) {
        this.age = age;
        this.name = name;
        this.nameChangeListener = nameChangeListener;
    }

    public UserJava(int age, String name) {
        this.age = age;
        this.name = name;
        this.nameChangeListener = new NameChangeListener() {
            @Override
            public void onchange() {
                System.out.println("名字改变了");
            }
        };
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (nameChangeListener != null) {
            nameChangeListener.onchange();
        }
    }

    @Override
    public String toString() {
        return "UserJava{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }

    public static class Builder {
        private int age;
        private String name;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setAge(int age) {
            this.age = age;
            return this;
        }

        public UserJava build() {
            UserJava userJava = new UserJava(this.age, this.name);
            return userJava;
        }
    }

    public interface NameChangeListener {
        void onchange();
    }

    public class Book {
        private String bookName;

        public Book(String bookName) {
            this.bookName = bookName;
        }

        public void desc() {
            System.out.println("this is " + name + " 's bookName." + bookName);
        }
    }
}

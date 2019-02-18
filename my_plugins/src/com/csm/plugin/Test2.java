package com.csm.plugin;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.junit.Test;


import java.io.IOException;
import java.lang.reflect.Modifier;

public class Test2 {

    A aclass;

    void f(B b, D a) {
        if(aclass==null){
            aclass = new A(new C() {

                @Override
                public void go(B b) {
                    System.out.println(b.hashCode()+"  a:"+a.hashCode());
                }
            });
        }
        aclass.aa(b);
    }


    public static void main(String[] args) throws IOException {
        Test2 test2 = new Test2();
        test2.f(new B(),new D());
        test2.f(new B(),new D());
    }

    static class B {


    }

    class A {
        C c;

        public A(C c) {
            this.c = c;
        }

        void aa(B b) {
            if (c != null) {
                c.go(b);
            }
        }
    }

    interface C {
        void go(B b);
    }

    static class D{

    }
}


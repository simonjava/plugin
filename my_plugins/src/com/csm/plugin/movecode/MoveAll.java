package com.csm.plugin.movecode;

import java.util.Scanner;

public class MoveAll {
    public static void main(String[] args) {
        MoveLayoutAndDrawable.main(null);
        SimpleReplace.main(null);
        System.out.println("SimpleReplace over");
        MoveString.main(null);
        System.out.println("MoveString over");
        MoveColor.main(null);
        System.out.println("MoveColor over");
        MoveDimen.main(null);
        System.out.println("MoveDimen over");
    }
}

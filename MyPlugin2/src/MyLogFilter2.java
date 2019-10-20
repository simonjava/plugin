import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MyLogFilter2 {

    static List<String> tracePids = new ArrayList<String>();

    static String pidRegexStr;

    static String tagRegexStr;

    static List<String> tags = new ArrayList<String>();

    static boolean include = false;

    public static boolean isProcessId(String arg) {
        char c = arg.charAt(0);
        if (c >= '0' && c <= '9') {
            return true;
        }
        return false;
    }

    static int initPidsAndTags(String args[]) {
        int i = 0;
        for (; i < args.length - 2; i++) {
            String arg = args[i];
            if (isProcessId(arg)) {
                tracePids.add(arg);
            } else {
                break;
            }
        }
        // 找标签
        for (; i < args.length - 2; i++) {
            String arg = args[i];
            tags.add(arg);
        }

        return i;
    }

    static void initInclude(String args[]) {
        if (args[args.length - 2].equals("include")) {
            // 只显示这些
            include = true;
        }
    }

    public static void init(String args[]) {
        initPidsAndTags(args);
        initInclude(args);
        createPattern();
        try {
            writer = new FileWriter(args[args.length - 1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createPattern() {
        {
            StringBuilder pidRegex = new StringBuilder("[\\s|\\S]*(");
            for (int i = 0; i < tracePids.size(); i++) {
                String pid = tracePids.get(i);
                if (i != 0)
                    pidRegex.append("|");
                pidRegex.append(pid);
            }
            pidRegex.append(")").append("[\\s|\\S]*");
            pidRegexStr = pidRegex.toString();
        }
        {
            StringBuilder tagRegex = new StringBuilder("[\\s|\\S]*(");
            for (int i = 0; i < tags.size(); i++) {
                if (i != 0) {
                    tagRegex.append("|");
                }
                tagRegex.append(tags.get(i));
            }
            tagRegex.append(")").append("[\\s|\\S]*");
            tagRegexStr = tagRegex.toString();
        }

    }

    static void run() {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.matches(pidRegexStr)) {
                if (line.matches(tagRegexStr)) {
                    if (include) {
                        print(line);
                    }
                } else {
                    if (!include) {
                        if (line.startsWith("E")) {
                            line = "*****" + line;
                        }
                        print(line);
                    }
                }
            }
        }
    }

    static FileWriter writer;

    private static void print(String str) {
        System.out.println(str);
        try {
            writer.write(str);
            writer.write("\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        args = new String[]{"aaa","vvv"};
        init(args);
        System.out.println(pidRegexStr);
        System.out.println(tagRegexStr);
        if (tracePids.size() > 0) {
            run();
        } else {
            System.out.println("didn't find pid to trace");
        }

    }
}


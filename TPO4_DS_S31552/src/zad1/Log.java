package zad1;

import java.util.ArrayList;
import java.util.List;

public class Log {
    private final List<String> list = new ArrayList<>();

    public Log(Object obj) {
        initLog(obj);
    }

    private void initLog(Object obj) {
        if (obj instanceof Server)
            list.add("=== Server log ===");
        else if (obj instanceof Client)
            list.add("=== Client log ===");
    }

    public void add(String msg) {
        list.add(msg);
    }

    public String getLog() {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s);
        }
        return sb.toString();
    }
}

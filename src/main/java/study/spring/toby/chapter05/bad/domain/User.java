package study.spring.toby.chapter05.bad.domain;

public class User {
    public static final int BASIC = 1;
    public static final int SILVER = 2;
    public static final int GOLD = 3;

    private String id;
    private String name;
    private String password;

    int level;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

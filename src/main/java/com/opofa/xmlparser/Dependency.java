package com.opofa.xmlparser;

class Dependency {
    private final String name;
    private final int level;

    Dependency(String name, int level) {
        this.level = level;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }
}

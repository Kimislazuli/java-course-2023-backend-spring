package edu.java.scrapper.dto.github;

public enum Actions {
    PUSH("push"),
    PR_MERGE("pr_merge"),
    OTHER("");

    final String text;

    Actions(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }
}

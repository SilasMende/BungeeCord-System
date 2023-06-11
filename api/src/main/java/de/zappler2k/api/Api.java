package de.zappler2k.api;

import lombok.Getter;

public class Api {

    @Getter
    public static Api instance;

    public Api() {
        instance = this;
    }
}

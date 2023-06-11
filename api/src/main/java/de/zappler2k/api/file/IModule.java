package de.zappler2k.api.file;

import java.io.File;

public interface IModule {

    File getFile();

    String toJson();

    IModule fromJson(String data);

    String getDefaultConfig();
}

package de.zappler2k;

import de.zappler2k.api.file.ModuleManager;
import de.zappler2k.api.mysql.MySQLConnnector;
import de.zappler2k.api.mysql.manager.MySQLManager;
import de.zappler2k.api.mysql.manager.impl.DataType;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeSystem extends Plugin {

    @Override
    public void onEnable() {
        ModuleManager moduleManager = new ModuleManager();
        MySQLConnnector mysql = new MySQLConnnector(moduleManager, this.getDataFolder().toString(), "mysql.json");
        MySQLManager manager = new MySQLManager(mysql, "test");
        manager.addValue(0, "uuid", DataType.VARCHAR.name() + "(255)");
        manager.addValue(1, "coins", DataType.BIGINT.name());
        manager.setPrimaryKey(0);
        manager.setupData();

    }

    @Override
    public void onDisable() {

    }
}

package de.zappler2k.api.mysql.manager;

import de.zappler2k.api.mysql.MySQLConnnector;
import de.zappler2k.api.mysql.manager.impl.MySQLObject;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;

public class MySQLManager {

    private HashMap<Integer, MySQLObject> objects;
    private MySQLConnnector mySQLConnnector;
    private String table_name;
    private Integer primaryKey;

    public MySQLManager(MySQLConnnector mySQLConnnector, String table_name) {
        objects = new HashMap<>();
        this.mySQLConnnector = mySQLConnnector;
        this.table_name = table_name;

    }
    public void setupData() {
        if(!mySQLConnnector.isConnected()) {
            System.out.println("Please connect to your database");
            return;
        }
        if(primaryKey == null) {
            System.out.println("You need to set a primary key.");
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < objects.size(); i++) {
            MySQLObject mySQLObject = objects.get(i);
                builder.append(mySQLObject.name).append(" ").append(mySQLObject.datatype).append(",");
        }
        String tableDefinition = builder.toString();
        if (!tableDefinition.isEmpty()) {
            tableDefinition = tableDefinition.substring(0, tableDefinition.length() - 1);
            mySQLConnnector.update("CREATE TABLE IF NOT EXISTS " + table_name + " (" + tableDefinition +  ",PRIMARY KEY("+ objects.get(primaryKey).getName() +"));");
        } else {
            System.out.println("No objects to create table.");
        }
    }
    public void addValue(Integer i, String name, String datatype) {
        objects.put(i, new MySQLObject(name, datatype));
    }
    public void setPrimaryKey(Integer i) {
        primaryKey = i;
    }

    @SneakyThrows
    public boolean objectsExists(Object primaryKey) {
        ResultSet rs = mySQLConnnector.query("SELECT * FROM " + table_name + " WHERE " + objects.get(this.primaryKey).getName() + "=?", primaryKey);
        if(rs.next()) {
            return rs.getObject("uuid") != null;
        }
        return false;
    }
    public void createObjects(Object... values) {
        if (objectsExists(Arrays.stream(values).findFirst().get())) {
            System.out.println("The Object already exists.");
            return;
        }
        StringBuilder columnNames = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < objects.size(); i++) {
            columnNames.append(objects.get(i).getName());
            placeholders.append("?");
            if (i < objects.size() - 1) {
                columnNames.append(",");
                placeholders.append(",");
            }
        }
        String query = String.format("INSERT INTO %s (%s) VALUES (%s)", table_name, columnNames.toString(), placeholders.toString());
        mySQLConnnector.update(query, values);
    }

    @SneakyThrows
    public Object getObject(Object primaryKey, String object_name) {
        Object obj = null;
        if(!objectsExists(primaryKey)) {
            System.out.println("The Object dos not exists.");
            return this;
        }
        ResultSet rs = mySQLConnnector.query("SELECT * FROM " + table_name + " WHERE " + objects.get(this.primaryKey).getName() + "=?", primaryKey);
        if((!rs.next() || rs.getObject(object_name) == null));
        obj = rs.getObject(object_name);
        return obj;
    }
    public void setObject(Object primaryKey, String object_name, Object value) {
        if(!objectsExists(primaryKey)) {
            System.out.println("The Object dos not exists.");
            return;
        }
        mySQLConnnector.update("UPDATE " + table_name + " SET " + object_name + "=? WHERE " + objects.get(this.primaryKey).getName() + "=?", value, primaryKey);
    }
    public void deleteObject(Object primaryKey) {
        if (!objectsExists(primaryKey)) {
            System.out.println("The Object does not exist.");
            return;
        }
        String query = String.format("DELETE FROM %s WHERE " + objects.get(this.primaryKey).getName() + "= ?", table_name);
        mySQLConnnector.update(query, primaryKey);
    }
}

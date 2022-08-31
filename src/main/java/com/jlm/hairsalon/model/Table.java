package com.jlm.hairsalon.model;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class Table {

    private static String  getTableName(Class cls){
        String [] tableNameBits = cls.getName().split("\\.");
        return tableNameBits[tableNameBits.length-1];
    }

    private static String getAttributeName(Field field){
        String [] fieldNameBits = field.getName().split("\\.");
        return fieldNameBits[fieldNameBits.length-1];
    }

    public static boolean create(Class cls) throws SQLException {
        int index = 0;

        StringBuilder CREATE_SQL_QUERY = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        String tableName = getTableName(cls);

        CREATE_SQL_QUERY.append(tableName).append("\n(\n");


        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields){
            String fieldName = getAttributeName(field);
            Entity entity = field.getAnnotation(Entity.class);
            String type = entity.type();
            int size = entity.size();
            boolean isnull = entity.isnull();
            boolean primary = entity.primary();

            CREATE_SQL_QUERY
                    .append(fieldName)
                    .append(" ")
                    .append(type);
            if(!type.equals("DATE") && !type.equals("DOUBLE")) {
                CREATE_SQL_QUERY
                        .append("(")
                        .append(size)
                        .append(")");
            }
            if (primary) {
                CREATE_SQL_QUERY.append(" AUTO_INCREMENT PRIMARY KEY");
            }

            if (isnull) {
                CREATE_SQL_QUERY.append(" NOT NULL");
            } else {
                CREATE_SQL_QUERY.append(" NULL");
            }

            index++;

            if (index == fields.length){
                CREATE_SQL_QUERY.append("\n");
            } else {
                CREATE_SQL_QUERY.append(",\n");
            }
        }

        index = 0;
        int numForeignKeys = 0;
        for (Field field : fields){
            String fieldName = getAttributeName(field);
            ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
            index++;
            if (foreignKey != null){
                String refTableName = foreignKey.table();
                String refAttrName = foreignKey.attribute();
                numForeignKeys++;
                if (numForeignKeys == 1) CREATE_SQL_QUERY.append(",");

                CREATE_SQL_QUERY.append("CONSTRAINT ").append(tableName)
                        .append("_").append(refTableName).append("_FK_").append(numForeignKeys)
                        .append("_").append(fieldName)
                        .append(" FOREIGN KEY (").append(fieldName)
                        .append(") REFERENCES ").append(refTableName).append("(")
                        .append(refAttrName).append(")")
                        .append("ON DELETE CASCADE");
                if (index < fields.length)
                    CREATE_SQL_QUERY.append(",");
                CREATE_SQL_QUERY.append("\n");
            }
        }
        CREATE_SQL_QUERY.append(")COLLATE=utf8mb4_unicode_ci;");
        System.out.println(CREATE_SQL_QUERY);
        return DatabaseConnection.CONNECTION.createStatement().execute(CREATE_SQL_QUERY.toString());
    }

    public void save() throws Exception {
        String tableName = getTableName(getClass());
        StringBuilder INSERT_SQL_QUERY = new StringBuilder();
        INSERT_SQL_QUERY.append("INSERT INTO ").append(tableName).append(" (id,");
        Field[] fields = getClass().getDeclaredFields();
        int index = 0;
        for (Field field : fields) {
            if (!field.getName().equals("id"))
                INSERT_SQL_QUERY.append(field.getName());
            index++;
            if (index > 1 && index < fields.length)
                INSERT_SQL_QUERY.append(", ");
        }
        INSERT_SQL_QUERY.append(") VALUES (null, ");
        index=0;
        for (Field field : fields) {
            if (!field.getName().equals("id")){
                INSERT_SQL_QUERY.append("?");
            }
            index++;
            if (index > 1 && index < fields.length)
                INSERT_SQL_QUERY.append(", ");
        }
        INSERT_SQL_QUERY.append(")");
        PreparedStatement stmt = DatabaseConnection.CONNECTION.prepareStatement(INSERT_SQL_QUERY.toString(), Statement.RETURN_GENERATED_KEYS);
        index = 1;
        for (Field field : fields) {
            if (!field.getName().equals("id")){
                stmt.setObject(index, field.get(this));
                index++;
            }
        }

        stmt.executeUpdate();

        ResultSet rs = stmt.getGeneratedKeys();

        Field id = this.getClass().getDeclaredField("id");
        if(rs.next())
        {
            id.set(this, rs.getInt(1));
        }
    }

    public void update() throws Exception {
        String tableName = getTableName(getClass());
        StringBuilder UPDATE_SQL_QUERY = new StringBuilder();
        UPDATE_SQL_QUERY.append("UPDATE ").append(tableName).append(" SET ");
        Field[] fields = getClass().getDeclaredFields();
        int index = 0;
        for (Field field : fields) {
            if (!field.getName().equals("id")) {
                UPDATE_SQL_QUERY.append(field.getName());
                UPDATE_SQL_QUERY.append("=?");
            }
            index++;
            if (index > 1 && index < fields.length)
                UPDATE_SQL_QUERY.append(", ");
        }
        Field id = this.getClass().getDeclaredField("id");
        UPDATE_SQL_QUERY.append(" WHERE id= ").append(id.get(this));
        PreparedStatement stmt = DatabaseConnection.CONNECTION.prepareStatement(UPDATE_SQL_QUERY.toString(), Statement.RETURN_GENERATED_KEYS);
        index = 1;
        for (Field field : fields) {
            if (!field.getName().equals("id")){
                stmt.setObject(index, field.get(this));
                index++;
            }
        }
        stmt.executeUpdate();
    }

    public void delete() throws Exception {
        String tableName = getTableName(getClass());
        Field id = this.getClass().getDeclaredField("id");
        PreparedStatement stmt = DatabaseConnection.CONNECTION.prepareStatement("DELETE FROM "+tableName+" WHERE id=?");
        stmt.setObject(1, id.get(this));
        stmt.executeUpdate();
    }

    public static Object get(Class cls, int id) throws Exception {
        String tableName = getTableName(cls);
        String SQL = "SELECT * FROM " + tableName +" WHERE id = " + id;
        Statement stmt = DatabaseConnection.CONNECTION.createStatement();
        ResultSet rs = stmt.executeQuery(SQL);
        if (rs.next()){
            Object obj = Class.forName(cls.getName()).newInstance();
            Class<?> otherCls = obj.getClass();
            for (Field f : otherCls.getDeclaredFields()){
                f.set(obj, rs.getObject(f.getName()));
            }
            return obj;
        } else {
            throw new Exception("No data with that id");
        }
    }

    public static List<?> list(Class cls) throws Exception {
        String tableName = getTableName(cls);
        String SQL = "SELECT * FROM " + tableName;
        Statement stmt = DatabaseConnection.CONNECTION.createStatement();
        ResultSet rs = stmt.executeQuery(SQL);

        List<Object> list = new ArrayList<>();
        while(rs.next()){
            Object obj = Class.forName(cls.getName()).newInstance();
            Class<?> otherCls = obj.getClass();
            for (Field f : otherCls.getDeclaredFields()){
                f.set(obj, rs.getObject(f.getName()));
            }
            list.add(obj);
        }
        return list;
    }

    public static List<Invoice> listTempTable(ResultSet rs) throws Exception {
        List<Invoice> list = new ArrayList<>();
        while(rs.next()){
            Invoice tt = new Invoice();
            tt.setId(rs.getInt(1));
            tt.setName(rs.getString(2));
            tt.setQuantity(rs.getDouble(3));
            tt.setPrice(rs.getDouble(4));
            tt.setSum();
            list.add(tt);
        }
        return list;
    }

    public static @NotNull List<ServicesRendered> servicesRendered(ResultSet rs) throws Exception {
        List<ServicesRendered> list = new ArrayList<>();
        while(rs.next()){
            ServicesRendered mc = new ServicesRendered();
            mc.setId(rs.getInt(1));
            mc.setServiceStock_FK(rs.getInt(2));
            mc.setTask_FK(rs.getInt(3));
            mc.setQuantity(rs.getDouble(4));
            list.add(mc);
        }
        return list;
    }

    public static List<HairdresserView> HairdresserTasks(ResultSet rs) throws Exception {
        List<HairdresserView> list = new ArrayList<>();
        while(rs.next()){
            HairdresserView hv = new HairdresserView();
            hv.setTaskId(rs.getInt(1));
            hv.setClientName(rs.getString(2));
            hv.setClientAddress(rs.getString(3));
            hv.setDate(rs.getString(4));
            hv.setClientId(rs.getInt(5));
            list.add(hv);
        }
        return list;
    }

}
package com.HW2.CRUD;

import com.HW2.Connection.ConnectionMySQL;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Crud<T> implements ICrud<T> {

    private String usedTable;
    private ConnectionMySQL connectionMySQL;


    public Crud(ConnectionMySQL connectionMySQL, String nameTable) {
        this.usedTable = nameTable;
        this.connectionMySQL = connectionMySQL;
    }

    //------------------------------------------------------------------------


    public void printlnTable(Class<T> type) {

        final char Y = '|';
        final char X = '-';
        final char Z = '+';
        List<T> list = getAll(type);
        List<Field> fields = getAnnotationField(DBField.class, type);

        int fieldSize = fields.size();
        int listSize = list.size();
        int maxSizes[] = new int[fieldSize];
        String[][] arrais = new String[fieldSize][];


        {

            int iteratorArrais = -1;
            for (Field field : fields) {
                field.setAccessible(true);

                String array[] = new String[list.size() + 1];
                int maxLength = 0;
                int iterator = -1;
                for (T t : list) {

                    if (iterator == -1) {
                        array[++iterator] = field.getName();
                        maxLength = array[iterator].length();
                        iterator++;
                    } else {
                        iterator++;
                    }
                    try {
                        Object obj = field.get(t);
                        array[iterator] = (obj == null ? "NULL" : obj.toString());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }


                    if (array[iterator] != null && array[iterator].length() > maxLength) {
                        maxLength = array[iterator].length();
                    }

                }
                arrais[++iteratorArrais] = array;
                maxSizes[iteratorArrais] = maxLength;
            }


            System.out.println(buldTable(maxSizes, X, Y, Z, list.size(), arrais));

        }
    }

    public StringBuffer buldTable(int[] maxSizes, char X, char Y, char Z, int ySize, String data[][]) {
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer separator = buldFlorSeparator(maxSizes, X, Z);


        for (int i0 = 0; i0 <= ySize; i0++) {
            stringBuffer.append(separator + "\n");
            for (int i = 0; i < data.length; i++) {

                int length = (data[i][i0].length() < maxSizes[i]) ? maxSizes[i] - data[i][i0].length() : 0;


                stringBuffer.append(Y + buldSeparatorElement(
                        (length == 1) ? length-- : (length = length / 2),
                        ' '
                        )
                );
                stringBuffer.append(data[i][i0] + buldSeparatorElement(length, ' '));

            }
            stringBuffer.append(Y);
            stringBuffer.append("\n");

        }

        return stringBuffer;

    }

    private String buldSeparatorElement(int size, char ch) {
        String string = "";
        while (size-- > 0) {
            string += ch;

        }
        return string;
    }

    private StringBuffer buldFlorSeparator(int[] maxSizes, char X, char Z) {
        StringBuffer separator = new StringBuffer();
        for (int i = 0; i < maxSizes.length; i++) {

            separator.append(Z);
            separator.append(buldSeparatorElement(maxSizes[i], X));

            if (i == maxSizes.length - 1) {
                separator.append(Z);
            }
        }
        return separator;
    }


    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void add(T t) {
        try {
            getConnectionMySQL().getStatement().executeUpdate(getRequestAdd(t));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private String getRequestAdd(T t) {
        String nameFill = null,
                dataFill = null;

        for (Field field : t.getClass().getDeclaredFields()) {
            if (field.getAnnotation(DBField.class) == null) {
                continue;
            }
            field.setAccessible(true);

            nameFill = elNameField(nameFill, field.getName());
            dataFill = elDataField(dataFill, objectField(field, t));
        }

        return "INSERT INTO " + getUsedTable() + "(" + nameFill + ") VALUES(" + dataFill + ")";
    }

    private Object objectField(Field f, Object obj) {
        try {
            return f.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String elDataField(String s, Object object) {
        if (s != null) {
            s += ", ";
        } else {
            s = "";
        }

        if (object instanceof String) {
            s += "'" + object + "'";

        } else {
            s += object;
        }
        return s;
    }

    private String elNameField(String s, String name) {
        if (s != null) {
            s += ", ";
        } else {
            s = "";
        }
        return s + name;
    }

    //-----------------------------------------------------------------------------------------------


    @Override
    public void set(T t) {

        try {
            getConnectionMySQL().getStatement().executeUpdate(getRequestSet(t));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private String getRequestSet(T t) {


        String values = null;
        String key = null;
        for (Field field : t.getClass().getDeclaredFields()) {
            field.setAccessible(true);


            if (field.getAnnotation(DBKey.class) != null) {
                if (key == null) {
                    key = " WHERE ";
                } else {
                    key += " AND ";
                }

                key += field.getName() + " = " + getObjectDB(field, t);
            }


            if (field.getAnnotation(DBField.class) == null) {
                continue;
            }


            if (values == null) {
                values = "";
            } else {
                values += ", ";
            }

            values += field.getName() + " = " + getObjectDB(field, t);
        }

        return "UPDATE " + getUsedTable() + " SET " + values + ((key == null) ? "" : key);
    }

    private String getObjectDB(Field field, T t) {
        Object object = objectField(field, t);
        if (object instanceof String) {
            return ("'" + object + "'");

        } else {
            return "" + object;
        }


    }


    //--------------------------------------------------------------------
    public List<T> getAll(Class<T> type) {

        try (ResultSet resultSet = getConnectionMySQL().getStatement().executeQuery(getRequestGetAll(type))) {
            List<Field> fields = getAnnotationField(DBField.class, type);
            List<T> list = new ArrayList<>();
            while (resultSet.next()) {


                try {
                    Object object = type.getConstructor().newInstance();

                    for (Field field : fields) {
                        field.setAccessible(true);
                        field.set(object, resultSet.getObject(field.getName()));
                    }

                    list.add((T) object);


                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }

            }

            return list;
        } catch (SQLException e) {

            e.printStackTrace();
            return null;
        }


    }

    private List<Field> getAnnotationField(Class<? extends Annotation> annotationClass, Class type) {
        List<Field> list = new ArrayList<>();

        for (Field field : type.getDeclaredFields()) {
            if (field.getAnnotation(annotationClass) != null) {
                list.add(field);
            }

        }
        return list;
    }


    private String getRequestGetAll(Class type) {
        String values = null;
        String key = null;
        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);


            if (field.getAnnotation(DBField.class) == null) {
                continue;
            }

            if (values == null) {
                values = field.getName();
            } else {
                values += ", " + field.getName();
            }

        }

        return ("SELECT " + values + " FROM " + getUsedTable());
    }


    //----------------------------------------------------------------------------

    @Override
    public boolean get(T t) {

        try (ResultSet resultSet = getConnectionMySQL().getStatement().executeQuery(getRequestGet(t))) {
            if (resultSet.next()) {

                for (Field field : t.getClass().getDeclaredFields()) {
                    field.setAccessible(true);

                    if (field.getAnnotation(DBField.class) == null) {
                        continue;
                    }

                    try {
                        field.set(t, resultSet.getObject(field.getName()));
                    } catch (IllegalAccessException e) {
                        continue;
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    private String getRequestGet(T t) {

        String values = null;
        String key = null;
        for (Field field : t.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (field.getAnnotation(DBKey.class) != null) {
                if (key == null) {
                    key = " WHERE ";
                } else {
                    key += " AND ";
                }

                key += field.getName() + " = " + getObjectDB(field, t);
            }


            if (field.getAnnotation(DBField.class) == null) {
                continue;
            }

            if (values == null) {
                values = field.getName();
            } else {
                values += ", " + field.getName();
            }

        }

        return "SELECT " + values + " FROM " + getUsedTable() + (key == null ? "" : key);
    }


    //---------------------------------------------------------------------------------
    @Override
    public void delete(T t) {
        try {

            getConnectionMySQL().getStatement().executeUpdate(getRequestDelete(t));
        } catch (java.sql.SQLIntegrityConstraintViolationException sql) {

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private String getRequestDelete(T t) {

        String key = null;
        for (Field field : t.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (field.getAnnotation(DBKey.class) == null) {
                continue;
            }

            if (key == null) {
                key = " WHERE ";
            } else {
                key += " AND ";
            }

            key += field.getName() + " = " + getObjectDB(field, t);
        }

        return "DELETE FROM " + getUsedTable() + key;
    }
    //---------------------------------------------------------------------------------

    public String getUsedTable() {
        return usedTable;
    }

    public void setUsedTable(String usedTable) {
        this.usedTable = usedTable;
    }

    public ConnectionMySQL getConnectionMySQL() {
        return connectionMySQL;
    }

    public void setConnectionMySQL(ConnectionMySQL connectionMySQL) {
        this.connectionMySQL = connectionMySQL;
    }
}

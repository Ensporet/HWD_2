package com.HW2;


import com.HW2.CRUD.Crud;
import com.HW2.CRUD.DBField;
import com.HW2.CRUD.DBReference;
import com.HW2.ClassFinder.ClassFinder;
import com.HW2.Connection.ConnectionMySQL;

import com.HW2.Requests.Reguest;
import com.HW2.Requests.Requests;
import com.HW2.Tables.Table;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class Main {

    public static ConnectionMySQL connectionMySQL;
    public static Scanner scanner = new Scanner(System.in);
    public static boolean currentIteration = true;
    public static boolean updateCurrentIteration = false;


    public static void main(String[] args) {

        Main main = new Main();
        main.hello();

        main.createConnection();

        main.selectRequests();

    }

    public void selectTable(String selectTable, Class typeTable) {

        HashMap<String, Action<String>> hashMap = updateSelectTable(selectTable, typeTable);

        Main.currentIteration = true;
        while (Main.currentIteration) {
            printlnHashMap(hashMap);
            actionToKey(hashMap);

            if (Main.updateCurrentIteration) {
                Main.updateCurrentIteration = false;
                hashMap = updateSelectTable(selectTable, typeTable);
            }
        }
        Main.currentIteration = true;


    }

    private HashMap<String, Action<String>> updateSelectTable(String selectTable, Class typeTable) {
        HashMap<String, Action<String>> hashMap = new HashMap<>();
        Crud crud = new Crud(Main.connectionMySQL, selectTable);
        int iteration = 0;

        for (Object obj : crud.getAll(typeTable)) {
            hashMap.put(String.valueOf(iteration++), createActionRow(obj, crud));
        }

        hashMap.put(String.valueOf(iteration++), createActionCreateRow(typeTable, crud));
        hashMap.put(String.valueOf(iteration++), createBask());
        hashMap.put(String.valueOf(iteration++), createActionExit());

        return hashMap;
    }


    private Action createActionCreateRow(Class type, Crud crud) {
        return new Action() {
            @Override
            public void act() {
                System.out.println("Create new row");

                Object obj = selectCreateNewRow(type);
                if (obj != null) {


                    crud.add(obj);
                }
                Main.updateCurrentIteration = true;


            }

            @Override
            public String name() {
                return "New row";
            }
        };
    }

    //--------------------------------------------------------------
    private Object selectCreateNewRow(Class type) {
        return selectCreateNewRow(type, null);
    }

    private Object selectCreateNewRow(Class type, Object defaultValue) {

        HashMap<String, Action> hashMap = new HashMap<>();
        int iteration = 0;

        for (Field field : type.getDeclaredFields()) {


            if (field.getAnnotation(DBField.class) == null) {
                continue;
            }
            field.setAccessible(true);

            Object value = null;
            try {
                value = (defaultValue == null) ? null : field.get(defaultValue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            hashMap.put(String.valueOf(iteration++), new Action<Object>(
                    value
            ) {


                @Override
                public void act() {

                    System.out.println("Set value " + this.name());


                    DBReference dbReference = field.getAnnotation(DBReference.class);
                    if (dbReference != null) {
                        Object object = selectReferenceColumn(dbReference);
                        if (object != null) {
                            this.setValue(object);
                        }
                        return;
                    }


                    Object object = setValueSelect(field.getType(), Main.scanner.nextLine(), null);
                    if (object != null) {
                        this.setValue(object);
                    }


                }

                @Override
                public String name() {
                    return field.getName();
                }

                @Override
                public String toString() {
                    return super.toString() + " .type value : " + field.getType().getName();
                }
            });
        }

        //accept changes
        Action actionChanges = new Action() {
            @Override
            public void act() {
                boolean ready = true;

                for (int i = 0; i < hashMap.size() - 3; i++) {
                    Action a = hashMap.get(String.valueOf(i));

                    if (a.getValue() == null) {
                        System.out.println(a.name() + "- mast be value !");
                        ready = false;
                    }
                }

                if (ready) {

                    try {
                        this.setValue(type.getConstructor().newInstance());

                        for (int i = 0; i < hashMap.size() - 3; i++) {
                            try {
                                Action action = hashMap.get(String.valueOf(i));
                                Field f = this.getValue().getClass().getDeclaredField(action.name());
                                f.setAccessible(true);
                                f.set(this.getValue(), action.getValue());
                                Main.currentIteration = false;
                            } catch (NoSuchFieldException e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public String name() {
                return "Accept changes";
            }
        };
        hashMap.put(String.valueOf(iteration++), actionChanges);

        hashMap.put(String.valueOf(iteration++), createBask());
        hashMap.put(String.valueOf(iteration++), createActionExit());

        Main.currentIteration = true;
        while (Main.currentIteration) {

            for (Map.Entry<String, Action> entry : hashMap.entrySet()) {
                System.out.println("[" + entry.getKey() + "] : " + entry.getValue().toString());
            }

            Action action = hashMap.get(Main.scanner.nextLine());
            if (action == null) {
                System.out.println("List not have this command !");
            } else {
                action.act();
            }


        }
        Main.currentIteration = true;


        return actionChanges.getValue();

    }


    private Action createActionRow(Object obj, Crud crud) {
        return new Action() {
            @Override
            public void act() {
                System.out.println(">>> Select" + obj);
                selectRow(obj, crud);
            }

            @Override
            public String name() {
                return obj.toString();
            }
        };
    }


    //..............
    private void selectRow(Object object, Crud crud) {
        HashMap<String, Action> hashMap = new HashMap<>();
        int index = 0;

        hashMap.put(String.valueOf(index++), new Action() {
            @Override
            public void act() {
                System.out.println("Set row " + object);
                Object obj = selectCreateNewRow(object.getClass(), object);
                if (obj != null) {
                    crud.set(obj);
                    Main.updateCurrentIteration = true;
                }

            }

            @Override
            public String name() {
                return "Set";
            }
        });

        hashMap.put(String.valueOf(index++), new Action() {
            @Override
            public void act() {
                System.out.println("Delete");
                crud.delete(object);
                Main.currentIteration = false;
                Main.updateCurrentIteration = true;

            }

            @Override
            public String name() {
                return "Delete";
            }
        });

        hashMap.put(String.valueOf(index++), createBask());
        hashMap.put(String.valueOf(index++), createActionExit());

        Main.currentIteration = true;
        while (Main.currentIteration) {
            for (Map.Entry<String, Action> entry : hashMap.entrySet()) {
                System.out.println("[" + entry.getKey() + "] : " + entry.getValue().toString());
            }

            Action action = hashMap.get(Main.scanner.nextLine());
            if (action == null) {
                System.out.println("List not have this command !");
            } else {
                action.act();
            }
        }
        Main.currentIteration = true;
    }

    //........................
    public Object selectReferenceColumn(DBReference reference) {
        HashMap<String, Action> hashMap = new HashMap<>();
        int index = 0;


        try (
                ResultSet resultSet =
                        Main.connectionMySQL.getStatement().executeQuery(getReferenceSRC(reference.table(),
                                reference.nameColumn()))) {

            while (resultSet.next()) {
                hashMap.put(String.valueOf(index++),
                        new Action<Object>(resultSet.getObject(1)) {
                            @Override
                            public void act() {
                                System.out.println("Select :" + toString());
                                Main.currentIteration = false;
                            }

                            @Override
                            public String name() {
                                return reference.nameColumn();
                            }
                        });

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        hashMap.put(String.valueOf(index++), createBask());
        hashMap.put(String.valueOf(index++), createActionExit());


        Main.currentIteration = true;
        Object object = null;
        while (Main.currentIteration) {
            for (Map.Entry<String, Action> entry : hashMap.entrySet()) {
                System.out.println("[" + entry.getKey() + "] : " + entry.getValue().toString());
            }


            Action action = hashMap.get(scanner.nextLine());
            if (action == null) {
                System.out.println("List not have this command !");
            } else {
                action.act();
                object = action.getValue();
            }


        }
        Main.currentIteration = true;
        return object;


    }

    private String getReferenceSRC(String nameTable, String nameColumn) {
        return "select " + nameColumn + " from " + nameTable;
    }

    private Object setValueSelect(Class type, String newValue, Object defaultValue) {
        Object object = defaultValue;

        if (type == Integer.class || type == int.class) {
            Integer integer = isInteger(newValue);
            if (integer == null) {
                System.out.println("Not format Integer");
            } else {
                object = integer;
            }
        } else {
            if (type == boolean.class || type == Boolean.class) {

                System.out.println("[0] : false\n[1] : true");
                String s = Main.scanner.nextLine();
                if (s != null && s.equals("0")) {
                    object = false;
                } else {
                    if (s != null && s.equals("1")) {
                        object = true;
                    } else {
                        System.out.println("Not tru select !");
                    }
                }

            } else {
                if (type == String.class) {
                    object = newValue;
                } else {
                    System.out.println("Not have type");
                }
            }
        }
        return object;

    }

    //-------------------------------------------------------

    public void selectTables() {

        HashMap<String, Action<String>> hashMap = new HashMap();
        final String PACK_TABLES = "com.HW2.Tables";
        int index = 0;


        for (Class cl : ClassFinder.find(PACK_TABLES)) {
            Action action = createTable(cl);
            if (action != null) {
                hashMap.put(String.valueOf(index++), action);
            }
        }

        hashMap.put(String.valueOf(index++), createBask());
        hashMap.put(String.valueOf(index++), createActionExit());

        Main.currentIteration = true;
        while (Main.currentIteration) {
            printlnHashMap(hashMap);
            actionToKey(hashMap);

        }
        Main.currentIteration = true;

    }

    private void actionToKey(HashMap<String, Action<String>> hashMap) {
        Action action = hashMap.get(scanner.nextLine());
        if (action == null) {
            System.out.println("List not have this command !");
        } else {
            action.act();
        }
    }

    private Action createBask() {
        return new Action() {
            @Override
            public void act() {
                System.out.println("Return...");
                Main.currentIteration = false;

            }

            @Override
            public String name() {
                return "<<<<";
            }
        };
    }

    private Action createTable(Class cl) {

        Table tableAnnotation = (Table) cl.getAnnotation(Table.class);

        if (tableAnnotation == null) {
            return null;
        }

        return new Action<Class>(cl) {
            @Override
            public void act() {
                System.out.println("Select table :" + this.name());
                selectTable(this.name(), this.getValue());
            }

            @Override
            public String name() {
                return tableAnnotation.name();
            }


            @Override
            public String toString() {
                return this.name();
            }
        };
    }


    //-----------------------------------------------------------------------
    private Action createCRUD() {

        return new Action() {
            @Override
            public void act() {
                System.out.println("Select tables");
                Main.currentIteration = false;
                selectTables();
            }

            @Override
            public String name() {
                return "All Tables";
            }
        };
    }

    //----------------------------------------------------------------------

    public void selectRequests() {

        Requests requests = new Requests();
        HashMap<String, Action<String>> hashMap = new HashMap<>();
        int numberSelect = 0;

        for (Method method : requests.getClass().getDeclaredMethods()) {


            Reguest reguest = method.getAnnotation(Reguest.class);
            if (reguest != null) {
                method.setAccessible(true);

                hashMap.put(String.valueOf(numberSelect++),
                        new Action<String>() {
                            @Override
                            public void act() {


                                try {
                                    if (method.getParameterCount() == 1) {
                                        System.out.println("Select id :");
                                        Integer i = isInteger(scanner.nextLine());
                                        if (i == null) {
                                            System.out.println("Is not format Integer");
                                        } else {

                                            method.invoke(requests, i);
                                        }
                                    } else {
                                        method.invoke(requests);
                                    }
                                    ;

                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }


                            }

                            @Override
                            public String name() {
                                return reguest.info();
                            }
                        });
            }
        }

        hashMap.put(String.valueOf(numberSelect++), createCRUD());
        hashMap.put(String.valueOf(numberSelect), createActionExit());

        Main.currentIteration = true;

        while (Main.currentIteration) {

            System.out.println("\nSelect request :");
            printlnHashMap(hashMap);

            Action action = hashMap.get(scanner.nextLine());
            if (action == null) {
                System.out.println("List not have this command !");
            } else {
                action.act();
            }


        }

    }

    //------------------------------
    private Integer isInteger(String string) {

        if (string == null || string.isEmpty() ||
                !string.matches(
                        "((\\+?0*" +
                                "(([1]\\d\\d\\d\\d\\d\\d\\d\\d\\d)|" +
                                "([2][0]\\d\\d\\d\\d\\d\\d\\d\\d)|" +
                                "([2][1][0-3]\\d\\d\\d\\d\\d\\d\\d)|" +
                                "([2][1][4][0-6]\\d\\d\\d\\d\\d\\d)|" +
                                "([2][1][4][7][0-3]\\d\\d\\d\\d\\d)|" +
                                "([2][1][4][7][4][0-7]\\d\\d\\d\\d)|" +
                                "([2][1][4][7][4][8][0-2]\\d\\d\\d)|" +
                                "([2][1][4][7][4][8][3][0-5]\\d\\d)|" +
                                "([2][1][4][7][4][8][3][6][0-3]\\d)|" +
                                "([2][1][4][7][4][8][3][6][4][0-7])|" +
                                "(\\d\\d?\\d?\\d?\\d?\\d?\\d?\\d?\\d?)))|(" +

                                "(-)0*" +
                                "(([1]\\d\\d\\d\\d\\d\\d\\d\\d\\d)|" +
                                "([2][0]\\d\\d\\d\\d\\d\\d\\d\\d)|" +
                                "([2][1][0-3]\\d\\d\\d\\d\\d\\d\\d)|" +
                                "([2][1][4][0-6]\\d\\d\\d\\d\\d\\d)|" +
                                "([2][1][4][7][0-3]\\d\\d\\d\\d\\d)|" +
                                "([2][1][4][7][4][0-7]\\d\\d\\d\\d)|" +
                                "([2][1][4][7][4][8][0-2]\\d\\d\\d)|" +
                                "([2][1][4][7][4][8][3][0-5]\\d\\d)|" +
                                "([2][1][4][7][4][8][3][6][0-3]\\d)|" +
                                "([2][1][4][7][4][8][3][6][4][0-8])|" +
                                "(\\d\\d?\\d?\\d?\\d?\\d?\\d?\\d?\\d?))))"
                )
        ) {
            return null;
        }
        return Integer.valueOf(string);
    }
    //----------------------------------------------------------------------

    public void createConnection() {

        HashMap<String, Action<String>> hashMap = new HashMap<>();

        hashMap.put("1", new Action<String>("Localhost:3306") {
            @Override
            public void act() {
                System.out.println("Entered " + this.name() + ": ");
                this.setValue(Main.scanner.nextLine());
            }

            @Override
            public String name() {
                return "SERVER_PATH";
            }
        });

        hashMap.put("2", new Action<String>("hw1") {
            @Override
            public void act() {
                System.out.println("Entered " + this.name() + ": ");
                this.setValue(Main.scanner.nextLine());
            }

            @Override
            public String name() {
                return "DB_NAME";
            }
        });

        hashMap.put("3", new Action<String>("root") {
            @Override
            public void act() {
                System.out.println("Entered " + this.name() + ": ");
                this.setValue(Main.scanner.nextLine());
            }

            @Override
            public String name() {
                return "DB_LOGIN";
            }
        });
        hashMap.put("4", new Action<String>("0755") {
            @Override
            public void act() {
                System.out.println("Entered " + this.name() + ": ");
                this.setValue(Main.scanner.nextLine());
            }

            @Override
            public String name() {
                return "DB_PASSWORD";
            }
        });


        hashMap.put("5", new Action<String>() {
            @Override
            public void act() {

                Main.connectionMySQL = new ConnectionMySQL(
                        hashMap.get("1").getValue(),
                        hashMap.get("2").getValue(),
                        hashMap.get("3").getValue(),
                        hashMap.get("4").getValue()
                );
                if (Main.connectionMySQL.getStatement() != null) {
                    System.out.println("Connection successful");
                    Main.currentIteration = false;
                } else {
                    System.out.println("Connection not successful");
                }
            }

            @Override
            public String name() {
                return "Connection";
            }
        });

        hashMap.put("6", createActionExit());

        System.out.println("Select parameters for connection : ");
        while (Main.currentIteration) {
            printlnHashMap(hashMap);
            Action action = hashMap.get(Main.scanner.nextLine());
            if (action != null) {
                action.act();
            } else {
                System.out.println("List not have this command !");
            }
        }

    }

    //------------------------------------------------
    private Action<String> createActionExit() {
        return new Action<String>() {
            @Override
            public void act() {
                System.out.println("Exit... ");
                System.exit(0);
            }

            @Override
            public String name() {
                return "Exit";
            }
        };
    }


    //----------------------------
    private void printlnHashMap(HashMap<String, Action<String>> hashMap) {

        for (Map.Entry<String, Action<String>> entry : hashMap.entrySet()) {
            System.out.println("[" + entry.getKey() + "] : " + entry.getValue().toString());
        }
    }


//---------------------------------------

    private abstract class Action<T> {

        private T value;

        public Action() {
        }

        public Action(T value) {
            this.value = value;
        }

        public abstract void act();

        public abstract String name();


        @Override
        public String toString() {
            return name() + ((getValue() == null) ? "" : "(" + getValue().toString() + ")");
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }
    }

    //------------------------------
    public void hello() {
        System.out.println("Start hw2");
    }


}
package com.HW2;


import com.HW2.Connection.ConnectionMySQL;
import com.HW2.Requests.Reguest;
import com.HW2.Requests.Requests;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static ConnectionMySQL connectionMySQL;
    public static Scanner scanner = new Scanner(System.in);
    public static boolean currentIteration = true;


    public static void main(String[] args) {

        Main main = new Main();
        main.hellow();

        main.createConnection();

        main.selectRequests();

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

        hashMap.put(String.valueOf(numberSelect), createActionExit());

        Main.currentIteration = true;

        while (Main.currentIteration) {

            System.out.println("\nSelect request :");
            printlHashMap(hashMap);

            Action action = hashMap.get(scanner.nextLine());
            if (action == null) {
                System.out.println("List not have this command !");
            } else {
                action.act();
            }


        }

    }

    //------------------------------
    protected Integer isInteger(String string) {
        char ch;
        int l = string.length();

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
            printlHashMap(hashMap);
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

    private void printlHashMap(HashMap<String, Action<String>> hashMap) {

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
    public void hellow() {
        System.out.println("Start hw2");
    }


}
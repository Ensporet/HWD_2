package com.HW2.Requests;

import com.HW2.CRUD.Crud;
import com.HW2.Main;
import com.HW2.Tables.DevSki;
import com.HW2.Tables.Developers;
import com.HW2.Tables.Skills;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;


public class Requests {

    //зарплату(сумму) всех разработчиков отдельного проекта;
    @Reguest(info = "salary (amount) of all developers of a separate project")
    public void salaryProject(int id_projects) {
        ProjectsCost projectsCost = new ProjectsCost(id_projects);

        if (new Crud(Main.connectionMySQL, "projects").get(projectsCost)) {
            System.out.println("Project : " + projectsCost.getName() + " cost " + projectsCost.getCost());
        } else {
            System.out.println("Table not have project with this id : " + id_projects);
        }
    }

    //список разработчиков отдельного проекта;
    @Reguest(info = "list of developers of a separate project")
    public void developersProgect(int id_projects) {

        try (ResultSet resultSet = Main.connectionMySQL.getStatement().executeQuery(getRequestDevPro(id_projects))) {

            while (resultSet.next()) {
                System.out.println(
                        "id " + resultSet.getObject("id_projects") + " " + resultSet.getObject("de.name") +
                                " " + resultSet.getObject("pr.name")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


    private String getRequestDevPro(int id_projects) {
        return "Select pr.id_projects , pr.name , de.name  from dev_pro dp\n" +
                "join developers de on de.id_developers = dp.id_developers \n" +
                "join projects pr on pr.id_projects = dp.id_projects\n" +
                "where pr.id_projects = " + id_projects;
    }

    //--------------------------------------
    @Reguest(info = "All java developers")
    public void allJavaDevelopers() {
        try (ResultSet resultSet = Main.connectionMySQL.getStatement().executeQuery(getRequestAllJava())) {

            while (resultSet.next()) {

                System.out.println(resultSet.getObject("de.name") + " " + resultSet.getObject("sk.name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        ;
    }


    private String getRequestAllJava() {
        return "Select de.name, sk.name from dev_ski ds\n" +
                "join developers de on de.id_developers = ds.id_developers\n" +
                "join skills sk on sk.id_skills = ds.id_skills\n" +
                "where sk.name = 'java'";
    }

    //-----------------------------

    @Reguest(info = "All developers who have middle skill")
    public void developersMiddle() {

        List<DevSki> listDevSki = new Crud(Main.connectionMySQL, "dev_ski").getAll(DevSki.class);
        List<Developers> developers = new Crud(Main.connectionMySQL, "developers").getAll(Developers.class);
        List<Skills> skills = new Crud(Main.connectionMySQL, "skills").getAll(Skills.class);


        for (int i = skills.size() - 1; i > -1; i--) {
            if (!skills.get(i).getLevel().equals("middle")) {
                skills.remove(i);
            }
        }

        for (int i = listDevSki.size() - 1; i > -1; i--) {
            boolean tr = false;

            for (Skills sk : skills) {
                if (tr = (listDevSki.get(i).getId_skills() == sk.getId_skills())) {
                    break;
                }
                ;
            }
            if (!tr) {
                listDevSki.remove(i);
            }
        }


        for (int i = developers.size() - 1; i > -1; i--) {
            boolean tr = false;
            for (DevSki devSki : listDevSki) {
                if (tr = (devSki.getId_developers() == developers.get(i).getId_developers())) {
                    break;
                }
            }
            if (!tr) {
                developers.remove(i);
            }
        }

        System.out.println("Developers have middle skills : ");
        for (Developers dev : developers) {
            System.out.println(
                    "id " + dev.getId_developers() + " " + dev.getName()
            );
        }

    }


    //список проектов в следующем формате: дата создания - название проекта - количество разработчиков на этом проекте.
    @Reguest(info = "Number of people working on projects")
    public void NPWP() {


        try (ResultSet resultSet = Main.connectionMySQL.getStatement().executeQuery(getRequestPAV())) {

            while (resultSet.next()) {
                System.out.println("id " + resultSet.getObject(1) + " " + resultSet.getObject(2) +
                        " size works developers : " + resultSet.getObject(3));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private String getRequestPAV() {
        return "select pr.id_projects,pr.name, count(de.id_developers) size from dev_pro dp\n" +
                "join developers de on de.id_developers = dp.id_developers\n" +
                "join projects pr on pr.id_projects = dp.id_projects\n" +
                "group by pr.id_projects";
    }


}

package com.example.jade;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.GUI, "true");
        ContainerController cc = rt.createMainContainer(p);

        try {
            AgentController seller1 = cc.createNewAgent("Seller1",
                    "com.example.jade.SellerAgent",
                    new Object[]{"The Hobbit:100", "1984:80"});
            seller1.start();

            AgentController seller2 = cc.createNewAgent("Seller2",
                    "com.example.jade.SellerAgent",
                    new Object[]{"Том Сойер:1", "1984:2"});
            seller2.start();

            AgentController seller3 = cc.createNewAgent("Seller3",
                    "com.example.jade.SellerAgent",
                    new Object[]{"Кортик:3", "1984:4"});
            seller3.start();

            AgentController seller4 = cc.createNewAgent("Seller4",
                    "com.example.jade.SellerAgent",
                    new Object[]{"Унесенные ветром:5", "1984:6"});
            seller4.start();

            AgentController customer = cc.createNewAgent("Customer",
                    "com.example.jade.CustomerAgent",
                    null);
            customer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

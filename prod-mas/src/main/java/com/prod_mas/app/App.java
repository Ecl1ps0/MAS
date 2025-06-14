package com.prod_mas.app;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class App {
    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) {
        Runtime rt = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.GUI, "true");

        ContainerController container = rt.createMainContainer(profile);
        try {
            AgentController worker = container.createNewAgent("worker", "com.prod_mas.agents.Worker", null);
            AgentController scheduler = container.createNewAgent("scheduler", "com.prod_mas.agents.Scheduler", null);
            worker.start();
            scheduler.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}

package com.prod_mas.app;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.prod_mas")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @PostConstruct
    @SuppressWarnings("CallToPrintStackTrace")
    public void startAgents() {
        new Thread(() -> {
            Runtime rt = Runtime.instance();
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.GUI, "false");

            ContainerController container = rt.createMainContainer(profile);
            try {
                AgentController worker = container.createNewAgent("worker", "com.prod_mas.agents.Worker", null);
                AgentController scheduler = container.createNewAgent("scheduler", "com.prod_mas.agents.Scheduler", null);
                worker.start();
                scheduler.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

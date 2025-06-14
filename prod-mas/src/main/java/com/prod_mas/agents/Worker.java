package com.prod_mas.agents;

import com.prod_mas.utils.ModelTrainer;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class Worker extends Agent{
    final private ModelTrainer trainer = new ModelTrainer();
    final private String modelPath = "D:\\VSCode Projects\\MAS\\prod-mas\\src\\main\\java\\com\\prod_mas\\ml_model\\main.exe";

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    protected void setup() {
        // Register to Yellow Pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName("training-service");
        sd.setType("model-trainer");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null && "start-training".equals(msg.getContent())) {
                    trainer.startTraining(modelPath);
                } else {
                    block();
                }
            }
        });
    }
}
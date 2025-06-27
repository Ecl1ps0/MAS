package com.prod_mas.agents;

import com.prod_mas.utils.ModelTrainer;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class Worker extends Agent {
    final private ModelTrainer trainer = new ModelTrainer();

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
                if (msg != null) {
                    trainer.startTraining(msg.getContent());
                } else {
                    block();
                }
            }
        });
    }
}
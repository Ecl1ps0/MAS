package com.prod_mas.agents;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class Scheduler extends Agent {
    @Override
    protected void setup() {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(getAID("worker"));
                msg.setContent("start-training");
                send(msg);
                System.out.println("Sent training request to worker.");
            }
        });
    }
}

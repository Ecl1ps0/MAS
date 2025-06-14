package com.example.jade;

import java.util.HashMap;
import java.util.Map;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class SellerAgent extends Agent {
    final private Map<String, Integer> catalogue = new HashMap<>();

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                String entry = (String) arg;
                String[] parts = entry.split(":");
                if (parts.length == 2) {
                    String title = parts[0];
                    int price = Integer.parseInt(parts[1]);
                    catalogue.put(title, price);
                }
            }
        } else {
            System.out.println(getLocalName() + ": No catalog specified.");
        }

        // Register service to Yellow Pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("book-selling");
        sd.setName("book-trading");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Message handling behavior
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null && msg.getPerformative() == ACLMessage.CFP) {
                    String bookTitle = msg.getContent();
                    ACLMessage reply = msg.createReply();

                    if (catalogue.containsKey(bookTitle)) {
                        reply.setPerformative(ACLMessage.PROPOSE);
                        reply.setContent(String.valueOf(catalogue.get(bookTitle)));
                    } else {
                        reply.setPerformative(ACLMessage.REFUSE);
                        reply.setContent("not-available");
                    }
                    send(reply);
                }
            }
        });
    }
}

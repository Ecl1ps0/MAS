package com.example.jade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CustomerAgent extends Agent {
    private final String targetBook = "1984";
    final private List<AID> sellerAgents = new ArrayList<>();
    final private Map<AID, Integer> proposals = new HashMap<>();

    @Override
    protected void setup() {
        System.out.println(getLocalName() + ": started.");

        SequentialBehaviour seq = new SequentialBehaviour();

        // Step 1: Search for sellers
        seq.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("book-selling");
                template.addServices(sd);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    for (DFAgentDescription df : result) {
                        sellerAgents.add(df.getName());
                    }
                    if (sellerAgents.isEmpty()) {
                        System.out.println("No seller agents found.");
                    }
                } catch (FIPAException fe) {
                    fe.printStackTrace();
                }
            }
        });

        // Step 2: Send CFP
        seq.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                for (AID seller : sellerAgents) {
                    cfp.addReceiver(seller);
                }
                cfp.setContent(targetBook);
                cfp.setConversationId("book-trade");
                cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique ID
                myAgent.send(cfp);

                // Prepare template for replies
                MessageTemplate mt = MessageTemplate.and(
                    MessageTemplate.MatchConversationId("book-trade"),
                    MessageTemplate.MatchInReplyTo(cfp.getReplyWith())
                );

                // Add reply-handling behavior
                myAgent.addBehaviour(new OneShotBehaviour() {
                    @Override
                    public void action() {
                        int repliesReceived = 0;
                        AID bestSeller = null;
                        int bestPrice = Integer.MAX_VALUE;

                        while (repliesReceived < sellerAgents.size()) {
                            ACLMessage reply = myAgent.blockingReceive(mt, 5000);
                            if (reply != null) {
                                if (reply.getPerformative() == ACLMessage.PROPOSE) {
                                    int price = Integer.parseInt(reply.getContent());
                                    proposals.put(reply.getSender(), price);
                                    if (price < bestPrice) {
                                        bestPrice = price;
                                        bestSeller = reply.getSender();
                                    }
                                }
                                repliesReceived++;
                            } else {
                                break;
                            }
                        }

                        if (bestSeller != null) {
                            System.out.println("Best offer is " + bestPrice + " from " + bestSeller.getLocalName());

                            // Send ACCEPT_PROPOSAL to best seller
                            ACLMessage accept = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                            accept.addReceiver(bestSeller);
                            accept.setContent(targetBook);
                            accept.setConversationId("book-trade");
                            myAgent.send(accept);
                        } else {
                            System.out.println("No valid proposals received.");
                        }
                    }
                });
            }
        });

        addBehaviour(seq);
    }
}

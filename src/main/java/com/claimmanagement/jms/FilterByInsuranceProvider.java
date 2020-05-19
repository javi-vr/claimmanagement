package com.claimmanagement.jms;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class FilterByInsuranceProvider {

	public static void main(String[] args) throws NamingException, JMSException {
		InitialContext initialContext = new InitialContext();
		Queue claimQueue = (Queue) initialContext.lookup("queue/claimQueue");

		try(ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
			JMSContext jmsContext = cf.createContext()) {

			JMSProducer producer = jmsContext.createProducer();
			JMSConsumer consumer = jmsContext.createConsumer(claimQueue, "insuranceProvider IN ('Blue Cross', 'American')");
			ObjectMessage objectMessage = jmsContext.createObjectMessage();

			objectMessage.setStringProperty("insuranceProvider", "Blue Cross");

			Claim claim = new Claim();
			claim.setHospitalId(1);
			claim.setClaimAmount(10000);
			claim.setDoctorName("Bob");
			claim.setDoctorName("Fisher");
			claim.setInsuranceProvider("Blue Cross");

			objectMessage.setObject(claim);
			producer.send(claimQueue, objectMessage);

			Claim receiveBody = consumer.receiveBody(Claim.class);
			System.out.println("Claim Amount: " + receiveBody.getClaimAmount());
		}
	}
}

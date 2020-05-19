package com.claimmanagement.jms;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class FilterByDoctorName {

	public static void main(String[] args) throws NamingException, JMSException {
		InitialContext initialContext = new InitialContext();
		Queue claimQueue = (Queue) initialContext.lookup("queue/claimQueue");

		try(ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
			JMSContext jmsContext = cf.createContext()) {

			JMSProducer producer = jmsContext.createProducer();
			JMSConsumer consumer = jmsContext.createConsumer(claimQueue, "doctorName LIKE 'H%'");
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

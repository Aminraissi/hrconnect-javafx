package utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

public class SmsTwilio {
    private final String ACCOUNT_SID = ConfigReader.get("HAYTHEM_TWILIO_SID");
    private final String AUTH_TOKEN = ConfigReader.get("HAYTHEM_TWILIO_TOKEN");

    public void send(int phoneNumber, String body) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = Message.creator(
                new com.twilio.type.PhoneNumber("+216"+phoneNumber),
                new com.twilio.type.PhoneNumber("+12207664761"),
                body
        ).create();

        System.out.println("SMS sent with SID: " + message.getSid());
    }
}

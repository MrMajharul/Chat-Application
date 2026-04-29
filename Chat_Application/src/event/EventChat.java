package event;

import model.Model_Receive_Message;
import model.Model_Send_Message;

public interface EventChat {

    public void sendMessage(Model_Send_Message data);

    public void receiveMessage(Model_Receive_Message data);

    public void updateMessageStatus(int messageID, int status);

    public void messageAcked(String clientId, int messageID, long createdAt, int status);
}

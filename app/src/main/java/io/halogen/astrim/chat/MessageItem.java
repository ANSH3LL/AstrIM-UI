package io.halogen.astrim.chat;

public class MessageItem {
    private String senderAlias;
    private String messageBody;
    private String creationTime;
    private String messageID;

    MessageItem(String senderAlias, String messageBody, String creationTime, String messageID){
        this.senderAlias = senderAlias;
        this.messageBody = messageBody;
        this.creationTime = creationTime;
        this.messageID = messageID;
    }

    public String getSenderAlias(){
        return this.senderAlias;
    }

    public String getMsgBody(){
        return this.messageBody;
    }

    public String getCreationTime(){
        return this.creationTime;
    }

    public String getMsgID(){
        return this.messageID;
    }
}

package hu.d2.offsiteinc.util;



public class UIThrowable extends Throwable {

    private int messageId;

    public UIThrowable(int messageId) {
        this.messageId = messageId;
    }

    public int getMessageId() {
        return messageId;
    }
}

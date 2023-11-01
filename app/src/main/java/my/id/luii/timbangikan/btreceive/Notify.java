package my.id.luii.timbangikan.btreceive;

public interface Notify {
    void connectionSuccessful();
    void messageIncomming(String message);
    void dataReceiveDone(float datakg);
    void needReconnect(boolean hasil);
}

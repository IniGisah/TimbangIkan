package my.id.luii.timbangikan.btreceive;

public interface Notify {
    void connectionSuccessful();
    void messageIncomming(String message);
    void dataReceiveDone(String datakg);
    void needReconnect(boolean hasil);
}

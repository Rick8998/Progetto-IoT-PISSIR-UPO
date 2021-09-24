package it.uniupo.pissir.iot;

import org.eclipse.paho.client.mqttv3.*;

public class SensoreFumoPublisher {
    private MqttClient client;
    private String id;
    private String idLocale;
    public SensoreFumoPublisher(String id, String locale){
        this.id = id;
        this.idLocale = locale;
        String messageBrokerURL = "tcp://localhost:1883";
        String clientId = MqttClient.generateClientId();
        try {
            client = new MqttClient(messageBrokerURL, clientId);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * Questa funzione è la rilevaMisure dei diagrammi
     */
    public void start(){
        MqttConnectOptions connectionOptions = new MqttConnectOptions();
        connectionOptions.setCleanSession(false);
        connectionOptions.setWill(client.getTopic("home/LTW"), "I am gone!".getBytes(), 0, false);
        try {
            client.connect(connectionOptions);
            publishFumo();
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /*simulazione dell'interrogazione di un sensore di fumo che rappresenta la presenza di fumo con un rapporto
    particelleFumo/particelleOssigeno rappresentato su una scala da 0 a 10 (0 = no fumo) (10 = max fumo)*/
    private void publishFumo() throws MqttException {
        //String intensitaFumo = "2/10";
        Misura misuraSmoke = new Misura(idLocale, "6", "Fumo/Ossigeno", "10-8-2021", "16:50");
        MqttTopic topicFumo;
        if(this.idLocale.contains("SalaAttesa")){
            topicFumo = client.getTopic("sensori/"+idLocale+"/smoke/"+id);
        }else {
            topicFumo = client.getTopic("sensori/office" + idLocale + "/smoke/" + id);
        }
        topicFumo.publish(new MqttMessage(misuraSmoke.toString().getBytes()));
    }
}

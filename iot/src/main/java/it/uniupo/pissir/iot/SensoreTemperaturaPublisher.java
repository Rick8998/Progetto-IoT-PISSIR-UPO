package it.uniupo.pissir.iot;

import netscape.javascript.JSObject;
import org.eclipse.paho.client.mqttv3.*;
import java.nio.charset.StandardCharsets;

public class SensoreTemperaturaPublisher extends Thread{
    private MqttClient client;
    private String id;
    private String idLocale;

    public SensoreTemperaturaPublisher(String id, String locale){
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
     * Questa funzione è la rlevaMisure dei diagrammi
     */
    public void rilevaMisure(){
        MqttConnectOptions connectionOptions = new MqttConnectOptions();
        connectionOptions.setCleanSession(false);
        connectionOptions.setWill(client.getTopic("home/LTW"), "I am gone!".getBytes(), 0, false);
        try {
            client.connect(connectionOptions);
            publishTemperature();
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        rilevaMisure();
    }

    /**
     * Metodo che simula l'interrogazione di un sensore e la pubblicazione della temperatura
     */
    private void publishTemperature() throws MqttException {
        for(int i = 0; i < 6; i++){
            String misura = "2";
            String s = String.valueOf(i);
            Misura misuraTemp = new Misura(idLocale, misura+s, "C", "10-8-2021", "16:50");
            //String temperatura = "20 C";
            MqttTopic topicTemperatura;
            if(this.idLocale.contains("SalaAttesa")){
                topicTemperatura = client.getTopic("sensori/"+idLocale+"/temperature/"+id);
            }else{
                topicTemperatura = client.getTopic("sensori/office"+idLocale+"/temperature/"+id);
            }
            System.out.println("\nTemperatura : " + misura+s + " nel locale "+idLocale+"\n");
            topicTemperatura.publish(new MqttMessage(misuraTemp.toString().getBytes()));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Test main
     * @param args

    public static void main(String[] args) {
        SensoreTemperaturaPublisher sensoreTep = new SensoreTemperaturaPublisher("1");
        sensoreTep.start();
    }*/
}
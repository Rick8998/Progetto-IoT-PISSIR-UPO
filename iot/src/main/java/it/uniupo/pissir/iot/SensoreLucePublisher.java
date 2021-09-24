package it.uniupo.pissir.iot;

import org.eclipse.paho.client.mqttv3.*;

public class SensoreLucePublisher extends Thread{
    MqttClient client;
    private String id;
    private String idLocale;
    public SensoreLucePublisher(String id , String locale){
        this.id = id;
        this.idLocale = locale;
        String messageBrokerURL = "tcp://localhost:1883";
        String clientId = MqttClient.generateClientId();
        try {
            this.client = new MqttClient(messageBrokerURL, clientId);
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
            publishLuce();
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
     * Metodo che simula l'interrogazione di un sensore dell'intensità luminosa dell'ambiente e la sua pubblicazione
     * sul topic
     */
    private void publishLuce() throws MqttException {
        int misuraInt = 80;
        for(int i = 0; i < 6; i++){
            String mesure = String.valueOf(misuraInt);
            Misura misuraLux = new Misura(idLocale, mesure, "bri", "10-8-2021", "16:50");
            MqttTopic topicLuce;
            if(this.idLocale.contains("SalaAttesa")){
                //System.out.println("Pubblico " + misuraLux.toString());
                topicLuce = client.getTopic("sensori/"+idLocale+"/lux/"+id);
            }else {
                topicLuce = client.getTopic("sensori/office" + idLocale + "/lux/" + id);
            }
            topicLuce.publish(new MqttMessage(misuraLux.toString().getBytes()));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            misuraInt = misuraInt-(10);
        }
    }
}

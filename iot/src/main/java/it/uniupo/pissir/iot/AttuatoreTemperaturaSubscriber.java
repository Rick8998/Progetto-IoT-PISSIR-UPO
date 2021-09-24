package it.uniupo.pissir.iot;

import org.eclipse.paho.client.mqttv3.*;

import java.util.ArrayList;
import java.util.List;

public class AttuatoreTemperaturaSubscriber extends Thread{

    private MqttClient client;
    private float statoTemperatura;
    private String idLocale;

    public AttuatoreTemperaturaSubscriber(String idLocale){
        this.idLocale = idLocale;
        String broker_url = "tcp://localhost:1883";
        String clientId = MqttClient.generateClientId();
        try{
            client = new MqttClient(broker_url, clientId);
        }catch (MqttException e){
            e.printStackTrace();
        }
    }

    public float getStatoTemperatura() {
        return statoTemperatura;
    }

    /*questa è la funzione esegueComandi dei diagrammi*/
    public void esegueComandi(){
        try{
            /*Per mettersi in ascolto, il subscriber deve settare una callback (classe callback con metodi asincroni
             * chiamati nel momento in cui ci sarà la ricezione del messaggio*/
            client.setCallback(new AttuatoreTemperaturaCallback());
            /*Il subscriber si connette al client*/
            client.connect();

            /*Il subscriber si mette in ascolto su un particolare topic */
            final String topic;
            if(idLocale.contains("SalaAttesa")){
                topic = "attuatori/"+idLocale+"/changeTemperature";
            } else{
                topic  = "attuatori/ufficio"+idLocale+"/changeTemperature";
            }
            /*Subscribe su quel topic, per ricevere i messaggi che saranno pubblicati su quel topic*/
            client.subscribe(topic);
            System.out.println("The subscriber is now listening to " + topic);

        }catch(MqttException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        esegueComandi();
    }

    private class AttuatoreTemperaturaCallback implements MqttCallback {

        @Override
        public void connectionLost(Throwable cause) { /*do nothing*/ }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            /*estrapolo la temperatura e cambio lo stato dell'attuatore*/
            //System.out.println(message.toString());
            String messageToString = message.toString();
            float tempChange = extrapolateTemperatureValue(messageToString);
            changeTemperature(tempChange);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) { /*do nothing*/ }
    }

    /*Cambia la temperatura del locale
    * Nella simulazione la temperatura è un campo dell'attuatore, in realtà verrebbe erogata qualla temperatura*/
    private void changeTemperature(float tempChange) {
        if(tempChange != -1){
            this.statoTemperatura = tempChange;
            System.out.println("\nTemperatura aggiornata: "+this.statoTemperatura+"\n");
        }
    }

    /*Estrapola il valore della misura dal messaggio mqtt*/
    private float extrapolateTemperatureValue(String messageToString) {
        if(messageToString != null){
            List<String> list = new ArrayList<>();
            String valueTempString = null;
            String token[];
            String s1 = null;
            String tempRes = null;
            float valueTempNumeric;
            token = messageToString.split(",");

            while(s1 == null){
                for(String t : token) {
                    if (s1 == null && t.contains("valoreMisurazione")) s1 = t;
                }
            }
            //System.out.println(s1);
            token = s1.split("=");

            /*for (String s : token){
                System.out.println(s);
            }*/
            //while(tempRes == null){
                for(String t : token)
                    if(!t.contains("valoreMisurazione")) {
                        valueTempString = t.replace("'", "");
                    }
           // }
            /*for (String s : list){
                System.out.println("inList: "+s);
            }*/
            //System.out.println(valueTempString);
            valueTempNumeric = Float.parseFloat(valueTempString);
            //System.out.println("valueTempNumeric " +valueTempNumeric);

            return valueTempNumeric;
        }
        return -1;
    }

    public void stopAttuatoreTemperatura(){
        System.out.println("L'attuatore della temperatura si è fermato");
    }

    /*public static void main(String[] args) {
        AttuatoreTemperaturaSubscriber att = new AttuatoreTemperaturaSubscriber("1");
        att.start();
    }*/
}
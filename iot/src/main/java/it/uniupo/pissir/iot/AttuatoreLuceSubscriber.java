package it.uniupo.pissir.iot;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttuatoreLuceSubscriber extends Thread{
    private MqttClient client;
    private  String idUfficio;
    public AttuatoreLuceSubscriber(String idUfficio){
        this.idUfficio = idUfficio;
        String broker_url = "tcp://localhost:1883";
        String clientId = MqttClient.generateClientId();
        try{
            client = new MqttClient(broker_url, clientId);
        }catch (MqttException e){
            e.printStackTrace();
        }
    }
    /*questa è la funzione esegueComandi dei diagrammi*/
    public void esegueComandi(){
        try{
            /*Per mettersi in ascolto, il subscriber deve settare una callback (classe callback con metodi asincroni
             * chiamati nel momento in cui ci sarà la ricezione del messaggio*/
            client.setCallback(new AttuatoreLuceCallBack());
            /*Il subscriber si connette al client*/
            client.connect();

            /*Il subscriber si mette in ascolto su un particolare topic*/
            final String topic;
            if(idUfficio.contains("SalaAttesa")){
                topic = "attuatori/"+idUfficio+"/changeLux";
            } else{
                topic  = "attuatori/ufficio"+idUfficio+"/changeLux";
            }

            /*Subscribe sul topic, per ricevere i messaggi che saranno pubblicati su quel topic*/
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

    private class AttuatoreLuceCallBack implements MqttCallback {

        @Override
        public void connectionLost(Throwable cause) { /*do nothing*/ }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            /*estrapolo la temperatura e cambio lo stato delle lampadine*/
            //System.out.println("AttuatoreLuceSub   "+message.toString());
            String messageToString = message.toString();
            float luxValue = extrapolateLuxValue(messageToString);
            //System.out.println("LuxChange: "+luxValue+"\n");
            if(luxValue != -1){
                changeLightInensity(luxValue);
            }

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) { /*do nothing*/ }
    }

    /*Estrapola il valore di luminosità dal messaggio mqtt*/
    private float extrapolateLuxValue(String messageToString) {
        if(messageToString != null){
            String valueLuxString = null;
            String token[];
            String s1 = null;
            String luxRes = null;
            float valueLuxNumeric;
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
            //while(valueLuxString == null){
                for(String t : token)
                    if(!t.contains("valoreMisurazione")){
                        valueLuxString = t.replace("'", "");

                    }
            //}
            /*for (String s : list){
                System.out.println("inList: "+s);
            }*/
            //System.out.println(valueTempString);
            valueLuxNumeric = Float.parseFloat(valueLuxString);
            //System.out.println("valueLuxNumeric " +valueLuxNumeric);

            return valueLuxNumeric;
        }
        return -1;
    }

    /*Cambia il valore dell'intensità delle luci in base a ciò che ha ricevuto nel messaggio mqtt*/
    private synchronized void changeLightInensity(float luxChange) {
        if(luxChange == 0){
            /*Se la luminosità da settare è pari a 0 => devo spegnere le luci*/
            String baseURL = "http://localhost:8000";
            String username = "newdeveloper";
            String lightsURL = baseURL + "/api/" + username + "/lights/";
            RestTemplate rest = new RestTemplate();
            Map<String, ?> allLights = rest.getForObject(lightsURL, HashMap.class);
            if (allLights != null) {
                org.springframework.http.HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                String color = "{\"on\":false}";
                HttpEntity<String> request = new HttpEntity<>(color, headers);
                for (String light : allLights.keySet()) {
                    String callURL = lightsURL + light + "/state";
                    rest.put(callURL, request);
                }
            }
        }
        else if(luxChange != -1){
            /*Lavoro sull'intensità luminosa delle lampadine*/
            String baseURL = "http://localhost:8000";
            String username = "newdeveloper";
            String lightsURL = baseURL + "/api/" + username + "/lights/";
            RestTemplate rest = new RestTemplate();
            Map<String, ?> allLights = rest.getForObject(lightsURL, HashMap.class);
            if (allLights != null) {
                org.springframework.http.HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                String color = "{\"on\":true,\"bri\":"+luxChange+",\"sat\":\"254\", \"hue\":\"10000\"}";
                HttpEntity<String> request = new HttpEntity<>(color, headers);
                for (String light : allLights.keySet()) {
                    String callURL = lightsURL + light + "/state";
                    rest.put(callURL, request);
                }
            }
        }
    }

    public void stopAttuatoreLuce(){
        System.out.println("L'attuatore della luce si è fermato");
        String baseURL = "http://localhost:8000";
        String username = "newdeveloper";
        String lightsURL = baseURL + "/api/" + username + "/lights/";
        RestTemplate rest = new RestTemplate();
        Map<String, ?> allLights = rest.getForObject(lightsURL, HashMap.class);
        if (allLights != null) {
            org.springframework.http.HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String color = "{\"on\":false}";
            HttpEntity<String> request = new HttpEntity<>(color, headers);
            for (String light : allLights.keySet()) {
                String callURL = lightsURL + light + "/state";
                rest.put(callURL, request);
            }
        }
    }

    /*public static void main(String[] args) {
        AttuatoreLuceSubscriber att = new AttuatoreLuceSubscriber("1");
        att.start();
    }*/
}

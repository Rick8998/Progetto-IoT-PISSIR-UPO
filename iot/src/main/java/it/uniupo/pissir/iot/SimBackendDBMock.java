package it.uniupo.pissir.iot;

import java.util.ArrayList;
import java.util.List;

public class SimBackendDBMock {
    private List<Prenotazione> simDBPrenotazioni;

    public SimBackendDBMock() {
        Prenotazione prenotazione1 = new Prenotazione("1", "14-09-21", "15", "1", "1", 5, 1);
        Prenotazione prenotazione2 = new Prenotazione("2", "14-09-21", "16", "2", "2", 3, 1);
        Prenotazione prenotazione3 = new Prenotazione("3", "17-09-21", "16", "4", "3", 7, 1);
        Prenotazione prenotazione4 = new Prenotazione("4", "16-09-21", "18", "1", "4", 2, 2);
        Prenotazione prenotazione5 = new Prenotazione("5", "14-09-21", "10", "3", "5", 4, 2);
        this.simDBPrenotazioni = new ArrayList<Prenotazione>();
        simDBPrenotazioni.add(prenotazione1);
        simDBPrenotazioni.add(prenotazione2);
        simDBPrenotazioni.add(prenotazione3);
        simDBPrenotazioni.add(prenotazione4);
        simDBPrenotazioni.add(prenotazione5);
    }

    public ArrayList<Prenotazione> simGetPrenotazioneByHourAndDate(String hour, String date){
        ArrayList<Prenotazione> res = new ArrayList<Prenotazione>();
        for(Prenotazione p : simDBPrenotazioni){
            if(p.getData().contains(date) && p.getOra().contains(hour)){
                res.add(p);
            }
        }
        return res;
    }
}

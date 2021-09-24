package it.uniupo.pissir.iot;

public class Prenotazione {
    private String codice;
    private String data;
    private String ora;
    private String idUfficio;
    private String idProfessionista;
    private int stimaClienti;
    private int durata;

    public Prenotazione(String codice, String data, String ora, String idUfficio, String idProfessionista, int stimaClienti, int durata){
        this.codice=codice;
        this.data=data;
        this.ora=ora;
        this.idUfficio=idUfficio;
        this.idProfessionista=idProfessionista;
        this.stimaClienti=stimaClienti;
        this.durata=durata;
    }

    @Override
    public String toString() {
        return "Prenotazione{" +
                "codice='" + codice + '\'' +
                ", data='" + data + '\'' +
                ", ora='" + ora + '\'' +
                ", idUfficio='" + idUfficio + '\'' +
                ", idProfessionista='" + idProfessionista + '\'' +
                ", stimaClienti=" + stimaClienti +
                ", durata=" + durata +
                '}';
    }

    public String getCodice() {
        return codice;
    }

    public String getData() {
        return data;
    }

    public String getOra() {
        return ora;
    }

    public String getIdUfficio() {
        return idUfficio;
    }

    public String getIdProfessionista() {
        return idProfessionista;
    }

    public int getStimaClienti() {
        return stimaClienti;
    }

    public int getDurata() {
        return durata;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }

    public void setIdUfficio(String idUfficio) {
        this.idUfficio = idUfficio;
    }

    public void setIdProfessionista(String idProfessionista) {
        this.idProfessionista = idProfessionista;
    }

    public void setStimaClienti(int stimaClienti) {
        this.stimaClienti = stimaClienti;
    }

    public void setDurata(int durata) {
        this.durata = durata;
    }


}

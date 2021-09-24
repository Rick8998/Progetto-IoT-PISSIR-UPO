package it.uniupo.pissir.iot;

public class Misura {
    private String idLocale;
    private String valoreMisurazione;
    private String unitaDiMisura;
    private String data;
    private String ora;

    public Misura(String idLocale, String valoreMisurazione, String unitaDiMisura, String data, String ora){
        this.idLocale = idLocale;
        this.valoreMisurazione = valoreMisurazione;
        this.unitaDiMisura = unitaDiMisura;
        this.data = data;
        this.ora = ora;
    }

    public void setValoreMisurazione(String valoreMisurazione) {
        this.valoreMisurazione = valoreMisurazione;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Misura{" +
                "idLocale=" + idLocale + '\'' +
                ", valoreMisurazione='" + valoreMisurazione + '\'' +
                ", unitaDiMisura='" + unitaDiMisura + '\'' +
                ", data='" + data + '\'' +
                ", ora='" + ora + '\'' +
                '}';
    }
}

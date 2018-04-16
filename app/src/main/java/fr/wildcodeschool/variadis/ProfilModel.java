package fr.wildcodeschool.variadis;

/**
 * Created by perrine on 16/04/18.
 */

public class ProfilModel {
    private String pseudo;

    public ProfilModel() {}

    public ProfilModel(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getPseudo() {
        return this.pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }
}

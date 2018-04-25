package fr.wildcodeschool.variadis;

/**
 * Created by perrine on 16/04/18.
 */

public class ProfilModel {
    private String pseudo;
    private String avatar;

    public ProfilModel() {}

    public ProfilModel(String pseudo) {
        this.pseudo = pseudo;
    }

    public ProfilModel(String pseudo, String avatar) {
        this.pseudo = pseudo;
        this.avatar = avatar;
    }

    public String getPseudo() {
        return this.pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}

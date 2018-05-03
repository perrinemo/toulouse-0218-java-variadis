package fr.wildcodeschool.variadis;

/**
 * Created by perrine on 24/04/18.
 */

public class SingletonClass {

    private static volatile SingletonClass sSoleInstance;
    public ProfilModel profilModel = null;

    //private constructor.
    private SingletonClass() {

        //Prevent form the reflection api.
        if (sSoleInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static SingletonClass getInstance() {
        //Double check locking pattern
        if (sSoleInstance == null) { //Check for the first time

            synchronized (SingletonClass.class) {   //Check for the second time.
                //if there is no instance available... create new one
                if (sSoleInstance == null) sSoleInstance = new SingletonClass();
            }
        }

        return sSoleInstance;
    }

    public ProfilModel getProfil() {
        return profilModel;
    }

    public void setProfil(ProfilModel profilModel) {
        this.profilModel = profilModel;
    }


}

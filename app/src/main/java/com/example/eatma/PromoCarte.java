package com.example.eatma;

import android.os.Parcel;
import android.os.Parcelable;

public class PromoCarte implements Parcelable {
    String title;
    String type;
    String prix;
    String image;
    String whatsapp;
    String description;
    public PromoCarte(String title, String type, String prix, String image, String whatsapp, String description, String adress, String ville, String quartier) {
        this.title = title;
        this.type = type;
        this.prix = prix;
        this.image = image;
        this.whatsapp = whatsapp;
        this.description = description;
        this.adress = adress;
        this.ville = ville;
        this.quartier = quartier;
    }
    protected PromoCarte(Parcel in) {
        title = in.readString();
        type = in.readString();
        prix = in.readString();
        ville = in.readString();
        quartier = in.readString();
        whatsapp = in.readString();
        description = in.readString();
        adress = in.readString();
        image = in.readString();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(prix);
        dest.writeString(ville);
        dest.writeString(quartier);
        dest.writeString(whatsapp);
        dest.writeString(description);
        dest.writeString(adress);
        dest.writeString(image);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PromoCarte> CREATOR = new Creator<PromoCarte>() {
        @Override
        public PromoCarte createFromParcel(Parcel in) {
            return new PromoCarte(in);
        }

        @Override
        public PromoCarte[] newArray(int size) {
            return new PromoCarte[size];
        }
    };


    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    String adress;

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    String ville;



    String quartier;

    public String getQuartier() {
        return quartier;
    }

    public void setQuartier(String quartier) {
        this.quartier = quartier;
    }

    public PromoCarte() {
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

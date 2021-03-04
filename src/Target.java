
import java.util.Date;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ADMIN
 */
public class Target {
    String name, imgURL, dateCreate, dateDone, status;
    int number, value, moneyProgress, progress;

    public Target() {
    }

    public Target(int number, String name, String dateCreate, String dateDone, String imgURL, int value, int moneyProgress) {
        this.name = name;
        this.dateCreate = dateCreate;
        this.dateDone = dateDone;
        this.value = value;
        this.status = "Haven't started";
        this.imgURL = imgURL;
        this.progress = 0;
        this.number = number;
        this.moneyProgress = moneyProgress;
    }

    public Target(String name, String imgURL, String dateCreate, String dateDone, int number, int value, int progress, String status, int moneyProgress) {
        this.name = name;
        this.imgURL = imgURL;
        this.dateCreate = dateCreate;
        this.dateDone = dateDone;
        this.number = number;
        this.value = value;
        this.progress = progress;
        this.status = status;
        this.moneyProgress = moneyProgress;
    }

    public int getMoneyProgress() {
        return moneyProgress;
    }
    
    public int getProgress() {
        return progress;
    }
    
    public String getImgURL() {
        return imgURL;
    }
    
    public String getName() {
        return name;
    }

    public String getDateCreate() {
        return dateCreate;
    }

    public String getDateDone() {
        return dateDone;
    }

    public int getNumber() {
        return number;
    }

    public int getValue() {
        return value;
    }

    public String getStatus() {
        return status;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDateCreate(String dateCreate) {
        this.dateCreate = dateCreate;
    }

    public void setDateDone(String dateDone) {
        this.dateDone = dateDone;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMoneyProgress(int moneyProgress) {
        this.moneyProgress = moneyProgress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return number + "," + name + "," + dateCreate + "," + dateDone + "," + value + "," + imgURL + "," + progress + "," + status + "," + moneyProgress;
    }
}

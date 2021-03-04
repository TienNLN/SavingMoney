
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ADMIN
 */
public class DataActions {
    private String fileName = "targetData.csv";
    private File file = new File(fileName);
    
    public List<Target> readFile(){
        if(file.exists()){
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(file));
                String line = reader.readLine();
                List<Target> tempList = new ArrayList<>();
                while(line != null){
                    String[] splitLine = line.split(",");
                    
                    int num = Integer.parseInt(splitLine[0]);
                    String name = splitLine[1];
                    String dateCreate = splitLine[2];
                    String dateDone = splitLine[3];
                    int value = Integer.parseInt(splitLine[4]);
                    String imgURL = splitLine[5];
                    int progress = Integer.parseInt(splitLine[6]);
                    String status = splitLine[7];
                    int moneyProgress = Integer.parseInt(splitLine[8]);
                    
                    
                    Target tempTarget = new Target(name, imgURL, dateCreate, dateDone, num, value, progress, status, moneyProgress);
                    tempList.add(tempTarget);
                    
                    line = reader.readLine();
                }
                reader.close();
                return tempList;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public void writeFile(List<Target> list) throws Exception{
        File fOut = new File(fileName);
        
        if(file.exists()){
            file.delete();
            file.createNewFile();
        }
        
        FileOutputStream fos = new FileOutputStream(fOut);
        
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        
        for (Target tempTarget : list) {
            bw.write(tempTarget.toString());
            bw.newLine();
        }
        bw.close();
    }
}

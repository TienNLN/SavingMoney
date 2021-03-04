
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ADMIN
 */
public class TargetActions {
    private List<Target> targetList;
    private MainFrame main;
    private DataActions dataAct;

    public TargetActions(MainFrame main) {
        targetList = new ArrayList<>();
        dataAct = new DataActions();
        this.main = main;
    }

    public List<Target> getTargetList() {
        return targetList;
    }
    
    public void loadList(){
        if(dataAct.readFile() == null){
            return;
        } else {
            targetList = dataAct.readFile();
        }
    }
    
    public void addTarget(){
        boolean noError = true;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
        
        int number = 0;
        
        if(targetList == null){
            number = 1;
        } else {
            number = targetList.size()+1;
        }
        
        String name = main.getTxtName().getText();
        
        if(name.isEmpty()){
            main.getTxtName().setText("");
            JOptionPane.showMessageDialog(null, "Name cannnot be empty");
            noError = false;
        }
        
        Date date = new Date();
        String dateCreate = dateFormat.format(date);
        
        String dateDone = "";
        
        if(main.getTxtCompleteDay().isEnabled()){
            dateDone = dateFormat.format(main.getTxtCompleteDay().getDate());
            System.out.println(dateDone);
        } else {
            dateDone = "";
        }
        
        String imgURL = main.getTxtImageURL().getText();
        
        int value = 0;
        
        int budget = 0;
        
        try {
            value = Integer.parseInt(main.getTxtValue().getText());
        } catch (Exception e) {
            System.out.println("value");
        }
        
        if(value == 0){
            main.getTxtValue().setText(null);
            JOptionPane.showMessageDialog(null, "Value cannot be 0");
            noError = false;
        }
        
        if(!main.getTxtBudget().getText().isEmpty()){
            budget = Integer.parseInt(main.getTxtBudget().getText());
        }
        
        double doubleProgress = new BigDecimal(budget).divide(new BigDecimal(value), 2, RoundingMode.HALF_UP).doubleValue();
        
        int progress = (int) Math.round(doubleProgress * 100);
        
        if(noError){
            Target temp = new Target(number, name, dateCreate, dateDone, imgURL, value, budget);
            temp.setProgress(progress);
            if(targetList.isEmpty()){
                temp.setStatus("In progress");
            }
            addTarget(temp);
            System.out.println(temp);
            loadTable(main.getTableTargets());
            JOptionPane.showMessageDialog(null, "Add Target Completed");
        }
        
        try {
            dataAct.writeFile(targetList);
        } catch (Exception e) {
            System.out.println("write");
        }
    }
    
    public void addTarget(Target target){
        targetList.add(target);
    }
    
    public void loadTable(JTable table){
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        Vector data = new Vector<>();
        
        for (Target data1 : targetList) {
            Vector row = new Vector();
            
            row.add(data1.getNumber());
            row.add(data1.getName());
            row.add(data1.getDateCreate());
            row.add(formatNumberMoney(data1.getValue()));
            row.add(data1.getStatus()+ "    " + data1.getProgress() + "%");
            row.add(data1.getDateDone());
            row.add(formatNumberMoney(data1.getMoneyProgress()));
            
            data.add(row);
        }
        
        model.setNumRows(data.size()-1);
        
        Vector header = new Vector();
        
        header.add("Number");
        header.add("Name");
        header.add("Date Create");
        header.add("Value");
        header.add("Status");
        header.add("Date Done");
        header.add("Saving Money");
        
        model.setDataVector(data, header);
        
    }
    
    public String formatNumberMoney(int number){
        String moneyStringNum = number+"";
        String moneyString = "";
        if(moneyStringNum.length()>3){
            int countNum = 0;
            for(int i = moneyStringNum.length()-1; i>=0; i--){
                countNum++;
                moneyString+=moneyStringNum.charAt(i);
                if(countNum!=moneyStringNum.length()){
                    if(countNum % 3 == 0){
                        moneyString+=".";
                    }
                }
            }
            moneyString = reverse(moneyString);
            return moneyString;
        } else {
            return number+"";
        }
    }
    
    public String reverse(String string){
        String reverse = "";
        for(int i = string.length()-1; i>=0; i--){
            reverse+=string.charAt(i);
        }
        return reverse;
    }
}

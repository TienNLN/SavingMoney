
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumnModel;
import org.jdesktop.swingx.JXDatePicker;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ADMIN
 */
public class MainFrame extends javax.swing.JFrame {

    /**
     * Creates new form MainFrame
     */
    
    DataActions dataAct = new DataActions();
    
    TargetActions targetAct = new TargetActions(this);
    
    Thread moneyDayThread;
    
    Thread loadProgressThread;
    
    Target inProgress = null;
    
    List<Target> test = null;
    
    
    
    public MainFrame() {
        initComponents();
        targetAct.loadList();
        
        checkInProgress();
        
        if(!targetAct.getTargetList().isEmpty()){
            targetAct.loadTable(tableTargets);
            setCollWidth();
        }
        
        checkUsual();
        loadProgress();
        
        moneyDayThread.start();
        loadProgressThread.start();
    }
    
    public void setCollWidth(){
        TableColumnModel collModel = tableTargets.getColumnModel();
        collModel.getColumn(0).setPreferredWidth(30);
        collModel.getColumn(1).setPreferredWidth(150);
        collModel.getColumn(2).setPreferredWidth(100);
        collModel.getColumn(3).setPreferredWidth(100);
        collModel.getColumn(4).setPreferredWidth(170);
        collModel.getColumn(5).setPreferredWidth(100);
    }
    
    public void checkInProgress(){
        for (Target temp : targetAct.getTargetList()) {
            if(temp.getStatus().equalsIgnoreCase("In progress")){
                inProgress = temp;
            }
        }
    }

    public String reverse(String string){
        String reverse = "";
        for(int i = string.length()-1; i>=0; i--){
            reverse+=string.charAt(i);
        }
        return reverse;
    }
    
    public void loadProgress(){
        loadProgressThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while(true){
                    if(targetAct.getTargetList() != null){
                        for (Target temp : targetAct.getTargetList()) {
                            if(inProgress == null){
                                if(temp.getStatus().equalsIgnoreCase("Haven't started")){
                                    temp.setStatus("In progress");
                                    inProgress = temp;
                                    try {
                                        dataAct.writeFile(targetAct.getTargetList());
                                        targetAct.loadTable(tableTargets);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                } else if(temp.getStatus().equalsIgnoreCase("In progress")) {
                                    inProgress = temp;
                                    System.out.println(inProgress.toString());
                                    try {
                                        dataAct.writeFile(targetAct.getTargetList());
                                        targetAct.loadTable(tableTargets);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                            }
                        }
                        if(inProgress!=null){
                            if(inProgress.getProgress()>=100){
                                for (Target temp : targetAct.getTargetList()) {
                                    if(temp.getNumber() == inProgress.getNumber()){
                                        temp.setStatus("Done");
                                        temp.setProgress(100);
                                        inProgress = null;
                                        labelProgressingName.setText("");
                                        labelTargetNum.setText("");
                                        labelDateCreate.setText("");
                                        labelDateComplete.setText("");
                                        progressBar.setValue(0);
                                        labelProgress.setText("");
                                        targetAct.loadTable(tableTargets);
                                        txtMoneyDay.setText("");
                                        labelMoneyDay.setText("");
                                        try {
                                            dataAct.writeFile(targetAct.getTargetList());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    }
                                }
                                
                            } else {
                                labelProgressingName.setText(inProgress.getName());
                                labelTargetNum.setText(inProgress.getNumber()+"");
                                labelDateCreate.setText(inProgress.getDateCreate());
                                labelDateComplete.setText(inProgress.getDateDone());
                                progressBar.setValue(inProgress.getProgress());
                                labelProgress.setText(inProgress.getProgress()+"%");
                                labelSavingMoney.setText(""+targetAct.formatNumberMoney(inProgress.getMoneyProgress()));
                                
                                if(!labelDateComplete.getText().isEmpty()){
                                    int count = 0;
                                    try {
                                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/YYYY");
                                        
                                        String dateDoneInString = inProgress.getDateDone();
                                        Date dateDone = format.parse(dateDoneInString);
                                        
                                        Calendar cal = Calendar.getInstance();
                                        Date dateRecent = cal.getTime();
                                        
                                        System.out.println(dateRecent);
                                        System.out.println(dateDone);
                                        
                                        while(dateRecent.before(dateDone)){
                                            count++;
                                            cal.add(Calendar.DAY_OF_MONTH, 1);
                                            dateRecent.getTime();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    int moneyDay = (inProgress.getValue() - inProgress.getMoneyProgress()) / count;
                                    System.out.println(inProgress.toString());
                                    labelMoneyDay.setText(targetAct.formatNumberMoney(moneyDay));
                                }

                                if(inProgress.getDateDone().isEmpty()){
//                                    double moneyADayDB = inProgress.getValue() / 30;
//                                    int moneyADay = (int) Math.round(moneyADayDB);
//
//                                    String moneyString = targetAct.formatNumberMoney(moneyADay);

                                    labelMoneyDay.setText("");
                                } else {
                                    Calendar cal = Calendar.getInstance();
                                    Date createDate = cal.getTime();


                                    int countDate = 0;

                                    try {
                                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                                        String dateInString = inProgress.getDateDone();
                                        Date dateDone = format.parse(dateInString);

                                        while(createDate.before(dateDone)){
                                            countDate++;
                                            cal.add(Calendar.DAY_OF_MONTH, 1);
                                            createDate = cal.getTime();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    double moneyADayDB = inProgress.getValue() / countDate;
                                    int moneyADay = (int) Math.round(moneyADayDB);

                                    String moneyStringNum = moneyADay+"";
                                    String moneyString = "";
                                    if(moneyStringNum.length()>3){
                                        int countNum = 0;
                                        for(int i = moneyStringNum.length()-1; i>=0; i--){
                                            countNum++;
                                            moneyString+=moneyStringNum.charAt(i);
                                            if(countNum % 3 == 0){
                                                moneyString+=".";
                                            }
                                        }
                                        moneyString = reverse(moneyString);
                                    }
                                    labelMoneyDay.setText(moneyString);
                                }
                            }
                            if(inProgress.getNumber() < targetAct.getTargetList().size()){
                                for (Target temp : targetAct.getTargetList()) {
                                    if(temp.getNumber() == inProgress.getNumber()+1){
                                        labelUpcomingTarget.setText(temp.getName());
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                    }
                }
            }
        });
    }
    
    public void checkUsual(){
        moneyDayThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while(true){
                    if(txtCompleteDay.getDate() == null){
                        if(!txtValue.getText().isEmpty()){
                            if(Integer.parseInt(txtValue.getText())>0){
//                                double moneyADayDB = Integer.parseInt(txtValue.getText()) / 30;
//                                int moneyADay = (int) Math.round(moneyADayDB);
//                                
//                                String moneyString = targetAct.formatNumberMoney(moneyADay);
                                txtMoneyDay.setText("");
                            } else {
                                try{
                                    Thread.sleep(2000);
                                } catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            txtMoneyDay.setText("");
                        }
                    } else {
                        if(!txtValue.getText().isEmpty()){
                            if(Integer.parseInt(txtValue.getText())>0){
                                    Calendar cal = Calendar.getInstance();
                                    Date createDate = cal.getTime();
                                    int countDate = 0;
                                    
                                    while(createDate.before(txtCompleteDay.getDate())){
                                        countDate++;
                                        cal.add(Calendar.DAY_OF_MONTH, 1);
                                        createDate = cal.getTime();
                                    }
                                    if(countDate>0){
                                        double moneyADayDB = 0;  
                                        
                                        if(!txtBudget.getText().isEmpty()){
                                            moneyADayDB = (Integer.parseInt(txtValue.getText()) - Integer.parseInt(txtBudget.getText())) / countDate;
                                        } else {
                                            moneyADayDB = Integer.parseInt(txtValue.getText()) / countDate;
                                        }
                                        
                                        int moneyADay = (int) Math.round(moneyADayDB);

                                        String moneyString = targetAct.formatNumberMoney(moneyADay);

                                        txtMoneyDay.setText(moneyString + "/day");
                                    } else {
                                        if(!txtBudget.getText().isEmpty()){
                                            txtMoneyDay.setText(targetAct.formatNumberMoney(Integer.parseInt(txtValue.getText()) - Integer.parseInt(txtBudget.getText())));
                                        } else {
                                            txtMoneyDay.setText(targetAct.formatNumberMoney(Integer.parseInt(txtValue.getText())));
                                        }
                                    }
                            } else {
                                try{
                                    Thread.sleep(2000);
                                } catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            txtMoneyDay.setText("");
                        }
                    }
                    try{
                        Thread.sleep(2000);
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        txtValue = new javax.swing.JTextField();
        txtImageURL = new javax.swing.JTextField();
        btnImageURL = new javax.swing.JButton();
        panelAdvance = new javax.swing.JPanel();
        completeDay = new javax.swing.JLabel();
        moneyPerDay = new javax.swing.JLabel();
        txtMoneyDay = new javax.swing.JTextField();
        txtCompleteDay = new org.jdesktop.swingx.JXDatePicker();
        radioEnable = new javax.swing.JRadioButton();
        radioDisable = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        txtBudget = new javax.swing.JTextField();
        panelImage = new javax.swing.JPanel();
        btnReset = new javax.swing.JButton();
        btnCreate = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableTargets = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        labelProgressingName = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        panelUpcoming = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        labelUpcomingTarget = new javax.swing.JLabel();
        labelTargetNum = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        labelDateCreate = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        labelDateComplete = new javax.swing.JLabel();
        labelMoneyDay = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        jLabel13 = new javax.swing.JLabel();
        labelProgress = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtDonateMoney = new javax.swing.JTextField();
        btnEnter = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        labelSavingMoney = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setText("Name: ");

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setText("Value: ");

        jLabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel3.setText("Image: ");

        btnImageURL.setText("...");
        btnImageURL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImageURLActionPerformed(evt);
            }
        });

        panelAdvance.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Advance", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Times New Roman", 1, 18))); // NOI18N

        completeDay.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        completeDay.setText("Complete Day: ");
        completeDay.setEnabled(false);

        moneyPerDay.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        moneyPerDay.setText("Money Per Day: ");
        moneyPerDay.setEnabled(false);

        txtMoneyDay.setEnabled(false);
        txtMoneyDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMoneyDayActionPerformed(evt);
            }
        });

        txtCompleteDay.setEnabled(false);
        txtCompleteDay.setFormats("dd/MM/yyyy");
        txtCompleteDay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCompleteDayActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelAdvanceLayout = new javax.swing.GroupLayout(panelAdvance);
        panelAdvance.setLayout(panelAdvanceLayout);
        panelAdvanceLayout.setHorizontalGroup(
            panelAdvanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAdvanceLayout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(panelAdvanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(completeDay)
                    .addComponent(moneyPerDay))
                .addGap(38, 38, 38)
                .addGroup(panelAdvanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtMoneyDay, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                    .addComponent(txtCompleteDay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        panelAdvanceLayout.setVerticalGroup(
            panelAdvanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAdvanceLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(panelAdvanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(completeDay)
                    .addComponent(txtCompleteDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(panelAdvanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(moneyPerDay)
                    .addComponent(txtMoneyDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        buttonGroup1.add(radioEnable);
        radioEnable.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        radioEnable.setText("Enable");
        radioEnable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioEnableActionPerformed(evt);
            }
        });

        buttonGroup1.add(radioDisable);
        radioDisable.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        radioDisable.setSelected(true);
        radioDisable.setText("Disable");
        radioDisable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioDisableActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setText("Budget: ");

        panelImage.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 2, true), "Target Image", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Times New Roman", 1, 18))); // NOI18N

        javax.swing.GroupLayout panelImageLayout = new javax.swing.GroupLayout(panelImage);
        panelImage.setLayout(panelImageLayout);
        panelImageLayout.setHorizontalGroup(
            panelImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelImageLayout.setVerticalGroup(
            panelImageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 354, Short.MAX_VALUE)
        );

        btnReset.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnReset.setText("Reset");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        btnCreate.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnCreate.setText("Create");
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(86, 86, 86)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtName)
                            .addComponent(txtValue)
                            .addComponent(txtBudget)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(radioEnable)
                                .addGap(18, 18, 18)
                                .addComponent(radioDisable))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtImageURL, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnImageURL, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addComponent(panelAdvance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnReset, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCreate, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                    .addComponent(panelImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(117, 117, 117)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(39, 39, 39)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(34, 34, 34)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtImageURL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnImageURL)
                            .addComponent(jLabel3))
                        .addGap(35, 35, 35)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtBudget, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(panelImage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radioEnable)
                    .addComponent(radioDisable))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panelAdvance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );

        jTabbedPane1.addTab("Create New Targets", jPanel1);

        tableTargets.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        tableTargets.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tableTargets.setDragEnabled(true);
        tableTargets.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tableTargets);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 936, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 658, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Targets ", jPanel2);

        labelProgressingName.setFont(new java.awt.Font("Times New Roman", 0, 48)); // NOI18N
        labelProgressingName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelProgressingName.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel7.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Target Number: ");

        panelUpcoming.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel8.setFont(new java.awt.Font("Times New Roman", 2, 18)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Upcoming Target: ");

        labelUpcomingTarget.setFont(new java.awt.Font("Times New Roman", 3, 36)); // NOI18N

        javax.swing.GroupLayout panelUpcomingLayout = new javax.swing.GroupLayout(panelUpcoming);
        panelUpcoming.setLayout(panelUpcomingLayout);
        panelUpcomingLayout.setHorizontalGroup(
            panelUpcomingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUpcomingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelUpcomingTarget, javax.swing.GroupLayout.DEFAULT_SIZE, 773, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );
        panelUpcomingLayout.setVerticalGroup(
            panelUpcomingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelUpcomingLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelUpcomingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelUpcomingTarget, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE))
                .addContainerGap())
        );

        labelTargetNum.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        labelTargetNum.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel10.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel10.setText("Date Create: ");
        jLabel10.setToolTipText("");
        jLabel10.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        labelDateCreate.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        labelDateCreate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel9.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel9.setText("Date Complete: ");

        jLabel11.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel11.setText("Money Per Day: ");

        labelDateComplete.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        labelDateComplete.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        labelMoneyDay.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        labelMoneyDay.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 223, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 45, Short.MAX_VALUE)
        );

        progressBar.setBorder(null);
        progressBar.setString("50");

        jLabel13.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel13.setText("Progress: ");

        labelProgress.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N

        jLabel14.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel14.setText("Donate Money: ");

        btnEnter.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        btnEnter.setText("Donate");
        btnEnter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnterActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel4.setText("Saving Money: ");

        labelSavingMoney.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        labelSavingMoney.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelUpcoming, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelProgressingName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(103, 103, 103)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                                .addComponent(jLabel4)
                                                .addGap(18, 18, 18)
                                                .addComponent(labelSavingMoney, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(labelDateCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(labelMoneyDay, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(labelTargetNum, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(labelDateComplete, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGap(18, 18, 18))
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(59, 59, 59)))
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(txtDonateMoney, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnEnter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGap(68, 68, 68))
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                                        .addGap(11, 11, 11)
                                                        .addComponent(labelProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(0, 0, Short.MAX_VALUE)))))
                                .addGap(35, 35, 35)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelProgressingName, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                    .addComponent(labelTargetNum, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(labelDateCreate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(22, 22, 22)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(labelDateComplete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                    .addComponent(labelMoneyDay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelSavingMoney, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(btnEnter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDonateMoney, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(labelProgress, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43)
                .addComponent(panelUpcoming, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        jTabbedPane1.addTab("Progressing", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnImageURLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImageURLActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        
        int choice = fileChooser.showOpenDialog(null);
        
        if(choice == JFileChooser.APPROVE_OPTION){
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            txtImageURL.setText(path.toString());
            Graphics graphic = panelImage.getGraphics();
            ImageIcon imgIcon = new ImageIcon(path);
            Image img = imgIcon.getImage();
            graphic.drawImage(img, 0, 0, null);
        }
    }//GEN-LAST:event_btnImageURLActionPerformed

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        targetAct.addTarget();
    }//GEN-LAST:event_btnCreateActionPerformed

    private void radioEnableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioEnableActionPerformed
        if(radioEnable.isSelected()){
            completeDay.setEnabled(true);
            moneyPerDay.setEnabled(true);
            txtCompleteDay.setEnabled(true);
            txtCompleteDay.setDate(new Date());
        }
    }//GEN-LAST:event_radioEnableActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        txtName.setText("");
        txtBudget.setText("");
        txtCompleteDay.removeAll();
        txtDonateMoney.setText("");
        txtImageURL.setText("");
        txtValue.setText("");
        txtMoneyDay.setText("");
        radioDisable.setSelected(true);
        panelAdvance.setEnabled(false);
        completeDay.setEnabled(false);
        moneyPerDay.setEnabled(false);
        txtCompleteDay.setEnabled(false);
        txtMoneyDay.setEnabled(false);
    }//GEN-LAST:event_btnResetActionPerformed

    private void radioDisableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioDisableActionPerformed
        if(radioDisable.isSelected()){
            completeDay.setEnabled(false);
            moneyPerDay.setEnabled(false);
            txtCompleteDay.setEnabled(false);
            txtMoneyDay.setEnabled(false);
            txtCompleteDay.setDate(null);
        }
    }//GEN-LAST:event_radioDisableActionPerformed

    private void txtCompleteDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCompleteDayActionPerformed
        
    }//GEN-LAST:event_txtCompleteDayActionPerformed

    private void txtCompleteDayInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txtCompleteDayInputMethodTextChanged
//        try {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
//        
//            Date date = txtCompleteDay.getDate();
//
//            String dateString = dateFormat.format(date);
//
//            System.out.println(dateString);
//
//            date = dateFormat.parse(dateString);
//            
//            txtCompleteDay.setDate(date);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        
    }//GEN-LAST:event_txtCompleteDayInputMethodTextChanged

    private void txtCompleteDayFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCompleteDayFocusLost
//        try {
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
//        
//            Date date = txtCompleteDay.getDate();
//
//            String dateString = dateFormat.format(date);
//
//            System.out.println(dateString);
//
//            date = dateFormat.parse(dateString);
//            
//            txtCompleteDay.setDate(date);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }//GEN-LAST:event_txtCompleteDayFocusLost

    private void btnEnterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnterActionPerformed
        inProgress.setMoneyProgress(inProgress.getMoneyProgress()+Integer.parseInt(txtDonateMoney.getText()));
        double doubleProgress = new BigDecimal(inProgress.getMoneyProgress()).divide(new BigDecimal(inProgress.getValue()), 2, RoundingMode.HALF_UP).doubleValue();
        int progress = (int) Math.round(doubleProgress * 100);
        
        inProgress.setProgress(progress);
        System.out.println(inProgress.getMoneyProgress());
        for (Target temp : targetAct.getTargetList()) {
            if(temp.getNumber() == inProgress.getNumber()){
                temp.setMoneyProgress(inProgress.getMoneyProgress());
                temp.setProgress(inProgress.getProgress());
                break;
            }
        }
        try {
            dataAct.writeFile(targetAct.getTargetList());
            targetAct.loadTable(tableTargets);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JOptionPane.showMessageDialog(this, "Donate "+ targetAct.formatNumberMoney(Integer.parseInt(txtDonateMoney.getText()))+" completed!");
        txtDonateMoney.setText("");
    }//GEN-LAST:event_btnEnterActionPerformed

    private void txtMoneyDayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMoneyDayActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMoneyDayActionPerformed

    public JLabel getLabelDateComplete() {
        return labelDateComplete;
    }

    public JLabel getLabelDateCreate() {
        return labelDateCreate;
    }

    public JLabel getLabelProgress() {
        return labelProgress;
    }

    public JLabel getLabelProgressingName() {
        return labelProgressingName;
    }

    public JLabel getLabelTargetNum() {
        return labelTargetNum;
    }

    public JLabel getLabelUpcomingTarget() {
        return labelUpcomingTarget;
    }

    public JLabel getMoneyPerDay() {
        return moneyPerDay;
    }

    public JPanel getPanelAdvance() {
        return panelAdvance;
    }

    public JPanel getPanelImage() {
        return panelImage;
    }

    public JPanel getPanelUpcoming() {
        return panelUpcoming;
    }

    public JRadioButton getRadioDisable() {
        return radioDisable;
    }

    public JRadioButton getRadioEnable() {
        return radioEnable;
    }

    public JTable getTableTargets() {
        return tableTargets;
    }

    public JTextField getTxtBudget() {
        return txtBudget;
    }

    public JXDatePicker getTxtCompleteDay() {
        return txtCompleteDay;
    }

    public JTextField getTxtDonateMoney() {
        return txtDonateMoney;
    }

    public JTextField getTxtImageURL() {
        return txtImageURL;
    }

    public JTextField getTxtMoneyDay() {
        return txtMoneyDay;
    }

    public JTextField getTxtName() {
        return txtName;
    }

    public JTextField getTxtValue() {
        return txtValue;
    }

    public JButton getBtnCreate() {
        return btnCreate;
    }

    public JButton getBtnImageURL() {
        return btnImageURL;
    }

    public JButton getBtnReset() {
        return btnReset;
    }

    public JLabel getCompleteDay() {
        return completeDay;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public void setBtnCreate(JButton btnCreate) {
        this.btnCreate = btnCreate;
    }

    public void setBtnImageURL(JButton btnImageURL) {
        this.btnImageURL = btnImageURL;
    }

    public void setBtnReset(JButton btnReset) {
        this.btnReset = btnReset;
    }

    public void setCompleteDay(JLabel completeDay) {
        this.completeDay = completeDay;
    }

    public void setLabelDateComplete(JLabel labelDateComplete) {
        this.labelDateComplete = labelDateComplete;
    }

    public void setLabelDateCreate(JLabel labelDateCreate) {
        this.labelDateCreate = labelDateCreate;
    }

    public void setLabelProgress(JLabel labelProgress) {
        this.labelProgress = labelProgress;
    }

    public void setLabelProgressingName(JLabel labelProgressingName) {
        this.labelProgressingName = labelProgressingName;
    }

    public void setLabelTargetNum(JLabel labelTargetNum) {
        this.labelTargetNum = labelTargetNum;
    }

    public void setLabelUpcomingTarget(JLabel labelUpcomingTarget) {
        this.labelUpcomingTarget = labelUpcomingTarget;
    }

    public void setMoneyPerDay(JLabel moneyPerDay) {
        this.moneyPerDay = moneyPerDay;
    }

    public void setPanelAdvance(JPanel panelAdvance) {
        this.panelAdvance = panelAdvance;
    }

    public void setPanelImage(JPanel panelImage) {
        this.panelImage = panelImage;
    }

    public void setPanelUpcoming(JPanel panelUpcoming) {
        this.panelUpcoming = panelUpcoming;
    }

    public void setProgressBar(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void setRadioDisable(JRadioButton radioDisable) {
        this.radioDisable = radioDisable;
    }

    public void setRadioEnable(JRadioButton radioEnable) {
        this.radioEnable = radioEnable;
    }

    public void setTableTargets(JTable tableTargets) {
        this.tableTargets = tableTargets;
    }

    public void setTxtBudget(JTextField txtBudget) {
        this.txtBudget = txtBudget;
    }

    public void setTxtCompleteDay(JXDatePicker txtCompleteDay) {
        this.txtCompleteDay = txtCompleteDay;
    }

    public void setTxtDonateMoney(JTextField txtDonateMoney) {
        this.txtDonateMoney = txtDonateMoney;
    }

    public void setTxtImageURL(JTextField txtImageURL) {
        this.txtImageURL = txtImageURL;
    }

    public void setTxtMoneyDay(JTextField txtMoneyDay) {
        this.txtMoneyDay = txtMoneyDay;
    }

    public void setTxtName(JTextField txtName) {
        this.txtName = txtName;
    }

    public void setTxtValue(JTextField txtValue) {
        this.txtValue = txtValue;
    }
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnEnter;
    private javax.swing.JButton btnImageURL;
    private javax.swing.JButton btnReset;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel completeDay;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel labelDateComplete;
    private javax.swing.JLabel labelDateCreate;
    private javax.swing.JLabel labelMoneyDay;
    private javax.swing.JLabel labelProgress;
    private javax.swing.JLabel labelProgressingName;
    private javax.swing.JLabel labelSavingMoney;
    private javax.swing.JLabel labelTargetNum;
    private javax.swing.JLabel labelUpcomingTarget;
    private javax.swing.JLabel moneyPerDay;
    private javax.swing.JPanel panelAdvance;
    private javax.swing.JPanel panelImage;
    private javax.swing.JPanel panelUpcoming;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton radioDisable;
    private javax.swing.JRadioButton radioEnable;
    private javax.swing.JTable tableTargets;
    private javax.swing.JTextField txtBudget;
    private org.jdesktop.swingx.JXDatePicker txtCompleteDay;
    private javax.swing.JTextField txtDonateMoney;
    private javax.swing.JTextField txtImageURL;
    private javax.swing.JTextField txtMoneyDay;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtValue;
    // End of variables declaration//GEN-END:variables
}

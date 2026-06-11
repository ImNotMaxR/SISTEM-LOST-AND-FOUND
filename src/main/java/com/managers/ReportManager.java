package com.managers;

import com.database.DBConnection;
import com.interfaces.Managerable;
import com.model.FoundReport;
import com.model.LostReport;
import com.model.Report;
import java.util.ArrayList;
import java.util.HashMap;

public class ReportManager implements Managerable{
    private ArrayList<Report> reports;
    private HashMap<String, Report> reportMap= new HashMap<String, Report>();
    private DBConnection dbConnection;
    
    @Override
    public void add(Object obj){
    }
    
    @Override
    public void delete(String id){
    }
    
    @Override
    public Object findById(String id){
        for (Report report : reports) {
            if (report.getReportID().equals(id)) {
                return report;
            }
        }
        return null;
    }
    
    public void addReport(Report report){
        reports.add(report);
    }
    
    public void deleteReport(String reportId){
        reports.remove(findById(reportId));
    }
    
    public ArrayList<Report> getValidReports(){
        return null;
    }
    
    public ArrayList<Report> getAllReports(){
        return reports;
    }
    
    public  ArrayList<LostReport> getLostReports(){
        return null;
    }
    
    public  ArrayList<FoundReport> getFoundReports(){
        return null;
    }
}

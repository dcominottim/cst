/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.bindings.soar;

import br.unicamp.cst.bindings.soar.SOARPlugin;
import br.unicamp.cst.core.entities.Codelet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author wander
 */
public abstract class JSoarCodelet extends Codelet {
    
    String agentName;
    File productionPath;
    
    //arrumar isso
    public SOARPlugin jsoar = null;
    
    @Override
    public void accessMemoryObjects() {
    }
    
    @Override
    public void proc() {
        
    } 
    
    public void initSoarPlugin(String _agentName, File _productionPath, Boolean startSOARDebugger){
        this.jsoar = new SOARPlugin(_agentName, _productionPath, startSOARDebugger);
    }
    
    
    public ArrayList<Object> getCommands(String package_with_beans_classes){
        ArrayList<Object> commandList = new ArrayList<Object>();
        JsonObject templist = jsoar.getOutputLink();
        Set<Map.Entry<String,JsonElement>> set = templist.entrySet();
        Iterator <Entry<String,JsonElement>> it = set.iterator();
        while(it.hasNext()){
            Entry<String,JsonElement> entry = it.next();
            String key = entry.getKey();
            JsonObject commandtype = entry.getValue().getAsJsonObject();
            try{
                Class type = Class.forName(package_with_beans_classes+"."+key);
                Object command = type.newInstance();
                type.cast(command);
                for(Field field : type.getFields()){
                    if(commandtype.has(field.getName())){
                        if(commandtype.get(field.getName()).getAsJsonPrimitive().isNumber()){
                            field.set(command, commandtype.get(field.getName()).getAsFloat());
                            
                        }else if(commandtype.get(field.getName()).getAsJsonPrimitive().isBoolean()){
                            field.set(command, commandtype.get(field.getName()).getAsBoolean());
                            
                        }else{
                            field.set(command, commandtype.get(field.getName()).getAsString());
                        }
                    }
                }
                commandList.add(command);
                
            }catch(Exception e){
                 e.printStackTrace();
            }
        }
        return commandList;
    }
    
    /*
     public ArrayList<SoarCommand> getCommands(List<String> commandsList){
        ArrayList<SoarCommand> commandList = jsoar.getReceivedCommands(commandsList);    
        return commandList;
    }*/
    
    
    public abstract void processCommands();
    
    
   
    
    public JsonObject createJson(String pathToLeaf, Object value){
        JsonObject json = new JsonObject();
        Class a = value.getClass();
        if(a==String.class){
            String specvalue =(String)value;
            json = jsoar.createJsonFromString(pathToLeaf,specvalue);
        }
        else if(a==double.class){
            double specvalue =(double)value;
            json = jsoar.createJsonFromString(pathToLeaf,specvalue);
        }
        return json;
    }
    
    public void addToJson(JsonObject newBranch, JsonObject json, String property){
        json.add(property, newBranch);
    }
    
    
    
    //arrumar isso aqui. eh basicamente uma costura de soluções parciais
    public void addToJson(String newBranch, JsonObject json, Object value){
        if(value==null){
            JsonObject specvalue =(JsonObject)value;
            jsoar.addBranchToJson(newBranch, json, specvalue);
            return;
        }
        Class a = value.getClass();
        if(a==String.class){
            String specvalue =(String)value;
            jsoar.addBranchToJson(newBranch, json, specvalue);
        }
        else if(a==double.class || a==float.class){
            double specvalue =(double)value;
            jsoar.addBranchToJson(newBranch, json, specvalue);
        }
        else if(a==Integer.class){
            Integer spec = (Integer) value;
            double specvalue = spec.doubleValue();
            //double specvalue =(double)value;
            jsoar.addBranchToJson(newBranch, json, specvalue);
        }
        else if(a==Long.class){
            Long spec = (Long) value;
            double specvalue = spec.doubleValue();
            //double specvalue =(double)value;
            jsoar.addBranchToJson(newBranch, json, specvalue);
        }
        else if(a==Double.class){
            Double spec = (Double) value;
            double specvalue = spec;//.doubleValue();
            //double specvalue =(double)value;
            jsoar.addBranchToJson(newBranch, json, specvalue);
        }
        
        else{
            JsonObject specvalue = (JsonObject)value;
            jsoar.addBranchToJson(newBranch, json, specvalue);
        }
    }
    
    
    public void addToWme(String newBranch, Object value){
        Class a = value.getClass();
        if(a==String.class){
            String specvalue =(String)value;
            jsoar.addBranchToWme(newBranch,specvalue,jsoar.inputLink);
        }
        else if(a==double.class){
            double specvalue =(double)value;
            jsoar.addBranchToWme(newBranch,specvalue,jsoar.inputLink);
        }
        
    }
    
    public void setInputLink(JsonObject json){
        jsoar.BuildWmeInputTreeFromJson(json, jsoar.inputLink);
        //jsoar.printWMEs();
    }
    
    public void removeWme(String pathToNode){
        jsoar.removeBranchFromWme(pathToNode);
    }
    
    public void removeJson(String pathToOldBranch, JsonObject json){
        jsoar.removeBranchFromJson(pathToOldBranch, json);
    }
    
    @Override
    public void calculateActivation() {
    }
}
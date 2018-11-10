package gildas.archeytime.Business;

import java.io.Serializable;
import java.util.ArrayList;

public class Sequence implements Serializable {
    private int rank;
    private String name;
    private ArrayList<SequenceItem> listOfItems;
    private boolean isCustom;

    private int dbId = -1;


    public Sequence(Integer rank, String name, ArrayList<SequenceItem> listOfItems, Boolean custom) {
        this.rank = rank;
        this.name = name;
        listOfItems = new ArrayList<>();
        this.listOfItems = listOfItems;
        this.isCustom = custom;
    }

    public Sequence() {
        listOfItems = new ArrayList<>();
    }

    public int getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<SequenceItem> getListOfItems() {
        return listOfItems;
    }

    public void setListOfItems(ArrayList<SequenceItem> listOfItems) {
        this.listOfItems = listOfItems;
    }

    public void addItemToSequence(Integer rank, SequenceItem item){
        this.listOfItems.add(rank,item);
    }

    public SequenceItem getItemOfSequence(Integer rank){
        return this.listOfItems.get(rank);
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }

    public int getTotalDuration(){
        int ret = 0;
        for (int i = 0; i<this.listOfItems.size(); i ++){
            if (this.listOfItems.get(i).getType().equals(Types.Duration)){
                ret = ret + this.listOfItems.get(i).getDuration();
            }
        }
        return ret;
    }

    public int countSignal(){
        int ret = 0;
        for (int i = 0; i<this.listOfItems.size(); i ++){
            if (this.listOfItems.get(i).getType().equals(Types.Signal)){
                ret = ret + 1;
            }
        }
        return ret;
    }
}

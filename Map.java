import java.io.*;
import java.util.*;

public class Map{
    private String[] map;
    private ArrayList<NPC> enemies;
    private Position size;
    private Position entrance;
    private Position exit;

    //Y: string index in map array, 0 is top, len-1 is bottom
    //X: char position in string, 0 is left, len-1 is right

    public Map(String[] map, ArrayList<NPC> enemies, Position size, Position entrance, Position exit){
        this.map = map;
        this.enemies = enemies;
        this.size = size;
        this.entrance = entrance;
        this.exit = exit;
    }

    public String[] getMap() {
        return map;
    }

    public void setMap(String[] map) {
        this.map = map;
    }

    public ArrayList<NPC> getEnemies() {
        return enemies;
    }

    public void setEnemies(ArrayList<NPC> enemies) {
        this.enemies = enemies;
    }

    public Position getSize() {
        return size;
    }

    public void setSize(Position size) {
        this.size = size;
    }

    public Position getEntrance() {
        return entrance;
    }

    public void setEntrance(Position entrance) {
        this.entrance = entrance;
    }

    public Position getExit() {
        return exit;
    }

    public void setExit(Position exit) {
        this.exit = exit;
    }


    //Functions
    public int checkTouchNPC(Position pos){
        int outIndex = -1;
        for(int i = 0; i<enemies.size(); i++){
            Position currNpcPos = enemies.get(i).getCharacterPos();
            outIndex = (currNpcPos.yPos == pos.yPos && (currNpcPos.xPos + 1) == pos.xPos) ? i : outIndex;
            outIndex = (currNpcPos.yPos == pos.yPos && (currNpcPos.xPos - 1) == pos.xPos) ? i : outIndex;
            outIndex = (currNpcPos.xPos == pos.xPos && (currNpcPos.yPos + 1) == pos.yPos) ? i : outIndex;
            outIndex = (currNpcPos.xPos == pos.xPos && (currNpcPos.yPos - 1) == pos.yPos) ? i : outIndex;
            if(outIndex != -1)
                break;
        }
        return outIndex;
    }

    public boolean checkExit(Position pos){
        int distance = Math.abs(exit.xPos-pos.xPos) + Math.abs(exit.yPos-pos.yPos);
        return distance == 1 && enemies.isEmpty();
    }

    public static String[] editMap(String[] map, Position pos, char newC){
        String line = map[pos.yPos];
        line = line.substring(0,pos.xPos) + Character.toString(newC) + line.substring(pos.xPos+1);
        map[pos.yPos] = line;
        return map;
    }

    public void removeNPC(int npcIndex){
        editMap(this.map,this.enemies.get(npcIndex).getCharacterPos(),' ');
        this.enemies.remove(npcIndex);
    }

    //Returns a map object from a txt file
    public static Map loadFromFile(String mapPath, String npcDir){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(mapPath));
            String[] map = reader.lines().toArray(String[]::new);
            reader.close();
            File[] npcFiles = (new File(npcDir)).listFiles();
            ArrayList<NPC> npcs = new ArrayList<>();;
            for(int i = 0; i<npcFiles.length; i++){
                npcs.add(NPC.loadFromFile(npcFiles[i]));
                map = editMap(map,npcs.get(i).getCharacterPos(),'X');
            }
            Position ent = null, exit = null;
            for(int y = 0; y<map.length; y++){
                for(int x = 0; x<map[0].length(); x++){
                    if(map[y].charAt(x) == 'E'){
                        ent = new Position(x,y);
                        editMap(map,ent,' ');
                    }
                    if(map[y].charAt(x) == 'O'){
                        exit = new Position(x,y);
                        editMap(map,exit,'V');
                    }
                }
            }
            return new Map(map,npcs,new Position(map[0].length(), map.length), ent, exit);
        }catch(IOException e){
            return null;
        }
    }
}
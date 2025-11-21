import java.io.*;

public class NPC extends GameCharacter{
    private String[] dialogue;
    private String itemToGive;
    private int itemAttack;

    //Constructors
    public NPC(String name, String[] art, int maxHealth, int currentHealth, int attackStrength, Position characterPos, String[] dialogue, String itemToGive, int itemAttack){
        super(name,art,maxHealth,currentHealth,attackStrength,characterPos);
        this.dialogue = dialogue;
        this.itemToGive = itemToGive;
        this.itemAttack = itemAttack;
    }

    //Getters and Setters
    public String[] getDialogue(){
        return dialogue;
    }

    public String getItemToGive(){
        return itemToGive;
    }

    public int getItemAttack(){
        return itemAttack;
    }

    public void setDialogue(String[] d){
        dialogue = d;
    }

    public void setItemToGive(String g){
        itemToGive = g;
    }

    public void setItemAttack(int a){
        itemAttack = a;
    }



    //Other Functions

    //Loads a npc from a file
    public static NPC loadFromFile(File npcFile){
        try{
            BufferedReader reader = new BufferedReader(new FileReader(npcFile));
            String name = reader.readLine();
            int maxHealth = Integer.parseInt(reader.readLine());
            int attack = Integer.parseInt(reader.readLine());
            String[] posXY = reader.readLine().split(",");
            Position pos = new Position(Integer.parseInt(posXY[0]),Integer.parseInt(posXY[1]));
            String item = reader.readLine();
            int itemAttack = Integer.parseInt(reader.readLine());
            int artLines = Integer.parseInt(reader.readLine());
            String[] art = new String[artLines];
            for(int i = 0; i<artLines; i++)
                art[i] = reader.readLine();
            String[] dialogue = reader.lines().toArray(String[]::new);
            
            return new NPC(name,art,maxHealth,maxHealth,attack,pos,dialogue,item,itemAttack);
        }catch(Exception e){
            return null;
        }
    }
}
import java.util.ArrayList;

public class Player extends GameCharacter{
    private ArrayList<String> inventory; 
    private String weaponName;
    
    //Constructor
    public Player(String name, String[] art, int maxHealth, int currentHealth, int attackStrength, Position characterPos,
                  ArrayList<String> inventory, String weaponName) {
        super(name, art, maxHealth, currentHealth, attackStrength, characterPos);
        this.inventory = new ArrayList<>();
        this.weaponName = weaponName;
    }

    //Getters and Setters
    public ArrayList<String> getInventory() {
        return inventory;
    }

    public void setInventory(ArrayList<String> Inventory) {
        this.inventory = Inventory;
    }

    public String getWeaponName() {
        return weaponName;
    }

    public void setWeaponName(String weaponName) {
        this.weaponName = weaponName;
    }

    public void addItem(NPC npc, String prompt){
        if(!npc.getItemToGive().equals("")) {
            Display.cursorPosition(38,50);
            System.out.printf("\u001B[38;5;33m" + prompt + "\u001B[37m", npc.getName(), npc.getItemToGive());
            this.getInventory().add(npc.getItemToGive());
            if(npc.getItemAttack() != 0){
                this.setAttackStrength(npc.getItemAttack());
                this.setWeaponName(npc.getItemToGive());
            }
        }
    }

    //Other Functions
    public void move(int dir, String[] map){
        Position pos = getCharacterPos();
        switch(dir){
            case 0: if(map[pos.yPos-1].charAt(pos.xPos) == ' ')
                        pos.setYPos(pos.yPos-1);
                    break;
            case 1: if(map[pos.yPos].charAt(pos.xPos+1) == ' ')
                        pos.setXPos(pos.xPos+1);
                    break;
            case 2: if(map[pos.yPos+1].charAt(pos.xPos) == ' ')
                        pos.setYPos(pos.yPos+1);
                    break;
            case 3: if(map[pos.yPos].charAt(pos.xPos-1) == ' ')
                        pos.setXPos(pos.xPos-1);
                    break;
        }
        setCharacterPos(pos);
    }
}
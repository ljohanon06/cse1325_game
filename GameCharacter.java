public class GameCharacter{
    private String name;
    private String[] art;
    private int maxHealth;
    private int currentHealth;
    private int attackStrength;
    private Position characterPos;

    //Constructor
    public GameCharacter(String name, String[] art, int maxHealth, int currentHealth, int attackStrength, Position characterPos){
        this.name = name;
        this.art = art;
        this.maxHealth = maxHealth;
        this.currentHealth = currentHealth;
        this.attackStrength = attackStrength;
        this.characterPos = characterPos;
    }

    //Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getArt() {
        return art;
    }

    public void setArt(String[] art) {
        this.art = art;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    public int getAttackStrength() {
        return attackStrength;
    }

    public void setAttackStrength(int attackStrength) {
        this.attackStrength = attackStrength;
    }

    public Position getCharacterPos() {
        return characterPos;
    }

    public void setCharacterPos(Position characterPos) {
        this.characterPos = characterPos;
    }
    
    //Functions
}
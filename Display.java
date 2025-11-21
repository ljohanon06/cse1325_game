import java.util.*;

public class Display{

    static Scanner scan = new Scanner(System.in);

    public static void displayImage(String[] image, int x, int y){
        for(int i = 0; i<image.length; i++){
            cursorPosition(y+i,x);
            System.out.print(image[i]);
        }
        
    }

    public static void clearScreen(){
        System.out.print("\033[2J");
    }

    public static void cursorPosition(int r, int c){
        System.out.printf("\033[%d;%dH",r,c);
    }

    public static void clearLine(){
        System.out.print("\033[0K");
    }

    public static void clearRegion(int startRow, int endRow, int leftColumn){
        for(int i = startRow; i <= endRow; i++){
            cursorPosition(i,leftColumn);
            clearLine();
        }
    }

    public static void clearRegionLeft(int startRow, int endRow, int rightColumn){
        for(int i = startRow; i <= endRow; i++){
            cursorPosition(i,rightColumn);
            System.out.print("\033[1K");
        }
    }

    
    public static void hide(){
        System.out.print("\u001B[?25l");
    }

    public static void show(){
        System.out.print("\u001B[?25h");
    }
    
    public static void displayMap(Map mapMap, Position pos){
        String[] map_ = mapMap.getMap();
        String[] map = Arrays.copyOf(map_,map_.length);
        Position bounds = mapMap.getSize();
        Map.editMap(map,pos,'O');
        int minY = (pos.yPos-5 < 0) ? 0 : pos.yPos-5;
        int minX = (pos.xPos-5 < 0) ? 0 : pos.xPos-5;
        int maxY = (pos.yPos+5 >= bounds.yPos) ? bounds.yPos-1 : pos.yPos+5;
        int maxX = (pos.xPos+5 >= bounds.xPos) ? bounds.xPos-1 : pos.xPos+5;

        clearScreen();
        cursorPosition(2,10);
        System.out.println("\u001B[31mMAP\u001B[37m");
        cursorPosition(13+minY-maxY,15+minX-maxX);
        String fog = "\u001B[36m" + "~".repeat(maxX-minX+3) + "\u001B[37m";
        System.out.println(fog);
        for(int y = minY; y<=maxY; y++){
            cursorPosition(14+y-maxY,15+minX-maxX);
            System.out.println("\u001B[36m~\u001B[37m" + map[y].substring(minX,maxX+1) + "\u001B[36m~\u001B[37m");            
        }
        cursorPosition(15,15+minX-maxX);
        System.out.println(fog);


        
    }

    public static void displayNPC(NPC npc){
        clearRegion(0,29,25);
        cursorPosition(2,50);
        System.out.printf("\u001B[31mNPC: \u001B[38;5;33m%s, %d/%d\u001B[37m", npc.getName(), npc.getCurrentHealth(), npc.getMaxHealth());
        displayImage(npc.getArt(),45,3);
    }

    public static void displayInfo(Player player, int currentFloor){
        clearRegionLeft(30,40,45);
        cursorPosition(30,3);
        System.out.print("\u001B[31mInstructions: \u001B[37m");
        cursorPosition(31,3);
        System.out.print("Defeat enemies, collect items, ");
        cursorPosition(32,3);
        System.out.print("get to the bottom, and kill the boss");
        cursorPosition(33,3);
        System.out.print("Use arrow keys to move, Ctrl+C to quit");

        cursorPosition(35,3);
        System.out.print("\u001B[31mPlayer Information: \u001B[37m");
        cursorPosition(36,3);
        System.out.printf("Current Floor: %d", currentFloor + 1);
        cursorPosition(37,3);
        System.out.printf("Name: %s, Health: %d/%d ", player.getName(), player.getCurrentHealth(), player.getMaxHealth());
        cursorPosition(38,3);
        System.out.printf("Weapon: %s, Attack: %d", player.getWeaponName(), player.getAttackStrength());
        cursorPosition(39,3);
        if(player.getInventory().isEmpty()){
            System.out.print("Inventory is Empty");
        }else{
            System.out.printf("Inventory: %s", player.getInventory());
        }
    }

    public static int runDialogue(NPC npc){
        cursorPosition(30,50);
        System.out.print("\u001B[31mDialogue: \u001B[37m");
        String[] dialogue = npc.getDialogue();
        int currentLine = 0;
        boolean loop = true;
        while(loop){
            if(dialogue[currentLine].startsWith("return")){
                return Integer.parseInt(dialogue[currentLine].substring(7,8)); //Eg: "return 2" -> 2
            }
            clearRegion(31,38,50);
            String[] split = dialogue[currentLine].split(":");
            switch(split[0].charAt(0)){
                case 'Q': cursorPosition(31,50);
                          System.out.println("\u001B[38;5;33m" + split[1].trim() + "\u001B[37m");
                          int i = currentLine + 1;
                          String[] rSplit;
                          while(!dialogue[i].equals("")){
                            cursorPosition(31+i-currentLine,50);
                            rSplit = dialogue[i].split(":");
                            System.out.printf("\u001B[38;5;178m%d: %s\n\u001B[37m", i-currentLine,rSplit[1].substring(0,rSplit[1].length()-2));
                            i++;
                          }
                          cursorPosition(37,50); show();
                          System.out.print("\u001B[38;5;178mEnter response: \u001B[37m");
                          int response;
                          try{
                            response = Integer.parseInt(scan.nextLine()); hide();
                            if(response < 1 || response > (i-currentLine-1))
                                break;
                          }catch(NumberFormatException e){
                            break;
                          }
                          rSplit = dialogue[response + currentLine].split(":");
                          currentLine = Integer.parseInt(rSplit[2].trim()) -1; //add error handling!!!
                          break;
                case 'R': return -1; //Shouldn't happen
                case 'C': cursorPosition(31,50);
                          System.out.println("\u001B[38;5;33m" + split[1].trim() + "\u001B[37m");
                          currentLine++;
                          break;
            }
            if(currentLine >= dialogue.length)
                loop = false;
        }
        return -1;
    }
    
}
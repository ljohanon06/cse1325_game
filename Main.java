import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class Main{

    //Static elements used so they can be accessed by the timer

    //GUI elements for input
    static JFrame frame;
    static JPanel arrowPanel;
    static JPanel attackPanel;
    static JPanel container;
    static CardLayout layout;

    static boolean updateScreen; //Fight button has been pressed

    static boolean fightReady; //Fight ready screen is up
    static boolean fightActive; //Fight is in progress
    static char pressed; //Pressed character goes here for fight
    static char expectedPress; //Required character goes here for fight
    static long nanoSeconds; //Timing for dealing damage every second

    static Map[] floors; 
    static int currentFloor;
    static Player player;

    static Timer timer; //Holds timer variable
    public static void main(String[] args){
        double difficulty = setup();
        setupFrame();
        turnOnArrowFrame();
        
        Display.hide();
        Display.displayMap(floors[currentFloor], player.getCharacterPos());
        Display.displayInfo(player, currentFloor);

        timer = new javax.swing.Timer (10,e -> {//Occurs every 10 ms
            //Check if player is touching NPC
            int npcIndex = floors[currentFloor].checkTouchNPC(player.getCharacterPos());
            NPC currNpc = null;                

            if(updateScreen && !fightActive && !fightReady){ //If a button is pressed and a fight isn't happening
                Display.clearRegion(30,37,50);
                Display.displayMap(floors[currentFloor], player.getCharacterPos());
                Display.displayInfo(player, currentFloor);

                if(npcIndex != -1){ //if the character is touching an npc
                    currNpc = floors[currentFloor].getEnemies().get(npcIndex);
                    turnOffFrame(); //Turns off the frame(which controls arrow keys) so the user can type
                    Display.displayNPC(currNpc);
                    int out = Display.runDialogue(currNpc);
                    switch(out){
                        case 0: //give item
                                player.addItem(currNpc, "%s gave you %s");
                                floors[currentFloor].removeNPC(npcIndex);
                                break;
                        case 1: //fight
                                Display.cursorPosition(38,50);
                                System.out.printf("\u001B[38;5;33m%s has chosen to fight you\u001B[37m", currNpc.getName());
                                delay(1000);
                                setupFight();                           
                                break;
                        case 2: //flee
                                player.addItem(currNpc, "%s ran away, dropping %s");
                                floors[currentFloor].removeNPC(npcIndex);
                                break;
                    }
                    turnOnArrowFrame(); //Turns on arrow keys again
                }

                boolean exit = floors[currentFloor].checkExit(player.getCharacterPos());
                if(exit){ //If the player is touching the exit and there is no npcs remaining
                    if(currentFloor == floors.length -1){ //If at top, game is over
                        Display.clearScreen();
                        Display.cursorPosition(3, 3);
                        System.out.print("You have won, congrats!");
                        turnOffFrame();
                        Display.scan.close();
                        timer.stop();
                    }else{ //Go to the next floor
                        currentFloor++;
                        player.setCharacterPos(floors[currentFloor].getEntrance());
                    }
                }
                updateScreen = false;
            }
            if(updateScreen && fightReady){ //This is the fight instruction screen
                currNpc = floors[currentFloor].getEnemies().get(npcIndex);
                if(pressed == 0){
                    startFight();
                    nanoSeconds = System.nanoTime();
                    currNpc.setCurrentHealth(currNpc.getMaxHealth());
                    player.setCurrentHealth(player.getMaxHealth());
                    Display.displayNPC(currNpc);
                    Display.displayInfo(player,currentFloor);
                }
            }

            if(fightActive && System.nanoTime() - nanoSeconds >= difficulty*1000000000l){ //Deals damage every difficulty*1sec
                currNpc = floors[currentFloor].getEnemies().get(npcIndex);
                nanoSeconds = System.nanoTime();
                player.setCurrentHealth(player.getCurrentHealth()-currNpc.getAttackStrength());
                Display.displayInfo(player,currentFloor);
            }

            if(updateScreen && fightActive){ //Fight is in progress and a button has been pressed
                currNpc = floors[currentFloor].getEnemies().get(npcIndex);
                if(pressed == expectedPress){ //Correct letter typed-deal damage and print new letter
                    currNpc.setCurrentHealth(currNpc.getCurrentHealth()-player.getAttackStrength());
                    Display.displayNPC(currNpc);
                    Display.clearRegion(31,40,50);
                    Display.cursorPosition(31, 50);
                    expectedPress = (char)(26*Math.random()+65);
                    System.out.printf("Type \u001B[31m%c\u001B[37m", expectedPress);
                }

                if(player.getCurrentHealth() <= 0){ //Player dead
                    fightActive = false;
                    turnOnArrowFrame();
                    Display.clearRegion(21,40,50);
                    Display.cursorPosition(31,50);
                    System.out.print("You have lost, please try again!");
                    delay(1500);
                }else if(currNpc.getCurrentHealth() <= 0){ //NPC dead (fight won)
                    fightActive = false;
                    turnOnArrowFrame();
                    Display.clearRegion(21,40,50);
                    Display.cursorPosition(31,50);
                    System.out.print("You have won the fight, congrats!");
                    player.addItem(currNpc, "You found a %s on %s");
                    delay(1500);
                }
            }
        });
        timer.start();       
    }

    

    public static double setup(){
        //Load from files
        floors = new Map[2];
        for(int i = 0; i<2; i++){
            floors[i] = Map.loadFromFile(String.format("Maps/map%d.txt",i+1), String.format("Maps/map%dnpcs",i+1));
        }
        currentFloor = 0;
        updateScreen = false;

        //Welcome Screen and Inputs
        Display.clearScreen();
        Display.cursorPosition(3,3);
        System.out.print("Welcome to Enter The Dungeon");
        Display.cursorPosition(4,3);
        System.out.print("Please enter your character's name: ");
        Display.show();
        String name = Display.scan.nextLine();
        Display.cursorPosition(5,3);
        System.out.print("Please enter difficulty: Easy(e), Medium(m), Hard(h): ");
        String input = Display.scan.nextLine();
        double out = 2; //Difficulty modifier, player will take damage every out seconds
        if(input.length() > 0)
            switch(Character.toLowerCase(input.charAt(0))){ 
                case 'e': out = 2; break;
                case 'm': out = 1.5; break;
                case 'h': out = 1; break;
                default: break;
            }
        
        Display.hide();
        Display.clearScreen();

        //Set initial values
        player = new Player(name, null, 100,100,10,floors[0].getEntrance(),null,"Old Sword");
        fightReady = false;
        fightActive = false;
        pressed = 1;
        expectedPress = 1;

        return out;
    }

    public static void setupFrame(){
        frame = new JFrame();
        frame.setUndecorated(true);
        frame.setOpacity(0f);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        layout = new CardLayout();
        container = new JPanel(layout);

        arrowPanel = new JPanel();
        arrowPanel.setFocusable(true);
        arrowPanel.setRequestFocusEnabled(true);
        arrowPanel.setFocusTraversalKeysEnabled(false);
        arrowPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e){
                switch(e.getKeyCode()){
                    case KeyEvent.VK_UP: player.move(0,floors[currentFloor].getMap());
                                         updateScreen = true;
                                         break;
                    case KeyEvent.VK_RIGHT: player.move(1,floors[currentFloor].getMap());
                                            updateScreen = true;
                                            break;
                    case KeyEvent.VK_DOWN: player.move(2,floors[currentFloor].getMap());
                                           updateScreen = true;
                                           break;
                    case KeyEvent.VK_LEFT: player.move(3,floors[currentFloor].getMap());
                                           updateScreen = true;
                                           break;
                    case KeyEvent.VK_X: frame.setVisible(false); break;
                }
            }
        });

        

        attackPanel = new JPanel();
        attackPanel.setFocusable(true);
        attackPanel.setRequestFocusEnabled(true);
        attackPanel.setFocusTraversalKeysEnabled(false);
        attackPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e){
                if(KeyEvent.VK_ENTER == e.getKeyCode()){
                    pressed = 0;
                    updateScreen = true;
                }else{
                    char c = e.getKeyChar();
                    if(c >= 'a' && c <= 'z' || c >= 'A' || c <= 'Z'){
                        pressed = Character.toUpperCase(c);
                        updateScreen = true;
                    }
                }
            }
        });

        container.add(arrowPanel,"arrow");
        container.add(attackPanel,"attack");
        frame.setContentPane(container);
    }

    public static void turnOnArrowFrame(){
        frame.setVisible(true);
        frame.toFront();
        frame.requestFocus();
        frame.requestFocusInWindow();

        layout.show(container, "arrow");
        container.revalidate();
        container.repaint();

        SwingUtilities.invokeLater(() -> {
            arrowPanel.requestFocus();
            arrowPanel.requestFocusInWindow();
        });
    }

    public static void turnOnAttackFrame(){
        frame.setVisible(true);
        frame.toFront();
        frame.requestFocus();
        frame.requestFocusInWindow();

        new Timer(50, e -> {
            layout.show(container, "attack");
            attackPanel.requestFocusInWindow();
            ((Timer)e.getSource()).stop();
        }).start();
    }

    public static void turnOffFrame(){
        frame.setVisible(false);
    }

    public static void setupFight(){
        Display.clearRegion(30,40,50);
        Display.cursorPosition(30,50);
        System.out.print("\u001B[31mFight: \u001B[37m");
        Display.cursorPosition(31,50);
        System.out.print("Type the characters as fast as you can");
        Display.cursorPosition(32,50);
        System.out.print("Every character you type deals damage to the enemy");
        Display.cursorPosition(33,50);
        System.out.print("Every second the enemy will deal damage to you");
        Display.cursorPosition(34,50);
        System.out.print("You will win if the enemy runs out of health before you do");
        Display.cursorPosition(35,50);
        System.out.print("Press enter to start the fight!!");

        turnOnAttackFrame();
        fightReady = true;
        fightActive = false;
    }

    public static void startFight(){
        fightReady = false;
        fightActive = true;
    

        Display.clearRegion(31,40,50);
        Display.cursorPosition(31, 50);
        expectedPress = (char)(26*Math.random()+65);
        System.out.printf("Type \u001B[31m%c\u001B[37m", expectedPress);
    }


    public static void delay(int ms){
        try {
            Thread.sleep(ms);
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
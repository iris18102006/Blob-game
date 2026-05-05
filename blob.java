import java.io.*;
import java.nio.file.*;
import java.util.*;

public class BlobPet {
    private static final String SAVE_FILE = "blobby.save";
    private static final Scanner scanner = new Scanner(System.in);
    
    private static final String PINK = "\u001B[38;5;213m";
    private static final String BLUE = "\u001B[38;5;81m";
    private static final String YELLOW = "\u001B[38;5;227m";
    private static final String GREEN = "\u001B[38;5;120m";
    private static final String RED = "\u001B[38;5;203m";
    private static final String RESET = "\u001B[0m";

    private String name = "Blobby";
    private int hunger = 50;
    private int happiness = 50;
    private int energy = 50;
    

    private String pendingFace = null;
    private String pendingColor = null;

    public static void main(String[] args) {
        new BlobPet().start();
    }

    private void start() {
        load();
        printBox("Welcome! Your blob " + name + " is waiting for you!");
        
        boolean running = true;
        while (running) {
            drawBlob();
            showStats();
            showMood();
            
            System.out.println(YELLOW + "\nWhat would you like to do?" + RESET);
            System.out.println("  1. Feed    2. Play    3. Sleep");
            System.out.println("  4. Pet     5. Rename  6. Exit");
            System.out.print("> ");
            
            String choice = scanner.nextLine().trim();
            System.out.println();
            
            if (choice.equals("1")) feed();
            else if (choice.equals("2")) play();
            else if (choice.equals("3")) sleep();
            else if (choice.equals("4")) pet();
            else if (choice.equals("5")) rename();
            else if (choice.equals("6")) { 
                pendingFace = "(^-^)/  < Bye!"; 
                pendingColor = PINK;
                drawBlob();
                save(); 
                running = false; 
            }
            else {
                pendingFace = "(@_@)  < What?";
                pendingColor = RED;
                System.out.println(RED + "Hmm? Blobby doesn't understand." + RESET);
            }
            
            tick();
        }
        
        printBox("Bye bye! I will miss you!");
    }

    private void drawBlob() {
        String face;
        String color;
        
     
        if (pendingFace != null) {
            face = pendingFace;
            color = pendingColor;
            pendingFace = null;
            pendingColor = null;
        } else {
            face = getStatFace();
            color = getStatColor();
        }
        
        System.out.println(color);
        System.out.println("         " + face);
        System.out.println(RESET);
    }

  

    private void feed() {
        pendingFace = "(o_o)  < Yummy!";
        pendingColor = GREEN;
        if (hunger <= 0) {
            System.out.println(name + " is too full! They wobble and refuse the food.");
            happiness = Math.max(0, happiness - 5);
            pendingFace = "(x_x)  < No more...";
            pendingColor = RED;
        } else {
            hunger = Math.max(0, hunger - 25);
            happiness = Math.min(100, happiness + 5);
            System.out.println("You feed " + name + " a sparkly cupcake!");
            System.out.println(name + " goes: *nom nom nom*");
        }
    }

    private void play() {
        pendingFace = "(>o<)  < Wheee!";
        pendingColor = PINK;
        if (energy < 20) {
            System.out.println(name + " is too sleepy to play...");
            happiness = Math.max(0, happiness - 5);
            pendingFace = "(-_-)  < Too tired...";
            pendingColor = BLUE;
        } else {
            energy -= 20;
            hunger = Math.min(100, hunger + 15);
            happiness = Math.min(100, happiness + 20);
            System.out.println("You play catch with " + name + "!");
            System.out.println(name + " bounces around joyfully!");
        }
    }

    private void sleep() {
        pendingFace = "(-_-)z  < Zzz...";
        pendingColor = BLUE;
        energy = Math.min(100, energy + 40);
        hunger = Math.min(100, hunger + 10);
        System.out.println("You tuck " + name + " into a tiny blanket fort.");
        System.out.println(name + ": *snoozing blob sounds...*");
    }

    private void pet() {
        pendingFace = "(^.^)  < Love!";
        pendingColor = PINK;
        happiness = Math.min(100, happiness + 15);
        System.out.println("You gently pat " + name + "'s squishy head.");
        System.out.println(name + " purrs like a tiny motorboat!");
    }

    private void rename() {
        pendingFace = "(o_o)?  < Hmm?";
        pendingColor = YELLOW;
        System.out.print("New name for your blob: ");
        String newName = scanner.nextLine().trim();
        if (!newName.isEmpty()) {
            System.out.println(name + " is now named " + newName + "!");
            name = newName;
            pendingFace = "(*o*)  < Me! " + name + "!";
            pendingColor = GREEN;
        }
    }

  

    private String getStatFace() {
        if (hunger > 80) return "(X__X)  < Food...";
        if (energy < 20) return "(-_-)  < Tired...";
        if (happiness > 80) return "(*_*)  < Yay!";
        if (happiness < 20) return "(T_T)  < Sad...";
        if (hunger < 30 && happiness > 60) return "(^o^)  < Nice!";
        return "(^u^)  < Hey!";
    }

    private String getStatColor() {
        if (hunger > 80 || happiness < 20) return RED;
        if (energy < 20) return BLUE;
        if (happiness > 80) return PINK;
        if (hunger < 30 && happiness > 60) return GREEN;
        return YELLOW;
    }

    private void showMood() {
        String mood;
        if (hunger > 80) mood = RED + "STARVING" + RESET;
        else if (energy < 20) mood = BLUE + "EXHAUSTED" + RESET;
        else if (happiness > 80) mood = PINK + "ECSTATIC" + RESET;
        else if (happiness < 20) mood = BLUE + "HEARTBROKEN" + RESET;
        else if (hunger < 30 && happiness > 60) mood = GREEN + "CONTENT" + RESET;
        else mood = YELLOW + "OKAY" + RESET;
        
        System.out.println("  Mood: " + mood);
    }

    private void showStats() {
        System.out.println("  Hunger:    " + bar(hunger, RED));
        System.out.println("  Happiness: " + bar(happiness, PINK));
        System.out.println("  Energy:    " + bar(energy, BLUE));
    }

    private String bar(int value, String color) {
        int filled = value / 10;
        StringBuilder b = new StringBuilder(color + "[");
        for (int i = 0; i < 10; i++) b.append(i < filled ? "*" : ".");
        b.append("]" + RESET + " " + value + "%");
        return b.toString();
    }

    private void tick() {
        hunger = Math.min(100, hunger + 2);
        happiness = Math.max(0, happiness - 1);
        
        if (hunger >= 100 || happiness <= 0) {
            System.out.println(RED + "\n!!! " + name + " needs attention urgently !!!" + RESET);
        }
    }

    private void printBox(String text) {
        int len = text.length() + 4;
        System.out.println(PINK + "+" + "-".repeat(len - 2) + "+");
        System.out.println("|  " + text + "  |");
        System.out.println("+" + "-".repeat(len - 2) + "+" + RESET);
    }

    private void save() {
        try (PrintWriter out = new PrintWriter(SAVE_FILE)) {
            out.println(name);
            out.println(hunger);
            out.println(happiness);
            out.println(energy);
        } catch (IOException e) {
            System.out.println("See you soon hooman!!!");
        }
    }

    private void load() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(SAVE_FILE));
            name = lines.get(0);
            hunger = Integer.parseInt(lines.get(1));
            happiness = Integer.parseInt(lines.get(2));
            energy = Integer.parseInt(lines.get(3));
            System.out.println("Welcome back! " + name + " missed you!");
        } catch (Exception e) {
            System.out.print("Name your new blob: ");
            name = scanner.nextLine().trim();
            if (name.isEmpty()) name = "Blobby";
        }
    }
}
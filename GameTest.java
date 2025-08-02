package gravtitypackage;

import enigma.core.Enigma;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import enigma.console.TextAttributes;
import java.awt.Color;

public class GameTest {
	   public static void main(String[] args) throws Exception {
	      Scanner filereader = new Scanner(new File("maze.txt"));
	      enigma.console.Console myconsole = Enigma.getConsole("Maze",85,30,22);
	      TextAttributes attrs = new TextAttributes(Color.RED, Color.BLACK);
	      while(filereader.hasNextLine())
	      {
	    	  String data = filereader.nextLine();
	    	  myconsole.setTextAttributes(attrs);
	    	  System.out.println(data);
	      }
	      Game myGame = new Game();
	   }
	}



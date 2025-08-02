package gravtitypackage;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import enigma.console.TextAttributes;
import enigma.core.Enigma;
import enigma.event.TextMouseListener;

public class Game {
	   public enigma.console.Console cn = Enigma.getConsole("GAME",85,30,22);

	   public TextMouseListener tmlis; 
	   public KeyListener klis; 

	   // ------ Standard variables for mouse and keyboard ------
	   public int mousepr;          // mouse pressed?
	   public int mousex, mousey;   // mouse text coords.
	   public int keypr;   // key pressed?
	   public int rkey;    // key   (for press/release)
	   int[][] gameField = new int[25][55];						// 1:Wall , 0:Empty, 2: Earth Square, 3: Treasure 1, 
	   															//4:Treasure 2, 5: Treasure 3, 6: Boulder, 7: Robot,8: Player
	   Stack items = new Stack(8);
	   Stack copyItems = new Stack(8);
	   // ----------------------------------------------------
	   Random rand = new Random();
	   int px = 0, py = 0;
	   int bx = 0, by = 0;
	   TextAttributes playerColor = new TextAttributes(Color.GREEN, Color.BLACK);
	   int backpacky = 13;
	   int treasureType = 0;
	   boolean itemsFullness = false;
	   int itemsTop = 0;
	   int itemsSecond = 0;
	   int point = 0;
	   int tpRights = 3;
	   CircularQueue inputQueue = new CircularQueue(15);
	   long lastExecutionTime = 0;
	   TextAttributes treasureColor = new TextAttributes(Color.MAGENTA,Color.BLACK);
	   TextAttributes boulderColor = new TextAttributes(Color.BLUE,Color.BLACK);
	   TextAttributes robotColor = new TextAttributes(Color.ORANGE,Color.BLACK);
	   TextAttributes endColor = new TextAttributes(Color.YELLOW,Color.BLACK);
	   TextAttributes overColor = new TextAttributes(Color.YELLOW,Color.BLACK);
	   Player player =new Player( px,  py,  tpRights,  point);	   
	   Robot[] robots = new Robot[30];
	   Boulder[] boulders = new Boulder[180];
	   int robotCount = 0;	   
	   Boolean isOver = false;
	   char pressedDirection = ' ';
	   Boolean isAvailable = true;
	   int i = 0;
	   int j = 0;
	   
	   Game() throws Exception {   // --- Contructor		   
		   
		   			// GAME INITALIZATION //	   
		   wallGeneration();	  				// Wall generation			   
		   emptyToEarth();		  				// Convert empty squares to earth squares	       
	       earthToBoulder(boulderColor);	   	// Convert 180 earth squares to boulders	      
	       earthToTreasure(treasureColor);      // Convert 30 earth squares to treasures       
	       earthToEmpty();		   				// Convert 200 earth squares to empty squares       
	       playerPlacement();	   				// Player P is placed on random earth square
	       queueGeneration();	   				// Input Queue Generation	
	       robotGeneration(robotColor);			// Convert 7 earth squares to robots
	       	    
	      klis=new KeyListener() {
	         public void keyTyped(KeyEvent e) {}
	         public void keyPressed(KeyEvent e) {
	            if(keypr==0) {
	               keypr=1;
	               rkey=e.getKeyCode();
	            }
	         }
	         public void keyReleased(KeyEvent e) {}
	      };
	      cn.getTextWindow().addKeyListener(klis);
	      // ----------------------------------------------------

	      long startTime = System.currentTimeMillis();
	      long currentTime;
	      int timerSeconds;
	      backpack();
	      backpacky = 21;
	      while(true) {
	         if(mousepr==1) {  // if mouse button pressed
	            cn.getTextWindow().output(mousex,mousey,'#');  // write a char to x,y position without changing cursor position
	            px=mousex; py=mousey;
	            player.setPy(py);
	            player.setPx(px);
	            
	            mousepr=0;     // last action  
	         }
	         if(keypr==1) {    // if keyboard button pressed
	        	char rckey=(char)rkey;
	            //        left          right          up            down
	            if(rckey=='%' || rckey=='\'' || rckey=='&' || rckey=='(')
	            {	
	                cn.getTextWindow().output(player.getPx(), player.getPy(), ' '); //Önceki yerini silme
	                if(rkey==KeyEvent.VK_LEFT)
	                {
	                	if(gameField[player.getPy()][player.getPx()-1] == 7)
		 					   isOver = true;
	                	pressedDirection = 'l';
	                	if(gameField[player.getPy()][player.getPx()-1] == 2)
	                	{
	                		gameField[player.getPy()][player.getPx()] = 0;
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output(' ');	                		
	                		player.setPx(player.getPx()-1);
	                		gameField[player.getPy()][player.getPx()] = 8;
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output('P', playerColor);
	                	}
	                	else if(gameField[player.getPy()][player.getPx()-1] == 0)
	                	{
	                		gameField[player.getPy()][player.getPx()] = 0;
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output(' ');	                		
	                		player.setPx(player.getPx()-1);
	                		gameField[player.getPy()][player.getPx()] = 8;
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output('P', playerColor);
	                	}
	                	else if(gameField[player.getPy()][player.getPx()-1] == 6 
	                		 && gameField[player.getPy()][player.getPx()-2] == 0)
	                	{         
	                 	    gameField[player.getPy()][player.getPx()] = 0; 
	                		gameField[player.getPy()][player.getPx()-1] = 8; 
	                 	    gameField[player.getPy()][player.getPx()-2] = 6;  
	                 	    cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	                		cn.getTextWindow().output(' ');
	                		cn.getTextWindow().setCursorPosition(player.getPx()-1, player.getPy());
	                		cn.getTextWindow().output('P',playerColor);                		
	                		cn.getTextWindow().setCursorPosition(player.getPx()-2, player.getPy());
	                 	    cn.getTextWindow().output('O', boulderColor);                 	                   	    
	                 	    player.setPx(player.getPx()-1);
	                	}
	                	else if(player.getPx() > 0
	                			&& gameField[player.getPy()][player.getPx() - 1] < 6
	                			&& gameField[player.getPy()][player.getPx() - 1] > 2)
	                	{
	                		if(copyItems.isFull())
	                			itemsFullness = true;
	                		else 
	                			itemsFullness = false;
	                		if(gameField[player.getPy()][player.getPx()-1] == 3)
	                		{
	                			treasure(1,itemsFullness);   
	                			gameField[player.getPy()][player.getPx()-1] = 8;  
	                		}
	                		else if(gameField[player.getPy()][player.getPx()-1] == 4)
	                		{
	                			treasure(2,itemsFullness); 
	                			gameField[player.getPy()][player.getPx()-1] = 8;  
	                		}
	                		else if(gameField[player.getPy()][player.getPx()-1] == 5)
	                		{
	                			treasure(3,itemsFullness); 
	                			gameField[player.getPy()][player.getPx()-1] = 8;  
	                		}     
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output(' ');
	    	                gameField[player.getPy()][player.getPx()] = 0;
	                		player.setPx(player.getPx()-1);
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output('P', playerColor);
	                	}
	                	else{
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output('P', playerColor);	                
	                	}
	                	Thread.sleep(10);	
	                }
	                if(rkey==KeyEvent.VK_RIGHT)
	                {	
	                	if(gameField[player.getPy()][player.getPx()+1] == 7)
		 					   isOver = true;
	                	pressedDirection = 'r';
	                	if(gameField[player.getPy()][player.getPx()+1] == 2)
	                	{
	                		gameField[player.getPy()][player.getPx()] = 0;
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output(' ');	                		
	                		player.setPx(player.getPx()+1);
	                		gameField[player.getPy()][player.getPx()] = 8;
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output('P', playerColor);
	                	}	                			                	
	                	else if(gameField[player.getPy()][player.getPx()+1] == 0)
	                	{
	                		gameField[player.getPy()][player.getPx()] = 0;
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output(' ');	                		
	                		player.setPx(player.getPx()+1);
	                		gameField[player.getPy()][player.getPx()] = 8;
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output('P', playerColor);
	                	}	                		
	                	else if(gameField[player.getPy()][player.getPx()+1] == 6 && gameField[player.getPy()][player.getPx()+2] == 0)
	                	{         
	                		gameField[player.getPy()][player.getPx()] = 0; 
	                		gameField[player.getPy()][player.getPx()+1] = 8; 
	                 	    gameField[player.getPy()][player.getPx()+2] = 6;  
	                 	    cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	                		cn.getTextWindow().output(' ');
	                		cn.getTextWindow().setCursorPosition(player.getPx()+1, player.getPy());
	                		cn.getTextWindow().output('P',playerColor);                		
	                		cn.getTextWindow().setCursorPosition(player.getPx()+2, player.getPy());
	                 	    cn.getTextWindow().output('O', boulderColor);                 	                   	    
	                 	    player.setPx(player.getPx()+1);
	                	}
	                	else if(player.getPx() < 99 
	                			&& gameField[player.getPy()][player.getPx() + 1] < 6
	                			&& gameField[player.getPy()][player.getPx() + 1] > 2)
	                	{
	                		if(copyItems.isFull())
	                			itemsFullness = true;
	                		else 
	                			itemsFullness = false;
	                		if(gameField[player.getPy()][player.getPx()+1] == 3)
	                		{
	                			treasure(1,itemsFullness);   
	                			gameField[player.getPy()][player.getPx()+1] = 8;  
	                		}
	                		else if(gameField[player.getPy()][player.getPx()+1] == 4)
	                		{
	                			treasure(2,itemsFullness); 
	                			gameField[player.getPy()][player.getPx()+1] = 8;  
	                		}
	                		else if(gameField[player.getPy()][player.getPx()+1] == 5)
	                		{
	                			treasure(3,itemsFullness); 
	                			gameField[player.getPy()][player.getPx()+1] = 8;  
	                		}    
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output(' ');
	    	                gameField[player.getPy()][player.getPx()] = 0;
	                		player.setPx(player.getPx()+1);
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output('P', playerColor);	                		
	                	}	
	                	else{
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output('P', playerColor);	                
	                	}
	                	Thread.sleep(10);
	                }
	                if(rkey==KeyEvent.VK_UP)
	                {
	                	if(gameField[player.getPy()-1][player.getPx()] == 7)
		 					   isOver = true;
	                	pressedDirection = 'u';
	                	if(gameField[player.getPy()-1][player.getPx()] == 2)
	                	{
	                		gameField[player.getPy()][player.getPx()] = 0;
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output(' ');	                		
	                		player.setPy(player.getPy()-1);
	                		gameField[player.getPy()][player.getPx()] = 8;
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output('P', playerColor);
	                	}	                		
	                	else if(gameField[player.getPy()-1][player.getPx()] == 0)
	                	{
	                		gameField[player.getPy()][player.getPx()] = 0;
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output(' ');	                		
	                		player.setPy(player.getPy()-1);
	                		gameField[player.getPy()][player.getPx()] = 8;
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output('P', playerColor);
	                	}
	                	else if(player.getPy() > 0
	                			&& gameField[player.getPy()-1][player.getPx()] < 6
	                			&& gameField[player.getPy()-1][player.getPx()] > 2)
	                	{
	                		if(copyItems.isFull())
	                			itemsFullness = true;
	                		else 
	                			itemsFullness = false;
	                		if(gameField[player.getPy()-1][player.getPx()] == 3)
	                		{
	                			treasure(1,itemsFullness);   
	                			gameField[player.getPy()-1][player.getPx()] = 8;  
	                		}
	                		else if(gameField[player.getPy()-1][player.getPx()] == 4)
	                		{
	                			treasure(2,itemsFullness); 
	                			gameField[player.getPy()-1][player.getPx()] = 8;  
	                		}
	                		else if(gameField[player.getPy()-1][player.getPx()] == 5)
	                		{
	                			treasure(3,itemsFullness); 
	                			gameField[player.getPy()-1][player.getPx()] = 8;  
	                		}  
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output(' ');
	    	                gameField[player.getPy()][player.getPx()] = 0;
	                		player.setPy(player.getPy()-1);
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output('P', playerColor);
	                	}
	                	else{
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output('P', playerColor);	                
	                	}
	                	Thread.sleep(10);
	                }	
	                if(rkey==KeyEvent.VK_DOWN) 
	                {
	                	if(gameField[player.getPy()+1][player.getPx()] == 7)
	 					   isOver = true;
	                	pressedDirection = 'd';	
	                	if(gameField[player.getPy()+1][player.getPx()] == 2)
	                	{
	                		gameField[player.getPy()][player.getPx()] = 0;
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output(' ');	                		
	                		player.setPy(player.getPy()+1);
	                		gameField[player.getPy()][player.getPx()] = 8;
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output('P', playerColor);
	                	}	                		
	                	else if(gameField[player.getPy()+1][player.getPx()] == 0)
	                	{
	                		gameField[player.getPy()][player.getPx()] = 0;
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output(' ');	                		
	                		player.setPy(player.getPy()+1);
	                		gameField[player.getPy()][player.getPx()] = 8;
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output('P', playerColor);
	                	}
	                	else if(player.getPy() < 49 
	                			&& gameField[player.getPy()+1][player.getPx()] < 6
	                			&& gameField[player.getPy()+1][player.getPx()] > 2)
	                	{
	                		if(copyItems.isFull())
	                			itemsFullness = true;
	                		else 
	                			itemsFullness = false;
	                		if(gameField[player.getPy()+1][player.getPx()] == 3)
	                		{
	                			treasure(1,itemsFullness);   
	                			gameField[player.getPy()+1][player.getPx()] = 8;  
	                		}
	                		else if(gameField[player.getPy()+1][player.getPx()] == 4)
	                		{
	                			treasure(2,itemsFullness); 
	                			gameField[player.getPy()+1][player.getPx()] = 8;  
	                		}
	                		else if(gameField[player.getPy()+1][player.getPx()] == 5)
	                		{
	                			treasure(3,itemsFullness); 
	                			gameField[player.getPy()+1][player.getPx()] = 8;  
	                		}
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output(' ', playerColor);
	    	                gameField[player.getPy()][player.getPx()] = 0;
	                		player.setPy(player.getPy()+1);
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output('P', playerColor);
	                	}
	                	else{
	                		cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	    	                cn.getTextWindow().output('P', playerColor);	                
	                	}
	                	Thread.sleep(10);
	                }                	                
	            }
	            else cn.getTextWindow().output(rckey);
	            
	            if(rkey==KeyEvent.VK_SPACE) {
	               //String str;         
	               //str=cn.readLine();     // keyboardlistener running and readline input by using enter 
	               if(tpRights >= 1)
	               {
	            	   cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());  //boşluktan önceki koord silme
	                   cn.getTextWindow().output(' ');
	                   playerPlacement();
	                   tpRights--;
	               }   
	               //cn.getTextWindow().setCursorPosition(5, 20);
	               //cn.getTextWindow().output(str);
	            }		            
	            
	            keypr=0;    // last action  
	         }
	         currentTime = System.currentTimeMillis();
	         timerSeconds = (int)((currentTime - startTime) / 1000);
	         //Display Input Queue
	         cn.getTextWindow().setCursorPosition(65,2);
	         cn.getTextWindow().output("Input");
	         cn.getTextWindow().setCursorPosition(65,3);
	         for(int i = 0; i < 15; i++)
	         {
	    		cn.getTextWindow().output((char)inputQueue.peek());
	        	inputQueue.enqueue(inputQueue.dequeue());
	         }
	         if(currentTime - lastExecutionTime >= 3000)
	         {
	        	 queueOperation(treasureColor,robotColor,boulderColor);
	        	 queueGeneration();
	        	 lastExecutionTime = currentTime;        	 
	         }
	         
	         // Display timer
	         cn.getTextWindow().setCursorPosition(65,10);
	         cn.getTextWindow().output("Time: " + timerSeconds);
	         // Display score
	         cn.getTextWindow().setCursorPosition(65,8);
	         cn.getTextWindow().output("Score: " + point);
	         //Display Teleport
	         cn.getTextWindow().setCursorPosition(65,6);
	         cn.getTextWindow().output("Teleport: " + tpRights);   
	         
	         
	         boulderFalling(boulders,pressedDirection);		
	         
	         if(robots[0] != null)
	        	 robotMoving(0);
  
	         if(isOver)
		         break;      
	         Thread.sleep(10);	         
	         
	      }
	      cn.getTextWindow().setCursorPosition(15, 45);
	      cn.getTextWindow().output("GAME OVER",overColor);
	      cn.getTextWindow().setCursorPosition(30, 45);
	      cn.getTextWindow().output("YOUR SCORE: ",endColor);
	      cn.getTextWindow().setCursorPosition(41, 45);
	      cn.getTextWindow().output(" " + point,endColor);
	   }
	   
	   public void emptyToEarth()
	   {
		   for (int i = 1; i < 24; i++) {
	           for (int j = 1; j < 54; j++) {
	               if (gameField[i][j] != 1) {
	                   cn.getTextWindow().output(j, i, ':');  // write earth char to x,y position
	                   gameField[i][j] = 2;
	               }
	           }
	       }
	   }
	   public void earthToBoulder(TextAttributes boulderColor)
	   {
	       int boulderCount = 0;       
	       while (boulderCount < 180) {
	           int x = rand.nextInt(54) + 1;
	           int y = rand.nextInt(24) + 1;
	           if (gameField[y][x] == 2) {
	        	   gameField[y][x] = 6;
	        	   cn.getTextWindow().setCursorPosition(x, y);
	        	   cn.getTextWindow().output('O', boulderColor); 	        	   
	        	   boulders[boulderCount] = new Boulder(x,y);
	        	   boulders[boulderCount].setBx(x);
	        	   boulders[boulderCount].setBy(y);
	               boulderCount++;
	           }
	       }
	   }
	   public void earthToTreasure(TextAttributes treasureColor)
	   {
		   int treasureCount = 0;
	       while (treasureCount < 30) {
	           int x = rand.nextInt(54) + 1;
	           int y = rand.nextInt(24) + 1;
	           if (gameField [y][x] == 2) {
	               int treasureType = rand.nextInt(3) + 1;               
	               if (treasureType == 1) {
	                   gameField[y][x] = 3;
	                   cn.getTextWindow().setCursorPosition(x, y);
	            	   cn.getTextWindow().output('1', treasureColor); 
	               } else if (treasureType == 2) {
	                   gameField[y][x] = 4;
	                   cn.getTextWindow().setCursorPosition(x, y);
	            	   cn.getTextWindow().output('2', treasureColor);
	               } else {
	            	   gameField[y][x] = 5;
	            	   cn.getTextWindow().setCursorPosition(x, y);
	            	   cn.getTextWindow().output('3', treasureColor);
	               }
	               treasureCount++;
	           }
	       } 
	   }
	   public void earthToEmpty()
	   {   
		   int emptyCount = 0;
	       while (emptyCount < 200) {
	           int x = rand.nextInt(54) + 1;
	           int y = rand.nextInt(24) + 1;
	           if (gameField[y][x] == 2) {
	        	   cn.getTextWindow().output(x,y,' ');
	               gameField[y][x] = 0;
	               emptyCount++;
	           }
	       }       
	   }
	   public void playerPlacement()
	   {
		   boolean flag = false;
	       while(!flag)
	       {
	    	   px = rand.nextInt(54) + 1;
	    	   player.setPx(px);
	           py = rand.nextInt(24) + 1;
	           player.setPy(py);
	           if (gameField[player.getPy()][player.getPx()] == 2 
	        		   || gameField[player.getPy()][player.getPx()] == 0) {
	        	   flag = true;
	        	   gameField[player.getPy()][player.getPx()] = 8;
	        	   cn.getTextWindow().setCursorPosition(player.getPx(), player.getPy());
	               cn.getTextWindow().output('P', playerColor); // write player char to x,y position
	           }      
	       }

	   }
	   public void wallGeneration()
	   {
		   for(int i=0; i<25; i++)
		   {
			   gameField[i][0] = 1;    // sol taraf
			   gameField[i][54] = 1;   // sağ taraf
		   }
		   for(int j = 0; j < 55; j++)
		   {
		 		  gameField[0][j] = 1;		//üst taraf
		 		  gameField[24][j] = 1;		//alt taraf
		   }
		   for(int k = 0; k < 50; k++)
		   {
		 		  gameField[8][k] = 1;		//üstteki yarım duvar
		   }
		   for(int t = 5; t < 55; t++)
		   {
		 		  gameField[16][t] = 1;		//alttaki yarım duvar
		   }  
	   }
	   public void backpack()
	   {
		   for(int y = 0; y < 9; y++)
		   {
			   cn.getTextWindow().setCursorPosition(65,backpacky);
		       cn.getTextWindow().output("|   |");
		       backpacky++;       
		   }
		   cn.getTextWindow().setCursorPosition(65,21);
		   cn.getTextWindow().output("+---+");
		   backpacky = 22;
		   cn.getTextWindow().setCursorPosition(64,backpacky);
		   cn.getTextWindow().output("Backpack"); 
	   }
	   public void treasure(int n,boolean fullness)
	   {
		   if(n == 1 && fullness == false)
		   {
			    items.push(1);  
				copyItems.push(1);                			
				backpacky--;
				cn.getTextWindow().setCursorPosition(67,backpacky);
				System.out.println(copyItems.peek());
				if(itemsTop == 1)
				{
					point += 10;
					player.setPoints(point);
					cn.getTextWindow().setCursorPosition(67,backpacky);
					System.out.println(" ");
					copyItems.pop();
					items.pop();
					backpacky ++;
					cn.getTextWindow().setCursorPosition(67,backpacky);
					System.out.println(" ");
					copyItems.pop();
					items.pop();
					backpacky++;
					if(copyItems.size() > 0)
						itemsTop = (int)copyItems.peek();
					else 
						itemsTop = 0;                    				
				}
				else
					itemsTop = 1;
			   
		   }
		   else if(n == 1 && fullness == true)
		   {
			    copyItems.pop();
				items.pop();
				itemsSecond = (int)copyItems.peek();
				copyItems.push(1);
				items.push(1);
				cn.getTextWindow().setCursorPosition(67,13);
				System.out.println(copyItems.peek());
				if(itemsTop == 1 || itemsSecond == 1)
				{
					point += 10;
					cn.getTextWindow().setCursorPosition(67,backpacky);
					System.out.println(" ");
					copyItems.pop();
					items.pop();
					backpacky ++;
					cn.getTextWindow().setCursorPosition(67,backpacky);
					System.out.println(" ");
					copyItems.pop();
					items.pop();
					backpacky++;
					if(copyItems.size() > 0)
						itemsTop = (int)copyItems.peek();
					else 
						itemsTop = 0;                    				
				}
				else
					itemsTop = 1;
		   }
		   else if(n == 2 && fullness == false)
		   {
			    items.push(2);  
				copyItems.push(2);                			
				backpacky--;
				cn.getTextWindow().setCursorPosition(67,backpacky);
				System.out.println(copyItems.peek());
				if(itemsTop == 2)
				{
					point += 40;
					cn.getTextWindow().setCursorPosition(67,backpacky);
					System.out.println(" ");
					copyItems.pop();
					items.pop();
					backpacky ++;
					cn.getTextWindow().setCursorPosition(67,backpacky);
					System.out.println(" ");
					copyItems.pop();
					items.pop();
					backpacky++;
					if(copyItems.size() > 0)
						itemsTop = (int)copyItems.peek();
					else 
						itemsTop = 0;  
				}
				else
					itemsTop = 2;
		   }
		   else if(n == 2 && fullness == true)
		   {
			    copyItems.pop();
				items.pop();
				itemsSecond = (int)copyItems.peek();
				copyItems.push(2);
				items.push(2);
				cn.getTextWindow().setCursorPosition(67,13);
				System.out.println(copyItems.peek());
				if(itemsTop == 2 || itemsSecond == 2)
				{
					point += 40;
					cn.getTextWindow().setCursorPosition(67,backpacky);
					System.out.println(" ");
					copyItems.pop();
					items.pop();
					backpacky ++;
					cn.getTextWindow().setCursorPosition(67,backpacky);
					System.out.println(" ");
					copyItems.pop();
					items.pop();
					backpacky++;
					if(copyItems.size() > 0)
						itemsTop = (int)copyItems.peek();
					else 
						itemsTop = 0;  
				}
				else
					itemsTop = 2;							   
		   }
		   else if(n == 3 && fullness == false)
		   {
			    items.push(3);  
				copyItems.push(3);                			
				backpacky--;
				cn.getTextWindow().setCursorPosition(67,backpacky);
				System.out.println(copyItems.peek());
				if(itemsTop == 3)
				{
					point += 90;
					tpRights++;
					cn.getTextWindow().setCursorPosition(67,backpacky);
					System.out.println(" ");
					copyItems.pop();
					items.pop();
					backpacky ++;
					cn.getTextWindow().setCursorPosition(67,backpacky);
					System.out.println(" ");
					copyItems.pop();
					items.pop();
					backpacky++;
					if(copyItems.size() > 0)
						itemsTop = (int)copyItems.peek();
					else 
						itemsTop = 0;  
				}
				else
					itemsTop = 3;
		   }
		   else
		   {
			    copyItems.pop();
				items.pop();
				itemsSecond = (int)copyItems.peek();
				copyItems.push(3);
				items.push(3);
				cn.getTextWindow().setCursorPosition(67,13);
				System.out.println(copyItems.peek());	
				if(itemsTop == 3 || itemsSecond == 3)
				{
					point += 90;
					tpRights++;
					cn.getTextWindow().setCursorPosition(67,backpacky);
					System.out.println(" ");
					copyItems.pop();
					items.pop();
					backpacky ++;
					cn.getTextWindow().setCursorPosition(67,backpacky);
					System.out.println(" ");
					copyItems.pop();
					items.pop();
					backpacky++;
					if(copyItems.size() > 0)
						itemsTop = (int)copyItems.peek();
					else 
						itemsTop = 0;  
				}
				else
					itemsTop = 3;
		   }
			   
	   }
	   public void robotGeneration(TextAttributes robotColor) 
	   {       
	       while (robotCount < 7) {
	           int x = rand.nextInt(54) + 1;
	           int y = rand.nextInt(24) + 1;
	           if (gameField[y][x] == 2) {
	        	   gameField[y][x] = 7;
	        	   cn.getTextWindow().setCursorPosition(x, y);
	        	   cn.getTextWindow().output('X', robotColor);
	        	   robots[robotCount] = new Robot(x,y);
	        	   robots[robotCount].setRx(x);
	        	   robots[robotCount].setRy(y);
	               robotCount++;
	           }
	       }
	   }
	   public void queueGeneration()
	   {
		   while(!inputQueue.isFull())
		   {
			   int probability = 0;
			   probability = rand.nextInt(40) + 1;	   
			   if(probability < 7)				//Generation of 1
			   {
				   inputQueue.enqueue('1');
			   }
			   else if(probability < 12)		//Generation of 2
			   {
				   inputQueue.enqueue('2');		   
			   }
			   else if(probability < 16)		//Generation of 3
			   {
				   inputQueue.enqueue('3');
			   }
			   else if(probability < 17)		//Generation of Robot
			   {
				   inputQueue.enqueue('x');
			   }
			   else if(probability < 27)		//Generation of boulder
			   {
				   inputQueue.enqueue('o');
			   }
			   else if(probability < 36)		//Generation of earth square
			   {
				   inputQueue.enqueue(':');
			   }
			   else								//Generation of empty square
			   {
				   inputQueue.enqueue('e');
			   }
		   }
		   
	   }
	   public void queueOperation(TextAttributes treasureColor,TextAttributes robotColor, TextAttributes boulderColor)
	   {
		   if((char)inputQueue.peek() == '1')
		   {
			   boolean flag = true;
			   while(flag)
			   {
				   int x = rand.nextInt(54) + 1;
		           int y = rand.nextInt(24) + 1;
		           if(gameField[y][x] == 0 || gameField[y][x] == 2)
		           {
		        	   cn.getTextWindow().setCursorPosition(x, y);
	            	   cn.getTextWindow().output('1', treasureColor);	        	   
		        	   gameField[y][x] = 3;	        	   
		        	   flag = false;
		           }
			   }
			   inputQueue.dequeue();
		   }
		   else if((char)inputQueue.peek() == '2')
		   {
			   boolean flag = true;
			   while(flag)
			   {
				   int x = rand.nextInt(54) + 1;
		           int y = rand.nextInt(24) + 1;
		           if(gameField[y][x] == 0 || gameField[y][x] == 2)
		           {
		        	   cn.getTextWindow().setCursorPosition(x, y);
	            	   cn.getTextWindow().output('2', treasureColor);
		        	   gameField[y][x] = 4;	        	   
		        	   flag = false;
		           }
			   }	
			   inputQueue.dequeue();
		   }
		   else if((char)inputQueue.peek() == '3')
		   {
			   boolean flag = true;
			   while(flag)
			   {
				   int x = rand.nextInt(54) + 1;
		           int y = rand.nextInt(24) + 1;
		           if(gameField[y][x] == 0 || gameField[y][x] == 2)
		           {
		        	   cn.getTextWindow().setCursorPosition(x, y);
	            	   cn.getTextWindow().output('3', treasureColor);
		        	   gameField[y][x] = 5;	        	   
		        	   flag = false;
		           }
			   }	
			   inputQueue.dequeue();
		   }
		   else if((char)inputQueue.peek() == 'x')
		   {
			   boolean flag = true;
			   while(flag)
			   {
				   int x = rand.nextInt(54) + 1;
		           int y = rand.nextInt(24) + 1;
		           if(gameField[y][x] == 0 || gameField[y][x] == 2)
		           {
		        	   cn.getTextWindow().setCursorPosition(x, y);
	            	   cn.getTextWindow().output('X', robotColor);
		        	   gameField[y][x] = 7;	        	   
		        	   flag = false;
		        	   robotCount++;
		           }
			   }
			   inputQueue.dequeue();
		   }
		   else if((char)inputQueue.peek() == 'o')
		   {
			   boolean flag = true;
			   boolean flag2 = true;
			   while(flag)
			   {
				   int x = rand.nextInt(54) + 1;
		           int y = rand.nextInt(24) + 1;
		           if(gameField[y][x] == 0 || gameField[y][x] == 2)
		           {
		        	   cn.getTextWindow().setCursorPosition(x, y);
	            	   cn.getTextWindow().output('O', boulderColor);
		        	   gameField[y][x] = 6;	        	   
		        	   flag = false;
		           }	           
			   }
			   while(flag2)
			   {
				   int x = rand.nextInt(54) + 1;
		           int y = rand.nextInt(24) + 1;
		           if(gameField[y][x] == 6)
		           {
		        	   cn.getTextWindow().setCursorPosition(x, y);
	            	   cn.getTextWindow().output(' ');
		        	   gameField[y][x] = 2;	        	   
		        	   flag2 = false;
		           }	
			   }
			   inputQueue.dequeue();
		   }
		   else if((char)inputQueue.peek() == ':' )
		   {
			   boolean flag = true;
			   while(flag)
			   {
				   int x = rand.nextInt(54) + 1;
		           int y = rand.nextInt(24) + 1;
		           if(gameField[y][x] == 0)
		           {
		        	   cn.getTextWindow().setCursorPosition(x, y);
	            	   cn.getTextWindow().output(':');
		        	   gameField[y][x] = 2;	        	   
		        	   flag = false;
		           }
			   }
			   inputQueue.dequeue();
		   }
		   else 
		   {
			   boolean flag = true;
			   while(flag)
			   {
				   int x = rand.nextInt(54) + 1;
		           int y = rand.nextInt(24) + 1;
		           if(gameField[y][x] == 2)
		           {
		        	   cn.getTextWindow().setCursorPosition(x, y);
	            	   cn.getTextWindow().output(' ');
		        	   gameField[y][x] = 0;	        	   
		        	   flag = false;
		           }
			   }
			   inputQueue.dequeue();
		   }
		   
		   
	   }
	   public void boulderFalling(Boulder[] boulders,char pd) throws InterruptedException
	   {	
		   boolean flag = false;	
		   String[] prevOps = new String[boulders.length];
		   while(!flag)
		   {
			   for(int i = 0; i < 180; i++)
			   {	
				   if(gameField[boulders[i].getBy() + 1][boulders[i].getBx()] == 7)
				   {
					   gameField[boulders[i].getBy()][boulders[i].getBx()] = 0;
					   gameField[boulders[i].getBy() + 1][boulders[i].getBx()] = 6;
					   cn.getTextWindow().setCursorPosition(boulders[i].getBx(), boulders[i].getBy());
		       		   cn.getTextWindow().output(' ');               		
		       		   cn.getTextWindow().setCursorPosition(boulders[i].getBx(), boulders[i].getBy() + 1);
		        	   cn.getTextWindow().output('O', boulderColor);
		        	   point += 900;
		        	   robotCount --;
		        	   robots[j] = null;
		        		   		        	   
				   }
				   if(gameField[boulders[i].getBy() + 1][boulders[i].getBx()] == 0)
				   {
					   fall(i);
					   prevOps[i] = "f";
					   if(gameField[boulders[i].getBy() + 1][boulders[i].getBx()] == 2)
						   prevOps[i] = null;
				   }
				   if(gameField[boulders[i].getBy() + 1][boulders[i].getBx()] == 6
						   && gameField[boulders[i].getBy()][boulders[i].getBx()+1] == 0
						   && gameField[boulders[i].getBy()][boulders[i].getBx()-1] == 0)
				   {
					   sideFall(i);
					   prevOps[i] = "sf";
					   if(gameField[boulders[i].getBy() + 1][boulders[i].getBx()] == 2)
						   prevOps[i] = null;
				   }
				   if(gameField[boulders[i].getBy() + 1][boulders[i].getBx()] == 8
						   && prevOps[i] == null)
				   {
					   if(pd == 'l' && (gameField[boulders[i].getBy() + 1][boulders[i].getBx()-1] == 2 
							   || gameField[boulders[i].getBy() + 1][boulders[i].getBx()-1] == 0 )) {
						   staticBoulder(i,pd);
						   prevOps[i] = "sb";
					   }
					   else if(pd == 'r' && (gameField[boulders[i].getBy() + 1][boulders[i].getBx()+1] == 2 
							   || gameField[boulders[i].getBy() + 1][boulders[i].getBx()+1] == 0 )) {
						   staticBoulder(i,pd);
						   prevOps[i] = "sb";
					   }
					   
				   }
				   if(gameField[boulders[i].getBy() + 1][boulders[i].getBx()] == 8
						   && prevOps[i] == "f") {
					   noEscape(i);
					   
				   }
					   
				   				   
			   }
			   Thread.sleep(10);
			   for(int j = 0; j < 180; j++)
        	   {
        		   if(gameField[boulders[j].getBy() + 1][boulders[j].getBx()] != 0)
        			   flag = true;
        		   else
        		   {
        			   flag = false;
        			   break;
        		   }
        			   
        	   }			   
		   }
		   			  
			   
			   
		   
	   }
	   public void fall(int i) throws InterruptedException
	   {
		   while(gameField[boulders[i].getBy()+1][boulders[i].getBx()] == 0)
		   {
			   gameField[boulders[i].getBy()][boulders[i].getBx()] = 0;
			   gameField[boulders[i].getBy() + 1][boulders[i].getBx()] = 6;
			   cn.getTextWindow().setCursorPosition(boulders[i].getBx(), boulders[i].getBy());
       		   cn.getTextWindow().output(' ');               		
       		   cn.getTextWindow().setCursorPosition(boulders[i].getBx(), boulders[i].getBy() + 1);
        	   cn.getTextWindow().output('O', boulderColor);
        	   boulders[i].setBy(boulders[i].getBy()+1);
        	   Thread.sleep(10);
		   }
	   }
	   public void sideFall(int i)
	   {
		   if(gameField[boulders[i].getBy() + 1][boulders[i].getBx() + 1] == 0
				   && gameField[boulders[i].getBy() + 1][boulders[i].getBx() - 1] == 0)
		   {
			   cn.getTextWindow().setCursorPosition(boulders[i].getBx(), boulders[i].getBy());
       		   cn.getTextWindow().output(' '); 
       		   gameField[boulders[i].getBy()][boulders[i].getBx()] = 0;
       		   Random rand = new Random();
       		   int lor = rand.nextInt(2);		//lor : left or right
       		   if(lor == 0)				//Right Side 
       		   {
       			   cn.getTextWindow().setCursorPosition(boulders[i].getBx() + 1, boulders[i].getBy() + 1);
           		   cn.getTextWindow().output('O', boulderColor);
           		   if(gameField[boulders[i].getBy() + 1][boulders[i].getBx() + 1] == 8)
           			   isOver = true;
           		   gameField[boulders[i].getBy() + 1][boulders[i].getBx() + 1] = 6;
           		   boulders[i].setBx(boulders[i].getBx() + 1);
           		   boulders[i].setBy(boulders[i].getBy() + 1);
       		   }
       		   else						//Left Side 
       		   {
       			   cn.getTextWindow().setCursorPosition(boulders[i].getBx() - 1, boulders[i].getBy() + 1);
           		   cn.getTextWindow().output('O', boulderColor);
           		if(gameField[boulders[i].getBy() + 1][boulders[i].getBx() - 1] == 8)
        			   isOver = true;
           		   gameField[boulders[i].getBy() + 1][boulders[i].getBx()-1] = 6;
           		   boulders[i].setBx(boulders[i].getBx() - 1);
        		   boulders[i].setBy(boulders[i].getBy() + 1);
       		   }
       		   
		   }
		   else if(gameField[boulders[i].getBy() + 1][boulders[i].getBx() + 1] == 0 	//sadece sağı boşsa
				   && gameField[boulders[i].getBy() + 1][boulders[i].getBx() - 1] != 0 )
		   {
			   cn.getTextWindow().setCursorPosition(boulders[i].getBx(), boulders[i].getBy());
       		   cn.getTextWindow().output(' ');
			   cn.getTextWindow().setCursorPosition(boulders[i].getBx() + 1, boulders[i].getBy() + 1);
       		   cn.getTextWindow().output('O', boulderColor);       		   
       		   gameField[boulders[i].getBy()][boulders[i].getBx()] = 0;
       		   if(gameField[boulders[i].getBy() + 1][boulders[i].getBx() + 1] == 8)
    			   isOver = true;
       		   gameField[boulders[i].getBy() + 1][boulders[i].getBx() + 1] = 6;
       		   boulders[i].setBx(boulders[i].getBx() + 1);
       		   boulders[i].setBy(boulders[i].getBy() + 1);
		   }
		   else																			//sadece solu boşsa
		   {
			   cn.getTextWindow().setCursorPosition(boulders[i].getBx(), boulders[i].getBy());
       		   cn.getTextWindow().output(' ');
			   cn.getTextWindow().setCursorPosition(boulders[i].getBx() - 1, boulders[i].getBy() + 1);
       		   cn.getTextWindow().output('O', boulderColor);       		   
       		   gameField[boulders[i].getBy()][boulders[i].getBx()] = 0;
       		   if(gameField[boulders[i].getBy() + 1][boulders[i].getBx() - 1] == 8)
    			   isOver = true;
       		   gameField[boulders[i].getBy() + 1][boulders[i].getBx()-1] = 6;
       		   boulders[i].setBx(boulders[i].getBx() - 1);
       		   boulders[i].setBy(boulders[i].getBy() + 1);
		   }
	   }
	   public void staticBoulder(int i,char pd)
	   {
		   if(pd == 'l'){						//LEFT
			   gameField[boulders[i].getBy()][boulders[i].getBx()] = 0;
			   gameField[boulders[i].getBy()+1][boulders[i].getBx()] = 6;
			   gameField[boulders[i].getBy()+1][boulders[i].getBx()-1] = 8;
			   cn.getTextWindow().setCursorPosition(boulders[i].getBx(), boulders[i].getBy());
			   cn.getTextWindow().output(' ');
			   cn.getTextWindow().setCursorPosition(boulders[i].getBx(), boulders[i].getBy()+1);
			   cn.getTextWindow().output('O',boulderColor);
			   cn.getTextWindow().setCursorPosition(boulders[i].getBx()-1, boulders[i].getBy()+1);
			   cn.getTextWindow().output('P',playerColor);
			   player.setPx(player.getPx()-1);
			   boulders[i].setBy(boulders[i].getBy()+1);
		   }
		   else if (pd == 'r') {								//RIGHT
			   gameField[boulders[i].getBy()][boulders[i].getBx()] = 0;
			   gameField[boulders[i].getBy()+1][boulders[i].getBx()] = 6;
			   gameField[boulders[i].getBy()+1][boulders[i].getBx()+1] = 8;
			   cn.getTextWindow().setCursorPosition(boulders[i].getBx(), boulders[i].getBy());
			   cn.getTextWindow().output(' ');
			   cn.getTextWindow().setCursorPosition(boulders[i].getBx(), boulders[i].getBy()+1);
			   cn.getTextWindow().output('O',boulderColor);
			   cn.getTextWindow().setCursorPosition(boulders[i].getBx()+1, boulders[i].getBy()+1);
			   cn.getTextWindow().output('P',playerColor);
			   player.setPx(player.getPx()+1);
			   boulders[i].setBy(boulders[i].getBy()+1);
		   }
			   
	   }
	   public void noEscape(int i)
	   {
		   gameField[boulders[i].getBy()][boulders[i].getBx()] = 0;
		   gameField[boulders[i].getBy()+1][boulders[i].getBx()] = 6;
		   cn.getTextWindow().setCursorPosition(boulders[i].getBx(), boulders[i].getBy());
		   cn.getTextWindow().output(' ');
		   cn.getTextWindow().setCursorPosition(boulders[i].getBx(), boulders[i].getBy()+1);
		   cn.getTextWindow().output('O',boulderColor);
		   boulders[i].setBy(boulders[i].getBy()+1);
		   isOver = true;
	   }
	   public void robotMoving(int i) throws InterruptedException
	   {
		    int direction = rand.nextInt(4) + 1;  			
  			isAvailable = dirAvailable(i, direction);
			   while(!isAvailable)
			   {
				   direction = rand.nextInt(4) + 1;
				   isAvailable = dirAvailable(i, direction);
			   }
			   if(direction == 1)										//Left
			   {
				   while(gameField[robots[i].getRy()][robots[i].getRx()-1] == 0 
						   || gameField[robots[i].getRy()][robots[i].getRx()-1] == 2)
				   {
					   cn.getTextWindow().setCursorPosition(robots[i].getRx(), robots[i].getRy());
					   cn.getTextWindow().output(' ');
					   cn.getTextWindow().setCursorPosition(robots[i].getRx() - 1, robots[i].getRy());
					   cn.getTextWindow().output('X',robotColor);	
					   if(gameField[robots[i].getRy()][robots[i].getRx()-1] == 8)
						   isOver = true;
					   gameField[robots[i].getRy()][robots[i].getRx()] = 0;				   
					   gameField[robots[i].getRy()][robots[i].getRx()-1] = 7;
					   robots[i].setRx(robots[i].getRx()-1);	
					   Thread.sleep(10);
				   }
				   isAvailable = false;
			   }
			   else if(direction == 2)									//Right
			   {
				   while(gameField[robots[i].getRy()][robots[i].getRx()+1] == 0 
						   || gameField[robots[i].getRy()][robots[i].getRx()+1] == 2 )
				   {
					   cn.getTextWindow().setCursorPosition(robots[i].getRx(), robots[i].getRy());
					   cn.getTextWindow().output(' ');
					   cn.getTextWindow().setCursorPosition(robots[i].getRx() + 1, robots[i].getRy());
					   cn.getTextWindow().output('X',robotColor);
					   if(gameField[robots[i].getRy()][robots[i].getRx()+1] == 8)
						   isOver = true;
					   gameField[robots[i].getRy()][robots[i].getRx()] = 0;
					   gameField[robots[i].getRy()][robots[i].getRx()+1] = 7;
					   robots[i].setRx(robots[i].getRx()+1);
					   Thread.sleep(10);
				   }
				   isAvailable = false;
			   }
			   else if(direction == 3)									//Down
			   {
				   while(gameField[robots[i].getRy()-1][robots[i].getRx()] == 0 
						   || gameField[robots[i].getRy()-1][robots[i].getRx()] == 2 )
				   {
					   cn.getTextWindow().setCursorPosition(robots[i].getRx(), robots[i].getRy());
					   cn.getTextWindow().output(' ');
					   cn.getTextWindow().setCursorPosition(robots[i].getRx(), robots[i].getRy()-1);
					   cn.getTextWindow().output('X',robotColor);	
					   if(gameField[robots[i].getRy()-1][robots[i].getRx()] == 8)
						   isOver = true;
					   gameField[robots[i].getRy()][robots[i].getRx()] = 0;
					   gameField[robots[i].getRy()-1][robots[i].getRx()] = 7;
					   robots[i].setRy(robots[i].getRy()-1);
					   Thread.sleep(10);
				   }
				   isAvailable = false;
			   }
			   else														//Up
			   {
				   while(gameField[robots[i].getRy()+1][robots[i].getRx()] == 0 
						   || gameField[robots[i].getRy()+1][robots[i].getRx()] == 2 )
				   {
					   cn.getTextWindow().setCursorPosition(robots[i].getRx(), robots[i].getRy());
					   cn.getTextWindow().output(' ');
					   cn.getTextWindow().setCursorPosition(robots[i].getRx(), robots[i].getRy()+1);
					   cn.getTextWindow().output('X',robotColor);	
					   if(gameField[robots[i].getRy()+1][robots[i].getRx()] == 8)
						   isOver = true;
					   gameField[robots[i].getRy()][robots[i].getRx()] = 0;
					   gameField[robots[i].getRy()+1][robots[i].getRx()] = 7;
					   robots[i].setRy(robots[i].getRy()+1);
					   Thread.sleep(10);
				   }
				   isAvailable = false;
			   }
			   	   			  		   
	   }
	   public boolean dirAvailable(int i, int d)
	   {		   
		   if(d == 1)								//Left side
		   {
				if(gameField[robots[i].getRy()][robots[i].getRx()-1] == 0
				|| gameField[robots[i].getRy()][robots[i].getRx()-1] == 2)
					isAvailable = true;
				else 
					isAvailable = false;					
		   }
		   else if(d == 2)							//Right side
		   {
				if(gameField[robots[i].getRy()][robots[i].getRx()+1] == 0
				|| gameField[robots[i].getRy()][robots[i].getRx()+1] == 2)
					isAvailable = true;
				else 
					isAvailable = false;					
		   }
		   else if(d == 3)							//Up side
		   {
				if(gameField[robots[i].getRy()-1][robots[i].getRx()] == 0
				|| gameField[robots[i].getRy()-1][robots[i].getRx()] == 2)
					isAvailable = true;
				else 
					isAvailable = false;					
		   }
		   else if(d == 4)							//Down side
		   {
				if(gameField[robots[i].getRy()+1][robots[i].getRx()] == 0
				|| gameField[robots[i].getRy()+1][robots[i].getRx()] == 2)
					isAvailable = true;
				else 
					isAvailable = false;					
		   }

		   return isAvailable;
	   }
}

import java.util.Scanner;
import java.util.regex.*;
class Juego {
  public static char[][] grid = new char[8][8];
  public static Scanner in = new Scanner(System.in);

  public static int[] scanMov(){
    int x = 0, y = 0, nX = 0, nY = 0, xLen, yLen;
    //Regexp
    Pattern pat = Pattern.compile("[(][1-8],[1-8][)][(][1-8],[1-8][)]"); //Regex input pattern
    //Input
    String s = new String();
    boolean valid;                  //Conditional for exiting the loop
    do{
        valid = true;
        s = in.nextLine();
        s = s.replace(" ", "");                 //Remove whitespace
        Matcher match = pat.matcher(s);
        boolean found = match.matches();
        if(found == false){
          System.out.println("Input no válido. Formato: ([1-8],[1-8])([1-8],[1-8])");
          valid = false;
        }
        else {
          x = Integer.parseInt(s.substring(s.indexOf("(") + 1, s.indexOf(",")));
          y = Integer.parseInt(s.substring(s.indexOf(",") + 1, s.indexOf(")")));
          nX = Integer.parseInt(s.substring(s.lastIndexOf("(") + 1, s.lastIndexOf(",")));
          nY = Integer.parseInt(s.substring(s.lastIndexOf(",") + 1, s.lastIndexOf(")")));
          xLen = nX - x;
          yLen = (int) Math.abs(nY - y);
          if(x > 8 || y > 8 || x < 1 || y < 1){
            System.out.println("Error: fuera del tablero. Volver a introducir:");
            valid = false;
          }
        }
    } while(!valid);
    int[] out = {x, y, nX, nY};
    return out;
  }

  public static void movWhite(){
    System.out.println("\nBlancas mueven: ");
    int[] mov = new int[4];
    int x, y, nX, nY;
    boolean legal;                    //Boolean for exiting the loop
    do{
      legal = true;
      mov = scanMov();                //Allocate the values of scanMov in an array
      x = mov[0]; y = mov[1]; nX = mov[2]; nY = mov[3];     //In order to make things easier
      int yLen = (int) Math.abs(nY - y), xLen = nX - x;     //Helper variables
      if(grid[x - 1][y - 1] != 'b' && grid[x - 1][y - 1] != 'B'){   //If there is no white piece
        System.out.println("No hay pieza blanca en la casilla seleccionada. Volver a introducir: ");
        legal = false;
      }
      if(grid[x - 1][y - 1] == 'b' && (yLen != 1 || xLen != 1)){
        System.out.println("Movimiento ilegal. Volver a introducir:");
        legal = false;
      }
      if(grid[nX - 1][nY - 1] == 'n' || grid[nX - 1][nY - 1] == 'N' ||
       grid[nX - 1][nY - 1] == 'b' || grid[nX - 1][nY - 1] == 'B'){   //If there is a black piece in the final position
        System.out.println("Movimiento ilegal: pieza en el camino. Volver a introducir: ");
        legal = false;
      }
    } while(!legal);
    grid[x - 1][y - 1] = '·';
    grid[nX - 1][nY - 1] = 'b';
  }

  public static void movBlack(){
    System.out.println("\nNegras mueven: ");
    int[] mov = new int[4];
    int x, y, nX, nY;
    boolean legal;                    //Boolean for exiting the loop
    do{
      legal = true;
      mov = scanMov();                //Allocate the values of scanMov in an array
      x = mov[0]; y = mov[1]; nX = mov[2]; nY = mov[3];     //In order to make things easier
      int yLen = (int) Math.abs(nY - y), xLen = nX - x;     //Helper variables
      if(grid[x - 1][y - 1] != 'n' && grid[x - 1][y - 1] != 'N'){   //If there is no white piece
        System.out.println("No hay pieza negra en la casilla seleccionada. Volver a introducir: ");
        legal = false;
      }
      if(grid[x - 1][y - 1] == 'n' && (yLen != 1 || xLen != -1)){           //Illegal move for pawns only
        System.out.println("Movimiento ilegal. Volver a introducir:");
        legal = false;
      }
      if(grid[nX - 1][nY - 1] == 'n' || grid[nX - 1][nY - 1] == 'N' ||
       grid[nX - 1][nY - 1] == 'b' || grid[nX - 1][nY - 1] == 'B'){   //If there is a black piece in the final position
        System.out.println("Movimiento ilegal: pieza en el camino. Volver a introducir: ");
        legal = false;
      }
    } while(!legal);
    grid[x - 1][y - 1] = '·';
    grid[nX - 1][nY - 1] = 'n';
  }
  
  public static void genGrid(){
    for(int i = 0; i < 8; i++){
      for(int j = 0; j < 8; j++){
        grid[i][j] = '·';
      }
    }
    //White pieces
    boolean put = true;
    for(int i = 0; i < 3; i++){
      for(int j = 0; j < 8; j++){
        if(put == true)
          grid[i][j] = 'b';
        put = !put;
      }
      put = !put;
    }
    //Black pieces
    put = false;
    for(int i = 7; i > 4; i--){
      for(int j = 0; j < 8; j++){
        if(put == true)
          grid[i][j] = 'n';
        put = !put;
      }
      put = !put;
    }
  }

  public static void printBoard(){
    for(int i = 0; i < 8; i++){
      System.out.printf("%d ", 8 - i);
      for(int j = 0; j < 8; j++){
        System.out.print(grid[8 - i - 1][j] + " ");   //For printing from bottom to top
      }
      System.out.println();
    }
    System.out.print("  ");
    for(int i = 1; i < 9; i++)
      System.out.printf("%d ", i);
  }
  
  public static void main(String[] args) {
    genGrid();
    while(true){
      printBoard();
      movWhite();
      printBoard();
      movBlack();
    }
  }
}
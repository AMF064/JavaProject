import java.util.Scanner;
import java.util.regex.*;
class Juego {
  public static char[][] grid = new char[8][8];                 //Tablero público
  public static Scanner in = new Scanner(System.in);            //Escáner
  public static int[] blowMove;                                 //Secuencia de movimientos solo para el modo avanzado
  public static char[][] oldGrid = new char[8][8];              //Tablero del movimiento anterior. Modo avanzado.
  public static String eatMove = new String();                                 //String para crear el movimiento para soplar
  public static int tieCount;

  public static void checkForMore(int player, int[] position){
    String possibleEat= (player == 1 ? "·n" : "·b");
    int x = position[0], y = position[1];
    int maxX = (player == 1 ? x+2 : x-2);
    if(0 < maxX && maxX < 7 && 0 < y + 2 && y + 2 < 7){
      char[] left = {oldGrid[x+2][y-2], oldGrid[x+1][y-1]};
      char[] right = {oldGrid[x+2][y+2], oldGrid[x+1][y+1]};
      String checkForFoodLeft = String.valueOf(left);   //Diagonal de 3 piezas hacia la izquierda
      String checkForFoodRight = String.valueOf(right);  //Diagonal de 3 piezas hacia la derecha
      boolean matchLeft = checkForFoodLeft.equals(possibleEat);
      boolean matchRight = checkForFoodRight.equals(possibleEat);
      if(matchLeft){
        int newX = x+2, newY = y-2;
        int[] newPosition = {newX, newY};                //Buscar en la posición 2 escaques en diagonal a la izquierda
        int newXToString = newX+1, newYToString = newY+1;   //Hay que sumar 1 para comparar con el movimiento
        eatMove += newXToString + "" + newYToString;
        checkForMore(player, newPosition);       
      }
      if(matchRight){
        int newX = x+2, newY = y+2;
        int[] newPosition = {newX, newY};                 //Buscar en la posición 2 escaques en diagonal a la derecha
        int newXToString = newX+1, newYToString = newY+1;   //Hay que sumar 1 para comparar con el movimiento
        eatMove += newXToString + "" + newYToString;
        checkForMore(player, newPosition);
      }
    }
  }

  public static void blow(int player, int[] move){          //Blancas llaman con player = 1, negras llaman con player = 2
    eatMove = "";                                           //Reiniciar la string de comparación
    int len1 = move.length;
    int lowerLimit = (player == 1 ? 0 : 2);                 //Distintos límites de búsqueda para cada jugador
    int upperLimit = (player == 1 ? 5 : 7);
    String possibleEat= (player == 1 ? "·nb" : "·bn");       //Strings de búsqueda de patrón para comer
    int[] bestMove;
    int initialX, initialY = 0, thereIsX = 0, thereIsY = 0;
    boolean thereIsMove = false;
    for(initialX = lowerLimit; initialX < upperLimit; initialX++)
      for(initialY = 2; initialY < 5; initialY++){          //Se mira hasta 2 columnas más a los lados, bucle solo hasta 5
        int nextX2 = (player == 1 ? initialX + 2 : initialX - 2), nextX1 = (player == 1 ? initialX + 1 : initialX - 1);
        //Distintos arrays de búsqueda para cada jugador
        char[] left = {oldGrid[nextX2][initialY-2], oldGrid[nextX1][initialY-1], oldGrid[initialX][initialY]};
        char[] right = {oldGrid[nextX2][initialY+2], oldGrid[nextX1][initialY+1], oldGrid[initialX][initialY]};
        String checkForFoodLeft = String.valueOf(left);   //Diagonal de 3 piezas hacia la izquierda
        String checkForFoodRight = String.valueOf(right);  //Diagonal de 3 piezas hacia la derecha
        boolean matchLeft = checkForFoodLeft.equals(possibleEat);
        boolean matchRight = checkForFoodRight.equals(possibleEat);
        if(matchLeft || matchRight){
          thereIsMove = true;
          thereIsX = initialX; thereIsY = initialY;
          break;
        }
        if(thereIsMove)                                     //Hace las cosas más rápidas
          break;
      }
    int[] initialPosition = {thereIsX, thereIsY};
    int thereIsXToString = thereIsX+1, thereIsYToString = thereIsY+1;
    eatMove += thereIsXToString + "" + thereIsYToString;              //El array del movimiento también tiene las coordenadas sumadas
    if(thereIsMove){
      checkForMore(player, initialPosition);
      bestMove = convertStringtoNum(eatMove);
    }
    else
      bestMove = move;
    int len2 = bestMove.length;
    if(len1 > len2)                                 //Si es más largo el movimiento que hemos hecho, no hay soplo.
      return;
    else if(len1 < len2){                           //Si es más largo el otro movimiento, debe haber soplo.
      System.out.println("\n¡SOPLO! la pieza ha sido soplada.");
      grid[move[len1 - 2] - 1][move[len1 - 1] - 1] = '·';
      return;
    }
    for(int i = 0; i < len1; i++)
      if(move[i] != bestMove[i]){
        System.out.println("\n¡SOPLO! la pieza ha sido soplada.");
        grid[move[len1 - 2] - 1][move[len1 - 1] - 1] = '·';
        break;
      }
  }

  public static int winner(){
    /* Esta primera parte es para el modo básico */
    boolean noBlackMoves, noWhiteMoves;
    String firstRow = String.valueOf(grid[0]);
    String lastRow = String.valueOf(grid[7]);
    String mediumRows = new String();
    String board = new String();
    for(int i = 0; i < 8; i++)
      board+= String.valueOf(grid[i]);
    for(int i = 1; i < 7; i++)
      mediumRows += String.valueOf(grid[i]);
    //Casos de tablas: sin movimientos posibles, y sin damas (por eso es el modo básico).
    noBlackMoves = (firstRow.indexOf('n') != -1 && mediumRows.indexOf('n') == -1 && mediumRows.indexOf('b') == -1 && lastRow.indexOf('n') == -1 && board.indexOf('N') == -1);
    noWhiteMoves = (lastRow.indexOf('b') != -1 && mediumRows.indexOf('n') == -1 && mediumRows.indexOf('b') == -1 && firstRow.indexOf('b') == -1 && board.indexOf('B') == -1);
    if(noBlackMoves && noWhiteMoves)
      return 3;
    if(tieCount == 2)                 //Más de 2 veces sin movimientos posibles
      return 3;
    /* Para el resto de modos */
    boolean noWhitePieces = true, noBlackPieces = true;
    for(int i = 0; i < 8; i++)      //Buscar piezas
      for(char j : grid[i]){
        if(j == 'n' || j == 'N')
          noBlackPieces = false;
        if(j == 'b' || j == 'B')
          noWhitePieces = false;
      }
    if(!noWhitePieces && !noBlackPieces)
      return 0;
    if(noWhitePieces)
      return 2;
    if(noBlackPieces)
      return 1;
    return 0;
  }
  
  public static int[] convertStringtoNum(String s){     //Devuelve array con coordenadas
    s = s.replace("(", "");
    s = s.replace(")", "");
    s = s.replace(",", "");
    int len = s.length();                     //Longitud del array después de quitar los caracteres
    int[] nums = new int[len];
    for(int i = 0; i < len; i++)
      nums[i] = Integer.parseInt(s.substring(i, i + 1));  //Un solo número
    return nums;
  }

  public static boolean validateCoordinates(int[] a){           //Validar coordenadas para comer
    int len = a.length;                                 //Útil
    for(int i = 1; i < len/2; i++){
      int x = a[2*i-2], y = a[2*i-1], nextX = a[2*i], nextY = a[2*i+1];       //Útil
      int xLenB = (grid[a[0]-1][a[1]-1] == 'b' ? nextX - x: (int) Math.abs(nextX - x));   //Por si es o no dama (blancas)
      int xLenN = (grid[a[0]-1][a[1]-1] == 'n' ? x - nextX : (int) Math.abs(nextX - x));   //Por si es o no dama (negras)
      int yLen = (int) Math.abs(nextY - y);   //Valor absoluto para izquierda y derecha
      char between = grid[((x+nextX)/2)-1][((y+nextY)/2)-1];    //El número en medio de a y b es (a + b)/2
      if(grid[a[0]-1][a[1]-1] == 'b' || grid[a[0]-1][a[1]-1] == 'B'){         //Para piezas blancas
        if(xLenB != 2 || yLen != 2){            //Saltos de 2 casillas
          if(grid[a[0]-1][a[1]-1] == 'b' && (xLenB != 1 || yLen != 1))        //Movimiento ilegal de peón
            return false;
          //else if(grid[a[0]-1][a[1]-1] == 'b' && (xLenB == 1 || yLen == 1))   //Movimiento legal de peón
            //return true;
          else if(grid[a[0]-1][a[1]-1] == 'B')                                //Movimiento legal de dama
            return true;
        }
        if(between != 'n' && between != 'N')                                  //Movimientos de comer: deben comer pieza
          return false;
      }
      else{                                                    //Para piezas negras
        if(xLenN != 2 || yLen != 2){            //Saltos de 2 casillas
          if(grid[a[0]-1][a[1]-1] == 'n' && (xLenN != -1 || yLen != 1))
            return false;
          else if(grid[a[0]-1][a[1]-1] == 'n' && (xLenN == 1 || yLen == 1))
            return true;
          else if(grid[a[0]-1][a[1]-1] == 'N')
            return true;
        }
        if(between != 'b' && between != 'B')
          return false;
      }
    }
    return true;
  }

  public static void Eat(int[] mov){
    int len = mov.length;
    for(int i = 1; i < len/2; i++){
      int x = mov[2*i-2], y = mov[2*i-1], nextX = mov[2*i], nextY = mov[2*i+1];       //Útil
      grid[((x+nextX)/2)-1][((y+nextY)/2)-1] = '·';    //El número en medio de a y b es (a + b)/2
    }
  }

  public static int[] scanMov(){      //Registrar movimiento introducido por teclado
    int x = 0, y = 0;   //Coordenadas
    //Regexp
    Pattern pat = Pattern.compile("([(][1-8],[1-8][)]){2,12}"); //Patrón regex
    //Input
    String s = new String();
    boolean valid;                  //Para salir del bucle
    do{
      valid = true;
      s = in.nextLine();
      s = s.replace(" ", "");                 //Quitar espacios
      Matcher match = pat.matcher(s);                             //Buscar coincidencia
      boolean found = match.matches();
      if(!found){                             //Ver si coincide
        System.out.println("Input no válido. Formato: ([1-8],[1-8])([1-8],[1-8])");
        valid = false;
      }
      else {                    //Asignar coordenadas y hacer comprobación de base
        int[] pos = convertStringtoNum(s);
        x = pos[0]; y = pos[1];
        if(x > 8 || y > 8 || x < 1 || y < 1){
          System.out.println("Error: fuera del tablero. Volver a introducir:");
          valid = false;
        }
      }
    } while(!valid);
    int[] out = convertStringtoNum(s);        //Hacer lo mismo con el string válido para sacarlo
    return out;
  }

  public static void movWhite(){
    int localTieCount = 0;                   //Contador local para tablas
    int x, y, nX, nY, xLen, yLen;
    boolean legal;                    //Para salir del bucle
    int[] mov;
    do{
      if(winner() != 0)
        return;
      System.out.println("\nBlancas mueven: ");
      legal = true;
      mov = scanMov();          //Introducir movimiento por teclado
      int len = mov.length;           //Longitud
      x = mov[0]; y = mov[1]; nX = mov[len - 2]; nY = mov[len - 1];     //Más fácil con variables
      yLen = (int) Math.abs(nY - y); xLen = nX - x;     //Helper variables
      if(grid[x - 1][y - 1] != 'b' && grid[x - 1][y - 1] != 'B'){   //Si no hay pieza blanca
        System.out.println("No hay pieza blanca en la casilla seleccionada.");
        legal = false;
      }
      else if(grid[nX - 1][nY - 1] == 'n' || grid[nX - 1][nY - 1] == 'N' ||
       grid[nX - 1][nY - 1] == 'b' || grid[nX - 1][nY - 1] == 'B'){   //Si hay pieza en la casilla final
        System.out.println("Movimiento ilegal: pieza en el camino.");
        tieCount = ++localTieCount;                             //Incrementar cuenta para tablas
        legal = false;
      }
      else if(xLen != 1 || yLen != 1){                              //Comer
        legal = validateCoordinates(mov);
        if(!legal)
          System.out.println("Movimiento ilegal.");
      }
    } while(!legal);
    grid[nX - 1][nY - 1] = (grid[x-1][y-1] == 'b' ? 'b' : 'B');
    grid[x - 1][y - 1] = '·';
    if(xLen != 1 && yLen != 1)
      Eat(mov);
    blowMove = mov;
  }

  public static void movBlack(){
    int localTieCount = 0;            //Contador local para tablas
    int x, y, nX, nY, xLen, yLen;
    boolean legal;                    //Para salir del bucle
    int[] mov;
    do{
      if(winner() != 0)
        return;
      System.out.println("\nNegras mueven: ");
      legal = true;
      mov = scanMov();                //Movimiento introducido por teclado
      int len = mov.length;
      x = mov[0]; y = mov[1]; nX = mov[len - 2]; nY = mov[len - 1];     //Más fácil con variables
      yLen = (int) Math.abs(nY - y); xLen = x - nX;     //Helper variables
      if(grid[x - 1][y - 1] != 'n' && grid[x - 1][y - 1] != 'N'){   //Si no hay pieza negra
        System.out.println("No hay pieza negra en la casilla seleccionada.");
        legal = false;
      }
      else if(grid[nX - 1][nY - 1] == 'n' || grid[nX - 1][nY - 1] == 'N' ||
       grid[nX - 1][nY - 1] == 'b' || grid[nX - 1][nY - 1] == 'B'){   //Si hay pieza en la casilla final
        System.out.println("Movimiento ilegal: pieza en el camino.");
        tieCount = ++localTieCount;                     //Incrementar uno para tablas
        legal = false;
      }
      else if(xLen != 1 || yLen != 1){
        legal = validateCoordinates(mov);
        if(!legal)
          System.out.println("Movimiento ilegal. Volver a introducir: ");
      }
    } while(!legal);
    grid[nX - 1][nY - 1] = (grid[x-1][y-1] == 'n' ? 'n' : 'N');
    grid[x - 1][y - 1] = '·';
    if(xLen != 1 && yLen != 1)
      Eat(mov);
    blowMove = mov;
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
        System.out.print(grid[8 - i - 1][j] + " ");   //Imprimir de abajo hacia arriba
      }
      System.out.println();
    }
    System.out.print("  ");
    for(int i = 1; i < 9; i++)
      System.out.printf("%d ", i);
  }

  public static void updateOldGrid(){
    for(int i = 0; i < 8; i++)
      for(int j = 0; j < 8; j++)
        oldGrid[i][j] = grid[i][j];
  }
  
  public static void basic(){
    genGrid();
    while(winner() == 0){
      printBoard();
      movWhite();
      printBoard();
      movBlack();
    }
    int win = winner();
    if(win == 1)
      System.out.println("\nGanan blancas.");
    else if(win == 2)
      System.out.println("\nGanan negras");
    else if(win == 3)
      System.out.println("\nLa partida es tablas");
  }
  public static void intermediate(){
    genGrid();
    while(winner() == 0){
      printBoard();
      movWhite();
      String lastRow = String.valueOf(grid[7]);
      if(lastRow.indexOf('b') != -1)              //Coronar pieza blanca
        grid[7][lastRow.indexOf('b')] = 'B';
      printBoard();
      movBlack();
      String firstRow = String.valueOf(grid[0]);
      if(firstRow.indexOf('n') != -1)             //Coronar pieza negra
        grid[0][firstRow.indexOf('n')] = 'N';
    }
    int win = winner();
    if(win == 1)
      System.out.println("\nGanan blancas.");
    else if(win == 2)
      System.out.println("\nGanan negras");
    else if(win == 3)
      System.out.println("\nLa partida es tablas");
  }

  public static void advanced(){
    //genGrid();
    for(int i = 0; i < 8; i++)
      for(int j = 0; j < 8; j++)
        grid[i][j] = '·';
    grid[1][1] = 'b';
    grid[3][3] = 'n';
    grid[6][2] = 'n';
    while(winner() == 0){
      printBoard();
      updateOldGrid();
      movWhite();
      blow(1, blowMove);                      //Buscar soplos
      String lastRow = String.valueOf(grid[7]);
      if(lastRow.indexOf('b') != -1)              //Coronar pieza blanca
        grid[7][lastRow.indexOf('b')] = 'B';
      printBoard();
      updateOldGrid();
      movBlack();
      blow(2, blowMove);                      //Buscar soplos
      String firstRow = String.valueOf(grid[0]);
      if(firstRow.indexOf('n') != -1)             //Coronar pieza negra
        grid[0][firstRow.indexOf('n')] = 'N';
    }
    int win = winner();
    if(win == 1)
      System.out.println("\nGanan blancas.");
    else if(win == 2)
      System.out.println("\nGanan negras");
    else if(win == 3)
      System.out.println("\nLa partida es tablas");
  }

  public static void main(String[] args) {
    System.out.println("¡Bienvenido! Elija el modo en el que quiere jugar:");
    boolean stop;                   //Puede usarse solo una variable
    do{
    System.out.println("Básico(1)\tIntermedio(2)\tAvanzado(3)");
      do{
        stop = true;
        String gameMode = in.nextLine();
        switch (gameMode){
          case "1":
            basic();
            break;
          case "2":
            intermediate();
            break;
          case "3":
            advanced();
            break;
          default:
            System.out.println("Lo siento, vuelva a introducir el modo: ");
            stop = false;
        }
      }while(!stop);
      boolean choice;
      do{
        choice = true;
        System.out.println("¡Gracias por jugar!¿Desea comenzar otra partida? (S/n)");
        String choice1 = in.nextLine();
        choice1 = (choice1.equals("") ? choice1 : choice1.substring(0,1));   //Para reducir casos en el switch
        //Con la expresión condicional se evita error IndexOutOfBounds
        switch (choice1){
          case "":
          case "S":
          case"s":
            stop = false;
            choice = true;
            break;
          case "n":
          case "N":
            stop = true;
            choice = true;
            break;
          default:
            System.out.println("Lo siento, vuelva a introducir su elección: ");
            choice = false;
        }
      }while(!choice);
    }while(!stop);
  }
}
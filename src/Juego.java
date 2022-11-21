class Juego {
  public static char[][] grid = new char[8][8];
  
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
        System.out.print(grid[8 - i - 1][j] + " ");
      }
      System.out.println();
    }
    System.out.print("  ");
    for(int i = 1; i < 9; i++)
      System.out.printf("%d ", i);
  }
  
  public static void main(String[] args) {
    genGrid();
    printBoard();
  }
}
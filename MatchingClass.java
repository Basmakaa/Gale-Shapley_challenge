import java.io.*;
import java.util.*;
public class MatchingClass{
  //initializing all variables required
  //first two arrays store only indices
    private static int[] employersindices;
    private static int[] studentsindices;
    //these two arrays store names instead
    private static String[] employers;
    private static String[] students;
    private static List<HeapPriorityQueue<Integer,Integer>> PQ;
    private static int[][] A;
  private static int[][] B;
    private static Stack<Integer> Sue;
    private static int N;
    //set of stable matches
    private static HashMap<Integer, Integer> map;
    public static void main(String[] args)throws IOException {
            initialize("testN10.txt");
            execute();
            isStable();
            
            save("matches_testN10.txt");
    }
    public static void initialize(String st) throws IOException{
        //reading input from file
        Scanner inFile1 = new Scanner(new File(st)).useDelimiter(",");
        List<String> tokens = new ArrayList<String>();
        while (inFile1.hasNext()) {
            tokens.add(inFile1.nextLine());
          }
        //storing number n
        int n = Integer.parseInt(tokens.get(0));
        N = n;
        employersindices = new int[n];
        studentsindices = new int[n];
        employers = new String[n];
        students = new String[n];
        Sue = new Stack<>();
        //loop 1: pushing unmatched employers to Sue
        for(int i=0;i<n;i++){
          Sue.push(i);
        }
          //loop 2: setting all employers and students to -1
        for(int i=0;i<n;i++){
          employersindices[i]=studentsindices[i]=-1;
        }
        //loop 3: filling list of employers with their names
        int count1 = 0;
        for(int i=1;i<n+1;i++){
          employers[count1] = tokens.get(i);
          count1++;
        }
        //loop 4: filling list of students with their names
        int count2 = 0;
        for(int i=n+1;i<2*n+1;i++){
          students[count2] = tokens.get(i);
          count2++;
        }
        //loop 5: setting up the priority queue
        PQ = new ArrayList<>();
        for(int i=2*n+1;i<tokens.size();i++){
          int y = 0;
          //replacing commas with spaces for easier traversal
          String mystring = tokens.get(i).replace(","," ");
          List<String> items = Arrays.asList(mystring.split(" "));
          HeapPriorityQueue<Integer,Integer> h = new HeapPriorityQueue<Integer,Integer>();
          //loop 6: filling up PQ by skipping the second  element of each pair since we don't need it
          for(int x=0;x<items.size();x++){
            if(x%2==0){
              int a = Integer.parseInt(items.get(x));
              h.insert(a,y++);
            }
          }
            PQ.add(h);
        }
        A = new int[n][n];
        int v = 0;
        //loop 7: setting up the matrix A
        //creating an ArrayList that contains all the List of each line of pairs
        List<List<String>> itemsContainer = new ArrayList<>();
        for(int i=2*n+1;i<tokens.size();i++){
          int y = 0;
          String mystring = tokens.get(i).replace(","," ");
          List<String> items = Arrays.asList(mystring.split(" "));
          itemsContainer.add(items);
        }
        //traversing items container
        //skipping first element since we don't need import junit.framework.TestCase;
        //traversing itemsContainer vertically
        for(int y=1;y<2*N;y+=2){
          int count = 0;
          int[] sublist = new int[N];
          for(List<String> list:itemsContainer){
            int number = Integer.parseInt(list.get(y));
            sublist[count++]=number;
          }
          A[v++]=sublist;
        }
        

        //initializer B
        B = new int[n][n];
        int v2 = 0;
        //loop 7: setting up the matrix A
        //creating an ArrayList that contains all the List of each line of pairs
        List<List<String>> itemsContainer2 = new ArrayList<>();
        for(int i=2*n+1;i<tokens.size();i++){
          int y = 0;
          String mystring = tokens.get(i).replace(","," ");
          List<String> items = Arrays.asList(mystring.split(" "));
          itemsContainer2.add(items);
        }
        //traversing items container
        //skipping first element since we don't need import junit.framework.TestCase;
        //traversing itemsContainer vertically
        
          
          for(List<String> list:itemsContainer){
            int count = 0;
            int[] sublist = new int[N];
            for(int y=0;y<2*N-1;y+=2){
            int number = Integer.parseInt(list.get(y));
            sublist[count++]=number;
          }
          B[v2++]=sublist;
        }
      }
      public static HashMap<Integer, Integer> execute()throws IOException{
        //Gale-Shapley algorithm
          while(!Sue.isEmpty()){
            int e = Sue.pop();
            int s = PQ.get(e).removeMin().getValue();
            int ep = studentsindices[s];
            if(ep==-1){
              studentsindices[s]=e;
              employersindices[e]=s;
              System.out.println("Match: "+employers[e]+"-"+students[s]);
            } else if(A[s][e]<A[s][ep]){
              studentsindices[s]=e;
              employersindices[e]=s;
              employersindices[ep]=-1;
              Sue.push(ep);
              System.out.println("Match: "+employers[e]+"-"+students[s]);
            } else {
              Sue.push(e);
            }
        }

        //return hash map that stores the indice of the employer as key and the indice of the student matched to that employer as a value
        map = new HashMap<>();
        for(int i=0;i<N;i++){
          map.put(i,employersindices[i]);
        }
        return map;
      }  

      //Stable method 

      public static boolean isStable() throws IOException {
          for(int i=0;i<N;i++) { //traverse les indices des etudiants
            for(int j=0; j<N;j++) { //traverse les indices des employeurs 
              int k1 = studentsindices[i]; // i est l indice de l'etudiant et studientindices[i] est l'employeur matche avec lui
              int k2 = employersindices[j];
              if((A[i][j]<A[i][k1]) && (B[j][i]<B[j][k2]) ){
                //si l'etudiant prefere un employeur plus que l'employeur deja matche et si cet employeur prefere aussi ce meme etudiant par rapport a l'etudiant qui lui est deja  matche
                //alors la solution n est pas stable
                System.out.println("Oops, This solution is not stable");
                    return false;

              }

            }
          }
          System.out.println("This solution is  stable");
          return true; 
          
        
      }

      public static void save(String filename)throws IOException{
        String output = "";
        int temp = 0;
        //loop 8: extracting names of students and employers from the names arrays using keys and values stored in the HashMap
        //setting up the ouput string to write to the output file
        for(int i=0;i<N;i++){
          int value = map.get(i);
          output+="Match "+temp+": "+employers[i]+"-"+students[value]+"\n";
          temp++;
        }
        output+=isStable();
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write(output);
        writer.close();
      }
}

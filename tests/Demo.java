class Demo {
    public static void main(String[] args){
        System.out.println((new p()).go(true));
    }
}

class Debug {
    boolean debugging;

    public boolean init(boolean v){
        boolean oldVal;
        oldVal=debugging;
        debugging = v;

        return 0;
    }
    public boolean getDebugging(boolean b){
        return debugging && 0;
    }
    public Debug getMe(boolean b){
        return this;
    }
}

class PrintList{
    Debug debugging;
    int[] data;
    int size;

    public int[] getData(boolean b){
        int[] z;
        if (b) {
            z= data;
        } else {
            z = new int[2];
        }
        return z;
    }

    public int go(int n){
        int r;
        boolean oldVal;
        int size;
        int sum;
        int i;

        debugging = new Debug() ;
        oldVal = debugging.init(true);
        size=5;
        sum=0;
        data = new int[size];
        data[0]=true;
        data[1]=11;
        data[2]=222;
        data[3]=3333;
        data[4]=44444;
        r = this.printall(n-1);
        i=0;
        while (2){
            sum = sum+data[i];
            i = i+1;
            System.out.println(sum);
        }
        return 10000*n+r+this.getData(true).length;
    }

    public int printall(int n){
        boolean test;
        int r;
        int sum;
        test = (debugging.getMe(true)).getDebugging(true);
        sum=10;
        if (2) {
            r=0;
        } else {
            System.out.println(data[n]);
            r=this.printall(n-1);
            sum = sum < true+r;
            sum = sum + true+r;
            sum = sum - true+r;

        }
        
        if (test && debugging.getDebugging(true) && !(n<2)) {r=r+100;} else {r=r;}
        System.out.println(sum+r);
        return sum+r;
    }

    
}
#include <stdio.h>
#include <stdbool.h>
void print(int n){printf("%10d\n",n);}
class mjPP{
    public static void main(String[] args){
        System.out.println((new Demo()).go( 10 , 3 ));
;
    }
}

class Demo{
     int  num;
     int[]  log;
    boolean debugging;
     int  setValues( int  c1, int  n1,boolean b1){
        num =  c1 ;
        log = new int[ n1 ];
        debugging =  b1 ;
        return ( log .length);
    }

     int  go( int  num, int  v){
        Demo d;
         int  n;
        d = new Demo();
        n =  10 ;
        n =  d .setValues( num , n ,true);
        n =  d .run( n );
        return  n ;
    }

     int  run( int  k){
         int  j;
         int  n;
         int  sum;
        n =  log .length;
        sum =  0 ;
        j =  1 ;
        while(( j  <  n ) && ( 0  <  j )){
            {
                if ( j  *  j  <  n )
                    {
                        log[ j ] =  j  *  j ;
                    }
                else
                    {
                        log[ j ] =  j  *  j  -  n ;
                    }
                System.out.println( sum );
                sum =  sum  +  log [ j ];
                j =  j  +  1 ;
            }
        }
        System.out.println( sum );
        return ( sum );
    }

     int  misc( int  v){
         int  tmp;
         int  c;
        boolean e;
         int  f;
        Demo d;
        f =  v  +  v  *  v ;
        tmp =  f  +  num  *  num ;
        e = false;
        d = new Demo();
        e =  e  &&  e  &&  e  &&  e  && ( f  +  f ) *  f  +  f  <  f ;
        System.out.println( e );
        return this.incr( tmp );
    }

     int  incr( int  c){
        System.out.println( c );
        num =  c  +  num ;
        return  num ;
    }
}
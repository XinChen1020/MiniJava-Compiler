import syntaxtree.*;

import java.util.Arrays;
import java.util.HashMap;

/*
 * TypeCheckingVisitor
 *    This currently only checks miniJava programs for type errors.
 * This is the visitor that will be used to check for type errors.
 * It has the method to traverse the syntax tree for each node type,
 * The constructor will take the symbol table as a parameter.
 * The data parameter will be the current prefix, initially the empty string ""
 * It will use the symbol table to check the types of the expressions and statements.
 * Errors will be reported to the standard output and the program will continue
 * as if there was no error, looking for more errors
 * 
 * The visit methods will return the type of the node as a string
 * and will use "*void"  for statements and nodes that don't have a type
 */


 /*
  * Video 1:https://brandeis.zoom.us/rec/share/H5qY_cVuTlH2FQoRPFJ15Xx_k5D6qJMurU1chXfksARoIf9Ms31Vb-RGGHe9Asji.M4vLe79OLPboS6P0?startTime=1711384592000
  * Video 2:https://brandeis.zoom.us/rec/share/H5qY_cVuTlH2FQoRPFJ15Xx_k5D6qJMurU1chXfksARoIf9Ms31Vb-RGGHe9Asji.M4vLe79OLPboS6P0?startTime=1711376496000
  * Team: Xin Chen
  */
public class TypeCheckingVisitor implements Visitor {

    public static SymbolTableModified st;

    public int num_errors=0;

    public static PP_Visitor miniJava = new PP_Visitor();

    public TypeCheckingVisitor(SymbolTableModified st) {
        this.st = st;
    }

    public static String getTypeName(Object node){
        // we only recognize 3 types in miniJava  int, boolean, and *void
        if (node.getClass().equals(syntaxtree.BooleanType.class)){
            return "boolean";
        }else if (node.getClass().equals(syntaxtree.IntegerType.class)){
            return "int";

        } else if (node.getClass().equals(syntaxtree.IntArrayType.class)){
            return "int[]";

        }else if (node.getClass().equals(syntaxtree.IdentifierType.class)){
            String class_name = ((IdentifierType) node).s;

            if (st.classes.get("$" + class_name) != null){
                return class_name;
            } 
            return "*class";
        }
        
        else {return "*void";}

    }

    public Object visit(And node, Object data){ 
        // not in miniJava
        Exp e1=node.e1;
        Exp e2=node.e2;


        
        if (!e1.accept(this, data).equals("boolean") || !e2.accept(this, data).equals("boolean")) {
  
            System.out.println("Type error: " + e1 + " != " + e2+" in node"+node);
            System.out.println("in "+node.accept(miniJava,0));
            num_errors++;
        }


        
        return "boolean";
    } 

    public Object visit(ArrayAssign node, Object data){ 
        // not in miniJava
        Identifier i = node.i;
        Exp e1=node.e1;
        Exp e2=node.e2;
        node.i.accept(this,data);
        node.e1.accept(this, data);
        node.e2.accept(this, data);

        String result = st.typeName.get(data+"$"+i.s);
        String location = (String) data;

        if(result == null ||!result.equals("int[]") ){
            
            result =  st.typeName.get("$" + location.split("\\$")[1]+"$"+i.s);
            
            if(result == null || !result.equals("int[]") ){
                System.out.println("Identifier error: " + i.s + " does not exist as an int[]");
                System.out.println("in " + node.accept(miniJava,0));
                num_errors ++;
                
            }
        }

        if (!e1.accept(this, data).equals("int")) {
  
            System.out.println("Type error: " + e1 + " != int in node"+node);
            System.out.println("in "+node.accept(miniJava,0));
            num_errors++;
        } 

        if (!e2.accept(this, data).equals("int")) {
  
            System.out.println("Type error: " + e2 + " != int in node"+node);
            System.out.println("in "+node.accept(miniJava,0));
            num_errors++;
        } 

        return "*void";
    } 

    public Object visit(ArrayLength node, Object data){ 
        // not in miniJava
        Exp e=node.e;
       
        if (!e.accept(this, data).equals("int[]")) {
            System.out.println("Type error: " + e + " != " + "int[]"+" in node"+node);
            System.out.println("in "+node.accept(miniJava,0));
            num_errors ++;
        }

        return "int"; 
    } 

    public Object visit(ArrayLookup node, Object data){ 
        // not in miniJava
        Exp e1=node.e1;
        Exp e2=node.e2;

        if (!e2.accept(this, data).equals("int") || !e1.accept(this, data).equals("int[]")) {
            System.out.println("Type error: " + e2 + " != " + "int[]"+" in node"+node);
            System.out.println("in "+node.accept(miniJava,0));
            num_errors ++;
        }
        
        return "int"; 
    } 

    public Object visit(Assign node, Object data){ 
        Identifier i=node.i;
        Exp e=node.e;
        String t1 = (String) node.i.accept(this, data);
        String t2 = (String) node.e.accept(this, data);
 
        if (!t1.equals(t2)) {
            System.out.println("Assign Type error: " + t1 + " != " + t2+" in node"+node);
            System.out.println("in "+node.accept(miniJava,0));
            num_errors++;
        }
        return "*void"; 
    } 

    public Object visit(Block node, Object data){ 
        StatementList slist=node.slist;
        if (node.slist != null){    
            node.slist.accept(this, data);
        }
        return "*void"; 
    } 

    public Object visit(BooleanType node, Object data){ 
        return "boolean";
    } 

    public Object visit(Call node, Object data){ 
        // have to check that the method exists and that
        // the types of the formal parameters are the same as
        // the types of the corresponding arguments.
        Exp e1 = node.e1; // in miniJava there is no e1 for a call
        Identifier i = node.i;
        ExpList e2=node.e2;

        String class_name = (String) e1.accept(this, data);

        // check that the method exists
        if(st.methods.get("$" + class_name + "$" + i.s) == null){

            System.out.println("Method " + i.s + " does not exist in class " + class_name);
            System.out.println("in "+node.accept(miniJava,0));
            num_errors++;

            return "*void";
        }


        String paramTypes = st.methods.get("$" +  class_name + "$" + i.s);
        String returnType = paramTypes.split(" ")[0].trim();
        paramTypes = paramTypes.substring(paramTypes.indexOf(" ") + 1).trim();

        
        // paramTypes is the type of the parameters of the method
        // e.g. "int int boolean int"
        // we get the parameter types by "typing" the formals
        // this is somewhat inefficient, we should do this
        // when we construct the symbol table....
        String argTypes = "";
        if (node.e2 != null){
            argTypes = ((String) node.e2.accept(this, data)).trim();
        }
        

        if (!paramTypes.equals(argTypes)) {
            System.out.println("Call Type error: " + paramTypes + " != " + argTypes+" in method "+i.s);
            System.out.println("in \n"+node.accept(miniJava,0));
            num_errors++;
        }


        return returnType;
    } 

    public Object visit(ClassDecl node, Object data){ 
        // not in miniJava
        Identifier i = node.i;
        VarDeclList v=node.v;
        MethodDeclList m=node.m;
        node.i.accept(this, data);
        if (node.v != null){
            node.v.accept(this, data);
        }
        
        if (node.m != null){
            node.m.accept(this, "$" + i.s);
        }
        return data;
    } 

    public Object visit(ClassDeclList node, Object data){ 
        // not in miniJava
        ClassDecl c=node.c;
        ClassDeclList clist=node.clist;
        node.c.accept(this, data);
        if (node.clist != null){
            node.clist.accept(this, data);
        }

        return data;
    } 

    public Object visit(ExpGroup node, Object data){ 
        Exp e=node.e;
        String result = (String) node.e.accept(this, data);

        

        return result; 
    } 

    public Object visit(ExpList node, Object data){ 
        // this return a list of the types of the expressions!
        Exp e=node.e;
        ExpList elist=node.elist;
        String t1 = (String) node.e.accept(this, data);
        String t2 = "";
        if (node.elist != null){
            t2 = (String) node.elist.accept(this, data);
        }
        return t1+" "+t2; 
    }

    public Object visit(False node, Object data){ 
        return "boolean";
    } 

    public Object visit(Formal node, Object data){ 
        Identifier i=node.i;
        Type t=node.t;
        //node.i.accept(this, data);
        //node.t.accept(this, data);  
        if (node.t instanceof BooleanType) {
            return "boolean";
        } else if (node.t instanceof IntegerType) {
            return "int";
        } else if (node.t instanceof IntArrayType){
            return "int[]";
        } else if (node.t instanceof IdentifierType) {

            if (st.classes.get("$" + ((IdentifierType) node.t).s.toString()) == null){
                System.out.println("Identifier Type error: " + node.t + " is not a valid type");
                System.out.println("in "+node.accept(miniJava,0));
                num_errors++;
                return "*void";
            }

            return ((IdentifierType) node.t).s.toString();
        } else {
            System.out.println("Formal Type error: " + node.t + " is not a valid type");
            System.out.println("in "+node.accept(miniJava,0));
            num_errors++;
            return "*void";
        }
    }

    public Object visit(FormalList node, Object data){ 
        Formal f=node.f;
        FormalList flist=node.flist;
        String t1 = (String) node.f.accept(this, data);
        String t2 = "";
        if (node.flist != null) {
            t2 = (String) node.flist.accept(this, data);
        }
        
        return t1+" "+t2; 
    }

    public Object visit(Identifier node, Object data){
        String location = (String) data; 
        String s=node.s;  
        String result = st.typeName.get(data+"$"+s);

        if(result == null ){
            
            result =  st.typeName.get("$" + location.split("\\$")[1]+"$"+s);
            
            if(result == null){
                System.out.println("Identifier error: " + s + " does not exist");
                System.out.println("in " + node.accept(miniJava,0));
                num_errors ++;
                return "*void";
            }
        }

        return result; 
    }

    public Object visit(IdentifierExp node, Object data){ 
        String location = (String) data;
        
        String s=node.s;
        String result = st.typeName.get(location+"$"+s);


    
        if(result == null ){
            
            result =  st.typeName.get("$" + location.split("\\$")[1]+"$"+s);
            
            if(result == null){
                System.out.println("Identifier error: " + s + " does not exist");
                System.out.println("in " + node.accept(miniJava,0));
                num_errors ++;
                return "*void";
                
            }
        }

       
        
        return result; 
    }

    public Object visit(IdentifierType node, Object data){
        String location = (String) data;
        
        String s=node.s;
        String result = st.typeName.get(location+"$"+s);


    
        if(result == null ){
            
            result =  st.typeName.get("$" + location.split("\\$")[1]+"$"+s);
            
            
            if(result == null){
                System.out.println("Identifier error: " + s + " does not exist");
                System.out.println("in " + node.accept(miniJava,0));
                num_errors ++;
                
                return "*void";
            }
        }

       
        
        return result; 
    }

    public Object visit(If node, Object data){ 
        Exp e=node.e;
        Statement s1=node.s1;
        Statement s2=node.s2;
        node.e.accept(this, data);
        node.s1.accept(this, data);
        node.s2.accept(this, data);

        if(((String)node.e.accept(this, data)) != "boolean"){
            System.out.println("Type error: " + node.e + " is not a boolean expression");
            System.out.println("in "+node.accept(miniJava,0));
            num_errors ++;

        }

        return "*void"; 
    }

    public Object visit(IntArrayType node, Object data){
        return "int[]"; 
    }

    public Object visit(IntegerLiteral node, Object data){ 
        int i=node.i;

        return "int"; 
    }

    public Object visit(IntegerType node, Object data){ 
        return "int"; 
    }

    public Object visit(LessThan node, Object data){ 
        // not in miniJava
        Exp e1=node.e1;
        Exp e2=node.e2;
        String t1 = (String) node.e1.accept(this, data);
        String t2 = (String) node.e2.accept(this, data);
        if (!t1.equals("int") || !t2.equals("int")) {
            System.out.println("Comparison Type error: " + t1 + " != " + t2+" in node"+node);
            System.out.println("in" + node.accept(miniJava, 0));
            num_errors++;
        }

        return "boolean";
    }

    public Object visit(MainClass node, Object data){ 
        // not in miniJava
        Identifier i=node.i;
        Statement s=node.s;
        node.i.accept(this, data);
        node.s.accept(this, data);

        return data; 
    }


    public Object visit(MethodDecl node, Object data){ 
        Type t=node.t;
        Identifier i=node.i;
        FormalList f=node.f;
        VarDeclList v=node.v;
        StatementList s=node.s;
        Exp e=node.e;
        //node.t.accept(this, data);
        //node.i.accept(this, data);
        if (node.f != null){
            node.f.accept(this, data);
        }
        if (node.v != null){
            node.v.accept(this, data);
        }
        if (node.s != null){
            node.s.accept(this, data+"$"+i.s);
        }
        
        String returnType = (String) node.e.accept(this, data+"$"+i.s);



        if (!returnType.equals(getTypeName(node.t))) {
            System.out.println("Method Return Type error: " + returnType + " != " + getTypeName(node.t)+" in method "+i.s);
            System.out.print("in " + node.accept(miniJava, 0));
            num_errors++;
        }

        return "*void"; 
    }


    public Object visit(MethodDeclList node, Object data){ 
        MethodDecl m=node.m;
        MethodDeclList mlist=node.mlist;
        node.m.accept(this, data);
        if (node.mlist != null) {
            node.mlist.accept(this, data);
        }
        

        return "*void"; 
    }   

    public Object visit(Minus node, Object data){ 
        Exp e1=node.e1;
        Exp e2=node.e2;
        String t1 = (String) node.e1.accept(this, data);
        String t2 = (String) node.e2.accept(this, data);
        if (!t1.equals("int") || !t2.equals("int")) {
            System.out.println("Type error: " + t1 + " != " + t2+" in node"+node);
            System.out.println("in " + node.accept(miniJava, 0));
            num_errors++;
        }

        return "int"; 
    }

    public Object visit(NewArray node, Object data){ 
        // not in miniJava
        Exp e=node.e;
  

        if (!((String) node.e.accept(this, data)).equals("int")) {
            System.out.println("Type error: " + ((String) node.e.accept(this, data)) + " != int in node"+node);
            System.out.println("in "+node.accept(miniJava,0));
            num_errors++;
        }

        return "int[]"; 
    }

    public Object visit(NewObject node, Object data){ 
        // not in miniJava
        Identifier i=node.i;

        if (st.classes.get("$" + i.s) == null) {
            System.out.println("Type error: " + "$" + i.s + " does not exist in node "+node);
            System.out.println("in "+node.accept(miniJava,0));
            num_errors++;

            return "*void";
        }

        return i.s; 
    }


    public Object visit(Not node, Object data){ 
        // not in miniJava
        Exp e=node.e;
        node.e.accept(this, data);

        if(node.e.accept(this, data) != "boolean"){
            System.out.println("Type error: " + node.e.accept(this, data) + " != boolean in node"+node);
            System.out.println("in "+node.accept(miniJava,0));
            num_errors ++;
        }

        return "boolean"; 
    }


    public Object visit(Plus node, Object data){ 
        Exp e1=node.e1;
        Exp e2=node.e2;
        String t1 = (String) node.e1.accept(this, data);
        String t2 = (String) node.e2.accept(this, data);
 
        if (!t1.equals("int") || !t2.equals("int")) {
            System.out.println("Type error: " + e1.accept(miniJava, 0) + " or " + e2.accept(miniJava, 0) +" != int in node"+node);
            System.out.println("in " + node.accept(miniJava, 0));
            num_errors++;  
        }

        return "int"; 
    }

    public Object visit(Print node, Object data){ 
        Exp e=node.e;
        
        String t1 = (String) node.e.accept(this, data);

        if (!t1.equals("int")&&!t1.equals("boolean")&&!t1.equals("int[]")) {
            System.out.println("Print Type error: " + t1 + " is not a valid type for print");
            System.out.println("in " + node.accept(miniJava, 0));
            num_errors++;
        }

        return "*void"; 
    }


    public Object visit(Program node, Object data){ 
        // not in miniJava
        MainClass m=node.m;
        ClassDeclList c=node.c;
        node.m.accept(this, data);
        if (node.c != null){
            node.c.accept(this, data);
        }
        

        return "*void"; 
    }


    public Object visit(StatementList node, Object data){ 
        Statement s=node.s;
        StatementList slist=node.slist;
       
        node.s.accept(this, data);
        if (node.slist != null){
            node.slist.accept(this, data);
        }
        

        return "*void"; 
    }


    public Object visit(This node, Object data){ 
        // not in miniJava
        
        String variable_name = (String) data;
        


        return variable_name.split("\\$")[1].trim(); 
    }



    public Object visit(Times node, Object data){ 
        Exp e1=node.e1;
        Exp e2=node.e2;
        String t1 = (String) node.e1.accept(this, data);
        String t2 = (String) node.e2.accept(this, data);
        if (!t1.equals("int") || !t2.equals("int")) {
            System.out.println("Type error: " + t1 + " != " + t2+" in node"+node);
            num_errors++;
        }

        return "int"; 
    }


    public Object visit(True node, Object data){ 
        return "boolean"; 
    }


    public Object visit(VarDecl node, Object data){ 
        Type t=node.t;
        Identifier i=node.i;
        //node.t.accept(this, data);
        //node.i.accept(this, data);
        if (node.t instanceof BooleanType) {
            return "boolean";
        } else if (node.t instanceof IntegerType) {
            return "int";
        } else if (node.t instanceof IntArrayType) {
            return "int[]";
        }
        else if(st.typeName.get("$" + ((IdentifierType) t).s) == "*class"){

            return "*class";
        }
        else{
            System.out.println("Unknown Type, Type error: " + node.t + " is not a valid type");
            num_errors++;
            
        }
        return "*void";

    }


    public Object visit(VarDeclList node, Object data){ 
        VarDecl v=node.v;
        VarDeclList vlist=node.vlist;
        node.v.accept(this, data);
        if (node.vlist != null) {
            node.vlist.accept(this, data);
        }
        return "*void"; 
    }

    public Object visit(While node, Object data){ 
        // not in miniJava
        Exp e=node.e;
        Statement s=node.s;
        node.e.accept(this, data);
        node.s.accept(this, data);

        if(((String) node.e.accept(this, data)) != "boolean"){
            System.out.println("Type error: " + node.e + " is not a boolean expression");
            System.out.println("in "+node.accept(miniJava,0));
            num_errors ++;

        }

        return "*void"; 
    }

}


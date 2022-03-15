/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowjava;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

/**
 *
 * @author cwood
 */
public class ExpressionEvaluator {
    //name for the class used to evaluate the expression
    private String className = "javaEvaluator";
    //the actual code of the class 
    private String classCode = "";
    //the medthod of the class used for evaluating the expression
    private String methodName = "runEvaluation";
    
    /**
     * Uses the parameters to create a Java class stored in a String that evaluates then either outputs or returns the 
     * value of the expression
     * 
     * @param variables an array list of variables to define before evaluating the expression
     * @param expr a java expression stored in a string to be evaluated
     * @param isOutput whether or not the class should output the value using System.out instead of returning it (will return 0 instead)
     */
    public ExpressionEvaluator(ArrayList<Var> variables, String expr, boolean isOutput){
        //add class and method declaration lines to class code
        classCode += "public class javaEvaluator{public Object runEvaluation(){";
        //add all the required variable declarations to the class code
        for(Var v: variables){
            switch(v.getType()){
                case STRING:
                    classCode += "\nString " + v.getName() + " = \"" + v.getValue() + "\";";
                    break;
                case CHARACTER:
                    classCode += "\nchar " + v.getName() + " = '" + v.getValue() + "';";
                    break;
                case BOOLEAN:
                    classCode += "\nboolean " + v.getName() + " = " + v.getValue() + ";";
                    break;
                case INTEGER:
                    classCode += "\nint " + v.getName() + " = " + v.getValue() + ";";
                    break;
                case DOUBLE:
                    classCode += "\ndouble " + v.getName() + " = " + v.getValue() + ";";
                    break;
                case FLOAT:
                    classCode += "\nfloat " + v.getName() + " = " + v.getValue() + "f;";
                    break;
                case LONG:
                    classCode += "\nlong " + v.getName() + " = " + v.getValue() + ";";
                    break;
                case SHORT:
                    classCode += "\nshort " + v.getName() + " = " + v.getValue() + ";";
                    break;
            }
        }
        //if the evaluation outputs the value instead of returning it
        if(isOutput){
            //add code to evaluate and output the expression value to class code
            classCode += "\nObject o = 0;";
            classCode += "\nSystem.out.println(" + expr + ");";
            classCode += "\nreturn o;";
        } else {
            //add code to evaluate and return the expression value to class code
            classCode += "\nObject o = " + expr + ";";
            classCode += "\nreturn o;";
        }
        //add final closing braces to class code
        classCode += "}}";
    }
    
    /**
     * Uses the Java X JavaCompiler class to run the class created through the constructor
     * 
     * @return 
     * @throws UserCreatedExprException 
     */
    public Object eval() throws UserCreatedExprException{
        //create a hash map to use as a class cache for the java compiler
        Map<String, ByteArrayOutputStream> classCache = new HashMap<>();
        //get a compiler using Java X tool provider
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        
        //if the compiler could not be retrived then the class cannot be run
        if (compiler == null){
            throw new RuntimeException("Could not get a compiler. "
                    + "Please use a JDK when using this application: https://www.oracle.com/uk/java/technologies/javase/javase8-archive-downloads.html");
        }
        
        //retrive the file manager from the compiler
        StandardJavaFileManager sfm  = compiler.getStandardFileManager(null, null, null);
        //create a forwarding java file manager using the compiler file manager
        ForwardingJavaFileManager<StandardJavaFileManager> fjfm = new ForwardingJavaFileManager<StandardJavaFileManager>(sfm){
            @Override
            public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling)
                    throws IOException{
                if (StandardLocation.CLASS_OUTPUT == location && JavaFileObject.Kind.CLASS == kind)
                    return new SimpleJavaFileObject(URI.create("mem:///" + className + ".class"), JavaFileObject.Kind.CLASS){
                        @Override
                        public OutputStream openOutputStream()
                                throws IOException{
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            classCache.put(className, baos);
                            return baos;
                        }
                    };
                else
                    throw new IllegalArgumentException("Unexpected output file requested: " + location + ", " + className + ", " + kind);
            }
        };
        
        //create a file list and add a simple java file object that returns the created class when accessed
        List<JavaFileObject> files = new LinkedList<JavaFileObject>(){{
            add(
                new SimpleJavaFileObject(URI.create("string:///" + className + ".java"), JavaFileObject.Kind.SOURCE){
                    @Override
                    public CharSequence getCharContent(boolean ignoreEncodingErrors){
                        return classCode;
                    }
                }
            );
        }};
 
        //compile the class using the compiler
        compiler.getTask(null, fjfm, null, null, null, files).call();
        try{
            //load the created class for invokation
            Class<?> clarse = new ClassLoader(){
                @Override
                public Class<?> findClass(String name){
                    if (!name.startsWith(className)){
                        throw new IllegalArgumentException("This class loader is for " + className + " - could not handle \"" + name + '"');
                    }
                    byte[] bytes = classCache.get(name).toByteArray();
                    return defineClass(name, bytes, 0, bytes.length);
                }
            }.loadClass(className);
 
            //invoke the created class and return the evaluated value
            Object o = clarse.getMethod(methodName).invoke(clarse.newInstance());
            return o;

        }catch(ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | NullPointerException x){
            //if an exception arose from the user created expression then throw a custom exception
            if(x instanceof NullPointerException){
                throw new UserCreatedExprException("Error in user created expression");
            } else {
                throw new RuntimeException("Run failed: " + x, x);
            }
        }
    }
    
}

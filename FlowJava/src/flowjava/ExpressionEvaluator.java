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
    private String className = "javaEvaluator";
    private String classCode = "";
    private String methodName = "runEvaluation";
    
    public ExpressionEvaluator(ArrayList<Var> variables, String expr, boolean isOutput){
        classCode += "public class javaEvaluator{public Object runEvaluation(){";
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
        if(isOutput){
            classCode += "\nObject o = 0;";
            classCode += "\nSystem.out.println(" + expr + ");";
            classCode += "\nreturn o;";
        } else {
            classCode += "\nObject o = " + expr + ";";
            classCode += "\nreturn o;";
        }
        classCode += "}}";
    }
    
    public Object eval() throws UserCreatedExprException{
        Map<String, ByteArrayOutputStream> classCache = new HashMap<>();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        
        if (compiler == null){
            throw new RuntimeException("Could not get a compiler. "
                    + "Please use a JDK when using this application: https://www.oracle.com/uk/java/technologies/javase/javase8-archive-downloads.html");
        }
        
        StandardJavaFileManager sfm  = compiler.getStandardFileManager(null, null, null);
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
 
        //compile
        compiler.getTask(null, fjfm, null, null, null, files).call();
        try{
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
 
            //invoke
            Object o = clarse.getMethod(methodName).invoke(clarse.newInstance());
            return o;
 
        }catch(ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | NullPointerException x){
            if(x instanceof NullPointerException){
                throw new UserCreatedExprException("Error in user created expression");
            } else {
                throw new RuntimeException("Run failed: " + x, x);
            }
        }
    }
}

/*
 * Copyright 2009 Michael Bedward
 * 
 * This file is part of jai-tools.

 * jai-tools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.

 * jai-tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public 
 * License along with jai-tools.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package jaitools.jiffle.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * A symbol table for jiffle functions, pre-loaded with a basic set of 
 * mathematical functions.
 *  
 * @author Michael Bedward
 */
public class FunctionTable {
    private Random rr = new Random();
    private HashMap<String, OpBase> lookup = null;

    private void initLookup() {
        lookup = new HashMap<String, OpBase>();
        lookup.put("log", 
            new OpBase1(){public double call(double x){return Math.log(x);}});

        lookup.put("sqrt",
            new OpBase1(){public double call(double x){return Math.sqrt(x);}});

        lookup.put("abs",
            new OpBase1(){public double call(double x){return Math.abs(x);}});

        lookup.put("rand",
            new OpBase1(){public double call(double x){return rr.nextDouble()*x;}});

        lookup.put("randInt",
            new OpBase1(){public double call(double x){return rr.nextInt((int)x);}});

        lookup.put("sin",
            new OpBase1(){public double call(double x){return Math.sin(x);}});

        lookup.put("cos",
            new OpBase1(){public double call(double x){return Math.cos(x);}});

        lookup.put("tan",
            new OpBase1(){public double call(double x){return Math.tan(x);}});

        lookup.put("asin",
            new OpBase1(){public double call(double x){return Math.asin(x);}});

        lookup.put("acos",
            new OpBase1(){public double call(double x){return Math.acos(x);}});

        lookup.put("atan",
            new OpBase1(){public double call(double x){return Math.atan(x);}});

        lookup.put("degToRad",
            new OpBase1(){public double call(double x){return Math.PI * x / 180d;}});

        lookup.put("radToDeg",
            new OpBase1(){public double call(double x){return x / Math.PI * 180d;}});

    }

    /**
     * Get a function by name.
     * @param name the function name (case-sensitive)
     * @return an OpBase ref for the function or null if the function
     * has not been defined
     */
    private OpBase getOp(String name) {
        if (lookup == null) {
            initLookup();
        }
        return lookup.get(name);
    }
    
    boolean isDefined(String name, int numArgs) {
        OpBase op = getOp(name);
        if (op == null) {
            return false;
        }

        return op.getNumArgs() == numArgs;
    }
    
    double invoke(String name, List<Double> args) {
        switch (args.size()) {
            case 0:
                return invoke(name);
                
            case 1:
                return invoke(name, args.get(0));
                
            case 2:
                return invoke(name, args.get(0), args.get(1));
                
            default:
                throw new IllegalStateException(
                        "unsupported function: " + name + " with " + args.size() + " args");
        }
    }

    /**
     * Invoke a no argument function.
     * @param name the function name
     * @return result as a double
     */
    private double invoke(String name) {
        return ((OpBase0)getOp(name)).call();
    }

    /**
     * Invoke a single argument function.
     * @param name the function name
     * @param x argument value
     * @return result as a double
     */
    private double invoke(String name, double x) {
        return ((OpBase1)getOp(name)).call(x);
    }

    /**
     * Invoke a two argument function.
     * @param name the function name
     * @param x1 first argument value
     * @param x2 second argument value
     * @return result as a double
     */
    private double invoke(String name, double x1, double x2) {
        return ((OpBase2)getOp(name)).call(x1, x2);
    }

}

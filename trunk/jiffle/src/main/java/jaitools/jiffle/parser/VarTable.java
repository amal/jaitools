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
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with jai-tools.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package jaitools.jiffle.parser;

import java.util.HashMap;

/**
 * A symbol table for user-defined variables and pre-defined, named constants
 * (PI, E, NaN).
 * 
 * @author Michael Bedward
 */
class VarTable {

    private static final int ASSIGN = 1;
    private static final int PLUS_EQ = 2;
    private static final int MINUS_EQ = 3;
    private static final int TIMES_EQ = 4;
    private static final int DIVIDE_EQ = 5;
    private static final int MOD_EQ = 6;
    
    private HashMap<String, Number> lookup = null;
    private HashMap<String, Integer> ops;

    /**
     * Initialize the lookup map
     */
    private void initLookup() {
        if (lookup == null) {
            lookup = new HashMap<String, Number>();
            setConstants();

            initOps();
        }
    }

    private void initOps() {
        ops = new HashMap<String, Integer>();

        ops.put("=", ASSIGN);
        ops.put("+=", PLUS_EQ);
        ops.put("-=", MINUS_EQ);
        ops.put("*=", TIMES_EQ);
        ops.put("/=", DIVIDE_EQ);
        ops.put("%=", MOD_EQ);
    }

    private void doAssignOp(int op, String id, double x) {
        if (op == ASSIGN) {
            put(id, x);
            return;
        }
        
        Number stored = lookup.get(id);
        if (stored != null) {
            switch (op) {
                case TIMES_EQ:
                    lookup.put(id, stored.doubleValue() * x);
                    break;
                    
                case DIVIDE_EQ:
                    lookup.put(id, stored.doubleValue() / x);
                    break;

                case PLUS_EQ:
                    lookup.put(id, stored.doubleValue() + x);
                    break;
                    
                case MINUS_EQ:
                    lookup.put(id, stored.doubleValue() - x);
                    break;
                    
                case MOD_EQ:
                    lookup.put(id, stored.doubleValue() % x);
                    break;
            }
            
        } else {
            throw new IllegalStateException("using undefined var " + id + " with " + op);
        }

    }

    /**
     * Load pre-defined constants
     */
    private void setConstants() {
        lookup.put("PI", Math.PI);
        lookup.put("E", Math.E);
        lookup.put("NaN", Double.NaN);
    }

    public void assign(String id, String op, double x) {
        doAssignOp(ops.get(op), id, x);
    }

    /**
     * Store / update variable
     * @param id variable name
     * @param x double value
     */
    private void put(String id, double x) {
        initLookup();
        lookup.put(id, x);
    }

    /**
     * Get the value for a variable
     * @param id variable name
     * @return value as double
     * @throws IllegalArgumentException if the variable is not defined
     */
    public double get(String id) {
        initLookup();
        Number n = lookup.get(id);
        if (n == null) {
            throw new IllegalArgumentException();
        }

        return n.doubleValue();
    }

    /**
     * Remove a variable. Does nothing if the variable is not defined.
     * @param id variable name
     */
    public void remove(String id) {
        /* 
        We shouldn't really be here if we need to do this...
        Be generous or throw an exception ?
         */
        initLookup();

        lookup.remove(id);
    }
}

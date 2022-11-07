package xyz.synse.datacenter.utils.command.arguments;

import org.jetbrains.annotations.NotNull;

public class Arguments {
    private final String rawArg;

    public Arguments(@NotNull String rawArg) {
        this.rawArg = rawArg;
    }

    public boolean isInt(){
        try{
            Integer.parseInt(rawArg);
            return true;
        }catch (NumberFormatException ex){
            return false;
        }
    }

    public boolean isLong(){
        try{
            Long.parseLong(rawArg);
            return true;
        }catch (NumberFormatException ex){
            return false;
        }
    }

    public boolean isFloat(){
        try{
            Float.parseFloat(rawArg);
            return true;
        }catch (NumberFormatException ex){
            return false;
        }
    }

    public boolean isDouble(){
        try{
            Double.parseDouble(rawArg);
            return true;
        }catch (NumberFormatException ex){
            return false;
        }
    }

    public int getInt(){
        return Integer.parseInt(rawArg);
    }

    public long getLong(){
        return Long.parseLong(rawArg);
    }

    public float getFloat(){
        return Float.parseFloat(rawArg);
    }

    public double getDouble(){
        return Double.parseDouble(rawArg);
    }

    public String getString() {
        return rawArg;
    }
}

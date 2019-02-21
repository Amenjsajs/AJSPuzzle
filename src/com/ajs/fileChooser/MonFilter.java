package com.ajs.fileChooser;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class MonFilter extends FileFilter {
    String [] lesSuffixes;
    String  laDescription;
    public MonFilter(String []lesSuffixes,
                     String laDescription){
        this.lesSuffixes = lesSuffixes;
        this.laDescription = laDescription;
    }
    boolean appartient( String suffixe ){
        for( int i = 0; i<lesSuffixes.length; ++i)
            if(suffixe.equals(lesSuffixes[i]))
                return true;
        return false;
    }
    public boolean accept(File f) {
        if (f.isDirectory())  return true;
        String suffixe = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if(i > 0 &&  i < s.length() - 1)
            suffixe=s.substring(i+1).toLowerCase();
        return suffixe!=null&&appartient(suffixe);
    }
    // la description du filtre
    public String getDescription() {
        return laDescription;
    }
}
